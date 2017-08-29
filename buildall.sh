#!/bin/bash
echo "Building flashlib";

cd flashlibj;
./build.sh;
cd ..;
cp flashlibj/build/libs/flashlib.jar flashboard/libs;
cd flashboard;
./build.sh;

echo "Ready";

