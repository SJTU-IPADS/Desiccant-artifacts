source ./local.env
couchdb_url=http://$COUCHDB_USERNAME:$COUCHDB_PASSWORD@$COUCHDB_IP:$COUCHDB_PORT

SCRIPTS_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
SRC_DIR=$SCRIPTS_DIR/../src

# frontend
cd $SRC_DIR/frontend
# install dependencies -- making this script need sudo to execute
npm install
# package handler with dependencies
zip -rq index.zip *
# create/update action
wsk -i action update alexa-frontend index.zip -a provide-api-key true --docker=zzm-whisk/openwhisk-runner-nodejs:14-simlambda$1$2 --memory $2

# interact
cd $SRC_DIR/interact
# install dependencies -- making this script need sudo to execute
npm install
# package handler with dependencies
zip -rq index.zip * ../infra
# create/update action
wsk -i action update alexa-interact index.zip -a provide-api-key true --docker=zzm-whisk/openwhisk-runner-nodejs:14-simlambda$1$2 --memory $2

# fact
cd $SRC_DIR/fact
# install dependencies -- making this script need sudo to execute
npm install
# package handler with dependencies
zip -rq index.zip index.js handler.js node_modules ../infra/language.js
# create/update action
wsk -i action update alexa-fact index.zip --docker=zzm-whisk/openwhisk-runner-nodejs:14-simlambda$1$2 --memory $2

# reminder
cd $SRC_DIR/reminder
# install dependencies -- making this script need sudo to execute
npm install
# package handler with dependencies
zip -rq index.zip index.js handler.js node_modules ../infra/language.js
# create/update action
wsk -i action update alexa-reminder index.zip --docker=zzm-whisk/openwhisk-runner-nodejs:14-simlambda$1$2 --memory $2 \
  --param COUCHDB_URL $couchdb_url \
  --param DATABASE $ALEXA_REMINDER_COUCHDB_DATABASE

# smarthome
cd $SRC_DIR/smarthome

# install dependencies for devices
rm -rf node_modules
cp package-device.json package.json
npm install

# door controller
cp door-index.js index.js
# package handler with dependencies
zip -rq door.zip index.js door-handler.js net.js node_modules ../infra/language.js
# create/update action
wsk -i action update alexa-home-door door.zip --docker=zzm-whisk/openwhisk-runner-nodejs:14-simlambda$1$2 --memory $2 \
  --param IP "$ALEXA_SMARTHOME_IP" \
  --param PORT "$ALEXA_SMARTHOME_PORT_DOOR"


# light controller
cp light-index.js index.js
# package handler with dependencies
zip -rq light.zip index.js light-handler.js net.js node_modules ../infra/language.js
# create/update action
wsk -i action update alexa-home-light light.zip --docker=zzm-whisk/openwhisk-runner-nodejs:14-simlambda$1$2 --memory $2 \
  --param IP "$ALEXA_SMARTHOME_IP" \
  --param PORT "$ALEXA_SMARTHOME_PORT_LIGHT"

# TV controller
cp tv-index.js index.js
# package handler with dependencies
zip -rq tv.zip index.js tv-handler.js net.js node_modules ../infra/language.js
# create/update action
wsk -i action update alexa-home-tv tv.zip --docker=zzm-whisk/openwhisk-runner-nodejs:14-simlambda$1$2 --memory $2 \
  --param IP "$ALEXA_SMARTHOME_IP" \
  --param PORT "$ALEXA_SMARTHOME_PORT_TV"

# air-conditioning controller
cp air-conditioning-index.js index.js
# package handler with dependencies
zip -rq air-conditioning.zip index.js air-conditioning-handler.js net.js node_modules ../infra/language.js
# create/update action
wsk -i action update alexa-home-air-conditioning air-conditioning.zip --docker=zzm-whisk/openwhisk-runner-nodejs:14-simlambda$1$2 --memory $2 \
  --param IP "$ALEXA_SMARTHOME_IP" \
  --param PORT "$ALEXA_SMARTHOME_PORT_AIR_CONDITIONING"

# plug controller
cp plug-index.js index.js
# package handler with dependencies
zip -rq plug.zip index.js plug-handler.js net.js node_modules  ../infra/language.js
# create/update action
wsk -i action update alexa-home-plug plug.zip --docker=zzm-whisk/openwhisk-runner-nodejs:14-simlambda$1$2 --memory $2 \
  --param IP "$ALEXA_SMARTHOME_IP" \
  --param PORT "$ALEXA_SMARTHOME_PORT_PLUG"


# install dependencies for smarthome entry
rm -rf node_modules
cp package-smarthome.json package.json
npm install

# smarthome entry
cp smarthome-index.js index.js
# package handler with dependencies
zip -rq smarthome.zip index.js smarthome-handler.js node_modules ../infra ./en-US.json
# create/update action
wsk -i action update alexa-smarthome smarthome.zip -a provide-api-key true --docker=zzm-whisk/openwhisk-runner-nodejs:14-simlambda$1$2 --memory $2 \
  --param SMARTHOME_PASSWORD "$ALEXA_SMARTHOME_PASSWORD"
