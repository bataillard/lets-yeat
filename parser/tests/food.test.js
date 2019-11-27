const f = require("../food.js");
const Recipe = require("../recipe.js").Recipe;
const Ingredient = require("../recipe.js").Ingredient;

const cheerio = require("cheerio");

const fs = require("fs");
const path = require("path");
const testHtml = fs.readFileSync(path.resolve(__dirname, "./food.html"), "utf8");
jest.dontMock("fs");
jest.setTimeout(30000);

var testUrl = "https://www.food.com/recipe/creamy-cajun-chicken-pasta-39087";
var testImg = "https://images.video.snidigital.com/image/upload/w_1024,h_576,c_fit/prod/genius/sni1uss-aakamaihdnetScripps_-_Genius_Kitchen130257170925_4179517_Creamy_Cajun_Chicken_Pasta_1520353859jpg.jpg";
var testName = "Creamy Cajun Chicken Pasta"

//not working properly
test("parse correct tags", () => {
	expect(f.parseTags(cheerio.load(testHtml), "chicken breast")).toStrictEqual(["chicken"]);
});
test("parse correct image source", () => {
	expect(f.parseRecipeImage(cheerio.load(testHtml))).toBe(testImg);
});
test("parse correct ingredients", () => {
	expect(f.parseIngredients(cheerio.load(testHtml))[0].name).toBe("2 boneless skinless chicken breast halves, cut into thin strips");
});

test("parse correct cooking time", () => {
	expect(f.parseCookingTime(cheerio.load(testHtml))).toBe(25);
});
test("parse correct cooking instructions", () => {
	expect(f.parseCookingInstructions(cheerio.load(testHtml))[0]).toBe("Place chicken and Cajun seasoning in a bowl and toss to coat.");
	expect(f.parseCookingInstructions(cheerio.load(testHtml))[3]).toBe("Pour over hot linguine and toss with Parmesan cheese.");
});


test("parse recipe from URL", async () => {
	var recipe_promise = f.parseRecipeFromUrl(testUrl);
	var recipe = await recipe_promise.then();
	expect(recipe.url).toBe(testUrl);
	expect(recipe.name).toBe(testName);
	expect(recipe.tags).toStrictEqual(["creamy", "chicken", "pasta"]);
});

test("get Urls", async () => {
	var recipe_Url_promise = f.getRecipeUrls(testUrl);
	var recipe_Url = await recipe_Url_promise.then();
	expect.anything(recipe_Url[0]);
});

test("get recipes", async () => {
	var recipe_Url_promise = f.getRecipes(10);
	var recipe_Url = await recipe_Url_promise.then();
	expect.anything(recipe_Url[0]);
});