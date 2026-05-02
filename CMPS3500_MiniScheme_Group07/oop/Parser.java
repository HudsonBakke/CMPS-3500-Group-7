/**************************************
 * NAME: Hudson Bakke
 * FILE: Parser.java
 * ASGT: CMPS 3500 Group Project
 * DATE: 3/27/2026
 **************************************/

/// Parser for MiniScheme. ParseExp method recursively returns the following 
/// expression in the token stream converted into an Expr class
public final class Parser {

    private Parser() {}

    public static Expr ParseExp(TokenStream stream) throws ParserException {

        Expr new_expr = null;

        // Handles atom nodes
        if (stream.peek().type != TokenType.L_PAREN) {

            // Root expression
            if (stream.peek().type == TokenType.BEGIN_STREAM) {
                new_expr = new Expr.RootExpr();
                stream.dequeue();
                while (stream.peek().type != TokenType.END_STREAM) {
                    new_expr.AddTo(ParseExp(stream));
                }
                return new_expr;
            }

            // Identifiers
            if (stream.peek().type == TokenType.IDENTIFIER) {
                new_expr = new Expr.SymbolExpr(stream.peek().val);
            }

            // Integer literals
            else if (stream.peek().type == TokenType.INTEGER) {
                new_expr = new Expr.IntExpr(Integer.parseInt(stream.peek().val));
            }

            // Boolean literals
            else if (stream.peek().type == TokenType.BOOL_TRUE) {
                new_expr = new Expr.BoolExpr(true);
            } else if (stream.peek().type == TokenType.BOOL_FALSE) {
                new_expr = new Expr.BoolExpr(false);
            }

            // Error if the atom type is unrecognized
            else throw new ParserException.InvalidNodeType("Invalid atom node type");

            stream.dequeue();
        }

        // Handles non-atom nodes
        else {
            stream.dequeue();
            switch (stream.peek().type) {

                // Operator expressions
                case OPERATOR_PLUS:
                case OPERATOR_MINUS:
                case OPERATOR_MULTIPLY:
                case OPERATOR_DIVIDE:
                case OPERATOR_EQUALS:
                case OPERATOR_LESS:
                case OPERATOR_GREATER:
                case OPERATOR_LESSEQUALS:
                case OPERATOR_GREATEREQUALS:
                    new_expr = new Expr.OperatorExpr(stream.dequeue().val);
                    new_expr.AddTo(ParseExp(stream));
                    new_expr.AddTo(ParseExp(stream));
                    break;

                // Conditional expressions
                case SPECIAL_IF:
                    stream.dequeue();
                    new_expr = new Expr.IfExpr();
                    new_expr.AddTo(ParseExp(stream));
                    new_expr.AddTo(ParseExp(stream));
                    new_expr.AddTo(ParseExp(stream));
                    break;

                case SPECIAL_COND:
                    stream.dequeue();
                    new_expr = ParseExp(stream, "cond list");
                    break;

                // Let expressions
                case SPECIAL_LET:
                    stream.dequeue();
                    new_expr = new Expr.LetExpr();
                    new_expr.AddTo(ParseExp(stream, "binding list"));
                    new_expr.AddTo(ParseExp(stream));
                    break;

                // Lambda expressions
                case SPECIAL_LAMBDA:
                    stream.dequeue();
                    new_expr = new Expr.LambdaExpr();
                    new_expr.AddTo(ParseExp(stream, "parameter list"));
                    new_expr.AddTo(ParseExp(stream));
                    break;
                
                // Define expressions
                case SPECIAL_DEFINE:
                    stream.dequeue();
                    new_expr = new Expr.DefineExpr();
                    new_expr.AddTo(ParseExp(stream));
                    new_expr.AddTo(ParseExp(stream));
                    break;

                // Call expressions
                case IDENTIFIER:
                case L_PAREN:
                    new_expr = new Expr.CallExpr();
                    new_expr.AddTo(ParseExp(stream));
                    while (stream.peek().type != TokenType.R_PAREN) {
                        new_expr.AddTo(ParseExp(stream));
                    }
                    break;
                
                default: throw new ParserException.InvalidNodeType("Encountered an unexpected token");
            }
            if (stream.peek().type != TokenType.R_PAREN) {
                throw new ParserException.MalformedParentheses("Expected right parenthesis");
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

            case "cond option":
                stream.dequeue();
                new_expr = new Expr.CondOption();

                switch (stream.peek().type) {

                    case TokenType.SPECIAL_ELSE:
                        stream.dequeue();
                        new_expr.AddTo(new Expr.BoolExpr(true));
                        break;

                    case TokenType.BOOL_TRUE:
                        stream.dequeue();
                        new_expr.AddTo(new Expr.BoolExpr(true));
                        break;

                    case TokenType.BOOL_FALSE:
                        stream.dequeue();
                        new_expr.AddTo(new Expr.BoolExpr(false));
                        break;

                    case TokenType.L_PAREN:
                        new_expr.AddTo(ParseExp(stream));
                        break;

                    default:
                        throw new ParserException.InvalidNodeType
                        ("Wrong node type provided in cond option");
                }
                new_expr.AddTo(ParseExp(stream));
                break;

            case "cond list":
                new_expr = new Expr.CondExpr();
                while (stream.peek().type == TokenType.L_PAREN) {
                    new_expr.AddTo(ParseExp(stream, "cond option"));
                }
                return new_expr;
        }
        if (stream.peek().type != TokenType.R_PAREN) {
            throw new ParserException.MalformedParentheses("Expected right parenthesis");
        }
        stream.dequeue();
        return new_expr;
    }
}