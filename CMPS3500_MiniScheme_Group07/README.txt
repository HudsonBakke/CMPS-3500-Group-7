CMPS 3500 MiniScheme - Group 07
Checkpoint 1 Submission
================================

CURRENT STATUS
--------------
This submission satisfies the Checkpoint 1 milestone.

Completed so far:
  - Required project folder structure
  - run_all.sh runner skeleton
  - Partially working MiniScheme parser/reader (functional/minischeme_reader.lisp)
    (as of right now can only parse atoms and generic expressions)
  - Fully working MiniScheme parser/reader (oop/Main.java)

In progress:
  - Procedural implementation (C++)

RUNNING THE PARSER
------------------
The working code is in both the Java and Common Lisp implementations

Requirements:
  - SBCL (Steel Bank Common Lisp) for Lisp
  - Java VM for Java

Usage:
  Functional:
    TO RUN:       sbcl --script functional/minischeme_reader.lisp tests/public/[filename].scm

  OOP:
    TO COMPILE:   javac oop/*.java
    TO RUN:       java -cp oop Main tests/public/[filename].scm



FOLDER STRUCTURE
----------------
  procedural/    C++ placeholder (in progress)
  oop/           Fully formed Java parser
  functional/    Working Lisp parser/reader
  tests/public/  Public test cases
  challenges/    Challenge case files (empty for now)
  docs/          Contribution statements
  run_all.sh     Runner skeleton
  README.txt     This file