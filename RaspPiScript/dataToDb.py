import serial
import datetime
import os
import json
import sqlite3
from sqlite3 import Error
import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore

# Use a service account modify the path 
cred = credentials.Certificate('/home/jalex/Documents/FirestoreTest/smart-iot-monitoring-system-firebase-adminsdk-2eh9a-0fb43ffd0e.json')
firebase_admin.initialize_app(cred)

db = firestore.client()

#Determine is arduino is connected to expected port
def is_arduino_connected():
	return os.path.exists('/dev/ttyACM0')

#Create serial object based of available connection
if is_arduino_connected():
	print('Found arduino')
	ser = serial.Serial('/dev/ttyACM0', 9600)

def create_reading():
	#Create a new reading/record from serial read

	serialLine = ser.readline()
	print(serialLine.decode("utf-8"))
	
	#Read serial line and decode bytes into a string that will be split by commas
	sensorData = serialLine.decode("utf-8").split(',')
	dateTime = datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S').decode('utf-8')

	#If sensorData array is another size than 4 data.
	if(len(sensorData) == 7):
		reading = (dateTime,sensorData[0], sensorData[1], sensorData[2],sensorData[3],sensorData[4],sensorData[5])
		print(reading)

		#calculate Dc Load Power
		dcloadPower=sensorData[1]*sensorData[5]
		#Adding Data
		doc_ref = db.collection(u'values').document(u'User X').collection('messages').document()
		doc_ref.set({
		u'date':dateTime,
		u'CURRMWT':sensorData[0],
		u'CURRBATT':sensorData[1],
		u'VBATT':sensorData[2],
        u'VAVG' : sensorData[3],
        u'WAVG' : sensorData[4],
        u'DCLOAD': sensorData[5],
        u'DCPOWER': dcloadPower
		})

#Read Equipment status 
doc_ref = db.collection(u'own').document(u'led')

doc =doc_ref.get()
print(doc)


def main():
	if is_arduino_connected():
		print('reading arduino')
		create_reading()
	else:
		print('Waiting connection')


#If this module runs as main then loop indefinetly
if __name__ == '__main__':
	while True:
		main()
