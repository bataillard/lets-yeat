var firebasepath = "/home/timothycheng/firebasekey.json";
var admin = require("firebase-admin");
// initialization for firebase
admin.initializeApp({
	credential: admin.credential.applicationDefault();
})

var devicetoken = "fcmXO6W_TEQ:APA91bEjSjsLFH4xu5h9rUC_rYKC-J-I5f5t7fmKdsgikji2J-g2yephdxVyeQznxdAmw8SaWETbhQR4MIhw_MpH3VLdpQihJknx9OWUHVNRDjgBpN0k5Le-1D-EeNpJTnqw4qg5cDSH";

var message = {
    notification: {
        title: "Let's Yeat",
        body: "It is time to cook!",
    },
    token: devicetoken
};

admin.messaging()
 .send(message).then((response) => {
    console.log("Successfully sent message: ", response);
}).catch((error) => {
    console.log("There is an error!! ", error);
});

return;