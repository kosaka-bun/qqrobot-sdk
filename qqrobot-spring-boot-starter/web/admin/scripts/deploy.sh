#!/bin/bash

set -e

cd $(dirname "$0")/..
project_path="$(pwd)"
target_dir="$project_path/../../src/main/resources/web"

rm -rf "$target_dir/admin"

if [ ! -d node_modules ]; then
  npm install
fi
npm run build:prod

mv dist "$target_dir"
cd "$target_dir"
mv dist admin
