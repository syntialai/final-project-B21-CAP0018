import tensorflow as tf
import numpy as np
import cv2 as cv
import os
import urllib.request
from preprocess import preprocess_normalize, preprocess_standardize
from flask import Flask, request, jsonify
from optical_character_recognition import *

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

# Google Cloud Vision and AutoML credentials
os.environ["GOOGLE_APPLICATION_CREDENTIALS"]="q-hope-dd17dc6b88bb.json"
os.environ["PROJECT_ID"]="613609637569"
pathJson = 'q-hope-dd17dc6b88bb.json'

# test API (hello world)
@app.route("/")
def test() :
    return 'hello world!'

# register a new user
@app.route("/register", methods = ['POST'])
def register() :
    '''
    Receives : a 'POST' request containing 2 images (KTP and user selfie)
    and processes the inputs as follows :
    1. Detect faces in ktp and selfie
    2. Get face embedding from the faces in ktp and selfie, and compares the similarity between the two
    Returns : json response of the following format :
    {'face_verification' : 'REJECTED' or 'VERIFIED', 'ktp_data' : {extracted data from ktp}}
    '''
    # Get KTP and selfie files
    ktp_file = request.files['ktp'].read()
    selfie_file = request.files['selfie'].read()
    response = {}

    # Try to detect face in photo
    try :
        ktp_standard = preprocess_standardize(ktp_file)
        selfie_standard = preprocess_standardize(selfie_file)
        selfie_normalize = preprocess_normalize(selfie_file)
    except :
        print('no face detected')
        # face verificatino rejected since no face is detected
        response['face_verification'] = 'REJECTED'

    # Check if face is classified as real/fake
    real_fake = xception.predict(selfie_normalize)[0]
    if real_fake < 0.4 : # Face classified as fake ~ rejected
        print('Face classified as fake')
        response['face_verification'] = 'REJECTED'
        # return 'Face classified as fake'
    else :
        # response['face_verification'] = 'VERIFIED'
        print('Face classified as real')

    # Get face embedding for images in KTP and selfie
    ktp_face = facenet.predict(ktp_standard)[0]
    selfie_face = facenet.predict(selfie_standard)[0]
    cos_sim = np.dot(ktp_face,selfie_face)
    print('cos_sim = {}'.format(cos_sim))

    # Determine if faces match
    cos_sim_threshold = 0.55
    if cos_sim > cos_sim_threshold :
        ## EDIT THIS AFTER OCR IS DONE

        # face is verified to be real and similar
        response['face_verification'] = 'VERIFIED'

    else :
        # Update verification status
        response['face_verification'] = 'REJECTED'

    # Extract KTP data
    ktp_df = get_extract(ktp_file, pathJson)
    ktp_dict = ktp_df.to_dict('records')[0]
    response['ktp_data'] = ktp_dict

    print(response)

    return jsonify(response),200




if __name__ == '__main__':
    app.run(host='0.0.0.0', port=80)
