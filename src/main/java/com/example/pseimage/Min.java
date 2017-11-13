/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.pseimage;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.PriorityQueue;

/**
 *
 * @author Herbert
 */
public class Min extends Filter implements Filterable{
    
    /*
    * O filtro de mínima realça regiões escuras da imagem. Ele aumenta o tamanho de
    * objetos mais escuros que o fundo e diminui o tamanho de objetos mais claros
    * que o fundo em que estão inseridos.
    */
    @Override
    public String filter(List<String> params) {
        
        this.openImage(params.get(0));
        this.setFilteredImageName("min");
        
        int maskWidth = Integer.parseInt(params.get(1));
        int maskHeight = Integer.parseInt(params.get(2));
        
        int edge = maskWidth / 2;
        int[][] newImage = new int[image.getWidth()][image.getHeight()];

        for(int i=edge; i<image.getWidth()-edge; i++){
            for(int j=edge; j<image.getHeight()-edge; j++){
               
               PriorityQueue<Integer> values = new PriorityQueue<>();
               int v = 0;
               for(int x=i-edge; x<=i+edge; x++){
                   for(int y=j-edge; y<=j+edge; y++){
                       
                       Pixel p = new Pixel(image.getRGB(x, y));
                       v = p.gray;
                       values.add(v);
                       
                   }
               }                           
               
               v = values.element();    
               //for(int k=0; k<maskWidth * maskHeight; k++){
               //    System.out.print(values.remove()+" ");
               //}
               //System.out.println(v);
               newImage[i][j] = v;
               
                
            }
        }     
        
        for(int i=edge; i<image.getWidth()-edge; i++){
            for(int j=edge; j<image.getHeight()-edge; j++){
               Pixel n = new Pixel(image.getRGB(i, j));
               int v = newImage[i][j];
               n.setRGB(v, v, v);
               image.setRGB(i, j, n.getComposedPixel());
            }
        }
        
        this.storeImage();
        return filteredImage;
    }
    
}
