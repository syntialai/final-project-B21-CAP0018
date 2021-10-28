import werkzeug
from firebase_admin import credentials, firestore, initialize_app
from flask import Flask, request, jsonify
from werkzeug.exceptions import HTTPException
from marshmallow import Schema, fields, ValidationError
from functools import wraps
import jwt
from datetime import datetime, timedelta
import os

app = Flask(__name__)
app.config['SECRET_KEY'] = 'cdf1791e499190767ec7267f2a1b1f8e'
cred = credentials.Certificate('q-hope-cred.json')
default_app = initialize_app(cred)
db = firestore.client()  # this connects to our Firestore database
collection = db.collection('hospital_data/1000030/room_data')  # opens 'places' collection
doc = collection.document('1000030_business')  # specifies the 'rome' document

COLLECTION_TRANSACTIONS = 'transaction'
COLLECTION_HOSPITALS = 'hospitals'


def getSuccessResponse(response):
    return jsonify(response), 200


def mapToDictionary(documents):
    mappedDocuments = []
    for document in documents:
        mappedDocuments.append(document.to_dict())
    return mappedDocuments


# ===================== Error handling ============================
@app.errorhandler(werkzeug.exceptions.BadRequest)
def handle_bad_request(error):
    return jsonify(
        message=error.description
    ), error.code


@app.errorhandler(werkzeug.exceptions.Unauthorized)
def handle_unauthorized(error):
    return jsonify(
        message=error.description
    ), error.code


@app.errorhandler(werkzeug.exceptions.NotFound)
def handle_not_found(error):
    return jsonify(
        message=error.description
    ), error.code


# ===================== End of Error handling ============================

@app.route('/hospitals', methods=['GET'])
def get_all_hospitals():
    all_hospitals = db.collection(u'hospital_data')

    docs = all_hospitals.stream()

    for doc in docs:
        # hospitals id need to be included in field
        name = u'{}'.format(doc.to_dict()['nama_rumah_sakit'])
        print(name)
        type = u'{}'.format(doc.to_dict()['jenis_rumah_sakit'])
        print(type)
        image = u'{}'.format(doc.to_dict()['foto_rumah_sakit'])
        print(image)
        address = u'{}'.format(doc.to_dict()['alamat_rumah_sakit'])
        print(address)
        description = u'{}'.format(docs.to_dict()['alamat_str'])
        print(description)
        available_room_count = u'{}'.format(doc.to_dict()['total_kamar_kosong'])
        print(available_room_count)
        print("===")

    raise werkzeug.exceptions.BadRequest()


@app.route('/hospitals/<id>', methods=['GET'])
def get_hospitals_by_id(id):
    hospital = db.collection(u'hospital_data').document(id).get()
    room_types = db.collection(u'hospital_room').document(id).get()

    name = u'{}'.format(hospital.to_dict()['nama_rumah_sakit'])
    print(name)
    email = u'{}'.format(hospital.to_dict()['email'])
    print(email)
    type = u'{}'.format(hospital.to_dict()['jenis_rumah_sakit'])
    print(type)
    image = u'{}'.format(hospital.to_dict()['foto_rumah_sakit'])
    print(image)
    telephone = u'{}'.format(hospital.to_dict()['nomor_telepon'])
    print(telephone)
    website = u'{}'.format(hospital.to_dict()['website'])
    print(website)
    address = u'{}'.format(hospital.to_dict()['alamat_rumah_sakit'])
    print(address)
    description = u'{}'.format(hospital.to_dict()['alamat_str'])
    print(description)
    state = u'{}'.format(hospital.to_dict()['provinsi'])
    print(state)
    district = u'{}'.format(hospital.to_dict()['kecamatan'])
    print(district)
    city = u'{}'.format(hospital.to_dict()['kota_administrasi'])
    print(city)
    village = u'{}'.format(hospital.to_dict()['kelurahan'])
    print(village)
    postal_code = u'{}'.format(hospital.to_dict()['kode_pos'])
    print(postal_code)
    print("===")
    type = u'{}'.format(room_types.to_dict()['type'])
    print(type)
    price = u'{}'.format(room_types.to_dict()['price'])
    print(price)
    available_room_count = u'{}'.format(room_types.to_dict()['available_room_count'])
    print(available_room_count)
    total_room = u'{}'.format(room_types.to_dict()['total_room'])
    print(total_room)

    raise werkzeug.exceptions.BadRequest()
    # return getSuccessResponse(hospital.to_dict())


