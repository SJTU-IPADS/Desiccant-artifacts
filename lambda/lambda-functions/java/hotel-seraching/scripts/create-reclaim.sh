role=`cat /home/ubuntu/lambda-functions/lambda.role`
vpc=`cat /home/ubuntu/lambda-functions/lambda.vpc`
ecr=`cat /home/ubuntu/lambda-functions/lambda.ecr`
aws lambda create-function \
--function-name zzmae-java-hotel-1-reclaim \
--package-type Image \
--code ImageUri=$ecr/zzmae-java-lambda-evict:hotel-evict \
--role $role \
--vpc-config $vpc \
--memory-size 256 \
--timeout 30
aws lambda create-function \
--function-name zzmae-java-hotel-2-reclaim \
--package-type Image \
--code ImageUri=$ecr/zzmae-java-lambda-evict:hotel-evict \
--role $role \
--vpc-config $vpc \
--memory-size 256 \
--timeout 30
aws lambda create-function \
--function-name zzmae-java-hotel-3-reclaim \
--package-type Image \
--code ImageUri=$ecr/zzmae-java-lambda-evict:hotel-evict \
--role $role \
--vpc-config $vpc \
--memory-size 256 \
--timeout 30
