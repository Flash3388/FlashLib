IF EXIST %~dp0\build\flashboard\NUL RMDIR /S /Q %~dp0\build\flashboard

call gradle build

mkdir %~dp0\build\flashboard
copy %~dp0\build\libs\flashboard.jar build\flashboard
xcopy %~dp0\dist %~dp0\build\flashboard /s /i
mkdir %~dp0\build\flashboard\libs
xcopy %~dp0\libs %~dp0\build\flashboard\libs /s /i

echo "Folder is ready in build\flashboard"
