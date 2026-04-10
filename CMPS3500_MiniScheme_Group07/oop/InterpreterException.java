public abstract class InterpreterException extends Exception {
    
    public InterpreterException(String _message) {
        super(_message);
    }

    static class UndeclaredIdentifier extends InterpreterException {
        public UndeclaredIdentifier() {
            super("UNDECLARED_IDENTIFIER");
        }
    }

    static class WrongArity extends InterpreterException {
        public WrongArity() {
            super("WRONG_ARITY");
        }
    }

    static class TypeMismatch extends InterpreterException {
        public TypeMismatch() {
            super("TYPE_MISMATCH");
        }
    }

    static class DivisionByZero extends InterpreterException {
        public DivisionByZero() {
            super("DIVISION_BY_ZERO");
        }
    }
}
