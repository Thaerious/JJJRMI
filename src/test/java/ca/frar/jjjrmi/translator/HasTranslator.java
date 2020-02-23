/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.translator;

/**
 *
 * @author Ed Armstrong
 */
public interface HasTranslator {
    Translator getTranslator();
    void addResult(TranslatorResult result);
}
