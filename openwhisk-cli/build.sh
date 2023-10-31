go mod download
go get -u github.com/jteeuwen/go-bindata/...
~/go/bin/go-bindata -pkg wski18n -o wski18n/i18n_resources.go wski18n/resources 
#go mod edit -go='1.13' -replace='github.com/apache/openwhisk-client-go/whisk'='../openwhisk-client-go/whisk'
go build -o wsk
