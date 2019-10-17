/**
 * We only call this script periodicly to add more recipes into recipe collection
 * 
 */

const parse = require('../parser/budgetbytes')
const mongoClient = require('mongodb').MongoClient
const serverURL = "mongodb://localhost:27017/";
var db
var recipe
mongoClient.connect(serverURL, {useNewUrlParser: true,useUnifiedTopology: true }, (err,client) =>{
    if (err) return console.log(err)
    db = client.db('backenddb')
	recipe = db.collection("recipe")
})

// (max, fromYear, fromMonth, toYear, toMonth)
var batch2018 = parse.parseByDate(100,2018,1,2018,2).then(batch =>{
    recipe.insertMany(batch);
    console.log("Insertion success.")
});
