;;; minischeme_reader.lisp
;;; Checkpoint 3 Functional MiniScheme Interpreter

(defun read-file-as-string (path)
  (with-open-file (in path :direction :input)
    (let ((contents (make-string (file-length in))))
      (read-sequence contents in)
      contents)))

(defun tokenize (input)
  (labels ((flush-token (token tokens)
             (if (> (length token) 0)
                 (cons token tokens)
                 tokens)))
    (let ((tokens '())
          (current ""))
      (loop for ch across input do
        (cond
          ((or (char= ch #\Space)
               (char= ch #\Newline)
               (char= ch #\Tab)
               (char= ch #\Return))
           (setf tokens (flush-token current tokens))
           (setf current ""))

          ((or (char= ch #\()
               (char= ch #\)))
           (setf tokens (flush-token current tokens))
           (setf current "")
           (push (string ch) tokens))

          (t
           (setf current (concatenate 'string current (string ch))))))

      (setf tokens (flush-token current tokens))
      (reverse tokens))))

(defun atom-from-token (token)
  (cond
    ((string= token "#t") t)
    ((string= token "#f") nil)

    ((every #'digit-char-p token)
     (parse-integer token))

    ((and (> (length token) 1)
          (char= (char token 0) #\-)
          (every #'digit-char-p (subseq token 1)))
     (parse-integer token))

    (t (intern (string-upcase token)))))

(defun parse-expression (tokens)
  (when (null tokens)
    (error "PARSE_ERROR"))

  (let ((token (car tokens)))
    (cond
      ((string= token "(")
       (parse-list (cdr tokens) '()))

      ((string= token ")")
       (error "PARSE_ERROR"))

      (t
       (values (atom-from-token token) (cdr tokens))))))

(defun parse-list (tokens acc)
  (when (null tokens)
    (error "PARSE_ERROR"))

  (let ((token (car tokens)))
    (cond
      ((string= token ")")
       (values (reverse acc) (cdr tokens)))

      (t
       (multiple-value-bind (expr rest) (parse-expression tokens)
         (parse-list rest (cons expr acc)))))))  ; <-- fixed: was missing one closing paren

(defun parse-program (tokens)
  (let ((exprs '()))
    (loop while tokens do
      (multiple-value-bind (expr rest) (parse-expression tokens)
        (push expr exprs)
        (setf tokens rest)))
    (reverse exprs)))

;;;; =========================
;;;; Environment
;;;; =========================

(defun make-cell (value)
  (cons value nil))

(defun cell-value (cell)
  (car cell))

(defun set-cell-value (cell value)
  (setf (car cell) value))

(defun env-lookup-cell (name env)
  (let ((binding (assoc name env)))
    (if binding
        (cdr binding)
        (error "UNDECLARED_IDENTIFIER"))))

(defun env-lookup (name env)
  (cell-value (env-lookup-cell name env)))

(defun env-extend (vars vals env)
  (append
   (mapcar (lambda (var val)
             (cons var (make-cell val)))
           vars
           vals)
   env))

(defun env-extend-with-cells (vars cells env)
  (append (mapcar #'cons vars cells) env))

;;;; =========================
;;;; Values / Closures
;;;; =========================

(defun bool-value-p (x)
  (or (eq x t) (eq x nil)))

(defun make-closure (params body-forms env)
  (list 'CLOSURE params body-forms env))

(defun closure-p (value)
  (and (listp value)
       (>= (length value) 4)
       (eq (first value) 'CLOSURE)))

(defun closure-params (closure)
  (second closure))

(defun closure-body-forms (closure)
  (third closure))

(defun closure-env (closure)
  (fourth closure))

(defun type-name (value)
  (cond
    ((integerp value) "int")
    ((bool-value-p value) "bool")
    ((closure-p value) "function")
    (t "unknown")))

(defun print-value (value)
  (cond
    ((eq value t) "#t")
    ((eq value nil) "#f")
    ((closure-p value) "<function>")
    (t value)))

;;;; =========================
;;;; Evaluator Helpers
;;;; =========================

(defun ensure-integer (value)
  (if (integerp value)
      value
      (error "TYPE_MISMATCH")))

(defun eval-args (args env)
  (mapcar (lambda (arg) (evaluate arg env)) args))

(defun eval-sequence (forms env)
  (if (null forms)
      (error "PARSE_ERROR")
      (let ((result nil))
        (dolist (form forms result)
          (setf result (evaluate form env))))))

(defun eval-arithmetic (op args env)
  (if (/= (length args) 2)
      (error "WRONG_ARITY")
      (let ((left (ensure-integer (evaluate (first args) env)))
            (right (ensure-integer (evaluate (second args) env))))
        (case op
          (+ (+ left right))
          (- (- left right))
          (* (* left right))
          (/ (if (= right 0)
                 (error "DIVISION_BY_ZERO")
                 (truncate left right)))
          (otherwise (error "EVAL_ERROR"))))))

(defun eval-comparison (op args env)
  (if (/= (length args) 2)
      (error "WRONG_ARITY")
      (let ((left (ensure-integer (evaluate (first args) env)))
            (right (ensure-integer (evaluate (second args) env))))
        (case op
          (= (= left right))
          (< (< left right))
          (> (> left right))
          (<= (<= left right))
          (>= (>= left right))
          (otherwise (error "EVAL_ERROR"))))))

(defun eval-if (args env)
  (if (/= (length args) 3)
      (error "PARSE_ERROR")
      (let ((condition (evaluate (first args) env)))
        (if condition
            (evaluate (second args) env)
            (evaluate (third args) env)))))

(defun eval-let (args env)
  (if (< (length args) 2)
      (error "WRONG_ARITY")
      (let* ((bindings (first args))
             (body-forms (rest args)))
        (unless (listp bindings)
          (error "PARSE_ERROR"))

        (let ((vars '())
              (vals '()))
          (dolist (binding bindings)
            (unless (and (listp binding)
                         (= (length binding) 2)
                         (symbolp (first binding)))
              (error "PARSE_ERROR"))

            (push (first binding) vars)
            (push (evaluate (second binding) env) vals))

          (eval-sequence body-forms
                         (env-extend (reverse vars)
                                     (reverse vals)
                                     env))))))

(defun eval-lambda (args env)
  (if (< (length args) 2)
      (error "WRONG_ARITY")
      (let ((params (first args))
            (body-forms (rest args)))
        (unless (listp params)
          (error "PARSE_ERROR"))

        (dolist (p params)
          (unless (symbolp p)
            (error "PARSE_ERROR")))

        (make-closure params body-forms env))))

(defun apply-function (fn args env)
  (let ((arg-values (eval-args args env)))
    (cond
      ((closure-p fn)
       (let ((params (closure-params fn))
             (body-forms (closure-body-forms fn))
             (saved-env (closure-env fn)))

         (if (/= (length params) (length arg-values))
             (error "WRONG_ARITY")
             (eval-sequence body-forms
                            (env-extend params arg-values saved-env)))))

      (t
       (error "TYPE_MISMATCH")))))

(defun eval-define (args env)
  (unless (= (length args) 2)
    (error "PARSE_ERROR"))

  (let ((name (first args))
        (value-expr (second args)))
    (unless (symbolp name)
      (error "PARSE_ERROR"))

    ;; Placeholder cell allows recursive functions to reference themselves.
    (let* ((cell (make-cell nil))
           (new-env (env-extend-with-cells (list name) (list cell) env))
           (value (evaluate value-expr new-env)))

      (set-cell-value cell value)

      ;; Mutate global environment list by returning name/value pair through special handling.
      (values name cell))))

(defun eval-cond (args env)
  (dolist (clause args)
    (unless (and (listp clause)
                 (= (length clause) 2))
      (error "PARSE_ERROR"))

    (let ((test (first clause))
          (body (second clause)))
      (when (or (eq test 'ELSE)
                (evaluate test env))
        (return-from eval-cond (evaluate body env)))))

  (error "EVAL_ERROR"))

;;;; =========================
;;;; Main Evaluator
;;;; =========================

(defun evaluate (expr env)
  (cond
    ((integerp expr) expr)

    ((eq expr t) t)

    ((eq expr nil) nil)

    ((symbolp expr)
     (env-lookup expr env))

    ((listp expr)
     (let ((op (car expr))
           (args (cdr expr)))
       (cond
         ((member op '(+ - * /))
          (eval-arithmetic op args env))

         ((member op '(= < > <= >=))
          (eval-comparison op args env))

         ((eq op 'IF)
          (eval-if args env))

         ((eq op 'LET)
          (eval-let args env))

         ((eq op 'LAMBDA)
          (eval-lambda args env))

         ((eq op 'COND)
          (eval-cond args env))

         ((eq op 'DEFINE)
          (error "DEFINE_ONLY_ALLOWED_AT_TOP_LEVEL"))

         (t
          (apply-function (evaluate op env) args env)))))

    (t
     (error "EVAL_ERROR"))))

(defun eval-program (exprs)
  (let ((env '())
        (result nil))
    (dolist (expr exprs result)
      (if (and (listp expr) (eq (first expr) 'DEFINE))
          (multiple-value-bind (name cell) (eval-define (rest expr) env)
            (setf env (env-extend-with-cells (list name) (list cell) env))
            (setf result name))
          (setf result (evaluate expr env))))))

;;;; =========================
;;;; Output
;;;; =========================

(defun print-result (case-path value)
  (format t "Implementation: functional~%")
  (format t "Case: ~a~%" case-path)
  (format t "Status: OK~%")
  (format t "Result: ~a~%" (print-value value))
  (format t "Type: ~a~%" (type-name value)))

(defun print-error (case-path e)
  (let ((msg (princ-to-string e)))
    (let ((err-type
           (cond
             ((search "PARSE_ERROR" msg) "PARSE_ERROR")
             ((search "UNDECLARED_IDENTIFIER" msg) "UNDECLARED_IDENTIFIER")
             ((search "WRONG_ARITY" msg) "WRONG_ARITY")
             ((search "TYPE_MISMATCH" msg) "TYPE_MISMATCH")
             ((search "DIVISION_BY_ZERO" msg) "DIVISION_BY_ZERO")
             (t msg))))
      (format t "Implementation: functional~%")
      (format t "Case: ~a~%" case-path)
      (format t "Status: ERROR~%")
      (format t "Error: ~a~%" err-type))))

(defun main ()
  (let ((args sb-ext:*posix-argv*))
    (if (< (length args) 2)
        (format t "Usage: sbcl --script functional/minischeme_reader.lisp <file>~%")
        (let* ((path (nth 1 args))
               (input (read-file-as-string path))
               (tokens (tokenize input)))
          (handler-case
              (let* ((exprs (parse-program tokens))
                     (result (eval-program exprs)))
                (print-result path result))
            (error (e)
              (print-error path e)))))))

(main)