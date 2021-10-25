import firebase_admin
import os
from flask import Flask, request, jsonify
from firebase_admin import credentials, firestore, initialize_app
app = Flask(__name__)
cred = credentials.Certificate("./test-key.json")
default_app = initialize_app(cred)
db = firestore.client()  # this connects to our Firestore database
collection = db.collection('hospital_data/1000030/room_data')  # opens 'places' collection
doc = collection.document('1000030_business')  # specifies the 'rome' document
@app.route('/')
def test():
    return "hello world"
    
@app.route('/hospital_data/1000030/room_data', methods=['POST'])
def create():
    """
        create() : Add document to Firestore collection with request body
        Ensure you pass a custom ID as part of json body in post request
        e.g. json={'id': '1', 'title': 'Write a blog post'}
    """
    try:
        available_room = request.json['available_room']
        collection.document("1000030_business").set(request.json)
        return jsonify({"success": True}), 200
    except Exception as e:
        return f"An Error Occured: {e}"
@app.route('/hospital_data/1000030/room_data', methods=['PUT'])
def update():
    """
        update() : Update document in Firestore collection with request body
        Ensure you pass a custom ID as part of json body in post request
        e.g. json={'id': '1', 'title': 'Write a blog post today'}
    """
    try:
        available_room = request.json['available_room']
        collection.document("1000030_economy").update(request.json)
        return jsonify({"success": True}), 200
    except Exception as e:
        return f"An Error Occured: {e}"
# port = int(os.environ.get('PORT', 80))
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=6543)
