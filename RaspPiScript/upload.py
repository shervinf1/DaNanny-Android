import serial
import datetime
import os
import json
import firebase_admin
import sqlite3
from sqlite3 import Error
from firebase_admin import credentials
from firebase_admin import firestore

#Change this to your database location
CONST_DATABASE = database = '/home/jalex/Documents/FirestoreTest/PWC7DaysTest.db'
#Change this to the sql for inserting a new record
CONST_SQL_SELECT = ''' SELECT TIMESTAMP, avg(CURRMWT), avg(CURRBATT),avg(DCLOAD),avg(CURRPV),avg(ACLOAD),avg(VAVG), avg(WAVG), avg(VBATT) FROM CURRENTDATA GROUP BY strftime('%Y-%m-%d %H', TIMESTAMP);  '''

# Use a service account for firestore connection. Modify the path where the json is stored 
cred = credentials.Certificate('/home/jalex/Documents/FirestoreTest/smart-iot-monitoring-system-firebase-adminsdk-2eh9a-0fb43ffd0e.json')
firebase_admin.initialize_app(cred)

db = firestore.client()

#Create connection to database file
def create_connection(db_file):
	try:
		conn = sqlite3.connect(db_file)
		print('Database Connected')
		return conn
	except Error as e:
		print(e)
	return None

def getData():
    conn = create_connection(database)
    cur = conn.cursor()
    cur.execute(CONST_SQL_SELECT)

    rows = cur.fetchall()

    for row in rows:
        print(len(row))
        create_reading_firestore(row)


def create_reading_firestore(row):
	#If sensorData array is another size than 4 data.
    print("Insert in Firestore")
	#Adding Data
    doc_ref = db.collection(u'values').document(u'User X').collection('test3').document()
    doc_ref.set({
	u'TIMESTAMP':row[0],
	u'CURRMWT':row[1],
	u'CURRBATT':row[2],
	u'VBATT':row[8],
    u'VAVG' : row[6],
    u'WAVG' : row[7],
    u'DCLOAD': row[3],
    u'DCPOWER': row[7],
    u'ACLOAD':row[5],
    u'CURRPV':row[4]})

getData()
