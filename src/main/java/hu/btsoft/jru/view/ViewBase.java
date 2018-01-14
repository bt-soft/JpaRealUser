/*
 *  ------------------------------------------------------------------------------------
 *
 *  JPA Real User Test Monitor project
 *
 *  Module:  JruTest (JruTest)
 *  File:    ViewBase.java
 *  Created: 2018.01.13. 22:19:50
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.jru.view;

import hu.btsoft.jru.core.jsf.JsfLib;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 *
 * @author BT
 */
public class ViewBase implements Serializable {

    protected String currentUser;

    /**
     *
     */
    @PostConstruct
    protected void superInit() {
        //Az aktuálisan bejelentkezett user lesz a változtató user
        currentUser = JsfLib.getCurrentUser();
    }

    /**
     * JSF message megjelenítése
     *
     * @param clientId JSF message id
     * @param severity sújosság
     * @param msg      szöveg
     */
    protected void addJsfMessage(String clientId, FacesMessage.Severity severity, String msg) {
        String summary = "nemtom";
        if (FacesMessage.SEVERITY_INFO == severity) {
            summary = "Információ";
        } else if (FacesMessage.SEVERITY_WARN == severity) {
            summary = "Figyelmeztetés";
        } else if (FacesMessage.SEVERITY_ERROR == severity) {
            summary = "Hiba";
        } else if (FacesMessage.SEVERITY_FATAL == severity) {
            summary = "Súlyos hiba";
        }
        FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(severity, summary, msg));
    }
}
