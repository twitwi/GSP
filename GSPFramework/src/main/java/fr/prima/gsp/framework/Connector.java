/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gsp.framework;

/**
 *
 * @author emonet
 */
/*package*/ class Connector {

    Module sourceModule;
    String sourcePort;
    Module targetModule;
    String targetPort;

    public Connector(Module sourceModule, String sourcePort, Module targetModule, String targetPort) {
        this.sourceModule = sourceModule;
        this.sourcePort = sourcePort;
        this.targetModule = targetModule;
        this.targetPort = targetPort;
    }

}
