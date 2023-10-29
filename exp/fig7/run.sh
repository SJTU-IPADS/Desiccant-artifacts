prepare_reclaim() {
  SCRIPT_ROOT=`pwd`
  TARGET_PATH=../../application/java/time/scripts
  cd $TARGET_PATH
  wsk action update ZZMReclaimAll ../target/ntp-function.jar --main org.ipads.NTPFunction --docker zzm-whisk/openwhisk-runner-java:openjdk8-simlambda-nogc256 --memory 256
  wsk action update ZZMRemoveAll ../target/ntp-function.jar --main org.ipads.NTPFunction --docker zzm-whisk/openwhisk-runner-java:openjdk8-simlambda-nogc256 --memory 256
  cd $SCRIPT_ROOT
}

# usage: run_single_app cnt app vanilla/eager/desiccant mem java/nodejs
run_single_app() {
  i=1

  if [ $3 = "eager" ]; then
    GC_TYPE="gc"
  else
    GC_TYPE="nogc"
  fi

  if [ $5 = "java" ]; then
    APP_DIR_NAME="java"
    APP_PID_NAME="java"
  else
    APP_DIR_NAME="nodejs"
    APP_PID_NAME="node"
  fi

  # invoke first to make sure are shared library are calculated in the shared_clean part
  SCRIPT_ROOT=`pwd`
  echo $SCRIPT_ROOT
  cd ../../application/$APP_DIR_NAME/$2/scripts/
  sh prepare.sh
  sh update.sh -$GC_TYPE $4

  cd $SCRIPT_ROOT
  sh ../../application/$APP_DIR_NAME/$2/scripts/invoke.sh
  DOCKER_ID=`docker ps -n 1 -q | tail -n 1`
  RUNTIME_PID=`docker top $DOCKER_ID | grep $APP_PID_NAME | awk -F ' ' {'print $2'} | grep -v PID`

  mkdir -p result/$2-$3

  while [ $i -le $1 ];
  do
    echo "$3:$2: $i/$1"
    sudo pmap -XX $RUNTIME_PID > result/$2-$3/pmap-$i.txt
    sh ../../application/$APP_DIR_NAME/$2/scripts/invoke.sh >> result/$2-$3/invoke-logs.txt
    i=$((i+1))
  done
  if [ $3 = "desiccant" ]; then
    wsk action invoke ZZMReclaimAll -i --result
    sleep 3
    sudo pmap -XX $RUNTIME_PID > result/$2-$3/pmap-$1.txt
  fi
  sleep 5
  wsk action invoke ZZMRemoveAll -i --result
}

# usage: run_chain_app cnt app chain-hip vanilla/eager/desiccant mem java/nodejs
run_chain_app() {
  i=1

  if [ $4 = "eager" ]; then
    GC_TYPE="gc"
  else
    GC_TYPE="nogc"
  fi

  if [ $6 = "java" ]; then
    APP_DIR_NAME="java"
    APP_PID_NAME="java"
  else
    APP_DIR_NAME="nodejs"
    APP_PID_NAME="node"
  fi

  # invoke first to make sure are shared library are calculated in the shared_clean part
  SCRIPT_ROOT=`pwd`
  cd ../../application/$APP_DIR_NAME/$2/scripts/
  sh prepare.sh
  sh update.sh -$GC_TYPE $5

  cd $SCRIPT_ROOT
  sh ../../application/$APP_DIR_NAME/$2/scripts/invoke.sh

  DOCKER_IDS=`docker ps -n $3 -q | tail -n $3`
  RUNTIME_PIDS=()
  for DOCKER_ID in $DOCKER_IDS
  do
    RUNTIME_PID=`docker top $DOCKER_ID | grep $APP_PID_NAME | awk -F ' ' {'print $2'} | grep -v PID`
    RUNTIME_PIDS+=($RUNTIME_PID)
  done

  j=0
  while [ $j -lt $3 ];
  do
    mkdir -p result/$2-$4/$j/
    j=$((j+1))
  done

  while [ $i -le $1 ];
  do
    echo "$4:$2: $i/$1"
    j=0
    while [ $j -lt $3 ];
    do
      RUNTIME_PID=${RUNTIME_PIDS[j]}
      sudo pmap -XXX $RUNTIME_PID > result/$2-$4/$j/pmap-$i.txt
      j=$((j+1))
    done
    sh ../../application/$APP_DIR_NAME/$2/scripts/invoke.sh >> result/$2-$4/invoke-logs.txt
    i=$((i+1))
  done
  if [ $4 = "desiccant" ]; then
    wsk action invoke ZZMReclaimAll -i --result
    sleep 3
    j=0
    while [ $j -lt $3 ];
    do
      RUNTIME_PID=${RUNTIME_PIDS[j]}
      sudo pmap -XXX $RUNTIME_PID > result/$2-$4/$j/pmap-$1.txt
      j=$((j+1))
    done
  fi
  sleep 5
  wsk action invoke ZZMRemoveAll -i --result

}

