# End-to-End API and Integration Test Script
Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host " STARTUP IDEA VALIDATION PLATFORM - AUTOMATED API TESTS" -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

$baseUrl = "http://localhost:8080"
$mvnCmd = ".\local-maven\apache-maven-3.9.6\bin\mvn.cmd"

# 1. Start Spring Boot in background job
Write-Host "`n[1/7] Launching Spring Boot application..." -ForegroundColor Yellow
$appProcess = Start-Process -FilePath "cmd.exe" -ArgumentList "/c `"$mvnCmd`" -o spring-boot:run" -PassThru -WindowStyle Hidden

# 2. Wait for application to be available on port 8080
$maxAttempts = 35
$attempt = 0
$ready = $false

while ($attempt -lt $maxAttempts -and -not $ready) {
    Start-Sleep -Seconds 2
    $attempt++
    try {
        $health = Invoke-RestMethod -Uri "$baseUrl/api/users/health" -Method Get -TimeoutSec 3 -ErrorAction Stop
        if ($health -like "*up and running*") {
            $ready = $true
            Write-Host "Spring Boot is UP and RUNNING! (Attempt $attempt)" -ForegroundColor Green
        }
    } catch {
        Write-Host "Waiting for server to initialize... ($attempt/$maxAttempts)" -ForegroundColor DarkGray
    }
}

if (-not $ready) {
    Write-Host "FAILED: Server did not respond within expected time." -ForegroundColor Red
    if ($appProcess -and -not $appProcess.HasExited) { Stop-Process -Id $appProcess.Id -Force }
    exit 1
}

try {
    # 3. Test Static HTML Loading
    Write-Host "`n[2/7] Testing Static Frontend Resources..." -ForegroundColor Yellow
    $indexHtml = Invoke-WebRequest -Uri "$baseUrl/index.html" -Method Get -UseBasicParsing
    Write-Host "  -> GET /index.html : Status $($indexHtml.StatusCode) OK" -ForegroundColor Green

    $dashHtml = Invoke-WebRequest -Uri "$baseUrl/dashboard.html" -Method Get -UseBasicParsing
    Write-Host "  -> GET /dashboard.html : Status $($dashHtml.StatusCode) OK" -ForegroundColor Green

    # 4. Test User Registration
    Write-Host "`n[3/7] Testing User Registration API (POST /api/auth/register)..." -ForegroundColor Yellow
    $uniqueSuffix = Get-Random -Minimum 1000 -Maximum 9999
    $regPayload = @{
        username = "founder_$uniqueSuffix"
        email = "founder_$uniqueSuffix@startup.io"
        password = "Password@123"
        fullName = "Alex Founder"
    } | ConvertTo-Json

    $regResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/register" -Method Post -Body $regPayload -ContentType "application/json"
    $token = $regResponse.token
    Write-Host "  -> Registered user: $($regResponse.username) (Role: $($regResponse.role))" -ForegroundColor Green
    Write-Host "  -> JWT Token received: $($token.Substring(0, 25))..." -ForegroundColor DarkCyan

    # 5. Test User Login
    Write-Host "`n[4/7] Testing User Login API (POST /api/auth/login)..." -ForegroundColor Yellow
    $loginPayload = @{
        usernameOrEmail = "founder_$uniqueSuffix"
        password = "Password@123"
    } | ConvertTo-Json

    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method Post -Body $loginPayload -ContentType "application/json"
    $authToken = $loginResponse.token
    Write-Host "  -> Login successful for: $($loginResponse.username)" -ForegroundColor Green

    $authHeaders = @{
        "Authorization" = "Bearer $authToken"
        "Content-Type" = "application/json"
    }

    # 6. Test Idea Submission
    Write-Host "`n[5/7] Testing Startup Idea Submission (POST /api/ideas)..." -ForegroundColor Yellow
    $ideaPayload = @{
        title = "MedAssist AI"
        industry = "Healthcare"
        targetAudience = "General Practitioners and Outpatient Clinics"
        stage = "Ideation"
        description = "An automated AI clinical decision copilot that summarizes medical histories and assists doctors in spotting rare contraindications during outpatient visits."
    } | ConvertTo-Json

    $ideaResponse = Invoke-RestMethod -Uri "$baseUrl/api/ideas" -Method Post -Headers $authHeaders -Body $ideaPayload
    $ideaId = $ideaResponse.id
    Write-Host "  -> Created Idea ID: $ideaId - '$($ideaResponse.title)' in Industry: $($ideaResponse.industry)" -ForegroundColor Green

    # 7. Test AI Analysis Validation Scan
    Write-Host "`n[6/7] Testing AI Validation Scan (POST /api/ideas/$ideaId/analyze)..." -ForegroundColor Yellow
    $analysisResponse = Invoke-RestMethod -Uri "$baseUrl/api/ideas/$ideaId/analyze" -Method Post -Headers $authHeaders
    Write-Host "  -> Viability Score: $($analysisResponse.viabilityScore)/100" -ForegroundColor Green
    Write-Host "  -> Market Feasibility: $($analysisResponse.marketFeasibility.Substring(0, 70))..." -ForegroundColor DarkCyan
    Write-Host "  -> Strengths: $($analysisResponse.strengths.Substring(0, 60))..." -ForegroundColor DarkCyan
    Write-Host "  -> Competitors Found: $($analysisResponse.competitors.Count)" -ForegroundColor Green
    Write-Host "  -> Funding Roadmap Stage: $($analysisResponse.fundingRoadmap.fundingStage)" -ForegroundColor Green

    # 8. Test Saved Analysis Report Retrieval
    Write-Host "`n[7/7] Testing Saved Report Retrieval (GET /api/ideas/$ideaId/report)..." -ForegroundColor Yellow
    $savedReport = Invoke-RestMethod -Uri "$baseUrl/api/ideas/$ideaId/report" -Method Get -Headers $authHeaders
    Write-Host "  -> Saved Report ID: $($savedReport.id) for Idea: '$($savedReport.ideaTitle)'" -ForegroundColor Green
    Write-Host "  -> Report Viability Score: $($savedReport.viabilityScore)" -ForegroundColor Green

    Write-Host "`n==========================================================" -ForegroundColor Green
    Write-Host " ALL 7 INTEGRATION & END-TO-END TESTS PASSED SUCCESSFULLY!" -ForegroundColor Green
    Write-Host "==========================================================" -ForegroundColor Green

} finally {
    Write-Host "`nStopping Spring Boot test process..." -ForegroundColor DarkGray
    if ($appProcess -and -not $appProcess.HasExited) {
        Stop-Process -Id $appProcess.Id -Force
    }
    # Clean up any orphan java processes spawned by cmd
    Get-Process -Name java -ErrorAction SilentlyContinue | Where-Object { $_.CommandLine -like "*startup-idea-platform*" } | Stop-Process -Force -ErrorAction SilentlyContinue
}
