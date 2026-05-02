/**************************************
 * NAME: Adam Gregory
 * FILE: evaluator.cpp
 * ASGT: CMPS 3500 Group Project
 * DATE: 4/30/2026
 **************************************/

#include <iostream>
#include <vector>
#include <string>
#include <cstdlib>
#include "helpers.h"

// value arrays
std::vector<ValueType> valueType;
std::vector<int> valueInt;
std::vector<bool> valueBool;

// support env for let
std::vector<int> envParent;
std::vector<std::vector<std::string>> envNames;
std::vector<std::vector<int>> envValues;

// support closure
std::vector<int> closureParams;
std::vector<int> closureBody;
std::vector<int> closureEnv;

int make_int(int x) {
    valueType.push_back(VAL_INT);
    valueInt.push_back(x);
    valueBool.push_back(false);

    closureParams.push_back(-1);
    closureBody.push_back(-1);
    closureEnv.push_back(-1);

    return valueType.size() - 1;
}

int make_bool(bool b) {
    valueType.push_back(VAL_BOOL);
    valueInt.push_back(0);
    valueBool.push_back(b);

    closureParams.push_back(-1);
    closureBody.push_back(-1);
    closureEnv.push_back(-1);

    return valueType.size() - 1;
}

int make_error() {
    valueType.push_back(VAL_ERROR);
    valueInt.push_back(0);
    valueBool.push_back(false);

    closureParams.push_back(-1);
    closureBody.push_back(-1);
    closureEnv.push_back(-1);

    return valueType.size() - 1;
}

int make_closure(int params, int body, int env) {
    valueType.push_back(VAL_CLOSURE);
    valueInt.push_back(0);
    valueBool.push_back(false);

    closureParams.push_back(params);
    closureBody.push_back(body);
    closureEnv.push_back(env);

    return valueType.size() - 1;
}

int make_env(int parent) {
    envParent.push_back(parent);
    envNames.push_back(std::vector<std::string>());
    envValues.push_back(std::vector<int>());

    return envParent.size() - 1;
}

void env_define(int env, std::string name, int value) {
    envNames[env].push_back(name);
    envValues[env].push_back(value);
}

void env_set(int env, std::string name, int value) {
    for (int i = 0; i < envNames[env].size(); i++) {
        if (envNames[env][i] == name) {
            envValues[env][i] = value;
            return;
        }
    }

    env_define(env, name, value);
}

int env_lookup(int env, std::string name) {
    int current = env;

    while (current != -1) {
        for (int i = 0; i < envNames[current].size(); i++) {
            if (envNames[current][i] == name) {
                return envValues[current][i];
            }
        }

        current = envParent[current];
    }

    std::cout << "Evaluation error: undefined variable " << name << "\n";
    return make_error();
}

bool is_integer_string(std::string s) {
    if (s.size() == 0) return false;

    int start = 0;
    
    if (s[0] == '-') start = 1;

    if (start >= s.size()) return false;

    for (int i = start; i < s.size(); i++) {
        if (!isdigit(s[i])) {
            return false;
        }
    }

    return true;
}

// solve simple arithmetic expressions
int eval_arithmetic(std::string op, int node, int env) {
    if (children[node].size() < 3) {
        std::cout << "Evaluation error: needs two operands\n";
        return make_error();
    }

    int first = eval(children[node][1], env);

    if (valueType[first] != VAL_INT) {
        std::cout << "Evalulation error: operand is not an integer\n";
        return make_error();
    }

    int result = valueInt[first];

    for (int i = 2; i < children[node].size(); i++) {
        int next = eval(children[node][i], env);

        if (valueType[next] != VAL_INT) {
            std::cout << "Evaluation error: operand is not an integer\n";
            return make_error();
        }

        if (op == "+") {
            result += valueInt[next];
        } else if (op == "-") {
            result -= valueInt[next];
        } else if (op == "*") {
            result *= valueInt[next];
        } else if (op == "/") {
            result /= valueInt[next];
        }
    }

    return make_int(result);
}

