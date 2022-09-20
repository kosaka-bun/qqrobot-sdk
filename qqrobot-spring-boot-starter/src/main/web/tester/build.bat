rmdir /s /q ..\..\resources\framework\tester\web
call npm run build:prod
move .\dist ..\..\resources\framework\tester\
cd ..\..\resources\framework\tester\
ren dist web
