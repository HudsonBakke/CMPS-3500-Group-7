/**************************************
 * NAME: Hudson Bakke
 * FILE: InterpreterException.java
 * ASGT: CMPS 3500 Group Project
 * DATE: 4/24/2026
 **************************************/

/// Some standard errors
public abstract class InterpreterException extends Exception {
    
    public InterpreterException(String _message) {
        super(_message);
    }

    /// An undeclared identifier is used
    static class UndeclaredIdentifier extends InterpreterException {
        public UndeclaredIdentifier() {
            super("UNDECLARED_IDENTIFIER");
        }
    }

    /// A function is passed the wrong number of arguments
    static class WrongArity extends InterpreterException {
        public WrongArity() {
            super("WRONG_ARITY");
        }
    }

    /// An int is used where a bool is expected, or vice versa
    static class TypeMismatch extends InterpreterException {
        public TypeMismatch() {
            super("TYPE_MISMATCH");
        }
    }

    /// A division by zero is attempted
    static class DivisionByZero extends InterpreterException {
        public DivisionByZero() {
            super("DIVISION_BY_ZERO");
        }
    }
}
