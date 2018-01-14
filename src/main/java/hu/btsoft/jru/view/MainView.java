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

import hu.btsoft.jru.model.entity.JruTbl;
import hu.btsoft.jru.model.service.JruService;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
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
    private JruService jruService;

    @Getter
    @Setter
    private String testData;

    public void doTest() {
        JruTbl entity = jruService.doTest(testData, currentUser);
        if (entity.getId() != null) {
            addJsfMessage("growl", FacesMessage.SEVERITY_INFO, "OK");
        } else {
            addJsfMessage("growl", FacesMessage.SEVERITY_ERROR, "Hiba");
        }

    }
}
