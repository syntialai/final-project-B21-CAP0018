import enum
from datetime import datetime
from functools import wraps

import jwt
import werkzeug
from firebase_admin import credentials, firestore, initialize_app, auth, storage
from flask import Flask, request, jsonify
from marshmallow import Schema, fields, ValidationError
from werkzeug.exceptions import HTTPException

app = Flask(__name__)
app.config['SECRET_KEY'] = 'cdf1791e499190767ec7267f2a1b1f8e'
app.config['BUCKET_NAME'] = 'q-hope.appspot.com'
cred = credentials.Certificate('D:/BANGKIT/Capstone Project/final-project-B21-CAP0018/api/cloud-api/q-hope-cred.json')
default_app = initialize_app(cred, {
    'storageBucket': app.config['BUCKET_NAME']
})
db = firestore.client()
bucket = storage.bucket(app.config['BUCKET_NAME'])

COLLECTION_TRANSACTIONS = 'transactions'
COLLECTION_PAYMENTS = 'payments'
COLLECTION_HOSPITALS = 'hospitals'
COLLECTION_HOSPITAL_ROOM = 'hospital_rooms'
COLLECTION_USERS = 'users'


class TransactionStatus(enum.Enum):
    ONGOING = 1
    CONFIRMED = 2
    FINISHED = 3
    CANCELED = 4

    @classmethod
    def has_key(cls, name):
        return name in cls.__members__


def get_success_response(response):
    return jsonify(response), 200


def get_success_create_response(response):
    return jsonify(response), 201


def get_current_timestamp():
    return int(datetime.now().timestamp())


def map_to_dictionaries(documents):
    mapped_documents = []
    for document in documents:
        mapped_documents.append(document.to_dict())
    return mapped_documents


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


def validate_data_exists(document):
    if not document.exists:
        raise werkzeug.exceptions.NotFound('Data not found')


def validate_data_not_none(data):
    if data is None:
        raise werkzeug.exceptions.BadRequest('Request param data can\'t be none')


# ===================== End of Error handling ============================


# ============================ Auth API ==================================

@app.route('/hospital/<id>/generate-token', methods=['POST'])
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
            raise werkzeug.exceptions.NotFound('A valid token is missing.')
        try:
            decoded_token = auth.verify_id_token(token)
        except:
            raise werkzeug.exceptions.NotFound('Token is invalid.')

        return f(decoded_token['uid'], *args, **kwargs)

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

# ======================== End of Auth API ===============================


# ========================== Hospital API ================================

class AddHospitalAddressGeopointSchema(Schema):
    lat = fields.String(required=True)
    lng = fields.String(required=True)


class AddHospitalAddressSchema(Schema):
    name = fields.String(required=True)
    geopoint = fields.Dict(keys=fields.Str(), values=fields.Nested(AddHospitalAddressGeopointSchema()))
    description = fields.String(required=True)
    state = fields.String(required=True)
    district = fields.String(required=True)
    city = fields.String(required=True)
    village = fields.String(required=True)
    postalCode = fields.String(required=True)


class AddHospitalSchema(Schema):
    name = fields.String(required=True)
    email = fields.Email(required=True)
    type = fields.String()
    image = fields.URL(required=True)
    telephone = fields.String(required=True)
    website = fields.URL()
    address = fields.Dict(keys=fields.Str(), values=fields.Nested(AddHospitalAddressSchema()))


@app.route('/hospitals', methods=['GET'])
@token_required
def get_all_hospitals():
    hospital_docs = db.collection(COLLECTION_HOSPITALS).stream()

    hospital_responses = []
    for hospital_doc in hospital_docs:
        hospital = hospital_doc.to_dict()
        hospital_response = {
            'id': hospital['id'],
            'name': hospital['name'],
            'type': hospital['type'],
            'image': hospital['image'],
            'address': {
                'geopoint': hospital['address']['geopoint'],
                'description': hospital['address']['description']
            },
            'available_room_count': hospital['available_room_count']
        }
        hospital_responses.append(hospital_response)

    return get_success_response(hospital_responses)


@app.route('/hospitals/<id>', methods=['GET'])
@token_required
def get_hospital_by_id(id):
    hospital_doc = db.collection(COLLECTION_HOSPITALS).document(id).get()
    room_type_docs = db.collection(COLLECTION_HOSPITAL_ROOM).where('hospital_id', '==', id).stream()

    validate_data_exists(hospital_doc)

    hospital_response = hospital_doc.to_dict()
    hospital_response['room_types'] = map_to_dictionaries(room_type_docs)

    return get_success_response(hospital_response)


