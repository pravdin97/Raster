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

    private String path = "F:\\Projects\\java\\Raster\\res\\pic.jpg";

    private ColorRGB[][] arr;

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
            arr[x][y].r = 0;
            arr[x][y].g = 0;
            arr[x][y].b = 0;
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

            arr[x][y].r = 0;
            arr[x][y].g = 0;
            arr[x][y].b = 0;
            img.setRGB(x, y, new Color( arr[x][y].r, arr[x][y].g, arr[x][y].b, 200).getRGB());
        }
    }

    private void Median(int q)
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

                int r = CalcMedian(tempR, q);
                int g = CalcMedian(tempG, q);
                int b = CalcMedian(tempB, q);

                arr[x][y].r = r;
                arr[x][y].r = g;
                arr[x][y].r = b;

                img.setRGB(x, y, new Color(value(r), value(g), value(b), 200).getRGB());
            }

        printImg();
    }

    private int CalcMedian(int[][] arr, int q)
    {
        int n = 2 * q + 1, mas[] = new int[n * n];
        int k = 0;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                mas[k] += arr[i][j];
                k++;
            }

        for(int j = 0; j < n * n - 1; j++)
            for (int i = 0; i < n * n - j - 1; i++)
                if (mas[i] > mas[i+1])
                {
                    int tmp = mas[i];
                    mas[i] = mas[i+1];
                    mas[i+1] = tmp;
                }

        return mas[q];
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
}
