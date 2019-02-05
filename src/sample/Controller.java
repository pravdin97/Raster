package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;

import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

public class Controller {

    @FXML
    private Canvas canvas;

    @FXML
    private VBox showPane;

    private Stage stage;
    private Scene scene;

    private BufferedImage img;
    private int height, width;

    private int[] red, green, blue, bright;

    private static double IncreaseContrastRatio = 2;
    private static double DecreaseContrastRatio = 0.5;
    private static int BrightnessRatio = 10;

    private String path = "/home/egor/projects/image.jpg";

    private ColorRGB[][] arr;
    @FXML
    private Label lol;

    @FXML
    private Label heightValue;

    @FXML
    private Slider widthResize;

    @FXML
    private Slider heightResize;

    @FXML
    void initialize()
    {
        try {
            img = ImageIO.read(new File(path));
            width = img.getWidth();
            height = img.getHeight();
        } catch (IOException e) {
        }
        canvas.prefHeight(height);
        canvas.prefWidth(width);

        canvas.setWidth(width);
        canvas.setHeight(height);

    }

    @FXML
    private void LoadImage()
    {
        try {
            img = ImageIO.read(new File(path));
            width = img.getWidth();
            height = img.getHeight();
            arr = new ColorRGB[width][height];

            for (int y = 0; y < height; y++)
                for (int x = 0; x < width; x++)
                {
                    Color color = new Color( img.getRGB(x, y));
                    arr[x][y] = new ColorRGB(color.getRed(), color.getGreen(), color.getBlue());
                    arr[x][y].rD = arr[x][y].r;
                    arr[x][y].gD = arr[x][y].g;
                    arr[x][y].bD = arr[x][y].b;
                }
        } catch (IOException e) {
        }

//        widthValue.setText(""+width);
        widthResize.setValue(width);
        heightResize.setValue(height);

        make3Graphs();
        printImg();
    }

