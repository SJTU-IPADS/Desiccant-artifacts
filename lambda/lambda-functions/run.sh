#usage run_single count language app vanilla/desiccant
run_single() {
  if [ $4 = "desiccant" ]; then
    SCRIPT_SUFFIX="-reclaim"
  else
    SCRIPT_SUFFIX=""
  fi

  CUR_DIR=`pwd`
  sh ./$2/$3/scripts/update$SCRIPT_SUFFIX.sh
  sleep 3
  i=1
  while [ $i -le $1 ];
  do
    echo "$4:$2: $i/$1"
    sh ./$2/$3/scripts/invoke$SCRIPT_SUFFIX.sh
    cat output.json >> $CUR_DIR/result/$4/$2-$3.log
    i=$((i+1))
  done

  if [ $4 = "desiccant" ]; then
    sh ./$2/$3/scripts/reclaim.sh
    cat output.json >> $CUR_DIR/result/$4/$2-$3.log
  fi
}

#usage run_single count language app chain_cnt vanilla/desiccant
run_chain() {
  CUR_DIR=`pwd`

  if [ $5 = "desiccant" ]; then
    SCRIPT_SUFFIX="-reclaim"
  else
    SCRIPT_SUFFIX=""
  fi

  sh ./$2/$3/scripts/update$SCRIPT_SUFFIX.sh

  cd ./$2/$3/scripts/
  i=1
  while [ $i -le $1 ];
  do
    echo "$5:$2: $i/$1"
    j=1
    while [ $j -le $4 ];
    do
      sh invoke$SCRIPT_SUFFIX-$j.sh -$3
      cat output.json >> $CUR_DIR/result/$5/$2-$3-$j.log
      j=$((j+1))
    done
    i=$((i+1))
  done

  if [ $5 = "desiccant" ]; then
    j=1
    while [ $j -le $4 ];
    do
      sh ./reclaim-$j.sh
      cat output.json >> $CUR_DIR/result/$5/$2-$3-$j.log
      j=$((j+1))
    done
  fi
  cd $CUR_DIR
}

run_test() {
  languages=(java java java java java java)
  apps=(time sort file-hash image-resize hotel-seraching mapreduce)
  chain_cnts=(1 1 1 1 3 2)
  TYPE=$1

  count=100

  rm -rf ./result/$TYPE
  mkdir -p ./result/$TYPE

  for i in $(seq 0 `expr ${#apps[@]} - 1`); do
    LANGUAGE=${languages[i]}
    APP=${apps[i]}
    CHAIN_CNT=${chain_cnts[i]}
    if [ $CHAIN_CNT -eq 1 ]; then
      run_single $count $LANGUAGE $APP $TYPE
    else
      run_chain $count $LANGUAGE $APP $CHAIN_CNT $TYPE
    fi
  done
}

run_test vanilla
run_test desiccant
