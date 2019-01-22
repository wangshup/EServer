#!/bin/sh
# Use -Dlog4j.debug for Log4J startup debugging info
# Use -Xms512M -Xmx512M to start with 512MB of heap memory. Set size according to your needs.
# Use -XX:+CMSClassUnloadingEnabled -XX:+CMSPermGenSweepingEnabled for PermGen GC

JAVA_CMD="/opt/tiger/jdk/jdk11/bin/java"
localhost="10.224.4.175"
# moudle must be unique
MODULE=gs_dev
CPATH="./:lib/*"

######
# check param
######
if [ "$1" == "" ] || [ "$1" == "help" ] ; then
        echo "run.sh [start/stop/restart/help]"
        echo ""
        echo "start   : start game server   "
        echo "stop    : stop game server    "
        echo "restart : restart game server "
        echo ""
        echo "for example:"
        echo "          run.sh start"
        exit 0
fi

######
# stop
######
if [ "$1" == "stop" ] || [ "$1" == "restart" ] ; then
        echo "stop server: [ $MODULE ]"
        echo "kill server process"

        pid=$(ps ax | grep "module.name=$MODULE " | grep -v grep | awk '{print $1}')

if [ "$pid" == "" ] ; then
        echo "kill ok"
else
        ps ax | grep "module.name=$MODULE " | grep -v grep | awk '{print $1}' |xargs kill -15
        
        count=0
        flag=1
        result=1
        while [ "$flag" -eq 1 ]
        do
           sleep 1s
           result=$(ps ax | grep "module.name=$MODULE " | grep -v grep | awk '{print $1}')
           if [ -z "$result" ]; then
              echo "process $pid is finished"
              flag=0
           fi
           let count+=1;
           if [ "$count" -gt 30 ]; then
              ps ax | grep "module.name=$MODULE " | grep -v grep | awk '{print $1}' |xargs kill -9
              count=0
              echo "kill timeout, use kill -9"
           fi
        done

fi

        if [ "$1" == "stop" ] ; then
                exit 0
        fi
fi

echo "check has the same process: $MODULE"
pid=$(ps ax | grep "module.name=$MODULE " | grep -v grep | awk '{print $1}')

if [ "$pid" == "" ] ; then
  echo "OK"
else
  echo "has same process, please stop process id:$pid"
  exit 0
fi

echo "starting ..."

nohup ${JAVA_CMD} -javaagent:lib/gs.jar -cp "${CPATH}" -Xmn1g -Xmx4g -Xms4g -Dmodule.name=$MODULE -Djava.rmi.server.hostname=${localhost} -Xrunjdwp:transport=dt_socket,address=*:5005,server=y,suspend=n -Dfile.encoding=UTF-8 gs.Main gs.json >/dev/null 2>&1 &

tail -F /opt/immotal_server/gs_dev/logs/gs.log|sed '/Server start successfully/Q'
