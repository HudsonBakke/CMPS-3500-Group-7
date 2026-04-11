(define make-adder
  (lambda (x)
    (lambda (y) (+ x y))))
((make-adder 10) 5)