wsk action update image-resize-function ../target/awt-thumbnail.jar --main org.ipads.AwtThumbnail --docker zzm-whisk/openwhisk-runner-java:openjdk8-simlambda$1$2 --memory $2 -i
