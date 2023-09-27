rm -rf ./j2re-image
cp -r ../jdk8u-jdk8u322-ga/build/linux-x86_64-normal-server-release/images/j2re-image/ .
cp config.json /tmp/config.json

docker build . -f dockerfiles/256/Dockerfile-nogc -t "zzm-whisk/openwhisk-runner-java:openjdk8-simlambda-nogc256"
docker build . -f dockerfiles/256/Dockerfile-imagemagic-nogc -t "zzm-whisk/openwhisk-runner-java:openjdk8-simlambda-imagemagic-nogc256"
docker build . -f dockerfiles/256/Dockerfile-gc -t "zzm-whisk/openwhisk-runner-java:openjdk8-simlambda-gc256"
docker build . -f dockerfiles/256/Dockerfile-imagemagic-gc -t "zzm-whisk/openwhisk-runner-java:openjdk8-simlambda-imagemagic-gc256"
docker build . -f dockerfiles/512/Dockerfile-nogc -t "zzm-whisk/openwhisk-runner-java:openjdk8-simlambda-nogc512"
docker build . -f dockerfiles/512/Dockerfile-imagemagic-nogc -t "zzm-whisk/openwhisk-runner-java:openjdk8-simlambda-imagemagic-nogc512"
docker build . -f dockerfiles/512/Dockerfile-gc -t "zzm-whisk/openwhisk-runner-java:openjdk8-simlambda-gc512"
docker build . -f dockerfiles/512/Dockerfile-imagemagic-gc -t "zzm-whisk/openwhisk-runner-java:openjdk8-simlambda-imagemagic-gc512"
docker build . -f dockerfiles/1024/Dockerfile-nogc -t "zzm-whisk/openwhisk-runner-java:openjdk8-simlambda-nogc1024"
docker build . -f dockerfiles/1024/Dockerfile-imagemagic-nogc -t "zzm-whisk/openwhisk-runner-java:openjdk8-simlambda-imagemagic-nogc1024"
docker build . -f dockerfiles/1024/Dockerfile-gc -t "zzm-whisk/openwhisk-runner-java:openjdk8-simlambda-gc1024"
docker build . -f dockerfiles/1024/Dockerfile-imagemagic-gc -t "zzm-whisk/openwhisk-runner-java:openjdk8-simlambda-imagemagic-gc1024"
