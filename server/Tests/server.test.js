/*
* This file tests the server.js file
*
*
*
*
*/

const request = require("supertest");
const server = require("../server.js");

const {MongoClient} = require("mongodb");
var ObjectId = require('mongodb').ObjectID;

var connection;
var db;

var Rid = "123456789123";
var Rname = "test food";
var RpictureURL = "http://testpic.com";
var Rtime = "25";
var Rdifficulty = "3";
var Ringredients = [{"name": "testIngredient",
					"quantity": 5,
					"unit": "oz"
					}, 
					{"name": "testspice",
					"quantity": 1,
					"unit": "pinch"
					},
					{"name": "testeverythingNice",
					"quantity": 100,
					"unit": "alot"
					}];
var Rtags = ["testTag1", "tagTest2"];
var Rinstructions =["testassemble", "testmix", "testcook"];
var mockRecipe

var Uemail = "test@test.com";
var Uemail2 = "test@email.com";
var Uemail3 = "fail@login.test";
var Usecret = "testSecret";
var Upreferences = ["testTag1", "tagTest2"];
var Upreferences2 = ["badtags", "getnoresults"];
var UcookTime = "";
var Utoken = "testToken";
var mockUser;
var mockUser2
var recipe

jest.setTimeout(30000);

beforeAll(async () => {
	console.log(global.__MONGO_URI__)
    connection = await MongoClient.connect(global.__MONGO_URI__, {
		useNewUrlParser: true,
		useUnifiedTopology: true
    });
    db = await connection.db(global.__MONGO_DB_NAME__);
	recipe = db.collection("recipe");
	user = db.collection("user");
	
	mockUser = {
		"user":{
			"email": Uemail,
			"secret": Usecret,
			"firebaseToken": Utoken
		},
		"choice":{
			"tags": Upreferences,
			"difficulty": Rdifficulty,
			"time": UcookTime
		}
	};
	mockUser2 = {
		"user":{
			"email": Uemail2,
			"secret": Usecret,
			"firebaseToken": Utoken
		},
		"choice":{
			"tags": Upreferences2,
			"difficulty": Rdifficulty,
			"time": UcookTime
		}
	};
	
	mockRecipe = {
		"_id": new ObjectId(Rid),
        "name": Rname,
        "time": Rtime,
        "difficulty": Rdifficulty,
        "ingredients": Ringredients,
        "tags": Rtags,
		}
	await recipe.insertOne(mockRecipe);
	
});

afterAll(async () => {
	await recipe.deleteOne({"name": Rname});
	await user.deleteOne({"email":Uemail});
	await user.deleteOne({"email":Uemail2});
	await connection.close();
})


test("gets recipe from id; result 200", async () => {	
	const res = await request(server.server)
		.get("/recipe/id")
		.query({"id": Rid})
		expect(res.statusCode).toBe(200);
});

test("gets recipe from id; result 401", async () => {	
	const res = await request(server.server)
		.get("/recipe/id")
		.query({"id": "notInDatabas"})
		expect(res.statusCode).toBe(401);
});

test("trys to register a user; success", async () => {
	const res = await request(server.server)
		.post("/user/register")
		.send(mockUser)
		expect(res.statusCode).toBe(200);	
	const res2 = await request(server.server)
		.post("/user/register")
		.send(mockUser2)
		expect(res2.statusCode).toBe(200);
});

test("trys to register a user; already in system", async () => {
	const res = await request(server.server)
		.post("/user/register")
		.send(mockUser)
		expect(res.statusCode).toBe(409);
});

test("update firebase token; correct email", async () => { 
	var data = "?email=" +Uemail +"&token=newTestToken"
	const res = await request(server.server)
		.put("/user/token" + data);
	expect(res.statusCode).toBe(200);
});

test("update firebase token; bad email", async () => { 
	var data = "?email=notRight@email.com&token=newTestToken"
	const res = await request(server.server)
		.put("/user/token" + data);
	expect(res.statusCode).toBe(200);
});

test("gets recipe suggestion; success", async () =>{
	const res = await request(server.server)
		.get("/recipe/suggest")
		.query({"email": Uemail})
		expect(res.statusCode).toBe(200);
});

test("gets recipe suggestion; no recipe to suggest", async () =>{
	const res = await request(server.server)
		.get("/recipe/suggest")
		.query({"email": Uemail2})
		expect(res.statusCode).toBe(402);
});

test("attempt logging in; fail not in database", async () => {
	mockUser2 = {
		"user":{
			"email": Uemail3,
			"secret": Usecret,
			"firebaseToken": Utoken
		},
		"choice":{
			"tags": Upreferences2,
			"difficulty": Rdifficulty,
			"time": UcookTime
		}
	};
	
	const res  = await request(server.server)
		.post("/user/login")
		.send(mockUser2);
		expect(res.statusCode).toBe(200);
});

test("attempt logging in; success", async () => {
	const res = await request(server.server)
		.post("/user/login")
		.send(mockUser);
		expect(res.statusCode).toBe(200);
});

test("attempt logging in; success but wrong pass", async () => {
	mockUser2 = {
		"user":{
			"email": Uemail,
			"secret": "notCorrectSecret",
			"firebaseToken": Utoken
		},
		"choice":{
			"tags": Upreferences2,
			"difficulty": Rdifficulty,
			"time": UcookTime
		}
	};
	
	const res  = await request(server.server)
		.post("/user/login")
		.send(mockUser2);
		expect(res.statusCode).toBe(200);
});


test("get a list of recipe stubs; only search word", async () => {
	const res = await request(server.server)
		.get("/recipe/list")
		.query({"email": Uemail,
				"max": 1,
				"search": Rname
//				"tags": {}
				})
		expect(res.statusCode).toBe(200);
});

test("get a list of recipe stubs; only tags", async () => {
	const res = await request(server.server)
		.get("/recipe/list")
		.query({"email": Uemail,
				"max": 1,
				"search": "",
				"tags": Rtags
				})
		expect(res.statusCode).toBe(200);
});

test("get a list of recipe stubs; tags and search word", async () => {
	const res = await request(server.server)
		.get("/recipe/list")
		.query({"email": Uemail,
				"max": 1,
				"search": Rname,
				"tags": Rtags
				})
		expect(res.statusCode).toBe(200);
});
test("get a list of recipe stubs; no tags and search word or user", async () => {
	const res = await request(server.server)
		.get("/recipe/list")
		.query({"max": 1})
		expect(res.statusCode).toBe(400);
});




