;author: Patrick Charles prc9212
;class : CMPS 450
;date  : 9/22/15
;assignment #1

;a function that collects integers from the user until a 0 is encountered and
;returns them in a list in the order they were input

(define ( zero ) (lambda (tempList)
	(let ((input (read)))
	;read input into list
	
	  (cond (( = input 0 ) (reverse tempList)) 
	  ;if zero, stop reading, return inputted values and reverse
	  
	    (else (zero ( cons tempList )))))))
	    ;calls function again for input and adds inputted value to 
	    ;list with 'cons' function
