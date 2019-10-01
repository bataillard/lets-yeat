'use strict';

const rp = require('request-promise');
const $ = require('cheerio');
const url = "https://www.budgetbytes.com/ground-turkey-stir-fry/";

main(url);

/* 
 * Represents basic recipe following the scheme defined as:
 *
 * : Recipe {
 *   name: String,
 *   pictureUrl: String            // URL pointing to image
 *   time: Int,                 // Number of minutes
 *   difficulty: Float,         // In range 0-5
 *   ingredients: [Ingredient]  // List of ingredient objects 
 *   instructions: [String]     // Ordered list of steps
 *   tags: [String]             // List of tags for each recipe
 * }
 * 
 * inspired by https://schema.org/Recipe 
 * This will then be transmitted to the database to be stored
 */
class Recipe {
    constructor(name, pictureUrl, time, difficulty, ingredients, instructions, tags) {
        this.name = name;
        this.pictureUrl = pictureUrl;
        this.time = time;
        this.difficulty = difficulty;
        this.ingredients = [...ingredients];
        this.instruction = [...instructions];
        this.tags = [...tags];
    }
}

function main(url) {
    rp(url)
    .then((html) => {
        const pictureUrl = parseImgSrc(html);
        const recipe = $(".wprm-recipe-container > .wprm-recipe", html);

        const name = $(".wprm-recipe-name", recipe).text();
        const time = parseTime(recipe);
        
        console.log(new Recipe(name, pictureUrl, time, null, [], [], []));
    })
    .catch((err) => {
        console.log(err);
    });
}

function parseImgSrc(html) {
    const noscriptTag = $(".post > p > noscript", html).get(0).children[0].data;
    const parser = $.load(noscriptTag, {xmlMode: true});

    return parser("img").attr("src");

}

function parseTime(recipe) {
    const hoursStr = $(".wprm-recipe-total_time-hours", recipe).text();
    const minutesStr = $(".wprm-recipe-total_time-minutes", recipe).text();
    
    const convert = (value) => value === "" ? 0 : parseInt(value)

    return 60*convert(hoursStr) + convert(minutesStr);
}



