import pyrebase
from firebase import Firebase

config = {
	"apiKey":"AIzaSyAItt9ZfKPFqM_0V92_f5f3wHQmCEBWmGY",
	"authDomain":"com.example.dananny",
	"databaseURL":"https://firestore.googleapis.com/v1/",
	"storageBucket":"energy-core.appspot.com"
}

firebase = Firebase(config)

# Get a reference to the auth service
auth = firebase.auth()

# Log the user in
email = "jcruz@test.com"
password = "12345678"
user = auth.get_account_info(user['idToken'])

print(info['users'][0]['localId'])