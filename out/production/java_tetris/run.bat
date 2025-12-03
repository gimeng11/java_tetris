@echo off
chcp 65001 >nul
cls

set CP=.
set CP=%CP%;jline-3.21.0.jar
set CP=%CP%;jline-terminal-3.21.0.jar
set CP=%CP%;jline-terminal-jna-3.21.0.jar
set CP=%CP%;jna-5.13.0.jar
set CP=%CP%;jna-platform-5.13.0.jar

echo === compileing... ===
javac -encoding UTF-8 -cp "%CP%" Main.java
if errorlevel 1 (
    echo error!
    pause
    exit /b
)

echo === running... ===
java -cp "%CP%" Main
pause
