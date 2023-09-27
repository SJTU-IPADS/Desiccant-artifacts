cd ..
if [ $# -gt 0 ]; then
        echo "Set IP ADDR $1"
        replacement="public static String IP_ADDR = \"$1\";"
        sed -i '16s/.*/'"$replacement"'/' src/main/java/org/ipads/RedisUtil.java
fi
mvn clean package
