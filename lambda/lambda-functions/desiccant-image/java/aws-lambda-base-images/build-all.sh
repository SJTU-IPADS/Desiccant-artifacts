ecr=`cat /home/ubuntu/lambda-functions/lambda.ecr`
sudo docker login -u AWS -p $(aws ecr get-login-password --region us-east-1) $ecr
sh build.sh inner/Dockerfile-time time-evict
sh build.sh inner/Dockerfile-sort sort-evict
sh build.sh inner/Dockerfile-filehash file-hash-evict
sh build.sh inner/Dockerfile-image-resize image-resize-evict
sh build.sh inner/Dockerfile-mapreduce mapreduce-evict
sh build.sh inner/Dockerfile-hotel hotel-evict

