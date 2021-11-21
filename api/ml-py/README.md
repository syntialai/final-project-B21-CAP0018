# B21-CAP0018 - Cloud Computing

## Services Used in Google Cloud Platform

- Cloud Firestore
- Cloud Storage
- Compute Engine VM Instance
- IAM & Admin
- VPC Network Firewall
- Cloud Function
- APIs & Services
- Monitoring


## Specifications

#### VM Instances
- n2-standard-2 machine
- Running on Debian OS 10 busters
- 30GB of Boot Disks 
- Zone: asia-southeast2-a (Jakarta)

## Usage
- Setup the VM Instance as the Machine Learning Model server
- VPC Network Firewall to give access to use the port
- Cloud Storage to save the model and images
- Cloud Firestore as the database
- Cloud Function used to detect trigger and send POST request
- Monitoring to monitor the APIs and Services
- Billing to monitor the spending
