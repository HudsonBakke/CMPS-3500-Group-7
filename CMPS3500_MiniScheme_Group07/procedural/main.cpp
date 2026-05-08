/**************************************
 * NAME:
 * MODIFIED BY: Adam Gregory
 * FILE: main.cpp
 * ASGT: CMPS 3500 Group Project
 * DATE: 3/31/2026
 **************************************/

#include <iostream>
#include <sstream>
#include <string>
#include <vector>
#include "helpers.h"

extern std::vector<ValueType> valueType;
extern std::vector<int> valueInt;
extern std::vector<bool> valueBool;

struct StdoutCapture {
    std::streambuf* orig;
    std::ostringstream buf;
    StdoutCapture() { orig = std::cout.rdbuf(buf.rdbuf()); }
    std::string release() { std::cout.rdbuf(orig); return buf.str(); }
};

std::string map_error(const std::string& msg) {
    if (msg.find("undefined variable") != std::string::npos)
        return "UNDECLARED_IDENTIFIER";
    if (msg.find("wrong number of arguments") != std::string::npos)
        return "WRONG_ARITY";
    if (msg.find("operand is not an integer") != std::string::npos ||
        msg.find("comparison operands must be integers") != std::string::npos ||
        msg.find("first item is not a function") != std::string::npos)
        return "TYPE_MISMATCH";
    if (msg.find("DIVISION_BY_ZERO") != std::string::npos)
        return "DIVISION_BY_ZERO";
    // any parse-related message
    if (msg.find("Parse error") != std::string::npos ||
        msg.find("parse error") != std::string::npos ||
        msg.find("Parsing error") != std::string::npos ||
        msg.find("missing closing") != std::string::npos ||
        msg.find("unexpected") != std::string::npos ||
        msg.find("needs bindings") != std::string::npos ||
        msg.find("needs parameters") != std::string::npos ||
        msg.find("must be a list") != std::string::npos ||
        msg.find("bad let binding") != std::string::npos ||
        msg.find("bad cond clause") != std::string::npos ||
        msg.find("if statement must") != std::string::npos ||
        msg.find("needs two operands") != std::string::npos ||
        msg.find("define needs") != std::string::npos)
        return "PARSE_ERROR";
    return "PARSE_ERROR";
}

int main(int argc, char* argv[]) {
    if (argc < 2) {
        std::cerr << "Usage: ./minischeme <file>\n";
        return 1;
    }

    std::string path = argv[argc - 1];
    std::string expression = read_file(path);

    if (expression.empty()) {
        std::cout << "Implementation: procedural\n";
        std::cout << "Case: " << path << "\n";
        std::cout << "Status: ERROR\n";
        std::cout << "Error: PARSE_ERROR\n";
        return 1;
    }

    std::vector<TokenType> tokens;
    std::vector<std::string> values;
    tokenizer(expression, tokens, values);

    int pos = 0;
    int globalEnv = make_env(-1);
    int lastResult = -1;
    bool hadError = false;
    std::string errorCategory = "EVAL_ERROR";

    while (pos < (int)tokens.size()) {
        StdoutCapture cap;
        int root = parse_expression(tokens, values, pos);
        std::string captured = cap.release();

        // parser returned -1 = parse error
        if (root == -1) {
            hadError = true;
            errorCategory = "PARSE_ERROR";
            break;
        }

        StdoutCapture cap2;
        int result = eval(root, globalEnv);
        captured += cap2.release();

        if (valueType[result] == VAL_ERROR) {
            hadError = true;
            errorCategory = map_error(captured);
            break;
        }

        lastResult = result;
    }

    std::cout << "Implementation: procedural\n";
    std::cout << "Case: " << path << "\n";

    if (hadError || lastResult == -1) {
        std::cout << "Status: ERROR\n";
        std::cout << "Error: " << errorCategory << "\n";
        return 1;
    }

    std::cout << "Status: OK\n";

    if (valueType[lastResult] == VAL_INT) {
        std::cout << "Result: " << valueInt[lastResult] << "\n";
        std::cout << "Type: int\n";
    } else if (valueType[lastResult] == VAL_BOOL) {
        std::cout << "Result: " << (valueBool[lastResult] ? "#t" : "#f") << "\n";
        std::cout << "Type: bool\n";
    } else if (valueType[lastResult] == VAL_CLOSURE) {
        std::cout << "Result: <function>\n";
        std::cout << "Type: function\n";
    } else {
        std::cout << "Status: ERROR\n";
        std::cout << "Error: EVAL_ERROR\n";
        return 1;
    }

    return 0;
}