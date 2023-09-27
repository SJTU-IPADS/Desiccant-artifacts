cd ../node-14.20
./configure
make -j`nproc`

cd ../openwhisk-runtime-nodejs
sh build.sh
