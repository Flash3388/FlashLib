#!/bin/bash
mkdir build/flashboard
cp -a dist/. build/flashboard
cp -r libs build/flashboard/flashboard_lib
cp build/libs/flashboard.jar build/flashboard
