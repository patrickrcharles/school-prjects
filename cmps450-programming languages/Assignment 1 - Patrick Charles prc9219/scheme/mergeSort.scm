;author: Patrick Charles prc9212
;class : CMPS 450
;date  : 9/22/15

;Mergesort for a list of integers

(define merge (lambda (L1 L2 tempList)
    (cond ((and (null? L2) (null? L1)) (reverse tempList))
        ((null? L1) (merge '() (cdr L2) (cons (car L2) tempList)))
        ((null? L2) (merge (cdr L1) '() (cons (car L1) tempList)))
        ((<= (car L1) (car L2)) (merge (cdr L1) L2 (cons (car L1) tempList)))
            (else (merge L1 (cdr L2) (cons (car L2) tempList))))))
 
(define merge1 (lambda (L1 L2) (merge L1 L2 '())))
 
(define splitlist (lambda (L left right counter)
    (cond ((null? L) (list (reverse left) (reverse right)))
        ((< (count left) counter) (splitlist (cdr L) (cons (car L) left) right counter))
        (else (splitlist (cdr L) left (cons (car L) right) counter)))))
 
(define split (lambda (L) (splitlist L '() '() (/ (count L) 2))))
 
(define mergesort (lambda (L)
    (cond ((= (count L) 1) L)
        (else
        (let ((lists (splitlist L '() '() (/ (count L) 2))))
        (merge (mergesort (car lists)) (mergesort (cadr lists)) '()))))))
