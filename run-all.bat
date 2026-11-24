@echo off
echo =======================================================================
echo          Launching Full MediSlot System (Backend + Frontend)
echo =======================================================================
start "MediSlot Backend" cmd /c "start-backend.bat"
timeout /t 5 /nobreak
start "MediSlot Frontend" cmd /c "start-frontend.bat"
echo Both services launched in separate windows!
echo Backend:  http://localhost:8080
echo Frontend: http://localhost:5173
echo H2 DB:    http://localhost:8080/h2-console
