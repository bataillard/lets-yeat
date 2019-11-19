/**
 * We only call this script periodicly to add more recipes into recipe collection
 * 
 */

const parse1 = require('../parser/budgetbytes')
const parse2 = require('../parser/foodnetwork')
const parse3 = require('../parser/allrecipe')
const parse4 = require('../parser/food')
const parse5 = require('../parser/skinnytaste')
const toGet = 30;
const mongoClient = require('mongodb').MongoClient
const serverURL = "mongodb://localhost:27017/";
var db;
var recipe;

mongoClient.connect(serverURL, {useNewUrlParser: true,useUnifiedTopology: true }, (err,client) =>{
    if (err) return console.log(err)
    console.log("Parsing recipes . . .")
    db = client.db('backenddb')
	recipe = db.collection("recipe")
})

// (max, fromYear, fromMonth, toYear, toMonth)
var batch2018 = parse1.parseByDate(100,2016,1,2019,10).then(batch =>{
    for (each_recipe of batch){
        if(each_recipe != null){
            recipe.insert(each_recipe)
        }
    }
    console.log("Insertion 1 success: budgetbyte.");
});

// var batch_foodnetwork = parse2.getRecipes(toGet).then(batch =>{
//     for (each_recipe of batch){
//         if(each_recipe != null){
//             recipe.insert(each_recipe)
//         }
//     }
//     console.log("Insertion 2 success: food network.");
// })

var batch_allrecipe = parse3.getRecipes(toGet).then(batch =>{
    for (each_recipe of batch){
        if(each_recipe != null){
            recipe.insert(each_recipe)
        }
    }
    console.log("Insertion 3 success: all recipe.");
})

var batch_food = parse4.getRecipes(toGet).then(batch =>{
    for (each_recipe of batch){
        if(each_recipe != null){
            recipe.insert(each_recipe)
        }
    }
    console.log("Insertion 4 success: food.");
})

var batch_foodnetworth = parse5.getRecipes(toGet).then(batch =>{
    for (each_recipe of batch){
        if(each_recipe != null){
            recipe.insert(each_recipe)
        }
    }
    console.log("Insertion 5 success: Skinny Taste.");
})

return;