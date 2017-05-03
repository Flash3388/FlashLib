
mkdir %~dp0\build\flashboard
copy %~dp0\build\libs\flashboard.jar build\flashboard
xcopy %~dp0\dist %~dp0\build\flashboard /s /i
xcopy %~dp0\libs %~dp0\build\flashboard\flashboard_lib /s /i

echo "Folder is ready in build\flashboard"