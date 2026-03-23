public abstract class ParserException extends Exception {

    public ParserException(String _message) {
        super(_message);
    }

    static class InvalidAdd extends ParserException {
        public InvalidAdd(String _message) {
            super(_message);
        }
    }

    static class InvalidNodeType extends ParserException {
        public InvalidNodeType(String _message) {
            super(_message);
        }
    }
}