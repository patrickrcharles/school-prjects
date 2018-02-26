

%start program with var to take user input
%author: Patrick Charles prc9219
%class : CMPS 450
%due date  : 10/30/15
%assignment #2 - logic programming


%into list
start([H|T]):- 
	
	%prompt user for input
	write('enter number : '),

	%read user input
	read(H),

	%if zero, stop
    dif(H,0),

    %else, read more into into tail of current list
    start(T).

%if empty list, stop/output "X = []"
start([]).