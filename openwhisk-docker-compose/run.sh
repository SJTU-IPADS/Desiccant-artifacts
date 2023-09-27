cp ./.wskprops ~
cp docker-compose-$1.yml docker-compose.yml
make destroy; make run
