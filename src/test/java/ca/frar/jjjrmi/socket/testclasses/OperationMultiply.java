/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.socket.testclasses;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.socket.JJJObject;


public class OperationMultiply extends JJJObject implements Operation{
    int value = 0;
    
    private OperationMultiply(){}
    
    @NativeJS
    public OperationMultiply(int value){
        this.value = value;
    }
    
    public void run(RemoteData data) throws NoDataException{
        int a = data.pop();
        a = a * value;
        data.push(a);
    }    
}
