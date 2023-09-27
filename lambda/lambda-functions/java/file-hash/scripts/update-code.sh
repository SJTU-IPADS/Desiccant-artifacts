cd ..
#wsk action update hashfile target/hash-file.jar --main org.ipads.HashFile --timeout 300000 --docker zzm-whisk/openwhisk-runner-java:openjdk8-simlambda$1$2 --memory $2
aws lambda update-function-code --function-name zzmae-java-filehash --zip-file fileb://target/hash-file.jar
