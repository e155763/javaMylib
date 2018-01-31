package ImageProcessing;

/**
 * ImageProcessing
 * Created by e155763 on 2018/01/30.
 */
public class HoughTransform {
    private int thetaMax;
    private int phoMax;

    private int w;
    private int h;

    Color pixColor = new Color();

    public HoughTransform(int width, int height){
        this.w = width;
        this.h = height;
    }

    private int getPixPoint(int x, int y){
        return (y * w + x);
    }

    public void line(int width, int height, double dtheta){
        this.phoMax = (int)Math.sqrt(Math.pow(width, 2) + Math.pow(height,2));
    }

    public void circle(int pix[], int radiusMin, int radiusMax, int threshold){
        final int R_MAX = radiusMax - radiusMin;
        final int X_MAX = w;
        final int Y_MAX = h;
        int distX, distY;
        int centerY, centerX, radius;

        // 円の投票
        short[][][] vote =new short[X_MAX][Y_MAX][R_MAX];
        for(int y = 0; y < h; y++){
            for(int x = 0; x < w; x++){
                if(pix[getPixPoint(x, y)] == pixColor.WHITE){
                    for(centerY = 0; centerY < h; centerY++){
                        distY = Math.abs(y - centerY);
                        if(distY > R_MAX){
                            continue;
                        }
                        for(centerX = 0; centerX < w; centerX++){
                            distX = Math.abs(x - centerX);
                            radius = (short)(Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2))+0.5);
                            if(radius >= R_MAX){
                                continue;
                            }
                            vote[centerX][centerY][radius]++;
                        }
                    }
                }
            }
        }

        // 円の描写
        for(centerY = 0; centerY < Y_MAX; centerY++){
            for(centerX = 0; centerX < X_MAX; centerX++){
                for(radius = radiusMin; radius < R_MAX; radius++){
                    if(vote[centerX][centerY][radius] > threshold){
                        //円を
                        pix[getPixPoint(centerX, centerY)] = pixColor.RED;
                    }
                }
            }
        }
    }
}
