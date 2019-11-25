/*
    Main server js file
    - manage interaction with user DB and receipe DB

*/
// for Tim's computer
//var firebasepath = "/home/firebasekey.json"
// if running on Server
var firebasepath = "/home/ubc/project/server/google.json"
// if running on you rown computer, use the following:
// var firebasepath = "PATH-TO-THIS-FILE/firebasekey.json"
// this file is credentials for firebase admin, Martin has sent it to the group in slack

// this is Martin's emulator device access token
var martinDeviceToken = "fcmXO6W_TEQ:APA91bEjSjsLFH4xu5h9rUC_rYKC-J-I5f5t7fmKdsgikji2J-g2yephdxVyeQznxdAmw8SaWETbhQR4MIhw_MpH3VLdpQihJknx9OWUHVNRDjgBpN0k5Le-1D-EeNpJTnqw4qg5cDSH"
var devicetoken = "e_wP1VIOmw4:APA91bHFToKrYKnYTbe2QpsbdEZ_gpj4ADvc9IU0h-p4VqSM5RPV0w04H_eIMUaHZKuJghtjFB-NeOx3w4bVnjZY2sC3DtTQnBfjQqszG6SKa5nWpWog_hYEraaeOeBFrpRvEBjP-kui"
// get for Luca's device, testing with Kyle's
var serviceAccount = require("./google.json")
//var parser = require("/home/ubc/project/parser")
var admin = require('firebase-admin')
const express = require('express')
const mongoClient = require('mongodb').MongoClient
const serverURL = "mongodb://127.0.0.1:27017/";
var ObjectId = require('mongodb').ObjectID;
var server = express();
server.use(express.json())
var db
var user
var recipe

module.exports.server = server;


// parse receipes from 1st website
const url = "https://www.budgetbytes.com/ground-turkey-stir-fry/";



mongoClient.connect(serverURL, { useNewUrlParser: true, useUnifiedTopology: true }, async (err, client) => {
    if (err) return 
    db = client.db('backenddb')
    users = db.collection("user")
    recipes = db.collection("recipe")
    // listen to port
	server.listen(3001, function () {})
    // initialization for firebase
    admin.initializeApp({
        credential: admin.credential.cert(serviceAccount)
        //credential: admin.credential.applicationDefault()
    })
})


/******************************************************************************/
/******************************************************************************/
/**********************************API CALLS***********************************/
/******************************************************************************/
/******************************************************************************/

/**
 * 
 * Get recipe from recipe ID
 * returns the entire recipe json object on success
 * returns 401 on bad input ID, and 400 on database failure
 *  
 */

server.get('/recipe/id', (req, res) => {
    let { id } = req.query;
    if(id == "notification_test"){
        db.collection("recipe").find().limit(1).toArray((err, result) => {
            if (result.length == 0) {
                res.status(401).json("No recipe with this ID");
            } else {
                recipID = new RecipeID("notification_test");
                var ing = [];
                ingredientes = result[0].ingredients;
                ingredientes.forEach(function (element) {
                    var temp = new Ingredient(element.name, element.quantity, element.unit);
                    ing.push(temp);
                });
    
                var recipe = new Recipe(recipID, result[0].name, result[0].pictureUrl, result[0].time, result[0].difficulty, ing, result[0].tags, result[0].instruction);
    
                if (err) {
                    res.status(400).json("Error with database");
                } else {
                    res.status(200).json(recipe);
                }
            }
        })
    }else{
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

                var recipe = new Recipe(recipID, result[0].name, result[0].pictureUrl, result[0].time, result[0].difficulty, ing, result[0].tags, result[0].instruction);

                if (err) {
                    res.status(400).json("Error with database");
                } else {
                    res.status(200).json(recipe);
                }
            }
        })
    }
})
/**
 * Input: user id (email in this case)
 * 
 * Function: Get a new recipe suggestion
 * Return: id for recommended recipe
 * 
 */

server.get('/recipe/suggest', (req, res) => {
    let { email } = req.query;
    getUserPromise = new Promise(function (resolve, reject) {
        db.collection("user").find({ "email": email }).toArray((err, result) => {
            if (err) {
                reject("Database Failure");
            } else if (result.length != 1) {
                reject("No user with that email");
            } else {
                let user = new User(result[0].email, result[0].password, result[0].preferences, result[0].cookTime, result[0].token);
                resolve(user);
            }
        })

    })

    getUserPromise.then(function (retrievedUser) {
        db.collection("recipe").find({ tags: { $all: retrievedUser.preferences } }).toArray((err, result) => {
            if (err) {
                res.status(401).json("Database Failure");
            }else if (result.length == 0) {
                res.status(402).json("No available recipes");
            } else {
                randNum = Math.floor(Math.random() * Math.floor(result.length))
                recip = new RecipeID(result[randNum]._id);
                res.status(200).json(recip);
            }
        })

    }, function (err) {
        res.status(400).json(err);
    });
})


/* Get a list of recipe stubs to display to the user
 * Returns only the id, name, picture, time, and difficulty */
