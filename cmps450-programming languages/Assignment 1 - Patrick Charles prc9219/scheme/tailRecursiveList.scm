;author: Patrick Charles prc9212
;class : CMPS 450
;date  : 9/22/15

;This program will write a tail-recursive function to 
;compute the length of an arbitrary list.

(define counter (lambda (L result) 
; function receives a list and accumulator
(if (null? L) result
;if list is empty, return the accumulator
(counter(cdr L) (+ 1 result)
;calls itself and goes through list incrementing 
;accumulator each time
))))
 
(define count( lambda (L) (counter L 0)))
;function loops through list adding to accumulator until null (0)
;and returns the result