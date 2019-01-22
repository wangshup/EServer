# Use -Dlog4j.debug for Log4J startup debugging info
# Use -Xms512M -Xmx512M to start with 512MB of heap memory. Set size according to your needs.
# Use -XX:+CMSClassUnloadingEnabled -XX:+CMSPermGenSweepingEnabled for PermGen GC

JAVA_CMD="/usr/bin/java"
localhost="10.1.37.101"
# moudle must be unique
MODULE=rpg_s1
CPATH="./:lib/*:lib/Jetty/*:extensions/__lib__/*:extensions/RPG1/*"

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
ulimit -SHn 65535
echo 8061540 > /proc/sys/fs/file-max
echo 32668 65535 > /proc/sys/net/ipv4/ip_local_port_range

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

nohup ${JAVA_CMD} -cp "${CPATH}" -Xmx2g -Xms2g -Dmodule.name=$MODULE -Djava.rmi.server.hostname=${localhost} -Xrunjdwp:transport=dt_socket,address=5005,server=y,suspend=n -Dfile.encoding=UTF-8 com.dd.server.ServerBootStrap > nohup.out 2>&1 &
