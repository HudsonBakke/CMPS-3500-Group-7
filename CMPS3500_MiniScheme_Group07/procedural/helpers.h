/**************************************
 * NAME: Adam Gregory
 * FILE: helpers.h
 * ASGT: CMPS 3500 Group Project
 * DATE: 3/31/2026
 **************************************/

#include <string>
#include <vector>

enum TokenType {
    L_PAREN,
    R_PAREN,

    IDENTIFIER,
    INTEGER,
    BOOL_TRUE,
    BOOL_FALSE,

    OPERATOR_PLUS,
    OPERATOR_MINUS,
    OPERATOR_MULTIPLY,
    OPERATOR_DIVIDE,
    OPERATOR_EQUALS,
    OPERATOR_LESS,
    OPERATOR_GREATER,
    OPERATOR_LESSEQUALS,
    OPERATOR_GREATEREQUALS,

    SPECIAL_IF,
    SPECIAL_COND,
    SPECIAL_LET,
    SPECIAL_LAMBDA,
    SPECIAL_DEFINE
};

enum NodeType {
    NODE_ATOM,
    NODE_LIST
};

std::string read_file(const std::string &path);
void tokenizer(
    std::string str, std::vector<TokenType> &tokens, 
    std::vector<std::string> &values
);
int parse_expression(
    const std::vector<TokenType> &tokens,
    const std::vector<std::string> &values,
    int &pos
);
void print_ast(int node, int depth = 0);