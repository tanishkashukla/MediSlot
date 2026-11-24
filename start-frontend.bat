@echo off
echo =======================================================================
echo          Starting MediSlot React Frontend (Vite Port 5173)
echo =======================================================================
cd frontend
if not exist node_modules (
    echo Installing npm dependencies...
    call npm install
)
call npm run dev
pause
