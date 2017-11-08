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
public class Gaussian extends Filter implements Filterable{
    
    int[] gauss1 = {1, 1};
    int[] gauss2 = {1, 2, 1};
    int[] gauss3 = {1, 3, 3, 1};
    int[] gauss4 = {1, 4, 6, 4, 1};
    int[] gauss5 = {1, 5, 10, 10, 5, 1};
    int[] gauss6 = {1, 6, 15, 20, 15, 6, 1};
    int[] gauss7 = {1, 7, 21, 35, 35, 21, 7, 1};
    int[] gauss8 = {1, 8, 28, 56, 70, 56, 28, 8, 1};

    @Override
    public String filter(List<String> params) {
        this.openImage(params.get(0));
        //this.setFilteredImageName("gauss");
        
        int[][] mask = getMask(Integer.parseInt(params.get(1)));
        
        List<String> gaussianMask = new ArrayList<>();
        gaussianMask.add(this.file);
        gaussianMask.add(""+mask.length);
        gaussianMask.add(""+mask[0].length);
        gaussianMask.add("gauss");
        
        for(int i=0; i<mask.length; i++){
            for(int j=0; j<mask[0].length; j++){
                gaussianMask.add(""+mask[i][j]);
            }
        }
        return new GenericMask().filter(gaussianMask);
    }

    private int[][] getMask(int level) {
        int[] gauss;
        switch(level){
            case 1:
                gauss = gauss1;
                break;
                
            case 2:
                gauss = gauss2;
                break;
                
            case 3:
                gauss = gauss3;
                break;
                
            case 4:
                gauss = gauss4;
                break;
                
            case 5:
                gauss = gauss5;
                break;
                
            case 6:
                gauss = gauss6;
                break;
                
            case 7:
                gauss = gauss7;
                break;
                
            case 8:
                gauss = gauss8;
                break; 
            
            default:
                gauss = new int[4];
        }
        
        int[][] mask = new int[gauss.length][gauss.length];
        
        for(int i=0; i<gauss.length; i++){
            mask[i][0] = mask[0][i] = gauss[i];
        }
        
        for(int i=1; i<gauss.length; i++){
            for(int j=1; j<gauss.length; j++){
                mask[i][j] = mask[i][0] * mask[0][i];
            }
        }
        
        return mask;
    }
    
}
