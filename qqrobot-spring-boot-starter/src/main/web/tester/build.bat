rmdir /s /q ..\..\resources\web\tester-framework
call npm run build:prod
move .\dist ..\..\resources\web\
cd ..\..\resources\web\
ren dist tester-framework
