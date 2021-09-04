import tensorflow as tf
import numpy as np
import cv2 as cv
import os
import urllib.request
import pytesseract
from preprocess import preprocess_normalize, preprocess_standardize
from flask import Flask, request
from firebase_admin import credentials, firestore, initialize_app
from optical_character_recognition import url_to_image, preprocessing_image, extract_text

## directories for models
# yolov3 for face detection
yolov3_cfg_dir = './yolov3_face/yolov3.cfg'
yolov3_weights_dir = './yolov3_face/yolov3.weights'
# facenet for face embedding
facenet_savedmodel_dir = './keras-facenet-v2/facenet_savedmodel'
# xception for fake/real face classification
xception_model = './xception1/xception27052021_input160160.hdf5'
xception_weights = './xception1/02-0.93.hdf5'

## make app
app = Flask(__name__)

## load models
# facenet
facenet = tf.keras.models.load_model(facenet_savedmodel_dir)
# xception
xception = tf.keras.models.load_model(xception_model)
xception.load_weights(xception_weights)

# firebase
cred = credentials.Certificate('./firebase_credentials/q-hope-3b34d8a7bf60.json')
initialize_app(cred)
db = firestore.client()

# test API (hello world)
@app.route("/")
def test() :
    return 'hello world!'

# register a new user
@app.route("/register", methods = ['POST'])
def register() :
    '''
    Receives a json 'POST' request with the following format :
    {'userId' : userId_value, 'ktp_url' : ktp_photo_url, 'selfie_url' : selfie_url}
    and processes the inputs as follows :
    1. Detect faces in ktp and selfie
    2. Get face embedding from the faces in ktp and selfie, and compares the similarity between the two
    3. Updates the database for the user.
    '''
    # read json request
    data = request.json

    # extract data from json
    userId = data['userId']
    ktp_url = data['ktp_url']
    selfie_url = data['selfie_url']
    ktp_filename, _ = urllib.request.urlretrieve(ktp_url)
    selfie_filename,_ = urllib.request.urlretrieve(selfie_url)

    # Try to detect face in photo
    try :
        ktp_standard = preprocess_standardize(ktp_filename)
        selfie_standard = preprocess_standardize(selfie_filename)
        selfie_normalize = preprocess_normalize(selfie_filename)
    except :
        print('no face detected')
        # Return something to indicate that no face is detected
        db.collection(u'users').document(userId).set({'verification_status' : 'REJECTED'})
        return 'Failed, no face detected'

    # Check if face is classified as real/fake
    real_fake = xception.predict(selfie_normalize)[0]
    if real_fake < 0.4 :
        print('Face classified as fake')
        db.collection(u'users').document(userId).set({'verification_status' : 'REJECTED'})
        return 'Face classified as fake'
    else :
        print('Face classified as real')

    # Get face embedding for images in KTP and selfie
    ktp_face = facenet.predict(ktp_standard)[0]
    selfie_face = facenet.predict(selfie_standard)[0]
    cos_sim = np.dot(ktp_face,selfie_face)
    print('cos_sim = {}'.format(cos_sim))

    # Determine if faces match
    cos_sim_threshold = 0.55
    if cos_sim > cos_sim_threshold :
        # Read KTP data
        ktp_img = url_to_image(ktp_url)

        # Extract text and store to firebase
        ktp_img = preprocessing_image(ktp_img) # This will sometimes return None as its items value.
        # pytesseract.pytesseract.tesseract_cmd = r'C:\Program Files\Tesseract-OCR\tesseract.exe' # for tesseract (on Windows only)
        teks = extract_text(pytesseract.image_to_string(ktp_img,lang="ind"))
        data = teks.to_dict('records')[0]
        print(f'dict : {data}')
        # Kalau ada None di dictnya, nanti diapakan, harus diproses lebih lanjut oleh user ya?
        for key in data.keys():
            if (data[key] is None) or (data[key] == '') :
                data.pop(key)

        # Process face embedding for identification
        face_embedding = list(selfie_face.astype('float'))
        data['face_embedding'] = face_embedding


        # update user data in database
        db.collection(u'users').document(userId).set(data, merge=True)

        # Update verification status
        data['verification_status'] = 'VERIFIED'
        db.collection(u'users').document(userId).set(data, merge = True)

        return "Successfully register" # Apa yang harus direturn ke yang minta request di awal? (Jika fotonya sesuai)
    else :
        # Update verification status
        db.collection(u'users').document(userId).set({'verification_status' : 'REJECTED'})
        return "Faces don't match" # Apa yang harus direturn ke yang minta request di awal? (Jika foto tidak sesuai)


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=80)
