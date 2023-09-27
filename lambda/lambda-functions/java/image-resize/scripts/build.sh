cd ..
if [ $# -gt 0 ]; then
        echo "Set IP ADDR $1"
        replacement="public static String IP_ADDR = \"$1\";"
        sed -i '25s/.*/'"$replacement"'/' src/main/java/org/ipads/AwtThumbnail.java
fi

mvn package -Dmaven.test.skip=true -Dmaven.compiler.fork=true -Dmaven.compiler.executable=/home/ubuntu/j2sdk-image/bin/javac

