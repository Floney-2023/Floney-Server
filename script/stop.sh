#!/usr/bin/env bash

ROOT_PATH="/home/ubuntu/Floney-Server"
DEPLOY_LOG="$ROOT_PATH/deploy.log"

SERVICE_PID=$(pgrep -f Floney-Server)

TIME_NOW=$(date +%c)

if [ -z "$SERVICE_PID" ]; then
  echo "[ $TIME_NOW ] Running application not found." >> $DEPLOY_LOG
else
  echo "[ $TIME_NOW ] Terminate application PID : $SERVICE_PID" >> $DEPLOY_LOG
  kill -15 "$SERVICE_PID"
  sleep 5
fi
