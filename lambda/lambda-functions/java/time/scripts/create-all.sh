cd ..
#wsk action update sort target/sort.jar --main org.ipads.Sort --timeout 300000 --docker zzm-whisk/openwhisk-runner-java:openjdk8-simlambda$1$2 --memory $2 -i
#aws lambda create-function --function-name zzm-time-function-gc --package-type Image --code ImageUri=894912142054.dkr.ecr.us-east-1.amazonaws.com/zzm-lambda-evict:zzm-time-gc --role arn:aws:iam::894912142054:role/ZZM-LambdaProfile --memory-size 256  --timeout 100 --vpc-config SubnetIds=subnet-24faae69,SecurityGroupIds=sg-02463bfdd6482fcf8
#aws lambda create-function --function-name zzm-time-function-nogc --package-type Image --code ImageUri=894912142054.dkr.ecr.us-east-1.amazonaws.com/zzm-lambda-evict:zzm-time-nogc --role arn:aws:iam::894912142054:role/ZZM-LambdaProfile --memory-size 256  --timeout 100 --vpc-config SubnetIds=subnet-24faae69,SecurityGroupIds=sg-02463bfdd6482fcf8
#aws lambda create-function --function-name zzm-time-function-zzm --package-type Image --code ImageUri=894912142054.dkr.ecr.us-east-1.amazonaws.com/zzm-lambda-evict:zzm-time-gc --role arn:aws:iam::894912142054:role/ZZM-LambdaProfile --memory-size 256  --timeout 100 --vpc-config SubnetIds=subnet-24faae69,SecurityGroupIds=sg-02463bfdd6482fcf8
#aws lambda create-function --function-name zzm-sort-gc --package-type Image --code ImageUri=894912142054.dkr.ecr.us-east-1.amazonaws.com/zzm-lambda-evict:zzm-time-gc --role arn:aws:iam::894912142054:role/ZZM-LambdaProfile --memory-size 256  --timeout 100 --vpc-config SubnetIds=subnet-24faae69,SecurityGroupIds=sg-02463bfdd6482fcf8
#aws lambda create-function --function-name zzm-sort-zzm --package-type Image --code ImageUri=894912142054.dkr.ecr.us-east-1.amazonaws.com/zzm-lambda-evict:zzm-time-gc --role arn:aws:iam::894912142054:role/ZZM-LambdaProfile --memory-size 256  --timeout 100 --vpc-config SubnetIds=subnet-24faae69,SecurityGroupIds=sg-02463bfdd6482fcf8
#aws lambda create-function --function-name zzm-sort-nogc --package-type Image --code ImageUri=894912142054.dkr.ecr.us-east-1.amazonaws.com/zzm-lambda-evict:zzm-time-nogc --role arn:aws:iam::894912142054:role/ZZM-LambdaProfile --memory-size 256  --timeout 100 --vpc-config SubnetIds=subnet-24faae69,SecurityGroupIds=sg-02463bfdd6482fcf8
#aws lambda create-function --function-name zzm-filehash-gc --package-type Image --code ImageUri=894912142054.dkr.ecr.us-east-1.amazonaws.com/zzm-lambda-evict:zzm-time-gc --role arn:aws:iam::894912142054:role/ZZM-LambdaProfile --memory-size 256  --timeout 100 --vpc-config SubnetIds=subnet-24faae69,SecurityGroupIds=sg-02463bfdd6482fcf8
#aws lambda create-function --function-name zzm-filehash-zzm --package-type Image --code ImageUri=894912142054.dkr.ecr.us-east-1.amazonaws.com/zzm-lambda-evict:zzm-time-gc --role arn:aws:iam::894912142054:role/ZZM-LambdaProfile --memory-size 256  --timeout 100 --vpc-config SubnetIds=subnet-24faae69,SecurityGroupIds=sg-02463bfdd6482fcf8
#aws lambda create-function --function-name zzm-filehash-nogc --package-type Image --code ImageUri=894912142054.dkr.ecr.us-east-1.amazonaws.com/zzm-lambda-evict:zzm-time-nogc --role arn:aws:iam::894912142054:role/ZZM-LambdaProfile --memory-size 256  --timeout 100 --vpc-config SubnetIds=subnet-24faae69,SecurityGroupIds=sg-02463bfdd6482fcf8
#aws lambda create-function --function-name zzm-image-resize-function-gc --package-type Image --code ImageUri=894912142054.dkr.ecr.us-east-1.amazonaws.com/zzm-lambda-evict:zzm-time-gc --role arn:aws:iam::894912142054:role/ZZM-LambdaProfile --memory-size 256  --timeout 100 --vpc-config SubnetIds=subnet-24faae69,SecurityGroupIds=sg-02463bfdd6482fcf8
#aws lambda create-function --function-name zzm-image-resize-function-zzm --package-type Image --code ImageUri=894912142054.dkr.ecr.us-east-1.amazonaws.com/zzm-lambda-evict:zzm-time-gc --role arn:aws:iam::894912142054:role/ZZM-LambdaProfile --memory-size 256  --timeout 100 --vpc-config SubnetIds=subnet-24faae69,SecurityGroupIds=sg-02463bfdd6482fcf8
#aws lambda create-function --function-name zzm-image-resize-function-nogc --package-type Image --code ImageUri=894912142054.dkr.ecr.us-east-1.amazonaws.com/zzm-lambda-evict:zzm-time-nogc --role arn:aws:iam::894912142054:role/ZZM-LambdaProfile --memory-size 256  --timeout 100 --vpc-config SubnetIds=subnet-24faae69,SecurityGroupIds=sg-02463bfdd6482fcf8
#aws lambda create-function --function-name zzm-filehash-gc --package-type Image --code ImageUri=894912142054.dkr.ecr.us-east-1.amazonaws.com/zzm-lambda-evict:zzm-time-gc --role arn:aws:iam::894912142054:role/ZZM-LambdaProfile --memory-size 256  --timeout 100 --vpc-config SubnetIds=subnet-24faae69,SecurityGroupIds=sg-02463bfdd6482fcf8
#aws lambda create-function --function-name zzm-filehash-zzm --package-type Image --code ImageUri=894912142054.dkr.ecr.us-east-1.amazonaws.com/zzm-lambda-evict:zzm-time-gc --role arn:aws:iam::894912142054:role/ZZM-LambdaProfile --memory-size 256  --timeout 100 --vpc-config SubnetIds=subnet-24faae69,SecurityGroupIds=sg-02463bfdd6482fcf8
#aws lambda create-function --function-name zzm-filehash-nogc --package-type Image --code ImageUri=894912142054.dkr.ecr.us-east-1.amazonaws.com/zzm-lambda-evict:zzm-time-nogc --role arn:aws:iam::894912142054:role/ZZM-LambdaProfile --memory-size 256  --timeout 100 --vpc-config SubnetIds=subnet-24faae69,SecurityGroupIds=sg-02463bfdd6482fcf8

