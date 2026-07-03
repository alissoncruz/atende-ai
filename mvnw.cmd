@echo off
setlocal enabledelayedexpansion

set "MAVEN_VERSION=3.9.9"
set "MAVEN_DIR=%~dp0.mvn\maven"
set "MAVEN_HOME=%MAVEN_DIR%\apache-maven-%MAVEN_VERSION%"
set "MVN=%MAVEN_HOME%\bin\mvn.cmd"

if exist "%MVN%" goto :run

echo Maven %MAVEN_VERSION% nao encontrado. Baixando...
if not exist "%MAVEN_DIR%" mkdir "%MAVEN_DIR%"

set "ZIP=%MAVEN_DIR%\maven.zip"
set "URL=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/apache-maven-%MAVEN_VERSION%-bin.zip"

powershell -NoProfile -Command "Invoke-WebRequest -Uri '%URL%' -OutFile '%ZIP%'"
if errorlevel 1 (
    echo ERRO: Falha ao baixar Maven. Verifique sua conexao.
    pause & exit /b 1
)

powershell -NoProfile -Command "Expand-Archive -Path '%ZIP%' -DestinationPath '%MAVEN_DIR%' -Force"
del "%ZIP%"
echo Maven %MAVEN_VERSION% instalado em %MAVEN_HOME%

:run
"%MVN%" %*
