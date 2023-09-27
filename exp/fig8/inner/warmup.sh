prepare_reclaim() {
  TARGET_PATH=../../application/java/time/scripts
  cd $TARGET_PATH
  wsk action update ZZMReclaimAll ../target/ntp-function.jar --main org.ipads.NTPFunction --docker zzm-whisk/openwhisk-runner-java:openjdk8-simlambda-nogc256 --memory 256
  wsk action update ZZMRemoveAll ../target/ntp-function.jar --main org.ipads.NTPFunction --docker zzm-whisk/openwhisk-runner-java:openjdk8-simlambda-nogc256 --memory 256
}

prepare() {
  TARGET_PATH=../../application/$1/$2/scripts
  cd $TARGET_PATH
  sh ./prepare.sh; sh ./update.sh -nogc 256; sh ./invoke.sh
}

invoke_close() {
  SOURCE_PATH=`pwd`
  mkdir -p $SOURCE_PATH/result/$1/$2/
  TARGET_PATH=$SOURCE_PATH/../../application/$1/$2/scripts
  i=0
  while [ $i -le $3 ]
  do
    sh $TARGET_PATH/invoke.sh > $SOURCE_PATH/result/$1/$2/$4-$i.txt
    i=$(($i+1))
  done
}

invoke_close_warmup() {
  SOURCE_PATH=`pwd`
  TARGET_PATH=$SOURCE_PATH/../../application/$1/$2/scripts
  i=0
  while [ $i -le $3 ]
  do
    sh $TARGET_PATH/invoke.sh
    i=$(($i+1))
  done
}

CUR_DIR=`pwd`

# warm up
prepare_reclaim
cd $CUR_DIR
prepare java time
cd $CUR_DIR
i=0
while [ $i -lt 10 ]
do
  cd $CUR_DIR
  invoke_close_warmup java time 400 &
  i=$(($i+1))
done
wait
