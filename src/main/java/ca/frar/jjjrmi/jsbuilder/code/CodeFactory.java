package ca.frar.jjjrmi.jsbuilder.code;
import ca.frar.jjjrmi.jsbuilder.JSCodeElement;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtFieldReference;
import spoon.support.reflect.code.CtReturnImpl;

public class CodeFactory {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("JJJRMI");

    private CodeFactory() {}

    public static String binaryOperator(BinaryOperatorKind kind){
        switch (kind) {
            case OR: return"||";
            case AND: return"&&";
            case BITOR: return"|";
            case BITXOR: return"^";
            case BITAND: return"&";
            case EQ: return"===";
            case NE: return"!==";
            case LT: return"<";
            case GT: return">";
            case LE: return"<=";
            case GE: return">=";
            case SL: return"<<";
            case SR: return">>";
            case USR: return">>>";
            case PLUS: return"+";
            case MINUS: return"-";
            case MUL: return"*";
            case DIV: return"/";
            case MOD: return"%";
            case INSTANCEOF: return"instanceof";
            default: return"/* unknown operator */";
        }
    }

    public static JSCodeElement generate(CtElement ctCodeElement){
        if (ctCodeElement == null) return new JSEmptyElement();

        switch (ctCodeElement.getClass().getCanonicalName()){
            case "spoon.support.reflect.code.CtAnnotationImpl": break;
            case "spoon.support.reflect.code.CtAnnotationFieldAccessImpl": break;
            case "spoon.support.reflect.code.CtArrayAccessImpl": break;
            case "spoon.support.reflect.code.CtArrayReadImpl": return new JSArrayRead((CtArrayRead) ctCodeElement);
            case "spoon.support.reflect.code.CtArrayWriteImpl": return new JSArrayWrite((CtArrayWrite) ctCodeElement);
            case "spoon.support.reflect.code.CtAssertImpl": break;
            case "spoon.support.reflect.code.CtAssignmentImpl": return new JSAssignment((CtAssignment) ctCodeElement);
            case "spoon.support.reflect.code.CtBinaryOperatorImpl": return new JSBinaryOperator((CtBinaryOperator) ctCodeElement);
            case "spoon.support.reflect.code.CtBlockImpl": return new JSBlock((CtBlock) ctCodeElement);
            case "spoon.support.reflect.code.CtBreakImpl": return new JSBreak((CtBreak) ctCodeElement);
            case "spoon.support.reflect.code.CtCaseImpl": return new JSCase((CtCase) ctCodeElement);
            case "spoon.support.reflect.code.CtCatchImpl": return new JSCatch((CtCatch) ctCodeElement);
            case "spoon.support.reflect.code.CtCatchVariableImpl": break;
            case "spoon.support.reflect.code.CtCFlowBreakImpl": break;
            case "spoon.support.reflect.code.CtClassImpl": break;
            case "spoon.support.reflect.code.CtCodeSnippetExpressionImpl": break;
            case "spoon.support.reflect.code.CtCodeSnippetStatementImpl": break;
            case "spoon.support.reflect.code.CtCommentImpl": return new JSComment((CtComment) ctCodeElement);
            case "spoon.support.reflect.code.CtConditionalImpl": return new JSConditional((CtConditional) ctCodeElement);
            case "spoon.support.reflect.code.CtConstructorCallImpl": return new JSConstructorCall((CtConstructorCall) ctCodeElement);
            case "spoon.support.reflect.code.CtContinueImpl": return new JSContinue((CtContinue) ctCodeElement);
            case "spoon.support.reflect.code.CtDoImpl": return new JSDo((CtDo) ctCodeElement);
            case "spoon.support.reflect.code.CtEnumImpl": break;
            case "spoon.support.reflect.code.CtExecutableReferenceExpressionImpl": break;
            case "spoon.support.reflect.code.CtExpressionImpl": break;
            case "spoon.support.reflect.code.CtFieldAccessImpl": break;
            case "spoon.support.reflect.code.CtFieldReadImpl": return new JSFieldRead((CtFieldRead) ctCodeElement);
            case "spoon.support.reflect.code.CtFieldWriteImpl": return new JSFieldWrite((CtFieldWrite) ctCodeElement);
            case "spoon.support.reflect.code.CtForImpl": return new JSFor((CtFor) ctCodeElement);
            case "spoon.support.reflect.code.CtForEachImpl": return new JSForEach((CtForEach) ctCodeElement);
            case "spoon.support.reflect.code.CtIfImpl": return new JSIf((CtIf) ctCodeElement);
            case "spoon.support.reflect.code.CtInvocationImpl": return new JSInvocation((CtInvocation) ctCodeElement);
            case "spoon.support.reflect.code.CtJavaDocImpl": break;
            case "spoon.support.reflect.code.CtLabelledFlowBreakImpl": break;
            case "spoon.support.reflect.code.CtLambdaImpl": return new JSLambda((CtLambda) ctCodeElement);
            case "spoon.support.reflect.code.CtLiteralImpl": return new JSLiteral((CtLiteral) ctCodeElement);
            case "spoon.support.reflect.code.CtLocalVariableImpl": return new JSLocalVariable((CtLocalVariable) ctCodeElement);
            case "spoon.support.reflect.code.CtLoopImpl": break;
            case "spoon.support.reflect.code.CtNewArrayImpl": return new JSNewArray((CtNewArray) ctCodeElement);
            case "spoon.support.reflect.code.CtNewClassImpl": break;
            case "spoon.support.reflect.code.CtOperatorAssignmentImpl": return new JSOperatorAssignment((CtOperatorAssignment) ctCodeElement);
            case "spoon.support.reflect.code.CtReturnImpl": return new JSReturnImpl((CtReturnImpl)ctCodeElement);
            case "spoon.support.reflect.code.CtStatementImpl": break;
            case "spoon.support.reflect.code.CtStatementListImpl": break;
            case "spoon.support.reflect.code.CtSuperAccessImpl": return new JSSuperAccess((CtSuperAccess) ctCodeElement);
            case "spoon.support.reflect.code.CtSwitchImpl": return new JSSwitch((CtSwitch) ctCodeElement);
            case "spoon.support.reflect.code.CtSynchronizedImpl": break;
            case "spoon.support.reflect.code.CtTargetedExpressionImpl": break;
            case "spoon.support.reflect.code.CtThisAccessImpl": return new JSThisAccess((CtThisAccess) ctCodeElement);
            case "spoon.support.reflect.code.CtThrowImpl": return new JSThrow((CtThrow) ctCodeElement);
            case "spoon.support.reflect.code.CtTryImpl": return new JSTry((CtTry) ctCodeElement);
            case "spoon.support.reflect.code.CtTryWithResourceImpl": break;
            case "spoon.support.reflect.code.CtTypeAccessImpl": return new JSTypeAccess((CtTypeAccess) ctCodeElement);
            case "spoon.support.reflect.code.CtUnaryOperatorImpl": return new JSUnaryOperator((CtUnaryOperator) ctCodeElement);
            case "spoon.support.reflect.code.CtVariableAccessImpl": break;
            case "spoon.support.reflect.code.CtVariableReadImpl": return new JSVariableRead((CtVariableRead) ctCodeElement);
            case "spoon.support.reflect.code.CtVariableWriteImpl": return new JSVariableWrite((CtVariableWrite) ctCodeElement);
            case "spoon.support.reflect.code.CtWhileImpl": return new JSWhile((CtWhile) ctCodeElement);
        }

        LOGGER.warn("Unhandled element type:"+ ctCodeElement.getClass().getCanonicalName() + " at " + ctCodeElement.getPosition());
        return new JSCodeSnippet("/* unhandled " + ctCodeElement.getClass().getSimpleName() + " */");
    }
}