@app.route('/hospitals', methods=['POST'])
@token_required
def add_hospital():
    add_hospital_schema = AddHospitalSchema()
    hospital_request = add_hospital_schema.load(request.json)

    hospital_doc = db.collection(COLLECTION_HOSPITALS).document()
    hospital_doc.set({
        'name': hospital_request['name'],
        'image': hospital_request['image'],
        'address': hospital_request['address'],
        'type': hospital_request['type'],
        'telephone': hospital_request['telephone'],
        'website': hospital_request['website'],
        'email': hospital_request['email'],
        'available_room_count': 0,
        'created_at': get_current_timestamp(),
        'updated_at': get_current_timestamp()
    })

    return get_success_create_response({
        'id': hospital_doc.id
    })

# ====================== End of Hospital API =============================


# ======================== Transactions API ==============================

class CreateTransactionSchema(Schema):
    hospital_id = fields.String(required=True)
    room_type_id = fields.String(required=True)
    referral_letter_url = fields.URL(required=True)
    referral_letter_name = fields.String(required=True)
    payment_type = fields.String(required=True)


class UpdateTransactionStatusSchema(Schema):
    status = fields.String(required=True)


@app.route('/transactions', methods=['GET'])
@token_required
def get_user_transactions(user_id):
    transactions_doc = db.collection(COLLECTION_TRANSACTIONS).where('user_id', '==', user_id).stream()
    user_transaction_responses = map_to_dictionaries(transactions_doc)
    return get_success_response(user_transaction_responses)


@app.route('/hospital/transactions', methods=['GET'])
@hospital_token_required
def get_hospital_transactions(hospital_id):
    transactions_doc = db.collection(COLLECTION_TRANSACTIONS).where('hospital_id', '==', hospital_id).stream()
    hospital_transaction_responses = map_to_dictionaries(transactions_doc)
    return get_success_response(hospital_transaction_responses)


@app.route('/transactions', methods=['POST'])
@token_required
def create_transaction(user_id):
    create_transaction_schema = CreateTransactionSchema()
    transaction_request = create_transaction_schema.load(request.json)

    hospital_id = transaction_request.get('hospital_id')
    room_type_id = transaction_request.get('room_type_id')
    payment_type = transaction_request.get('payment_type')

    hospital_doc = db.collection(COLLECTION_HOSPITALS).document(hospital_id).get()
    user_doc = db.collection(COLLECTION_USERS).document(user_id).get()
    hospital_room_doc = db.collection(COLLECTION_HOSPITAL_ROOM).document(room_type_id).get()
    transaction_doc = db.collection(COLLECTION_TRANSACTIONS).document()
    payment_doc = db.collection(COLLECTION_PAYMENTS).document()

    validate_data_exists(hospital_doc)
    validate_data_exists(user_doc)
    validate_data_exists(hospital_room_doc)

    hospital = hospital_doc.to_dict()
    user = user_doc.to_dict()
    hospital_room = hospital_room_doc.to_dict()
    transaction_doc.set({
        'hospital': {
            'id': hospital_id,
            'name': hospital['name'],
            'image': hospital['image'],
            'address': hospital['address']['description']
        },
        'user': {
            'id': user_id,
            'name': user['name'],
            'birth_date': user['birth_date'],
            'email': user['email'],
            'nik': user['nik'],
            'phone_number': user['phone_number'],
            'address': user['address']
        },
        'total': hospital_room['price'],
        'created_at': get_current_timestamp(),
        'updated_at': get_current_timestamp(),
        'status': TransactionStatus.ONGOING.name,
        'referral_letter': {
            'name': transaction_request.get('referral_letter_name'),
            'url': transaction_request.get('referral_letter_url'),
        }
    })
    payment_doc.set({
        'name': payment_type,
        'paid_at': 0,
        'status': TransactionStatus.ONGOING,
        'total': hospital_room['price'],
        'transaction_id': transaction_doc.id
    })
    return get_success_create_response({
        'id': transaction_doc.id
    })


@app.route('/transactions/<id>', methods=['GET'])
def get_transaction_by_id(id):
    transaction_doc = db.collection(COLLECTION_TRANSACTIONS).document(id).get()
    payment_doc = db.collection(COLLECTION_PAYMENTS).where('transaction_id', '==', id)

    validate_data_exists(transaction_doc)
    validate_data_exists(payment_doc)

    transaction_response = transaction_doc.to_dict()
    transaction_response['payment'] = payment_doc.to_dict()

    return get_success_response(transaction_response)


