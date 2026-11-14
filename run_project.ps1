$ErrorActionPreference = 'Stop'

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host " STARTING STARTUP IDEA VALIDATION PLATFORM (SPRING BOOT)" -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

$mvnCmd = ".\local-maven\apache-maven-3.9.6\bin\mvn.cmd"

if (-not (Test-Path $mvnCmd)) {
    Write-Host "Checking Maven installation..."
    $mvnCmd = "mvn"
}

Write-Host "Launching Spring Boot backend..." -ForegroundColor Green
Write-Host "Web Application will be available at: http://localhost:8080" -ForegroundColor Yellow

& $mvnCmd -o spring-boot:run
