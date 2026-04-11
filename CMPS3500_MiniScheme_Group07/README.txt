CMPS 3500 MiniScheme - Group 07
Checkpoint 2 Submission
================================

CURRENT STATUS
--------------
This submission satisfies the Checkpoint 2 milestone.

The fully functional implementation is the OOP (Java) version.
The functional (Lisp) implementation supports most core features.
The procedural (C++) implementation has a working tokenizer and is in progress.

FULLY FUNCTIONAL IMPLEMENTATION
--------------------------------
OOP - Java (oop/)

  Working features:
    - Integer and boolean literals
    - Arithmetic: +, -, *, /
    - Comparisons: =, <, >, <=, >=
    - if expressions
    - let bindings with lexical scope
    - lambda / anonymous functions
    - Function application
    - Lexical closures
    - define (top-level bindings)
    - Recursion via define
    - Variable shadowing
    - Higher-order functions
    - Multiple top-level expressions per file

  Error categories handled:
    - PARSE_ERROR
    - UNDECLARED_IDENTIFIER
    - WRONG_ARITY
    - TYPE_MISMATCH
    - DIVISION_BY_ZERO

  Known limitations:
    - cond is stubbed but not yet evaluated
    - Negative integer literals not tokenized correctly

  Files:
    Main.java             Entry point and output formatter
    Parser.java           Recursive-descent parser
    Interpreter.java      Evaluator and environment model
    Expr.java             AST node hierarchy
    AST.java              AST wrapper and debug printer
    Tokenizer.java        File reader and tokenizer
    Token.java            Token data holder
    TokenType.java        Token type enum
    TokenStream.java      Queue-like token stream
    ParserException.java  Parser error types
    InterpreterException.java  Interpreter error types

  To compile:
    javac oop/*.java

  To run:
    java -cp oop Main tests/public/core_01.scm


CORE EVALUATOR IN PROGRESS
----------------------------
Functional - Common Lisp (functional/)

  Working features:
    - Integer and boolean literals (including negative integers)
    - Arithmetic: +, -, *, /
    - Comparisons: =, <, >, <=, >=
    - if expressions
    - let bindings with lexical scope
    - lambda / anonymous functions
    - Function application
    - Lexical closures

  Missing / not yet implemented:
    - define
    - Recursion via define
    - cond
    - Multi-expression files

  Error categories handled:
    - PARSE_ERROR
    - UNDECLARED_IDENTIFIER
    - WRONG_ARITY
    - TYPE_MISMATCH
    - DIVISION_BY_ZERO

  To run:
    sbcl --script functional/minischeme_reader.lisp tests/public/core_01.scm


TOKENIZER COMPLETE - PARSER IN PROGRESS
-----------------------------------------
Procedural - C++ (procedural/)

  Working features:
    - File loading
    - Tokenization of all MiniScheme token types:
        parentheses, identifiers, integers (including negative),
        booleans, operators, and keywords

  Missing / not yet implemented:
    - Parser / AST
    - Evaluator
    - Environment and scope
    - Error reporting

  Files:
    tokenizer.cpp         Tokenizer implementation
    helpers.h             Token enum and function declarations
    file_loader_stub.cpp  File reader helper
    main.cpp              Tokenizer demo (in progress)


RUNNER
------
run_all.sh now calls each implementation directly.

  To list all test cases:
    ./run_all.sh list-cases

  To run one implementation on one file:
    ./run_all.sh run-case oop tests/public/core_01.scm
    ./run_all.sh run-case functional tests/public/core_01.scm
    ./run_all.sh run-case procedural tests/public/core_01.scm

  To compare all three on one file:
    ./run_all.sh compare-case tests/public/core_01.scm

  Dependencies:
    - Java JDK 11+ (for OOP)
    - SBCL (for Functional)
    - g++ with C++17 (for Procedural)


FOLDER STRUCTURE
----------------
  procedural/    C++ tokenizer (parser in progress)
  oop/           Fully functional Java interpreter
  functional/    Common Lisp evaluator (define/cond in progress)
  tests/public/  Public test cases
  challenges/    Challenge case files
  docs/          Contribution statements
  run_all.sh     Command-driven runner
  README.txt     This file