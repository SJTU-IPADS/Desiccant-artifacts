cd ..
role=`cat /home/ubuntu/lambda-functions/lambda.role`
vpc=`cat /home/ubuntu/lambda-functions/lambda.vpc`
ecr=`cat /home/ubuntu/lambda-functions/lambda.ecr`
aws lambda create-function --function-name zzmae-java-hotel-1 --zip-file fileb://target/reservation.jar --runtime java8 --role $role --memory-size 256  --timeout 100 --vpc-config $vpc --handler org.ipads.FunctionExecutor::handleRequest
aws lambda create-function --function-name zzmae-java-hotel-2 --zip-file fileb://target/reservation.jar --runtime java8 --role $role --memory-size 256  --timeout 100 --vpc-config $vpc --handler org.ipads.FunctionExecutor::handleRequest
aws lambda create-function --function-name zzmae-java-hotel-3 --zip-file fileb://target/reservation.jar --runtime java8 --role $role --memory-size 256  --timeout 100 --vpc-config $vpc --handler org.ipads.FunctionExecutor::handleRequest
