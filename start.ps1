<#
.SYNOPSIS
    Starts the GH-600 Study App (Spring Boot backend + React/Vite frontend).

.DESCRIPTION
    Launches both the Spring Boot backend (:8080) and the Vite React dev server (:5173).
    Automatically installs frontend npm dependencies if node_modules is missing.
    Press Ctrl+C to stop both servers cleanly (when running in the current terminal).

.PARAMETER SeparateWindows
    Launch backend and frontend in separate PowerShell windows instead of the current terminal.

.PARAMETER OpenBrowser
    Open http://localhost:5173 in the default browser once the servers are ready.

.PARAMETER Stop
    Stop any processes currently listening on ports 8080 and 5173, then exit.

.EXAMPLE
    .\start.ps1
    Starts both servers in the current terminal. Press Ctrl+C to stop both.

.EXAMPLE
    .\start.ps1 -OpenBrowser
    Starts both servers and opens http://localhost:5173 when ready.

.EXAMPLE
    .\start.ps1 -SeparateWindows
    Starts backend and frontend in two separate PowerShell windows.

.EXAMPLE
    .\start.ps1 -Stop
    Stops any running processes on ports 8080 and 5173.
#>
[CmdletBinding()]
param(
    [switch]$SeparateWindows,
    [switch]$OpenBrowser,
    [switch]$Stop
)

$ErrorActionPreference = 'Stop'
$RootDir = $PSScriptRoot
$BackendDir = Join-Path $RootDir 'backend'
$FrontendDir = Join-Path $RootDir 'frontend'
$BackendPort = 8080
$FrontendPort = 5173

function Stop-PortProcess {
    param([int]$Port)
    $connections = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue
    if ($connections) {
        $pids = $connections | Select-Object -ExpandProperty OwningProcess -Unique
        foreach ($procId in $pids) {
            if ($procId -gt 0) {
                Write-Host "Stopping process PID $procId listening on port $Port..." -ForegroundColor Yellow
                & taskkill.exe /PID $procId /T /F 2>&1 | Out-Null
            }
        }
    }
}

function Test-PortInUse {
    param([int]$Port)
    $conn = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue
    return ($null -ne $conn)
}

if ($Stop) {
    Stop-PortProcess -Port $BackendPort
    Stop-PortProcess -Port $FrontendPort
    Write-Host "Stopped servers on ports $BackendPort and $FrontendPort." -ForegroundColor Green
    exit 0
}

Write-Host "=== GH-600 Study App Startup ===" -ForegroundColor Cyan

# 1. Verify prerequisites
if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    Write-Error "Java is not on PATH. Please install JDK 25 and ensure JAVA_HOME/PATH are set."
}
if (-not (Get-Command npm -ErrorAction SilentlyContinue)) {
    Write-Error "npm is not on PATH. Please install Node.js."
}

# 2. Check if ports are already in use
if (Test-PortInUse -Port $BackendPort) {
    Write-Warning "Port $BackendPort is already in use. Run '.\start.ps1 -Stop' to free it."
    exit 1
}
if (Test-PortInUse -Port $FrontendPort) {
    Write-Warning "Port $FrontendPort is already in use. Run '.\start.ps1 -Stop' to free it."
    exit 1
}

# 3. Install frontend dependencies if node_modules is missing
$NodeModulesDir = Join-Path $FrontendDir 'node_modules'
if (-not (Test-Path $NodeModulesDir)) {
    Write-Host "[frontend] node_modules not found. Running npm install..." -ForegroundColor Yellow
    Push-Location $FrontendDir
    try {
        & npm.cmd install
        if ($LASTEXITCODE -ne 0) {
            throw "npm install failed with exit code $LASTEXITCODE"
        }
    } finally {
        Pop-Location
    }
}

