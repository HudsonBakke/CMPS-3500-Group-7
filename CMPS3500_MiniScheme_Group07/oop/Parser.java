public final class Parser {

    private Parser() {}

    public static Expr ParseExp(TokenStream stream) throws ParserException {

        Expr new_expr = null;

        if (stream.peek().type != TokenType.L_PAREN) {

            if (stream.peek().type == TokenType.BEGIN_STREAM) {
                new_expr = new Expr.RootExpr();
                stream.dequeue();
                while (stream.peek().type != TokenType.END_STREAM) {
                    new_expr.AddTo(ParseExp(stream));
                }
                return new_expr;
            }

            if (stream.peek().type == TokenType.IDENTIFIER) {
                new_expr = new Expr.SymbolExpr(stream.peek().val);
            }
            else if (stream.peek().type == TokenType.INTEGER) {
                new_expr = new Expr.IntExpr(Integer.parseInt(stream.peek().val));
            }
            else if (stream.peek().type == TokenType.BOOL_TRUE) {
                new_expr = new Expr.BoolExpr(true);
            }
            else if (stream.peek().type == TokenType.BOOL_FALSE) {
                new_expr = new Expr.BoolExpr(false);
            }
            else {
                throw new ParserException.InvalidNodeType("Invalid atom node type");
            }

            stream.dequeue();
        }

        else {
            stream.dequeue();
            switch (stream.peek().type) {

                case OPERATOR_PLUS:
                case OPERATOR_MINUS:
                case OPERATOR_MULTIPLY:
                case OPERATOR_DIVIDE:
                case OPERATOR_EQUALS:
                case OPERATOR_LESS:
                case OPERATOR_GREATER:
                case OPERATOR_LESSEQUALS:
                case OPERATOR_GREATEREQUALS:
                    new_expr = new Expr.OperatorExpr(stream.dequeue().val.charAt(0));
                    new_expr.AddTo(ParseExp(stream));
                    new_expr.AddTo(ParseExp(stream));
                    break;

                case SPECIAL_IF:
                    stream.dequeue();
                    new_expr = new Expr.IfExpr();
                    new_expr.AddTo(ParseExp(stream));
                    new_expr.AddTo(ParseExp(stream));
                    new_expr.AddTo(ParseExp(stream));
                    break;

                case SPECIAL_COND:
                    // To be implemented later
                    break;

                case SPECIAL_LET:
                    stream.dequeue();
                    new_expr = new Expr.LetExpr();
                    new_expr.AddTo(ParseExp(stream, "binding list"));
                    new_expr.AddTo(ParseExp(stream));
                    break;

                case SPECIAL_LAMBDA:
                    stream.dequeue();
                    new_expr = new Expr.LambdaExpr();
                    new_expr.AddTo(ParseExp(stream, "parameter list"));
                    new_expr.AddTo(ParseExp(stream));
                    break;
                
                case SPECIAL_DEFINE:
                    stream.dequeue();
                    new_expr = new Expr.DefineExpr();
                    new_expr.AddTo(ParseExp(stream));
                    new_expr.AddTo(ParseExp(stream));
                    break;

                case IDENTIFIER:
                case L_PAREN:
                    new_expr = new Expr.CallExpr();
                    new_expr.AddTo(ParseExp(stream));
                    while (stream.peek().type != TokenType.R_PAREN) {
                        new_expr.AddTo(ParseExp(stream));
                    }
                    break;
                
                default: ;
            }
            stream.dequeue();
        }
        return new_expr;
    }

    public static Expr ParseExp(TokenStream stream, String flag) throws ParserException {
        // For parsing list expressions
        Expr new_expr = null;

        switch (flag) {

            case "binding":
                stream.dequeue();
                new_expr = new Expr.Binding();
                new_expr.AddTo(ParseExp(stream));
                new_expr.AddTo(ParseExp(stream));
                break;

            case "binding list":
                stream.dequeue();
                new_expr = new Expr.BindingList();
                while (stream.peek().type == TokenType.L_PAREN) {
                    new_expr.AddTo(ParseExp(stream, "binding"));
                }
                break;

            case "parameter list":
                stream.dequeue();
                new_expr = new Expr.ParamList();
                while (stream.peek().type == TokenType.IDENTIFIER) {
                    new_expr.AddTo(ParseExp(stream));
                }
                break;
        }

        stream.dequeue();
        return new_expr;
    }
}