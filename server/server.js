/*
	Main server js file
	- manage interaction with user DB and receipe DB

*/
// for Tim's computer
//var firebasepath = "/home/firebasekey.json"
// if running on Server
var firebasepath = "/home/ubc/firebasekey.json"
// if running on you rown computer, use the following:
// var firebasepath = "PATH-TO-THIS-FILE/firebasekey.json"
// this file is credentials for firebase admin, Martin has sent it to the group in slack

// this is Martin's emulator device access token
var martinDeviceToken = "fcmXO6W_TEQ:APA91bEjSjsLFH4xu5h9rUC_rYKC-J-I5f5t7fmKdsgikji2J-g2yephdxVyeQznxdAmw8SaWETbhQR4MIhw_MpH3VLdpQihJknx9OWUHVNRDjgBpN0k5Le-1D-EeNpJTnqw4qg5cDSH"
var devicetoken = "e_wP1VIOmw4:APA91bHFToKrYKnYTbe2QpsbdEZ_gpj4ADvc9IU0h-p4VqSM5RPV0w04H_eIMUaHZKuJghtjFB-NeOx3w4bVnjZY2sC3DtTQnBfjQqszG6SKa5nWpWog_hYEraaeOeBFrpRvEBjP-kui" 
// get for Luca's device, testing with Kyle's

var parser = require('../parser')
//var recipees = require('/classes')
var admin = require('firebase-admin')
const express = require('express')
const mongoClient = require('mongodb').MongoClient
const serverURL = "mongodb://localhost:27017/";
var ObjectId = require('mongodb').ObjectID;
var server = express();
server.use(express.json())
var db
var user
var recipe

// parse receipes from 1st website
const url = "https://www.budgetbytes.com/ground-turkey-stir-fry/";


mongoClient.connect(serverURL, {useNewUrlParser: true,useUnifiedTopology: true }, (err,client) =>{
	if (err) return console.log(err)
	db = client.db('backenddb')
	users = db.collection("user")
	recipes = db.collection("recipe")
	// listen to port
	server.listen(3001,function(){
		console.log("server is up!!!!")
	})
	// initialization for firebase
	admin.initializeApp({
        credential: admin.credential.applicationDefault()
    })
    console.log("server is up!!!!")
})



/******************************************************************************/
/******************************************************************************/
/**********************************API CALLS***********************************/
/******************************************************************************/
/******************************************************************************/
server.get('/test', (req, res) => {
     db.collection("recipe").find().toArray((err, result) => {
        if(err){
            console.log(err);
        } else {
            console.log(result[0]);
        }
    })
})

/* Get recipe from recipe ID
 * returns the entire recipe json object on success
 * returns 401 on bad input ID, and 400 on database failure*/
server.get('/recipe/id', (req, res) => {
    let { id } = req.query;
    db.collection("recipe").find({ "_id": new ObjectId(id) }).toArray((err, result) => {
        if (result.length == 0) {
            res.status(401).json("No recipe with this ID");
        } else {
            recipID = new RecipeID(result[0]._id);
            var ing = [];
            ingredientes = result[0].ingredients;
            ingredientes.forEach(function (element) {
                var temp = new Ingredient(element.name, element.quantity, element.unit);
                ing.push(temp);
            });

            var recipe = new Recipe(recipID, result[0].name, result[0].url, result[0].time, result[0].difficulty, ing, result[0].tags, result[0].instruction);

            if (err) {
                res.status(400).json("Error with database");
            } else {
                res.status(200).json(recipe);
            }
        }
    })
})

server.get('/us', (req, res) => {
    console.log("here");
    res.json("Success");
})

/* Get a new recipe suggestion
 * Returns just the id for the new recipe*/
server.get('/recipe/suggest', (req, res) => {
    console.log("getting a suggestion");
    db.collection("recipe").find().toArray((err, result) => {
        recip = new RecipeID(result[0]._id);
        if (result.length == 0) {
            res.status(401).json("No available recipes");
        } else {
            res.status(200).json(recip);
        }
    })
})


/* Get a list of recipe stubs to display to the user
 * Returns only the id, name, picture, time, and difficulty */
