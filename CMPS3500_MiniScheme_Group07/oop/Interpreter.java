/**************************************
 * NAME: Hudson Bakke
 * FILE: Interpreter.java
 * ASGT: CMPS 3500 Group Project
 * DATE: 4/24/2026
 **************************************/

import java.util.ArrayList;
import java.util.List;

public final class Interpreter {
    
    private static List<Expr.Binding> definitions = new ArrayList<>();

    private Interpreter() {}

    /// Recursively interprets an expression and collapses it down to an atom node
    public static Expr Interpret(Expr e, List<Expr.Binding> scope) throws InterpreterException, ParserException {

        if (e instanceof Expr.RootExpr root_expr) {
            Expr result = null;
            for (Expr expr : root_expr.children) {
                result = Interpret(expr, scope);
            }
            return result;
        }

        // Literals nodes just return
        else if (e instanceof Expr.IntExpr) return e;
        else if (e instanceof Expr.BoolExpr) return e;

        // Symbols trigger the interpreter to seach backwards through the scope; in this way, shadowing is preserved,
        // as the last recorded binding of a value to that specific symbol will be used
        else if (e instanceof Expr.SymbolExpr symbol_expr) {
            for (int i = scope.size()-1; i >= 0; i--) {
                Expr.Binding candidate = scope.get(i);
                if (((Expr.SymbolExpr)candidate.name).name.equals(symbol_expr.name)) {
                    return candidate.value;
                }
            }
            // If no matches are found in the current scope, fall back to global definitions
            for (int i = definitions.size()-1; i >= 0; i--) {
                Expr.Binding candidate = definitions.get(i);
                if (((Expr.SymbolExpr)candidate.name).name.equals(symbol_expr.name)) {
                    return candidate.value;
                }
            }

            throw new InterpreterException.UndeclaredIdentifier();
        }

        else if (e instanceof Expr.OperatorExpr operator_expr) {
            Expr operand1 = Interpret(operator_expr.operand1, scope);
            Expr operand2 = Interpret(operator_expr.operand2, scope);

            // For now can only handle operations on integers - no boolean operands
            switch (operator_expr.op) {
                case "+": return new Expr.IntExpr(VerifyInt(operand1).value + VerifyInt(operand2).value);
                case "-": return new Expr.IntExpr(VerifyInt(operand1).value - VerifyInt(operand2).value);
                case "*": return new Expr.IntExpr(VerifyInt(operand1).value * VerifyInt(operand2).value);
                case "/":
                    if (VerifyInt(operand2).value == 0) {
                        throw new InterpreterException.DivisionByZero();
                    } 
                    return new Expr.IntExpr(VerifyInt(operand1).value / VerifyInt(operand2).value);
                case "=": return new Expr.BoolExpr(VerifyInt(operand1).value == VerifyInt(operand2).value);
                case "<": return new Expr.BoolExpr(VerifyInt(operand1).value < VerifyInt(operand2).value);
                case ">": return new Expr.BoolExpr(VerifyInt(operand1).value > VerifyInt(operand2).value);
                case "<=": return new Expr.BoolExpr(VerifyInt(operand1).value <= VerifyInt(operand2).value);
                case ">=": return new Expr.BoolExpr(VerifyInt(operand1).value >= VerifyInt(operand2).value);
                default: return null;
            }
        }

        else if (e instanceof Expr.IfExpr if_expr) {
            Expr condition = Interpret(if_expr.condition, scope);
            return (VerifyBool(condition)).value 
                ? Interpret(if_expr.then_expr, scope) 
                : Interpret(if_expr.else_expr, scope);
        }

        else if (e instanceof Expr.LetExpr let_expr) {
            List<Expr.Binding> new_scope = new ArrayList<>(scope);
            for (Expr binding : ((Expr.BindingList)let_expr.bindings).bindings) {
                new_scope.add(
                    new Expr.Binding().AddTo(((Expr.Binding)binding).name)
                                      .AddTo(Interpret(((Expr.Binding)binding).value, scope))               
                );
            }
            return Interpret(let_expr.body, new_scope);
        }

        // Lambda expressions just return; that way they can be called in call expressions
        else if (e instanceof Expr.LambdaExpr lambda_expr) {
            List<Expr.Binding> environment = new ArrayList<>(definitions);
            environment.addAll(scope);
            lambda_expr.CaptureEnvironment(environment);
            return lambda_expr;
        }

        else if (e instanceof Expr.DefineExpr define_expr) {
            definitions.add(
                new Expr.Binding().AddTo(define_expr.name)
                                  .AddTo(Interpret(define_expr.value, scope))
            );
            return null;
        }

        else if (e instanceof Expr.CondExpr cond_expr) {
            for (Expr option : cond_expr.options) {
                Expr.CondOption condOption = (Expr.CondOption) option;
                Expr condition = Interpret(condOption.condition, scope);
                if (VerifyBool(condition).value) {
                    return Interpret(condOption.body, scope);
                }
            }
            return null;
        }

        else if (e instanceof Expr.CallExpr call_expr) {
            Expr f = Interpret(call_expr.function, scope);
            if (f instanceof Expr.LambdaExpr func) {
                List<Expr> params = ((Expr.ParamList) func.params).params;

                if (params.size() != call_expr.args.size()) {
                    throw new InterpreterException.WrongArity();
                }

                List<Expr.Binding> new_scope = new ArrayList<>(func.environment);
                for (int i = 0; i < call_expr.args.size(); i++) {
                    new_scope.add(
                        new Expr.Binding().AddTo(params.get(i))
                                          .AddTo(Interpret(call_expr.args.get(i), scope))
                    );
                }
                return Interpret(func.body, new_scope);
            }
            else throw new InterpreterException.TypeMismatch();
        }

        else return null;
    }

    // Downcasts and verifies that a literal expression is of type IntExpr, otherwise throws
    // InterpreterException.TypeMismatch
    public static Expr.IntExpr VerifyInt(Expr e) throws InterpreterException.TypeMismatch {
        if (e instanceof Expr.IntExpr int_expr) return int_expr;
        throw new InterpreterException.TypeMismatch();
    }

    // Downcasts and verifies that a literal expression is of type BoolExpr, otherwise throws
    // InterpreterException.TypeMismatch
    public static Expr.BoolExpr VerifyBool(Expr e) throws InterpreterException.TypeMismatch {
        if (e instanceof Expr.BoolExpr bool_expr) return bool_expr;
        throw new InterpreterException.TypeMismatch();
    }
}
