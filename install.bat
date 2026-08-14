@echo off
setlocal enabledelayedexpansion

echo ================================================================
echo  Building and Installing cwsgit globally for Windows...
echo ================================================================

call mvn clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Maven build failed! Please check JDK 21+ and Maven environment.
    exit /b %ERRORLEVEL%
)

if not exist "C:\cwsgit" mkdir "C:\cwsgit"

for %%f in (target\*.jar) do (
    copy /Y "%%f" "C:\cwsgit\cwsgit.jar" >nul
)

@echo @echo off > "C:\cwsgit\cwsgit.cmd"
@echo java -jar "C:\cwsgit\cwsgit.jar" %%* >> "C:\cwsgit\cwsgit.cmd"

echo.
echo Ensuring C:\cwsgit is in User PATH...
powershell -NoProfile -ExecutionPolicy Bypass -Command "$oldPath = [Environment]::GetEnvironmentVariable('Path', 'User'); if ($oldPath -notlike '*C:\cwsgit*') { [Environment]::SetEnvironmentVariable('Path', $oldPath + ';C:\cwsgit', 'User') }"

echo ================================================================
echo  ✓ Success! 'cwsgit' installed globally.
echo.
echo  1. Restart your terminal window.
echo  2. Run 'cwsgit install' inside your repository.
echo ================================================================