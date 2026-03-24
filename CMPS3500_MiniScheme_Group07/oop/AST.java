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
        else return "Unknown node type";
    }

    public void show() {
        // For debugging
        for (Expr expr : root.children) {
            System.out.println(shownode(expr));
        }
    }
}
