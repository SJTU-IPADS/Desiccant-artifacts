cd ..
role=`cat /home/ubuntu/lambda-functions/lambda.role`
vpc=`cat /home/ubuntu/lambda-functions/lambda.vpc`

#wsk action update sort target/sort.jar --main org.ipads.Sort --timeout 300000 --docker zzm-whisk/openwhisk-runner-java:openjdk8-simlambda$1$2 --memory $2 -i
aws lambda create-function --function-name zzmae-java-time-function --zip-file fileb://target/ntp-function.jar --runtime java8 --role $role --memory-size 256  --timeout 100 --vpc-config $vpc --handler org.ipads.FunctionExecutor::handleRequest
#aws lambda create-function --function-name zzm-time-function --package-type Image --code ImageUri=894912142054.dkr.ecr.us-east-1.amazonaws.com/zzm-lambda-evict:zzm-time-gc --role arn:aws:iam::894912142054:role/ZZM-LambdaProfile --memory-size 256  --timeout 100 --vpc-config SubnetIds=subnet-24faae69,SecurityGroupIds=sg-02463bfdd6482fcf8
#aws lambda create-function --function-name zzm-time-function-nogc --package-type Image --code ImageUri=894912142054.dkr.ecr.us-east-1.amazonaws.com/zzm-lambda-evict:zzm-time-nogc --role arn:aws:iam::894912142054:role/ZZM-LambdaProfile --memory-size 256  --timeout 100 --vpc-config SubnetIds=subnet-24faae69,SecurityGroupIds=sg-02463bfdd6482fcf8
