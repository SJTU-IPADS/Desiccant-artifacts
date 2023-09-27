cd ..
role=`cat /home/ubuntu/lambda-functions/lambda.role`
vpc=`cat /home/ubuntu/lambda-functions/lambda.vpc`
ecr=`cat /home/ubuntu/lambda-functions/lambda.ecr`
aws lambda create-function --function-name zzmae-java-image-resize --zip-file fileb://target/awt-thumbnail.jar --runtime java8 --role $role --memory-size 256  --timeout 20 --vpc-config $vpc --handler org.ipads.FunctionExecutor::handleRequest
