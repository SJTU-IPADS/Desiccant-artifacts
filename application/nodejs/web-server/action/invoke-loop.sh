# usage sh invoke-close-debug.sh cnt app
i=0
while [ $i -lt $1 ];
do
    sh ./invoke.sh
    sudo pmap -x $2 | tail -n 1 >> memlog.txt
    i=$((i+1))
done
