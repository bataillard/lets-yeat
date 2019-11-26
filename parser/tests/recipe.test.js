/*
* This file test recipe.js
* namely convertQuantity()
*/
const Recipe = require("../recipe.js");

	
test("checks if integer converts", () => {
	expect(Recipe.Ingredient.convertQuantity("10")).toBe(10);
});

test("checks if decimal converts", () => {
	expect(Recipe.Ingredient.convertQuantity("1.1")).toBe(1.1);
});

test("checks if fraction converts", () => {
	expect(Recipe.Ingredient.convertQuantity("1/4")).toBe(.25);
});

test("checks if length empty return null", () => {
	expect(Recipe.Ingredient.convertQuantity("")).toBe(null);
});



