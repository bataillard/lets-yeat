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

 // ================================ Navigating Site ================================= //
const ALLRECIPE_BASE = "https://www.allrecipes.com";
const CATEGORY = "/recipes";
const PAGE = "/?page=";

/*
 * input: number of reqested recipes
 * return: array containing 
 */
function getRecipes(number_of_recipes){
    console.log("Website is ",ALLRECIPE_BASE,CATEGORY,PAGE)
    let recipe_promises = [];

    // initialize recipes owed
    var recipe_count = number_of_recipes;
    var page_count = 1;
    // while (recipe_count > 0){
    //     var page_url;
    //     // url on page one doesn't have /?fspn=X where X is page number
    //     // on page 1 it is just https://www.foodnetwork.ca/everyday-cooking/recipes
    //     if (page_count == 1){
    //         page_url = FOODNETWORK_BASE + CATEGORY;
    //     }else{
    //         page_url = FOODNETWORK_BASE + CATEGORY + PAGE + page_count.toString();
    //     }
    //     page_count++;
    //     var recipe_urls = getRecipeUrls(page_url);
    //     recipe_promises.push(recipe_urls);
    //     recipe_count -= recipe_urls.length;
    // }
    recipe_promises.push(getRecipeUrls(ALLRECIPE_BASE+CATEGORY+PAGE+page_count.toString()));

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

function getRecipeUrls(recipes_url){
    var js_code;
    return rp(recipes_url).then(html => {
        var $ = cheerio.load(html);
        var recipe_url_list= [];

        // contains the JSON object we need for all links
        // to individual recipes
        js_code = $("#wrapper section > script").html()//[0].text();
        const match_data = js_code.match(/var viewModel = (.*);/);
        var recipe_list = JSON.parse(match_data[1]).Records;
        for (recipe of recipe_list){
            recipe_url_list.push(FOODNETWORK_BASE+recipe.LinkURL)
        }
        return Promise.resolve(recipe_url_list);
    }).catch(() => Promise.resolve([])); // In case of error while parsing list, return empty list
}

// ================================ Single Recipe Parsing ================================= //

/**
 * Parsing promise of a single recipe from url
 * - ar stands for all recipe
 * input: url of single food network recipe
 * return: a function that returns promise of one Recipe object
 */

function parseRecipeFromUrl(ar_url){
    return rp(ar_url).then(html =>{
        // $ is function with our loaded HTML, ready for us to use
        // param is just selectors.
        var $ = cheerio.load(html);
        const time_in_minutues = parseCookingTime($);
        console.log("Time is ", time_in_minutues)
        // function returns nothing if food network doesn't provide 
        // prep time. This recipe will be discarded.
        if (!time_in_minutues)
            return null;
        
        const picture_url = parseRecipeImage($);
        const tags = parseTags($);
        const ingredients = parseIngredients($);
        const instructions = parseCookingInstructions($);
        const difficulty = 3;

        // html class name of recipe title is "recipeTitle"
        const recipe_title = $(".recipeTitle").text()

        return null;
        // return new Recipe(fn_url, recipe_title, picture_url, 
        //     time_in_minutues, difficulty, ingredients, 
        //     instructions, tags);
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
    
    $(".recipeInstructions").find('p').each(function(_,element){
        var step = $(this).html()
        
        // sometimes credits are given in the same classes, so remove those.
        if (step != null && (step.search("href") == -1)){
            /**
             * TODO:
             * - fix copyright sign
             */
            // text is steps, led by 1. 2. 3. numbers. Assuming no more than 99 steps.
            // always trim off first 3 char and check 4th char if is space, trim it as well.
            step = step.substring(3).trim()
            //first char is always space, trim it
            instructions.push(step.trim());
        }
    })
    return instructions;
}

/**
 * input: function with loaded HTML of recipe
 * return: cooking time in minutes
 * 
 * NOTE: food network is inconsistent in cooking time
 * - on the rare occasion that no prep time is provided,
 *   should discard the recipe.
 */
function parseCookingTime($){
    // total prep time in food network is always provided in minutes
    const time = $(".ready-in-time").text()
    var num = Number(time.match(/\d+/)); // 
    var unit = time.match(/[a-zA-Z]+$/); // either m(inute) or h(our)
    console.log("Here");
    return unit == "m"? num : num*60;
}

/**
 * input: function with loaded HTML of recipe
 * return: array of Ingredient objects (name, amount, unit)
 */
function parseIngredients($){
    var ingredients = [];
    
    // regular expression to parse fractions in ingredient units
    // usually in the form &frac13 which means 1/3 
    // var regex = //
    var list = $(".recipe-ingredients p").each(function(_,element){
        var item = $(this).next().html()

        if (item != null){
            /**
             * TODO: 2x
             * 1 - fix fraction, in html &frac12 -> unicode &#xBD 
             * 2 - fix copyright sign
             */
            // first char is always space, trim it
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
        const image_src = $(".rec-photo")[0].attribs["src"];
        console.log(image_src)
        return image_src;
    } catch (err){
        console.log("Parse image error: ", err)
        return null;
    }
}
/**
 * input: function with loaded HTML of recipe
 * return: array of tags
 */
function parseTags($){
    potential_tags = [];
    // tags in Food network is under see-more class
    $(".see-more .category a").each(function(i, elem){
        potential_tags.push($(this).html().toLowerCase());
    })
    // Intersection of words and potential tags
    const tags = [...new Set(potential_tags)].filter(w => possible_tags.has(w));
    
    return tags;
}

var test_url = "https://www.allrecipes.com/recipe/8665/braised-balsamic-chicken/?internalSource=recipe%20hub&referringContentType=Recipe%20Hub&clickId=cardslot%2062"
parseRecipeFromUrl(test_url)