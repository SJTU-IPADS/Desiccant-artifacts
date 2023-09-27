cd ..
role=`cat /home/ubuntu/lambda-functions/lambda.role`
vpc=`cat /home/ubuntu/lambda-functions/lambda.vpc`
ecr=`cat /home/ubuntu/lambda-functions/lambda.ecr`
aws lambda create-function --function-name zzmae-java-map --zip-file fileb://target/chain-micro.jar --runtime java8 --role $role --memory-size 256  --timeout 100 --vpc-config $vpc --handler org.ipads.FunctionExecutor::handleRequest
aws lambda create-function --function-name zzmae-java-reduce --zip-file fileb://target/chain-micro.jar --runtime java8 --role $role --memory-size 256  --timeout 100 --vpc-config $vpc --handler org.ipads.FunctionExecutor::handleRequest
