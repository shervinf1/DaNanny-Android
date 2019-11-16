import serial
import datetime
import os
import json
import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
import pyrebase
from firebase import Firebase


config = {
	"apiKey":"AIzaSyAItt9ZfKPFqM_0V92_f5f3wHQmCEBWmGY",
	"authDomain":"com.example.dananny",
	"databaseURL":"https://firestore.googleapis.com/v1/",
	"storageBucket":"energy-core.appspot.com"
}

firebase = Firebase(config)


# Log the user in
email = "jcruz@test.com"
password = "12345678"
user = auth.sign_in_with_email_and_password(email,password)

info = auth.get_account_info(user['idToken']
print(info['users'][0]['localId'])

# Get a reference to the auth service
auth = firebase.auth()


# Use a service account for firestore connection. Modify the path where the json is stored 
cred = credentials.Certificate('/home/pi/Desktop/energy-core-firebase-adminsdk-2lnli-4d6dc8dbe9.json')
firebase_admin.initialize_app(cred)

db = firestore.client()

#Determine is arduino is connected to expected port
def is_arduino_connected():
	return os.path.exists('/dev/ttyACM1')


#Create serial object based of available connection
if is_arduino_connected():
	print('Found arduino')
	ser = serial.Serial('/dev/ttyACM1', 115200)
else:
	print('Waiting connection')

def create_reading_firestore():
	#Create a new reading/record from serial read

    serialLine = ser.readline()
    print(serialLine.decode("utf-8"))
	
	#Read serial line and decode bytes into a string that will be split by commas
    sensorData = serialLine.decode("utf-8").split(',')
    dateTime = datetime.datetime.now().timestamp()
    
	#If sensorData array is another size than 4 data.
    if(len(sensorData) == 14):
        reading = (dateTime,sensorData[0], sensorData[1], sensorData[2],sensorData[3],sensorData[4],sensorData[5],sensorData[6],sensorData[7],sensorData[8],sensorData[9],sensorData[10],sensorData[11],sensorData[12],sensorData[13])
        print(reading)
	VBATT=sensorData[0]
	TEMP=sensorData[1]
	FANID=sensorData[2]
        CURRFAN=sensorData[3]
        VFAN=sensorData[4]
        WFAN=sensorData[5]
	LEDID=sensorData[6]
        CURRLED=sensorData[7]
        VLED=sensorData[8]
	WLED=sensorData[9]
	SOURCE=sensorData[10]
	CURRGEN=sensorData[11]
	VGEN=sensorData[12]
	WGEN=sensorData[13]
	
	userId=db.collection(u'Users').document(info['users'][0]['localId'])
	sourceId=db.collection(u'Sources').document(SOURCE)
	fanId=db.collection(u'Devices').document(FANID)
	ledId=db.collection(u'Devices').document(LEDID)
	
	
        print("Insert in Firestore")
		#Adding Data
	gen_ref=db.collection(u'Generation').document()
	device1_ref=db.collection(u'Measurements').document()
	device2_ref=db.collection(u'Measurements').document()
	batt_ref=db.collection(u'Battery').document()
	temp_ref=db.collection(u'Temperature').document()
	

	gen_ref.set({
	u'date':dateTime,
	u'current':CURRGEN,
	u'voltage':VGEN,
	u'watts':WGEN,
	u'userID':userId,
	u'sourceID':sourceId
	})

	device1_ref.set({
	u'date':dateTime,
	u'current':CURRFAN,
	u'voltage':VFAN,
	u'watts':WFAN,
	u'userID':userId,
	u'deviceID':ledId
	})

	device2_ref.set({
	u'date':dateTime,
	u'current':CURRFAN,
	u'voltage':VFAN,
	u'watts':WFAN,
	u'userID':userId,
	u'deviceID':fanId
	})


	temp_ref.set({
	u'date':dateTime,
	u'temperature':TEMP,
	u'userID':userId
	})


#Initiate database connection and determine which serial to read
def main():
    conn = create_connection(CONST_DATABASE)
    if is_arduino_connected():
        print('reading arduino')
        create_reading_firestore()         
    else:
        print('Waiting connection')
    

#If this module runs as main then loop indefinetly
if __name__ == '__main__':
	while True:
		main()

