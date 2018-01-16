@echo off
set GF_ASDAMIN_HOME=d:\JEE\glassfish5\bin
rem set GF_ASDAMIN_HOME=d:\JEE\lassfish-4.1.2\bin
set testUsersCnt=100
setlocal EnableDelayedExpansion

rem --- fileReaml
echo create jru-fileRealm:
call %GF_ASDAMIN_HOME%\asadmin create-auth-realm --classname com.sun.enterprise.security.auth.realm.file.FileRealm --property file=${com.sun.aas.instanceRoot}/config/jru-keyfile:jaas-context=fileRealm jru-fileRealm

rem --- admin user
echo create admin user:
call %GF_ASDAMIN_HOME%\asadmin.bat create-file-user --groups JRU_USER:JRU_ADMIN --authrealmname jru-fileRealm --passwordfile pass.txt admin

rem --- Test users
echo Create test users:
for /L %%i in (1, 1, %testUsersCnt%) do (
     set "formattedValue=000000%%i"
     set user=user!formattedValue:~-3!
     echo user!formattedValue:~-3!
     call %GF_ASDAMIN_HOME%\asadmin.bat create-file-user --groups JRU_USER --authrealmname jru-fileRealm --passwordfile pass.txt %%user%%
)

endlocal