/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.pseimage;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 *
 * @author Herbert
 */
public class Logarithmic extends Filter implements Filterable{

    @Override
    public String filter(List<String> params) {
        
        this.openImage(params.get(0));
        this.setFilteredImageName("logarithmic");        
        int c = Integer.parseInt(params.get(1));
        
        //c = 40;
                
        for(int x=0; x<image.getWidth(); x++){
            for(int y=0; y<image.getHeight(); y++){
                int pixel = image.getRGB(x, y);
                Pixel p = new Pixel(pixel);
                double k = 1.0 + (double)p.gray;
                
                double log = Math.log(k);                
                //System.out.println(log);
                int value = (int) (c * log);                
                p.setRGB(value, value, value);  
                image.setRGB(x, y, p.getComposedPixel());
            }
        }
        
        this.storeImage();
        return filteredImage;
    }
    
}
