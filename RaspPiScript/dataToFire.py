import serial
import datetime
import os
import json
import firebase_admin
import sqlite3
from sqlite3 import Error
from firebase_admin import credentials
from firebase_admin import firestore

#Change this to your sqlite3 Database Location
CONST_DATABASE = database = '/home/jalex/Documents/FirestoreTest/WindCurr.db'
#Change this to the sql for inserting a new record
CONST_SQL_INSERT = ''' INSERT INTO MWTDATA(TIMESTAMP,CURRMWT,CURRBATT,VBATT,VAVG,WAVG,DCLOAD) VALUES(?,?,?,?,?,?,?) '''

# Use a service account for firestore connection. Modify the path where the json is stored 
cred = credentials.Certificate('/home/jalex/Documents/FirestoreTest/smart-iot-monitoring-system-firebase-adminsdk-2eh9a-0fb43ffd0e.json')
firebase_admin.initialize_app(cred)

db = firestore.client()

#Determine is arduino is connected to expected port
def is_arduino_connected():
	return os.path.exists('/dev/ttyACM1')

#Determine if xbee is connected to expected port
def is_xbee_connected():
    return os.path.exists('/dev/ttyUSB0')


#Create connection to sqlite3 database file
def create_connection(db_file):
	try:
		conn = sqlite3.connect(db_file)
		return conn
	except Error as e:
		print(e)
	return None

#Create serial object based of available connection
if is_arduino_connected():
	print('Found arduino')
	ser = serial.Serial('/dev/ttyACM1', 9600)
elif is_xbee_connected():
	print('Found xbee')
	ser = serial.Serial('/dev/ttyUSB0', 9600)
else:
	print('Waiting connection')


def create_reading_sqlite3(conn, database):
	serialLine = ser.readline()
	print(serialLine.decode("utf-8"))
	if serialLine != '':
		#Read serial line and decode bytes into a string that will be split by commas
		sensorData = serialLine.decode("utf-8").split(',')
		dateTime = datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')
		#If sensorData array is another size than 13 data isn't reliable. Don't create a reading
		reading = (dateTime,sensorData[0], sensorData[1], sensorData[2],sensorData[3],sensorData[4],sensorData[5])
		insert_reading(conn,reading)


#Insert new reading/record in sqlite3 database table
def insert_reading(conn, reading):
	cur = conn.cursor()
	cur.execute(CONST_SQL_INSERT, reading)
	conn.commit()
	print('Inserting')
	print(cur.lastrowid)   

def create_reading_firestore():
	#Create a new reading/record from serial read

    serialLine = ser.readline()
    print(serialLine.decode("utf-8"))
	
	#Read serial line and decode bytes into a string that will be split by commas
    sensorData = serialLine.decode("utf-8").split(',')
    dateTime = datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S').decode('utf-8')
    
	#If sensorData array is another size than 4 data.
    if(len(sensorData) == 6):
        reading = (dateTime,sensorData[0], sensorData[1], sensorData[2],sensorData[3],sensorData[4],sensorData[5])
        print(reading)
        MWT=sensorData[0]
        BATT=sensorData[1]
        VBATT=sensorData[2]
        VAVG=sensorData[3]
        WAVG=sensorData[4]
        DCLOAD=sensorData[5]
        print("Insert in Firestore")
        #calculate Dc Load Power
        dcloadPower=float(sensorData[1])*float(sensorData[5])
		#Adding Data
        doc_ref = db.collection(u'values').document(u'User X').collection('test').document()
        doc_ref.set({
		u'date':dateTime,
		u'CURRMWT':MWT,
		u'CURRBATT':BATT,
		u'VBATT':VBATT,
        u'VAVG' : VAVG,
        u'WAVG' : WAVG,
        u'DCLOAD': DCLOAD,
        u'DCPOWER': dcloadPower
		})

#Initiate database connection and determine which serial to read
def main():
    conn = create_connection(CONST_DATABASE)
    if is_arduino_connected():
        print('reading arduino')
        create_reading_sqlite3(conn, database)
        create_reading_firestore()         
    elif is_xbee_connected():
        print('reading xbee')
        create_reading_sqlite3(conn, database)
        create_reading_firestore()
        
    else:
        print('Waiting connection')
    

#If this module runs as main then loop indefinetly
if __name__ == '__main__':
	while True:
		main()

