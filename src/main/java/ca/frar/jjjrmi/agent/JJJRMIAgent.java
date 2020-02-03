/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

/**
 *
 * @author Ed Armstrong
 */
public class JJJRMIAgent {

    public static void premain(String args, Instrumentation inst) {
        System.out.println("JJJRMIAgent premain");

        ClassFileTransformer cft = new ClassFileTransformer() {
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
                System.out.println("cft agent " + className);
                return classfileBuffer;
            }
        };

//        inst.addTransformer(cft);        
        inst.addTransformer(new JJJRMITransformer());
    }
}
