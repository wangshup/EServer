chcp 65001

title "GAME_SERVER 网关服务器"

@java -cp "./;lib/*;lib/Jetty/*" -Xrunjdwp:transport=dt_socket,address=5005,server=y,suspend=n -Dfile.encoding=UTF-8 -Dio.netty.leakDetection.level=advanced com.dd.gate.GateBootStrap