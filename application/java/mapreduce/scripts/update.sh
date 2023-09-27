cd ..
wsk action update map target/chain-micro.jar --main org.ipads.Mapper --docker  zzm-whisk/openwhisk-runner-java:openjdk8-simlambda$1$2 --memory $2 -i 
wsk action update reduce target/chain-micro.jar --main org.ipads.Reducer --docker zzm-whisk/openwhisk-runner-java:openjdk8-simlambda$1$2 --memory $2 -i 
wsk action update mapreduce --sequence map,reduce -i
