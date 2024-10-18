#!/bin/sh

set -e

cd $(dirname "$0")/..
PROJECT_PATH="$(pwd)"
TARGET_DIR="$PROJECT_PATH/../../src/main/resources/web"

rm -rf "$TARGET_DIR/admin"
if [ ! -d node_modules ]; then
  npm install
fi
npm run build:prod

mv dist "$TARGET_DIR"
cd "$TARGET_DIR"
mv dist admin

git add admin