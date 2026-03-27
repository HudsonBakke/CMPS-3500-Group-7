/**************************************
 * NAME: Hudson Bakke
 * FILE: Main.java
 * ASGT: CMPS 3500 Group Project
 * DATE: 3/27/2026
 **************************************/

import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        // TO COMPILE:
        // javac oop\*.java
        // TO RUN:
        // java -cp oop Main [FILEPATH]
        String in_file_path = args[0];

        try {
            TokenStream tokens = Tokenizer.Tokenize(in_file_path);

            AST ast = new AST();

            try {
                ast.root = (Expr.RootExpr) Parser.ParseExp(tokens);
            }
            catch (ParserException e) {
                System.out.println("Parser error");
            }

            ast.show();
        }
        catch (IOException e) {
            System.out.println("Error: could not open file");
        }
    }
}