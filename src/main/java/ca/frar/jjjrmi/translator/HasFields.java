/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.translator;

import java.lang.reflect.Field;

/**
 *
 * @author edward
 */
interface HasFields {
    public Field getField(Class<?> aClass, String name);
}
