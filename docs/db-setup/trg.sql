/*
 *  ------------------------------------------------------------------------------------
 *
 *  JPA Real User Test Monitor project
 *
 *  Module:  JruTest (JruTest)
 *  File:    trg.sql
 *  Created: 2018.01.14. 14:08:09
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
create or replace trigger JRU_TBL_BI
  before insert
  on JRU_TBL
  for each row
declare
begin
  SELECT JRU_SEQ.NEXTVAL
  INTO   :new.id
  FROM   dual;
end JRU_TBL_BI;
/


---
--- Journal
---

create or replace trigger JRU_JRNL_BI
  before insert
  on JRU_JRNL
  for each row
declare
begin
  SELECT JRU_SEQ.NEXTVAL
  INTO   :new.id
  FROM   dual;
end JRU_JRN_BI;
/


create or replace trigger JRU_JRNL_TRG
  after insert or update or delete on JRU_TBL
  for each row
declare
begin
  INSERT INTO JRU_JRNL
    (JRU_TBL_ID, OLD_VALUE, NEW_VALUE, ORA_USER, JPA_USER, MOD_TIMESTAMP)
  VALUES
    (:new.id,
     :old.txt,
     :new.txt,
     (select user as ora_user from dual),
     NVL((SELECT SYS_CONTEXT('userenv', 'client_identifier') AS JPA_USER
           FROM dual),
         '! client_identifier not set !'),

     SYSDATE);
EXCEPTION
  WHEN OTHERS THEN
    INSERT INTO JRU_JRNL
      (JRU_TBL_ID, OLD_VALUE, NEW_VALUE)
    VALUES
      (:new.id, 'ERROR', 'ERROR');
end JRU_TBL_JRN;
/