/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.annotations;

/**
 *
 * @author Ed Armstrong
 */
public enum ProcessLevel {
    NONE,       /* only those marked with NativeJS & !SkipJS */
    ANNOTATED,  /* only those marked with (NativeJS | ServerSide) & !SkipJS (default) */
    ALL         /* only those not marked SkipJS */
}
