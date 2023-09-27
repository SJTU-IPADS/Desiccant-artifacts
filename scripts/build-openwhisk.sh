cd ../openwhisk-desiccant
sh build-controller.sh
sh build-invoker.sh
cd ../openwhisk-vanilla
sh build-controller.sh
sh build-invoker.sh

