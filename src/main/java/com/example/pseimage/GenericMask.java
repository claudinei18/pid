/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.pseimage;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Herbert
 */
public class GenericMask extends Filter implements Filterable{

    @Override
    public String filter(List<String> params) {
        this.openImage(params.get(0));
                
        int[][] mask;
        int[][] newImage;
        
        int maskWidth = Integer.parseInt(params.get(1));
        int maskHeight = Integer.parseInt(params.get(2));
        String type = params.get(3);
        
        this.setFilteredImageName(type+"_mask "+maskWidth+"x"+maskHeight);
        newImage = new int[image.getWidth()][image.getHeight()];
        int edge = maskWidth / 2;
        
        mask = new int[maskWidth][maskHeight];
        int w = 0;
        int paramIndex = 4;
        for(int i=0; i<maskWidth; i++){
            for(int j=0; j<maskHeight; j++){
                try{
                    mask[i][j] = Integer.parseInt(params.get(paramIndex++));
                    //System.out.println(i+" , "+j+" ->"+mask[i][j]);
                    w += mask[i][j];                    
                } catch(NullPointerException ex){
                    Logger.getLogger(Filter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        
        for(int i=edge; i<image.getWidth()-edge; i++){
            for(int j=edge; j<image.getHeight()-edge; j++){
                
               int v = 0;
               for(int x=i-edge; x<=i+edge; x++){
                   for(int y=j-edge; y<=j+edge; y++){
                       
                       Pixel p = new Pixel(image.getRGB(x, y));
                       v += mask[x - (i-edge)][y - (j-edge)] * p.gray;
                       //if((x - (i-edge)) == 2 && (y - (j-edge)) == 2)
                       //System.out.println("i="+i+" j="+j+" x="+x+" y="+y+" v="+v);
                   }
               }
               
               if(type.equals("lowpass") || type.equals("gauss")){
                   v = v / w;                   
               } else{
                   v = Math.min(255, Math.max(0, v));
               }
               
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
