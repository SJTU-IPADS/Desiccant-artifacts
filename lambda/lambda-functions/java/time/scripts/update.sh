aws lambda update-function-configuration --function-name zzmae-java-time-function --description "`date`"
#aws lambda update-function-configuration --function-name zzm-time-function-nogc --description "`date`"
#wsk action update time-function ../target/ntp-function.jar --main org.ipads.NTPFunction --docker zzm-whisk/openwhisk-runner-java:openjdk8-simlambda$1$2 --memory $2
