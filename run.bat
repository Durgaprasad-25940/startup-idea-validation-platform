@echo off
title Startup Idea Validation Platform
cd /d "%~dp0"

echo ==========================================================
echo  STARTING STARTUP IDEA VALIDATION PLATFORM (SPRING BOOT)
echo ==========================================================
echo Web Application URL: http://localhost:8080
echo.

if exist "local-maven\apache-maven-3.9.6\bin\mvn.cmd" (
    call "local-maven\apache-maven-3.9.6\bin\mvn.cmd" -o spring-boot:run
) else (
    call mvn spring-boot:run
)

pause
