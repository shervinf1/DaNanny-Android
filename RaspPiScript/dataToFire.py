import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore

# Use a service account
cred = credentials.Certificate('/home/jalex/Documents/FirestoreTest/weekresearch-firebase-adminsdk-ttdtr-858005c763.json')
firebase_admin.initialize_app(cred)

db = firestore.client()

#Adding Data
doc_ref = db.collection(u'users').document(u'alace')
doc_ref.set({
    u'first': u'Ad',
    u'last': u'lace',
    u'born': 1005
})
 
users_ref = db.collection(u'users')
docs = users_ref.get()

for doc in docs:
    print(u'{} => {}'.format(doc.id, doc.to_dict()))
 
