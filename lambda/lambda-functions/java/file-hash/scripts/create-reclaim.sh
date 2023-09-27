role=`cat /home/ubuntu/lambda-functions/lambda.role`
vpc=`cat /home/ubuntu/lambda-functions/lambda.vpc`
ecr=`cat /home/ubuntu/lambda-functions/lambda.ecr`
aws lambda create-function \
--function-name zzmae-java-filehash-reclaim \
--package-type Image \
--code ImageUri=$ecr/zzmae-java-lambda-evict:file-hash-evict \
--role $role \
--vpc-config $vpc \
--memory-size 256 \
--timeout 10
