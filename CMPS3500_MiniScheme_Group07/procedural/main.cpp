/**************************************
 * NAME:
 * MODIFIED BY: Adam Gregory
 * FILE: main.cpp
 * ASGT: CMPS 3500 Group Project
 * DATE: 3/31/2026
 **************************************/

#include <iostream>
#include <string>
#include "helpers.h"
#include "vector"

//print token type
std::string token_type_tostring(TokenType type) {
    switch (type) {
        case L_PAREN: return "L_PAREN";
        case R_PAREN: return "R_PAREN";

        case IDENTIFIER: return "IDENTIFIER";
        case INTEGER: return "INTEGER";
        case BOOL_TRUE: return "BOOL_TRUE";
        case BOOL_FALSE: return "BOOL_FALSE";

        case OPERATOR_PLUS: return "OPERATOR_PLUS";
        case OPERATOR_MINUS: return "OPERATOR_MINUS";
        case OPERATOR_MULTIPLY: return "OPERATOR_MULTIPLY";
        case OPERATOR_DIVIDE: return "OPERATOR_DIVIDE";
        case OPERATOR_EQUALS: return "OPERATOR_EQUALS";
        case OPERATOR_LESS: return "OPERATOR_LESS";
        case OPERATOR_GREATER: return "OPERATOR_GREATER";
        case OPERATOR_LESSEQUALS: return "OPERATOR_LESSEQUALS";
        case OPERATOR_GREATEREQUALS: return "OPERATOR_GREATEREQUALS";

        case SPECIAL_IF: return "SPECIAL_IF";
        case SPECIAL_COND: return "SPECIAL_COND";
        case SPECIAL_LET: return "SPECIAL_LET";
        case SPECIAL_LAMBDA: return "SPECIAL_LAMBDA";
        case SPECIAL_DEFINE: return "SPECIAL_DEFINE";

        default: return "UNKNOWN";
    }
}

int main() {
    //path to input
    const std::string path = "../tests/public/core_04.scm";

    //read file and print expression
    std::string expression = read_file(path);
    std::cout << expression << std::endl;

    //tokenize expression
    std::vector<TokenType> tokens;
    std::vector<std::string> values;
    tokenizer(expression, tokens, values);

    //print tokens
    for (int i=0; i<tokens.size(); i++) {
        std::cout << token_type_tostring(tokens[i]) << " : " << values[i] << std::endl;
    }

    return 0;
}