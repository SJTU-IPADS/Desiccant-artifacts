role=`cat /home/ubuntu/lambda-functions/lambda.role`
vpc=`cat /home/ubuntu/lambda-functions/lambda.vpc`
ecr=`cat /home/ubuntu/lambda-functions/lambda.ecr`
aws lambda create-function \
--function-name zzmae-java-time-reclaim \
--package-type Image \
--code ImageUri=$ecr/zzmae-java-lambda-evict:time-evict \
--role $role \
--memory-size 256 \
--timeout 10
