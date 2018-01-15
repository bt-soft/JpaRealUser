@echo off
set testUsersCnt=50
set testUserFile=jmeter-test-users.txt
setlocal EnableDelayedExpansion

rem --- Test users
echo Create test users
type nul> %testUserFile%
for /L %%i in (1, 1, %testUsersCnt%) do (
     set "formattedValue=000000%%i"
     echo user!formattedValue:~-3!,pass12 >> %testUserFile%
)

endlocal