/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.vsp;

import fr.prima.videoserviceclient.ServiceImageSource;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author emonet
 */
public class VideoServiceProcessor {

    private ServiceImageSource imageSource;
    private Map<String, Pipeline> pipelines = new HashMap<String, Pipeline>();

    public VideoServiceProcessor(ServiceImageSource imageSource) {
        this.imageSource = imageSource;
    }




}
