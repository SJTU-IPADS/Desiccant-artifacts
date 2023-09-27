cd ../src
wsk -i action update dynamic-html index.zip --docker=zzm-whisk/openwhisk-runner-nodejs:14-simlambda$1$2 --memory $2
