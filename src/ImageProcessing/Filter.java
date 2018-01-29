package ImageProcessing;
 /**
 * PACKAGE_NAME
 * Created by e155763 on 2018/01/03.
 */

import java.awt.Color;
import java.util.Arrays;


public class Filter {
    private int w; //画像の幅
    private int h; //画像の高さ

    private int extraPix; //どのくらい画像を拡大したか
    private int filterScale = 3; //フィルタの大きさ

    private double Kernel[]; //空間フィルタを格納する変数
    private int KernelPix[]; //フィルタをかけるピクセルの集合
    private double KernelPixD[];
    private int extendPix[]; //フィルタをかけるために拡大したピクセル
    private double extendPixD[];
    private Color pixColor; //色情報取得
    private double red = 0;
    private double green = 0;
    private double blue = 0;
    private double value[]; // grayscale用の配列
    private double tmpRed = 0;
    private double tmpGreen = 0;
    private double tmpBlue = 0;

    private final int BLACK = 0xFF000000;
    private final int WHITE = 0xFFFFFFFF;

    public Filter(int width, int height){
        this.w = width;
        this.h = height;
    }


    // 丸め込み
    private int rounding(double pixel) {
        if (pixel < 0x00) {
            pixel = 0x00;
        } else if (0xFF < pixel) {
            pixel = 0xFF;
        }
        return (int)pixel;
    }

    // テスト用
    public int[] getExtendPix(){
        return extendPix;
    }

    // 座標を1次元にして返す
    private int getPixPoint(int x, int y){
        return (y * w + x);
    }

    // 拡張した画像の元の画像がある座標を1次元にして返す
    private int getExtendPixPoint(int x, int y){
        return (y + extraPix) * (w + filterScale - 1) + (x + extraPix);
    }

    // 空間フィルタのために画像を拡張
    private void setExtendPix() {
        extendPix = new int[(w + filterScale - 1) * (h + filterScale - 1)];
    }

    // canny法用
    private void setExtendPixD() {
        extendPixD = new double[(w + filterScale - 1) * (h + filterScale - 1)];
    }

    // 畳み込まれるピクセルを取得
    private void setConvolutedKernel(int x, int y) {
        KernelPix = new int[filterScale * filterScale];
        int k = 0;
        for (int i = -extraPix; i <= extraPix; i++) {
            for (int j = -extraPix; j <= extraPix; j++) {
                KernelPix[k] = extendPix[getExtendPixPoint(x+i, y+j)];
                k++;
            }
        }
    }

    // canny法用
    private void setConvolutedKernelD(int x, int y) {
        KernelPixD = new double[filterScale * filterScale];
        int k = 0;
        for (int i = -extraPix; i <= extraPix; i++) {
            for (int j = -extraPix; j <= extraPix; j++) {
                KernelPixD[k] = extendPixD[getExtendPixPoint(x+i, y+j)];
                k++;
            }
        }
    }

