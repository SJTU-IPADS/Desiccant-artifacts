cd ../jdk8u-jdk8u322-ga
sh build.sh

cd ../openwhisk-runtime-java/proxy
./gradlew oneJar
cd ../ 
sh build.sh
