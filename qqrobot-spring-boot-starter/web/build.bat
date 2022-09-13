rmdir /s /q ..\src\main\resources\framework\tester\web
call npm run build:prod
move .\dist ..\src/main\resources\framework\tester\
cd ..\src/main\resources\framework\tester\
ren dist web
