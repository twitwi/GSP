/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.vsp.modules.omiscid;

import fr.prima.omiscid.user.connector.ConnectorListener;
import fr.prima.omiscid.user.connector.ConnectorType;
import fr.prima.omiscid.user.connector.Message;
import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.variable.VariableAccessType;
import fr.prima.vsp.modules.AbstractModule;
import fr.prima.vsp.modules.ModuleParameter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author twilight
 */
public class OmiscidEventReceiver extends AbstractModule {

    @ModuleParameter(initOnly=true)
    public String serviceName = "VSPEventInput";

    @ModuleParameter(initOnly=true)
    public String connectorName = "in";

    @ModuleParameter(initOnly=true)
    public String connectorDescription = "accepts:...";

    @ModuleParameter(initOnly = true)
    public String constantName = "";

    @ModuleParameter(initOnly = true)
    public String constantValue = "";

    Service service;
    @Override
    public void initModule() {
        allowOpenEvents = true;
        try {
            service = OmiscidTools.createService(serviceName);
            service.addConnector(connectorName, "receives events in the form of a single string", ConnectorType.INPUT);
            service.addConnectorListener(connectorName, omiscidListener());
            if (constantName.length() > 0) {
                service.addVariable(constantName, "...", "...", VariableAccessType.CONSTANT);
                service.setVariableValue(constantName, constantValue);
            }
            service.start();
        } catch (IOException ex) {
            Logger.getLogger(OmiscidEventReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void stopModule() {
        service.stop();
    }

    private ConnectorListener omiscidListener() {
        return new ConnectorListener() {
            public void messageReceived(Service arg0, String arg1, Message arg2) {
                emitNamedEvent("omiscid_messageReceived", arg0, arg1, arg2);
                String stringMessage = arg2.getBufferAsStringUnchecked();
                if (stringMessage == null) {
                    emitNamedEvent("omiscid_nullString");
                } else {
                    emitNamedEvent(stringMessage);
                }
            }
            public void disconnected(Service arg0, String arg1, int arg2) {
                emitNamedEvent("omiscid_disconnected", arg0, arg1, arg2);
            }
            public void connected(Service arg0, String arg1, int arg2) {
                emitNamedEvent("omiscid_connected", arg0, arg1, arg2);
            }
        };
    }


}
