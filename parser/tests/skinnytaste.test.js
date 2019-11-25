const budget = require("../skinnytaste.js");
const Recipe = require("../recipe.js").Recipe;
const Ingredient = require("../recipe.js").Ingredient;

const fs = require("fs");
const path = require("path");
const testHtml1 = fs.readFileSync(path.resolve(__dirname, "./allrecipes1.html"), "utf8");
jest.dontMock("fs");


test("empty", () => {
	
});