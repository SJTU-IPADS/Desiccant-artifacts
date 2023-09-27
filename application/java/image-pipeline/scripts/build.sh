cd ..
if [ $# -gt 0 ]; then
        echo "Set IP ADDR $1"
        replacement="public static String IP_ADDR = \"$1\";"
        sed -i '10s/.*/'"$replacement"'/' image-recognize-commons/src/main/java/org/ipads/ImageRecognizationCommons.java
fi

mvn clean package
