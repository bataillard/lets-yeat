'use strict';

const budget = require('./budgetbytes.js');
const url_parser = require('url');

/**
 * Usage: 
 *  single recipe: "node index.js budget recipe `url`"
 *  by date:       "node index.js budget date `max` `fromYear` `fromMonth` `toYear` `toMonth`"
 */
function main() {
    const website = process.argv[2];
    const command = process.argv[3];

    if (website !== "budget") {
        console.error("Website not supported");
        return;
    }

    if (command === "recipe") {
        const urlString = process.argv[4];
        let url; 

        try {
            url = url_parser.parse(urlString);
        } catch (err) {
            console.error(err);
            console.error("Could not parse URL: " + urlString);
            return;
        }
    
        if (url.hostname === "www.budgetbytes.com") {
            budget.parseUrl(url.href).then(recipe => console.log(recipe))
        } else {
            console.error("Unsupported URL");
            return;
        }
    } else if (command === "date") {
        const max = parseInt(process.argv[4]);
        const fy = parseInt(process.argv[5]);
        const fm = parseInt(process.argv[6]);
        const ty = parseInt(process.argv[7]);
        const tm = parseInt(process.argv[8]);

        budget.parseByDate(max, fy, fm, ty, tm).then(console.log);
    } else {
        console.error("Command not supported");
        return;
    }
}

main();




