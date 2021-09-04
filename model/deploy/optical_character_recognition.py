import cv2
import pandas as pd
import re
import pytesseract
try:
 from PIL import Image
except ImportError:
 import Image
import urllib
import numpy as np
import time

"""# Open Image"""

def url_to_image(url):
	# download the image, convert it to a NumPy array, and then read
	# it into OpenCV format
	resp = urllib.request.urlopen(url)
	image = np.asarray(bytearray(resp.read()), dtype="uint8")
	image = cv2.imdecode(image, cv2.COLOR_RGB2BGR)
	# return the image
	return cv2.cvtColor(image , cv2.COLOR_BGR2RGB)

"""# Image Preprocessing"""

def resize(image):

  size = 1700, 2000
  im_resized = cv2.resize(image, (size))

  return im_resized

# get grayscale image
def get_grayscale(image):
    return cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

#thresholding
def thresholding(image):
    # threshold the image, setting all foreground pixels to
    # 255 and all background pixels to 127
    th, threshed = cv2.threshold(image, 127, 255, cv2.THRESH_TRUNC)
    return threshed

def preprocessing_image(img):
    gray = get_grayscale(img)

    threshed = thresholding(gray)

    image = threshed.copy()

    return image

"""# Extract Text"""

def extract_text(extract):
  keys = ['no_ktp', 'name', 'place_of_birth', 'birth_date', 'gender']

  df = pd.DataFrame(columns = keys)

  dict_ktp = {key: None for key in keys}
  for word in extract.split("\n"):

    if "”—" in word:
      word = word.replace("”—", ":")

    #normalize NIK
    if re.search("NIK", word):

      if "L" in word:
        word = word.replace("L", "1")
      if "l" in word:
        word = word.replace("l", "1")
      if "O" in word:
        word = word.replace("O", "0")
      if "o" in word:
        word = word.replace("o", "0")
      if "D" in word:
        word = word.replace("D", "0")
      if "?" in word:
        word = word.replace("?", "7")
      if "B" in word:
        word = word.replace("B", "8")
      if "b" in word:
        word = word.replace("b", "6")
      if "A" in word:
        word = word.replace("A", "4")
      if "Z" in word:
        word = word.replace("Z", "2")
      if "z" in word:
        word = word.replace("z", "2")
      if "S" in word:
        word = word.replace("S", "5")
      if "s" in word:
        word = word.replace("s", "5")

      try:
        array = re.findall(r'[0-9]+', word)
        for arr in array:
          if len(arr) == 16:
            dict_ktp['no_ktp'] = str(arr)

      except:
        dict_ktp['NIK'] = None

    if re.search("Nama", word):

      word = word.replace("Nama", "")
      word = word.upper()

      try:
        array = re.findall(r'[A-Z]+[A-Z]+', word)
        dict_ktp['name'] = ' '.join([str(elem) for elem in array])

      except:
        dict_ktp['name'] = None

    if re.search(r'[A-Z]+[A-Z]+[,.]+ \d{2}\W\d{2}\W\d{4}', word):

      try:
        array = re.findall(r'[A-Z]+[A-Z]+', word)
        arr   = re.findall(r'\d{2}\W\d{2}\W\d{4}', word)
        dict_ktp['place_of_birth'] = ' '.join([str(elem) for elem in array])
        dict_ktp['birth_date'] = '/'.join([str(v) for elem in arr for v in re.findall(r"[\w']+", elem)])
        dict_ktp['birth_date'] = time.mktime(time.strptime(dict_ktp['birth_date'], "%d/%m/%Y"))

      except:
        dict_ktp['place_of_birth'] = None
        dict_ktp['birth_date'] = None

    try:
      if re.search("^PEREM|.*PUAN", word):
        dict_ktp['gender'] = 'FEMALE'
      elif re.search("LAKI", word):
        dict_ktp['gender'] = 'MALE'

    except:
      dict_ktp['gender'] = None

  return df.append(dict_ktp, ignore_index=True)
