/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.pseimage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * @author Herbert
 */
public class Filter {

    protected String file, filteredImage;
    protected BufferedImage image, grayImage;

    /**
     * Open a stored image
     *
     * @param file the name of the stored image
     * @return
     */
    public BufferedImage openImage(String file) {
        this.file = file;
        try {
            File file2 = new File(file);
            this.image = ImageIO.read(file2);
            this.grayImage = this.toGray();
        } catch (IOException ex) {
            Logger.getLogger(PSEImage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return image;
    }

    /**
     * Convert the image to 8 bits grayscale
     *
     * @return the converted image
     */
    public BufferedImage toGray() {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int pixel = image.getRGB(x, y);
                int alpha = (pixel >> 24) & 0xff;
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = pixel & 0xff;

                int gray = (red + green + blue) / 3;
                pixel = (alpha << 24) | (gray << 16) | (gray << 8) | gray;
                image.setRGB(x, y, pixel);
            }
        }
        this.storeImage(file + "_grayscale", image);
        return image;
    }

    /**
     * Store the image
     */
    public void storeImage() {
        try {
            ImageIO.write(image, "JPG", new File(filteredImage));
        } catch (IOException ex) {
            Logger.getLogger(Filter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void storeImage(String name, BufferedImage i) {
        try {
            ImageIO.write(i, "JPG", new File(name));
        } catch (IOException ex) {
            Logger.getLogger(Filter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setFilteredImageName(String type) {
        filteredImage = file + "_" + type;
    }

    public static int[] getHistogram(String file) {

        BufferedImage img = new Filter().openImage(file);
        int[] histogram = new int[256];

        for (int k = 0; k < 256; k++) {
            histogram[k] = 0;
        }

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                int rgb = img.getRGB(i, j);
                Pixel p = new Pixel(rgb);
                histogram[p.gray]++;
            }
        }
        return histogram;
    }

}
