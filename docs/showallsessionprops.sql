SET PAUSE ON
SET PAUSE 'Press Return to Continue'
SET PAGESIZE 60
SET LINESIZE 300
 
COL parameter FOR A24
COL value FOR A36
COL description FOR A80
 
SELECT 'AUDITED_CURSORID' AS Parameter, SYS_CONTEXT('USERENV','AUDITED_CURSORID') AS Value, 'Returns the cursor ID of the SQL that triggered the audit' AS Description FROM Dual
UNION ALL
SELECT 'AUTHENTICATION_DATA' AS Parameter, SYS_CONTEXT('USERENV','AUTHENTICATION_DATA') AS Value, 'Authentication data' AS Description FROM Dual
UNION ALL
SELECT 'AUTHENTICATION_TYPE' AS Parameter, SYS_CONTEXT('USERENV','AUTHENTICATION_TYPE') AS Value, 'Describes how the user was authenticated. Can be one of the following values: Database, OS, Network, or Proxy' AS Description FROM Dual
UNION ALL
SELECT 'BG_JOB_ID' AS Parameter, SYS_CONTEXT('USERENV','BG_JOB_ID') AS Value, 'If the session was established by an Oracle background process, this parameter will return the Job ID. Otherwise, it will return NULL.' AS Description FROM Dual
UNION ALL
SELECT 'CLIENT_IDENTIFIER' AS Parameter, SYS_CONTEXT('USERENV','CLIENT_IDENTIFIER') AS Value, 'Returns the client identifier (global context)' AS Description FROM Dual
UNION ALL
SELECT 'CLIENT_INFO' AS Parameter, SYS_CONTEXT('USERENV','CLIENT_INFO') AS Value, 'User session information' AS Description FROM Dual
UNION ALL
SELECT 'CURRENT_SCHEMA' AS Parameter, SYS_CONTEXT('USERENV','CURRENT_SCHEMA') AS Value, 'Returns the default schema used in the current schema' AS Description FROM Dual
UNION ALL
SELECT 'CURRENT_SCHEMAID' AS Parameter, SYS_CONTEXT('USERENV','CURRENT_SCHEMAID') AS Value, 'Returns the identifier of the default schema used in the current schema' AS Description FROM Dual
UNION ALL
SELECT 'CURRENT_SQL' AS Parameter, SYS_CONTEXT('USERENV','CURRENT_SQL') AS Value, 'Returns the SQL that triggered the audit event' AS Description FROM Dual
UNION ALL
SELECT 'CURRENT_USER' AS Parameter, SYS_CONTEXT('USERENV','CURRENT_USER') AS Value, 'Name of the current user' AS Description FROM Dual
UNION ALL
SELECT 'CURRENT_USERID' AS Parameter, SYS_CONTEXT('USERENV','CURRENT_USERID') AS Value, 'Userid of the current user' AS Description FROM Dual
UNION ALL
SELECT 'DB_DOMAIN' AS Parameter, SYS_CONTEXT('USERENV','DB_DOMAIN') AS Value, 'Domain of the database from the DB_DOMAIN initialization parameter' AS Description FROM Dual
UNION ALL
SELECT 'DB_NAME' AS Parameter, SYS_CONTEXT('USERENV','DB_NAME') AS Value, 'Name of the database from the DB_NAME initialization parameter' AS Description FROM Dual
UNION ALL
SELECT 'ENTRYID' AS Parameter, SYS_CONTEXT('USERENV','ENTRYID') AS Value, 'Available auditing entry identifier' AS Description FROM Dual
UNION ALL
SELECT 'EXTERNAL_NAME' AS Parameter, SYS_CONTEXT('USERENV','EXTERNAL_NAME') AS Value, 'External of the database user' AS Description FROM Dual
UNION ALL
SELECT 'FG_JOB_ID' AS Parameter, SYS_CONTEXT('USERENV','FG_JOB_ID') AS Value, 'If the session was established by a client foreground process, this parameter will return the Job ID. Otherwise, it will return NULL.' AS Description FROM Dual
UNION ALL
SELECT 'GLOBAL_CONTEXT_MEMORY' AS Parameter, SYS_CONTEXT('USERENV','GLOBAL_CONTEXT_MEMORY') AS Value, 'The number used in the System Global Area by the globally accessed context' AS Description FROM Dual
UNION ALL
SELECT 'HOST' AS Parameter, SYS_CONTEXT('USERENV','HOST') AS Value, 'Name of the host machine from which the client has connected' AS Description FROM Dual
UNION ALL
SELECT 'INSTANCE' AS Parameter, SYS_CONTEXT('USERENV','INSTANCE') AS Value, 'The identifier number of the current instance' AS Description FROM Dual
UNION ALL
SELECT 'IP_ADDRESS' AS Parameter, SYS_CONTEXT('USERENV','IP_ADDRESS') AS Value, 'IP address of the machine from which the client has connected' AS Description FROM Dual
UNION ALL
SELECT 'ISDBA' AS Parameter, SYS_CONTEXT('USERENV','ISDBA') AS Value, 'Returns TRUE if the user has DBA privileges. Otherwise, it will return FALSE.' AS Description FROM Dual
UNION ALL
SELECT 'LANG' AS Parameter, SYS_CONTEXT('USERENV','LANG') AS Value, 'The ISO abbreviate for the language' AS Description FROM Dual
UNION ALL
SELECT 'LANGUAGE' AS Parameter, SYS_CONTEXT('USERENV','LANGUAGE') AS Value, 'The language, territory, and character of the session. In the following format:language_territory.characterset' AS Description FROM Dual
UNION ALL
SELECT 'NETWORK_PROTOCOL' AS Parameter, SYS_CONTEXT('USERENV','NETWORK_PROTOCOL') AS Value, 'Network protocol used' AS Description FROM Dual
UNION ALL
SELECT 'NLS_CALENDAR' AS Parameter, SYS_CONTEXT('USERENV','NLS_CALENDAR') AS Value, 'The calendar of the current session' AS Description FROM Dual
UNION ALL
SELECT 'NLS_CURRENCY' AS Parameter, SYS_CONTEXT('USERENV','NLS_CURRENCY') AS Value, 'The currency of the current session' AS Description FROM Dual
UNION ALL
SELECT 'NLS_DATE_FORMAT' AS Parameter, SYS_CONTEXT('USERENV','NLS_DATE_FORMAT') AS Value, 'The date format for the current session' AS Description FROM Dual
UNION ALL
SELECT 'NLS_DATE_LANGUAGE' AS Parameter, SYS_CONTEXT('USERENV','NLS_DATE_LANGUAGE') AS Value, 'The language used for dates' AS Description FROM Dual
UNION ALL
SELECT 'NLS_SORT' AS Parameter, SYS_CONTEXT('USERENV','NLS_SORT') AS Value, 'BINARY or the linguistic sort basis' AS Description FROM Dual
UNION ALL
SELECT 'NLS_TERRITORY' AS Parameter, SYS_CONTEXT('USERENV','NLS_TERRITORY') AS Value, 'The territory of the current session' AS Description FROM Dual
UNION ALL
SELECT 'OS_USER' AS Parameter, SYS_CONTEXT('USERENV','OS_USER') AS Value, 'The OS username for the user logged in' AS Description FROM Dual
UNION ALL
SELECT 'PROXY_USER' AS Parameter, SYS_CONTEXT('USERENV','PROXY_USER') AS Value, 'The name of the user who opened the current session on behalf of SESSION_USER' AS Description FROM Dual
UNION ALL
SELECT 'PROXY_USERID' AS Parameter, SYS_CONTEXT('USERENV','PROXY_USERID') AS Value, 'The identifier of the user who opened the current session on behalf of SESSION_USER' AS Description FROM Dual
UNION ALL
SELECT 'SESSION_USER' AS Parameter, SYS_CONTEXT('USERENV','SESSION_USER') AS Value, 'The database user name of the user logged in' AS Description FROM Dual
UNION ALL
SELECT 'SESSION_USERID' AS Parameter, SYS_CONTEXT('USERENV','SESSION_USERID') AS Value, 'The database identifier of the user logged in' AS Description FROM Dual
UNION ALL
SELECT 'SESSIONID' AS Parameter, SYS_CONTEXT('USERENV','SESSIONID') AS Value, 'The identifier of the auditing session' AS Description FROM Dual
UNION ALL
SELECT 'TERMINAL' AS Parameter, SYS_CONTEXT('USERENV','TERMINAL') AS Value, 'The terminal where the user is logged' AS Description FROM Dual
/