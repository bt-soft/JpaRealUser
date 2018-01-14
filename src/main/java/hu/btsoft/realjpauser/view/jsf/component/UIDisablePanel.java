/*
 *  ------------------------------------------------------------------------------------
 *
 *  JPA Real User Test Monitor project
 *
 *  Module:  JruTest (JruTest)
 *  File:    UIDisablePanel.java
 *  Created: 2018.01.01. 16:20:38
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.realjpauser.view.jsf.component;

import java.io.IOException;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.context.FacesContext;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.component.datatable.DataTable;

/**
 * A JSF konténeren elhelyezkedő összes JSF komponens tiltására képes Panel
 *
 * ötlet:
 *
 * @see https://stackoverflow.com/questions/3152561/how-to-disable-page-form-in-jsf
 *
 * @author BT
 */
@FacesComponent("hu.btsoft.realjpauser.view.jsf.component.UIDisablePanel")
public class UIDisablePanel extends UIComponentBase {

    private static final String OWN_ATTRIBUTE_FLAG = UIDisablePanel.class.getSimpleName() + "Flag";
    private static final String DISABLED_ATTRIBUTE = "disabled";
    private static final String UI_STATE_DISABLED_STYLECLASS = "ui-state-disabled";
    private static final String PF_DATATABLE_ROWSTYLECLASS_ATTRIBUTE = "rowStyleClass";

    /**
     * Konstruktor
     */
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public UIDisablePanel() {
        setRendererType(null);
    }

    /**
     *
     * @param context
     *
     * @throws IOException
     */
    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        processDisablePanel(this, isDisabled());
    }

    /**
     * A HtmlOutputLabel stílus osztályának birizgálása
     *
     * @param htmlOutputLabel JSF HTMLOutputLabel komponens példány
     * @param toDisable       enable/disable
     */
    private void setHtmlOutputLabelStyleClass(HtmlOutputLabel htmlOutputLabel, boolean toDisable) {

        //Most mi az állapota?
        String currStyleClass = (String) htmlOutputLabel.getStyleClass();

        if (toDisable) {

            if (currStyleClass == null || !currStyleClass.contains(UI_STATE_DISABLED_STYLECLASS)) {
                htmlOutputLabel.getAttributes().put(OWN_ATTRIBUTE_FLAG, true); //megjelöljük, hogy mi raktuk rá az attribútumot

                //beállítjuk, hogy tiltott a komponens
                String s = currStyleClass == null ? UI_STATE_DISABLED_STYLECLASS : currStyleClass + " " + UI_STATE_DISABLED_STYLECLASS;
                htmlOutputLabel.setStyleClass(s);
            }

        } else {

            if (htmlOutputLabel.getAttributes().get(OWN_ATTRIBUTE_FLAG) != null) {  //csak a magunk által beállított attribútummal foglalkozunk
                htmlOutputLabel.getAttributes().remove(OWN_ATTRIBUTE_FLAG); //leszedjük a saját attribútumot

                if (currStyleClass != null) {
                    String s = StringUtils.normalizeSpace(currStyleClass.replaceAll(UI_STATE_DISABLED_STYLECLASS, ""));
                    htmlOutputLabel.setStyleClass(s);
                }
            }
        }

    }

    /**
     * Komponens tiltása/engedélyezése
     * PrimeFaces DataTable komponensnél a rowStyleClass-t is állítgatja
     *
     * @param uiComponent JSF komponens példány
     * @param toDisable   enable/disable
     * @param attribute   a komponens attribútuma, amely jelzi a tiltást
     */
    private void setComponentDisable(UIComponent uiComponent, boolean toDisable, String attribute) {
        if (toDisable) {
            //Most mi az állapota?
            Boolean curState = (Boolean) uiComponent.getAttributes().get(attribute);

            if (curState == null || curState == false) {
                uiComponent.getAttributes().put(OWN_ATTRIBUTE_FLAG, true); //megjelöljük, hogy mi raktuk rá az attribútumot
                uiComponent.getAttributes().put(attribute, true); //beállítjuk, hogy tiltott a komponens

                //Primefaces datatable?
                //Ha igen akkor beállítjuk a rowStyleClass-t is
                if (uiComponent instanceof DataTable) {
                    uiComponent.getAttributes().put(PF_DATATABLE_ROWSTYLECLASS_ATTRIBUTE, UI_STATE_DISABLED_STYLECLASS);
                }
            }

        } else {

            if (uiComponent.getAttributes().get(OWN_ATTRIBUTE_FLAG) != null) {  //csak a magunk által beállított attribútummal foglalkozunk
                uiComponent.getAttributes().remove(OWN_ATTRIBUTE_FLAG); //leszedjük a saját attribútumot
                uiComponent.getAttributes().put(attribute, false); //beállítjuk, hogy nem tiltott a komponens

                //Primefaces datatable?
                //Ha igen akkor leszedjük a rowStyleClass-t is
                if (uiComponent instanceof DataTable) {
                    uiComponent.getAttributes().remove(PF_DATATABLE_ROWSTYLECLASS_ATTRIBUTE);
                }

            }
        }

    }

    /**
     * A JSF komponensfán véggigyalogolava engedélyezi/tiltja az erre alkalmas komponenseket
     *
     * @param root      komponensfa gyökér elem
     * @param toDisable tiltás/engedélyezés
     */
    public void processDisablePanel(UIComponent root, boolean toDisable) {

        //végigmegyünk az összes gyerek komponensen
        root.getChildren().stream().map((UIComponent c) -> {

            //if (c instanceof UIInput || c instanceof UICommand) {
            if (c instanceof HtmlOutputLabel) { //JSF output (label, text, ...) ?

                setHtmlOutputLabelStyleClass((HtmlOutputLabel) c, toDisable);

            } else if (c instanceof DataTable) { //Primefaces datatable?
                setComponentDisable(c, toDisable, DataTable.PropertyKeys.disabledSelection.toString());

            } else if (c instanceof UIComponentBase) { //Minden más JSF UI komponensen vágiggyaloglunk
                setComponentDisable(c, toDisable, DISABLED_ATTRIBUTE);
            }
            return c;

        }).filter((c) -> (c.getChildCount() > 0)).forEachOrdered((c) -> {

            //Ha a komponensnek van(nak) további gyerek komponense(i), akkor rekurzívan azokon is végigmegyünk
            processDisablePanel(c, toDisable);
        });

    }

    /**
     * <p>
     * Return the identifier of the component family to which this component belongs.
     * This identifier, in conjunction with the value of the <code>rendererType</code> property,
     * may be used to select the appropriate {@link Renderer} for this component instance.</p>
     *
     * @return
     */
    @Override
    public String getFamily() {
        return "uiDisablePanel";
    }

    /**
     * Disabled attribútum elkérése
     *
     * @return true/false
     */
    public boolean isDisabled() {
        return (boolean) getStateHelper().eval(DISABLED_ATTRIBUTE, false);
    }

    /**
     * Disabled attribútum beállítása
     *
     * @param disabled új attribútum
     */
    public void setDisabled(boolean disabled) {
        getStateHelper().put(DISABLED_ATTRIBUTE, disabled);
    }

}
