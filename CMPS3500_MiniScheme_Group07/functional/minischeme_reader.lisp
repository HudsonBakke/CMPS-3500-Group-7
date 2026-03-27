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
          ;; whitespace → end token
          ((or (char= ch #\Space)
               (char= ch #\Newline)
               (char= ch #\Tab)
               (char= ch #\Return))
           (setf tokens (flush-token current tokens))
           (setf current ""))

          ;; parentheses → separate tokens
          ((or (char= ch #\()
               (char= ch #\)))
           (setf tokens (flush-token current tokens))
           (setf current "")
           (push (string ch) tokens))

          ;; normal characters
          (t
           (setf current (concatenate 'string current (string ch))))))

      (setf tokens (flush-token current tokens))
      (reverse tokens))))

(defun atom-from-token (token)
  (cond
    ;; booleans
    ((string= token "#t") t)
    ((string= token "#f") nil)

    ;; integer
    ((every #'digit-char-p token)
     (parse-integer token))

    ;; negative integer
    ((and (> (length token) 1)
          (char= (char token 0) #\-)
          (every #'digit-char-p (subseq token 1)))
     (parse-integer token))

    ;; identifier
    (t (intern (string-upcase token)))))

(defun parse-expression (tokens)
  (when (null tokens)
    (error "PARSE_ERROR"))

  (let ((token (car tokens)))
    (cond
      ;; start list
      ((string= token "(")
       (parse-list (cdr tokens) '()))

      ;; unexpected closing
      ((string= token ")")
       (error "PARSE_ERROR"))

      ;; atom
      (t
       (values (atom-from-token token) (cdr tokens))))))

(defun parse-list (tokens acc)
  (when (null tokens)
    (error "PARSE_ERROR"))

  (let ((token (car tokens)))
    (cond
      ;; end of list
      ((string= token ")")
       (values (reverse acc) (cdr tokens)))

      ;; keep parsing
      (t
       (multiple-value-bind (expr rest) (parse-expression tokens)
         (parse-list rest (cons expr acc)))))))

(defun parse-tokens (tokens)
  (multiple-value-bind (expr rest) (parse-expression tokens)
    (if (null rest)
        expr
        (error "PARSE_ERROR"))))

(defun main ()
  (let ((args sb-ext:*posix-argv*))
    (if (< (length args) 2)
        (format t "Usage: sbcl --script functional/minischeme_reader.lisp <file>~%")
        (let* ((path (nth 1 args))
               (input (read-file-as-string path))
               (tokens (tokenize input)))
          
          (format t "=== Input ===~%~a~%~%" input)
          (format t "=== Tokens ===~%~s~%~%" tokens)

          (handler-case
              (let ((parsed (parse-tokens tokens)))
                (format t "=== Parsed ===~%~s~%" parsed))
            (error ()
              (format t "=== Parsed ===~%PARSE_ERROR~%")))))))

(main)