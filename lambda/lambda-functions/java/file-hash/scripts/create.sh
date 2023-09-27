cd ..
role=`cat /home/ubuntu/lambda-functions/lambda.role`
vpc=`cat /home/ubuntu/lambda-functions/lambda.vpc`
ecr=`cat /home/ubuntu/lambda-functions/lambda.ecr`
#wsk action update sort target/sort.jar --main org.ipads.Sort --timeout 300000 --docker zzm-whisk/openwhisk-runner-java:openjdk8-simlambda$1$2 --memory $2 -i 
aws lambda create-function --function-name zzmae-java-filehash --zip-file fileb://target/hash-file.jar --runtime java8 --role $role --memory-size 256  --vpc-config $vpc --handler org.ipads.FunctionExecutor::handleRequest
