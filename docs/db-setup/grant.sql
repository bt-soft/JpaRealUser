/*
 *  ------------------------------------------------------------------------------------
 *
 *  JPA Real User Test Monitor project
 *
 *  Module:  JruTest (JruTest)
 *  File:    grant.sql
 *  Created: 2018.01.14. 14:08:53
 *
 *  ------------------------------------------------------------------------------------
 */
/**
 * Author:  BT
 * Created: 2018.01.14.
 */

---
--- Sequence
---
GRANT SELECT ON SCHEMAOWNER.JRU_SEQ to jru_role;


---
--- Work Table
---
GRANT SELECT, INSERT, UPDATE, DELETE ON JRU_TBL to jru_role;


---
--- Journal
---
GRANT SELECT ON JRU_JRNL to jru_role;

