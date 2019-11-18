import os
from time import sleep
import json
import firebase_admin
from firebase_admin import credentials, firestore
import RPi.GPIO as GPIO
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
user = auth.sign_in_with_email_and_password(email,password)

info = auth.get_account_info(user['idToken'])
print(info['users'][0]['localId'])




# Create a callback on_snapshot function to capture changes
def on_snapshot(col_snapshot, changes, read_time):
    for doc in col_snapshot:
        print(u'Equipment: {}'.format(doc.id))
        print(u'Status: {}'.format(doc.to_dict()['status']))
        print(u'GPIO PIN: {}'.format(doc.to_dict()['gpio']))
        power_manager(format(doc.to_dict()['gpio']), format(doc.to_dict()['status']))


def power_manager(pin, state):
    GPIO.setmode(GPIO.BCM)
    GPIO.setup(int(pin), GPIO.OUT)
    if state == 'ON':
        GPIO.output(int(pin), GPIO.HIGH)
    else:
        GPIO.output(int(pin), GPIO.LOW)


# Use a service account modify the path 
path='/home/pi/Desktop/energy-core-firebase-adminsdk-2lnli-4d6dc8dbe9.json'
cred = credentials.Certificate(path)
firebase_admin.initialize_app(cred)

#Initialize Firestore DB
db = firestore.client()
doc_ref = db.collection(u'Devices')

# Watch the document
doc_watch = doc_ref.on_snapshot(on_snapshot)


while True:
    sleep(1)
