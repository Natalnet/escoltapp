const functions = require('firebase-functions');
const firebase = require('firebase-admin');
const request = require('request');
const uuidv1 = require('uuid/v1');

var API_KEY = '';
var serviceAccount = require("./escoltapp-b2297-924a192e2f76.json");

firebase.initializeApp({
  credential: firebase.credential.cert(serviceAccount),
  databaseURL: "",
  storageBucket: ""
});

const messaging = firebase.messaging();

const express = require("express");
const spawn = require('threads').spawn;
const app = express();

const timersMap = new Map(); 



app.get('/users/:uid', (req, res) => {
	const uid = req.params.uid;

	firebase.database().ref('/users/' + uid).once('value').then((snapshot) => {
		if (snapshot.exists()) {
			var json = {};
			json[uid] = snapshot.val();
			res.status(200).json(json);
		} else {
			res.status(500).send('O usuário com id ' + uid + ' não existe.');
		}
		return;
	}).catch((error)=>{
		console.log(error);
		res.status(500).send(error);
	});
});

app.post('/users/:uid', (req, res) => {
	const uid = req.params.uid;
	const firstName = Buffer.from(req.body.firstName).toString();
	const lastName = Buffer.from(req.body.lastName).toString();
	const phone = Buffer.from(req.body.phone).toString();
	const token =  Buffer.from(req.body.token).toString();
	const photo = Buffer.from(req.body.photo);

	// Create a root reference
	var bucket = firebase.storage().bucket();

	var file = bucket.file('/users/' + uid + '/user_photo.png');

	var fileMetadata = {
      metadata: {
        contentType: 'image/png',
        metadata: {
          custom: 'metadata'
        },
        public: true,
        validation: 'md5'
      }
    };

	file.save(photo,fileMetadata).then(() => {
		var config = {
		  action: 'read',
		  expires: 4102444740000
		};
		console.log('user image stored successfully');
		return file.getSignedUrl(config);
	}).then(array => {
		console.log('FILE PATH: ' + array[0]);
		firebase.database().ref('/users/' + uid).set({
		    firstName: firstName,
		    lastName: lastName,
		    phone : phone,
		    token : token,
		    photoUrl : array[0]
		});
		console.log('user written successfully');
		return;
	}).then(() => {
		console.log('retuning status 200');
		res.status(200).json({});
		return;
	}).catch(error => {
		console.log(error);
		res.status(500).send(error);
	});
	
});

app.get('/users/:uid/followers', (req, res) => {
	readFollow(req,res,'followers')
});

app.get('/users/:uid/following', (req, res) => {
	readFollow(req,res,'following')
});

function readFollow(req,res,follow) {
	const uid = req.params.uid;

	var followRef = firebase.database().ref('/users/' + uid + '/' + follow);

	var followList = {};

	followRef.once('value', (snapshot) => {
	 	var size = snapshot.numChildren();
	  	if(size > 0) {
	      	snapshot.forEach((childSnapshot) => {
		        var followerId = childSnapshot.key;
				var usersRef = firebase.database().ref('/users/' + followerId);
				
				usersRef.once('value',(snapshot2) => {
					followList[snapshot2.key] = snapshot2.val();
					size--;
					if (size === 0) {
						res.status(200).json(followList);
					}
				})
	      	});
  		} else {
      		res.status(200).json({});
      	}
      
    });
}

app.get('/users/:uid/addfollowing/:phone', (req, res) => {
	const uid = req.params.uid;
	const uphone = req.params.phone;

	var usersRef = firebase.database().ref('/users');
	usersRef.once('value', (snapshot) => {
		snapshot.forEach((childSnapshot) => {
			console.log(childSnapshot.val().phone + ' ' + uphone);
			if(childSnapshot.val().phone === uphone && childSnapshot.key !== uid) {
				var user = {};
				var key = childSnapshot.key;
				user[key] = childSnapshot.val();

				var add = {};
				add[key] = true;

				firebase.database().ref('/users/' + uid + '/following').update(add);

				var add2 = {};
				add2[uid] = true;

				firebase.database().ref('/users/' + key + '/followers').update(add2);

				res.status(200).json(user);
			}
		});
		res.status(500).send('Telefone não encontrado.');
	});

});

app.get('/users/:uid/addplace/:_label/:latitude/:longitude', (req, res) => {
	const uid = req.params.uid;
	const label = req.params._label;
	const latitude = req.params.latitude;
	const longitude = req.params.longitude;

	var place = {};
	place[uuidv1()] = {
		label : label,
		latitude : latitude,
		longitude : longitude
	};

	firebase.database().ref('/users/' + uid + '/places').update(place);

	res.status(200).json({});
	
});

app.get('/messaging/danger/:uid/:userphone/:firstname/:lastname', (req, res) => {
	const uid = req.params.uid;
	const userphone = req.params.userphone;
	const firstname = req.params.firstname;
	const lastname = req.params.lastname;

	var topic = userphone.replace(/[^\d]/gi,'');

	const username = firstname + ' ' + lastname;

	request({
    	url: 'https://fcm.googleapis.com/fcm/send',
    	method: 'POST',
    	headers: {
      		'Content-Type' :' application/json',
      		'Authorization': 'key='+API_KEY
    	},
    	body: JSON.stringify({
      		data : {
      			uid: uid,
        		phone: userphone,
        		firstname: firstname,
        		lastname: lastname
      		},
      		to : '/topics/' + topic
    	})
  		}, (error, response, body) => {
    		if (error) { 
    			console.error(error); 
    			res.status(500).send('erro.');
    		}
    		else if (response.statusCode >= 400) { 
      			console.error('HTTP Error: '+response.statusCode+' - '+response.statusMessage); 
      			res.status(500).send('erro.');
    		}
    		else {
    			console.log(`Message sent to topic ${topic}`)
      			res.status(200).json({'msg': `Message sent to topic ${uid}`});
    		}
  		}
	);
});



exports.api = functions.https.onRequest(app);