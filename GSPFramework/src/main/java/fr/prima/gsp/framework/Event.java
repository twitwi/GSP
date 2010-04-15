/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gsp.framework;

/**
 *
 * Data inside this structure MUST not be modified! Be a good citizen.
 */
public class Event {
    
    private Object[] information;
    private String[] additionalTypeInformation;

    public Event(Object[] information, String[] additionalTypeInformation) {
        this.information = information;
        this.additionalTypeInformation = additionalTypeInformation;
    }

    public String[] getAdditionalTypeInformation() {
        return additionalTypeInformation;
    }

    public String getAdditionalTypeInformation(int index) {
        return additionalTypeInformation[index];
    }

    public Object[] getInformation() {
        return information;
    }

    public Object getInformation(int index) {
        return information[index];
    }

}
