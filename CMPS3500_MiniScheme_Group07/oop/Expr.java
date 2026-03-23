import java.util.ArrayList;
import java.util.List;

public abstract class Expr {

    public abstract Expr AddTo(Expr _newExpr) throws ParserException;

    static class RootExpr extends Expr {
        // Root of the AST
        public List<Expr> children;
        public RootExpr() { children = new ArrayList<>(); }

        @Override
        public Expr AddTo(Expr _newExpr) throws ParserException {
            if (_newExpr instanceof Binding ||
                _newExpr instanceof BindingList ||
                _newExpr instanceof ParamList
            ) throw new ParserException.InvalidNodeType("Cannot add node of type " + _newExpr.getClass().getName());
            children.add(_newExpr);
            return this;
        }
    }

    static class IntExpr extends Expr {
        // Integer literals
        public final int value;
        public IntExpr(int _val) { value = _val; }

        @Override
        public Expr AddTo(Expr _newExpr) throws ParserException {
            throw new ParserException.InvalidAdd("You cannot add a sub expression to an int literal");
        }
    }

    static class BoolExpr extends Expr {
        // Boolean literals
        public final boolean value;
        public BoolExpr(boolean _val) { value = _val; }

        @Override
        public Expr AddTo(Expr _newExpr) throws ParserException {
            throw new ParserException.InvalidAdd("You cannot add a sub expression to a bool literal");
        }
    }

    static class SymbolExpr extends Expr {
        // Identifiers
        public final String name;
        public SymbolExpr(String _name) { name = _name; }

        @Override
        public Expr AddTo(Expr _newExpr) throws ParserException {
            throw new ParserException.InvalidAdd("You cannot add a sub expression to a symbol");
        }
    }

    static class IfExpr extends Expr {
        // if statements
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
            else throw new ParserException.InvalidAdd("If expression is already full");
            return this;
        }
    }

    static class Binding extends Expr {
        // A single let statement binding
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
            else throw new ParserException.InvalidAdd("Binding expression is already full");
            return this;
        }
    }

    static class BindingList extends Expr {
        // A list of bindings for one let expression
        public List<Expr> bindings;
        public BindingList() { bindings = new ArrayList<>();}

        @Override
        public Expr AddTo(Expr _newExpr) throws ParserException {
            bindings.add(_newExpr);
            return this;
        }
    }

    static class LetExpr extends Expr {
        // let statements
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
            else throw new ParserException.InvalidAdd("Let expression is already full");
            return this;
        }
    }

    static class ParamList extends Expr {
        // holds function parameters
        public List<Expr> params;
        public ParamList() { params = new ArrayList<>(); }

        @Override
        public Expr AddTo(Expr _newExpr) throws ParserException {
            params.add(_newExpr);
            return this;
        }
    }

    static class LambdaExpr extends Expr {
        // lambda statements
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
            else throw new ParserException.InvalidAdd("Lambda expression is already full");
            return this;
        }
    }

    static class DefineExpr extends Expr {
        // define statements
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
            else throw new ParserException.InvalidAdd("Define expression is already full");
            return this;
        }
    }

    static class CondExpr extends Expr {
        // cond statements
        // to be implemented later
        @Override
        public Expr AddTo(Expr _newExpr) throws ParserException {
            return this;
        }
    }

    static class CallExpr extends Expr {
        // function calls
        public Expr function;
        public Expr params;

        public CallExpr() {
            function = null;
            params = null;
        }

        @Override
        public Expr AddTo(Expr _newExpr) throws ParserException {
            if (function == null) function = _newExpr;
            else if (params == null) params = _newExpr;
            else throw new ParserException.InvalidAdd("Call expression is already full");
            return this;
        }
    }
}
