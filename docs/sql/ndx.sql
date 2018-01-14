/*
 *  ------------------------------------------------------------------------------------
 *
 *  JPA Real User Test Monitor project
 *
 *  Module:  JruTest (JruTest)
 *  File:    ndx.sql
 *  Created: 2018.01.14. 14:06:14
 *
 *  ------------------------------------------------------------------------------------
 */
/**
 * Author:  BT
 * Created: 2018.01.14.
 */




---
--- Journal
---

-- Create/Recreate indexes
create index JRU_JRNL_IX_MODDAT on JRU_JRNL (MOD_TIMESTAMP)
  tablespace TESTTBLSPC
  pctfree 10
  initrans 2
  maxtrans 255;


create index JRU_JRNL__IX_JRU_ID on JRU_JRNL (jru_tbl_id)
  tablespace TESTTBLSPC
  pctfree 10
  initrans 2
  maxtrans 255;
