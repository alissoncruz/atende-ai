@echo off
echo ============================================
echo  AtendeAi - Frontend React + Vite
echo ============================================
cd /d "%~dp0frontend"

if not exist "node_modules" (
    echo Instalando dependencias (npm install)...
    call npm install
)

echo.
echo Iniciando servidor de desenvolvimento...
echo Acesse: http://localhost:3000
echo.
call npm run dev

pause
