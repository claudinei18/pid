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
        this.toGray();
                
        int[][] mask;
        int[][] newImage;
        
        int maskWidth = Integer.parseInt(params.get(1));
        int maskHeight = Integer.parseInt(params.get(2));
        String type = params.get(3);
        
        this.setFilteredImageName(type+"_mask "+maskWidth+"x"+maskHeight);
        
        int previousEdge = (maskWidth - 1) / 2;
        int rearEdge = 0;
        if((maskWidth % 2) == 0){
            rearEdge = previousEdge + 1;
            //System.out.println("PAR "+rearEdge);
        }
        else
            rearEdge = previousEdge;
        
        newImage = new int[image.getWidth()+previousEdge+rearEdge][image.getHeight()+previousEdge+rearEdge];
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
        
        //Tratamento de bordas
        for(int i=0; i<newImage.length; i++){
            for(int j=0; j<newImage[0].length; j++){
                if(i<previousEdge || i>=newImage.length-rearEdge ||
                       j<previousEdge || j >=newImage[0].length-rearEdge){
                    
                    if(i<previousEdge && j<previousEdge){
                        Pixel p = new Pixel(image.getRGB(0, 0));
                        newImage[i][j] = p.gray;
                    } else if(i<previousEdge && j>=newImage[0].length-rearEdge){
                        Pixel p = new Pixel(image.getRGB(0, image.getHeight()-1));
                        newImage[i][j] = p.gray;
                    } else if(i>=newImage.length-rearEdge && j<previousEdge){
                        Pixel p = new Pixel(image.getRGB(image.getWidth()-1, 0));
                        newImage[i][j] = p.gray;
                    } else if(i>=newImage.length-rearEdge && j>=newImage[0].length-rearEdge){
                        Pixel p = new Pixel(image.getRGB(image.getWidth()-1, image.getHeight()-1));
                        newImage[i][j] = p.gray;
                    } else if(i<previousEdge){
                        //System.out.println(j);
                        Pixel p = new Pixel(image.getRGB(0, j-previousEdge));
                        newImage[i][j] = p.gray;
                    } else if(i>=newImage.length-rearEdge){
                        Pixel p = new Pixel(image.getRGB(image.getWidth()-1, j-previousEdge));
                        newImage[i][j] = p.gray;
                    } else if(j<previousEdge){
                        Pixel p = new Pixel(image.getRGB(i-previousEdge, 0));
                        newImage[i][j] = p.gray;
                    } else{
                        Pixel p = new Pixel(image.getRGB(i-previousEdge, image.getHeight()-1));
                        newImage[i][j] = p.gray;
                    }  
                    
                    //newImage[i][j] = 0;
                } else{
                    Pixel p = new Pixel(image.getRGB(i-previousEdge, j-previousEdge));
                    newImage[i][j] = p.gray;
                }
            }
        }
        
        int[][] finalImage = new int[newImage.length][newImage[0].length];
        for(int i=previousEdge; i<newImage.length-rearEdge; i++){
            for(int j=previousEdge; j<newImage[0].length-rearEdge; j++){
                
                int v = 0;  
               
                for(int x=i-previousEdge; x<=i+rearEdge; x++){
                    for(int y=j-previousEdge; y<=j+rearEdge; y++){
                        //Pixel p = new Pixel(image.getRGB(x, y));
                        v += mask[x - (i-previousEdge)][y - (j-previousEdge)] * newImage[x][y];                            
                    }
                }
               
                if(type.equals("lowpass") || type.equals("gauss")){
                    v = v / w;                   
                } else{
                    v = Math.min(255, Math.max(0, v));
                }
               
                finalImage[i][j] = v;
                
            }
        }
        
        for(int i=0; i<image.getWidth(); i++){
            for(int j=0; j<image.getHeight(); j++){
               Pixel n = new Pixel(image.getRGB(i, j));
               //System.out.println(i+" , "+j+" "+previousEdge);
               int v = finalImage[i+previousEdge][j+previousEdge];
               n.setRGB(v, v, v);
               image.setRGB(i, j, n.getComposedPixel());
            }
        }
        
        this.storeImage();
        return filteredImage;
    }
    
}
