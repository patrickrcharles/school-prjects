%author: Patrick Charles prc9219
%class : CMPS 450
%due date  : 10/30/15
%assignment #2 - logic programming

%minmax
%takes list and 2 variables
%computes max and min using helper functions
%and outputs the values

minmax([H|T], Min, Max) :- 
	max([H|T], Max), min([H|T], Min). 

%max

%takes list and variable(M) to store max
max([H|T], M) :- 
	max(T, H, M).

%puts tail in list. sets head as max.
max([], M, M).

%takes tail as list, compares head of tail to previous tail
%if =<, recursively iterates through list until higher number found
max([H|T], Y, M) :- 
	H =< Y, max(T, Y, M).

%if head of tail >previous head, new max is setand
%iterates through list
max([H|T], Y, M) :- 
	H > Y, max(T, H, M).

%min

%takes list and variable(M) to store min
min([H|T], M) :- 
	min(T, H, M).

%puts tail in list. sets head as min.
min([], M, M).

%takes tail as list, compares head of tail to previous tail
%if =<, recursively iterates through list until lower number found
min([H|T], Y, M) :- 
	H =< Y, min(T, H, M).

%if head of tail >previous head, new min is set and
%iterates through list
min([H|T], Y, M) :- 
	H > Y, min(T, Y, M).