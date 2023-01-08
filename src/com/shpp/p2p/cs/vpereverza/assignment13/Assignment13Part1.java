package com.shpp.p2p.cs.vpereverza.assignment13;

import acm.graphics.GImage;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * The program calculates the number of silhouettes in the picture
 * (for the correct result, control the data in constants)
 */

public class Assignment13Part1 {

    //The number of pixels in the current silhouette.
    private static int pixels = 0;

    //Array of found silhouette pixels
    private static boolean[][] foundSilhouettes;

    //The border in the possible permissible error (difference) of the background brightness in the picture
    private static final int BORDER_LUMINANCES = 50;

    //The minimum number of object pixels to be counted.
    private static final int MIN_OBJECT_SIZE = 25;

    //Maximum transparency border.
    private static final int BORDER_TRANSPARENCY = 230;

    //The way to the picture
    private static final String PATH = "images/test.png";

    /**
     * This method takes the path to the image as the first parameter,
     * if it is not specified, then it analyzes the test image.
     */
    public static void main(String[] args) {
        try {
            if (args.length > 0) {
                analiseImage(args[0]);
            } else {
                analiseImage(PATH);
            }
        } catch (Exception e) {
            System.out.println("File not found");
        }
    }

    /**
     * The method takes the path to the image as a parameter
     * and analyzes the alpha channel of the background (transparency).
     * If the background is transparent, then passes the data
     * and the alpha channel to the silhouette calculation method,
     * if not transparent, then passes the background brightness.
     * After calculating the silhouettes, displays the result.
     *
     * @param path the path to the file
     */
    private static void analiseImage(String path) {
        int[][] imagePixels = new GImage(path).getPixelArray();
        foundSilhouettes = new boolean[imagePixels.length][imagePixels[0].length];
        double alpha = new Color(imagePixels[0][0], true).getAlpha();
        System.out.println("The number of silhouettes in the picture: " + countTheSilhouettes(imagePixels,
                alpha > BORDER_TRANSPARENCY ? findLuminances(imagePixels[0][0]) : alpha, !(alpha > BORDER_TRANSPARENCY)));
    }

    /**
     * The method analyzes the image pixels by comparing them with the background.
     * If the current pixel is different, then call the method of finding the silhouette in width,
     * which fills the boolean array with the pixels that are the silhouette.
     * If the silhouette is smaller than the required size, then it is not accepted, it is not considered.
     *
     * @param imagePixels  pixel array pictures
     * @param background   background value
     * @param transparency background transparency
     * @return the number of silhouettes in the picture
     */
    private static int countTheSilhouettes(int[][] imagePixels, double background, boolean transparency) {
        int silhouettes = 0;

        for (int row = 0; row < imagePixels.length; row++) {
            for (int col = 0; col < imagePixels[row].length; col++) {
                double pixelLuminances = findPixel(imagePixels[row][col], transparency);
                if ((pixelLuminances >= background + BORDER_LUMINANCES ||
                        pixelLuminances <= background - BORDER_LUMINANCES) && !foundSilhouettes[row][col]) {
                    pixels = 0;
                    findElements(imagePixels, row, col, transparency, background);
                    if (pixels >= MIN_OBJECT_SIZE)
                        silhouettes++;
                }
            }
        }
        return silhouettes;
    }

    /**
     * Parameter that finds the pixel value.
     *
     * @param imagePixel   current pixel
     * @param transparency background transparency
     * @return alpha channel for a transparent background, brightness for a contrast image
     */
    private static double findPixel(int imagePixel, boolean transparency) {
        if (!transparency) {
            return findLuminances(imagePixel);
        }
        return new Color(imagePixel, true).getAlpha();
    }

    /**
     * A method that analyzes the presence of possible neighboring pixels
     * and passes data to another method to analyze this pixel.
     *
     * @param imagePixels  pixel array pictures
     * @param row          row pictures
     * @param col          column pictures
     * @param transparency background transparency
     * @param background   background value
     */
    private static void findElements(int[][] imagePixels, int row, int col, boolean transparency, double background) {
        Queue<ArrayList<Integer>> pixelSilhouettes = new LinkedList<>();
        ArrayList<Integer> pixel = new ArrayList<>();
        pixel.add(row);
        pixel.add(col);
        pixelSilhouettes.add(pixel);

        while (!pixelSilhouettes.isEmpty()) {
            pixel = pixelSilhouettes.element();
            pixelSilhouettes.remove();
            if (pixel.get(0) > 0 && !foundSilhouettes[pixel.get(0) - 1][pixel.get(1)])
                addFoundPixels(pixelSilhouettes, imagePixels,
                        pixel.get(0) - 1, pixel.get(1), transparency, background);

            if (pixel.get(0) < imagePixels.length - 1 && !foundSilhouettes[pixel.get(0) + 1][pixel.get(1)])
                addFoundPixels(pixelSilhouettes, imagePixels,
                        pixel.get(0) + 1, pixel.get(1), transparency, background);

            if (pixel.get(1) > 0 && !foundSilhouettes[pixel.get(0)][pixel.get(1) - 1])
                addFoundPixels(pixelSilhouettes, imagePixels,
                        pixel.get(0), pixel.get(1) - 1, transparency, background);

            if (pixel.get(1) < imagePixels[0].length - 1 && !foundSilhouettes[pixel.get(0)][pixel.get(1) + 1])
                addFoundPixels(pixelSilhouettes, imagePixels,
                        pixel.get(0), pixel.get(1) + 1, transparency, background);
        }
    }

    /**
     * A method that analyzes the adjacent pixel of a previously found pixel other than the background.
     * If the current pixel differs from the background, then we add it to the queue to analyze its neighbors.
     *
     * @param pixelSilhouettes silhouette pixel analysis queue
     * @param imagePixels      pixel array pictures
     * @param row              row pictures
     * @param col              column pictures
     * @param transparency     background transparency
     * @param background       background value
     */
    private static void addFoundPixels(Queue<ArrayList<Integer>> pixelSilhouettes, int[][] imagePixels,
                                       int row, int col, boolean transparency, double background) {
        double luminancesNextPixels = findPixel(imagePixels[row][col], transparency);
        if (luminancesNextPixels >= background + BORDER_LUMINANCES ||
                luminancesNextPixels <= background - BORDER_LUMINANCES) {
            foundSilhouettes[row][col] = true;
            pixels++;
            ArrayList<Integer> pixel = new ArrayList<>();
            pixel.add(row);
            pixel.add(col);
            pixelSilhouettes.add(pixel);
        }
    }

    /**
     * The method calculates the brightness of a pixel.
     *
     * @param imagePixel current pixel
     * @return pixel brightness
     */
    private static double findLuminances(int imagePixel) {
        return 0.299 * GImage.getRed(imagePixel) +
                0.587 * GImage.getGreen(imagePixel) +
                0.114 * GImage.getBlue(imagePixel);
    }
}

