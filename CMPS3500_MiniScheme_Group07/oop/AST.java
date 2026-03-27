/**************************************
 * NAME: Hudson Bakke
 * FILE: AST.java
 * ASGT: CMPS 3500 Group Project
 * DATE: 3/27/2026
 **************************************/

/// The AST class holds the Abstract Syntax Tree for the MiniScheme program, starting at the root.
/// The show() method is used for debugging by printing the contents of the AST.
public class AST {

    public Expr.RootExpr root;
    public AST() { root = new Expr.RootExpr(); }

    private String shownode(Expr node) {
        // For debugging

        if (node instanceof Expr.IntExpr int_node) {
            return ("IntLiteral {\n"
                   +"\tValue: " + Integer.toString(int_node.value)
                   +"\n}"
            );
        }
        else if (node instanceof Expr.BoolExpr bool_node) {
            return ("BoolLiteral {\n"
                   +"\tValue: " + Boolean.toString(bool_node.value)
                   +"\n}"
            );
        }
        else if (node instanceof Expr.SymbolExpr ident_node) {
            return ("Identifier {\n"
                   +"\tName: \"" + ident_node.name
                   +"\"\n}"
            );
        }
        else if (node instanceof Expr.OperatorExpr op_node) {
            return ("MathOperation {\n"
                   +"\tType: " + op_node.op
                   +"\n\tOperand1: " + shownode(op_node.operand1)
                   +"\n\tOperand2: " + shownode(op_node.operand2)
                   +"\n}"
            );
        }
        else if (node instanceof Expr.IfExpr if_node) {
            return ("IfExpression {\n"
                   +"\tCondition: " + shownode(if_node.condition)
                   +"\n\tThen: " + shownode(if_node.then_expr)
                   +"\n\tElse: " + shownode(if_node.else_expr)
                   +"\n}"
            );
        }
        else if (node instanceof Expr.Binding binding_node) {
            return ("LetBinding {\n"
                   +"\tName: " + shownode(binding_node.name)
                   +"\n\tValue: " + shownode(binding_node.value)
            );
        }
        else if (node instanceof Expr.BindingList binding_list_node) {
            String ret = "LetBindingList {\n";
            int i = 1;
            for (Expr binding : binding_list_node.bindings) {
                ret += "\tBinding" + Integer.toString(i) + ": " + shownode(binding) + "\n";
                i++;
            }
            ret += "}";
            return ret;
        }
        else if (node instanceof Expr.LetExpr let_node) {
            return ("LetExpression {\n"
                   +"\tBindings: " + shownode(let_node.bindings)
                   +"\n\tBody: " + shownode(let_node.body)
                   +"\n}"
            );
        }
        else if (node instanceof Expr.ParamList param_list_node) {
            String ret = "ParameterList {\n";
            int i = 1;
            for (Expr param : param_list_node.params) {
                ret += "\tParameter" + Integer.toString(i) + ": " + shownode(param) + "\n";
                i++;
            }
            ret += "}";
            return ret;
        }
        else if (node instanceof Expr.LambdaExpr lambda_node) {
            return ("LambdaExpression {\n"
                   +"Parameters: " + shownode(lambda_node.params)
                   +"Body: " + shownode(lambda_node.body)
            );
        }
        else if (node instanceof Expr.DefineExpr define_node) {
            return ("DefineExpression {\n"
                   +"\tName: " + shownode(define_node.name)
                   +"\n\tValue: " + shownode(define_node.value) 
            );
        }
        else if (node instanceof Expr.CondExpr) {
            return "cond expressions not implemented yet";
        }

        else if (node instanceof Expr.CallExpr call_node) {
            String ret = "CallExpression {\n"
                        +"\tFunction: " + shownode(call_node.function) + "\n";
            int i = 1;
            for (Expr arg : call_node.args) {
                ret += "\tArgument" + Integer.toString(i) + ": " + shownode(arg) + "\n";
                i++;
            }
            ret += "}";
            return ret;
        }
        else return "Unknown node type";
    }

    public void show() {
        // For debugging
        for (Expr expr : root.children) {
            System.out.println(shownode(expr));
        }
    }
}
