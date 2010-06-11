/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gspbaseutils;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import fr.prima.gsp.framework.spi.AbstractModule;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author twilight
 */
public class StringToCharPointer extends AbstractModule {

    public void input(String s) {
        try {
            byte[] bytes = s.getBytes("utf-8");
            ByteBuffer buf = ByteBuffer.allocateDirect(bytes.length + 1);
            buf.put(bytes);
            buf.put((byte) 0);
            output(Native.getDirectBufferPointer(buf));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(StringToCharPointer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void output(Pointer charPointer) {
        emitEvent(charPointer);
    }


}
