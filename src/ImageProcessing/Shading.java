package ImageProcessing;
 /**
 * PACKAGE_NAME
 * Created by e155763 on 2018/01/03.
 */

import java.awt.Color;

public class Shading {

    private Color color;

    private int w;
    private int h;

    public Shading() {

    }

    public void toGrayScale(int pix[], int width, int height) {
        this.w = width;
        this.h = height;
        int r, g, b; //色データ
        int d;
        for (int i = 0; i < w * h; i++) {
            color = new Color(pix[i]);
            r = color.getRed();
            g = color.getGreen();
            b = color.getBlue();
            d = (r * 2126 + g * 7152 + b * 722) / 10000;
            color = new Color(d, d, d);
            pix[i] = color.getRGB();
        }
    }


    public void toBinaryImage(int pix[], int width, int height) {
        this.w = width;
        this.h = height;
        int[] hist = histogram(pix);//ヒストグラムを取得
        int max = Integer.MIN_VALUE;
        int s = 0;//閾値
        int res_b;//暗い方の合計
        int count_b;//暗い方の画素数
        int res_w;//明るい方の合計
        int count_w;//明るい方の画素値
        double m_b;//平均
        double m_w;//平均
        double res1;//合計
        Color bin;
        for (int t = 2; t <= 0xFF; t++) {
            res_b = 0;
            count_b = 0;
            res_w = 0;
            count_w = 0;
            m_b = 0;//平均
            m_w = 0;//平均
            res1 = 0.0;
            for (int i = 0; i < t; i++) {
                res_b += hist[i] * i;//輝度値*画素数
                count_b += hist[i];
            }
            for (int i = t; i <= 0xFF; i++) {
                res_w += hist[i] * i;//輝度値*画素数
                count_w += hist[i];
            }
            if (count_b == 0) continue;
            else res_b /= count_b;
            if (count_w == 0) continue;
            else res_w /= count_w;
            res1 = count_b * count_w * (res_b - res_w) * (res_b - res_w);
            if (res1 > max) {
                s = t;
                max = (int) res1;
            }
        }

        for (int i = 0; i < w * h; i++) {
            color = new Color(pix[i]);
            if ((color.getRGB() & 0xFF) > s) {
                bin = Color.WHITE;
            } else {
                bin = Color.BLACK;
            }
            pix[i] = bin.getRGB();
        }
    }

    public void toNot(int pix[], int width, int height){
        this.w = width;
        this.h = height;
        Color not; //反転した色情報
        for (int i = 0; i < w * h; i++) {
            color = new Color(pix[i]);
            if ((color.getRGB() & 0xFF) != 0xFF) {
                not = Color.WHITE;
            } else {
                not = Color.BLACK;
            }
            pix[i] = not.getRGB();
        }
    }

    private int[] histogram(int pix[]) {
        int hist[] = new int[0xFF+1];
        Color pix_color;

        for (int i = 0; i <= 0xFF; i++) {
            int buf = 0;
            for (int j = 0; j < w * h; j++) {
                pix_color = new Color(pix[j]);
                if (pix_color.getRed() == i) {
                    buf++;
                }
            }
            hist[i] = buf;
        }
        return hist;
    }
}
