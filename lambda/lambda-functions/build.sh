cd java
ROOT_PATH=`pwd`
HOST=`hostname -I | awk '{print $1}'`
cd $ROOT_PATH/file-hash
cd scripts && sh build.sh $HOST
cp ../target/hash-file.jar /home/ubuntu/lambda-functions/desiccant-image/java/aws-lambda-base-images/inner/file-hash.jar
cd $ROOT_PATH/hotel-seraching
cd scripts && sh build.sh $HOST
cp ../target/reservation.jar /home/ubuntu/lambda-functions/desiccant-image/java/aws-lambda-base-images/inner/hotel.jar
cd $ROOT_PATH/image-resize
cd scripts && sh build.sh $HOST
cp ../target/awt-thumbnail.jar /home/ubuntu/lambda-functions/desiccant-image/java/aws-lambda-base-images/inner/image-resize.jar
cd $ROOT_PATH/mapreduce
cd scripts && sh build.sh $HOST
cp ../target/chain-micro.jar /home/ubuntu/lambda-functions/desiccant-image/java/aws-lambda-base-images/inner/mapreduce.jar
cd $ROOT_PATH/sort
cd scripts && sh build.sh
cp ../target/sort.jar /home/ubuntu/lambda-functions/desiccant-image/java/aws-lambda-base-images/inner/sort.jar
cd $ROOT_PATH/time
cd scripts && sh build.sh
cp ../target/ntp-function.jar /home/ubuntu/lambda-functions/desiccant-image/java/aws-lambda-base-images/inner/time.jar


