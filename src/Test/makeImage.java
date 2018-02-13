import ImageProcessing.Filter;
import ImageProcessing.HoughTransform;
import ImageProcessing.Shading;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * PACKAGE_NAME
 * Created by e155763 on 2018/01/31.
 */
public class makeImage {
    public static void main(String args[]) throws IOException {
        BufferedImage imageFile;

        int w;
        int h;

        int pix[];
        int newPix[];

        imageFile = ImageIO.read(new File("/Users/e155763/src/java/subject/6/objectsH29.jpg"));

        w = imageFile.getWidth(null);
        h = imageFile.getHeight(null);

        pix = imageFile.getRGB(0, 0, w, h, null, 0, w);
        newPix = new int[w*h];

        Shading sh = new Shading();
        Filter filter = new Filter(w, h);
        HoughTransform ht = new HoughTransform(w,h);

        // 一枚めの画像処理
        //sh.toGrayScale(pix, w, h);
        //filter.gaussianFilter(pix, w, h, 5, 0.3);
        //filter.sobelFilterY(pix, w, h);
        //filter.sobelFilterX(pix, w, h);
        //sh.toBinaryImage(pix, w, h);
        //sh.toNot(pix, w, h);
        //sh.toneCurve(pix, newPix, 3.0);
        int counter = 0;
        double a;
        int b, c;
        Random rand = new Random();
        for(int i = 0; counter != 5; i++){
            a = rand.nextDouble() + 0.3;
            b = rand.nextInt(255) + 1;
            c = rand.nextInt(b);


            filter.canny(pix, newPix, a, c,b);
            counter = ht.circle(newPix, 28, 35, 50, 10);

            imageFile = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
            imageFile.setRGB(0,0,w,h,newPix,0,w);
            newPix = imageFile.getRGB(0, 0, w, h, null, 0, w);

            System.out.println(a);
            System.out.println(b);
            System.out.println(c);

            ImageIO.write(imageFile, "png", new File("/Users/e155763/src/java/subject/6/test" + i + ".png"));
        }

    }
}
