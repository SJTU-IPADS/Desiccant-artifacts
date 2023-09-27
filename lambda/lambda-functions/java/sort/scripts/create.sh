cd ..
#wsk action update sort target/sort.jar --main org.ipads.Sort --timeout 300000 --docker zzm-whisk/openwhisk-runner-java:openjdk8-simlambda$1$2 --memory $2 -i 
role=`cat /home/ubuntu/lambda-functions/lambda.role`
vpc=`cat /home/ubuntu/lambda-functions/lambda.vpc`
ecr=`cat /home/ubuntu/lambda-functions/lambda.ecr`
aws lambda create-function --function-name zzmae-java-sort --zip-file fileb://target/sort.jar --runtime java8 --role $role --memory-size 256 --handler org.ipads.FunctionExecutor::handleRequest
#aws lambda create-function --function-name zzm-sort-gc --zip-file fileb://target/sort.jar --runtime java8 --role arn:aws:iam::894912142054:role/service-role/zzm-heap-profiler-role-h5rbqedp --memory-size 256 --handler org.ipads.FunctionExecutorGC::handleRequest
