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
public interface Filterable {
    
    String filter(List<String> params);
    
}