    @FXML
    private void Negative()
    {
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                int r, g, b;

                arr[x][y].r = 255 - arr[x][y].r;
                arr[x][y].g = 255 - arr[x][y].g;
                arr[x][y].b = 255 - arr[x][y].b;

                r = value(arr[x][y].r);
                g = value(arr[x][y].g);
                b = value(arr[x][y].b);

                img.setRGB(x, y, new Color( r, g, b, 255).getRGB());
            }
        }
        printImg();
    }

    private double Y(int r, int g, int b)
    {
        return 0.299 * r + 0.5876 * g + 0.114 * b;
    }

    private int value(int v)
    {

        if (v < 0)
            v = 0;
        if (v > 255)
            v = 255;

        return v;
    }

    @FXML
    private void PlusBright() { bright(true); }

    @FXML
    private void MinusBright() { bright(false); }

    @FXML
    private void PlusContrast() { contrast(true); }

    @FXML
    private void MinusContrast() { contrast(false); }

    private void bright(boolean incr)
    {
        int k;
        if (!incr)
            k = -BrightnessRatio;
        else k = BrightnessRatio;
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                arr[x][y].r += k;
                arr[x][y].g += k;
                arr[x][y].b += k;

                img.setRGB(x, y, new Color( value(arr[x][y].r), value(arr[x][y].g), value(arr[x][y].b), 200).getRGB());
            }
        }
        printImg();
    }

    @FXML
    public void grey()
    {
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                int r, g, b;
                Color color = new Color(img.getRGB(x, y));

                r = color.getRed();
                g = color.getGreen();
                b = color.getBlue();

                r = (int) Y(r, g, b);
                g = b = r;

                img.setRGB(x, y, new Color( r, g, b, 200).getRGB());
            }
        }
        printImg();
    }

    private void contrast(boolean incr)
    {
        double avrRed = 0, avrGreen = 0, avrBlue = 0;
        double k;
        if (incr)
            k = IncreaseContrastRatio;
        else k = DecreaseContrastRatio;

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                int r, g, b;

                r = arr[x][y].r;
                g = arr[x][y].g;
                b = arr[x][y].b;

                avrRed += r;
                avrGreen += g;
                avrBlue += b;
            }
        }
        avrRed /= (width*height);
        avrGreen /= (width*height);
        avrBlue /= (width*height);


        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                int r, g, b;

                r = arr[x][y].r;
                g = arr[x][y].g;
                b = arr[x][y].b;

                arr[x][y].r = ((int) (k * (r - avrRed) + avrRed));
                arr[x][y].g = ((int) (k * (g - avrGreen) + avrGreen));
                arr[x][y].b = ((int) (k * (b - avrBlue) + avrBlue));

                r = value(arr[x][y].r);
                g = value(arr[x][y].g);
                b = value(arr[x][y].b);

                img.setRGB(x, y, new Color( r, g, b, 200).getRGB());

            }
        }
        printImg();
    }

    private void printImg()
    {
        Image image = SwingFXUtils.toFXImage(img, null);

        canvas.setWidth(width);
        canvas.setHeight(height);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.fillRect(0, 0, width, height);
        gc.drawImage(image, 0, 0);
    }

    private void makeGraph()
    {
        HashMap<Double, Integer> gram = new HashMap<>();

        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
            {
                Color color = new Color(img.getRGB(x, y));
                double bright = Y(color.getRed(), color.getGreen(), color.getBlue());
                int value;
                if (gram.get(bright) == null)
                    value = 0;
                else value = gram.get(bright) + 1;

                gram.put(bright, value);
            }

        System.out.println();
    }

    private void make3Graphs()
    {
        red = new int[256];
        green = new int[256];
        blue = new int[256];
        bright = new int[256];

        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
            {
                red[value(arr[x][y].r)]++;
                green[value(arr[x][y].g)]++;
                blue[value(arr[x][y].b)]++;

                int bri = (int) Y(value(arr[x][y].r), value(arr[x][y].g), value(arr[x][y].b));
                bright[bri]++;
            }
    }

    @FXML
    public void BW()
    {
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
            {
                if (arr[x][y].r > 127 || arr[x][y].g > 127 || arr[x][y].b > 127)
                {
                    arr[x][y].r = 255;
                    arr[x][y].g = 255;
                    arr[x][y].b = 255;
                }
                else
                {
                    arr[x][y].r = 0;
                    arr[x][y].g = 0;
                    arr[x][y].b = 0;
                }

                img.setRGB(x, y, new Color( arr[x][y].r, arr[x][y].g, arr[x][y].b, 200).getRGB());

            }

        printImg();
    }

    private BarChart fill(int[] arr)
    {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        for (int i = 0; i < 256; i++)
            series.getData().add(new XYChart.Data<>("" + i, arr[i]));

        barChart.getData().add(series);
        return barChart;
    }

    @FXML
    public void redHist()
    {
        make3Graphs();
        makeGraph();
        GramWindow(red);
    }

    @FXML
    public void greenHist()
    {
        make3Graphs();
        GramWindow(green);
    }

    @FXML
    public void blueHist()
    {
        make3Graphs();
        GramWindow(blue);
    }

    @FXML
    public void brightHist()
    {
        make3Graphs();
        GramWindow(bright);
    }

    private void GramWindow(int[] arr)
    {

//        make3Graphs();

        showPane = new VBox(fill(arr));

        scene = new Scene(showPane, width, height);

        stage = new Stage();
        stage.setTitle("Histograms");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void Ravn()
    {
        Ravnomer(2);
    }


    private void Ravnomer(int q)
    {
        int n = 2 * q + 1;
        int tempR[][] = new int[n][n];
        int tempG[][] = new int[n][n];
        int tempB[][] = new int[n][n];

        for (int y = q; y < height - q; y++)
            for (int x = q; x < width - q; x++)
            {
                int k = 0, l = 0;
                for (int i = x - q; i <= x + q; i++) {
                    for (int j = y - q; j <= y + q; j++) {
                        tempR[k][l] = arr[i][j].r;
                        tempG[k][l] = arr[i][j].g;
                        tempB[k][l] = arr[i][j].b;
                        l++;
                    }
                    k++;
                    l = 0;
                }

                int r = Calc(tempR, q);
                int g = Calc(tempG, q);
                int b = Calc(tempB, q);

                img.setRGB(x, y, new Color(value(r), value(g), value(b), 200).getRGB());
            }

        printImg();
    }

    private int Calc(int[][] arr, int q)
    {
        int res = 0, n = 2 * q + 1;

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                arr[i][j] /=  (n * n);
                res += arr[i][j];
            }

        return res;
    }

    private double[][] GaussianFilterInit(int q, double sigma)
    {
        double A, sum = 0, filter[][] = new double[2*q+1][2*q+1];

        for (int i = -q; i <= q; i++) {
            for (int j = -q; j <= q; j++) {
                double h = Math.exp(-(i * i + j * j) / (2 * sigma * sigma));
                sum += h;
            }
        }

        A = 1 / sum;

        int k = 0, l = 0;
        for (int i = -q; i <= q; i++) {
            for (int j = -q; j <= q; j++) {
                double h = Math.exp(-(i * i + j * j) / (2 * sigma * sigma)) * A;
                filter[k][l] = h;
                l++;
            }
            k++; l = 0;
        }
        return filter;
    }

    private int GaussianCalc(int[][] arr, int q, double[][] filter)
    {
        int res = 0, n = 2 * q + 1;

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                arr[i][j] *= filter[i][j];
                res += arr[i][j];
            }

        return res;
    }

    private void GaussianBlur(int q, double sigma)
    {
        double filter[][] = GaussianFilterInit(q, sigma);

        int n = 2 * q + 1;
        int tempR[][] = new int[n][n];
        int tempG[][] = new int[n][n];
        int tempB[][] = new int[n][n];

        for (int y = q; y < height - q; y++)
            for (int x = q; x < width - q; x++)
            {
                int k = 0, l = 0;
                for (int i = x - q; i <= x + q; i++) {
                    for (int j = y - q; j <= y + q; j++) {
                        tempR[k][l] = arr[i][j].r;
                        tempG[k][l] = arr[i][j].g;
                        tempB[k][l] = arr[i][j].b;
                        l++;
                    }
                    k++;
                    l = 0;
                }

                int r = GaussianCalc(tempR, q, filter);
                int g = GaussianCalc(tempG, q, filter);
                int b = GaussianCalc(tempB, q, filter);

                img.setRGB(x, y, new Color(value(r), value(g), value(b), 200).getRGB());
            }

        printImg();
    }

    @FXML
    public void Gauss()
    {
        GaussianBlur(2, 0.7);
    }

    private void SharpF(int q, int k)
    {
        int n = 2 * q + 1;
        int tempR[][] = new int[n][n];
        int tempG[][] = new int[n][n];
        int tempB[][] = new int[n][n];

        for (int y = q; y < height - q; y++)
            for (int x = q; x < width - q; x++)
            {
                int c = 0, l = 0;
                for (int i = x - q; i <= x + q; i++) {
                    for (int j = y - q; j <= y + q; j++) {
                        tempR[c][l] = arr[i][j].r;
                        tempG[c][l] = arr[i][j].g;
                        tempB[c][l] = arr[i][j].b;
                        l++;
                    }
                    c++;
                    l = 0;
                }

                int r = SharpCalc(tempR, q, k);
                int g = SharpCalc(tempG, q, k);
                int b = SharpCalc(tempB, q, k);

                img.setRGB(x, y, new Color(value(r), value(g), value(b), 200).getRGB());
            }

//        printImg();
    }

    private int SharpCalc(int[][] arr, int q, int k)
    {
        int res = 0, n = 2 * q + 1;

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {

                if (i == j && i == q)
                    arr[i][j] *= k+1;
                else
                    arr[i][j] *= (-k/8);
                res += arr[i][j];
            }

        return res;
    }

    @FXML
    public void Sharp()
    {
        SharpF(1, 8);
        printImg();
    }

    @FXML
    public void Noise()
    {
        Random rnd = new Random();
        int k = rnd.nextInt() % 100;

        for (int i = 0; i < k; i++)
        {
            int x = rnd.nextInt(width);
            int y = rnd.nextInt(height);
            arr[x][y].r = rnd.nextInt(255);
            arr[x][y].g = rnd.nextInt(255);
            arr[x][y].b = rnd.nextInt(255);
            img.setRGB(x, y, new Color( arr[x][y].r, arr[x][y].g, arr[x][y].b, 200).getRGB());

            makeLine(rnd.nextInt(width - 5), rnd.nextInt(height - 5));
        }

        printImg();
    }

    public void makeLine(int x, int y)
    {
        Random rnd = new Random();
        int k = rnd.nextInt() % 4;

        for (int i = 0; i < k; i++)
        {
            if (k % 2 == 0)
                x++;
            else y++;

            arr[x][y].r = rnd.nextInt(255);
            arr[x][y].g = rnd.nextInt(255);
            arr[x][y].b = rnd.nextInt(255);
            img.setRGB(x, y, new Color( arr[x][y].r, arr[x][y].g, arr[x][y].b, 200).getRGB());
        }
    }

    private void Median(int q)
    {
        int n = 2 * q + 1;
        int tempR[][] = new int[n][n];
        int tempG[][] = new int[n][n];
        int tempB[][] = new int[n][n];
        int tempY[][] = new int[n][n];

        for (int y = q; y < height - q; y++)
            for (int x = q; x < width - q; x++)
            {
                int k = 0, l = 0;
                for (int i = x - q; i <= x + q; i++) {
                    for (int j = y - q; j <= y + q; j++) {
                        tempR[k][l] = arr[i][j].r;
                        tempG[k][l] = arr[i][j].g;
                        tempB[k][l] = arr[i][j].b;

                        tempY[k][l] =(int) Y(arr[i][j].r, arr[i][j].g, arr[i][j].b);

                        l++;
                    }
                    k++;
                    l = 0;
                }
                int ind[] = CalcMedian(tempY, tempR, tempG, tempB, q);
                int r = ind[0];
                int g = ind[1];
                int b = ind[2];

//                int r = CalcMedian(tempR, q);
//                int g = CalcMedian(tempG, q);
//                int b = CalcMedian(tempB, q);

                arr[x][y].r = r;
                arr[x][y].g = g;
                arr[x][y].b = b;

                img.setRGB(x, y, new Color(value(r), value(g), value(b), 200).getRGB());
            }

        printImg();
    }

    private int[] CalcMedian(int[][] arr, int[][] r, int[][] g, int[][] b, int q)
    {
        int n = 2 * q + 1, mas[] = new int[n * n], red[] = new int[n * n], green[] = new int[n * n], blue[] = new int[n * n];
        int k = 0;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                mas[k] = arr[i][j];
//                indexes[k] = k;
                red[k] = r[i][j];
                green[k] = g[i][j];
                blue[k] = b[i][j];
                k++;
            }

        for(int j = 0; j < n * n - 1; j++)
            for (int i = 0; i < n * n - j - 1; i++)
                if (mas[i] > mas[i+1])
                {
                    int tmp = mas[i];
                    mas[i] = mas[i+1];
                    mas[i+1] = tmp;

//                    tmp = indexes[i];
//                    indexes[i] = indexes[i+1];
//                    indexes[i+1] = tmp;

                    tmp = red[i];
                    red[i] = red[i+1];
                    red[i+1] = tmp;

                    tmp = green[i];
                    green[i] = green[i+1];
                    green[i+1] = tmp;

                    tmp = blue[i];
                    blue[i] = blue[i+1];
                    blue[i+1] = tmp;
                }

