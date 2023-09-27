#wsk action invoke image-resize-function -i --result --param imageName test.jpg
aws lambda invoke --function-name zzmae-java-image-resize --payload '{"mainClass": "org.ipads.AwtThumbnail", "args": {"imageName":"test.jpg"}}' --cli-binary-format raw-in-base64-out output.json

