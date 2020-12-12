del vs_projects\CMakeCache.txt

cmake -S . -B vs_projects -G "Visual Studio 16 2019" -DCMAKE_PREFIX_PATH="C:\Program Files (x86)\Microsoft Visual Studio\2019\BuildTools\VC\Tools\MSVC\14.24.28314\bin\Hostx64\x64"

call msvc_build.bat

pause