@app.route('/transactions/<id>', methods=['PUT'])
def update_transaction_status_by_id(id):
    update_transaction_status_schema = UpdateTransactionStatusSchema()
    transaction_request = update_transaction_status_schema.load(request.json)
    status = transaction_request.get('status')

    if not TransactionStatus.has_key(status):
        raise werkzeug.exceptions.BadRequest()

    transaction_doc = db.collection(COLLECTION_TRANSACTIONS).document(id)
    transaction = transaction_doc.get()

    validate_data_exists(transaction)

    status_update = {
        'status': status
    }
    if status == TransactionStatus.FINISHED.name:
        status_update['updatedAt'] = get_current_timestamp()

    transaction_doc.update(status_update)
    return get_success_response(status_update)


# ====================== End of Transactions API =========================


# ========================== Upload File API =============================

@app.route('/upload/referral_letter', methods=['POST'])
@token_required
def upload_referral_letter_by_user_id(user_id):
    if 'file' not in request.files:
        raise werkzeug.exceptions.BadRequest('File is not exist!')

    file = request.files['file']
    blob = bucket.blob(f'referral_letters/{user_id}/{file.filename}')
    blob.upload_from_file(file)
    blob.make_public()

    return get_success_create_response({
        'url': blob.public_url
    })


@app.route('/upload/image', methods=['POST'])
@token_required
def upload_image_by_user_id(user_id):
    if 'image' not in request.files:
        raise werkzeug.exceptions.BadRequest('Image is not exist!')

    file = request.files['image']
    blob = bucket.blob(f'images/{user_id}/{file.filename}')
    blob.upload_from_file(file)
    blob.make_public()

    return get_success_create_response({
        'url': blob.public_url
    })

# ====================== End of Upload File API ==========================


# ============================ Payment API ===============================

class UpdatePaymentStatusSchema(Schema):
    status = fields.String(required=True)


@app.route('/payment', methods=['GET'])
def get_payment():
    '''
    Gets payment id and description from database.
    Returns a json containing payment info
    if request is successful
    '''
    payment_responses = []  # List to save payment info dictionaries
    payment_docs = db.collection(u'payment').stream()
    for doc in payment_docs:
        doc_dict = doc.to_dict()
        payment_dict = {
            'id': doc.id,
            'name': doc_dict['name'],
            'paid_at': datetime.datetime.fromtimestamp(doc_dict['paid_at'] / 1e3).strftime('%Y/%m/%d %H:%M:%S'),
            'total': doc_dict['total'],
            'transaction_id': doc_dict['transaction_id']
        }
        payment_responses.append(payment_dict)
    return get_success_response(payment_responses)


@app.route('/payment/<id>', methods=['GET'])
def get_payment_by_id(id):
    '''
    Gets payment by id from request route
    '''
    payment_collection = db.collection(COLLECTION_PAYMENTS)
    payment_doc = payment_collection.document(id).get()

    validate_data_exists(payment_doc)

    payment = payment_doc.to_dict()
    payment_response = {
        'id': id,
        'name': payment['name'],
        'paid_at': datetime.datetime.fromtimestamp(payment['paidAt'] / 1e3).strftime('%Y/%m/%d %H:%M:%S'),
        'total': payment['total'],
        'transaction_id': payment['transactionId']
    }
    return get_success_response(payment_response)


@app.route('/payment/<id>', methods=['PUT'])
def update_payment_status_by_id(id):
    update_payment_status_schema = UpdatePaymentStatusSchema()
    payment_request = update_payment_status_schema.load(request.json)
    status = payment_request.get('status')

    if not TransactionStatus.has_key(status):
        raise werkzeug.exceptions.BadRequest()

    payment_doc = db.collection(COLLECTION_TRANSACTIONS).document(id)
    payment = payment_doc.get()

    validate_data_exists(payment)

    status_update = {
        'status': status
    }
    if status == TransactionStatus.FINISHED.name:
        status_update['paidAt'] = get_current_timestamp()

    payment_doc.update(status_update)
    return get_success_response(status_update)

# ======================== End of Payment API ============================


# ============================= User API =================================

class AddUserSchema(Schema):
    phone_number = fields.String(required=True)


@app.route('/user', methods=['POST'])
@token_required
def addUser(uid):
    request_data = request.json
    schema = AddUserSchema()
    try:
        result = schema.load(request_data)
        phone_number = result.get('phone_number')
        user = db.collection('users').document(uid).get()
        if user.exists:
            raise werkzeug.exceptions.BadRequest("User already exists.")
        newUser = {'phone_number': phone_number, 'verification_status': False}
        db.collection('users').document(uid).set(newUser)

        return jsonify(newUser), 200
    except ValidationError as err:
        raise werkzeug.exceptions.BadRequest(err.messages)

# ========================= End of User API ==============================


@app.route('/trylah', methods=['GET'])
@token_required
def trylah(user_id):
    return jsonify({"Success": user_id}), 200


# port = int(os.environ.get('PORT', 80))
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=6543, debug=True)
