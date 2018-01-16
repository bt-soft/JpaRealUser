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
create or replace trigger JRU_TBL_BI_TRG
  before insert
  on JRU_TBL
  for each row
declare
begin
  SELECT JRU_SEQ.NEXTVAL
  INTO   :new.id
  FROM   dual;
end JRU_TBL_BI_TRG;
/

---
--- Journal
---

create or replace trigger JRU_JRNL_BI_TRG
  before insert
  on JRU_JRNL
  for each row
declare
begin
  SELECT JRU_SEQ.NEXTVAL
  INTO   :new.id
  FROM   dual;
end JRU_JRNL_BI_TRG;
/


create or replace trigger JRU_JRNL_AIUD_TRG
  after insert or update or delete on JRU_TBL
  for each row
declare
begin
  INSERT INTO JRU_JRNL
    (JRU_TBL_ID, OLD_VALUE, ORA_USER, CLIENT_IDENTIFIER, MOD_TIMESTAMP)
  VALUES
    (:new.id,
     :old.txt,
     (select user as from dual),
     NVL((SELECT SYS_CONTEXT('userenv', 'client_identifier') FROM dual),
         '! client_identifier not set !'),
     
     SYSDATE);
EXCEPTION
  WHEN OTHERS THEN
    INSERT INTO JRU_JRNL (JRU_TBL_ID, OLD_VALUE) VALUES (:new.id, 'ERROR');
end JRU_JRNL_AIUD_TRG;
/
