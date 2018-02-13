package ImageProcessing;
 /**
 * PACKAGE_NAME
 * Created by e155763 on 2018/01/03.
 */

public class Shading {

    private Color color = new Color();

    public Shading() {

    }

    public void toneCurve(int pix[], int newPix[], int thLow, int thHigh){
        int gray;
        for (int i = 0; i < pix.length; i++){
            gray = color.getGray(pix[i]);
            if(gray < thLow){
                gray = color.BLACK;
            }else if(thHigh < gray){
                gray = color.WHITE;
            }else{
                gray = (int)((double)0xFF * (double)(gray - thLow) / (double)(thHigh - thLow));
            }
            newPix[i] = color.getGRAY(gray);
        }
    }

    public void histogramEqualization(int pix[], int newPix[]){
        int[] hist = histogram(pix);
        int ave = pix.length / 0xFF;
        int tmp;
        int gray;
        for(int i = 0; i < pix.length; i++){
            tmp = 0;
            for(int j = 0; j < color.getGray(pix[i]); j++){
                tmp += hist[j];
            }
            gray = (int)((double)0xFF*(double)tmp/(double)pix.length);
            newPix[i] = color.getGRAY(gray);
        }
    }


    public void toGrayScale(int pix[], int newPix[]) {
        int r, g, b; //色データ
        int d;
        for (int i = 0; i < pix.length; i++) {
            r = color.getRed(pix[i]);
            g = color.getGreen(pix[i]);
            b = color.getBlue(pix[i]);
            d = (r * 2126 + g * 7152 + b * 722) / 10000;
            newPix[i] = color.getRGB(d, d, d);
        }
    }


    public void toBinaryImage(int pix[]) {
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

        for (int i = 0; i < pix.length; i++) {
            if (color.getGray(pix[i]) > s) {
                pix[i] = color.WHITE;
            } else {
                pix[i] = color.BLACK;
            }
        }
    }

    public void toNot(int pix[]){
        for (int i = 0; i < pix.length; i++) {
            if ((color.getRGB(pix[i]) & 0xFF) != 0xFF) {
                pix[i] = color.WHITE;
            } else {
                pix[i] = color.BLACK;
            }
        }
    }

    private int[] histogram(int pix[]) {
        int hist[] = new int[0xFF+1];

        for (int i = 0; i <= 0xFF; i++) {
            int buf = 0;
            for (int j = 0; j < pix.length; j++) {
                if (color.getGray(pix[j]) == i) {
                    buf++;
                }
            }
            hist[i] = buf;
        }
        return hist;
    }
}
