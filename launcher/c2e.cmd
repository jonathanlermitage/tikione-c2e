@echo off
rem ---------------------------------------------------------------------------------------------------------------------------
rem TikiOne C2E start-up script.
rem Used to launch TikiOne C2E with the bundled JVM (if exists) or the operating system's JVM.
rem ---------------------------------------------------------------------------------------------------------------------------

cd %~dp0%

set "OPATH=%PATH%"
set "PATH=%cd%\jre\bin\;%~dp0%\jre\bin\"
set "PATH=%PATH%;C:\Program Files (x86)\Java\jre8\bin\"
set "PATH=%PATH%;C:\Program Files (x86)\Java\jre9\bin\"
set "PATH=%PATH%;C:\Program Files (x86)\Java\jre10\bin\"
set "PATH=%PATH%;C:\Program Files (x86)\Java\jre\bin\"
set "PATH=%PATH%;C:\Program Files\Java\jre8\bin\"
set "PATH=%PATH%;C:\Program Files\Java\jre9\bin\"
set "PATH=%PATH%;C:\Program Files\Java\jre10\bin\"
set "PATH=%PATH%;C:\Program Files\Java\jre\bin\"
set "PATH=%PATH%;%OPATH%"

java.exe -jar -Xms32m -Xmx512m -Dfile.encoding=UTF-8 "c2e.jar" %1 %2 %3 %4 %5 %6 %7 %8 %9
