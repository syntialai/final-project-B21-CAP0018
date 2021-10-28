import enum
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
cred = credentials.Certificate('D:/BANGKIT/Capstone Project/final-project-B21-CAP0018/api/cloud-api/q-hope-cred.json')
default_app = initialize_app(cred)
db = firestore.client()

COLLECTION_TRANSACTIONS = 'transaction'
COLLECTION_HOSPITALS = 'hospital_data'
COLLECTION_HOSPITAL_ROOM = 'hospital_room'
COLLECTION_USERS = 'users'


class TransactionStatus(enum.Enum):
    ONGOING = 1
    CONFIRMED = 2
    FINISHED = 3
    CANCELED = 4

    @classmethod
    def hasKey(cls, name):
        return name in cls.__members__


def getSuccessResponse(response):
    return jsonify(response), 200


def getSuccessCreateResponse(response):
    return jsonify(response), 201


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


@app.errorhandler(ValidationError)
def handle_validation_error(error):
    return jsonify(
        message=error.messages
    ), werkzeug.exceptions.BadRequest.code


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


@app.errorhandler(Exception)
def handle_exception(error):
    return jsonify(
        message=error.__str__()
    ), 500


def validateDataExists(document):
    if not document.exists:
        raise werkzeug.exceptions.NotFound()


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


# ======================== Transactions API ==============================

class CreateTransactionSchema(Schema):
    hospital_id = fields.String(required=True)
    user_id = fields.String(required=True)
    room_type_id = fields.String(required=True)
    referral_letter_url = fields.URL(required=True)
    referral_letter_name = fields.String(required=True)
    payment_type = fields.String(required=True)


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


@app.route('/transactions', methods=['POST'])
def createTransaction():
    createTransactionSchema = CreateTransactionSchema()
    transactionRequest = createTransactionSchema.load(request.json)

    hospitalId = transactionRequest.get('hospital_id')
    userId = transactionRequest.get('user_id')
    roomTypeId = transactionRequest.get('room_type_id')

    hospitalDoc = db.collection(COLLECTION_HOSPITALS).document(hospitalId).get()
    userDoc = db.collection(COLLECTION_USERS).document(userId).get()
    hospitalRoomDoc = db.collection(COLLECTION_HOSPITAL_ROOM).document(roomTypeId).get()
    transactionDoc = db.collection(COLLECTION_TRANSACTIONS)

    validateDataExists(hospitalDoc)
    validateDataExists(userDoc)
    validateDataExists(hospitalRoomDoc)

    hospital = hospitalDoc.to_dict()
    user = userDoc.to_dict()
    hospitalRoom = hospitalRoomDoc.to_dict()
    transactionDoc.add({
        'hospital': {
           'id': hospitalId,
           'name': hospital['name'],
           'image': hospital['image'],
           'address': hospital['address']['description']
        },
        'user': {
            'id': userId,
            'name': user['name'],
            'birthDate': user['birthDate'],
            'email': user['email'],
            'nik': user['nik']
        },
        'total': hospitalRoom['price'],
        'createdAt': int(datetime.now().timestamp()),
        'updatedAt': int(datetime.now().timestamp()),
        'status': TransactionStatus.ONGOING.name,
        'payment': transactionRequest.get('payment_type'),
        'referralLetter': {
            'name': transactionRequest.get('referral_letter_name'),
            'url': transactionRequest.get('referral_letter_url'),
        }
    })
    return getSuccessCreateResponse({})


@app.route('/transactions/<id>', methods=['GET'])
def getTransactionsById(id):
    transactionDoc = db.collection(COLLECTION_TRANSACTIONS).document(id).get()
    validateDataExists(transactionDoc)
    return getSuccessResponse(transactionDoc.to_dict())


@app.route('/transactions/<id>', methods=['PUT'])
def updateTransactionStatusById(id):
    status = request.args.get('status').upper()
    if status is None:
        raise werkzeug.exceptions.NotFound()

    if not TransactionStatus.hasKey(status):
        raise werkzeug.exceptions.BadRequest()

    transactionDoc = db.collection(COLLECTION_TRANSACTIONS).document(id)
    transactionValue = transactionDoc.get()

    validateDataExists(transactionValue)

    statusRequest = {
        'status': status
    }
    transactionDoc.update(statusRequest)
    return getSuccessResponse({})


# ====================== End of Transactions API =========================


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
