const st = require("../skinnytaste.js");
const Recipe = require("../recipe.js").Recipe;
const Ingredient = require("../recipe.js").Ingredient;

const cheerio = require("cheerio");

const fs = require("fs");
const path = require("path");
const testHtml = fs.readFileSync(path.resolve(__dirname, "./skinnytaste.html"), "utf8");
jest.dontMock("fs");
jest.setTimeout(30000);

var testUrl = "https://www.skinnytaste.com/chicken-zucchini-stir-fry/";
var testImg = "https://www.skinnytaste.com/wp-content/uploads/2019/08/Chicken-and-Zucchini-Stir-Fry-RS-5-170x255.jpg";
var testName = "Chicken Zucchini Stir Fry";

test("parse correct tags", () => {
	expect(st.parseTags(cheerio.load(testHtml), "")).toStrictEqual(["chicken"]);
});
test("parse correct image source", () => {
	expect(st.parseRecipeImage(cheerio.load(testHtml))).toBe(testImg);
});
test("parse correct ingredients", () => {
	expect(st.parseIngredients(cheerio.load(testHtml))[0].name).toBe("1/4 cup low sodium soy sauce or use gf soy sauce");
});

test("parse correct cooking time", () => {
	expect(st.parseCookingTime(cheerio.load(testHtml))).toBe(20);
});
test("parse correct cooking instructions", () => {
	expect(st.parseCookingInstructions(cheerio.load(testHtml))[0]).toBe("In a large bowl add the soy sauce, chicken broth, cornstarch, mirin, sugar, and sesame oil and whisk until everything is completely dissolved.");
	expect(st.parseCookingInstructions(cheerio.load(testHtml))[4]).toBe("Stir the garlic and ginger well and add in the sauce, whisking well. Cook the sauce 1 minute, then add in the zucchini and cook for 2 minutes more, until thickened and the zucchini is tender crisp. Remove from heat, add in the chicken and stir well to coat. Garnish with sesame seeds and scallions if desired.");
});

test("parse recipe from URL", async () => {
	var recipe_promise = st.parseRecipeFromUrl(testUrl);
	var recipe = await recipe_promise.then();
	expect(recipe.url).toBe(testUrl);
	expect(recipe.name).toBe(testName);
	expect(recipe.tags).toStrictEqual(["chicken"]);
});

test("get Urls", async () => {
	var recipe_Url_promise = st.getRecipeUrls(testUrl);
	var recipe_Url = await recipe_Url_promise.then();
	expect.anything(recipe_Url[0]);
});

test("get recipes", async () => {
	var recipe_promise = st.getAllRecipes(10);
	var recipe = await recipe_promise.then();
	expect.anything(recipe[0]);
});