cd ../application/

APP_ROOT_DIR=`pwd`

if [ $# -ne 1 ]; then
    echo "Usage: $0 <ip_address>"
    exit 1
fi

echo "====set ip address to $1===="

cd $APP_ROOT_DIR/nodejs/
replacement="const ip_addr = '$1';"
sed -i '3s/.*/'"$replacement"'/' thumbnailer/src/index.js
replacement="LOCAL_HOST=$1"
sed -i '1s/.*/'"$replacement"'/' alexa/scripts/local.env
sed -i '1s/.*/'"$replacement"'/' data-analysis/scripts/local.env
param="{\"url\":\"http://whisk_admin:some_passw0rd@$1:5984\",\"username\":\"whisk_admin\",\"host\":\"$1\",\"dbname\":\"wage\",\"id\":\"id100\",\"password\":\"some_passw0rd\"}"
echo $param > data-analysis/data-analysis-param.json

cd $APP_ROOT_DIR

echo "====build apps===="

languages=(java java java java java java java nodejs nodejs nodejs nodejs nodejs nodejs nodejs nodejs nodejs nodejs nodejs nodejs)
apps=(time sort file-hash image-resize hotel-seraching image-pipeline mapreduce clock dynamic-html factor fft fibonacci filesystem matrix pi unionfind web-server data-analysis alexa)

for i in $(seq 0 `expr ${#apps[@]} - 1`); do
  LANGUAGE=${languages[i]}
  APP=${apps[i]}
  echo "====build $LANGUAGE app: $APP===="
  cd $APP_ROOT_DIR/$LANGUAGE/$APP/scripts
  sh build.sh $1
done
