/*
 *  ------------------------------------------------------------------------------------
 *
 *  JPA Real User Test Monitor project
 *
 *  Module:  JruTest (JruTest)
 *  File:    JruJrnl.java
 *  Created: 2018.01.14. 18:22:33
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.jru.model.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 *
 * @author BT
 */
@Entity
@Cacheable(false)
@Table(name = "JRU_JRNL", catalog = "", schema = "SCHEMAOWNER")
@Data
@NamedQueries({
    @NamedQuery(name = "JruJrnl.findAllErrors", query = "SELECT j from JruJrnl j INNER JOIN j.jruTbl t WHERE j.jruTbl.id = t.id AND j.clientIdentifier != t.paramUser ORDER BY j.modTimestamp DESC")
})
public class JruJrnl implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @NotNull
    @Column(name = "ID", nullable = false)
    private Long id;

    @Size(max = 512)
    @Column(name = "OLD_VALUE", length = 512)
    private String oldValue;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "ORA_USER", nullable = false, length = 50)
    private String oraUser;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "CLIENT_IDENTIFIER", nullable = false, length = 50)
    private String clientIdentifier;

    @NotNull
    @Column(name = "MOD_TIMESTAMP", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modTimestamp;

    @ManyToOne(optional = false)
    @JoinColumn(name = "JRU_TBL_ID", referencedColumnName = "ID", nullable = false)
    private JruTbl jruTbl;

}
