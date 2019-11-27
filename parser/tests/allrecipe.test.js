const ar = require("../allrecipe.js");
const Recipe = require("../recipe.js").Recipe;
const Ingredient = require("../recipe.js").Ingredient;

const cheerio = require("cheerio");

const fs = require("fs");
const path = require("path");
const testHtml = fs.readFileSync(path.resolve(__dirname, "./allrecipe.html"), "utf8");
jest.dontMock("fs");
jest.setTimeout(30000);

var testUrl = "https://www.allrecipes.com/recipe/74375/unbelievably-awesome-barbeque-chicken-pizza/";
var testImg = "https://images.media-allrecipes.com/userphotos/3149364.jpg";
var testName = "Unbelievably Awesome Barbeque Chicken Pizza Recipe - Allrecipes.com"

test.skip("parse correct tags", () => {
	expect(ar.parseTags(cheerio.load(testHtml))).toStrictEqual(["cheese", "chicken"]);
});
test.skip("parse correct image source", () => {
	expect(ar.parseRecipeImage(cheerio.load(testHtml))).toBe(testImg);
});
test.skip("parse correct ingredients", () => {
	expect(ar.parseIngredients(cheerio.load(testHtml))[0].name).toBe("2 Tbsp olive oil");
});

test.skip("parse correct cooking time", () => {
	expect(ar.parseCookingTime(cheerio.load(testHtml))).toBe("40");
});
test.skip("parse correct cooking instructions", () => {
	expect(ar.parseCookingInstructions(cheerio.load(testHtml))[0]).toBe("1. Heat 1 tablespoon of the olive oil in a skillet over high heat. Sprinkle the chicken with salt, pepper and taco seasoning. Add the chicken to the skillet and saute over medium-high heat until done, about 4 minute per side. Remove from the skillet and dice into cubes. Set aside.");
	expect(ar.parseCookingInstructions(cheerio.load(testHtml))[7]).toBe("3. Slice the lime in half and squeeze the juice from half the lime into the bowl. Sprinkle with salt, and stir together until combined. Be sure to taste the pico de gallo and adjust the seasonings, adding salt or more diced jalapeno if needed.");
});

test.skip("parse recipe from URL", async () => {
	var recipe_promise = ar.parseRecipeFromUrl(testUrl);
	var recipe = await recipe_promise.then();
	expect(recipe.url).toBe(testUrl);
	expect(recipe.name).toBe(testName);
	expect(recipe.tags).toStrictEqual(["cheese", "chicken"]);
});

test.skip("get Urls", async () => {
	var recipe_Url_promise = ar.getRecipeUrls(testUrl);
	var recipe_Url = await recipe_Url_promise.then();
	expect(recipe_Url[0]).toBe("https://www.foodnetwork.ca/recipe/stewed-chicken-refried-beans-and-oaxaca-cheese-quesadillas/23063/");
});