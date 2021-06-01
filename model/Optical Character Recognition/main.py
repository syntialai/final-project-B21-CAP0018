from flask import Flask, request
from firebase_admin import credentials, firestore, initialize_app
from optical_character_recognition import url_to_image, preprocessing_image, extract_text, add_data

@app.route("/register", methods = ['POST'])
def register() :
    # read json request
    data = request.json

    # extract data from json
    userId = data['userId']
    ktp_url = data['ktp_url']
    selfie_url = data['selfie_url']

    # upload image
    img = url_to_image(ktp_url)

    # Extract text and store to firestore
    image = preprocessing_image(img)
    teks = extract_text(pytesseract.image_to_string(image, lang="ind"))
    data_teks = teks.to_dict('records')

@app.route("/test_firebase", methods = ['POST'])
def firebase_test():
    request_json = request.json
    # print(request_json.get('selfie_url'))
    userId = request_json.get('userId')
    db.collection(u'users').document(userId).set(data_teks[0], merge=True)
    return 'test'

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)