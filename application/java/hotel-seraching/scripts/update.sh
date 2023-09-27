cd ..
wsk action update nearbychain target/reservation.jar --main com.example.NearByFuncChain -i --docker zzm-whisk/openwhisk-runner-java:openjdk8-simlambda$1$2 --memory $2
wsk action update getrates target/reservation.jar --main com.example.GetRatesFunc -i --docker zzm-whisk/openwhisk-runner-java:openjdk8-simlambda$1$2 --memory $2
wsk action update searchresult target/reservation.jar --main com.example.SearchResultFunc -i --docker zzm-whisk/openwhisk-runner-java:openjdk8-simlambda$1$2 --memory $2
wsk action update searchnearbysequence --sequence nearbychain,getrates,searchresult -i
