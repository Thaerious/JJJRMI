/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.socket;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author Ed Armstrong
 */
public class JJJContextListener implements ServletContextListener{
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(JJJContextListener.class);
    
    @Override
    public void contextInitialized(ServletContextEvent event) {
        LOGGER.debug("contextInitialized");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        LOGGER.debug("contextDestroyed");
    }
    
}
