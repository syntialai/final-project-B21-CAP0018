import enum
import jwt
import werkzeug
import os
import midtransclient
import requests
from datetime import datetime
from functools import wraps
from firebase_admin import credentials, firestore, initialize_app, auth, storage
from flask import Flask, request, jsonify
from marshmallow import Schema, fields, ValidationError, EXCLUDE, validates_schema, validate
from werkzeug.exceptions import HTTPException
from flask_cors import CORS

app = Flask(__name__)
CORS(app)
app.config['SECRET_KEY'] = 'cdf1791e499190767ec7267f2a1b1f8e'
app.config['BUCKET_NAME'] = 'q-hope.appspot.com'
app.config['IS_PRODUCTION'] = False
cred = credentials.Certificate('q-hope-cred.json')
default_app = initialize_app(cred, {
    'storageBucket': app.config['BUCKET_NAME']
})
snap = midtransclient.Snap(
    is_production=app.config['IS_PRODUCTION'],
    server_key=''
)
db = firestore.client()
bucket = storage.bucket()

if app.config['IS_PRODUCTION']:
    COLLECTION_TRANSACTIONS = 'transactions'
    COLLECTION_PAYMENTS = 'payments'
    COLLECTION_HOSPITALS = 'hospitals'
    COLLECTION_HOSPITAL_ROOM = 'hospital_rooms'
    COLLECTION_USERS = 'users'
else:
    COLLECTION_TRANSACTIONS = 'transactions_test'
    COLLECTION_PAYMENTS = 'payments_test'
    COLLECTION_HOSPITALS = 'hospitals_test'
    COLLECTION_HOSPITAL_ROOM = 'hospital_rooms_test'
    COLLECTION_USERS = 'users_test'


class TransactionStatus(enum.Enum):
    ONGOING = 1
    CONFIRMED = 2
    FINISHED = 3
    CANCELED = 4

    @classmethod
    def has_key(cls, name):
        return name in cls.__members__


class VerificationStatus(enum.Enum):
    NOT_UPLOAD = 0
    UPLOADED = 1
    VERIFIED = 2
    REJECTED = 3
    ACCEPTED = 4

    @classmethod
    def has_key(cls, name):
        return name in cls.__members__


class BloodType(enum.Enum):
    A = 0
    B = 1
    AB = 2
    O = 3

    @classmethod
    def has_key(cls, name):
        return name in cls.__members__


class Gender(enum.Enum):
    MALE = 0
    FEMALE = 1

    @classmethod
    def has_key(cls, name):
        return name in cls.__members__


class Religion(enum.Enum):
    ISLAM = 0
    CHRISTIAN = 1
    CATHOLIC = 2
    HINDU = 3
    BUDDHA = 4
    CONFUCIANISM = 5
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


@app.errorhandler(Exception)
def handle_exception(error):
    return jsonify(
        message=error.__str__()
    ), 500

# ===================== End of Error handling ============================

# ============================ Auth API ==================================


def token_required(f):
    @wraps(f)
    def decorator(*args, **kwargs):
        token = None
        if 'x-access-token' in request.headers:
            token = request.headers['x-access-token']

        if not token:
            raise werkzeug.exceptions.Unauthorized('A valid token is missing.')
        try:
            decoded_token = auth.verify_id_token(token)
        except:
            raise werkzeug.exceptions.Unauthorized('Token is invalid.')

        return f(decoded_token['uid'], *args, **kwargs)

    return decorator


def hospital_token_required(f):
    @wraps(f)
    def decorator(*args, **kwargs):
        token = None
        if 'x-access-token' in request.headers:
            token = request.headers['x-access-token']

        if not token:
            raise werkzeug.exceptions.Unauthorized('A valid token is missing.')
        try:
            decoded_token = auth.verify_id_token(token)
            if decoded_token['role'] != 'HOSPITAL':
                raise Exception()
        except:
            raise werkzeug.exceptions.Unauthorized('Token is invalid.')
        return f(decoded_token['uid'], *args, **kwargs)

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
def get_all_hospitals(uid):
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


@app.route('/hospital', methods=['GET'])
@hospital_token_required
def get_hospital(hospital_id):
    hospital_doc = db.collection(COLLECTION_HOSPITALS).document(hospital_id).get()
    validate_data_exists(hospital_doc)
    hospital_response = hospital_doc.to_dict()
    return get_success_response(hospital_response)


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

    raise werkzeug.exceptions.NotFound()

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

