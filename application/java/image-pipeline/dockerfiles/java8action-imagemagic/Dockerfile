from openwhisk/java8action

RUN apt-get update \
        && apt-get install -y imagemagick-6.q16\
        && ln -s /usr/lib/x86_64-linux-gnu/ImageMagick-6.8.9/bin-Q16/identify /usr/bin/identify\
        && ln -s /usr/lib/x86_64-linux-gnu/ImageMagick-6.8.9/bin-Q16/convert /usr/bin/convert
