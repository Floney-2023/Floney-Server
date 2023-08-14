#!/usr/bin/env bash

ROOT_PATH="/home/ubuntu/Floney-Server"
JAR_FILE="$ROOT_PATH/build/libs/Floney-0.0.1-SNAPSHOT.jar"

DEPLOY_LOG="$PROJECT_ROOT/deploy.log"

SERVICE_PID=$(pgrep -f $JAR_FILE) # 실행중인 Spring 서버의 PID

TIME_NOW=$(date +%c)

CURRENT_PID=$(pgrep -f $JAR_FILE)

if [ -z $CURRENT_PID ]; then
  echo "[ $TIME_NOW ] Running application not found." >> $DEPLOY_LOG
else
  echo "[ $TIME_NOW ] Terminate application PID : $CURRENT_PID" >> $DEPLOY_LOG
  kill -15 $CURRENT_PID
fi
