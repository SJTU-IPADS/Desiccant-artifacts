sudo docker build . -t java8:$2 -f $1
ecr=`cat /home/ubuntu/lambda-functions/lambda.ecr`
sudo docker tag java8:$2 $ecr/zzmae-java-lambda-evict:$2
sudo docker push $ecr/zzmae-java-lambda-evict:$2

