#!/usr/bin/env bash
set -uo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# ---------- Build helpers ----------
build_procedural() {
    build_procedural() {
    if [ ! -f procedural/minischeme ]; then
        echo "[build] Compiling procedural (C++)..." >&2
        g++ -std=c++17 -O2 -o procedural/minischeme \
            procedural/main.cpp \
            procedural/tokenizer.cpp \
            procedural/file_loader_stub.cpp \
            procedural/parser.cpp \
            procedural/evaluator.cpp 2>&1
    fi
}
}

build_oop() {
    if [ ! -f oop/Main.class ]; then
        echo "[build] Compiling OOP (Java)..." >&2
        javac oop/*.java 2>&1
    fi
}

# ---------- Run helpers ----------
run_procedural() {
    local file="$1"
    build_procedural
    if [ -f procedural/minischeme ]; then
        ./procedural/minischeme procedural "$file"
    else
        echo "Implementation: procedural"
        echo "Case: $file"
        echo "Status: ERROR"
        echo "Error: NOT_IMPLEMENTED"
    fi
}

run_oop() {
    local file="$1"
    build_oop
    if [ -f oop/Main.class ]; then
        java -cp oop Main "$file" 2>/dev/null
    else
        echo "Implementation: oop"
        echo "Case: $file"
        echo "Status: ERROR"
        echo "Error: NOT_IMPLEMENTED"
    fi
}

run_functional() {
    local file="$1"
    if command -v sbcl &>/dev/null; then
        sbcl --script functional/minischeme_reader.lisp "$file" 2>/dev/null
    else
        echo "Implementation: functional"
        echo "Case: $file"
        echo "Status: ERROR"
        echo "Error: NOT_IMPLEMENTED"
    fi
}

# ---------- Commands ----------
cmd="${1:-}"

case "$cmd" in

  list-cases)
    echo "=== Public Test Cases ==="
    if [ -d tests/public ]; then
        find tests/public -name '*.scm' | sort
    fi
    echo ""
    echo "=== Challenge Cases ==="
    if [ -d challenges/public ]; then
        find challenges/public -name '*.scm' | sort
    fi
    ;;

  run-case)
    impl="${2:-}"
    file="${3:-}"

    if [ -z "$impl" ] || [ -z "$file" ]; then
        echo "Usage: ./run_all.sh run-case <implementation> <file>"
        exit 1
    fi
    if [ ! -f "$file" ]; then
        echo "Error: file not found: $file"
        exit 1
    fi

    case "$impl" in
      procedural) run_procedural "$file" ;;
      oop)        run_oop        "$file" ;;
      functional) run_functional "$file" ;;
      *)
        echo "Unknown implementation: $impl"
        echo "Valid options: procedural, oop, functional"
        exit 1
        ;;
    esac
    ;;

  compare-case)
    file="${2:-}"
    if [ -z "$file" ]; then
        echo "Usage: ./run_all.sh compare-case <file>"
        exit 1
    fi
    if [ ! -f "$file" ]; then
        echo "Error: file not found: $file"
        exit 1
    fi

    echo "Case: $file"
    echo ""

    format_result() {
        local label="$1"
        local output="$2"
        local status result errname type

        status=$(echo "$output"  | grep "^Status:" | awk '{print $2}')
        result=$(echo "$output"  | grep "^Result:" | awk '{$1=""; print substr($0,2)}')
        errname=$(echo "$output" | grep "^Error:"  | awk '{print $2}')
        type=$(echo "$output"    | grep "^Type:"   | awk '{print $2}')

        if [ "$status" = "OK" ]; then
            printf "%-12s OK -> %s : %s\n" "${label}:" "$result" "$type"
        else
            printf "%-12s ERROR -> %s\n" "${label}:" "$errname"
        fi
    }

    proc_out=$(run_procedural "$file" 2>/dev/null)
    oop_out=$(run_oop         "$file" 2>/dev/null)
    func_out=$(run_functional  "$file" 2>/dev/null)

    format_result "procedural" "$proc_out"
    format_result "oop"        "$oop_out"
    format_result "functional" "$func_out"
    ;;

  *)
    echo "MiniScheme Comparative Runner"
    echo "Usage:"
    echo "  ./run_all.sh list-cases"
    echo "  ./run_all.sh run-case <implementation> <file>"
    echo "  ./run_all.sh compare-case <file>"
    echo ""
    echo "Implementations: procedural, oop, functional"
    exit 1
    ;;
esac