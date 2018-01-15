/*
 *  ------------------------------------------------------------------------------------
 *
 *  JPA Real User Test Monitor project
 *
 *  Module:  JruTest (JruTest)
 *  File:    MainView.java
 *  Created: 2018.01.13. 22:12:28
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.jru.view;

import hu.btsoft.jru.model.entity.JruJrnl;
import hu.btsoft.jru.model.entity.JruTbl;
import hu.btsoft.jru.model.service.EntityService;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author BT
 */
@Named(value = "mainView")
@ViewScoped
public class MainView extends ViewBase {

    @EJB
    private EntityService entityService;

    @Getter
    @Setter
    private String testData;

    @Getter
    private List<JruJrnl> errorList;

    @PostConstruct
    protected void init() {
        if (FacesContext.getCurrentInstance().getExternalContext().isUserInRole("APP_JRU_ADMIN")) {
            doRefreshErrorList();
        }
    }

    /**
     * Test Insert indítása
     */
    public void doTest() {
        JruTbl entity = entityService.doTest(testData, currentUser);
        if (entity.getId() != null) {
            addJsfMessage("growl", FacesMessage.SEVERITY_INFO, "OK");
        } else {
            addJsfMessage("growl", FacesMessage.SEVERITY_ERROR, "Hiba");
        }
    }

//    /**
//     * Test Insert indítása
//     */
//    public void doTestCallerPrincipal() {
//        JruTbl entity = entityService.doTest(testData);
//        if (entity.getId() != null) {
//            addJsfMessage("growl", FacesMessage.SEVERITY_INFO, "OK");
//        } else {
//            addJsfMessage("growl", FacesMessage.SEVERITY_ERROR, "Hiba");
//        }
//    }
    /**
     * Eltérő JPA userek kigyűjtése
     */
    @RolesAllowed("APP_JRU_ADMIN")
    public void doRefreshErrorList() {
        errorList = entityService.findAllErrors();
    }
}
