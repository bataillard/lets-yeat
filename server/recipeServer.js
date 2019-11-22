/**
 * We only call this script periodicly to add more recipes into recipe collection
 * 
 */

const parse1 = require('../parser/budgetbytes')
const parse2 = require('../parser/foodnetwork')
const parse3 = require('../parser/allrecipe')
const parse4 = require('../parser/food')
const parse5 = require('../parser/skinnytaste')
const toGet = 50;
const mongoClient = require('mongodb').MongoClient
const serverURL = "mongodb://localhost:27017/";
var db;
var recipe;
var fs = require('fs');
var logger
var logging = true;
mongoClient.connect(serverURL, {useNewUrlParser: true,useUnifiedTopology: true }, (err,client) =>{
    if (err) return console.log(err)
    console.log("Parsing recipes . . .")
    db = client.db('backenddb')
    recipe = db.collection("recipe")
    logger = fs.createWriteStream('log.txt', {
        flags: 'a' // 'a' means appending (old data will be preserved)
      })
})

// (max, fromYear, fromMonth, toYear, toMonth)
function batch_budgetbytes(){
    parse1.parseByDate(toGet,2016,1,2019,10).then(batch =>{
        for (each_recipe of batch){
            if(each_recipe != null){
                recipe.insertOne(each_recipe)
                if (logging)
                    logger.write(each_recipe.name+" ")
            }
        }
        console.log("Insertion 1 success: budgetbyte.");
    })
}

function batch_foodnetwork(){
    parse2.getRecipes(toGet).then(batch =>{
        for (each_recipe of batch){
            if(each_recipe != null){
                recipe.insertOne(each_recipe)
                if (logging)
                    logger.write(each_recipe.name+" ")
            }
        }
        console.log("Insertion 2 success: food network.");
    })
}

function batch_allrecipe(){
    parse3.getRecipes(toGet).then(batch =>{
        for (each_recipe of batch){
            if(each_recipe != null){
                recipe.insertOne(each_recipe)
                if (logging)
                    logger.write(each_recipe.name+" ")
            }
        }
        console.log("Insertion 3 success: all recipe.");
    })
} 

function batch_food(){
    parse4.getRecipes(toGet).then(batch =>{
        for (each_recipe of batch){
            if(each_recipe != null){
                recipe.insertOne(each_recipe)
                if (logging)
                    logger.write(each_recipe.name+" ")
            }
        }
        console.log("Insertion 4 success: food.");
    })
}

function batch_foodnetworth(){
    parse5.getRecipes(toGet).then(batch =>{
        for (each_recipe of batch){
            if(each_recipe != null){
                recipe.insertOne(each_recipe)
                if (logging)
                    logger.write(each_recipe.name+" ")
            }
        }
        console.log("Insertion 5 success: Skinny Taste.");
    })
}

function parse(){
    batch_budgetbytes();
    batch_allrecipe();
    batch_foodnetwork();
    batch_food();
    batch_foodnetworth();
}

parse();