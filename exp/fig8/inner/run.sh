WORK_DIR=`pwd`
cd ../../openwhisk-docker-compose/
sh run.sh vanilla
cd $WORK_DIR
rm -rf result-*
sh warmup.sh
sh run-vanilla.sh 1
sh run-vanilla.sh 2
sh run-vanilla.sh 4
sh run-vanilla.sh 8
sh run-vanilla.sh 16
sh remove.sh
python3 parse-result-merged.py | tee vanilla-result.txt
cd ../../openwhisk-docker-compose/
sh stop.sh
sh run.sh desiccant
cd $WORK_DIR
rm -rf result-*
sh warmup.sh
sh run-desiccant.sh 1
sh run-desiccant.sh 2
sh run-desiccant.sh 4
sh run-desiccant.sh 8
sh run-desiccant.sh 16
sh remove.sh
python3 parse-result-merged.py | tee desiccant-result.txt
cd ../../openwhisk-docker-compose/
sh stop.sh


