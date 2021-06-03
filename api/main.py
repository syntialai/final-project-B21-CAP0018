import requests

def verify_identity(event, context):
    """Triggered by a change to a Firestore document.
    Args:
         event (dict): Event payload.
         context (google.cloud.functions.Context): Metadata for the event.
    """
    resource_string = context.resource
    userId = resource_string.split("/")[-1] # This is the userId
    oldVerificationStatus = event["oldValue"]["fields"]["verification_status"]["stringValue"]
    verificationStatus = event["value"]["fields"]["verification_status"]["stringValue"] # This is the verification_status
    if (oldVerificationStatus != verificationStatus and verificationStatus == "UPLOADED"):
        ktpUrl = event["value"]["fields"]["ktp_url"]["stringValue"]
        selfieUrl = event["value"]["fields"]["selfie_url"]["stringValue"]
        # Call the API here 
        # key = b44da9c5e5d1082e3b113ab656df00b310b0d2b5
        payload = { 'userId': userId, 'ktp_url': ktpUrl, 'selfie_url': selfieUrl }
        r = requests.request("POST", url="http://34.101.79.70:4321/register", json=payload)
        print(f"Changed by user: {userId} with status {verificationStatus}, data ktp : {ktpUrl}, data selfie: {selfieUrl}")
