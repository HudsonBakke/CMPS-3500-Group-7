/**************************************
 * NAME: Hudson Bakke
 * FILE: Main.java
 * ASGT: CMPS 3500 Group Project
 * DATE: 3/27/2026
 **************************************/

import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        // TO COMPILE:
        // javac oop\*.java
        // TO RUN:
        // java -cp oop Main [FILEPATH]
        String[] in_file_paths = args;

        // I am allowing for multiple files to be evaluated at once for ease of testing
        for (String in_file_path : in_file_paths) {
            String status = "OK";
            String result = "";
            String type = "";
            String error = "";
            try {
                TokenStream tokens = Tokenizer.Tokenize(in_file_path);

                AST ast = new AST();

                try {
                    ast.root = (Expr.RootExpr) Parser.ParseExp(tokens);
                }
                catch (ParserException e) {
                    status = "ERROR";
                    error = "PARSE_ERROR";
                }

                if (status.equals("OK")) {
                    try {
                        Expr expr_result = Interpreter.Interpret((Expr)ast.root, new ArrayList<>());
                        if (expr_result instanceof Expr.IntExpr int_expr) {
                            result = Integer.toString(int_expr.value);
                            type = "int";
                        }
                        else if (expr_result instanceof Expr.BoolExpr bool_expr) {
                            result = bool_expr.value
                                ? "#t"
                                : "#f";
                            type = "bool";
                        }
                    }
                    catch (ParserException e) {
                        status = "ERROR";
                        error = "PARSE_ERROR";
                    }
                    catch (InterpreterException e) {
                        status = "ERROR";
                        error = e.getMessage();
                    }
                }
                System.out.println("Status: " + status);
                if (status.equals("OK")) {
                    System.out.println("Result: " + result);
                    System.out.println("Type: " + type);
                }
                else System.out.println("Error: " + error);
            }
            catch (IOException e) {
                System.out.println("Error: could not open file");
            }
        }
    }
}
