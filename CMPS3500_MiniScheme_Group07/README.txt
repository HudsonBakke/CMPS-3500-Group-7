CMPS 3500 MiniScheme - Group 07
Checkpoint 1 Submission
================================

CURRENT STATUS
--------------
This submission satisfies the Checkpoint 1 milestone.

Completed so far:
  - Required project folder structure
  - run_all.sh runner skeleton
  - Working MiniScheme parser/reader (functional/minischeme_reader.lisp)

In progress:
  - Procedural implementation (C++)
  - OOP implementation (Java)

RUNNING THE PARSER
------------------
The working parser is the Common Lisp implementation.

Requirements:
  - SBCL (Steel Bank Common Lisp)

Usage:
  sbcl --script functional/minischeme_reader.lisp tests/public/core_01.scm

FOLDER STRUCTURE
----------------
  procedural/    C++ placeholder (in progress)
  oop/           Java placeholder (in progress)
  functional/    Working Lisp parser/reader
  tests/public/  Public test cases
  challenges/    Challenge case files (empty for now)
  docs/          Contribution statements
  run_all.sh     Runner skeleton
  README.txt     This file