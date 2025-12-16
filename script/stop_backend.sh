#!/bin/bash

PROCESS_NAME=hospital-backend
PROCESS_KEYWORD="\-DprocessName\=${PROCESS_NAME}"

PID=$(pgrep -f -a "${PROCESS_KEYWORD}"|grep -v "grep\|tail"|awk '{print $1}')
if [ -n "${PID}" ]; then
  echo "Stopping process: [${PROCESS_NAME}] (PID: ${PID})"
  kill -15 ${PID}
  while true; do
    sleep 1
    if ps -p ${PID} > /dev/null; then
      echo "Stopping process: [${PROCESS_NAME}] (PID: ${PID})"
    else
      echo "Stopped process: [${PROCESS_NAME}]"
      break
    fi
  done
else
  echo "Error: ${PROCESS_NAME} is not running"
  exit 1
fi
