#!/bin/bash

JAR_PATH="/home/ubuntu/Debug/emotion/build/libs"
JAR_FILE="emotion-0.0.1-SNAPSHOT.jar"

# 프로세스를 종료합니다.
PID=$(pgrep -f $JAR_FILE)
if [ -n "$PID" ]; then
  echo "이미 실행 중인 프로세스를 종료합니다. PID: $PID"
  kill $PID
fi

cd $JAR_PATH

# JAR 파일을 실행합니다.
java -jar $JAR_FILE
