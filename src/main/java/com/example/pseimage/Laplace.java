/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.pseimage;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Herbert
 */
public class Laplace extends Filter implements Filterable{
    
    /*
    * O filtro Laplaciano é um filtro passa-altas. Ele é utilizado para evidenciar
    * as componentes de alta frequência da imagem (bordas). Como efeito colateral
    * ele também realça ruídos existentes na imagem.
    */
    @Override
    public String filter(List<String> params) {
        
        this.openImage(params.get(0));
        this.setFilteredImageName("laplace");
        
        List<String> laplaceMask = new ArrayList<>();
        laplaceMask.add(this.file);
        laplaceMask.add("3");
        laplaceMask.add("3");
        laplaceMask.add("laplace");
        laplaceMask.add("0");
        laplaceMask.add("-1");
        laplaceMask.add("0");
        laplaceMask.add("-1");
        laplaceMask.add("4");
        laplaceMask.add("-1");
        laplaceMask.add("0");
        laplaceMask.add("-1");
        laplaceMask.add("0");     
        
        return new GenericMask().filter(laplaceMask);
    }
    
    /*
    @Override
    public String filter(List<String> params) {      
        
        this.openImage(params.get(0));
        this.setFilteredImageName("laplace");
        
        int width  = image.getWidth();
        int height = image.getHeight();                

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                
                Pixel c00 = new Pixel(image.getRGB(x-1, y-1));
                Pixel c01 = new Pixel(image.getRGB(x-1, y));
                Pixel c02 = new Pixel(image.getRGB(x-1, y+1));
                Pixel c10 = new Pixel(image.getRGB(x, y-1));
                Pixel c11 = new Pixel(image.getRGB(x, y));
                Pixel c12 = new Pixel(image.getRGB(x, y+1));
                Pixel c20 = new Pixel(image.getRGB(x+1, y-1));
                Pixel c21 = new Pixel(image.getRGB(x+1, y));
                Pixel c22 = new Pixel(image.getRGB(x+1, y+1));
                
                int p = -c00.gray - c01.gray - c02.gray +
                        -c10.gray + 8*c11.gray - c12.gray +
                        -c20.gray - c21.gray - c22.gray;
                
                
                p = Math.min(255, Math.max(0, p));   
                p = p + c11.gray;
                c11.setRGB(p, p, p);
                image.setRGB(x, y, c11.getComposedPixel());
            }
        }             
        return image;
    }*/
    
}
