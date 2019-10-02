'use strict';

const rp = require('request-promise');
const $ = require('cheerio');

// =================================
function main() {
    const url = process.argv[2];
    
    parseBudgetBytesUrl(url).then(recipe => console.log(recipe))
}

main();
// =================================

/**
 * Set of possible tags recognized by the system, can be expanded as needed 
 */
const possible_tags = new Set([
    "beef", "turkey", "chicken", "fish", "pork", "vegan", "vegertarian", "easy", "quick", 
    "aisan", "chinese", "japanese", "thai", "vietnamese", "pasta", "pizza"
]);

/**
 * Represents basic recipe following the scheme defined as:
 *
 * : Recipe {
 *   name: string,
 *   pictureUrl: string         // URL pointing to image
 *   time: int,                 // Number of minutes
 *   difficulty: float,         // In range 0-5
 *   ingredients: [Ingredient]  // List of ingredient objects 
 *   instructions: [string]     // Ordered list of steps
 *   tags: [string]             // List of tags for each recipe
 * }
 * 
 * inspired by https://schema.org/Recipe 
 * This will then be transmitted to the database to be stored
 */
class Recipe {
    constructor(url, name, pictureUrl, time, difficulty, ingredients, instructions, tags) {
        this.url = url;
        this.name = name;
        this.pictureUrl = pictureUrl;
        this.time = time;
        this.difficulty = difficulty;
        this.ingredients = [...ingredients];
        this.instruction = [...instructions];
        this.tags = [...tags];
    }
}

/**
 * Represents each ingredient
 * :Ingredient {
 *   name: string,
 *   quantity: number,
 *   unit: string
 * }
 */
class Ingredient {

    /**
     * Converts quantity into a floating point number
     * @param {string, float} quantity 
     * @return {float, null} float or null if uncountable quantity (salt)
     */
    static convertQuantity(quantity) {
        const fractionRegExp = /(\d+)\s*\/\s*(\d+)/;
        const numberRegExp = /\d+[.]?/;
        
        if (typeof quantity === "string") {
            if (fractionRegExp.test(quantity)) {
                const match = quantity.match(fractionRegExp);
                return parseFloat(match[1]) / parseFloat(match[2]);
            }

            if (quantity.length == 0) {
                return null;
            }

            return parseFloat(quantity.match(numberRegExp)[0]);

        } else {
            return quantity;
        }
    }

    constructor(name, quantity, unit) {
        this.name = name.replace(/[*]/g, "");
        this.quantity = this.constructor.convertQuantity(quantity);
        this.unit = unit;
    }
}

/**
 * Returns a promise to a Recipe from given url
 * @param url 
 */
function parseBudgetBytesUrl(url) {
    return rp(url).then(html => {
        const pictureUrl = parseImgSrc(html);
        const tags = parseTags(html);

        const recipe = $(".wprm-recipe-container > .wprm-recipe", html);

        const name = $(".wprm-recipe-name", recipe).text();
        const time = parseTime(recipe);
        const difficulty = 3;
        const ingredients = parseIngredients(recipe);
        const instructions = parseInstructions(recipe);
        
        return new Recipe(url, name, pictureUrl, time, difficulty, 
            ingredients, instructions, tags);
    })
}

function parseImgSrc(html) {
    const noscriptTag = $(".post > p > noscript", html).get(0).children[0].data;
    const parser = $.load(noscriptTag, {xmlMode: true});

    return parser("img").attr("src");

}

function parseTags(html) {
    function extractBreadcrumbs(html) {
        return $("#breadcrumbs", html).text();
    }

    const words = extractBreadcrumbs(html).split(" ")   // split into words
        .map(s => s.replace(/\W/g, "").toLowerCase())   // remove special chars
        .filter(s => s.length > 0)                      // remove empty words
    
    // Intersection of words and potential tags
    const tags = [...new Set(words)].filter(w => possible_tags.has(w));
    
    return tags;
}

function parseTime(recipe) {
    const hoursStr = $(".wprm-recipe-total_time-hours", recipe).text();
    const minutesStr = $(".wprm-recipe-total_time-minutes", recipe).text();
    
    const convert = (value) => value === "" ? 0 : parseInt(value)

    return 60*convert(hoursStr) + convert(minutesStr);
}

function parseIngredients(recipe) {
    const ingredients = [];

    $("li.wprm-recipe-ingredient", recipe).each((_, e) => {
        const amount = $(e).find(".wprm-recipe-ingredient-amount").text();
        const name = $(e).find(".wprm-recipe-ingredient-name").text();

        const unitSel = $(e).find(".wprm-recipe-ingredient-unit");
        const unit = unitSel.length == 0 ? null : unitSel.text();

        ingredients.push(new Ingredient(name, amount, unit));
    });

    return ingredients;
}

function parseInstructions(recipe) {
    const instructions = []

    $("li.wprm-recipe-instruction", recipe).each((i, elem) => {
        instructions.push($(elem).text());
    });

    return instructions;
}


