@echo off
echo ============================================
echo  AtendeAi - Subindo PostgreSQL via Docker
echo ============================================
cd /d "%~dp0"
docker compose up postgres -d
echo.
echo PostgreSQL iniciado em localhost:5432
echo Banco: atendeai / Usuario: atendeai / Senha: secret
echo.
pause
