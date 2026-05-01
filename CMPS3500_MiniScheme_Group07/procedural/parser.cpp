/**************************************
 * NAME: Adam Gregory
 * FILE: parser.cpp
 * ASGT: CMPS 3500 Group Project
 * DATE: 4/30/2026
 **************************************/

#include <string>
#include "helpers.h"
#include <vector>
#include <iostream>

std::vector<NodeType> nodeType;
std::vector<std::string> nodeValue;
std::vector<std::vector<int>> children;

//nodes for processing
int makeNode(NodeType type, std::string value = "") {
    nodeType.push_back(type);
    nodeValue.push_back(value);
    children.push_back(std::vector<int>());

    return nodeType.size() - 1;
}

int parse_expression(
    const std::vector<TokenType> &tokens,
    const std::vector<std::string> &values,
    int &pos
) {
    // if position is indexing out of bounds, stop
    if (pos >= tokens.size()) {
        std::cout << "Parsing error: unexpected end of input\n";
        return -1;
    }

    // left parenthesis marks start
    if (tokens[pos] == L_PAREN) {
        // start node list
        int listNode = makeNode(NODE_LIST, "(");
        pos++;

        // while there are atoms, get child until we can't
        while (pos < tokens.size() && tokens[pos] != R_PAREN) {
            int child = parse_expression(tokens, values, pos);
            if (child != -1) {
                children[listNode].push_back(child);
            }
        }

        //make sure root expression closes
        if (pos >= tokens.size()) {
            std::cout << "Parse error: missing closing parenthesis\n";
            return -1;
        }

        // return listNode after root expression is closed
        pos++;
        return listNode;
    }

    //right parenthesis without a left
    if (tokens[pos] == R_PAREN) {
        std::cout << "Parse error: unexpected ')'\n";
        return -1;
    }

    int atomNode = makeNode(NODE_ATOM, values[pos]);
    pos++;
    return atomNode;
}

//helper for output
void print_ast(int node, int depth) {
    for (int i = 0; i < depth; i++) {
        //indentation for depth
        std::cout << " ";
    }

    if (nodeType[node] == NODE_ATOM) {
        std::cout << nodeValue[node] << "\n";
    } else {
        std::cout << "LIST\n";
        for (int i = 0; i < children[node].size(); i++) {
            print_ast(children[node][i], depth + 1);
        }
    }
}