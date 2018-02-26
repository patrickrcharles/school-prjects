;author: Patrick Charles prc9212
;class : CMPS 450
;date  : 9/22/15
;assignment #1

;tail-recursive function to compute the length of an arbitrary list.

(define list-length (lambda (L result) 
; function receives a list and accumulator

	(if (null? L) result
	;if list is empty, return the accumulator
					
		(loop(cdr L) (+ 1 result)
		;goes through list incrementing 
		;accumulator each time
					
          ))))
 
(define counter( lambda (L) (list-length L 0))
;function loops through list adding to accumulator until null (0)
;and returns the result