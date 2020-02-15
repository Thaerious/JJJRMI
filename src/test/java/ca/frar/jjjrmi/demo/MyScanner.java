/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.demo;

import org.reflections.Store;
import org.reflections.scanners.SubTypesScanner;

/**
 *
 * @author Ed Armstrong
 */
public class MyScanner extends SubTypesScanner{
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(Demo.class);
    
    public MyScanner(){
        super(false);
    }
    
}
