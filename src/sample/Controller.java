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
import javafx.scene.image.Image;

import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

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

    private ColorRGB[][] arr;

    @FXML
    void initialize()
    {
        try {
            img = ImageIO.read(new File("D:\\Projects\\Java\\testRastr\\src\\pik.jpg"));
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
            img = ImageIO.read(new File("D:\\Projects\\Java\\testRastr\\src\\pik.jpg"));
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

                img.setRGB(x, y, new Color( r, g, b, 200).getRGB());
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

    public void Ravnomer(int q)
    {
        double[][] filtr = new double[2*q+1][2*q+1];
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
            {

            }
    }
}
