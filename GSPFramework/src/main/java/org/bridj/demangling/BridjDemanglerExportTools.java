/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridj.demangling;

/**
 *
 * @author twilight
 */
public class BridjDemanglerExportTools {
    
    public static Demangler.TemplateArg[] getTemplates(Demangler.ClassRef cl) {
        return cl.ident.templateArguments;
    }
    
}
