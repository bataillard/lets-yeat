const ar = require("../allrecipe.js");
const Recipe = require("../recipe.js").Recipe;
const Ingredient = require("../recipe.js").Ingredient;

const cheerio = require("cheerio");

const fs = require("fs");
const path = require("path");
const testHtml = fs.readFileSync(path.resolve(__dirname, "./allrecipe.html"), "utf8");
const testHtml2 = fs.readFileSync(path.resolve(__dirname, "./allrecipe2.html"), "utf8");
jest.dontMock("fs");
jest.setTimeout(30000);

var testUrl = "https://www.allrecipes.com/recipe/261494/instant-pot-roasted-brussels-sprouts/";
var testImg = "https://images.media-allrecipes.com/userphotos/560x315/5779399.jpg";
var testName = "Instant Pot® Roasted Brussels Sprouts";

test("parse correct tags", () => {
	expect(ar.parseTags(cheerio.load(testHtml), testName)).toStrictEqual([]);
	expect(ar.parseTags(cheerio.load(testHtml2), "pan pizza")).toStrictEqual(["pizza"]);
});
test("parse correct image source", () => {
	expect(ar.parseRecipeImage(cheerio.load(testHtml))).toBe(testImg);
});
test("parse correct ingredients", () => {
	expect(ar.parseIngredients(cheerio.load(testHtml))[0].name).toBe("2 tablespoons olive oil");
});

test("parse correct cooking time", () => {
	expect(ar.parseCookingTime(cheerio.load(testHtml))).toBe(26);
});
test("parse correct cooking instructions", () => {
	expect(ar.parseCookingInstructions(cheerio.load(testHtml))[0]).toBe("Turn on a multi-functional pressure cooker (such as Instant Pot(R)) and select Saute function. Heat olive oil and cook onion until translucent, about 2 minutes. Add Brussels sprouts and cook for 1 minute more. Sprinkle with salt and pepper; pour vegetable broth over Brussels sprouts. Close and lock the lid. Select high pressure according to manufacturer's instructions; set timer for 3 minutes. Allow 10 to 15 minutes for pressure to build.");
});

test("parse recipe from URL", async () => {
	var recipe_promise = ar.parseRecipeFromUrl(testUrl);
	var recipe = await recipe_promise.then();
	expect(recipe.url).toBe(testUrl);
	expect(recipe.name).toBe(testName);
	expect(recipe.tags).toStrictEqual([]);
});

test("get Urls", async () => {
	var recipe_Url_promise = ar.getRecipeUrls(testUrl);
	var recipe_Url = await recipe_Url_promise.then();
	expect.anything(recipe_Url[0]);
});

test("get recipes", async () => {
	var recipe_Url_promise = ar.getRecipes(10);
	var recipe_Url = await recipe_Url_promise.then();
	expect.anything(recipe_Url[0]);
});