@echo off
REM Boti launcher – run "boti" or "boti script.boti" without exposing Java.

if defined BOTI_HOME (
  set "BOTI_ROOT=%BOTI_HOME%"
) else (
  set "SCRIPT_DIR=%~dp0"
  set "SCRIPT_DIR=%SCRIPT_DIR:~0,-1%"
  for %%I in ("%SCRIPT_DIR%\..") do set "BOTI_ROOT=%%~fI"
)

set "NATIVE=%BOTI_ROOT%\target\boti.exe"
set "JAR=%BOTI_ROOT%\target\boti-1.0-SNAPSHOT.jar"
if not exist "%JAR%" set "JAR=%BOTI_ROOT%\lib\boti-1.0-SNAPSHOT.jar"
set "BUNDLED_JAVA=%BOTI_ROOT%\jre\bin\java.exe"

if exist "%NATIVE%" (
  "%NATIVE%" %*
  exit /b
)
if exist "%JAR%" (
  if exist "%BUNDLED_JAVA%" (
    "%BUNDLED_JAVA%" -jar "%JAR%" %*
  ) else (
    java -jar "%JAR%" %*
  )
  exit /b
)

echo Boti: cannot find interpreter. Build with: mvn package
echo   Or set BOTI_HOME to the Boti install directory.
exit /b 1
