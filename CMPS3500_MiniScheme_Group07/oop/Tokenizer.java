import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Tokenizer {

    private Tokenizer() {}

    public static TokenStream Tokenize(String _inFilePath) throws IOException {

        TokenStream stream = new TokenStream();
        String content = Files.readString(Path.of(_inFilePath));
        content = content.replace("(", " ( ")
                         .replace(")", " ) ");
        String[] tokens = content.trim().split("\\s+");

        for (String token : tokens) {

            switch (token) {

                case "("      -> stream.enqueue(new Token(TokenType.L_PAREN, token));
                case ")"      -> stream.enqueue(new Token(TokenType.R_PAREN, token));

                case "#t"     -> stream.enqueue(new Token(TokenType.BOOL_TRUE, token));
                case "#f"     -> stream.enqueue(new Token(TokenType.BOOL_FALSE, token));

                case "+"      -> stream.enqueue(new Token(TokenType.OPERATOR_PLUS, token));
                case "-"      -> stream.enqueue(new Token(TokenType.OPERATOR_MINUS, token));
                case "*"      -> stream.enqueue(new Token(TokenType.OPERATOR_MULTIPLY, token));
                case "/"      -> stream.enqueue(new Token(TokenType.OPERATOR_DIVIDE, token));
                case "="      -> stream.enqueue(new Token(TokenType.OPERATOR_EQUALS, token));
                case "<"      -> stream.enqueue(new Token(TokenType.OPERATOR_LESS, token));
                case ">"      -> stream.enqueue(new Token(TokenType.OPERATOR_GREATER, token));
                case "<="     -> stream.enqueue(new Token(TokenType.OPERATOR_LESSEQUALS, token));
                case ">="     -> stream.enqueue(new Token(TokenType.OPERATOR_GREATEREQUALS, token));

                case "if"     -> stream.enqueue(new Token(TokenType.SPECIAL_IF, token));
                case "cond"   -> stream.enqueue(new Token(TokenType.SPECIAL_COND, token));
                case "let"    -> stream.enqueue(new Token(TokenType.SPECIAL_LET, token));
                case "lambda" -> stream.enqueue(new Token(TokenType.SPECIAL_LAMBDA, token));
                case "define" -> stream.enqueue(new Token(TokenType.SPECIAL_DEFINE, token));

                default       -> stream.enqueue(new Token(
                    (Character.isDigit(token.charAt(0)) ? TokenType.INTEGER : TokenType.IDENTIFIER), token));
            }
        }

        return stream;
    }
}