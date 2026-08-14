# PowerShell Global Installer for cwsgit (Windows)
$ErrorActionPreference = "Stop"

$repoOwner = "cws31"
$repoName  = "git-agent"
$assetName = "cwsgit.jar"
$installDir = "C:\cwsgit"

Write-Host "================================================================" -ForegroundColor Cyan
Write-Host " Installing cwsgit globally for Windows..." -ForegroundColor Cyan
Write-Host "================================================================" -ForegroundColor Cyan

# Check Java
if (-not (Get-Command "java" -ErrorAction SilentlyContinue)) {
    Write-Error "❌ Java is not installed. Please install JDK 21+ and add it to your PATH."
}

# Create Dir
if (-not (Test-Path $installDir)) {
    New-Item -ItemType Directory -Path $installDir | Out-Null
}

# Download JAR
$downloadUrl = "https://github.com/$repoOwner/$repoName/releases/latest/download/$assetName"
Write-Host "⬇️ Downloading latest release from GitHub..." -ForegroundColor Yellow
Invoke-WebRequest -Uri $downloadUrl -OutFile "$installDir\cwsgit.jar"

# Create batch wrapper
Set-Content -Path "$installDir\cwsgit.cmd" -Value "@echo off`r`njava -jar `"$installDir\cwsgit.jar`" %*"

# Add to User PATH
$userPath = [Environment]::GetEnvironmentVariable("Path", "User")
if ($userPath -notlike "*$installDir*") {
    [Environment]::SetEnvironmentVariable("Path", "$userPath;$installDir", "User")
    Write-Host "✓ Added $installDir to User PATH." -ForegroundColor Green
}

Write-Host "================================================================" -ForegroundColor Cyan
Write-Host " ✓ Success! 'cwsgit' installed globally." -ForegroundColor Green
Write-Host " Restart your terminal window, then run 'cwsgit install' in any Git repo." -ForegroundColor Yellow
Write-Host "================================================================" -ForegroundColor Cyan