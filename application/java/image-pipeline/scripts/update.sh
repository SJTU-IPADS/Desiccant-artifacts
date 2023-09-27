cd ..
wsk action update extractImageMetadata extract-image-metadata/target/extract-image-metadata.jar --main org.ipads.ExtractImageMetadata --docker zzm-whisk/openwhisk-runner-java:openjdk8-simlambda-imagemagic$1$2 --memory $2 -i
wsk action update transformMetadata transform-metadata/target/transform-metadata.jar --main org.ipads.TransfromMetadata --docker  zzm-whisk/openwhisk-runner-java:openjdk8-simlambda$1$2 --memory $2 -i
wsk action update thumbnail thumbnail/target/thumbnail.jar --main org.ipads.Thumbnail --docker zzm-whisk/openwhisk-runner-java:openjdk8-simlambda-imagemagic$1$2 --memory $2 -i
wsk action update storeImageMetadata  store-image-metadata/target/store-image-metadata.jar --main org.ipads.StoreImageMetadata --docker  zzm-whisk/openwhisk-runner-java:openjdk8-simlambda$1$2 --memory $2 -i
wsk action update imageRecognitionSequence --sequence extractImageMetadata,transformMetadata,thumbnail,storeImageMetadata -i
