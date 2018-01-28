//filter.java
//

import ImageProcessing.Filter;
import ImageProcessing.Shading;

import javax.imageio.ImageIO;
import java.applet.Applet;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.File;


public class myApplet extends Applet {

    private Image img = getImage(getCodeBase(),"objectsH29.jpg");
    private Image new_img;

    private int w = 0;
    private int h = 0;

    private int pix[];
    private int new_pix[];

    // Color color;
    // Color bin;

    public void init(){

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
        new_pix = new int[w*h];

        setPix();
    }

    private void setPix(){
        try{
            PixelGrabber pg = new PixelGrabber(img,0,0,w,h,pix,0,w);
            pg.grabPixels();
        }
        catch(InterruptedException e){
            System.out.println("画像が読み込めません");
        }

        Shading sh = new Shading();
        Filter filter = new Filter();

        // 一枚めの画像処理
        //sh.toGrayScale(pix, w, h);
        //filter.gaussianFilter(pix, w, h, 5, 0.3);
        //filter.sobelFilterY(pix, w, h);
        //filter.sobelFilterX(pix, w, h);
        //sh.toBinaryImage(pix, w, h);
        //sh.toNot(pix, w, h);
        filter.canny(pix, w, h, 0.3, 0,0);



        MemoryImageSource mimg = new MemoryImageSource(w,h,pix,0,w);
        new_img = createImage(mimg);
        try {
            BufferedImage bimg = new BufferedImage(img.getWidth(this),img.getHeight(this),BufferedImage.TYPE_INT_ARGB);
            ImageIO.write(bimg, "png", new File("test.png"));
        } catch (Exception e) {
            System.out.println("error");
        }
    }

    public void paint(Graphics g){
        g.drawImage(new_img,0,0,this);
    }

}