@app.route('/user', methods=['GET'])
@token_required
def get_user(uid):
    user = db.collection(COLLECTION_USERS).document(uid).get()
    return jsonify(user.to_dict()), 200


class AddUserSchema(Schema):
    class Meta:
        unknown = EXCLUDE

    phone_number = fields.String(required=True)


@app.route('/user', methods=['POST'])
@token_required
def add_user(uid):
    request_data = request.json
    schema = AddUserSchema()
    try:
        result = schema.load(request_data)
        phone_number = result.get('phone_number')
        user = db.collection(COLLECTION_USERS).document(uid).get()
        if user.exists:
            return jsonify(user.to_dict()), 200
        new_user = {'phone_number': phone_number, 'verification_status': VerificationStatus.NOT_UPLOAD.name}
        db.collection(COLLECTION_USERS).document(uid).set(new_user)

        return jsonify(new_user), 200
    except ValidationError as err:
        raise werkzeug.exceptions.BadRequest(err.messages)


class UpdateUserSchema(Schema):
    class Meta:
        unknown = EXCLUDE

    photo = fields.Raw(type='file')
    name = fields.String()
    date_of_birth = fields.Integer()
    address = fields.String()


@app.route('/user', methods=['PATCH'])
@token_required
def update_user(uid):
    schema = UpdateUserSchema()
    file_schema = schema.load(request.files)
    request_data = schema.load(request.form)
    user_ref = db.collection(COLLECTION_USERS).document(uid)

    photo = file_schema.get('photo')
    name = request_data.get('name')
    date_of_birth = request_data.get('date_of_birth')
    address = request_data.get('address')
    user = {}
    check_user = user_ref.get().to_dict()
    if photo:
        if photo.content_type.split('/')[0] != 'image':
            raise werkzeug.exceptions.UnsupportedMediaType("Photo should be an image.")
        blob = bucket.blob(f'user_data/{uid}/{photo.filename}')
        blob.upload_from_file(photo)
        blob.make_public()
        user['photo_url'] = blob.public_url
    if address:
        user['address'] = address
    if not is_verified(check_user['verification_status']):
        if name:
            user['name'] = name
        if date_of_birth and not is_verified(check_user['verification_status']):
            user['date_of_birth'] = date_of_birth

    if user:
        user['updated_at'] = get_current_timestamp()
        user_ref.update(user)
    user = user_ref.get().to_dict()
    return jsonify(user), 200


class IdentityVerificationSchema(Schema):
    class Meta:
        unknown = EXCLUDE
    ktp = fields.Raw(type='file', required=True)
    selfie = fields.Raw(type='file', required=True)

@app.route('/user/identity-verification', methods=['POST'])
@token_required
def identity_verification(uid):
    schema = IdentityVerificationSchema()
    file_schema = schema.load(request.files)
    ktp = file_schema.get('ktp')
    selfie = file_schema.get('selfie')
    user_ref = db.collection(COLLECTION_USERS).document(uid)
    check_user = user_ref.get().to_dict()
    # print(uid, ktp, selfie)
    # print(check_user['verification_status'])
    if is_uploaded(check_user['verification_status']):
        # resource_string = context.resource
        # userId = resource_string.split("/")[-1]  # This is the userId
        # oldVerificationStatus = event["oldValue"]["fields"]["verification_status"]["stringValue"]
        # verificationStatus = event["value"]["fields"]["verification_status"]["stringValue"]  # This is the verification_status
        # ktpUrl = event["value"]["fields"]["ktp_url"]["stringValue"]
        # selfieUrl = event["value"]["fields"]["selfie_url"]["stringValue"]
        ktp_url = {"ktp": (ktp.filename, ktp.stream, ktp.mimetype)}
        selfie_url = {"selfie": (selfie.filename, selfie.stream, selfie.mimetype)}
        print(ktp_url, selfie_url)
        # r1 = requests.request("POST", url="http://34.101.241.255:4321/", files=ktp_url)
        # r2 = requests.request("POST", url="http://34.101.241.255:4321/", files=selfie_url)

        r1 = requests.post("http://34.101.241.255:4321/", ktp_url)
        r2 = requests.post("http://34.101.241.255:4321/", selfie_url)

        # payload = {'userId': uid, 'ktp':ktp, 'selfie':selfie}
        print(uid, ktp, selfie)
        # r = requests.request("POST", url="http://34.101.241.255:4321/register", json=payload)
        print(f"Changed by user: {uid} with status {'verification_status'}, data ktp : {ktp}, data selfie: {selfie}")

        raise werkzeug.exceptions.BadRequest("Your files has been uploaded.")
    if is_verified(check_user['verification_status']):
        raise werkzeug.exceptions.BadRequest("Your files has been verified.")
    if ktp.content_type.split('/')[0] != 'image':
        raise werkzeug.exceptions.BadRequest("KTP should be an image.")
    if selfie.content_type.split('/')[0] != 'image':
        raise werkzeug.exceptions.BadRequest("Selfie should be an image.")

    raise werkzeug.exceptions.BadRequest("Server currently can't process your request.")
    # user = {'verification_status': VerificationStatus.UPLOADED.name}
    # filename, ktp_file_extension = os.path.splitext(ktp.filename)
    # blob = bucket.blob(f'user_data/{uid}/ktp{ktp_file_extension}')
    # blob.upload_from_file(ktp)
    # blob.make_public()
    # user['ktp_url'] = blob.public_url
    # filename, selfie_file_extension = os.path.splitext(selfie.filename)
    # blob = bucket.blob(f'user_data/{uid}/selfie{selfie_file_extension}')
    # blob.upload_from_file(selfie)
    # blob.make_public()
    # user['selfie_url'] = blob.public_url


