package gov.nih.nci.ncia.components;

import java.io.IOException;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

/**
 * This is a mostly mechanical conversion of the old HelpTag.
 * <li>Moved <img> into here (from pages)
 * <li>Can't output & for XHTML/Facelets - must output &amp;
 */
public class HelpComponent extends UIComponentBase {
    public void encodeBegin(FacesContext context) throws IOException {
        String label = (String) getAttributes().get("label");
        String helpId = (String) getAttributes().get("helpId");
        String help = (String) getAttributes().get("help");

    	label = "<img src='"+label+"' alt='Online Help' border='0' />";

        ResponseWriter writer = context.getResponseWriter();

        if (help != null) {
            String helpMessage = "";
            FacesContext facesContext = FacesContext.getCurrentInstance();
            Object message = facesContext.getApplication()
                                         .createValueBinding(help)
                                         .getValue(facesContext);
            String helpText = (String) facesContext.getApplication()
                                                   .createValueBinding("#{msg.help_text}")
                                                   .getValue(facesContext);

            if (message instanceof String) {
                helpMessage = (String) message;
            } else {
                helpMessage = help;
            }

            if (label != null) {
            	writer
                  .write("<a href=\"javascript:void(0);\" onmouseover=\"return overlib('" +
                    helpMessage + "', CAPTION, '" + helpText +
                    "');\" onmouseout=\"return nd();\">" + label + "</a>");
            } else {
            	writer
                  .write("<a href=\"javascript:void(0);\" onmouseover=\"return overlib('" +
                    helpMessage + "', CAPTION, '" + helpText +
                    "');\" onmouseout=\"return nd();\">[?]</a>");
            }
        } else if (helpId != null) {
            if (label != null) {
            	writer
                  .write("<a href=\"" + link + helpId + "\" target=\"_blank\" >" +
                    label + "</a>");
            } else {
            	writer
                  .write("<a href=\"" + link + helpId + "\" target=\"_blank\" >" +
                    "[?]" + "</a>");
            }
        } else {
        	writer.write("");
        }
    }

    public String getFamily() {
        return "HelpFamily";
    }


    private String link = "/ncia/HTML/help/index.html?single=true&amp;context=NBIA_Online_Help&amp;topic=";
}