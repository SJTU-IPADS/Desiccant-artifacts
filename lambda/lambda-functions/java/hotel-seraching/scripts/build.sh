cd ..
mvn install:install-file -Dfile=./lib/jsi-1.1.0.jar -DgroupId=net.sf.jsi -DartifactId=jsi -Dversion=1.1.0 -Dpackaging=jar
if [ $# -gt 0 ]; then
        echo "Set IP ADDR $1"
        replacement="public static String IP_ADDR = \"$1\";"
        sed -i '3s/.*/'"$replacement"'/' src/main/java/com/example/HotelCommon.java
fi
mvn package -Dmaven.test.skip=true -Dmaven.compiler.fork=true -Dmaven.compiler.executable=/home/ubuntu/j2sdk-image/bin/javac