server.get('/recipe/list', (req, res) => {
    let {max} = req.query;
    db.collection("recipe").find().toArray((err, result) => {
        if (err) {
            res.status(400).json("database failure with code: " + err);
        } else {
            console.log(result[0]);
            var i = 0;
            var stubs = []
            for (i = 0; i < Math.min(max, result.length); i++) {
                var stub = new RecipeStub(result[i].id, result[i].name, result[i].url, result[i].time, result[i].difficulty);
                stubs.push(stub);
            }
            res.status(200).json(stubs);
        }
    })
})


/* Add a new notification for a specified user */
server.post('/notification/new', (req, res) => {
    let { email, RecipeID } = req.query;

    db.collection("user").find({ "email": email }).toArray((err, result) => {
        var message = {
            notification: {
                title: "Time to cook",
                body: "Get in the kitchen and make mama proud!",
            },
            token: result[0].token
        }

        // send message via firebase push notification to Kyle's phone
        admin.messaging().send(message).then((response) => {
            res.status(200).json("Sent message");
        }).catch((error) => {
            res.status(400).json("Something went terribly wrong");
        })
     })
})


/* Update a users firebase token */
server.put("/user/token", (req, res) => {
    let { email, token } = req.query;

    var update = { $set: {} };
    update.$set["token"] = token;
    users.updateOne({ "email": email }, update)
        res.status(200).json("Token update complete.")

})


/* Attempt logging in a user 
 * Returns a token for later call authentication */
server.post("/user/login", (req, res) => {
    let { email, secret, firebaseToken, fromGoogle } = req.body;
    console.log(email);
    db.collection("user").find({ "email": email }).toArray((err, result) => {
        console.log(result);
        if(err){
            login = new LoginResult(false, false, "asdnfjk");
            res.status(400).json(login)
        } else if (result.length != 1) {
            login = new LoginResult(true, true, "asdnfjk");
            res.status(200).json(login);
        } else if ((result[0].password) !== secret) {
            login = new LoginResult(false, false, "asdnfjk");
            res.status(200).json(login);
        } else {
            login = new LoginResult(true, false, "asdnfjk");
            res.status(200).json(login);
        }

    })
})


/* Register a new user */
server.post("/user/register", (req, res) => {

    let { user, choice } = req.body;
    db.collection("user").find({ "email": user.email }).toArray((err, result) => {
        if (result.length != 0) {
            login = new LoginResult(false, false, "asdnfjk");
            res.status(200).json(login);
        } else {
            users.insertOne({
                "email": user.email,
                "password": user.secret,
                "difficulty": choice.difficulty,
                "preferences": choice.tags,
                "cookTime": choice.time,
                "token": user.firebaseToken
            }, (err, result) => {
                if (err) {
                    login = new LoginResult(false, false, "asdnfjk");
                    res.status(400).json(login);
                    return console.log(err);
                } else {
                    login = new LoginResult(true, false, "asdnfjk");
                    res.status(200).json(login)
                }
            });
        }
    })
})



/******************************************************************************/
/******************************************************************************/
/*********************************Classes**************************************/
/******************************************************************************/
/******************************************************************************/
class Recipe {
    constructor(id, name, pictureURL, time, difficulty, ingredients, tags, instructions) {
        this.id = id;
        this.name = name;
        this.pictureURL = pictureURL;
        this.time = time;
        this.difficulty = difficulty;
        this.ingredients = ingredients;
        this.tags = tags;
        this.instructions = instructions;
    }
};

class RecipeID {
    constructor(id) {
        this.id = id;
    }
}

class Ingredient {
    constructor(name, quantity, unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }
}

class RecipeStub {
    constructor(id, name, pictureUrl, time, difficulty) {
        this.id = id;
        this.name = name;
        this.pictureURL = pictureUrl;
        this.time = time;
        this.difficulty = difficulty;
    }
}

class LoginResult{
    constructor(success, needsRegistration, serverAuthToken){
        this.success = success;
        this.needsRegistration = needsRegistration;
        this.serverAuthToken = serverAuthToken;
    }
}