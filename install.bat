@echo off
setlocal enabledelayedexpansion

echo ================================================================
echo  Building and Installing cwsgit globally for Windows...
echo ================================================================

call mvn clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Maven build failed! Please make sure Maven and JDK 21+ are installed.
    exit /b %ERRORLEVEL%
)

if not exist "C:\cwsgit" mkdir "C:\cwsgit"

copy /Y "target\GitAgent-0.0.1-SNAPSHOT.jar" "C:\cwsgit\cwsgit.jar" >nul

@echo @echo off > "C:\cwsgit\cwsgit.cmd"
@echo java -jar "C:\cwsgit\cwsgit.jar" %%* >> "C:\cwsgit\cwsgit.cmd"

echo.
echo Adding C:\cwsgit to User PATH...
powershell -NoProfile -ExecutionPolicy Bypass -Command "$oldPath = [Environment]::GetEnvironmentVariable('Path', 'User'); if ($oldPath -notlike '*C:\cwsgit*') { [Environment]::SetEnvironmentVariable('Path', $oldPath + ';C:\cwsgit', 'User') }"

echo ================================================================
echo  ✓ Success! 'cwsgit' installed globally.
echo.
echo  1. Restart your terminal / PowerShell / Command Prompt window.
echo  2. Open ANY Git project and run:
echo        cwsgit install
echo ================================================================