package ImageProcessing;
 /**
 * PACKAGE_NAME
 * Created by e155763 on 2018/01/03.
 */

import java.awt.*;
import java.util.Arrays;



public class Filter {
    private int w; //画像の幅
    private int h; //画像の高さ

    private int extraPix; //どのくらい画像を拡大したか
    private int filterScale; //フィルタの大きさ

    private double filter[]; //空間フィルタを格納する変数
    private int filteringPix[]; //フィルタをかけるピクセルの集合
    private int extendPix[]; //フィルタをかけるために拡大したピクセル
    private Color pixColor; //色情報取得
    private int red = 0;
    private int green = 0;
    private int blue = 0;
    private int tmpRed = 0;
    private int tmpGreen = 0;
    private int tmpBlue = 0;


    // 丸め込み
    private int rounding(int pixel) {
        if (pixel < 0x00) {
            pixel = 0x00;
        } else if (0xFF < pixel) {
            pixel = 0xFF;
        }
        return pixel;
    }
    // 空間フィルタのために画像を拡張
    private void setExtendPix(int filterScale) {
        extendPix = new int[(w + filterScale - 1) * (h + filterScale - 1)];
    }

    // フィルタをかけるピクセルを取得
    private void setFilteringPix(int x, int y, int extraPix, int filterScale) {
        filteringPix = new int[filterScale * filterScale];
        int k = 0;
        for (int i = -extraPix; i <= extraPix; i++) {
            for (int j = -extraPix; j <= extraPix; j++) {
                filteringPix[k] = extendPix[(x + extraPix + i) * (w + filterScale - 1) + (y + extraPix) + j];
                k++;
            }
        }
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
    private int getPixPoint(int x, int y, int extraPix, int filterScale){
        return (y + extraPix) * (w + filterScale - 1) + (x + extraPix);
    }

    // フィルタリングする際の初期化
    private void init(int pix[]) {
        setExtendPix(filterScale);
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                extendPix[getPixPoint(i, j, extraPix, filterScale)] = pix[getPixPoint(i, j)];
            }
        }
        int l, m, n;

