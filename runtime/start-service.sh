# Use -Dlog4j.debug for Log4J startup debugging info
# Use -Xms512M -Xmx512M to start with 512MB of heap memory. Set size according to your needs.
# Use -XX:+CMSClassUnloadingEnabled -XX:+CMSPermGenSweepingEnabled for PermGen GC


JAVA_CMD="java"
localhost="10.1.2.233"
CPATH="./:lib/*:lib/Jetty/*"

# moudle must be unique
MODULE=route_server

######
# check param
######
if [ "$1" == "" ] || [ "$1" == "help" ] ; then 
	echo "sfs2x.sh [start/stop/restart/help]"
	echo ""
	echo "start   : start game server   "
	echo "stop    : stop game server    "
	echo "restart : restart game server "
	echo ""
	echo "for example:"
	echo "          sfs2x.sh start"
	exit 0
fi

######
# 
######
#ulimit -SHn 65535
#echo 8061540 > /proc/sys/fs/file-max
#echo 32668 65535 > /proc/sys/net/ipv4/ip_local_port_range

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
	ps ax | grep "module.name=$MODULE " | grep -v grep | awk '{print $1}' | xargs kill -15
	
	# wait 5 sceconds, wait kill finished
	sleep 5
	echo "kill ok"
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

nohup ${JAVA_CMD} -cp "${CPATH}"  -Xms4g -Xmx4g -Xmn1g -XX:SurvivorRatio=8 -XX:TargetSurvivorRatio=90 -XX:MaxTenuringThreshold=10 -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:ParallelGCThreads=8 -XX:+PrintGCDetails -Xloggc:logs/gc.log -XX:+PrintGCDateStamps -XX:-OmitStackTraceInFastThrow -XX:MaxPermSize=256M -XX:CMSInitiatingOccupancyFraction=75 -XX:ReservedCodeCacheSize=256m -XX:+HeapDumpOnOutOfMemoryError  -XX:HeapDumpPath=logs -Djava.rmi.server.hostname=${localhost} -Dfile.encoding=UTF-8 -Dmodule.name=$MODULE com.dd.server.ServerBootStrap > nohup.out 2>&1 &
