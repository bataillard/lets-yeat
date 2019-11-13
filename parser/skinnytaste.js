/**
 * This file parses skinnytaste.com into Recipe objects
 * - one of 5 parsers that make up the non-trivial component of
 *   CPEN 321 Let's Yeat, software engineering project.
 */

const path = require('path')
const rp = require('request-promise');
const cheerio = require('cheerio');
const Recipe = require('./recipe.js').Recipe;
const Ingredient = require('./recipe.js').Ingredient

const possible_tags = new Set(JSON.parse(require('fs').readFileSync(path.join(__dirname,"./tags.json"))).tags);
const minutes_in_hour = 60;
const recipes_per_page = 24;
const recipe_count_buffer = 10; // some recipes are cut from selection, this allows for margin of error
module.export = {getRecipes};

// ================================ Site Navigation  ================================= //
const SKINNY_BASE = "https://www.skinnytaste.com/";
const CATEGORY = "/recipes";
const PAGE = "/page/";

function getRecipes(request_num_recipes){
    return getAllRecipes(request_num_recipes+recipe_count_buffer).then((requested_recipes)=>{
        requested_recipes = requested_recipes.filter(Boolean)
        const allRecipes = [].concat(...requested_recipes).slice(0, request_num_recipes);
        return allRecipes;
    })
}
/*
 * input: number of reqested recipes + buffer
 * return: array containing recipe objects, may include null
 */
function getAllRecipes(number_of_recipes){
    let recipe_promises = [];
    // initialize recipes owed
    var recipe_owed = Number(number_of_recipes);
    var page_count = 1;
    while (recipe_owed > 0){
        var page_url = SKINNY_BASE + CATEGORY + PAGE + page_count.toString() + '/';
        page_count++;
        var recipe_urls = getRecipeUrls(page_url);
        recipe_promises.push(recipe_urls);
        console.log(recipe_owed)
        recipe_owed -= recipes_per_page;
    }
    // We now have Array[Promise[Array[URL]]], which we transform into Promise[Array[Array[URL]]] then flatten array
    // Then parse each URL => Promise[Array[Promise[Recipe]]], which we flatten again and return Promise[Array[Recipe]]

    // only return when all promises of Recipes are resolved
    return Promise.all(recipe_promises).then(urls => {
        // Keep only requested number recipes
        const flatURLs = [].concat(...urls).slice(0, number_of_recipes);
        const promises = flatURLs.map(parseRecipeFromUrl);
        return Promise.all(promises);
    });
}


/**
 * 
 * Given a url,
 * get all recipe urls from the page showcasing all recipes.
 * 
 */
function getRecipeUrls(recipes_url){
    return rp(recipes_url).then(html => {
        var $ = cheerio.load(html);
        var recipe_url_list= [];

        $('div.archive-post a[href]').each((index, elem) => {
            var potential_recipe_url = $(elem).attr('href');

            var recipe_meal_plan = new RegExp('https://www.skinnytaste.com/skinnytaste');
            var recipe_category= new RegExp('https://www.skinnytaste.com/recipes/');
            
            // a usual recipe url looks like:
            // https://www.skinnytaste.com/red-white-and-blueberry-cheesecake/
            // we only push recipe on if it does not end with skinnytaste.com/recipes_____ or
            // skinnytaste.com/______meal-plan
            if (!potential_recipe_url.match(recipe_meal_plan) &&
            !potential_recipe_url.match(recipe_category)){
                recipe_url_list.push(potential_recipe_url);
            }
         });
        return Promise.resolve(recipe_url_list);
    }).catch(() => Promise.resolve([])); // In case of error while parsing list, return empty list
}

// ================================ Single Recipe Parsing ================================= //

/**
 * Parsing promise of a single recipe from url
 * - st stands for skinny taste
 * input: url of single Allrecipe recipe
 * return: a function that returns promise of one Recipe object
 */

function parseRecipeFromUrl(st_url){
    return rp(st_url).then(html =>{
        // $ is function with our loaded HTML, ready for us to use
        // param is just selectors.
        var $ = cheerio.load(html);
        const time_in_minutes = parseCookingTime($);
        const picture_url = parseRecipeImage($);
        //const tags = parseTags($);
        const ingredients = parseIngredients($);
        const instructions = parseCookingInstructions($);
        const difficulty = 3;
        const recipe_title = $(".post-title h1").text()

        return null;
        // if (time_in_minutes != null && picture_url != null)
        //     return new Recipe(ar_url, recipe_title, picture_url, 
        //         time_in_minutes, difficulty, ingredients, 
        //         instructions, tags);
    })
    .catch(function(error){
        console.log("Encountered error.",error)
    })
}

/**
 * input: function with loaded HTML of recipe
 * return: instructions in array
 */
function parseCookingInstructions($){
    var instructions = [];
    
    $("li.wprm-recipe-instruction").each(function(_,element){
        var step = $(this).text()
        if (step != null && step.length !=0){
            instructions.push(step.trim());
        }
    })
    console.log(instructions)
    return instructions;
}

/**
 * input: function with loaded HTML of recipe
 * return: total cooking time in minutes
 * 
 */
function parseCookingTime($){
    // total prep time in food network is always provided in minutes
    const time = $(".wprm-recipe-total-time-container .wprm-recipe-time").text()
    var num = time.match(/\d+/g);
    var unit = time.match(/(hr|mins)/g) // either h(r) or m(ins)
    // in corner case that time is 1 hr 35 min
    // parse individual numbers and return results in minutes
    if(num!= null && num.length > 1){
        var total_time_min = Number(num[0]) * minutes_in_hour + Number(num[1]);
        return total_time_min;
    }else{
        var total_time_min = unit == "mins"? Number(num) : Number(num) * minutes_in_hour;
        console.log(total_time_min)
        return total_time_min;
    }
}

/**
 * input: function with loaded HTML of recipe
 * return: array of Ingredient objects
 */
function parseIngredients($){
    var ingredients = [];
    var list = $("li.wprm-recipe-ingredient").each(function(_,element){
        var item = $(this).text();
        if (item != null){
            ingredients.push(item.trim());
        }
    })
    return ingredients;
}

/**
 * input: function with loaded HTML of recipe
 * return: String, url of recipe image or null for any errors
 */
function parseRecipeImage($){
    try{
        const image_src = $(".wprm-recipe-image .attachment-post-thumbnail")
        return image_src[0].attribs["data-src"];
    } catch (err){
        // if error, link will be null as flag to recipient to discard
        return null;
    }
}
/**
 * input: function with loaded HTML of recipe
 * return: array of tags
 */
function parseTags($){
    potential_tags = [];
    var match_words = new RegExp(/\b($word)\b/i);
    $(".wprm-recipe-keyword").each(function(i, elem){
        var matches = $(this).html().matchAll(match_words);
        console.log(matches)
        for (word of matches)
            potential_tags.push(word.toLowerCase().trim());
    })
    // Intersection of words and potential tags
    const tags = [...new Set(potential_tags)].filter(w => possible_tags.has(w));
    return tags;
}
// var x = 50;
// getRecipes(x).then(x => {
//     for (rec in x){
//         console.log(`${rec} ${x[rec]}`)
//     }
//     console.log("done");
// })
console.log("--------------------------------------------------*******************************************")
var url = "https://www.skinnytaste.com/skinny-scalloped-potato-gratin/"
parseRecipeFromUrl(url);
