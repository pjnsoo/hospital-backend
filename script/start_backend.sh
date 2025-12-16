#!/bin/bash

ulimit -n 65536

export PROCESS_NAME=hospital-backend

echo PROCESS_NAME: ["$PROCESS_NAME"]
MAIN_CLASS=org.hospital.BackendServer

echo PROJECT_HOME: ["$PROJECT_HOME"]

cd $PROJECT_HOME
. $PROJECT_HOME/bin/env.sh

export LOG_FILE=${PROCESS_NAME}
echo LOG_PATH:["${LOG_PATH}/${LOG_FILE}.log"]


PROCESS_KEYWORD="\-Dsid\=${PROCESS_NAME}"
PID=$(pgrep -f -a "${PROCESS_KEYWORD}"|grep -v "grep\|tail"|awk '{print $1}')
if [ -n "${PID}" ]; then
  echo "Error: ${PROCESS_NAME} is already running (PID: ${PID})"
  exit 1
fi

JVM_OPTS="-XX:+HeapDumpOnOutOfMemoryError -XX:+UseG1GC -Xmx1024m"
JVM_CMD="$JRE $JVM_OPTS -Dhostname=${HOSTNAME} -DprocessName=${PROCESS_NAME} -classpath $CLASSPATH $MAIN_CLASS"

echo "$JVM_CMD"

nohup $JVM_CMD > /dev/null 2>&1 &
echo "Started process: [${PROCESS_NAME}]"