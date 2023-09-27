role=`cat /home/ubuntu/lambda-functions/lambda.role`
vpc=`cat /home/ubuntu/lambda-functions/lambda.vpc`
ecr=`cat /home/ubuntu/lambda-functions/lambda.ecr`
aws lambda create-function \
--function-name zzmae-java-image-resize-reclaim \
--package-type Image \
--code ImageUri=$ecr/zzmae-java-lambda-evict:image-resize-evict \
--role $role \
--vpc-config $vpc \
--memory-size 256 \
--timeout 10
