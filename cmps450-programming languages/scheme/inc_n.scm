;The inc_n function takes in an integer an returns an nth incremented
;function.

(define inc_n (lambda (n)
                (lambda(x) (+ n x))))
