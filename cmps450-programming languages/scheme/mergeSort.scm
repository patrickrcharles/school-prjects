(define merge (lambda (L1 L2 list-so-far)
                (cond ((and (null? L2) (null? L1)) (reverse list-so-far))
                      ((null? L1) (merge '() (cdr L2) (cons (car L2) list-so-far)))
                      ((null? L2) (merge (cdr L1) '() (cons (car L1) list-so-far)))
                      ((<= (car L1) (car L2)) (merge (cdr L1) L2 (cons (car L1) list-so-far)))
                       (else (merge L1 (cdr L2) (cons (car L2) list-so-far))))))
 
(define merge1 (lambda (L1 L2) (merge L1 L2 '())))
 
(define splitlist (lambda (L left right cnt)
                    (cond ((null? L) (list (reverse left) (reverse right)))
                          ((< (count left) cnt) (splitlist (cdr L) (cons (car L) left) right cnt))
                          (else (splitlist (cdr L) left (cons (car L) right) cnt)))))
 
(define split (lambda (L) (splitlist L '() '() (/ (count L) 2))))
 
(define mergesort (lambda (L)
                   (cond ((= (count L) 1) L)
                         (else
                          (let ((lists (splitlist L '() '() (/ (count L) 2))))
                            (merge (mergesort (car lists)) (mergesort (cadr lists)) '()))))))
