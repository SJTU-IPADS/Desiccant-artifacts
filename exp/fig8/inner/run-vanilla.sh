# Usage: sh run.sh CON_COUNT
# CON_COUNT is the concurrent execution count

prepare_reclaim() {
  TARGET_PATH=../../application/java/time/scripts
  cd $TARGET_PATH
  wsk action update ZZMReclaimAll ../target/ntp-function.jar --main org.ipads.NTPFunction --docker zzm-whisk/openwhisk-runner-java:openjdk8-simlambda-nogc256 --memory 256
  wsk action update ZZMRemoveAll ../target/ntp-function.jar --main org.ipads.NTPFunction --docker zzm-whisk/openwhisk-runner-java:openjdk8-simlambda-nogc256 --memory 256
}

prepare() {
  TARGET_PATH=../../application/$1/$2/scripts
  cd $TARGET_PATH
  sh ./prepare.sh; sh ./update-idx.sh -nogc 256 $3; sh ./invoke-idx.sh $3
}

invoke_close() {
  SOURCE_PATH=`pwd`
  TARGET_PATH=$SOURCE_PATH/../../application/$1/$2/scripts
  i=0
  while [ $i -le $3 ]
  do
    sh $TARGET_PATH/invoke-idx.sh $4 > /dev/null
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
CON_COUNT=$1
mkdir -p ./result-$CON_COUNT
cd $CUR_DIR

i=0
while [ $i -lt $CON_COUNT ]
do
  cd $CUR_DIR
  prepare nodejs fft $i
  i=$((i+1))
done

cd $CUR_DIR
i=0
while [ $i -lt $CON_COUNT ]
do
  invoke_close nodejs fft 100 $i &
  i=$((i+1))
done

wait
sleep 0.1

# <zzm> uncomment this for desiccant, comment this for vanilla
#echo "concurrent invocation done, now reclaim"
# wsk action invoke ZZMReclaimAll -i --result
#!/bin/bash
# sleep 2
# </zzm> uncomment this for desiccant, comment this for vanilla

i=0
for pid in $(pgrep -f "node --expose-gc --trace_gc_verbose"); do
  sudo pmap -XX $pid > "./result-$CON_COUNT/$i.txt"
  i=$((i+1))
done
