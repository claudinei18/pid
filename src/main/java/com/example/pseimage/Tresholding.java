/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.pseimage;

import java.util.List;

/**
 *
 * @author Herbert
 */
public class Tresholding extends Filter implements Filterable {

    @Override
    public String filter(List<String> params) {
        
        this.openImage(params.get(0));
        this.setFilteredImageName("tresholding");
        
        int t = Integer.parseInt(params.get(1));
        //int color1 = Integer.parseInt(params.get(2));
        //int color2 = Integer.parseInt(params.get(3));
        
        for(int i=0; i<image.getWidth(); i++){
            for(int j=0; j<image.getHeight(); j++){
                Pixel n = new Pixel(image.getRGB(i, j));
                    int v = (n.gray > t) ? 255 : 0;
                    n.setRGB(v, v, v);
                    image.setRGB(i, j, n.getComposedPixel());                
            }
        }
        
        this.storeImage();
        return filteredImage;
    }
    
}
