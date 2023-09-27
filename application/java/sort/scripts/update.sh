cd ..
wsk action update sort target/sort.jar --main org.ipads.Sort --timeout 300000 --docker zzm-whisk/openwhisk-runner-java:openjdk8-simlambda$1$2 --memory $2 -i 
