@rem Gradle Wrapper para Windows
@echo off
setlocal enabledelayedexpansion

set "GRADLE_VERSION=8.14"
set "GRADLE_HOME=%~dp0.gradle-dist\gradle-%GRADLE_VERSION%"
set "GRADLE_BIN=%GRADLE_HOME%\bin\gradle.bat"

if exist "%GRADLE_BIN%" goto :run

echo Baixando Gradle %GRADLE_VERSION%...
if not exist "%~dp0.gradle-dist" mkdir "%~dp0.gradle-dist"

set "ZIP=%TEMP%\gradle-%GRADLE_VERSION%-bin.zip"
powershell -NoProfile -Command "Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip' -OutFile '%ZIP%'"
if errorlevel 1 (
    echo ERRO: falha ao baixar Gradle. Verifique sua conexao.
    pause & exit /b 1
)

powershell -NoProfile -Command "Expand-Archive -Path '%ZIP%' -DestinationPath '%~dp0.gradle-dist' -Force"
del "%ZIP%"
echo Gradle %GRADLE_VERSION% pronto.

:run
"%GRADLE_BIN%" %*
