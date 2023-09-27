# Usage ./create-db.sh <db-name>
curl -X DELETE http://whisk_admin:some_passw0rd@127.0.0.1:5984/$1
curl -X PUT http://whisk_admin:some_passw0rd@127.0.0.1:5984/$1
