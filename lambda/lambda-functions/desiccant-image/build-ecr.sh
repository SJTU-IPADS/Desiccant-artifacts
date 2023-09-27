aws ecr create-repository --repository-name zzmae-lambda-evict --region us-east-1
aws ecr create-repository --repository-name zzmae-java-lambda-evict --region us-east-1
ecr=`cat /home/ubuntu/lambda-functions/lambda.ecr`
sudo docker login -u AWS -p $(aws ecr get-login-password --region us-east-1) $ecr
