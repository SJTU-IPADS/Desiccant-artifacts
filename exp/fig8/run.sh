start_openwhisk() {
  SCRIPT_ROOT=`pwd`
  cd ../../openwhisk-docker-compose/
  sh run.sh $1
  wait
  cd ../pmap-server-c
  sh start.sh
  cd $SCRIPT_ROOT
}

stop_openwhisk() {
  SCRIPT_ROOT=`pwd`
  cd ../../openwhisk-docker-compose/
  sh stop.sh
  cd ../pmap-server-c
  sh stop.sh
  cd $SCRIPT_ROOT
}

prepare_reclaim() {
  SCRIPT_ROOT=`pwd`
  TARGET_PATH=../../application/java/time/scripts
  cd $TARGET_PATH
  wsk action update ZZMReclaimAll ../target/ntp-function.jar --main org.ipads.NTPFunction --docker zzm-whisk/openwhisk-runner-java:openjdk8-simlambda-nogc256 --memory 256
  wsk action update ZZMRemoveAll ../target/ntp-function.jar --main org.ipads.NTPFunction --docker zzm-whisk/openwhisk-runner-java:openjdk8-simlambda-nogc256 --memory 256
  cd $SCRIPT_ROOT
}

prepare_warmup() {
  TARGET_PATH=../../application/$1/$2/scripts
  cd $TARGET_PATH
  sh ./prepare.sh; sh ./update.sh -nogc 256; sh ./invoke.sh
}

invoke_close_warmup() {
  SOURCE_PATH=`pwd`
  TARGET_PATH=$SOURCE_PATH/../../application/$1/$2/scripts
  i=0
  while [ $i -le $3 ]
  do
    echo "warmup: $i"
    sh $TARGET_PATH/invoke.sh > /dev/null
    i=$(($i+1))
  done
}

warmup() {
  CUR_DIR=`pwd`
  prepare_warmup java time
  # warm up
  i=0
  while [ $i -lt 10 ]
  do
    cd $CUR_DIR
    invoke_close_warmup java time 100 &
    i=$(($i+1))
  done
  wait
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

prepare() {
  TARGET_PATH=../../application/$1/$2/scripts
  cd $TARGET_PATH
  sh ./prepare.sh; sh ./update-idx.sh -nogc 256 $3; sh ./invoke-idx.sh $3
}

run_test() {
  CUR_DIR=`pwd`

  CON_COUNT=$1
  TEST_TYPE=$2

  echo "testing for $TEST_TYPE: $CON_COUNT"

  mkdir -p ./result/$TEST_TYPE/result-$CON_COUNT
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

  if [ $TEST_TYPE = "desiccant" ]; then
    echo "concurrent invocation done, now reclaim"
    wsk action invoke ZZMReclaimAll -i --result
    sleep 3
  fi

  i=0
  for pid in $(pgrep -f "node --expose-gc --trace_gc_verbose"); do
    sudo pmap -XX $pid > "./result/$TEST_TYPE/result-$CON_COUNT/$i.txt"
    i=$((i+1))
  done

  wsk action invoke ZZMRemoveAll -i --result
}


start_openwhisk vanilla
prepare_reclaim
warmup
run_test 1 vanilla
run_test 2 vanilla
run_test 4 vanilla
run_test 8 vanilla
run_test 16 vanilla
stop_openwhisk


start_openwhisk desiccant
prepare_reclaim
warmup
run_test 1 desiccant
run_test 2 desiccant
run_test 4 desiccant
run_test 8 desiccant
run_test 16 desiccant
stop_openwhisk
