#!/usr/bin/env bash

PROJECT_ROOT="/home/ubuntu/Floney-Server"
JAR_FILE="$PROJECT_ROOT/build/libs/Floney-0.0.1-SNAPSHOT.jar"

APP_LOG="$PROJECT_ROOT/application.log"
ERROR_LOG="$PROJECT_ROOT/deploy-error.log"
DEPLOY_LOG="$PROJECT_ROOT/deploy.log"

TIME_NOW=$(date +%c)

echo "[ $TIME_NOW ] Copy file $JAR_FILE to project root" >> $DEPLOY_LOG
cp $PROJECT_ROOT/build/libs/*.jar $JAR_FILE

echo "[ $TIME_NOW ] Run java application : $JAR_FILE" >> $DEPLOY_LOG
nohup java -jar $JAR_FILE —spring.profiles.active=develop > $APP_LOG 2> $ERROR_LOG &

CURRENT_PID=$(pgrep -f $JAR_FILE)
echo "[ $TIME_NOW ] Application running PID : $CURRENT_PID" >> $DEPLOY_LOG
