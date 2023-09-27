sudo lsof -i -P -n  | grep 13579
sudo taskset -c 0-19 ./pmap-server > /dev/null
