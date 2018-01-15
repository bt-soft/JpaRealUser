/*
 *  ------------------------------------------------------------------------------------
 *
 *  JPA Real User Test Monitor project
 *
 *  Module:  JruTest (JruTest)
 *  File:    tables.sql
 *  Created: 2018.01.14. 14:01:05
 *
 *  ------------------------------------------------------------------------------------
 */
/**
 * Author:  BT
 * Created: 2018.01.14.
 */

---
--- Work Table
---

-- Create table
create table JRU_TBL
(
  id  NUMBER not null,
  txt VARCHAR2(512) not null,
  jpa_user VARCHAR2(50) not null
)
tablespace TESTTBLSPC
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 10M
    minextents 1
    maxextents unlimited
  );

-- Add comments to the table
comment on table JRU_TBL   is 'JRU Test Table';

-- Add comments to the columns
comment on column JRU_TBL.id  is 'Primary Key';
comment on column SCHEMAOWNER.JRU_TBL.jpa_user is 'JPA level user';
comment on column JRU_TBL.txt  is 'Test Data';

-- Create/Recreate primary, unique and foreign key constraints
alter table JRU_TBL
  add constraint JRU_TBL_PK primary key (ID)
  using index
  tablespace TESTTBLSPC
  pctfree 10
  initrans 2
  maxtrans 255;



---
--- Journal
---


-- Create table
create table JRU_JRNL
(
  id              NUMBER(10) not null,
  jru_tbl_id      NUMBER(10) not null,
  old_value       VARCHAR2(512),
  ora_user        VARCHAR2(50) not null,
  jpa_user        VARCHAR2(50) not null,
  mod_timestamp   TIMESTAMP(6) not null
)
tablespace TESTTBLSPC
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 10M
  );


-- Add comments to the table
comment on table JRU_JRNL  is 'JRU Test Journal table';

-- Add comments to the columns
comment on column JRU_JRNL.id  is 'Primary key';
comment on column JRU_JRNL.jru_tbl_id  is 'JRU table ID';
comment on column JRU_JRNL.old_value  is 'Old value';
comment on column JRU_JRNL.ora_user  is 'Oracle level user';
comment on column JRU_JRNL.jpa_user  is 'JPA level user';
comment on column JRU_JRNL.mod_timestamp  is 'Change date';

-- Create/Recreate primary, unique and foreign key constraints
alter table JRU_JRNL
  add constraint JRU_JNR_PK primary key (ID)
  using index
  tablespace TESTTBLSPC
  pctfree 10
  initrans 2
  maxtrans 255;


alter table JRU_JRNL
  add constraint JRU_TBL_ID_UNQ foreign key (jru_tbl_id)
  references JRU_TBL (ID);

