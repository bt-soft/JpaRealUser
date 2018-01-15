/*
 *  ------------------------------------------------------------------------------------
 *
 *  JPA Real User Test Monitor project
 *
 *  Module:  JruTest (JruTest)
 *  File:    OracleDBSetup.sql
 *  Created: 2018.01.14. 14:00:34
 *
 *  ------------------------------------------------------------------------------------
 */
/**
 * Author:  BT
 * Created: 2018.01.14.
 */


---
--- Execute from 'schemaowner' Oracle account
---

@@drop.sql

@@seq.sql;
@@tables.sql;
@@ndx.sql;
@@trg.sql;
@@grant.sql;
/

prompt Ok

