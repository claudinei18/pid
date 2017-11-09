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
public class SubtractImage extends Filter implements Filterable {

    @Override
    public String filter(List<String> params) {
        
        BufferedImage img1 = this.openImage(params.get(0));
        BufferedImage img2 = this.openImage(params.get(1));
        String[] aux = params.get(0).split("/");
        String nomeAux = aux[aux.length - 1];
        this.setFilteredImageName("subtract "+nomeAux);
        
        for(int i=0; i<img1.getWidth(); i++){
            for(int j=0; j<img1.getHeight(); j++){
                
                int v1 = img1.getRGB(i, j);
                Pixel im1 = new Pixel(v1);
                int v2 = img2.getRGB(i, j);
                Pixel im2 = new Pixel(v2);
                
                int result = im1.gray - im2.gray;
                result = Math.min(255, Math.max(0, result));
                im2.setRGB(result, result, result);
                
                img2.setRGB(i, j, im2.getComposedPixel());
                
            }
        }
        
        this.storeImage(this.filteredImage, img2);
        return filteredImage;
    }
    
}