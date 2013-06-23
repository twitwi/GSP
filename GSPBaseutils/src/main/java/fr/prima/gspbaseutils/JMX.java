package fr.prima.gspbaseutils;

import com.j256.simplejmx.common.JmxOperation;
import com.j256.simplejmx.common.JmxResource;
import com.j256.simplejmx.server.JmxServer;
import fr.prima.gsp.framework.Assembly;
import fr.prima.gsp.framework.ModuleParameter;
import fr.prima.gsp.framework.spi.AbstractModule;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.JMException;

/**
 *
 * @author remonet
 */
@JmxResource(description = "GSP Module Controller", domainName = "fr.prima.gspbaseutils", beanName = "JMX")
public class JMX extends AbstractModule {

    // This will be automatically injected by the framework after setup and before init
    @ModuleParameter(initOnly = true)
    public Assembly assembly;
    // The port where to contact the JMX server
    @ModuleParameter
    public int port = -1;

    @JmxOperation(description = "Generic setter on GSP module")
    public void updateModuleParameter(String fullName, String value) {
        assembly.setModuleParameter(fullName, value);
    }

    @Override
    protected void initModule() {
        JmxServer jmx = port == -1 ? new JmxServer() : new JmxServer(port);
        try {
            jmx.start();
            jmx.register(this);
        } catch (JMException ex) {
            Logger.getLogger(JMX.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
