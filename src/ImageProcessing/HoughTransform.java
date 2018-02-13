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

    public void drewLine(int pix[], int x1, int y1, int x2, int y2){
        int x;
        int y;
        int deltaY = Math.abs(y2 - y1);
        int deltaX = Math.abs(x2 - x1);
        int dy = deltaY;
        int dx = deltaX;

        /* 傾きがマイナスならば */
        if(x1 > x2){
            x = -1;
        }else{
            x = 1;
        }
        if(y1 > y2){
            y = -1;
        }else{
            y = 1;
        }

        if (dy > dx){
            dy = deltaX;
            dx = deltaY;
        }

        int df1 = ((dx - dy) << 1); //dが負のときに加算(2b-2a)
        int df2 = -(dy << 1);       //dが正のときに加算(-2a)
        int d = dx - (dy << 1);     //dの初期値(b-2a)

        if (deltaY < deltaX){
            while(x1 != x2){
                pix[getPixPoint(x1, y1)] = pixColor.RED;
                x1 += x;
                if(d < 0){
                    y1 += y;
                    d += df1;
                }else{
                    d += df2;
                }
            }
        }else {
            while(y1 != y2){
                pix[getPixPoint(x1, y1)] = pixColor.RED;
                y1 += y;
                if(d < 0){
                    x1 += x;
                    d += df1;
                }else{
                    d += df2;
                }
            }
        }
    }

    void drawCircle(int pix[], int cx, int cy, int r) {
        int xx = 128*r;
        int yy = 0;
        int x = 0;
        int y = 0;
        while(yy <= xx){
            x = xx / 128;
            y = yy / 128;
            pix[getPixPoint(cx+x, cy+y)] = pixColor.RED;
            pix[getPixPoint(cx-x, cy-y)] = pixColor.RED;
            pix[getPixPoint(cx-x, cy+y)] = pixColor.RED;
            pix[getPixPoint(cx+x, cy-y)] = pixColor.RED;
            pix[getPixPoint(cx+y, cy+x)] = pixColor.RED;
            pix[getPixPoint(cx-y, cy-x)] = pixColor.RED;
            pix[getPixPoint(cx-y, cy+x)] = pixColor.RED;
            pix[getPixPoint(cx+y, cy-x)] = pixColor.RED;
            yy += xx / 128;
            xx -= yy / 128;
        }
    }


    public void line(int width, int height, double dtheta){
        this.phoMax = (int)Math.sqrt(Math.pow(width, 2) + Math.pow(height,2));
    }

    public int circle(int pix[], int rMin, int rMax, int threshold, int near){
        final int R_MAX = rMax;
        final int R_MIN = rMin;
        final int X_MAX = w;
        final int Y_MAX = h;
        int flag = 0;
        int distX, distY;
        int centerX, centerY, radius;
        int centerXMax = 0, centerYMax = 0, radiusMax = 0, voteMax;
        int counter = 0;

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

        // 円を検出
        while(flag == 0) {
            voteMax = 0;
            flag = 1;
            for (centerY = 0; centerY < Y_MAX; centerY++) {
                for (centerX = 0; centerX < X_MAX; centerX++) {
                    for (radius = R_MIN; radius < R_MAX; radius++) {
                        if (vote[centerX][centerY][radius] > voteMax) {
                            voteMax = vote[centerX][centerY][radius];
                            if (voteMax > threshold) {
                                flag = 0;
                            }
                            centerXMax = centerX;
                            centerYMax = centerY;
                            radiusMax = radius;
                        }
                    }
                }
            }

            //近傍点の除去
            for (int k = -near; k <= near; k++) {
                if (centerXMax + k >= X_MAX || centerXMax + k < 0) continue;
                for (int j = -near; j <= near; j++) {
                    if (centerYMax + j >= Y_MAX || centerYMax + j < 0) continue;
                    for (int i = -near; i <= near; i++) {
                        if (radiusMax + i >= R_MAX || radiusMax + i < 0) continue;
                        vote[centerXMax + k][centerYMax + j][radiusMax + i] = 0;
                    }
                }
            }
            //円の中点を描写
            drawCircle(pix, centerXMax, centerYMax, radiusMax);
            pix[getPixPoint(centerXMax, centerYMax)] = pixColor.RED;
            counter++;
        }
        return counter;
    }
}