class IdentityConfirmationSchema(Schema):
    class Meta:
        unknown = EXCLUDE

    name = fields.String(required=True)
    date_of_birth = fields.Integer(required=True)
    ktp_address = fields.String(required=True)
    blood_type = fields.String(required=True)
    birth_place = fields.String(required=True)
    district = fields.String(required=True)
    village = fields.String(required=True)
    city = fields.String(required=True)
    neighborhood = fields.String(required=True)
    hamlet = fields.String(validate=validate.Length(3), required=True)
    gender = fields.String(validate=validate.Length(3), required=True)
    religion = fields.String(required=True)

    @validates_schema
    def validate_date_of_birth(self, data, **kwargs):
        try:
            datetime.fromtimestamp(data['date_of_birth'])
        except:
            raise ValidationError('Not a valid date.')


@app.route('/user/identity-confirmation', methods=['POST'])
@token_required
def identity_confirmation(uid):
    schema = IdentityConfirmationSchema()
    request_data = schema.load(request.json)

    user_ref = db.collection(COLLECTION_USERS).document(uid)
    check_user = user_ref.get().to_dict()

    name = request_data.get('name')
    date_of_birth = request_data.get('date_of_birth')
    ktp_address = request_data.get('ktp_address')
    blood_type = request_data.get('blood_type')
    birth_place = request_data.get('birth_place')
    district = request_data.get('district')
    village = request_data.get('village')
    city = request_data.get('city')
    neighborhood = request_data.get('neighborhood')
    hamlet = request_data.get('hamlet')
    gender = request_data.get('gender')
    religion = request_data.get('religion')

    if not is_accepted(check_user['verification_status']):
        raise werkzeug.exceptions.BadRequest('Your ktp and selfie is not valid.')
    if is_verified(check_user['verification_status']):
        raise werkzeug.exceptions.BadRequest('Couldn\'t change this information.')
    if not BloodType.has_key(blood_type):
        raise werkzeug.exceptions.BadRequest('Invalid blood type.')
    if not Gender.has_key(gender):
        raise werkzeug.exceptions.BadRequest('Invalid gender.')
    if not Religion.has_key(religion):
        raise werkzeug.exceptions.BadRequest('Invalid religion.')

    to_be_update = {
        'name': name,
        'date_of_birth': date_of_birth,
        'ktp_address': ktp_address,
        'blood_type': blood_type,
        'gender': gender,
        'birth_place': birth_place,
        'district': district,
        'village': village,
        'city': city,
        'hamlet': hamlet,
        'religion': religion,
        'neighborhood': neighborhood,
        'verification_status': VerificationStatus.VERIFIED.name,
        'updated_at': get_current_timestamp()
    }

    user_ref.update(to_be_update)
    user = user_ref.get().to_dict()
    return jsonify(user), 200


def is_accepted(verification_status):
    return verification_status == VerificationStatus.ACCEPTED.name


def is_verified(verification_status):
    return verification_status == VerificationStatus.VERIFIED.name


def is_uploaded(verification_status):
    return verification_status == VerificationStatus.UPLOADED.name


# ========================= End of User API ==============================


# port = int(os.environ.get('PORT', 80))
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=6543, debug=(not app.config['IS_PRODUCTION']))
