/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.socket.testclasses;

/**
 *
 * @author Ed Armstrong
 */
interface Operation {
    public void run(RemoteStack data) throws NoDataException;
}
