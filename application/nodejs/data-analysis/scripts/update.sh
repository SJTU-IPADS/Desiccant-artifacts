source ./local.env

couchdb_url=http://$COUCHDB_USERNAME:$COUCHDB_PASSWORD@$COUCHDB_IP:$COUCHDB_PORT
SCRIPTS_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
SRC_DIR=$SCRIPTS_DIR/../src

cd $SRC_DIR

cp wage-package.json package.json
npm install

cp wage-read-document.js index.js
zip -rq wage-read-document.zip index.js node_modules
wsk -i action update wage-read-document wage-read-document.zip --docker=zzm-whisk/openwhisk-runner-nodejs:14-simlambda$1$2 --memory $2 \
    --param COUCHDB_URL "$couchdb_url" \
    --param COUCHDB_DATABASE "$WAGE_COUCHDB_DATABASE"

cp wage-fillup.js index.js
zip -rq wage-fillup.zip index.js node_modules
wsk -i action update wage-fillup wage-fillup.zip --docker=zzm-whisk/openwhisk-runner-nodejs:14-simlambda$1$2 --memory $2 \
    --param COUCHDB_URL "$couchdb_url" \
    --param COUCHDB_DATABASE "$WAGE_COUCHDB_DATABASE"

wsk -i action update wage-analysis-total wage-analysis-total.js --docker=zzm-whisk/openwhisk-runner-nodejs:14-simlambda$1$2 --memory $2

cp wage-analysis-realpay.js index.js
zip -rq wage-analysis-realpay.zip index.js constances.js
wsk -i action update wage-analysis-realpay wage-analysis-realpay.zip --docker=zzm-whisk/openwhisk-runner-nodejs:14-simlambda$1$2 --memory $2

cp wage-analysis-merit-percent.js index.js
zip -rq wage-analysis-merit-percent.zip index.js constances.js
wsk -i action update wage-analysis-merit-percent wage-analysis-merit-percent.zip --docker=zzm-whisk/openwhisk-runner-nodejs:14-simlambda$1$2 --memory $2

cp wage-analysis-result.js index.js
zip -rq wage-analysis-result.zip index.js node_modules
wsk -i action update wage-analysis-result wage-analysis-result.zip --docker=zzm-whisk/openwhisk-runner-nodejs:14-simlambda$1$2 --memory $2 \
    --param COUCHDB_URL "$couchdb_url" \
    --param COUCHDB_DATABASE_STATISTICS "$WAGE_COUCHDB_DATABASE_STATISTICS"

echo "Creating sequence that ties database read to handling action"
wsk -i action update wage-analysis-sequence \
  --sequence wage-read-document,wage-fillup,wage-analysis-total,wage-analysis-realpay,wage-analysis-merit-percent,wage-analysis-result


