/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.vsp.modules.omiscid;

import fr.prima.omiscid.user.connector.ConnectorListener;
import fr.prima.omiscid.user.connector.ConnectorType;
import fr.prima.omiscid.user.connector.Message;
import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.util.Utility;
import fr.prima.omiscid.user.variable.LocalVariableListener;
import fr.prima.omiscid.user.variable.VariableAccessType;
import fr.prima.vsp.modules.AbstractModule;
import fr.prima.vsp.modules.ModuleParameter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Element;

/**
 *
 * @author emonet
 */
public class OmiscidMessageOutput extends AbstractModule {


    @ModuleParameter(initOnly=true)
    public String serviceName = "VSPMessageOutput";

    @ModuleParameter(initOnly=true)
    public String connectorName = "out";

    @ModuleParameter(initOnly = true)
    public String constantName = "";
    @ModuleParameter(initOnly = true)
    public String constantValue = "";

    @ModuleParameter(initOnly = true)
    public String constant2Name = "";
    @ModuleParameter(initOnly = true)
    public String constant2Value = "";

    @ModuleParameter(initOnly = true)
    public String constant3Name = "";
    @ModuleParameter(initOnly = true)
    public String constant3Value = "";

    @ModuleParameter(initOnly = true)
    public boolean killable = false;
    private static final String killableVariableName = "killService";



    Service service;
    @Override
    public void initModule() {
        allowOpenEvents = true;
        try {
            service = OmiscidTools.createService(serviceName);
            service.addConnector(connectorName, "sends message from a processor", ConnectorType.OUTPUT);
            service.addConnectorListener(connectorName, omiscidListener());
            if (!constantName.isEmpty()) {
                service.addVariable(constantName, "...", "...", VariableAccessType.CONSTANT);
                service.setVariableValue(constantName, constantValue);
            }
            if (!constant2Name.isEmpty()) {
                service.addVariable(constant2Name, "...", "...", VariableAccessType.CONSTANT);
                service.setVariableValue(constant2Name, constant2Value);
            }
            if (!constant3Name.isEmpty()) {
                service.addVariable(constant3Name, "...", "...", VariableAccessType.CONSTANT);
                service.setVariableValue(constant3Name, constant3Value);
            }
            if (killable) {
                service.addVariable(killableVariableName, "...", "...", VariableAccessType.READ_WRITE);
                service.setVariableValue(killableVariableName, "alive");
                service.addLocalVariableListener(killableVariableName, new LocalVariableListener() {
                    public void variableChanged(Service service, String variableName, String value) {
                        service.stop();
                    }
                    public boolean isValid(Service service, String variableName, String newValue) {
                        return true;
                    }
                });
            }
            service.start();
        } catch (IOException ex) {
            Logger.getLogger(OmiscidMessageOutput.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void stopModule() {
        service.stop();
    }

    public void outputConstantValue() {
        message(constantValue);
    }

    public void message(Object s) {
        service.sendToAllClients(connectorName, Utility.message(s.toString()));
    }

    public void xmlMessage(Element e) {
        service.sendToAllClients(connectorName, Utility.Xml.elementToByteArray(e));
    }

    private ConnectorListener omiscidListener() {
        return new ConnectorListener() {
            public void messageReceived(Service arg0, String arg1, Message arg2) {
            }
            public void disconnected(Service arg0, String arg1, int arg2) {
                emitNamedEvent("omiscid_disconnected", arg0, arg1, arg2);
                if (arg0.getConnectorClientCount(arg1) == 0) {
                    emitNamedEvent("omiscid_nomoreclients", arg0, arg1, arg2);
                }
            }
            public void connected(Service arg0, String arg1, int arg2) {
                emitNamedEvent("omiscid_connected", arg0, arg1, arg2);
            }
        };
    }
    

}
