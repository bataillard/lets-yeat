/**
 * This file parses foodnetwork.com into Recipe objects
 * - one of 5 parsers that make up the non-trivial component of
 *   CPEN 321 Let's Yeat, software engineering project.
 */

const path = require('path')
const rp = require('request-promise');
const cheerio = require('cheerio');
const Recipe = require('./recipe.js').Recipe;
const Ingredient = require('./recipe.js').Ingredient

module.export = {getRecipes};
getRecipes(1)
 // ================================ Navigating Site ================================= //
const FOODNETWORK_BASE = "https://www.foodnetwork.ca";
const CATEGORY = "/everyday-cooking/recipes";
const PAGE = "/?fspn=";
/*""
 * input: number of reqested recipes
 * return: array containing 
 */
function getRecipes(number_of_recipes){
    let recipe_promises = [];

    // initialize recipes owed
    var recipe_count = number_of_recipes;
    var page_count = 1; 
    while (recipe_count > 0){
        var page_url;
        // url on page one doesn't have /?fspn=X where X is page number
        // on page 1 it is just https://www.foodnetwork.ca/everyday-cooking/recipes
        if (page_count == 1){
            page_url = FOODNETWORK_BASE + CATEGORY;
        }else{
            page_url = FOODNETWORK_BASE + CATEGORY + PAGE + page.toString();
        }
        page_count++;
        var recipe_urls = getRecipeUrls(page_url);
        recipe_count -= recipe_urls.length;
    }

    // only return when all promises of Recipes are resolved
    return Promise.all(recipe_promises).then(url => {
        // Keep only requested number recipes
        const flatURLs = [].concat(...urls).slice(0, number_of_recipes);
        const promises = flatURLs.map(parseRecipeFromUrl);

        return Promise.all(promises);
    });
}

function getRecipeUrls(recipes_url){
    return rp(recipes_url).then(html => {
        var $ = cheerio.load(html);
        var recipe_list = [];

        // contains the JSON object we need for all links
        // to individual recipes
        const js_code = $(".wrapper script")[0].text();
        const match_data = js_code.match(/var viewModel = (.*);/);
        console.log(match_data[1]);
        JSON.parse(match_data[1]).Records.each(recipe_item =>{
            recipe_list.push(FOODNETWORK_BASE+recipe_item.LinkURL)
        })
        // wait until all promises are resolved
        return Promise.resolve(recipe_list);
    }).catch(() => Promise.resolve([])); // In case of error while parsing list, return empty list
}


// ================================ Single Recipe Parsing ================================= //

// temp test website
const pasta = "https://www.foodnetwork.ca/everyday-cooking/recipe/italian-sausage-pasta-skillet/22840/"

// rp(pasta).then(html => {
//     var $ = cheerio.load(html)
//     console.log($(".recipeTitle").text())
// })
/**
 * Parsing promise of a single recipe from url
 * - fn stands for food network
 * input: url of single food network recipe
 * return: a function that returns promise of one Recipe object
 */

//  parseRecipeFromUrl(pasta).then(name => {
//     console.log("Recipe name is ", name)
// })

function parseRecipeFromUrl(fn_url){
    return rp(fn_url).then(html =>{
        // $ is function with our loaded HTML, ready for us to use
        // param is just selectors.
        var $ = cheerio.load(html)
        const picture_url = parseRecipeImage($);
        const tags = parseTags($);
        const time = parseCookingTime($);
        const ingredients = parseIngredients($);
        const instructions = parseCookingInstructions($);
        const difficulty = 3;

        // html class name of recipe title is "recipeTitle"
        const recipe_title = $(".recipeTitle").text()
        
        return new Recipe(fn_url, recipe_title, picture_url, time, 
           difficulty, ingredients, instructions, tags);
    })
}

/**
 * input: function with loaded HTML of recipe
 * return: instructions in array
 */
function parseCookingInstructions($){
    var instructions = [];

    $(".recipeInstructions p").each(text =>{
        // text is steps, led by 1. 2. 3. numbers. Assuming no more than 99 steps.
        // always trim off first 3 char and check 4th char if is space, trim it as well.
        var step = text.substring(3)
        if (step.charAt(0) = " "){
            step = step.substring(1)
        }
        instructions.push(step);
    })   
    return instructions;
}

/**
 * input: function with loaded HTML of recipe
 * return: cooking time in minutes
 */
function parseCookingTime($){
    // total prep time in food network is always provided in minutes
    const minutes = $(".recipeDetails-infoValueWrapper span").text()

    return minutes;
}

/**
 * input: function with loaded HTML of recipe
 * return: array of Ingredient objects (name, amount, unit)
 */
function parseIngredients($){
    var ingredients = [];
    
    // regular expression to parse fractions in ingredient units
    // usually in the form &frac13 which means 1/3 
    //var regex = //
    $(".recipe-ingredients p").each(text =>{

        // place holder
        // TODO: fix fraction issue
        ingredients.push(text);
    })

    return ingredients;
}

/**
 * input: function with loaded HTML of recipe
 * return: String, url of recipe image or null for any errors
 */
function parseRecipeImage($){
    try{
        const image_src = $(".recipe-photo").attr("src")
    } catch (err){
        return null;
    }
    return image_src;
}
/**
 * input: function with loaded HTML of recipe
 * return: array of tags
 */
function parseTags($){
    potential_tags = []
    // tags in Food network is under see-more class
    $(".see-more p").text().each(potential_tag => {
        potential_tags.push(potential_tag.toLowerCase());
    })
    const tags = [...new Set(words)].filter(w => possible_tags.has(w));

    return tags;
}