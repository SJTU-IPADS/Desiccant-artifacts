#wc -l ~/tmp/openwhisk/invoker/logs/invoker-local_logs.log
#wsk action invoke reservation -i --result --param HotelId 1 --param InDate 2020-02-26 --param OutDate 2020-02-28 --param Number 1 --param CustomerName Bob
#wsk action invoke checkuser -i --result --param Username SJTU_0 --param Password sjtuuser0
#wsk action invoke getprofiles -i --result --param-file hotels.json
#wsk action invoke getrates -i --result --param-file hotels.json
#wsk action invoke nearby -i --result --param Lat 37.7863 --param Lon -122.4015
cd ..
wsk action invoke searchnearbysequence -i --result --param thread 1 --param Lat 37.7863 --param Lon -122.4015 
#wsk action invoke recommendation -i --result --param Require dis --param Lat 37.7863 --param Lon -122.4015
#wsk action invoke recommendation -i --result --param Require rate 
#wsk action invoke recommendation -i --result --param Require price
