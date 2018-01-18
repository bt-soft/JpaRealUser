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
package hu.btsoft.jru.model.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author BT
 */
@Entity
@Cacheable(false)
@Table(name = "JRU_TBL", catalog = "", schema = "SCHEMAOWNER")
@Data
public class JruTbl implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Oracle SEQ
     */
    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "JRU_SEQ")
    @SequenceGenerator(name = "JRU_SEQ", sequenceName = "JRU_SEQ", schema = "SCHEMAOWNER")
    private Long id;

    /**
     * Paraméterben megadott user /
     */
    @Column(name = "PARAM_USER", nullable = false)
    private String paramUser;

    @Column(name = "TXT", nullable = false)
    private String testData;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "jruTbl", fetch = FetchType.EAGER)
    private List<JruJrnl> jruJrnls;
}
