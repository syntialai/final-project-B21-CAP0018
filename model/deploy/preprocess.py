import cv2 as cv
import numpy as np

# Read and load yolov3 model
cfgpath = './yolov3_face/yolov3.cfg'    # cfg path of yolo model
weightpath = './yolov3_face/yolov3.weights' # weights path of yolo model
net = cv.dnn.readNet(weightpath, cfgpath) # load model
layer_names = net.getLayerNames() # get model layer names
output_layers = [layer_names[i[0]-1] for i in net.getUnconnectedOutLayers()] # get output layer of yolo model


# standardize input ( (-mean)/std)
def preprocess_standardize(img_path, resize_size = (160,160), confidence_threshold = 0.5, nms_threshold = 0.6) :
    '''
    Reads image from img_path (a file from request) and returns a standardized image (subtracted mean and divided by std)
    '''
    # read image
    # img = cv.imread(img_path)
    img = cv.imdecode(np.fromstring(img_path, np.uint8), cv.IMREAD_UNCHANGED)
    # convert image for the yolov3 inputs
    blob = cv.dnn.blobFromImage(img, 1.0/255.0, (416,416), swapRB= True,crop = False)
    # Feed the input to yolov3
    net.setInput(blob)
    outputs = net.forward(output_layers)

    # Convert outputs from yolo into lists of bboxes and confidence scores
    boxes = []
    confidences = []
    h,w = img.shape[:2]
    for out in outputs :
        for o in out :
            scores = o[5:]
            c_id = np.argmax(scores)
            conf = scores[c_id]
            if conf > confidence_threshold :
                box = o[:4] * np.array([w,h,w,h])
                (centerX,centerY, width,height) = box.astype('int')
                x = int(centerX - (width/2))
                y = int(centerY - (height/2))
                box = [x, y, int(width), int(height)]

                boxes.append(box)
                confidences.append(float(conf))

    # Get indices of the top 1 detected face
    indices = cv.dnn.NMSBoxes(boxes, confidences, score_threshold=confidence_threshold,
                              nms_threshold = nms_threshold, top_k = 1)
    if len(indices) > 0 :
        for i in indices.flatten():
            (x,y) = (boxes[i][0], boxes[i][1])
            (w,h) = (boxes[i][2], boxes[i][3])
            # Change points to zero if bbox is negative
            x = 0 if x < 0 else x
            y = 0 if y < 0 else y
            crop_img = img[y:y+h, x:x+w]
            crop_img = cv.resize(crop_img, resize_size)
            # cv2_imshow(crop_img)

    # Normalize image and turn into np array
    crop_img = np.asarray(crop_img)
    crop_img = crop_img.astype('float32')
    mean,std = crop_img.mean(), crop_img.std()
    crop_img = (crop_img - mean)/std
    return np.expand_dims(crop_img,axis=0)


# preprocess normalize (divides each rgb value by 255)
def preprocess_normalize(img_path, resize_size = (160,160), confidence_threshold = 0.5, nms_threshold = 0.6) :
    # read image
    # img = cv.imread(img_path)
    img = cv.imdecode(np.fromstring(img_path, np.uint8), cv.IMREAD_UNCHANGED)
    print(img.shape)
    # convert image for the yolov3 inputs
    blob = cv.dnn.blobFromImage(img, 1.0/255.0, (416,416), swapRB= True,crop = False)
    # Feed the input to yolov3
    net.setInput(blob)
    outputs = net.forward(output_layers)

    # Convert outputs from yolo into lists of bboxes and confidence scores
    boxes = []
    confidences = []
    h,w = img.shape[:2]
    for out in outputs :
        for o in out :
            scores = o[5:]
            c_id = np.argmax(scores)
            conf = scores[c_id]
            if conf > confidence_threshold :
                box = o[:4] * np.array([w,h,w,h])
                (centerX,centerY, width,height) = box.astype('int')
                x = int(centerX - (width/2))
                y = int(centerY - (height/2))
                box = [x, y, int(width), int(height)]

                boxes.append(box)
                confidences.append(float(conf))

    # Get indices of the top 1 detected face
    indices = cv.dnn.NMSBoxes(boxes, confidences, score_threshold=confidence_threshold,
                              nms_threshold = nms_threshold, top_k = 1)
    if len(indices) > 0 :
        for i in indices.flatten():
            (x,y) = (boxes[i][0], boxes[i][1])
            (w,h) = (boxes[i][2], boxes[i][3])
            # Change points to zero if bbox is negative
            x = 0 if x < 0 else x
            y = 0 if y < 0 else y
            crop_img = img[y:y+h, x:x+w]
            crop_img = cv.resize(crop_img, resize_size)
            # cv2_imshow(crop_img)

    # Normalize image and turn into np array
    crop_img = np.asarray(crop_img)
    crop_img = crop_img.astype('float32')
    crop_img = crop_img / 255.0
    return np.expand_dims(crop_img,axis=0)
