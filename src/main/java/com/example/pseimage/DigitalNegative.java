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
public class DigitalNegative extends Filter implements Filterable{
    /*
    * O filtro negativo converte uma imagem no seu complemento.
    * Cada pixel da imagem é transformado no valor que falta para que o mesmo
    * tenha valor máximo. 
    */
    @Override
    public String filter(List<String> params) {         
        
        this.openImage(params.get(0));
        this.setFilteredImageName("negative");        
              
        for(int x=0; x<image.getWidth(); x++){
            for(int y=0; y<image.getHeight(); y++){
                int pixel = image.getRGB(x, y);
                Pixel p = new Pixel(pixel);  
                int value = 255 - p.gray;
                p.setRGB(value, value, value);  
                image.setRGB(x, y, p.getComposedPixel());
            }
        }        
        this.storeImage();
        return filteredImage;
    }        
}