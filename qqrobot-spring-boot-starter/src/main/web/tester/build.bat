chcp 65001

cd /d "%~dp0."

rmdir /s /q ..\..\resources\web\tester-framework
call npm run build:prod
move .\dist ..\..\resources\web\
cd ..\..\resources\web\
ren dist tester-framework
git add tester-framework
