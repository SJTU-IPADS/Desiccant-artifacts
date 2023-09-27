source ./local.env

SCRIPTS_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
SRC_DIR=$SCRIPTS_DIR/../src


docker build -t smartdevice $SRC_DIR/smarthome/device

if [[ ! $(docker ps | grep smartdevice) ]]; then
    docker run -p $ALEXA_SMARTHOME_PORT_DOOR:8080 -e DEVICE_NAME=door -d --rm --name door smartdevice
    docker run -p $ALEXA_SMARTHOME_PORT_LIGHT:8080 -e DEVICE_NAME=light -d --rm --name light smartdevice
    docker run -p $ALEXA_SMARTHOME_PORT_TV:8080 -e DEVICE_NAME=tv -d --rm --name tv smartdevice
    docker run -p $ALEXA_SMARTHOME_PORT_AIR_CONDITIONING:8080 -e DEVICE_NAME=air-conditioning -d --rm --name air-conditioning smartdevice
    docker run -p $ALEXA_SMARTHOME_PORT_PLUG:8080 -e DEVICE_NAME=plug -d --rm --name plug smartdevice
fi

