package ImageProcessing;
 /**
 * PACKAGE_NAME
 * Created by e155763 on 2018/01/03.
 */

import java.util.Arrays;


public class Filter {
    private int w; //画像の幅
    private int h; //画像の高さ

    private int extraPix; //どのくらい画像を拡大したか
    private int filterScale = 3; //フィルタの大きさ

    private double kernel[]; //空間フィルタを格納する変数
    private int kernelPix[]; //フィルタをかけるピクセルの集合
    private int extendPix[]; //フィルタをかけるために拡大したピクセル
    private double extendPixD[];
    private Color pixColor = new Color(); //色情報取得
    private double value[]; // grayscale用の配列

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
        kernelPix = new int[filterScale * filterScale];
        int k = 0;
        for (int i = -extraPix; i <= extraPix; i++) {
            for (int j = -extraPix; j <= extraPix; j++) {
                kernelPix[k] = extendPix[getExtendPixPoint(x+i, y+j)];
                k++;
            }
        }
    }

    // canny法用
    private void setKernelD(int x, int y) {
        kernel = new double[filterScale * filterScale];
        int k = 0;
        for (int i = -extraPix; i <= extraPix; i++) {
            for (int j = -extraPix; j <= extraPix; j++) {
                kernel[k] = extendPixD[getExtendPixPoint(x+i, y+j)];
                k++;
            }
        }
    }

    // フィルタリングする際の初期化
    private void padding(int pix[]) {
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
    private void padding(double pix[]) {
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
        double red;
        double green;
        double blue;
        double tmpRed;
        double tmpGreen;
        double tmpBlue;

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                tmpRed = 0;
                tmpGreen = 0;
                tmpBlue = 0;
                setConvolutedKernel(x, y);
                for (int i = 0; i < filterScale * filterScale; i++) {
                    red = pixColor.getRed(kernelPix[i]);
                    green = pixColor.getGreen(kernelPix[i]);
                    blue = pixColor.getBlue(kernelPix[i]);
                    tmpRed += red * filter[i];
                    tmpGreen += green * filter[i];
                    tmpBlue += blue * filter[i];
                }

                red = rounding(tmpRed);
                green = rounding(tmpGreen);
                blue = rounding(tmpBlue);
                if(pix != null) {
                    pix[getPixPoint(x, y)] = pixColor.getRGB((int) red, (int) green, (int) blue); //平均を取る
                }else{
                    value[getPixPoint(x, y)] = tmpRed;
                }
            }
        }
    }

    // 平均化フィルタ
    public void averagingFilter(int pix[], int newPix[], int filterScale) {
        this.filterScale = filterScale;
        this.extraPix = (1 + filterScale) / 2 - 1;
        this.kernel = new double [filterScale * filterScale];
        int ave = filterScale * filterScale;
        Arrays.fill(kernel, 1 / ave);

        padding(pix);
        convolution(newPix, kernel);
    }

    // ガウシアンフィルタ
    public void gaussianFilter(int pix[],int newPix[], int filterScale, double sigma) {
        this.filterScale = filterScale;
        this.extraPix = (1 + filterScale) / 2 - 1;
        this.kernel = new double[filterScale * filterScale];
        double normalize = 0.0;

        padding(pix);

        //フィルタの作成
        for (int x = 0; x < filterScale; x++) {
            for (int y = 0; y < filterScale; y++) {
                kernel[x * filterScale + y] = Math.exp(-(Math.pow(x - extraPix, 2.0) + Math.pow(y - extraPix, 2.0)) / 2 * Math.pow(sigma, 2.0)) / 2.0 * Math.PI * Math.pow(sigma, 2.0);
                normalize += kernel[x * filterScale + y];
            }
        }
        for(int i = 0; i < filterScale * filterScale; i++){
            kernel[i] /= normalize;
        }
        convolution(newPix, kernel);
    }

    // ソーベルフィルタ X
    public void sobelFilterX(int pix[],int newPix[]) {
        this.filterScale = 3;
        this.extraPix = (1 + filterScale) / 2 - 1;
        this.kernel = new double[]{-1, 0, 1, -2, 0, 2, -1, 0, 1};
        padding(pix);
        convolution(newPix, kernel);
    }

    // ソーベルフィルタ Y
    public void sobelFilterY(int pix[],int newPix[]) {
        this.filterScale = 3;
        this.extraPix = (1 + filterScale) / 2 - 1;
        this.kernel = new double[]{1, 2, 1, 0, 0, 0, -1, -2, -1};
        padding(pix);
        convolution(newPix, kernel);
    }



    // ケニーのエッジ検出アルゴリズム
    public void canny(int pix[],int newPix[], double sigma, int thLow, int thHigh){
        double[] fx = new double[w*h];
        double[] fy = new double[w*h];
        double[] g = new double[w*h];
        double theta;
        int center;

        double[] d = new double[w*h];

        gaussianFilter(pix, newPix, 5, sigma);

        sobelFilterX(pix,null);
        System.arraycopy(value, 0, fx, 0, pix.length);
        sobelFilterY(pix,null);
        System.arraycopy(value, 0, fy, 0, pix.length);

        for(int i = 0; i < h * w; i++) {
            g[i] = (Math.sqrt(Math.pow(fx[i], 2) + Math.pow(fy[i], 2)));
            d[i] = fy[i] / fx[i];
            newPix[i] = pixColor.getGRAY(rounding(g[i]));
        }

        // Non-maximum Suppression
        this.filterScale = 3;
        this.extraPix = (1 + filterScale) / 2 - 1;

        padding(g);
        for(int x = 0; x < w; x++){
            for(int y = 0; y < h; y++){
                setKernelD(x, y);
                theta = d[getPixPoint(x, y)];

                if (-0.4142 < theta && theta <= 0.4142){
                    if(kernel[4] <= kernel[3] || kernel[4] <= kernel[5]){
                        newPix[getPixPoint(x,y)] = pixColor.BLACK;
                    }
                }else if(0.4142 < theta && theta < 2.4142){
                    if(kernel[4] <= kernel[2] || kernel[4] <= kernel[6]){
                        newPix[getPixPoint(x,y)] = pixColor.BLACK;
                    }
                }else if( Math.abs(theta) >= 2.4142){
                    if(kernel[4] <= kernel[1] || kernel[4] <= kernel[7]){
                        newPix[getPixPoint(x,y)] = pixColor.BLACK;
                    }
                }else if(-2.4142 < theta && theta < -0.4142){
                    if(kernel[4] <= kernel[0] || kernel[4] <= kernel[8]){
                        newPix[getPixPoint(x,y)] = pixColor.BLACK;
                    }
                }
            }
        }

        // Hysteresis Threshold
        padding(newPix);

        for(int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                setConvolutedKernel(x, y);
                center = pixColor.getGray(kernelPix[4]);
                if(thHigh <= center){
                    newPix[getPixPoint(x,y)] = pixColor.WHITE;
                }else if(thLow < center && center < thHigh){
                    for (int aKernelPix : kernelPix) {
                        if (thHigh < pixColor.getGray(aKernelPix)) {
                            newPix[getPixPoint(x, y)] = pixColor.WHITE;
                            break;
                        }
                        newPix[getPixPoint(x, y)] = pixColor.BLACK;
                    }
                }else if(center <= thLow){
                    newPix[getPixPoint(x,y)] = pixColor.BLACK;
                }
            }
        }
    }
}
