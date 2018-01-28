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
import java.io.IOException;


public class myApplet extends Applet {

    private Image img;
    private Image new_img;
    private BufferedImage imageFile;

    private int w = 0;
    private int h = 0;

    private int pix[];
    private int new_pix[];

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
        new_pix = new int[w*h];

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

        imageFile = new BufferedImage(img.getWidth(null),img.getHeight(null),BufferedImage.TYPE_INT_ARGB);
        imageFile.setRGB(0,0,w,h,pix,0,w);
        pix = imageFile.getRGB(0, 0, w, h, null, 0, w);
        ImageIO.write(imageFile, "png", new File("/Users/e155763/src/java/subject/6/test.png"));
    }

    public void paint(Graphics g){
        g.drawImage(new_img,0,0,this);
    }

}
