;author: Patrick Charles prc9212
;class : CMPS 450
;date  : 9/22/15
;assignment #1

;compute the maximum and minimum of a list of integers

(define findmin (lambda (L min1)
;takes a list and a result
	
    (cond ((null? L) min1)
	;if list empty return 1st element/head
	
		((< (car L) min1) (findmin (cdr L)(car L)))
		;test if head of list is < min value so far
		;if so, calls itself with the tail of the list and 
		;the new min value and goes through entire list
		
		(else(findmin (cdr L) min1)))))
		;if headNOT < min value so far, calls itself with
		;the tail of list and initial min1 value
 
(define min1 (lambda (L) (findmin L (car L))))
;takes a list and returns min value using findmin function
 
(define findmax (lambda (L max1)
;takes a list and a result

    (cond ((null? L) max1)
	;if list empty return 1st element/head
        ((> (car L) max1) (findmax (cdr L)(car L)))
		;test if head of list is > max value so far
		;if so, calls itself with the tail of the list and 
		;the new min value and goes through entire list
		
		(else(findmax (cdr L) max1)))))
		;if headNOT > max value so far, calls itself with
		;the tail of list and initial min1 value
 
(define max1 (lambda (L) (findmin L (car L))))
;takes a list and returns max value using findmax function
 
(define minmax(lambda (L) (list (min1 L) (max1 L))))
;returns a list of minimum value and maximum value using min/max functions


