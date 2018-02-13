//filter.java
//

import ImageProcessing.Filter;
import ImageProcessing.HoughTransform;
import ImageProcessing.Shading;

import javax.imageio.ImageIO;
import java.applet.Applet;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;


public class myApplet extends Applet {

    private Image img;
    private Image new_img;
    private BufferedImage imageFile;

    private int w = 0;
    private int h = 0;

    private int pix[];
    private int newPix[];

    // Color color;
    // Color bin;

    public void init(){
        img = getImage(getCodeBase(),"objectsH29.jpg");

        MediaTracker mt = new MediaTracker(this);
        mt.addImage(img,0);
        try{
            mt.waitForID(0);
        } catch (InterruptedException e){
            System.out.println("画像が読み込めません");
        }


        w = img.getWidth(this);
        h = img.getHeight(this);

        pix = new int[w*h];
        newPix = new int[w*h];

        try {
            setPix();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setPix() throws IOException {
        try{
            PixelGrabber pg = new PixelGrabber(img,0,0,w,h,pix,0,w);
            pg.grabPixels();
        }
        catch(InterruptedException e){}

        Shading sh = new Shading();
        Filter filter = new Filter(w, h);
        HoughTransform ht = new HoughTransform(w,h);

        // 一枚めの画像処理
        sh.toGrayScale(pix, newPix);
        //sh.toneCurve(newPix, newPix, 80, 230);
        sh.histogramEqualization(newPix,newPix);
        filter.canny(newPix, newPix, 0.5, 30,100);
        ht.circle(newPix, 28, 35, 60, 40);



        MemoryImageSource mimg = new MemoryImageSource(w,h,newPix,0,w);
        new_img = createImage(mimg);

        imageFile = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        imageFile.setRGB(0,0,w,h,newPix,0,w);
        newPix = imageFile.getRGB(0, 0, w, h, null, 0, w);
        ImageIO.write(imageFile, "png", new File("/Users/e155763/src/java/subject/6/test.png"));
    }


    public void paint(Graphics g){
        g.drawImage(new_img,0,0,this);
    }


}
