#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
BUILDROOT=$DIR/../
REPO=harbor.emotibot.com/stock
cd ${BUILDROOT}

# 从pom.xml的finalName获取项目名称，失败则从mvn clean的打印信息中抽取
getName(){
    [ -f pom.xml ] && {
       CONTAINER=`awk '/<finalName>[^<]+<\/finalName>/ { \
            gsub(/<finalName>|<\/finalName>/,"",$1); \
            print $1; \
            exit;}' pom.xml`
    }
    [ -z $CONTAINER ] && {
       CONTAINER=`mvn clean -DskipTests|awk '$2=="Building" {print $3;exit;}'`
    }
}


CONTAINER="" && getName
# 打包
mvn clean package -DskipTests
GIT_HEAD="$(git rev-parse --short=7 HEAD)"
GIT_DATE=$(git log HEAD -n1 --pretty='format:%cd' --date=format:'%Y%m%d')
TAG="${GIT_HEAD}-${GIT_DATE}"
DOCKER_IMAGE=$REPO/$CONTAINER:$TAG

echo $DOCKER_IMAGE

cmd="docker build \
        --no-cache -t $DOCKER_IMAGE \
        -f $DIR/Dockerfile \
        $BUILDROOT"
echo $cmd
eval $cmd
