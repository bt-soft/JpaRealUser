/*
 *  ------------------------------------------------------------------------------------
 *
 *  JPA Real User Test Monitor project
 *
 *  Module:  JruTest (JruTest)
 *  File:    sys.sql
 *  Created: 2018.01.14. 14:11:51
 *
 *  ------------------------------------------------------------------------------------
 */
/**
 * Author:  BT
 * Created: 2018.01.14.
 */

---
--- Execute from sys Oracle manager account
---

-- tablespace
create tablespace testtblspc datafile '/oracle/DB/oradata/test/testtblspc.dbf' size 100M extent management local autoallocate;

-- schemaowner account
create user schemaowner identified by schemaowner  default tablespace testtblspc;
grant create session to schemaowner;
grant connect to schemaowner;
grant resource to schemaowner;
grant select_catalog_role to schemaowner;


-- jru_role
create role jru_role not identified;

-- JPA technical user
create user jpauser identified by jpauser default tablespace testtblspc;
grant create session to jpauser;
grant connect to jpauser;
grant resource to jpauser;
grant jru_role to jpauser;
