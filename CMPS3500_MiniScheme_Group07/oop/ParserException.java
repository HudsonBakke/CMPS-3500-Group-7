/**************************************
 * NAME: Hudson Bakke
 * FILE: ParserException.java
 * ASGT: CMPS 3500 Group Project
 * DATE: 3/27/2026
 **************************************/

/// Standard parser errors
public abstract class ParserException extends Exception {

    public ParserException(String _message) {
        super(_message);
    }

    /// Too many nodes were added to an expression,
    /// or a node was added to an atom expression
    static class InvalidAdd extends ParserException {
        public InvalidAdd(String _message) {
            super(_message);
        }
    }

    /// A node was added to an expression of an invalid type
    static class InvalidNodeType extends ParserException {
        public InvalidNodeType(String _message) {
            super(_message);
        }
    }

    /// No parentheses where parentheses were expected,
    /// or parentheses where no parentheses were expected
    static class MalformedParentheses extends ParserException {
        public MalformedParentheses(String _message) {
            super(_message);
        }
    }
}