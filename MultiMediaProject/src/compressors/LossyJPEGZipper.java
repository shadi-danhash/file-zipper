package compressors;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

public class LossyJPEGZipper {

    public static boolean zip(String src, String destWithOutExt , float quality) {
        try {
            File input = new File(src);
            BufferedImage image = ImageIO.read(input);

            File compressedImageFile = new File(destWithOutExt + ".jpg");
            OutputStream os = new FileOutputStream(compressedImageFile);

            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
            ImageWriter writer = (ImageWriter) writers.next();

            ImageOutputStream ios = ImageIO.createImageOutputStream(os);
            writer.setOutput(ios);

            ImageWriteParam param = writer.getDefaultWriteParam();

            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);  // Change the quality value you prefer
            writer.write(null, new IIOImage(image, null, null), param);

            os.close();
            ios.close();
            writer.dispose();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public static boolean zip(String src, String destWithOutExt ){
        return zip( src,  destWithOutExt , 0.05f );
    }
}
