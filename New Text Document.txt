@echo off
set /p message="Message: "
git add .
git commit -m "%message%"
git push
pause