const fn = require("../foodnetwork.js");
const Recipe = require("../recipe.js").Recipe;
const Ingredient = require("../recipe.js").Ingredient;

const cheerio = require("cheerio");

const fs = require("fs");
const path = require("path");
const testHtml = fs.readFileSync(path.resolve(__dirname, "./foodnetwork.html"), "utf8");
jest.dontMock("fs");


var testUrl = "http://www.foodnetwork.ca/recipe/chicken-quesadillas-with-pico-de-gallo/23062/";
var testImg = "media.foodnetwork.ca/recipetracker/403fe151-6790-4e3c-90fc-36783dc27c4f_chicken-quesadillas_webready.jpg";


test("parse correct tags", () => {
	expect(fn.parseTags(cheerio.load(testHtml))).toStrictEqual(["cheese", "chicken"]);
});
test("parse correct image source", () => {
	expect(fn.parseRecipeImage(cheerio.load(testHtml))).toBe(testImg);
});
test("parse correct ingredients", () => {
	expect(fn.parseIngredients(cheerio.load(testHtml))[0].name).toBe("2 Tbsp olive oil");
});

test("parse correct cooking time", () => {
	expect(fn.parseCookingTime(cheerio.load(testHtml))).toBe("40");
});
test("parse correct cooking instructions", () => {
	expect(fn.parseCookingInstructions(cheerio.load(testHtml))[0]).toBe("1. Heat 1 tablespoon of the olive oil in a skillet over high heat. Sprinkle the chicken with salt, pepper and taco seasoning. Add the chicken to the skillet and saute over medium-high heat until done, about 4 minute per side. Remove from the skillet and dice into cubes. Set aside.");
	expect(fn.parseCookingInstructions(cheerio.load(testHtml))[7]).toBe("3. Slice the lime in half and squeeze the juice from half the lime into the bowl. Sprinkle with salt, and stir together until combined. Be sure to taste the pico de gallo and adjust the seasonings, adding salt or more diced jalapeno if needed.");
});

test("parse recipe from URL", () => {
	
});