/*
	Main server js file
	- manage interaction with user DB and receipe DB

*/

var parser = require('../parser')
const express = require('express')
const mongoClient = require('mongodb').MongoClient
const router = express.Router();
const serverURL = "mongodb://localhost:27017/";

var server = express();
server.use(express.json())
var db
var user
var recipe

mongoClient.connect(serverURL, {useNewUrlParser: true,useUnifiedTopology: true }, (err,client) =>{
	if (err) return console.log(err)
	db = client.db('backenddb')
	user = db.collection("user")
	recipe = db.collection("recipe")
	// listen to port
	server.listen(3001,function(){
		console.log("server is up!!!!")
	})
})

/* Routing RESTful */

server.get('/', (req,res)=>{
	res.send(user.findOne())
});

/* Create new user profile */
server.post('/user/new', (req,res) => {
	const username = req.body.username
	const password = req.body.password
	const diff = req.body.difficulty
	const pref = req.body.preferences
	const cookTime = req.body.cookTime
	if (!(username && password && diff && pref && cookTime)){
		res.status(400).send("Missing one or more piece of user info.")
		return
	}
	user.insertOne({
		"username":username,
		"password":password,
		"difficulty":diff,
		"preferences":pref,
		"cookTime":cookTime,
		"email":""
	},(err, result) => {
		if (err) return console.log(err);
		res.send(req.body);
	});
})
 
/**
 * Handle login via google authentication
 * 2 cases arise:
 * 1) hash of email is in user collection
 * 	 - return profile. ie. the preferences of the user
 * 2) first time user
 *   - create a profile (name, email) with preferences left blank
 *   - returns json object that notifies front-end, user needs to complete profile setup
 */
server.get('user/googlogin',(req,res)=>{
	if (req.body == null){
		res.status(400).send("Invalid google login.")
		return
	}
	var findUser = user.find({"email":req.body.email})
	// if user is already registered with our service
	if (findUser){
		res.send(findUser)
	}else{ //otherwise, we create a basic profile without preferences
		user.insertOne({
			"username": req.body.username,
			"email": req.body.email,
			"name": req.body.name
		},(err,result)=>{
			if (err) return console.log(err)
			res.send("New user")
		})
	}


})
// parse receipes from 1st website
const url = "https://www.budgetbytes.com/ground-turkey-stir-fry/";
