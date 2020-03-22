/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.socket.testclasses;

import ca.frar.jjjrmi.rmi.socket.JJJSocket;

/**
 *
 * @author Ed Armstrong
 */
public class TestSocket extends JJJSocket<TestRoot>{
    private TestRoot root;

    @Override
    public TestRoot getRoot() {
        if (this.root == null) this.root = new TestRoot();
        return this.root;
    }
}
