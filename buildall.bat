echo "Building flashlib"

cd flashlibj
call build.bat
cd ..
copy flashlibj\build\libs\flashlib.jar flashboard\libs
cd flashboard
call build.bat

echo "Ready"