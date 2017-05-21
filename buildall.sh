#!/bin/bash
echo "Building flashlib all";

#building flashlibj
cd flashlibj;
gradle build;
cd ..;
cp flashlibj/build/libs/flashlib.jar flashboard/libs/;

#building flashboard
cd flashboard;
gradle build;
if [ -d "build/flashboard" ]; then
  rm -rf build/flashboard/
fi
mkdir build/flashboard;
cp -r dist/. build/flashboard/;
cp -r libs/. build/flashboard/flashboard_lib/;
cp build/libs/flashboard.jar build/flashboard;

echo "Flashboard build is ready in flashboard/build/flashboard";
cd..;

