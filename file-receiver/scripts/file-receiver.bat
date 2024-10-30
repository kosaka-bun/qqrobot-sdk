chcp 65001

cd /d "%~dp0."
start /b javaw -jar -Dfile.encoding=UTF-8 -Dspring.output.ansi.enabled=always file-receiver.jar