/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.jsbuilder.code;

import java.util.List;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLambda;
import spoon.reflect.declaration.CtParameter;

/**
 *
 * @author Ed Armstrong
 */
public class JSLambda extends AbstractJSCodeElement{    
    private final List<CtParameter<?>>  parameters;
    private JSCodeElement body = null;
    private JSCodeElement expression = null;

    JSLambda(CtLambda ctLambda) {
        parameters = ctLambda.getParameters();
        if (ctLambda.getBody() != null) this.body = this.generate(ctLambda.getBody());
        if (ctLambda.getExpression() != null) this.expression = this.generate(ctLambda.getExpression());
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("(");       
        for (int i = 0; i < parameters.size(); i++){
            CtParameter<?> parameter = parameters.get(i);
            if (i == 0) builder.append(parameter);
            else builder.append(", ").append(parameter);
        }        
        builder.append(")=>");
        if (body != null) builder.append(body);
        if (expression != null) builder.append(expression);
        return builder.toString();
    }    
}
