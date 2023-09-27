cd java
ROOT_PATH=`pwd`
cd $ROOT_PATH/file-hash
cd scripts && sh create.sh
sh create-reclaim.sh
cd $ROOT_PATH/hotel-seraching
cd scripts && sh create.sh
sh create-reclaim.sh
cd $ROOT_PATH/image-resize
cd scripts && sh create.sh
sh create-reclaim.sh
cd $ROOT_PATH/mapreduce
cd scripts && sh create.sh
sh create-reclaim.sh
cd $ROOT_PATH/sort
cd scripts && sh create.sh
sh create-reclaim.sh
cd $ROOT_PATH/time
cd scripts && sh create.sh
sh create-reclaim.sh


