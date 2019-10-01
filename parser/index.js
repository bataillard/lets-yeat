const rp = require('request-promise');
const $ = require('cheerio');
const url = "https://www.budgetbytes.com/ground-turkey-stir-fry/";

main(url);

function main(url) {
    rp(url)
    .then((html) => {
        const recipe = $(".wprm-recipe", html);
        const name = $(".wprm-recipe-name", recipe);
        console.log("Recipe name: " + name.text());
    })
    .catch((err) => {
        console.log(err);
    });
}