    // フィルタリングする際の初期化
    private void init(int pix[]) {
        value = new double[w*h];
        setExtendPix();
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                extendPix[getExtendPixPoint(i, j)] = pix[getPixPoint(i, j)];
            }
        }
        int l, m, n;

        // 画像を拡張
        for(l = 0; l < extraPix; l++){
            for(n = 0, m = 0 ; n < w + l * 2; n++){
                extendPix[getExtendPixPoint(n - l, m - 1 - l)] = extendPix[getExtendPixPoint(n - l, m - l)];
            }

            for(m = 0, n = n - 1 - l * 2; m < h + l * 2; m++){
                extendPix[getExtendPixPoint(n + 1 + l, m - l)] = extendPix[getExtendPixPoint(n + l, m - l)];
            }

            for(m = m - 1 - l * 2; -l * 2 <= n; n--){
                extendPix[getExtendPixPoint(n + l, m + 1 + l)] = extendPix[getExtendPixPoint(n + l, m + l)];
            }
            for(n = 0 ; -l * 2 <= m; m--){
                extendPix[getExtendPixPoint(n - 1 - l, m + l)] = extendPix[getExtendPixPoint(n - l, m + l)];
            }

            extendPix[getExtendPixPoint(-1 - l, -1 - l)] = extendPix[getExtendPixPoint(-l, -l)];
            extendPix[getExtendPixPoint( w + l, -1 - l)] = extendPix[getExtendPixPoint(w - 1 + l, -l)];
            extendPix[getExtendPixPoint( w + l,  h + l)] = extendPix[getExtendPixPoint(w - 1 + l, h - 1 + l)];
            extendPix[getExtendPixPoint(-1 - l,  h + l)] = extendPix[getExtendPixPoint(-l, h - 1 + l)];

        }
    }

    // canny法用
    private void init(double pix[]) {
        value = new double[w*h];

        setExtendPixD();
        Arrays.fill(extendPixD, 0.0);
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                extendPixD[getExtendPixPoint(i, j)] = pix[getPixPoint(i, j)];
            }
        }
    }

    // フィルタをかけるメソッド
    private void convolution(int pix[], double filter[]) {
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                setConvolutedKernel(x, y);
                for (int i = 0; i < filterScale * filterScale; i++) {
                    pixColor = new Color(KernelPix[i]);
                    red = pixColor.getRed();
                    green = pixColor.getGreen();
                    blue = pixColor.getBlue();
                    tmpRed += red * filter[i];
                    tmpGreen += green * filter[i];
                    tmpBlue += blue * filter[i];
                }

                red = rounding(tmpRed);
                green = rounding(tmpGreen);
                blue = rounding(tmpBlue);
                if(pix != null) {
                    pixColor = new Color((int) red, (int) green, (int) blue);
                    pix[getPixPoint(x, y)] = pixColor.getRGB(); //平均を取る
                }else{
                    value[getPixPoint(x, y)] = tmpRed;
                }
                tmpRed = 0;
                tmpGreen = 0;
                tmpBlue = 0;
            }
        }
    }

    // 平均化フィルタ
    public void averagingFilter(int pix[], int filterScale) {
        this.filterScale = filterScale;
        this.extraPix = (1 + filterScale) / 2 - 1;
        this.Kernel = new double [filterScale * filterScale];
        int ave = filterScale * filterScale;
        Arrays.fill(Kernel, 1 / ave);

        init(pix);
        convolution(pix, Kernel);
    }

    // ガウシアンフィルタ
    public void gaussianFilter(int pix[],int newPix[], int filterScale, double sigma) {
        this.filterScale = filterScale;
        this.extraPix = (1 + filterScale) / 2 - 1;
        this.Kernel = new double[filterScale * filterScale];
        double normalize = 0.0;

        init(pix);

        //フィルタの作成
        for (int x = 0; x < filterScale; x++) {
            for (int y = 0; y < filterScale; y++) {
                Kernel[x * filterScale + y] = Math.exp(-(Math.pow(x - extraPix, 2.0) + Math.pow(y - extraPix, 2.0)) / 2 * Math.pow(sigma, 2.0)) / 2.0 * Math.PI * Math.pow(sigma, 2.0);
                normalize += Kernel[x * filterScale + y];
            }
        }
        for(int i = 0; i < filterScale * filterScale; i++){
            Kernel[i] /= normalize;
        }
        convolution(newPix, Kernel);
    }

    // ソーベルフィルタ X
    public void sobelFilterX(int pix[],int newPix[]) {
        this.filterScale = 3;
        this.extraPix = (1 + filterScale) / 2 - 1;
        this.Kernel = new double[]{-1, 0, 1, -2, 0, 2, -1, 0, 1};
        init(pix);
        convolution(newPix, Kernel);
    }

    // ソーベルフィルタ Y
    public void sobelFilterY(int pix[],int newPix[]) {
        this.filterScale = 3;
        this.extraPix = (1 + filterScale) / 2 - 1;
        this.Kernel = new double[]{1, 2, 1, 0, 0, 0, -1, -2, -1};
        init(pix);
        convolution(newPix, Kernel);
    }



    // ケニーのエッジ検出アルゴリズム
    public void canny(int pix[],int newPix[], double sigma, int thLow, int thHigh){
        double[] fx = new double[w*h];
        double[] fy = new double[w*h];
        double[] g = new double[w*h];
        double theta;
        int center;

        double[] d = new double[w*h];
        Shading sh = new Shading();

        sh.toGrayScale(pix, w, h);

        gaussianFilter(pix,pix, 5, sigma);

        sobelFilterX(pix,null);
        System.arraycopy(value, 0, fx, 0, pix.length);
        sobelFilterY(pix,null);
        System.arraycopy(value, 0, fy, 0, pix.length);

        for(int i = 0; i < h * w; i++) {
            g[i] = (Math.sqrt(Math.pow(fx[i], 2) + Math.pow(fy[i], 2)));
            d[i] = Math.tan(fy[i] / fx[i]);
            pixColor = new Color(rounding(g[i]), rounding(g[i]), rounding(g[i]));
            newPix[i] = pixColor.getRGB();
        }

        // Non-maximum Suppression
        this.filterScale = 3;
        this.extraPix = (1 + filterScale) / 2 - 1;

        init(g);
        for(int x = 0; x < w; x++){
            for(int y = 0; y < h; y++){
                setConvolutedKernelD(x, y);
                theta = d[getPixPoint(x, y)];

                if (-0.4142 < theta && theta <= 0.4142){
                    if(KernelPixD[4] <= KernelPixD[3] || KernelPixD[4] <= KernelPixD[5]){
                        newPix[getPixPoint(x,y)] = BLACK;
                    }
                }else if(0.4142 < theta && theta < 2.4142){
                    if(KernelPixD[4] <= KernelPixD[2] || KernelPixD[4] <= KernelPixD[6]){
                        newPix[getPixPoint(x,y)] = BLACK;
                    }
                }else if( Math.abs(theta) >= 2.4142){
                    if(KernelPixD[4] <= KernelPixD[1] || KernelPixD[4] <= KernelPixD[7]){
                        newPix[getPixPoint(x,y)] = BLACK;
                    }
                }else if(-2.4142 < theta && theta < -0.4142){
                    if(KernelPixD[4] <= KernelPixD[0] || KernelPixD[4] <= KernelPixD[8]){
                        newPix[getPixPoint(x,y)] = BLACK;
                    }
                }
            }
        }

        // Hysteresis Threshold
        init(newPix);

        for(int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                setConvolutedKernel(x, y);
                pixColor = new Color(KernelPix[4]);
                center = pixColor.getRed();
                if(thHigh <= center){
                    newPix[getPixPoint(x,y)] = WHITE;
                }else if(thLow < center && center < thHigh){
                    for (int aKernelPix : KernelPix) {
                        pixColor = new Color(aKernelPix);
                        if (thHigh <= pixColor.getRed()) {
                            newPix[getPixPoint(x, y)] = WHITE;
                            break;
                        }
                        newPix[getPixPoint(x, y)] = BLACK;
                    }
                }else if(center <= thLow){
                    newPix[getPixPoint(x,y)] = BLACK;
                }

            }
        }
    }
}
