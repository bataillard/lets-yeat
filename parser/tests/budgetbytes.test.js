/**
* This file is used for testing budgetbytes.js

*
*/

const budget = require("../budgetbytes");
const Recipe = require("../recipe.js").Recipe;
const Ingredient = require("../recipe.js").Ingredient;
const $ = require("cheerio");


const fs = require("fs");
const path = require("path");
const testHtml1 = fs.readFileSync(path.resolve(__dirname, "./test.html"), "utf8");
const testHtml2 = fs.readFileSync(path.resolve(__dirname, "./test2.html"), "utf8");
jest.dontMock("fs");


const testUrlString = "https://www.budgetbytes.com/balsamic-chicken-and-mushrooms/";
const testIngArray = [new Ingredient("balsamic vinegar", "4", "Tbsp"),
	new Ingredient("soy sauce", "1.5", "Tbsp"),
	new Ingredient("brown sugar", "1", "Tbsp"),
	new Ingredient("garlic, minced", "2", "cloves"),
	new Ingredient("dried thyme", "1/4", "tsp"),
	new Ingredient("boneless, skinless chicken thighs (about 1.25 lbs. total)", "4", null),
	new Ingredient("salt and pepper", "1", "pinch"),
	new Ingredient("olive oil", "1", "Tbsp"),
	new Ingredient("mushrooms", "8", "oz."),
	new Ingredient("butter", "2", "Tbsp")];;
const testRecipe = $(".wprm-recipe-container > .wprm-recipe", testHtml1);
const testRecipe2 = $(".wprm-recipe-container > .wprm-recipe", testHtml2);


test("Tests helper function inPast", () => {
	//normal inputs
	expect(budget.inPast(2010,1,2015,1)).toBe(false);
	expect(budget.inPast(2015,1,2010,1)).toBe(true);
	expect(budget.inPast(2010,1,2010,1)).toBe(false);
	
	//zero cases
	expect(budget.inPast(0,0,2015,1)).toBe(false);
	expect(budget.inPast(2015,1,0,0)).toBe(false); // zero in the toDate arguments indicates we want current date
	expect(budget.inPast(0,0,0,0)).toBe(false);

});

test("parse correct tags", () => {
	expect(budget.parseTags(testHtml1, "")[0]).toBe("meat");
});

test("parse correct image source", () => {
	expect(budget.parseImgSrc(testHtml1)).toBe("https://www.budgetbytes.com/wp-content/uploads/2019/10/Balsamic-Chicken-and-Mushrooms-skillet-V1.jpg");
});

test("parse correct total time", () => {
	expect(budget.parseTime(testRecipe)).toBe(0); //no total time
	expect(budget.parseTime(testRecipe2)).toBe(25);
});

test("parse correct ingredients", () => {
	expect(budget.parseIngredients(testRecipe)[0]).toStrictEqual(testIngArray[0]);
	expect(budget.parseIngredients(testRecipe)[9]).toStrictEqual(testIngArray[9]);
});

test("parse correct instructions", () => {
	expect(budget.parseInstructions(testRecipe)[0]).toBe("Prepare the balsamic sauce first. In a small bowl, stir together the balsamic vinegar, soy sauce, brown sugar, minced garlic, and dried thyme. Set the sauce aside.");
});

test("parse url correctly", async () => {
	var recipe_promise = budget.parseUrl("https://www.budgetbytes.com/one-pot-creamy-pesto-chicken-pasta/");
	var recipe = await recipe_promise.then();
	expect(recipe.url).toBe("https://www.budgetbytes.com/one-pot-creamy-pesto-chicken-pasta/");
	expect(recipe.name).toBe("One Pot Creamy Pesto Chicken Pasta");
	expect(recipe.tags).toStrictEqual([ "pasta", "chicken", "creamy" ]);
});

