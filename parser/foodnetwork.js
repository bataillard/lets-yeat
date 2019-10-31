/**
 * This file parses foodnetwork.com into Recipe objects
 * - one of 5 parsers that make up the non-trivial component of
 *   CPEN 321 Let's Yeat, software engineering project.
 */

const path = require('path')
const rp = require('request-promise');
const $ = require('cheerio');
const Recipe = require('./recipe.js').Recipe;
const Ingredient = require('./recipe.js').Ingredient

export {getRecipes};

 // ================================ Navigating Site ================================= //
const FOODNETWORK_BASE = "https://www.foodnetwork.ca/everyday-cooking/recipes/"

/**
 * input: number of reqested recipes
 * return: array containing 
 */
function getRecipes(number_of_recipes){


    // place holder
    return {};
}



// ================================ Single Recipe Parsing ================================= //

/**
 * Parsing a single recipe from url
 * - fn stands for food network
 * input: url of single food network recipe
 * return: one Recipe object
 */

function parseRecipeFromUrl(fn_url){

}

/**
 * input: 
 * return: instructions in array
 */
function parseCookingInstructions(){

}

/**
 * 
 */
function parseCookingTime(){

}

/**
 * 
 */
function parseIngredients(){

}

/**
 * 
 */
function parseRecipeImage(){

}