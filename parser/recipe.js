/**
 * Represents basic recipe following the scheme defined as:
 *
 * : Recipe {
 *   name: string,
 *   pictureUrl: string?        // URL pointing to image, may be null
 *   time: int,                 // Number of minutes
 *   difficulty: float,         // In range 0-5
 *   ingredients: [Ingredient]  // List of ingredient objects 
 *   instructions: [string]     // Ordered list of steps
 *   tags: [string]             // List of tags for each recipe
 * }
 * 
 * inspired by https://schema.org/Recipe 
 * This will then be transmitted to the database to be stored
 */
exports.Recipe = class Recipe { 
    constructor(url, name, pictureUrl, time, difficulty, ingredients, instructions, tags) {
        this.url = url;
        this.name = name;
        this.pictureUrl = pictureUrl;
        this.time = time;
        this.difficulty = difficulty;
        this.ingredients = [...ingredients];
        this.instruction = [...instructions];
        this.tags = [...tags];
    }
};

/**
 * Represents each ingredient under the following schema
 * Ingredient {
 *   name: string,
 *   quantity: number?,
 *   unit: string
 * }
 */
exports.Ingredient = class Ingredient {

    /**
     * Converts quantity into a floating point number
     * @param {string, float} quantity 
     * @return {float, null} float or null if uncountable quantity (salt)
     */
    static convertQuantity(quantity) {
        const fractionRegExp = /(\d+)\s*\/\s*(\d+)/;
        const numberRegExp = /\d+([.]\d+)?/;
        
        if (typeof quantity === "string") {
            if (fractionRegExp.test(quantity)) {
                const match = quantity.match(fractionRegExp);
                return parseFloat(match[1]) / parseFloat(match[2]);
            }

            if (quantity.length === false) {
                return null;
            }

            if (numberRegExp.test(quantity)) {
                return parseFloat(quantity.match(numberRegExp)[0]);
            }

            return null;
        } else {
            return quantity;
        }
    }

    constructor(name, quantity, unit) {
        this.name = name.replace(/[*]/g, "");
        this.quantity = this.constructor.convertQuantity(quantity);
        this.unit = unit;
    }
};