        // 画像を拡張
        for(l = 0; l < extraPix; l++){
            for(n = 0, m = 0 ; n < w + l * 2; n++){
                extendPix[getPixPoint(n - l, m - 1 - l, extraPix, filterScale)] = extendPix[getPixPoint(n - l, m - l, extraPix, filterScale)];
            }

            for(m = 0, n = n - 1 - l * 2; m < h + l * 2; m++){
                extendPix[getPixPoint(n + 1 + l, m - l, extraPix, filterScale)] = extendPix[getPixPoint(n + l, m - l, extraPix, filterScale)];
            }

            for(m = m - 1 - l * 2; -l * 2 <= n; n--){
                extendPix[getPixPoint(n + l, m + 1 + l, extraPix, filterScale)] = extendPix[getPixPoint(n + l, m + l, extraPix, filterScale)];
            }
            for(n = 0 ; -l * 2 <= m; m--){
                extendPix[getPixPoint(n - 1 - l, m + l, extraPix, filterScale)] = extendPix[getPixPoint(n - l, m + l, extraPix, filterScale)];
            }

            extendPix[getPixPoint(-1 - l, -1 - l, extraPix, filterScale)] = extendPix[getPixPoint(-l, -l, extraPix, filterScale)];
            extendPix[getPixPoint(w + l, -1 - l, extraPix, filterScale)] = extendPix[getPixPoint(w - 1 + l, -l, extraPix, filterScale)];
            extendPix[getPixPoint(w + l, h + l, extraPix, filterScale)] = extendPix[getPixPoint(w - 1 + l, h - 1 + l, extraPix, filterScale)];
            extendPix[getPixPoint(-1 - l, h + l, extraPix, filterScale)] = extendPix[getPixPoint(-l, h - 1 + l, extraPix, filterScale)];

        }
    }

    // フィルタをかけるメソッド
    private void filtering(int pix[], double filter[]) {
        init(pix);

        for (int x = 0; x < h; x++) {
            for (int y = 0; y < w; y++) {
                setFilteringPix(x, y, extraPix, filterScale);
                for (int i = 0; i < filterScale * filterScale; i++) {
                    pixColor = new Color(filteringPix[i]);
                    red = pixColor.getRed();
                    green = pixColor.getGreen();
                    blue = pixColor.getBlue();
                    tmpRed += red * filter[i];
                    tmpGreen += green * filter[i];
                    tmpBlue += blue * filter[i];
                }

                tmpRed = rounding(tmpRed);
                tmpGreen = rounding(tmpGreen);
                tmpBlue = rounding(tmpBlue);
                red = tmpRed;
                green = tmpGreen;
                blue = tmpBlue;
                pixColor = new Color(red, green, blue);
                pix[x * w + y] = pixColor.getRGB(); //平均を取る
                tmpRed = 0;
                tmpGreen = 0;
                tmpBlue = 0;
            }
        }
    }

    // 平均化フィルタ
    public void averagingFilter(int pix[], int width, int height, int filterScale) {
        this.w = width;
        this.h = height;
        this.filterScale = filterScale;
        int ave = filterScale * filterScale;
        extraPix = (1 + filterScale) / 2 - 1;
        filter = new double [filterScale * filterScale];

        Arrays.fill(filter, 1 / ave);

        filtering(pix, filter);
    }

    // がウシアンフィルタ
    public void gaussianFilter(int pix[], int width, int height, int filterScale, double sigma) {
        this.w = width;
        this.h = height;
        this.filterScale = filterScale;
        this.extraPix = (1 + filterScale) / 2 - 1;
        this.filter = new double[filterScale * filterScale];
        double normalize = 0.0;

        //フィルタの作成
        for (int x = 0; x < filterScale; x++) {
            for (int y = 0; y < filterScale; y++) {
                filter[x * filterScale + y] = Math.exp(-(Math.pow(x - extraPix, 2.0) + Math.pow(y - extraPix, 2.0)) / 2 * Math.pow(sigma, 2.0)) / 2.0 * Math.PI * Math.pow(sigma, 2.0);
                normalize += filter[x * filterScale + y];
            }
        }
        for(int i = 0; i < filterScale * filterScale; i++){
            filter[i] /= normalize;
        }
        filtering(pix, filter);
    }

    // ソーベルフィルタ X
    public void sobelFilterX(int pix[], int width, int height) {
        this.w = width;
        this.h = height;
        this.filterScale = 3;
        this.extraPix = (1 + filterScale) / 2 - 1;
        this.filter = new double[]{-1, 0, 1, -2, 0, 2, -1, 0, 1};
        filtering(pix, filter);
    }

    // ソーベルフィルタ Y
    public void sobelFilterY(int pix[], int width, int height) {
        this.w = width;
        this.h = height;
        this.filterScale = 3;
        this.extraPix = (1 + filterScale) / 2 - 1;
        this.filter = new double[]{1, 2, 1, 0, 0, 0, -1, -2, -1};
        filtering(pix, filter);
    }
    // ケニーのエッジ検出アルゴリズム
    public void canny(int pix[], int width, int height, double sigma, double thHigh, double thLow){
        this.w = width;
        this.h = height;
        int[] fx = new int[w*h];
        int[] fy = new int[w*h];
        int[] g = new int[w*h];
        Shading sh = new Shading();

        sh.toGrayScale(pix, w, h);

        gaussianFilter(pix, w, h, 5, sigma);
        System.arraycopy(pix, 0, fx, 0, pix.length);
        System.arraycopy(pix, 0, fy, 0, pix.length);

        sobelFilterX(fx, w, h);
        sobelFilterY(fy, w, h);



        for(int i = 0; i < h * w; i++) {
            Color colorX = new Color(fx[i]);
            Color colorY = new Color(fy[i]);

            red = rounding((int) (Math.sqrt(Math.pow(colorX.getRed(), 2) + Math.pow(colorY.getRed(), 2))));
            green = rounding((int) (Math.sqrt(Math.pow(colorX.getGreen(), 2) + Math.pow(colorY.getGreen(), 2))));
            blue = rounding((int) (Math.sqrt(Math.pow(colorX.getBlue(), 2) + Math.pow(colorY.getBlue(), 2))));
            pixColor = new Color(red, green, blue);
            pix[i] = pixColor.getRGB();
        }
    }
}
