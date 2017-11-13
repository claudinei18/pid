/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.pseimage;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Herbert
 */
public class KMeans extends Filter implements Filterable{

    @Override
    public String filter(List<String> params) {
        
        try {
            this.openImage(params.get(0));
            this.setFilteredImageName("k-means");
            
            int k = Integer.parseInt(params.get(1));
            int[] centroid = new int[k];
            int[] h = Filter.getHistogram(params.get(0));
            int[] groups = new int[h.length];
            
            for(int i=0; i<k; i++){
                centroid[i] = new Random().nextInt(256);
            }
            
            for(int x=0; x<h.length; x++){
                groups[x] = k-1;
            }
            
            boolean changed = true;
            int count = 0;
            while(changed){
                changed = false;
                count++;
                for(int i=0; i<h.length; i++){
                    int distance = Math.abs(h[i] - centroid[groups[i]]);                    

                    for(int m=k-1; m>=0; m--){
                        int newDistance = Math.abs(h[i] - centroid[m]);
                        if(newDistance < distance){
                            groups[i] = m;
                            distance = newDistance;
                            //changed = true;
                            //System.out.println("oi "+i+" "+m+" - "+count);
                        }
                    }
                }
                    
                for(int m=0; m<k; m++){
                    int c = 0;
                    int n = 0;
                    for(int i=0; i<h.length; i++){
                        if(groups[i] == m){
                            c += h[i]*i;
                            n += h[i];
                        }
                    }
                    if(n != 0){
                        int groupMean = c / n;
                        int diff = Math.abs(groupMean - centroid[m]);
                        if(diff > (centroid[m] / 10)){                        
                            centroid[m] = c / n;
                            changed = true;
                        }
                    }
                    
                }
                if(count > 10000000)
                    changed = false;
            }    
            
            for(int i=0; i<image.getWidth(); i++){
                for(int j=0; j<image.getHeight(); j++){
                    Pixel n = new Pixel(image.getRGB(i, j));
                    int v = centroid[groups[n.gray]];
                    n.setRGB(v, v, v);
                    image.setRGB(i, j, n.getComposedPixel());
                }
            }
            
            this.storeImage();
            return filteredImage;
        } catch (IOException ex) {
            Logger.getLogger(KMeans.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
