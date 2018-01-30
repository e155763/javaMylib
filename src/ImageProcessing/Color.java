package ImageProcessing;

/**
 * ImageProcessing
 * Created by e155763 on 2018/01/28.
 */
public class Color {

    public final int RED = 0xFFFF0000;
    public final int GREEN = 0xFF00FF00;
    public final int BLUE = 0xFF0000FF;
    public final int BLACK = 0xFF000000;
    public final int WHITE = 0xFFFFFFFF;

    public int getAlpha(int pixel){
        return (pixel >> 24) & 0xFF;
    }

    public int getRed(int pixel){
        return (pixel >> 16) & 0xFF;
    }

    public int getGreen(int pixel){
        return (pixel >> 8) & 0xFF;
    }

    public int getBlue(int pixel){
        return pixel & 0xFF;
    }

    public int getGray(int pixel){
        return pixel & 0xFF;
    }

    public int[] getAlpha(int pix[]){
        int[] newPix = new int[pix.length];
        for(int i = 0; i < pix.length; i++){
            newPix[i] = (pix[i] >> 24) & 0xFF;
        }
        return newPix;
    }

    public int[] getRed(int pix[]){
        int[] newPix = new int[pix.length];
        for(int i = 0; i < pix.length; i++){
            newPix[i] = (pix[i] >> 16) & 0xFF;
        }
        return newPix;
    }

    public int[] getGreen(int pix[]){
        int[] newPix = new int[pix.length];
        for(int i = 0; i < pix.length; i++){
            newPix[i] = (pix[i] >> 8) & 0xFF;
        }
        return newPix;
    }

    public int[] getBlue(int pix[]){
        int[] newPix = new int[pix.length];
        for(int i = 0; i < pix.length; i++){
            newPix[i] = pix[i] & 0xFF;
        }
        return newPix;
    }

    public int[] getGray(int pix[]){
        int[] newPix = new int[pix.length];
        for(int i = 0; i < pix.length; i++){
            newPix[i] = pix[i] & 0xFF;
        }
        return newPix;
    }

    public int getGRAY(int gray){
        return 0xFF000000 | (gray << 16) | (gray << 8) | gray;
    }

    public int getRGB(int red, int green, int blue){
        return 0xFF000000 | (red << 16) | (green << 8) | blue;
    }

    public int getARGB(int alpha, int red, int green, int blue){
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

}
