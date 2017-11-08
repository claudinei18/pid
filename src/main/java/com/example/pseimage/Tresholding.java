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
        
        int L = Integer.parseInt(params.get(1));
        int color1 = Integer.parseInt(params.get(2));
        int color2 = Integer.parseInt(params.get(3));
        
        for(int i=0; i<image.getWidth(); i++){
            for(int j=0; j<image.getHeight(); j++){
                
                int v = image.getRGB(i, j);
                Pixel p = new Pixel(v);
                
                if(p.gray > L){
                    p.setRGB(color1, color1, color1);
                } else{
                    p.setRGB(color2, color2, color2);
                }
                
                image.setRGB(i, j, p.getComposedPixel());
                
            }
        }
        
        this.storeImage();
        return filteredImage;
    }
    
}
