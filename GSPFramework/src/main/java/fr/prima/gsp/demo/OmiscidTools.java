/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gsp.demo;

import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.service.impl.ServiceFactoryImpl;

/**
 *
 * @author emonet
 */
public class OmiscidTools {

    public static Service createService(String serviceName) {
        return new ServiceFactoryImpl().create(serviceName);
    }

}
