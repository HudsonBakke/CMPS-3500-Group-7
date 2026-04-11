;;; minischeme_reader.lisp

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
         (parse-list rest (cons expr acc)))))))

(defun parse-tokens (tokens)
  (multiple-value-bind (expr rest) (parse-expression tokens)
    (if (null rest)
        expr
        (error "PARSE_ERROR"))))

;;;; =========================
;;;; Evaluator
;;;; =========================

(defun env-lookup (name env)
  (let ((binding (assoc name env)))
    (if binding
        (cdr binding)
        (error "UNDECLARED_IDENTIFIER"))))

(defun env-extend (vars vals env)
  (append (pairlis vars vals) env))

(defun bool-value-p (x)
  (or (eq x t) (eq x nil)))

(defun make-closure (params body-forms env)
  (list 'CLOSURE params body-forms env))

(defun closure-p (value)
  (and (listp value)
       (>= (length value) 4)
       (eq (first value) 'CLOSURE)))

(defun type-name (value)
  (cond
    ((integerp value) "int")
    ((bool-value-p value) "bool")
    ((closure-p value) "function")
    (t "unknown")))

(defun eval-args (args env)
  (mapcar (lambda (arg) (evaluate arg env)) args))

(defun ensure-integer (value)
  (if (integerp value)
      value
      (error "TYPE_MISMATCH")))

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
      (error "WRONG_ARITY")
      (let ((condition (evaluate (first args) env)))
        (if condition
            (evaluate (second args) env)
            (evaluate (third args) env)))))

(defun closure-params (closure)
  (second closure))

(defun closure-body-forms (closure)
  (third closure))

(defun closure-env (closure)
  (fourth closure))

(defun eval-sequence (forms env)
  (if (null forms)
      (error "PARSE_ERROR")
      (let ((result nil))
        (dolist (form forms result)
          (setf result (evaluate form env))))))

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
                         (env-extend (reverse vars) (reverse vals) env))))))

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

         (t
          (apply-function (evaluate op env) args env)))))

    (t
     (error "EVAL_ERROR"))))

(defun print-result (value)
  (format t "Status: OK~%")
  (cond
    ((eq value t)
     (format t "Result: #t~%"))
    ((eq value nil)
     (format t "Result: #f~%"))
    ((closure-p value)
     (format t "Result: <function>~%"))
    (t
     (format t "Result: ~a~%" value)))
  (format t "Type: ~a~%" (type-name value)))

(defun main ()
  (let ((args sb-ext:*posix-argv*))
    (if (< (length args) 2)
        (format t "Usage: sbcl --script functional/minischeme_reader.lisp <file>~%")
        (let* ((path (nth 1 args))
               (input (read-file-as-string path))
               (tokens (tokenize input)))
          (handler-case
              (let* ((parsed (parse-tokens tokens))
                     (result (evaluate parsed '())))
                (format t "Implementation: functional~%")
                (format t "Case: ~a~%" path)
                (print-result result))
            (error (e)
              (format t "Implementation: functional~%")
              (format t "Case: ~a~%" path)
              (format t "Status: ERROR~%")
              (format t "Error: ~a~%" e)))))))

(main)