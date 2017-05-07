#!/bin/bash
if [ -d "build/flashboard" ]; then
  rm -rf build/flashboard/
fi

gradle build;
mkdir build/flashboard;
cp -a dist/. build/flashboard;
cp -r libs build/flashboard/flashboard_lib;
cp build/libs/flashboard.jar build/flashboard;

echo "Flashboard is ready in build/flashboard";