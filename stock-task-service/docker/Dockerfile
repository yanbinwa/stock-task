FROM harbor.emotibot.com/image-base/alpine-jdk:v1.8

# Set TimeZone
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# set bundle version
ENV BUNDLE_VER=0.0.1-SNAPSHOT
ENV SERVICE_NAME=stock-task-service

# setup workspace
WORKDIR /usr/src/${SERVICE_NAME}

# copy the project to /usr/src/emotibot-function2
COPY launch ./bin
COPY docker ./docker

# Download main jar
COPY target/${SERVICE_NAME}-${BUNDLE_VER}.jar ./

# defaults
ENTRYPOINT ["bin/run.sh"]