import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String in_file_path = args[0];

        try {
            TokenStream tokens = Tokenizer.Tokenize(in_file_path);

            //Stub code
            Token token = tokens.dequeue();
            while (token != null) {
                System.out.println(token.type);
                System.out.println(token.val);
                token = tokens.dequeue();
            }
        }
        catch (IOException e) {
            System.out.println("Error: could not open file");
        }
    }
}