@app.route('/transactions', methods=['GET'])
def getAllTransactions():
    userId = request.args.get('user_id')
    hospitalId = request.args.get('hospital_id')

    if userId is not None:
        transactionsDoc = db.collection(COLLECTION_TRANSACTIONS).where('user_id', '==', userId).stream()
        result = mapToDictionary(transactionsDoc)
        return getSuccessResponse(result)

    if hospitalId is not None:
        transactionsDoc = db.collection(COLLECTION_TRANSACTIONS).where('hospital_id', '==', hospitalId).stream()
        result = mapToDictionary(transactionsDoc)
        return getSuccessResponse(result)

    raise werkzeug.exceptions.BadRequest()


@app.route('/transactions/<id>', methods=['GET'])
def getTransactionsById(id):
    transactionDoc = db.collection(COLLECTION_TRANSACTIONS).document(id).get()

    if not transactionDoc.exists:
        raise werkzeug.exceptions.NotFound()

    return getSuccessResponse(transactionDoc.to_dict())


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


@app.route('/hospitals/<id>/generate-token', methods=['POST'])
def generate_hospital_token(id):
    hospital_ref = db.collection('hospitals').document(id)
    hospital = hospital_ref.get()
    if hospital.exists:
        hospital_data = hospital.to_dict()

        if 'token' in hospital_data:
            db.collection('blacklisted_token').document(hospital_data['token']).set({})

        iat = int(datetime.now().timestamp())
        encoded_jwt = jwt.encode({
            'hospital_id': id,
            'iat': iat
        }, app.config['SECRET_KEY'])
        token = encoded_jwt.decode('UTF-8')
        hospital_ref.update({'token': token})

        return jsonify(token=token, iat=iat), 200

    raise werkzeug.exceptions.NotFound()


def token_required(f):
    @wraps(f)
    def decorator(*args, **kwargs):
        token = None
        if 'x-access-token' in request.headers:
            token = request.headers['x-access-token']

        if not token:
            return jsonify({'message': 'a valid token is missing'}), 401
        try:
            current_user = jwt.decode(token, app.config['SECRET_KEY'], algorithms=["HS256"])
        except:
            return jsonify({'message': 'token is invalid'}), 401

        return f(current_user['user_id'], *args, **kwargs)

    return decorator


def hospital_token_required(f):
    @wraps(f)
    def decorator(*args, **kwargs):
        token = None
        if 'x-access-token' in request.headers:
            token = request.headers['x-access-token']

        if not token:
            raise werkzeug.exceptions.NotFound('A valid token is missing.')

        blacklisted = db.collection('blacklisted_token').document(token).get()
        if blacklisted.exists:
            raise werkzeug.exceptions.NotFound('Token is invalid.')

        try:
            current_user = jwt.decode(token, app.config['SECRET_KEY'], algorithms=["HS256"])
        except:
            raise werkzeug.exceptions.NotFound('Token is invalid.')

        return f(current_user['hospital_id'], *args, **kwargs)

    return decorator


class LoginSchema(Schema):
    phone_number = fields.String(required=True)


@app.route('/login', methods=['POST'])
def login():
    request_data = request.json
    schema = LoginSchema()
    try:
        result = schema.load(request_data)
        phone_number = result.get('phone_number')
        docs = db.collection('users').where('phone_number', '==', phone_number).limit(1).stream()
        user_id = None
        for document in docs:
            user_id = document.id

        if user_id is None:
            doc_ref = db.collection('users').document()
            doc_ref.set({'phone_number': phone_number, 'verification_status': False})
            user_id = doc_ref.id

        now = datetime.now()
        exp = now + timedelta(minutes=30)
        token = jwt.encode({
            'user_id': user_id,
            'exp': exp
        }, app.config['SECRET_KEY'])

        return jsonify({
            'token': token.decode('UTF-8'),
            'exp': int(exp.timestamp()),
            'iat': int(now.timestamp())
        }), 201
    except ValidationError as err:
        return jsonify(err.messages), 400


@app.route('/trylah', methods=['GET'])
@hospital_token_required
def trylah(user_id):
    return jsonify({"Success": user_id}), 200


# port = int(os.environ.get('PORT', 80))
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=6543, debug=True)