role=`cat /home/ubuntu/lambda-functions/lambda.role`
vpc=`cat /home/ubuntu/lambda-functions/lambda.vpc`
ecr=`cat /home/ubuntu/lambda-functions/lambda.ecr`

aws lambda create-function --function-name zzmae-map-gc --package-type Image --code ImageUri=$ecr/zzmae-lambda-evict:zzm-time-gc --role $role --memory-size 256  --timeout 100 --vpc-config $vpc
aws lambda create-function --function-name zzmae-map-zzm --package-type Image --code ImageUri=$ecr/zzmae-lambda-evict:zzm-time-gc --role $role --memory-size 256  --timeout 100 --vpc-confg $vpc
aws lambda create-function --function-name zzmae-map-nogc --package-type Image --code ImageUri=$ecr/zzmae-lambda-evict:zzm-time-nogc --role $role --memory-size 256  --timeout 100 --vpc-confg $vpc
aws lambda create-function --function-name zzmae-reduce-gc --package-type Image --code ImageUri=$ecr/zzmae-lambda-evict:zzm-time-gc --role $role --memory-size 256  --timeout 100 --vpc-confg $vpc
aws lambda create-function --function-name zzmae-reduce-zzm --package-type Image --code ImageUri=$ecr/zzmae-lambda-evict:zzm-time-gc --role $role --memory-size 256  --timeout 100 --vpc-confg $vpc
aws lambda create-function --function-name zzmae-reduce-nogc --package-type Image --code ImageUri=$ecr/zzmae-lambda-evict:zzm-time-nogc --role $role --memory-size 256  --timeout 100 --vpc-confg $vpc


aws lambda create-function --function-name zzmae-hotel-1-gc --package-type Image --code ImageUri=$ecr/zzmae-lambda-evict:zzm-time-gc --role $role --memory-size 256  --timeout 100 --vpc-confg $vpc
aws lambda create-function --function-name zzmae-hotel-1-zzm --package-type Image --code ImageUri=$ecr/zzmae-lambda-evict:zzm-time-gc --role $role --memory-size 256  --timeout 100 --vpc-confg $vpc
aws lambda create-function --function-name zzmae-hotel-1-nogc --package-type Image --code ImageUri=$ecr/zzmae-lambda-evict:zzm-time-nogc --role $role --memory-size 256  --timeout 100 --vpc-confg $vpc
aws lambda create-function --function-name zzmae-hotel-2-gc --package-type Image --code ImageUri=$ecr/zzmae-lambda-evict:zzm-time-gc --role $role --memory-size 256  --timeout 100 --vpc-confg $vpc
aws lambda create-function --function-name zzmae-hotel-2-zzm --package-type Image --code ImageUri=$ecr/zzmae-lambda-evict:zzm-time-gc --role $role --memory-size 256  --timeout 100 --vpc-confg $vpc
aws lambda create-function --function-name zzmae-hotel-2-nogc --package-type Image --code ImageUri=$ecr/zzmae-lambda-evict:zzm-time-nogc --role $role --memory-size 256  --timeout 100 --vpc-confg $vpc
aws lambda create-function --function-name zzmae-hotel-3-gc --package-type Image --code ImageUri=$ecr/zzmae-lambda-evict:zzm-time-gc --role $role --memory-size 256  --timeout 100 --vpc-confg $vpc
aws lambda create-function --function-name zzmae-hotel-3-zzm --package-type Image --code ImageUri=$ecr/zzmae-lambda-evict:zzm-time-gc --role $role --memory-size 256  --timeout 100 --vpc-confg $vpc
aws lambda create-function --function-name zzmae-hotel-3-nogc --package-type Image --code ImageUri=$ecr/zzmae-lambda-evict:zzm-time-nogc --role $role --memory-size 256  --timeout 100 --vpc-confg $vpc






