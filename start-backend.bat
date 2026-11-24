@echo off
echo =======================================================================
echo          Starting MediSlot Spring Boot Backend (Port 8081)
echo =======================================================================
cd backend
if exist mvnw.cmd (
    call mvnw.cmd spring-boot:run
) else (
    mvn spring-boot:run
)
pause