# 4. Launch servers
if ($SeparateWindows) {
    Write-Host "Starting Backend (Spring Boot) in a new window on http://localhost:$BackendPort ..." -ForegroundColor Green
    Start-Process powershell.exe -WorkingDirectory $BackendDir -ArgumentList @(
        '-NoExit',
        '-Command',
        "`$Host.UI.RawUI.WindowTitle = 'GH-600 Backend (:8080)'; .\mvnw.cmd spring-boot:run"
    ) | Out-Null

    Write-Host "Starting Frontend (Vite + React) in a new window on http://localhost:$FrontendPort ..." -ForegroundColor Blue
    Start-Process powershell.exe -WorkingDirectory $FrontendDir -ArgumentList @(
        '-NoExit',
        '-Command',
        "`$Host.UI.RawUI.WindowTitle = 'GH-600 Frontend (:5173)'; npm.cmd run dev"
    ) | Out-Null

    if ($OpenBrowser) {
        Start-Process "http://localhost:$FrontendPort"
    }

    Write-Host ""
    Write-Host "Both servers are launching in separate windows:" -ForegroundColor Cyan
    Write-Host "  Frontend : http://localhost:$FrontendPort"
    Write-Host "  Backend  : http://localhost:$BackendPort/api/hello"
    Write-Host "  H2 DB UI : http://localhost:$BackendPort/h2-console"
    Write-Host "To stop them later, close their windows or run: .\start.ps1 -Stop"
    exit 0
}

# Single-terminal mode using PowerShell background jobs with streamed output
Write-Host "Starting Backend (:8080) and Frontend (:5173)... (Press Ctrl+C to stop both)" -ForegroundColor Cyan
Write-Host "  Frontend : http://localhost:$FrontendPort"
Write-Host "  Backend  : http://localhost:$BackendPort/api/hello"
Write-Host "  H2 DB UI : http://localhost:$BackendPort/h2-console"
Write-Host ""

$backendJob = Start-Job -Name 'gh600-backend' -ScriptBlock {
    param($dir)
    Set-Location $dir
    & .\mvnw.cmd spring-boot:run 2>&1
} -ArgumentList $BackendDir

$frontendJob = Start-Job -Name 'gh600-frontend' -ScriptBlock {
    param($dir)
    Set-Location $dir
    & npm.cmd run dev 2>&1
} -ArgumentList $FrontendDir

$browserOpened = -not $OpenBrowser

try {
    while ($true) {
        $backendLines = Receive-Job -Job $backendJob -ErrorAction SilentlyContinue
        foreach ($line in $backendLines) {
            Write-Host "[backend]  $line" -ForegroundColor DarkGreen
        }

        $frontendLines = Receive-Job -Job $frontendJob -ErrorAction SilentlyContinue
        foreach ($line in $frontendLines) {
            Write-Host "[frontend] $line" -ForegroundColor Cyan
        }

        if (-not $browserOpened) {
            if ((Test-PortInUse -Port $BackendPort) -and (Test-PortInUse -Port $FrontendPort)) {
                Start-Process "http://localhost:$FrontendPort"
                $browserOpened = $true
            }
        }

        if ($backendJob.State -in @('Completed', 'Failed', 'Stopped')) {
            Write-Warning "[backend] exited with state: $($backendJob.State)"
            break
        }
        if ($frontendJob.State -in @('Completed', 'Failed', 'Stopped')) {
            Write-Warning "[frontend] exited with state: $($frontendJob.State)"
            break
        }

        Start-Sleep -Milliseconds 250
    }
} finally {
    Write-Host "`nShutting down GH-600 servers..." -ForegroundColor Yellow
    Stop-Job -Job $backendJob, $frontendJob -ErrorAction SilentlyContinue
    Remove-Job -Job $backendJob, $frontendJob -Force -ErrorAction SilentlyContinue
    Stop-PortProcess -Port $BackendPort
    Stop-PortProcess -Port $FrontendPort
    Write-Host "All servers stopped." -ForegroundColor Green
}

