#!/bin/bash
sleep 0.1

pid_list=$(pgrep -f "java -XX:MaxHeapSize=222823k")

if [ $(echo $pid_list | wc -w) -ne 1 ]; then
  echo "error"
else
  pmap_output=$(pmap -XX $pid_list)

if [ $# -eq 1 ]; then
  echo $pmap_output
else
  last_line=$(echo "$pmap_output" | tail -n 1)
  echo $last_line
fi

fi

