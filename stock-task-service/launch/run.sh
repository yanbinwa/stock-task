#!/bin/sh

SERVICE_NAME=stock-task-service
BUNDLE_VER=0.0.1-SNAPSHOT

java -Xms512m -Xmx6g \
     -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled \
     -XX:MaxMetaspaceSize=6g \
     -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 \
     -XX:+ScavengeBeforeFullGC -XX:+CMSScavengeBeforeRemark \
     -jar ${SERVICE_NAME}-${BUNDLE_VER}.jar