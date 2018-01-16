/*
*  ------------------------------------------------------------------------------------
*
*  JPA Real User Test Monitor project
*
*  Module:  JruTest (JruTest)
*  File:    queries.sql
*  Created: 2018.01.14. 21:23:02
*
*  ------------------------------------------------------------------------------------
*/
/**
* Author:  BT
* Created: 2018.01.14.
*/

-- Search for faulty records where the username is different
select *
  from jru_tbl t
 inner join JRU_JRNL j
    on t.id = j.jru_tbl_id
 where t.jpa_user != j.jpa_user;

-- JPA sessions
select v.SID, v.SERIAL#, v.USERNAME, v.STATUS, v.state
  from v$session v
 where v.USERNAME = 'JPAUSER';
