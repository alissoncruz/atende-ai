@echo off
echo ============================================
echo  AtendeAi - Spring Boot API
echo ============================================
cd /d "%~dp0"

:: ── Auto-detectar JAVA_HOME se nao definido ──────────────────────────────────
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\javac.exe" (
        echo JAVA_HOME ja definido: %JAVA_HOME%
        goto :java_ok
    )
)

echo Procurando JDK instalado...

:: ── Caminho exato encontrado nesta maquina ───────────────────────────────────
if exist "C:\Arquivos de Programas\Java\jdk-25\bin\javac.exe" (
    set "JAVA_HOME=C:\Arquivos de Programas\Java\jdk-25"
    goto :java_ok
)

:: ── Busca generica (Program Files em PT e EN, varias versoes) ────────────────
for /d %%D in (
    "C:\Arquivos de Programas\Java\jdk-*"
    "C:\Program Files\Java\jdk-*"
    "C:\Arquivos de Programas\Eclipse Adoptium\jdk-*"
    "C:\Program Files\Eclipse Adoptium\jdk-*"
    "C:\Arquivos de Programas\Microsoft\jdk-*"
    "C:\Program Files\Microsoft\jdk-*"
    "C:\Arquivos de Programas\Amazon Corretto\jdk*"
    "C:\Program Files\Amazon Corretto\jdk*"
    "C:\Program Files\JetBrains\*\jbr"
) do (
    if exist "%%~D\bin\javac.exe" (
        set "JAVA_HOME=%%~D"
        echo Encontrado: %%~D
        goto :java_ok
    )
)

:: Tentar derivar do PATH (java.exe -> bin -> JAVA_HOME)
for /f "tokens=*" %%i in ('where java 2^>nul') do (
    set "_JAVA_BIN=%%~dpi"
    set "_CANDIDATE=%%~dpi.."
    if exist "%%~dpi..\bin\javac.exe" (
        set "JAVA_HOME=%%~dpi.."
        echo Derivado do PATH: %JAVA_HOME%
        goto :java_ok
    )
)

echo.
echo ╔══════════════════════════════════════════════════════════╗
echo ║  ERRO: JDK nao encontrado!                               ║
echo ║                                                          ║
echo ║  Baixe e instale o JDK 25 em:                           ║
echo ║  https://adoptium.net/temurin/releases/?version=25      ║
echo ║                                                          ║
echo ║  Ou defina manualmente:                                  ║
echo ║  set JAVA_HOME=C:\caminho\para\jdk-25                   ║
echo ╚══════════════════════════════════════════════════════════╝
echo.
pause
exit /b 1

:java_ok
set "PATH=%JAVA_HOME%\bin;%PATH%"
echo JAVA_HOME=%JAVA_HOME%
echo.

:: ── Carregar variaveis do .env ────────────────────────────────────────────────
for /f "usebackq tokens=1,* delims==" %%A in (".env") do (
    set "line=%%A"
    if not "!line:~0,1!"=="#" if not "%%A"=="" set "%%A=%%B"
)

:: ── Selecionar Gradle: wrapper tem prioridade ────────────────────────────────
if exist "%~dp0gradlew.bat" (
    set "GRADLE_CMD=%~dp0gradlew.bat"
    echo Usando Gradle Wrapper (gradlew.bat)
) else (
    where gradle >nul 2>&1
    if %errorlevel% neq 0 (
        echo.
        echo ╔══════════════════════════════════════════════════════════╗
        echo ║  ERRO: Gradle nao encontrado!                            ║
        echo ║                                                          ║
        echo ║  Instale o Gradle: https://gradle.org/install/           ║
        echo ╚══════════════════════════════════════════════════════════╝
        echo.
        pause
        exit /b 1
    )
    set "GRADLE_CMD=gradle"
    echo Usando Gradle do PATH
)

echo Compilando e iniciando Spring Boot...
echo (Aguarde ~60s na primeira vez — Gradle baixa as dependencias)
echo.
%GRADLE_CMD% bootRun

pause
