rm ./core/nodejs14Action/node
cp -r ../node-14.20/out/Release/node ./core/nodejs14Action/
./gradlew core:nodejs14Action:distDocker
docker tag whisk/action-nodejs-v14:latest  zzm-whisk/openwhisk-runner-nodejs:14-simlambda

cd ./Dockerfiles
docker build . -f Dockerfile-gc-256 -t zzm-whisk/openwhisk-runner-nodejs:14-simlambda-gc256
docker build . -f Dockerfile-nogc-256 -t zzm-whisk/openwhisk-runner-nodejs:14-simlambda-nogc256
docker build . -f Dockerfile-gc-512 -t zzm-whisk/openwhisk-runner-nodejs:14-simlambda-gc512
docker build . -f Dockerfile-nogc-512 -t zzm-whisk/openwhisk-runner-nodejs:14-simlambda-nogc512
docker build . -f Dockerfile-gc-1024 -t zzm-whisk/openwhisk-runner-nodejs:14-simlambda-gc1024
docker build . -f Dockerfile-nogc-1024 -t zzm-whisk/openwhisk-runner-nodejs:14-simlambda-nogc1024
