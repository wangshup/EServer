chcp 936

@echo off
set GAME_HOME="D:\work\Tech\GameServer\EServer\runtime"
set PROJECT_HOME="D:\work\Tech\GameServer\EServer"

call gradle -b %PROJECT_HOME%\build.gradle --parallel build -x test 

call gradle -b %PROJECT_HOME%\build.gradle copyJars

copy %PROJECT_HOME%\ECoreServer\build\libs\*.jar %GAME_HOME%\lib
copy %PROJECT_HOME%\EDataServer\build\libs\*.jar %GAME_HOME%\lib
copy %PROJECT_HOME%\EProto\build\libs\*.jar %GAME_HOME%\lib
copy %PROJECT_HOME%\EHotSwap\build\libs\*.jar %GAME_HOME%\lib
copy %PROJECT_HOME%\EGate\build\libs\*.jar %GAME_HOME%\lib

copy %PROJECT_HOME%\EGameServer\build\libs\*.jar %GAME_HOME%\extensions\RPG1

pause