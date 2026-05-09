/**************************************
 * NAME: Adam Gregory
 * FILE: tokenizer.cpp
 * ASGT: CMPS 3500 Group Project
 * DATE: 3/31/2026
 **************************************/

#include <iostream>
#include <string>
#include "helpers.h"
#include <sstream>
#include <vector>
#include <algorithm>
#include <cctype>

void tokenizer(std::string str, 
    std::vector<TokenType>& tokens, 
    std::vector<std::string>& values) {
    //put spaces between parenthesis
    std::string expr;
    for (char c : str) {
        if (c == '(' || c == ')') {
            expr += ' ';
            expr +=   c;
            expr += ' ';
        } else {
            expr +=   c;
        }
    }

    //set up for reading expression
    std::istringstream iss(expr);
    std::string atom;

    //while the stream reads atoms in the expression, push value and type to stack
    while (iss >> atom) {
        //add token value
        values.push_back(atom);

        //add token type
        if (atom == "(") tokens.push_back(L_PAREN);
        else if (atom == ")") tokens.push_back(R_PAREN);

        else if (atom == "#f") tokens.push_back(BOOL_FALSE);
        else if (atom == "#t") tokens.push_back(BOOL_TRUE);

        else if (atom == "+") tokens.push_back(OPERATOR_PLUS);
        else if (atom == "-") tokens.push_back(OPERATOR_MINUS);
        else if (atom == "*") tokens.push_back(OPERATOR_MULTIPLY);
        else if (atom == "/") tokens.push_back(OPERATOR_DIVIDE);
        else if (atom == "=") tokens.push_back(OPERATOR_EQUALS);
        else if (atom == "<") tokens.push_back(OPERATOR_LESS);
        else if (atom == ">") tokens.push_back(OPERATOR_GREATER);
        else if (atom == "<=") tokens.push_back(OPERATOR_LESSEQUALS);
        else if (atom == ">=") tokens.push_back(OPERATOR_GREATEREQUALS);

        else if (atom == "if") tokens.push_back(SPECIAL_IF);
        else if (atom == "cond") tokens.push_back(SPECIAL_COND);
        else if (atom == "let") tokens.push_back(SPECIAL_LET);
        else if (atom == "lambda") tokens.push_back(SPECIAL_LAMBDA);
        else if (atom == "define") tokens.push_back(SPECIAL_DEFINE);

        else if (std::isdigit(atom[0]) || 
                (atom[0] == '-' && std::isdigit(atom[1])))
            tokens.push_back(INTEGER);
        else tokens.push_back(IDENTIFIER);
    }
}