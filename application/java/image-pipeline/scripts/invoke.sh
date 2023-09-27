#wc -l ~/tmp/openwhisk/invoker/logs/invoker-local_logs.log
wsk action invoke imageRecognitionSequence -i --result --param imageName test.jpg
