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

enum ValueType {
    VAL_INT,
    VAL_BOOL,
    VAL_CLOSURE,
    VAL_ERROR
};

extern std::vector<NodeType> nodeType;
extern std::vector<std::string> nodeValue;
extern std::vector<std::vector<int>> children;

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
int eval(int node, int env);
void print_value(int value);
int make_env(int parent);
void env_define(int env, std::string name, int value);
int make_int(int x);

//for output
std::string value_result_string(int value);
std::string value_type_string(int value);
bool is_error_value(int value);
bool is_bool_value(int value);
std::string get_eval_error();
void set_eval_error(std::string error);
void set_parse_error();
bool has_parse_error();