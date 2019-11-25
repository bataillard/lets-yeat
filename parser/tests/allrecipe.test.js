const ar = require("../allrecipe.js");
const Recipe = require("../recipe.js").Recipe;
const Ingredient = require("../recipe.js").Ingredient;

const cheerio = require("cheerio");

const fs = require("fs");
const path = require("path");
const testHtml = fs.readFileSync(path.resolve(__dirname, "./allrecipes1.html"), "utf8");
jest.dontMock("fs");


const testImgUrl = "https://imagesvc.meredithcorp.io/v3/mm/image?url=https%3A%2F%2Fimages.media-allrecipes.com%2Fuserphotos%2F377786.jpg&w=634&h=634&c=sc&poi=face&q=85";


test.skip("parse correct tags", () => {
	expect(ar.parseTags(cheerio.load(testHtml))).toBe("");
});

test.skip("parse correct image source", () => {
	expect(ar.parseRecipeImage(cheerio.load(testHtml))).toBe(testImgUrl);
});

test("parse correct cooking time", () => {
	
});

test("parse correct ingredients", () => {
	
});

test("parse correct instructions", () => {
	
});

test("empty", () => {
	
});
test("empty", () => {
	
});

test("empty", () => {
	
});