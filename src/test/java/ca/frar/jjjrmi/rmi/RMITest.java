/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.rmi;

import ca.frar.jjjrmi.rmi.testclasses.TestSocket;
import ca.frar.jjjrmi.server.WSTestServer;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Ed Armstrong
 */
public class RMITest {
    private final WSTestServer server;
    
    public RMITest(){
        this.server = new WSTestServer(new TestSocket());
    }
    
    @Test
    public void test_sanity(){
        
    }
    
}
