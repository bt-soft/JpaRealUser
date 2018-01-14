/*
 *  ------------------------------------------------------------------------------------
 *
 *  JPA Real User Test Monitor project
 *
 *  Module:  JruTest (JruTest)
 *  File:    JsfLib.java
 *  Created: 2018.01.13. 21:17:26
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.jru.core.jsf;

import hu.btsoft.jru.core.version.VersionUtils;
import java.io.Serializable;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.context.RequestContext;

/**
 *
 * @author BT
 */
@Named(value = "jsfLib")
@ApplicationScoped
public class JsfLib implements Serializable {

    /**
     * Verzió lekérdezése a Maven által karbantartott versionInfo.properties állományból
     *
     * @return verzió String
     */
    public String getVersion() {
        return VersionUtils.getModuleVersionStr(JsfLib.class);
    }

    /**
     * JSF implementáció verziójának lekérdezése
     *
     * @return Mojarra verziója
     */
    public String getJsfVersion() {
        return String.format("%s v%s (%s)",
                FacesContext.class.getPackage().getImplementationTitle(),
                FacesContext.class.getPackage().getImplementationVersion(),
                FacesContext.class.getPackage().getImplementationVendor()
        );
    }

    /**
     * PrimeFaces verzió lekérdezése
     *
     * @return PF verzió
     */
    public String getPrimefacesVersion() {
        return RequestContext.getCurrentInstance().getApplicationContext().getConfig().getBuildVersion();
    }

    /**
     * Redit a login lapra
     *
     * @return login lap JSF rtedir URL
     */
    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/login.xhtml?faces-redirect=true";
    }

    /**
     * Aktuális user lekérése
     *
     * @return user
     */
    public static String getCurrentUser() {
        return FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
    }

}
