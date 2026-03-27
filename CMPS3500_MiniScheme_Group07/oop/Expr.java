/**************************************
 * NAME: Hudson Bakke
 * FILE: Expr.java
 * ASGT: CMPS 3500 Group Project
 * DATE: 3/27/2026
 **************************************/

import java.util.ArrayList;
import java.util.List;

/// Abstract Expr class: extended to support all types of expressions in MiniScheme
public abstract class Expr {

    public abstract Expr AddTo(Expr _newExpr) throws ParserException;

    /// AST root
    /// May contain any amount of children of any type except Binding, BindingList, or ParamList
    static class RootExpr extends Expr {

        public List<Expr> children;
        public RootExpr() { children = new ArrayList<>(); }

        @Override
        public Expr AddTo(Expr _newExpr) throws ParserException {
            if (_newExpr instanceof Binding ||
                _newExpr instanceof BindingList ||
                _newExpr instanceof ParamList
            ) throw new ParserException.InvalidNodeType
                ("Cannot add node of type " + _newExpr.getClass().getName());
            children.add(_newExpr);
            return this;
        }
    }

    /// Integer literals
    /// atom node - cannot have children
    static class IntExpr extends Expr {

        public final int value;
        public IntExpr(int _val) { value = _val; }

        @Override
        public Expr AddTo(Expr _newExpr) throws ParserException {
            throw new ParserException.InvalidAdd
                ("You cannot add a sub expression to an int literal");
        }
    }

    /// Boolean literals
    /// atom node - cannot have children
    static class BoolExpr extends Expr {

        public final boolean value;
        public BoolExpr(boolean _val) { value = _val; }

        @Override
        public Expr AddTo(Expr _newExpr) throws ParserException {
            throw new ParserException.InvalidAdd
                ("You cannot add a sub expression to a bool literal");
        }
    }

    /// Programmer-defined symbols
    /// atom node - cannot have children
    static class SymbolExpr extends Expr {

        public final String name;
        public SymbolExpr(String _name) { name = _name; }

        @Override
        public Expr AddTo(Expr _newExpr) throws ParserException {
            throw new ParserException.InvalidAdd
                ("You cannot add a sub expression to a symbol");
        }
    }

    /// Math and logic operations
    /// Must have exactly 3 children - an operator, and 2 operands
    static class OperatorExpr extends Expr {

        public final String op;
        public Expr operand1;
        public Expr operand2;

        public OperatorExpr(String _op) {
            op = _op;
            operand1 = null;
            operand2 = null;
        }

        @Override
        public Expr AddTo(Expr _newExpr) throws ParserException {
            if (operand1 == null) operand1 = _newExpr;
            else if (operand2 == null) operand2 = _newExpr;
            else throw new ParserException.InvalidAdd
                ("Operator expression is already full");
            return this;
        }
    }

    /// Conditional expressions
    /// Must have exactly 3 children - a condition, a THEN, and an ELSE
    static class IfExpr extends Expr {

        public Expr condition;
        public Expr then_expr;
        public Expr else_expr;

        public IfExpr() {
            condition = null;
            then_expr = null;
            else_expr = null;
        }

        @Override
        public Expr AddTo(Expr _newExpr) throws ParserException {
            if (condition == null) condition = _newExpr;
            else if (then_expr == null) then_expr = _newExpr;
            else if (else_expr == null) else_expr = _newExpr;
            else throw new ParserException.InvalidAdd
                ("If expression is already full");
            return this;
        }
    }

    /// Let bindings
    /// Must have exactly 2 children - a name (symbol) and a value
    static class Binding extends Expr {

        public Expr name;
        public Expr value;

        public Binding() {
            name = null;
            value = null;
        }

        @Override
        public Expr AddTo(Expr _newExpr) throws ParserException {
            if (name == null) name = _newExpr;
            else if (value == null) value = _newExpr;
            else throw new ParserException.InvalidAdd
                ("Binding expression is already full");
            return this;
        }
    }

    /// List of let bindings
    /// May have any number of children but they must all be of type Binding
    static class BindingList extends Expr {

        public List<Expr> bindings;
        public BindingList() { bindings = new ArrayList<>();}

        @Override
        public Expr AddTo(Expr _newExpr) throws ParserException {
            bindings.add(_newExpr);
            return this;
        }
    }

    /// Let expression
    /// Must have exactly 2 children - a binding list and a body
    static class LetExpr extends Expr {

        public Expr bindings;
        public Expr body;

        public LetExpr() { 
            bindings = null;
            body = null; 
        }

        @Override
        public Expr AddTo(Expr _newExpr) throws ParserException {
            if (bindings == null) bindings = _newExpr;
            else if (body == null) body = _newExpr;
            else throw new ParserException.InvalidAdd
                ("Let expression is already full");
            return this;
        }
    }

    /// Parameter list
    /// May have any number of children but they must all be of type SymbolExpr
    static class ParamList extends Expr {

        public List<Expr> params;
        public ParamList() { params = new ArrayList<>(); }

        @Override
        public Expr AddTo(Expr _newExpr) throws ParserException {
            params.add(_newExpr);
            return this;
        }
    }

    /// Lambda expressions
    /// Must have exactly 2 children - a parameter list and a body
    static class LambdaExpr extends Expr {

        public Expr params;
        public Expr body;

        public LambdaExpr() {
            params = null;
            body = null;
        }

        @Override
        public Expr AddTo(Expr _newExpr) throws ParserException {
            if (params == null) params = _newExpr;
            else if (body == null) body = _newExpr;
            else throw new ParserException.InvalidAdd
                ("Lambda expression is already full");
            return this;
        }
    }

    /// Define statements
    /// Must have exactly 2 children - a name (symbol) and a value
    static class DefineExpr extends Expr {

        public Expr name;
        public Expr value;

        public DefineExpr() {
            name = null;
            value = null;
        }

        @Override
        public Expr AddTo(Expr _newExpr) throws ParserException {
            if (name == null) name = _newExpr;
            else if (value == null) value = _newExpr;
            else throw new ParserException.InvalidAdd
                ("Define expression is already full");
            return this;
        }
    }

    /// Cond expressions
    /// To be implemented later
    static class CondExpr extends Expr {

        @Override
        public Expr AddTo(Expr _newExpr) throws ParserException {
            return this;
        }
    }

    /// Function calls
    /// Must have at least 1 child - a function (either symbol or lambda) and any number of arguments
    static class CallExpr extends Expr {

        public Expr function;
        public List<Expr> args;

        public CallExpr() {
            function = null;
            args = new ArrayList<>();;
        }

        @Override
        public Expr AddTo(Expr _newExpr) throws ParserException {
            if (function == null) function = _newExpr;
            else args.add(_newExpr);
            return this;
        }
    }
}
