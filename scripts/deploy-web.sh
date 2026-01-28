#!/bin/bash

set -e

cd $(dirname "$0")/..

cd qqrobot-spring-boot-starter/web
find . -type d -name 'node_modules' -prune -o -type f -name '*.sh' -print0 | xargs -0 chmod +x

./admin/scripts/deploy.sh
./tester/scripts/deploy.sh