//        return mas[n+1];
        return new int[] {red[n+1], green[n+1], blue[n+1]};
    }


    @FXML
    public void Aqua()
    {
        Median(1);
        Median(1);
        Median(1);
        SharpF(1, 8);

        printImg();
    }

    private void Bilin(int nh, int nw) {
        BufferedImage src = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_BGR);
        ColorRGB[][] buff = new ColorRGB[nw][nh];
        for (int x = 0; x < nw; x++)
            for (int y = 0; y < nh; y++)
                buff [x][y] = new ColorRGB(0, 0, 0);

        double dy = (double) height / (double) nh, dx = (double) width / (double) nw;

        for (int y = 0; y < nh - 1; y++) {
            double v = y * dy;
            int vi = (int) v;
            double dv = v - vi;

            for (int x = 0; x < nw - 1; x++) {
                double u = x * dx;
                int ui = (int) u;
                double du = u - ui;

                double r, g, b;

//                r = (1 - du) * (1 - dv) * arr[ui][vi].r + du * (1 - dv) * arr[ui+1][vi].r + du * dv * arr[ui+1][vi+1].r + (1 - du) * dv * arr[ui][vi+1].r;
//                g = (1 - du) * (1 - dv) * arr[ui][vi].g + du * (1 - dv) * arr[ui+1][vi].g + du * dv * arr[ui+1][vi+1].g + (1 - du) * dv * arr[ui][vi+1].g;
//                b = (1 - du) * (1 - dv) * arr[ui][vi].b + du * (1 - dv) * arr[ui+1][vi].b + du * dv * arr[ui+1][vi+1].b + (1 - du) * dv * arr[ui][vi+1].b;

                r = ((1 - du) * arr[ui][vi].r + du * arr[ui+1][vi].r) * (1 - dv) + ((1 - du) * arr[ui][vi+1].r + du * arr[ui+1][vi+1].r) * dv;
                g = ((1 - du) * arr[ui][vi].g + du * arr[ui+1][vi].g) * (1 - dv) + ((1 - du) * arr[ui][vi+1].g + du * arr[ui+1][vi+1].g) * dv;
                b = ((1 - du) * arr[ui][vi].b + du * arr[ui+1][vi].b) * (1 - dv) + ((1 - du) * arr[ui][vi+1].b + du * arr[ui+1][vi+1].b) * dv;


                buff[x][y].r = (int) Math.floor(r);
                buff[x][y].g = (int) Math.floor(g);
                buff[x][y].b = (int) Math.floor(b);

                src.setRGB(x, y, new Color( value(buff[x][y].r), value(buff[x][y].g), value(buff[x][y].b), 200).getRGB());

            }
        }

        arr = buff;
        img = src;
        width = nw;
        height = nh;

        printImg();
    }

    private void Bilinear(int newHeight, int newWidth) {

        BufferedImage src = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_BGR);
        ColorRGB[][] buff = new ColorRGB[newWidth][newHeight];
        for (int x = 0; x < newWidth; x++)
            for (int y = 0; y < newHeight; y++)
                buff [x][y] = new ColorRGB(0, 0, 0);

        double dy = (double) height / (double) newHeight, dx = (double) width / (double) newWidth;
        for( int y = 0; y < newHeight - 1; y++ ) {

            double indY,indY2;
            int inY;

            indY = y * dy;
            inY = (int)Math.floor( indY);
            indY -= inY;
            indY2 = 1 - indY;

            for(int x = 0; x < newWidth - 1; x++ ) {

                int inX;
                double indX, indX2;

                indX = x * dx;
                inX = (int)Math.floor(indX);
                indX -= inX;
                indX2 = 1 - indX;

                double blue, green, red;

                double x1y1 = indX*indY, x1y2 = indX*indY2, x2y1 = indX2*indY, x2y2 = indX2*indY2;

                blue = arr[inX][inY].b * x2y2 + arr[inX+1][inY].b * x1y2 + arr[inX][inY+1].b * x2y1 + arr[inX+1][inY+1].b * x1y1;
                green = arr[inX][inY].g * x2y2 + arr[inX+1][inY].g * x1y2 + arr[inX][inY+1].g * x2y1 + arr[inX+1][inY+1].g * x1y1;
                red = arr[inX][inY].r * x2y2 + arr[inX+1][inY].r * x1y2 + arr[inX][inY+1].r * x2y1 + arr[inX+1][inY+1].r * x1y1;

                buff[x][y].r = (int) Math.floor(red);
                buff[x][y].g = (int) Math.floor( blue);
                buff[x][y].b = (int) Math.floor( green);

                src.setRGB(x, y, new Color( value(buff[x][y].r), value(buff[x][y].g), value(buff[x][y].b), 200).getRGB());


            }
        }

        arr = buff;
        img = src;
        width = newWidth;
        height = newHeight;

        printImg();
    }

    @FXML
    public void WidthResize()
    {
        lol.setText(""+width);
    }

    @FXML
    public void HeightResize()
    {
        heightValue.setText(""+height);
    }

    @FXML
    public void BilinearResize()
    {
        int h = (int) heightResize.getValue(), w = (int) widthResize.getValue();
        Bilin(h, w);
    }

    private void Nearest(int nw, int nh)
    {
        BufferedImage src = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_BGR);
        ColorRGB[][] buff = new ColorRGB[nw][nh];
        for (int x = 0; x < nw; x++)
            for (int y = 0; y < nh; y++)
                buff [x][y] = new ColorRGB(0, 0, 0);

        double k2 = (double) height / (double) nh, k1 = (double) width / (double) nw;
//        k1 = 1 / k1;
//        k2 = 1 / k2;

        for (int x = 0; x < nw; x++)
        {
            for (int y = 0; y < nh; y++)
            {
                int xx, yy;

                xx = (int) Math.floor(k1 * x);
                yy = (int) Math.floor(k2 * y);

                buff[x][y] = arr[xx][yy];

                src.setRGB(x, y, new Color( value(buff[x][y].r), value(buff[x][y].g), value(buff[x][y].b), 200).getRGB());
            }
        }
        arr = buff;
        img = src;
        width = nw;
        height = nh;

        printImg();
    }

    @FXML
    public void NearestResize()
    {
        int h = (int) heightResize.getValue(), w = (int) widthResize.getValue();
        Nearest(w, h);
    }
}
