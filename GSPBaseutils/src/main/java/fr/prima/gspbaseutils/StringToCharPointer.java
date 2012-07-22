/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gspbaseutils;

import fr.prima.gsp.framework.NativePointer;
import fr.prima.gsp.framework.nativeutil.NativeType;
import fr.prima.gsp.framework.spi.AbstractModule;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bridj.Pointer;

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
            output(Pointer.pointerToBuffer(buf));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(StringToCharPointer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void output(Pointer charPointer) {
        emitEvent(new NativePointer(charPointer, NativeType.CHAR_POINTER));
    }

}
