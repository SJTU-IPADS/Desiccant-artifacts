sudo docker run --rm -itd -p 16379:6379 --name zzm-redis redis
sudo docker run --rm -dit -p 5984:5984 -e COUCHDB_USER=whisk_admin -e COUCHDB_PASSWORD=some_passw0rd --name zzm-couchdb apache/couchdb:2.3
sleep 8
cd java
ROOT_PATH=`pwd`
cd $ROOT_PATH/file-hash
cd scripts && sh prepare.sh
cd $ROOT_PATH/hotel-seraching
cd scripts && sh prepare.sh
cd $ROOT_PATH/image-resize
cd scripts && sh prepare.sh
cd $ROOT_PATH/mapreduce
cd scripts && sh prepare.sh

