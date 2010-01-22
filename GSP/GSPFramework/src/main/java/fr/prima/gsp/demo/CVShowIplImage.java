/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gsp.demo;

import fr.prima.gsp.framework.ModuleParameter;
import fr.prima.gsp.framework.spi.AbstractModuleEnablable;
import fr.prima.jna.opencv.cxtypes.CxtypesLibrary;
import fr.prima.jna.opencv.cxtypes.IplImage;
import fr.prima.videoserviceclient.ByteBufferDataBuffer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.color.ColorSpace;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Hashtable;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

/**
 *
 * @author emonet
 */
public class CVShowIplImage extends AbstractModuleEnablable {

    @ModuleParameter
    public String title = "?";

    private BufferedImage bufferedImage;

    private JFrame f;
    private JPanel p;

    @Override
    public void stopModule() {
        f.dispose();
    }


    public CVShowIplImage() {
    }
    
    @Override
    public void initModule() {
        f = new JFrame(title);
        f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        f.setSize(new Dimension(300, 200));
        f.setLocation(100, new Random().nextInt(200));
        f.setLayout(new BorderLayout());
        f.add(p = new JPanel());
        p.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.isControlDown()) {
                    message("Mouse x:"+e.getX()+" y:"+e.getY());
                } else {
                    setEnabled(!isEnabled());
                }
            }
        });
        f.setVisible(true);
    }

    private void message(String msg) {
        emitEvent(msg);
    }
    @Override
    protected synchronized void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        f.setTitle(title+(enabled ? "" : " (paused)"));
        p.setBorder(enabled ? null : new LineBorder(Color.RED, 2));
        p.repaint();
    }


    public synchronized void input(IplImage im) {
        if (!isEnabled()) return;
        p.removeAll();
        bufferedImage = iplToBuffered(im);
        JLabel jLabel = new JLabel(new ImageIcon(bufferedImage));
        p.add(jLabel);
        f.pack();
    }

    public synchronized void drawRectangle(int x, int y, int w, int h) {
        bufferedImage.createGraphics().drawRect(x, y, w, h);
        f.repaint();
    }

    public static BufferedImage iplToBuffered(IplImage im) {
        int[] indices = new int[]{0};
        int dataType = DataBuffer.TYPE_BYTE;
        int cSpace = ColorSpace.CS_sRGB;
        int pixelStride = im.nChannels;
        if (im.depth == (CxtypesLibrary.IPL_DEPTH_SIGN | 32)) {
            indices = im.nChannels == 1 ? new int[]{0} : new int[]{2, 1, 0};
            dataType = DataBuffer.TYPE_BYTE;
            cSpace = im.nChannels == 1 ? ColorSpace.CS_GRAY : ColorSpace.CS_sRGB;
            pixelStride = im.nChannels;
            ComponentSampleModel sampleModel = new ComponentSampleModel(
                    dataType,
                    im.width, im.height,
                    pixelStride, im.width*pixelStride,
                    indices);
            ColorSpace colorSpace = ColorSpace.getInstance(cSpace);
            ComponentColorModel colorModel = new ComponentColorModel(colorSpace, false, false, ColorModel.OPAQUE, dataType);
            int imLength = im.imageSize;
            DataBufferByte dataBufferByte = new DataBufferByte(imLength);
            {
                byte[] data = dataBufferByte.getData();
                float max = 0;
                for (float f : im.imageData.getFloatArray(0, imLength/4)) {
                    max = Math.max(max, f);
                }
                //System.err.println("MAX: "+max);
                int i = 0;
                for (float f : im.imageData.getFloatArray(0, imLength/4)) {
                    data[i] = (byte) (255 * f / max);
                    i++;
                }
            }
            WritableRaster raster = Raster.createWritableRaster(
                    sampleModel,dataBufferByte,
                    null);
            //raster = colorModel.createCompatibleWritableRaster(w, h);
            BufferedImage bufferedImage = new BufferedImage(
                    colorModel,
                    raster,
                    false,
                    new Hashtable<String, String>());
            return bufferedImage;

        } else if (im.depth == (CxtypesLibrary.IPL_DEPTH_8U)) {
            indices = im.nChannels == 1 ? new int[]{0} : new int[]{2, 1, 0};
            dataType = DataBuffer.TYPE_BYTE;
            cSpace = im.nChannels == 1 ? ColorSpace.CS_GRAY : ColorSpace.CS_sRGB;
            pixelStride = im.nChannels;
            ComponentSampleModel sampleModel = new ComponentSampleModel(
                    dataType,
                    im.width, im.height,
                    pixelStride, im.widthStep,
                    indices);
            ColorSpace colorSpace = ColorSpace.getInstance(cSpace);
            ComponentColorModel colorModel = new ComponentColorModel(colorSpace, false, false, ColorModel.OPAQUE, dataType);
            int imLength = im.imageSize;
            WritableRaster raster = Raster.createWritableRaster(
                    sampleModel,
                    //new DataBufferByte(im.imageData.getPointer().getByteArray(0, imLength), imLength),
                    new ByteBufferDataBuffer(im.imageData.getByteBuffer(0, imLength)),
                    null);
            //raster = colorModel.createCompatibleWritableRaster(w, h);
            BufferedImage bufferedImage = new BufferedImage(
                    colorModel,
                    raster,
                    false,
                    new Hashtable<String, String>());
            return bufferedImage;
        }
        return null;
    }

}
