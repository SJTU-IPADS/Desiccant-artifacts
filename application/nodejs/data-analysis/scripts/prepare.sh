source ./local.env

couchdb_url=http://$COUCHDB_USERNAME:$COUCHDB_PASSWORD@$COUCHDB_IP:$COUCHDB_PORT
SCRIPTS_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
SRC_DIR=$SCRIPTS_DIR/../src

curl -X DELETE $couchdb_url/$WAGE_COUCHDB_DATABASE
curl -X DELETE $couchdb_url/$WAGE_COUCHDB_DATABASE_STATISTICS
curl -X PUT $couchdb_url/$WAGE_COUCHDB_DATABASE
curl -X PUT $couchdb_url/$WAGE_COUCHDB_DATABASE_STATISTICS





if [ ! -f $SCRIPTS_DIR/records.json ]; then
  echo "Creating initial records to post to couchdb..."
  $SCRIPTS_DIR/records_generator.sh 100
fi


curl -H 'Content-Type: application/json' \
       -X POST $couchdb_url/$WAGE_COUCHDB_DATABASE/_bulk_docs \
       --data "@$SCRIPTS_DIR/records.json"


cp ./param.json ~/tmp/
