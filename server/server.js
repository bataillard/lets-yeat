/*
	Main server js file
	- manage interaction with user DB and receipe DB

*/

var parser = require('../parser')
const express = require('express')
const mongoClient = require('mongodb').MongoClient
const serverURL = "mongodb://localhost:27017/";
var ObjectId = require('mongodb').ObjectID;
var server = express();
server.use(express.json())
var db
var user
var recipe

mongoClient.connect(serverURL, {useNewUrlParser: true,useUnifiedTopology: true }, (err,client) =>{
	if (err) return console.log(err)
	db = client.db('backenddb')
	users = db.collection("user")
	recipes = db.collection("recipe")
	// listen to port
	server.listen(3001,function(){
		console.log("server is up!!!!")
	})
})

/* Routing RESTful */

/**
 * Create new user profile
 * - restriction = no username repeats
 *  */ 
server.post('/users/', (req,res) => {
	var dupl = false; // sorry really hacky
	let { username, password, email, difficulty:diff, preferences: pref, cookTime} = req.body
	
	if (!(username && password && diff && pref && cookTime && email)){
		res.status(400).send("Missing one or more piece of user info.")
		return
	}
	var dup = users.find({"username":username}).limit(1)
	
	dup.forEach(function(usern, err){
		//console.log("usern is "+JSON.stringify(usern))
		console.log("username is "+username)
		if (usern.username == username){
			res.send("Duplicate username. Please select another username.")
			dupl = true
			return
		}
	})

	users.insertOne({
		"username":username,
		"password":password,
		"difficulty":diff,
		"preferences":pref,
		"cookTime":cookTime,
		"email":email,
		"lastCooked":[]
	},(err, result) => {
		if (err) return console.log(err);
		if (!dupl) // added this line to avoid "Cannot set headers after they are sent to the client" error.
			res.send(req.body);
	});
})
 
/**
 * Handle login via google authentication
 * 2 cases arise:
 * 1) hash of email is in user collection
 * 	 - return profile. ie. the preferences of the user
 * 2) first time user
 *   - return message that informs front end user is not in database.
 */
server.get('users/googlogin',(req,res)=>{
	if (req.body == null){
		res.status(400).send("Invalid google login.")
		return
	}
	var findUser = users.find({"email":req.body.email})
	// if user is already registered with our service
	if (findUser){
		res.send(findUser)
	}else{ //otherwise, we notify Frontend to get relevant info to create new user profile.
		res.send("New User")
	}
})

/**
 * FE provides username
 * API returns: user json or message to indicate user not in database.s
 */
server.get('/users/:username',(req,res)=>{
	var findUser = users.find({"username":req.params.username}).limit(1);
	findUser.forEach(function(doc, err){
		console.log(doc)
		res.send(doc)
	})
});

/**
 * FE provdes username and fields that wants to be updated
 * 
 * goal: change a field in user object
 * returns: fail or success message, (or no user)
 * 
 * assumptions: when patching fields preferences, lastCooked (these are arrays)
 * the entire array is passed back to replace the old one. BE assumes the FE has 
 * the original arrays and directly modifies them.
 */
server.patch('/users/:username',(req,res)=>{
	var update = { $set : {} };

	for (var field in req.body){
		update.$set[field] = req.body[field];
	}
	users.updateOne({"username":req.params.username},update)
	res.send("Update complete.")
	
})

/**
 * Get recipe from recipe ID
 * returns the entire recipe json object to F.E.
 * 
 */
server.get('/recipes/:recipeid',(req,res)=>{
	var findRecipe = recipes.find({"_id":new ObjectId(req.params.recipeid)}).limit(1);
	findRecipe.forEach(function(doc, err){
		console.log(doc)
		res.send(doc)
	})
})



// parse receipes from 1st website
const url = "https://www.budgetbytes.com/ground-turkey-stir-fry/";
