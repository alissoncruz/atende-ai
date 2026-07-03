@echo off
echo ============================================
echo  AtendeAi - Iniciando todos os servicos
echo ============================================
cd /d "%~dp0"

echo [1/3] Subindo PostgreSQL...
docker compose up postgres -d
timeout /t 5 /nobreak > nul

echo [2/3] Iniciando Spring Boot API...
start "AtendeAi API" cmd /k "cd /d %~dp0 && start-api.bat"

echo [3/3] Iniciando Frontend...
start "AtendeAi Frontend" cmd /k "cd /d %~dp0frontend && if not exist node_modules npm install && npm run dev"

echo.
echo ============================================
echo  Servicos iniciados!
echo  API:      http://localhost:8080
echo  Frontend: http://localhost:3000
echo  Login:    admin@atendeai.com / admin123
echo ============================================
echo.
pause
