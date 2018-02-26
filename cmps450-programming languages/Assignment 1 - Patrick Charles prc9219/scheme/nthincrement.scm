;author: Patrick Charles prc9212
;class : CMPS 450
;date  : 9/22/15
;assignment #1

;function takes in an integer and returns an nth increment function
;which increments parameters by n

(define nthinc (lambda (n)(lambda(x) (+ n x))))
;example    ((nthinc 3 ) 2 ) = 5
;where 3 is lambda (n) and 2 is lambda (x)
;it just take the 2 and adds it to the n and returns the value  
  
;it evaluates the parenthesis to see if there is another parameter
; and if there is, it is added to the original one given to the 
;function
