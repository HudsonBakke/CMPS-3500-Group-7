/**************************************
 * NAME: Hudson Bakke
 * FILE: Token.java
 * ASGT: CMPS 3500 Group Project
 * DATE: 3/27/2026
 **************************************/

/// Holds token data
public class Token {
    
    public final TokenType type;
    public final String val;

    public Token(TokenType _type, String _val) {
        type = _type;
        val = _val;
    }
}