// solve comparison operators
int eval_comparison(std::string op, int node, int env) {
    if (children[node].size() != 3) {
        std::cout << "Evaluation error: comparison needs exactly two operands\n";
        return make_error();
    }

    int left = eval(children[node][1], env);
    int right = eval(children[node][2], env);

    if (valueType[left] != VAL_INT || valueType[right] != VAL_INT) {
        std::cout << "Evaluation error: comparison operands must be integers\n";
        return make_error();
    }

    int a = valueInt[left];
    int b = valueInt[right];

    if (op == "=") {
        return make_bool(a == b);
    } else if (op == "<") {
        return make_bool(a < b);
    } else if (op == ">") {
        return make_bool(a > b);
    } else if (op == "<=") {
        return make_bool(a <= b);
    } else if (op == ">=") {
        return make_bool(a >= b);
    }

    std::cout << "Evaluation error: unknown comparison operator " << op << "\n";
    return make_error();
}

// helper for solving if expressions
bool is_true_value(int value) {
    if (valueType[value] == VAL_BOOL && valueBool[value] == false) {
        return false;
    }
    return true;
}

// solve if expressions
int eval_if(int node, int env) {
    if (children[node].size() != 4) {
        std::cout << "Evaluation error: if statement must have test, then, else\n";
        return make_error();
    }

    int condition = eval(children[node][1], env);

    if (is_true_value(condition)) {
        return eval(children[node][2], env);
    } else {
        return eval(children[node][3], env);
    }
}

// solve let expressions
int eval_let(int node, int env) {
    if (children[node].size() < 3) {
        std::cout << "Evaluation error: let needs bindings and body\n";
        return make_error();
    }

    int bindingsNode = children[node][1];

    if (nodeType[bindingsNode] != NODE_LIST) {
        std::cout << "Evaluation error: let bindings must be a list\n";
        return make_error();
    }

    int letEnv = make_env(env);

    for (int i = 0; i < children[bindingsNode].size(); i++) {
        int binding = children[bindingsNode][i];

        if (nodeType[binding] != NODE_LIST || children[binding].size() != 2) {
            std::cout << "Evaluation error: bad let binding\n";
            return make_error();
        }

        int nameNode = children[binding][0];
        int valueNode = children[binding][1];

        if (nodeType[nameNode] != NODE_ATOM) {
            std::cout << "Evaluation error: let binding name must be an atom\n";
            return make_error();
        }

        std::string name = nodeValue[nameNode];

        // evaluate using old env
        int value = eval(valueNode, env);

        env_define(letEnv, name, value);
    }

    int result = make_error();

    for (int i = 2; i < children[node].size(); i++) {
        result = eval(children[node][i], letEnv);
    }

    return result;
}

// lambda expressions
int eval_lambda(int node, int env) {
    if (children[node].size() < 3) {
        std::cout << "Evaluation error: lambda needs parameters and body\n";
        return make_error();
    }

    int paramsNode = children[node][1];
    //int bodyNode = children[node][2];

    if (nodeType[paramsNode] != NODE_LIST) {
        std::cout << "Evaluation error: lambda parameters must be a list\n";
        return make_error();
    }

    // changed bodyNode to use the whole node
    return make_closure(paramsNode, node, env);
}

// finish lambda
int apply_closure(int functionValue, int callNode, int env) {
    int paramsNode = closureParams[functionValue];
    //int bodyNode = closureBody[functionValue];
    int lambdaNode = closureBody[functionValue];
    int savedEnv = closureEnv[functionValue];

    int argCount = children[callNode].size() - 1;
    int paramCount = children[paramsNode].size();

    if (argCount != paramCount) {
        std::cout << "Evaluation error: wrong number of arguments\n";
        return make_error();
    }

    int callEnv = make_env(savedEnv);

    for (int i = 0; i < paramCount; i++) {
        std::string paramName = nodeValue[children[paramsNode][i]];

        int argValue = eval(children[callNode][i + 1], env);

        env_define(callEnv, paramName, argValue);
    }

    int result = make_error();

    for (int i = 2; i < children[lambdaNode].size(); i++) {
        result = eval(children[lambdaNode][i], callEnv);
    }

    return result;
}

