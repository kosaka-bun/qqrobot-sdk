#!/bin/sh

set -e

cd $(dirname "$0")/../..
PROJECT_PATH="$(pwd)"

# 构建qqrobot-spring-boot-starter/web
STARTER_WEB_PROJECT_PATH="$PROJECT_PATH/qqrobot-spring-boot-starter/web"

deploy-starter-web-project() {
  cd "$STARTER_WEB_PROJECT_PATH/$1/scripts"
  "$STARTER_WEB_PROJECT_PATH/$1/scripts/deploy.sh"
}

deploy-starter-web-project admin
deploy-starter-web-project tester

cd "$PROJECT_PATH"

# 发布
gradle-publish() {
  task_name=publish
  if [ -n "$1" ]; then
    task_name=":$1:$task_name"
  fi
  ./gradlew $task_name
}

gradle-publish qqrobot-framework-api
gradle-publish