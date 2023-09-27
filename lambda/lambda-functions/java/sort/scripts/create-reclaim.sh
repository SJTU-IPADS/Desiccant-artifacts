ecr=`cat /home/ubuntu/lambda-functions/lambda.ecr`
role=`cat /home/ubuntu/lambda-functions/lambda.role`
aws lambda create-function \
--function-name zzmae-java-sort-reclaim \
--package-type Image \
--code ImageUri=$ecr/zzmae-java-lambda-evict:sort-evict \
--role $role \
--memory-size 256 \
--timeout 10