start_background_containers() {
  docker run -itd --rm --name java-1 zzm-whisk/openwhisk-runner-java:openjdk8-simlambda-nogc256
  docker run -itd --rm --name java-2 zzm-whisk/openwhisk-runner-java:openjdk8-simlambda-imagemagic-nogc256
  docker run -itd --rm --name java-3 zzm-whisk/openwhisk-runner-java:openjdk8-simlambda-gc256
  docker run -itd --rm --name java-4 zzm-whisk/openwhisk-runner-java:openjdk8-simlambda-imagemagic-gc256

  docker run -itd --rm --name js-1 zzm-whisk/openwhisk-runner-nodejs:14-simlambda-nogc256
  docker run -itd --rm --name js-2 zzm-whisk/openwhisk-runner-nodejs:14-simlambda-gc256
}

start_openwhisk() {
  SCRIPT_ROOT=`pwd`
  cd ../../openwhisk-docker-compose/
  sh run.sh $1
  start_background_containers
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


languages=(java java java java java java java nodejs nodejs nodejs nodejs nodejs nodejs nodejs nodejs nodejs nodejs nodejs nodejs)
apps=(time sort file-hash image-resize hotel-seraching image-pipeline mapreduce clock dynamic-html factor fft fibonacci filesystem matrix pi unionfind web-server data-analysis alexa)
chain_cnts=(1 1 1 1 3 4 2 1 1 1 1 1 1 1 1 1 1 6 8)

echo "=====begin testing vanilla====="
start_openwhisk vanilla
prepare_reclaim
for i in $(seq 0 `expr ${#apps[@]} - 1`); do
  LANGUAGE=${languages[i]}
  APP=${apps[i]}
  CHAIN_CNT=${chain_cnts[i]}
  if [ $CHAIN_CNT -eq 1 ]; then
    run_single_app 100 $APP vanilla 256 $LANGUAGE
  else
    run_chain_app 100 $APP $CHAIN_CNT vanilla 256 $LANGUAGE
  fi
done
stop_openwhisk
wait

echo "=====begin testing eager gc====="
start_openwhisk vanilla
prepare_reclaim
for i in $(seq 0 `expr ${#apps[@]} - 1`); do
  LANGUAGE=${languages[i]}
  APP=${apps[i]}
  CHAIN_CNT=${chain_cnts[i]}
  if [ $CHAIN_CNT -eq 1 ]; then
    run_single_app 100 $APP eager 256 $LANGUAGE
  else
    run_chain_app 100 $APP $CHAIN_CNT eager 256 $LANGUAGE
  fi
done
stop_openwhisk
wait

echo "=====begin testing desiccant====="
start_openwhisk desiccant
prepare_reclaim
for i in $(seq 0 `expr ${#apps[@]} - 1`); do
  LANGUAGE=${languages[i]}
  APP=${apps[i]}
  CHAIN_CNT=${chain_cnts[i]}
  if [ $CHAIN_CNT -eq 1 ]; then
    run_single_app 100 $APP desiccant 256 $LANGUAGE
  else
    run_chain_app 100 $APP $CHAIN_CNT desiccant 256 $LANGUAGE
  fi
done
stop_openwhisk
wait