server.get('/recipe/list', (req, res) => {
    let { email, max, search, tags } = req.query;
        if (tags != undefined || search != undefined) {
            if(tags != undefined) {
                if (tags[1].length == 1) {
                    temp = tags;
                    tags = [];
                    tags.push(temp);
                }
                db.collection("recipe").find({ tags: { $all: tags } }).toArray((err, result) => {
                    if (err) {
                        res.status(400).json("Database Failure");
                    } else {
                        if (result[0] != undefined) {
                        }
                        var i = 0;
                        var stubs = []
                        var allIDs = []
                        for (i = 0; i < Math.min(max, result.length); i++) {
                            var idd = new RecipeID(result[i]._id);
                            allIDs.push(result[i]._id);
                            var stub = new RecipeStub(idd, result[i].name, result[i].pictureUrl, result[i].time, result[i].difficulty);
                            stubs.push(stub);
                        }
                        if (search == "") {
                            if (stubs.length > 0) {
                                res.status(200).json(stubs);
                            } else {
                                res.status(400).json("no recipes matching those tags or search");
                            }
                        } else {
                            db.collection("recipe").find({ name: search }).toArray((err, result) => {
                                if (err) {
                                    res.status(400).json("Database Failure");
                                } else {

                                    if (result[0] != undefined) {
                                    }
                                    var i = 0;
                                    let currentLength = stubs.length;
                                    for (i = 0; i < Math.min(max - currentLength, result.length); i++) {
                                        var idd = new RecipeID(result[i]._id);
                                        if (!(allIDs.includes(result[i]._id))) {


                                            var stub = new RecipeStub(idd, result[i].name, result[i].pictureUrl, result[i].time, result[i].difficulty);
                                            stubs.push(stub);
                                            allIDs.push(result[i]._id);
                                        }
                                    }
                                    if (stubs.length > 0) {
                                        res.status(200).json(stubs);
                                    } else {
                                        res.status(400).json("no recipes matching those tags or search");
                                    }
                                }
                            })
                        }
                    }
                })
            }
            else{
                db.collection("recipe").find({ name: search }).toArray((err, result) => {
                    if (err) {
                        res.status(400).json("Database Failure");
                    } else {
                        var i = 0;
                        var stubs = [];
                        for (i = 0; i < Math.min(max, result.length); i++) {
                            var idd = new RecipeID(result[i]._id);
                            var stub = new RecipeStub(idd, result[i].name, result[i].pictureUrl, result[i].time, result[i].difficulty);
                            stubs.push(stub);
                        }
                        if (stubs.length > 0) {
                            res.status(200).json(stubs);
                        } else {
                            res.status(400).json("no recipes matching those tags or search");
                        }
                    }
                })
            }
		} else {
			getUserPromise = new Promise(function (resolve, reject) {
				db.collection("user").find({ "email": email }).toArray((err, result) => {
					if (err) {
						reject("Database Failure");
					} else if (result.length != 1) {
						reject("No user with that email");
					} else {
						let user = new User(result[0].email, result[0].password, result[0].preferences, result[0].cookTime, result[0].token);
						resolve(user);
					}
				})

			})

			getUserPromise.then(function (retrievedUser) {
	//            retrievedUser.preferences = ['chicken'];

				db.collection("recipe").find({ tags: { $all: retrievedUser.preferences } }).toArray((err, result) => {
					if (err) {
						res.status(401).json("Database Failure");
					} else if (result.length == 0) {
						res.status(401).json("No available recipes");
					} else {
						var i = 0;
						var stubs = []
						for (i = 0; i < Math.min(max, result.length); i++) {
							var idd = new RecipeID(result[i]._id);
							var stub = new RecipeStub(idd, result[i].name, result[i].pictureUrl, result[i].time, result[i].difficulty);
							stubs.push(stub);
						}
						res.status(200).json(stubs);
					}
				})

			}, function (err) {
				res.status(400).json(err);
			});
		}
	
})

