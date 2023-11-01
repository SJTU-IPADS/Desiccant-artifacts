prepare() {
  TARGET_PATH=../../application/$1/$2/scripts
  cd $TARGET_PATH
  sh ./prepare.sh; sh ./update.sh -nogc 256; sh ./invoke.sh
}

warm_up() {
  TARGET_PATH=../../application/$1/$2/scripts
  cd $TARGET_PATH
  i=0
  while [ $i -le 100 ]
  do
    sh ./invoke.sh
    i=$(($i+1))
  done
}

execute_trace() {
  mkdir -p $1
  mkdir ./result

  languages=(java java java java java java java nodejs nodejs nodejs nodejs nodejs nodejs nodejs nodejs nodejs nodejs nodejs nodejs)
  apps=(time sort file-hash image-resize hotel-seraching image-pipeline mapreduce clock dynamic-html factor fft fibonacci filesystem matrix pi unionfind web-server data-analysis alexa)

  CUR_DIR=`pwd`

  prepare java time
  cd $CUR_DIR
  i=0
  while [ $i -lt 10 ]
  do
    warm_up java time &
    i=$(($i+1))
  done


  for i in $(seq 0 `expr ${#apps[@]} - 1`); do
      _a=${languages[i]}
      _b=${apps[i]}
      prepare $_a $_b &
  done

  docker run -itd --name pc1 zzm-whisk/openwhisk-runner-java:openjdk8-simlambda-nogc256
  docker run -itd --name pc2 zzm-whisk/openwhisk-runner-java:openjdk8-simlambda-imagemagic-nogc256
  docker run -itd --name pc3 zzm-whisk/openwhisk-runner-nodejs:14-simlambda-nogc256

  wait
  python3 inner/run-azure-test.py  $2 &
  wait
  killall sar
  trunc_cnt=`cat ./linecnt.txt`
  tail -n +$trunc_cnt ~/tmp/openwhisk/invoker/logs/invoker-local_logs.log  | grep evaluation > ./result/coldrate.log
  tail -n +$trunc_cnt ~/tmp/openwhisk/invoker/logs/invoker-local_logs.log  | grep evict > ./result/evict.log
  tail -n +$trunc_cnt ~/tmp/openwhisk/invoker/logs/invoker-local_logs.log  > ./result/invoker.log
  cp ~/tmp/openwhisk/invoker/logs/invoker-local_logs.log ./result/
  mv ./cpu.txt ./result/cpu.txt
  killall wsk
  rm -rf ./$1/result-$2
  mv ./result ./$1/result-$2
  cd $CUR_DIR
}

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

run_one_test() {
  start_openwhisk $1
  execute_trace $1 $2
  stop_openwhisk
}


run_one_test vanilla 15
run_one_test vanilla 20
run_one_test vanilla 25
run_one_test vanilla 30

run_one_test desiccant 15
run_one_test desiccant 20
run_one_test desiccant 25
run_one_test desiccant 30
run_one_test desiccant 35