int eval_cond(int node, int env) {
    if (children[node].size() < 2) {
        std::cout << "Evaluation error: cond needs at least one clause\n";
        return make_error();
    }

    for (int i = 1; i < children[node].size(); i++) {
        int clause = children[node][i];

        if (nodeType[clause] != NODE_LIST || children[clause].size() != 2) {
            std::cout << "Evaluation error: bad cond clause\n";
            return make_error();
        }

        int testNode = children[clause][0];
        int resultNode = children[clause][1];

        // else clause
        if (nodeType[testNode] == NODE_ATOM && nodeValue[testNode] == "else") {
            return eval(resultNode, env);
        }

        int testValue = eval(testNode, env);

        if (is_true_value(testValue)) {
            return eval(resultNode,env);
        }
    }

    std::cout << "Evaluation error: no cond condition was true\n";
    return make_error();
}

// solve define
int eval_define(int node, int env) {
    if (children[node].size() != 3) {
        std::cout << "Evaluation error: define needs a name and value\n";
        return make_error();
    }

    int nameNode = children[node][1];
    int valueNode = children[node][2];

    if (nodeType[nameNode] != NODE_ATOM) {
        std::cout << "Evaluation error: define name must be an identifier\n";
        return make_error();
    }

    std::string name = nodeValue[nameNode];

    int placeholder = make_error();
    env_define(env, name, placeholder);
    
    int value = eval(valueNode, env);

    env_set(env, name, value);

    return value;
}

int eval(int node, int env) {
    if (nodeType[node] == NODE_ATOM) {
        std::string atom = nodeValue[node];

        if (is_integer_string(atom)) {
            return make_int(std::stoi(atom));
        }

        if (atom == "#t") {
            return make_bool(true);
        }

        if (atom == "#f") {
            return make_bool(false);
        }

        return env_lookup(env, atom);
    }

    if (nodeType[node] == NODE_LIST) {
        if (children[node].size() == 0) {
            std::cout << "Evaluation error: empty list\n";
            return make_error();
        }

        int first = children[node][0];

        if (nodeType[first] == NODE_ATOM) {
            std::string op = nodeValue[first];

            if (op == "+" || op == "-" || op == "*" || op == "/") {
                return eval_arithmetic(op, node, env);
            }

            if (op == "=" || op == "<" || op == ">" || op == "<=" || op == ">=") {
                return eval_comparison(op, node, env);
            }

            if (op == "if") {
                return eval_if(node, env);
            }

            if (op == "let") {
                return eval_let(node, env);
            }

            if (op == "lambda") {
                return eval_lambda(node, env);
            }

            if (op == "cond") {
                return eval_cond(node, env);
            }

            if (op == "define") {
                return eval_define(node, env);
            }
        }

        int functionValue = eval(first, env);

        if (valueType[functionValue] != VAL_CLOSURE) {
            std::cout << "Evaluation error: first item is not a function\n";
            return make_error();
        }

        return apply_closure(functionValue, node, env);
    }

    std::cout << "Evaluation error: unknown node type\n";
    return make_error();
}

void print_value(int value) {
    if (valueType[value] == VAL_INT) {
        std::cout << valueInt[value] << "\n";
    } else if (valueType[value] == VAL_BOOL) {
        if (valueBool[value]) {
            std::cout << "#t\n";
        } else {
            std::cout << "#f\n";
        }
    } else if (valueType[value] == VAL_CLOSURE) {
        std::cout << "<closure>\n";
    }  else {
        std::cout << "error\n";
    }
}