%author: Patrick Charles prc9219
%class : CMPS 450
%due date  : 10/30/15
%assignment #2 - logic programming


%empty list and list with one element returned
mergesort([],[]).   
mergesort([X],[X]).

% X = list
% Y = H of split list
% Z = Z of split list
% S, S1, S2 = vars to store sorted lists
% L1, L2 = vars to store split lists
% M = merged list so far
% ZX, ZY = tails of X and Y respectively

%if at least2 elements,split list
mergesort([X,Y|Z],S) :-  
	%split list into L1,L2
	split1([X,Y|Z],L1,L2),

	%sort the list1
	mergesort(L1,S1),

	%sort list2
	mergesort(L2,S2),

	%merge 2 sorted lists into S
	merge(S1,S2,S).

%empty list returns empty sorted list
split1([],[],[]).

%single element list returns empty sorted list
split1([X],[X],[]).

%split list into 2 with first 2 elements as heads of their own list
split1([X,Y|Z],[X|ZX],[Y|ZY]) :- 
	split1(Z,ZX,ZY).

%single element and empty lists merge into single element list
merge(X,[],X).
merge([],Y,Y).

%if head <= tail, merge tail of X and tail of Y into M list
merge([X|ZX],[Y|ZY],[X|M]) :- 
	X =< Y, 
	merge(ZX,[Y|ZY],M).

%%if head > tail, merge head of X and tail of Y into M list
merge([X|ZX],[Y|ZY],[Y|M]) :-  
	X > Y,  
	merge([X|ZX],ZY,M).