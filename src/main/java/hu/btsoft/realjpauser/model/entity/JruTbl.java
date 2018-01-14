/*
 *  ------------------------------------------------------------------------------------
 *
 *  JPA Real User Test Monitor project
 *
 *  Module:  JruTest (JruTest)
 *  File:    JruTbl.java
 *  Created: 2018.01.13. 21:19:18
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.realjpauser.model.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author BT
 */
@Entity
@Table(name = "JRU_TBL", catalog = "", schema = "SCHEMAOWNER")
@Data
public class JruTbl implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    //Before Insert Trigger  -> do not have to deal with the sequence and @NotNull
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "JRU_SEQ")
    @SequenceGenerator(name = "JRU_SEQ", sequenceName = "JRU_SEQ", schema = "SCHEMAOWNER")
    private Long id;

    @Column(name = "JPA_USER", nullable = false)
    private String jpaUser;

    @Column(name = "TXT", nullable = false)
    private String testData;

}