/* Add a new notification for a specified user */
server.post('/notification/new', (req, res) => {
    let notificationBody = req.body;
    let dt = Date.now();
    let curDate = new Date(dt);
    if(notificationBody.id.id == "notification_test"){
        db.collection("recipe").find().limit(1).toArray((err, recipeResult) => {
            if (err) {
                res.status(400).json("Database Failure");
            } else if (recipeResult.length == 0){
                res.status(400).json("Bad Recipe ID");
            } else {
                let recName = recipeResult[0].name;
                if (notificationBody.time != undefined) {
                    let temp = new Date(notificationBody.time)
                    temp = temp.getTime();
                    secs = temp % 60000;
                    temp -= secs;
                    testDate = new Date(temp);
                    let timeTillNot = testDate.getTime() - curDate.getTime();
                    if (timeTillNot < 0) {
                        res.status(400).json("Negative notificaion time");
                    } else {
                        notificationBody.id.id = "" + recipeResult[0]._id;
                        makeNewNotification(timeTillNot, notificationBody, recName);
    
                        res.status(200).json("notification should be G");
                    }
                }
            }
        })
    }else{
        db.collection("recipe").find({  "_id": new ObjectId(notificationBody.id.id) }).toArray((err, recipeResult) => {
            if (err) {
                res.status(400).json("Database Failure");
            } else if (recipeResult.length == 0){
                res.status(400).json("Bad Recipe ID");
            } else {
                let recName = recipeResult[0].name;
                if (notificationBody.time != undefined) {
                    let temp = new Date(notificationBody.time)
                    temp = temp.getTime();
                    temp -= recipeResult[0].time * 60 * 1000
                    //temp += (8 * 60 * 60 * 1000);
                    secs = temp % 60000;
                    temp -= secs;
                    testDate = new Date(temp);
                    console.log("our time" + testDate);
                    console.log("server time" + curDate)
                    let timeTillNot = testDate.getTime() - curDate.getTime();
                    if (timeTillNot < 0) {
                        res.status(400).json("Negative notificaion time");
                    } else {

                        makeNewNotification(timeTillNot, notificationBody, recName);

                        res.status(200).json("notification should be G");
                    }
                } else {
                    var rectime = recipeResult[0].time
                    db.collection("user").find({ "email": notificationBody.email }).toArray((err, result) => {
                        if (result.length == 0) {
                            res.status(401).json("Bad email");
                        }
                        let t = result[0].cookTime
                        let cookt = "";
                        cookt += (curDate.getYear() + 1900) + "-";
                        cookt += (curDate.getMonth() + 1) + "-";
                        cookt += (curDate.getDate()) + "T";
                        let hours = 0 + t.hourOfDay;
        
                        //hours += 8;
                        if (hours > 24) {
                            hours -= 24;
                        }
                        if (hours < 10) {
                            cookt += "0" + hours + ":";
                        } else {
                            cookt += hours + ":";
                        }

                        if (t.minute < 10) {
                            cookt += "0" + t.minute + ":";
                        } else {
                            cookt += t.minute + ":"
                        }

                        cookt += "00";
                        let temp = new Date(cookt);
                        temp = temp.getTime();
                        temp -= rectime * 60 * 1000
                        secs = temp % 60000;
                        temp -= secs;
                        let testDate = new Date(temp);
                        if (testDate < curDate) {

                            testDate.setDate(testDate.getDate() + 1)
                        }
                        let timeTillNot = testDate.getTime() - curDate.getTime();
                        if (timeTillNot < 0) {
                            res.status(404).json("Negative notificaion time");
                        } else {

                            makeNewNotification(timeTillNot, notificationBody, recName);

                            res.status(200).json("notification should be G");
                        }
                    })
                }
            }
        })
    }
})

function makeNewNotification(timeTillNot, notificationBody, recName) {
    let timeOut = setTimeout(function () {
        db.collection("user").find({ "email": notificationBody.email }).toArray((err, result) => {
            var message = {
                notification: {
                    title: "Time to cook",
                    body: "Get in the kitchen and make mama proud!",
                },
                data: {
                        id: notificationBody.id.id,
                        name: recName
                },
                token: result[0].token
            }

            // send message via firebase push notification to Kyle's phone
            admin.messaging().send(message).then((response) => {
                console.log("sending notification");
            }).catch((error) => {
                console.log(error);
            })
        })
    }, timeTillNot)
}


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
    let  user  = req.body;
    db.collection("user").find({ "email": user.email }).toArray((err, result) => {
        if (err) {
            login = new LoginResult(false, false, "asdnfjk");
            res.status(400).json(login) 
        }else if (result.length != 1) {
            login = new LoginResult(true, true, "asdnfjk");
            res.status(200).json(login);
        } else if (user.fromGoogle) {
            login = new LoginResult(true, false, "asdnfjk");
            res.status(200).json(login);
        } else if ((result[0].password) !== user.secret) {
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
            res.status(409).json(login);
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
/*****************************Helper Functions*********************************/
/******************************************************************************/
/******************************************************************************/

function getUserByEmail(email) {
    db.collection("user").find({ "email": email }).toArray((err, result) => {
        if (err) {
            return ["Database Failure", 400];
        } else if (result.length != 1) {
            return ["No user with that email", 401];
        } else {
            let user = new User(result[0].email, result[0].password, result[0].preferences, result[0].cookTime, result[0].token);
            return [user, 200];
        }
    })
};
/******************************************************************************/
/******************************************************************************/
/*********************************Classes**************************************/
/******************************************************************************/
/******************************************************************************/
class Recipe {
    constructor(id, name, pictureUrl, time, difficulty, ingredients, tags, instructions) {
        this.id = id;
        this.name = name;
        this.pictureUrl = pictureUrl;
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
};

class RecipeStub {
    constructor(id, name, pictureUrl, time, difficulty) {
        this.id = id;
        this.name = name;
        this.pictureUrl = pictureUrl;
        this.time = time;
        this.difficulty = difficulty;
    }
};

class LoginResult {
    constructor(success, needsRegistration, serverAuthToken) {
        this.success = success;
        this.needsRegistration = needsRegistration;
        this.serverAuthToken = serverAuthToken;
    }
};

class User {
    constructor(email, secret, preferences, cookTime, token) {
        this.email = email;
        this.secret = secret;
        this.preferences = preferences;
        this.cookTime = cookTime;
        this.token = token;
    }
};

