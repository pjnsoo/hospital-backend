#!/bin/bash

echo HOSTNAME: ["$HOSTNAME"]


# 에이전트 자바 실행 경로 설정
JRE=/usr/bin/java
echo JAVA: ["$JRE"]

CLASSPATH=.
CLASSPATH=$CLASSPATH:$PROJECT_HOME/hospital-backend.jar
CLASSPATH=$CLASSPATH:$(find "$PROJECT_HOME"/libs -type d -exec sh -c 'printf "%s/*:" "$0"' {} \; | sed 's/:$//')
CLASSPATH=$CLASSPATH:$PROJECT_HOME/conf
echo CLASSPATH: ["$CLASSPATH"]

#로그 경로 설정할것
#인스턴스별로 설정할 경우, 해당 기동쉘에서 LOG_PATH 변경할 것
export LOG_PATH=$PROJECT_HOME/logs
