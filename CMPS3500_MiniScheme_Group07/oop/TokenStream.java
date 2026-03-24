public class TokenStream {

    private static class TokenNode {

        Token data;
        TokenNode next;
        TokenNode prev;

        TokenNode(Token _data) {
            data = _data;
            next = null;
            prev = null;
        }
    }

    private TokenNode head;
    private TokenNode tail;

    public TokenStream() {
        tail = new TokenNode(new Token(TokenType.BEGIN_STREAM, "NULL"));
        head = new TokenNode(new Token(TokenType.END_STREAM, "NULL"));
        head.next = tail;
        tail.prev = head;
    }

    public TokenStream enqueue(Token _token) {
        TokenNode newnode = new TokenNode(_token);
        newnode.next = head.next;
        newnode.prev = head;
        newnode.next.prev = newnode;
        head.next = newnode;
        return this;
    }

    public Token dequeue() {
        if (tail == null) return null;
        Token token = tail.data;
        if (tail == head) {
            tail = null;
        } 
        else {
            tail = tail.prev;
        }
        return token;
}

    public Token peek() {
        return (tail == null) ? null : tail.data;
    }
}
