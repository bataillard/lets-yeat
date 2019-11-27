/**
 * This file parses food.com into Recipe objects
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
const recipes_per_page = 8;
const recipe_count_buffer = 10;

// ================================ Site Navigation  ================================= //
const FOOD_BASE = "https://www.food.com";
const CATEGORY = "/recipes";
const PAGE = "/?ref=nav&pn=";
const PAGE1 = "/?ref=nav"

/**
 * 
 * This function = only access parser server has
 * input: max number of recipes
 * output: promise of an array with number of requested recipe objects.
 */
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
        var page_url;
        // when on page one, no extra params to url
        if(page_count == 1){
            page_url = FOOD_BASE + CATEGORY + PAGE1;
        }
        else{
            page_url = FOOD_BASE + CATEGORY + PAGE + page_count.toString();
        }
        page_count++;
        var recipe_urls = getRecipeUrls(page_url);
        recipe_promises.push(recipe_urls);
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
    // recipe urls are lazy loaded onto website as well
    // - this adds complexity to parsing
    var js_code;
    return rp(recipes_url).then(html => {
        var $ = cheerio.load(html);
        var recipe_url_list= [];

        // contains the JSON object we need for all links
        // to individual recipes, data is always stored in var initialData = {...JSON}
        //https://stackoverflow.com/questions/53393718/how-to-find-specific-javascript-code-in-pared-html-using-cheerio-with-nodejs
        js_code = $('script:contains("initialData")') 
        var match_data = js_code.html().match(/var initialData = (.*);/); // returns an array
        match_data = match_data[0].replace(/var initialData = /,"");
        match_data = match_data.substring(0, match_data.length -1); // last char is ";" get rid of it
        var recipe_list = JSON.parse(match_data).response.results;
        for (recipe of recipe_list){
            recipe_url_list.push(recipe.record_url)
        }
        return Promise.resolve(recipe_url_list);
    }).catch((err) => {
        //console.log(err)
        Promise.resolve([])
    }); // In case of error while parsing list, return empty list
}

// ================================ Single Recipe Parsing ================================= //

/**
 * Parsing promise of a single recipe from url
 * - f stands for food
 * input: url of single Allrecipe recipe
 * return: a function that returns promise of one Recipe object
 */

function parseRecipeFromUrl(f_url){
    // to artificailly slow down parsing process so website doesn't reject us.
    setTimeout(function(){
        // do nothing
    },500);
    return rp(f_url).then(html =>{
        const recipe_title = $(".recipe-title h1").text()
        // $ is function with our loaded HTML, ready for us to use
        // param is just selectors.
        var $ = cheerio.load(html);
        const time_in_minutes = parseCookingTime($);
        const picture_url = parseRecipeImage($);
        const tags = parseTags($, recipe_title);
        const ingredients = parseIngredients($);
        const instructions = parseCookingInstructions($);
        const difficulty = 3;
        
        if (time_in_minutes != null)// && picture_url != null)
            return new Recipe(f_url, recipe_title, picture_url, 
                time_in_minutes, difficulty, ingredients, 
                instructions, tags);
        else{
            return null;
        }
    })
    .catch(function(error){
        //console.log("Encountered error.",error)
    })
}

/**
 * input: function with loaded HTML of recipe
 * return: instructions in array
 */
function parseCookingInstructions($){
    var instructions = [];
    
    $("li.recipe-directions__step").each(function(_,element){
        var step = $(this).text()
        // last item also has same class but not part of instruction (hidden)
        // we don't want that.
        if (step != null && step.length !=0){
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
    const time = $(".recipe-facts__details.recipe-facts__time :not(.recipe-facts__title)").text()
    var num = time.match(/\d+/g);
    var unit = time.match(/(mins|hrs)/g) // either m(ins) or h(rs)
    // in corner case that time is 1 hr 35 min
    // parse individual numbers and return results in minutes
    if(num!= null && num.length > 1){
        var total_time_min = Number(num[0]) * minutes_in_hour + Number(num[1]);
        return total_time_min;
    }else{
        var total_time_min = unit == "mins"? Number(num) : Number(num) * minutes_in_hour;
        return total_time_min;
    }
}

/**
 * input: function with loaded HTML of recipe
 * return: array of Ingredient objects (name, amount, unit)
 */
function parseIngredients($){
    var ingredients = [];
    var list = $(".recipe-ingredients__ingredient").each(function(_,element){
        var item = $(this).text()
        // first replace duplicate spaces
        // then get rid of spaces for ranges
        // e.g. "1 -2" should be "1-2" meaning 1 to 2
        item = item.replace(/\s+/g, ' ').replace(/\s-/,'-');
        if (item != null){
            ingredients.push(new Ingredient(item.trim()));
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
        // image is lazy loaded, more complicated, have to get JS code.
        var js_code;
        js_code = $('script:contains("@context":"http://schema.org")').html()
        var match_data = JSON.parse(js_code);
        var image_src;
        if(match_data.video)
            image_src = match_data.video[0].thumbnailUrl;
        else{ // second way of parsing images when the schema changes
            image_src = match_data.image;
        }
        return image_src;
    } catch (err){
        // if error, link will be null as flag to recipient to discard
        //console.log(err)
        return null;
    }
}
/**
 * input: function with loaded HTML of recipe
 * return: array of tags
 */
function parseTags($, name){
    console.log("in")
    potential_tags = [];
    // tags in all recipe is under "toggle-similar__title" class
    $(".recipe-breadcrumbs__text.category").each(function(i, elem){
        potential_tags.push($(this).html().toLowerCase().trim());
    })
    var match_words = new RegExp(/(\w)+/ig);
    potential_tags.concat(name.match(match_words));
    // Intersection of words and potential tags
    const tags = [...new Set(potential_tags)].filter(w => possible_tags.has(w));
    return tags;
}

module.exports.parseRecipeImage = parseRecipeImage;
module.exports.parseTags = parseTags;
module.exports.parseCookingTime = parseCookingTime;
module.exports.parseIngredients = parseIngredients;
module.exports.parseCookingInstructions = parseCookingInstructions;
module.exports.parseRecipeFromUrl = parseRecipeFromUrl;
module.exports.getRecipeUrls = getRecipeUrls;
module.exports.getRecipes = getRecipes;

// var x = 10;
// getRecipes(x).then(x => {
//     console.log(x)
//     console.log("done");
// })
// var url1 = "https://www.food.com/recipe/beths-melt-in-your-mouth-barbecue-ribs-oven-107786"
// var url2 = "https://www.food.com/recipe/kittencals-italian-melt-in-your-mouth-meatballs-69173"
// parseRecipeFromUrl(url1).then(r => {console.log("done")})

// var url3 = "https://www.food.com/recipe?ref=nav"
// getRecipeUrls(url3)


getAllRecipes(20).then(x=>{
    console.log(x)
})
