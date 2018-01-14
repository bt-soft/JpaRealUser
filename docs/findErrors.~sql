/*
 *  ------------------------------------------------------------------------------------
 *
 *  JPA Real User Test Monitor project
 *
 *  Module:  JruTest (JruTest)
 *  File:    findErrors.sql
 *  Created: 2018.01.14. 21:23:02
 *
 *  ------------------------------------------------------------------------------------
 */
/**
 * Author:  BT
 * Created: 2018.01.14.
 */

select *
  from jru_tbl t
 inner join JRU_JRNL j
    on t.id = j.jru_tbl_id
 where t.jpa_user != j.jpa_user
