del vs_projects\Debug\nativeshooting.*

call "C:\Program Files (x86)\Microsoft Visual Studio\2019\Community\Common7\Tools\VsDevCmd.bat"

cd vs_projects

msbuild shooting.sln

cp vs_projects/Debug/nativeshooting.dll src/main/resources/nativeshooting.dll

pause