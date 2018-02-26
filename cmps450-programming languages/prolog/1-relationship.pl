%author: Patrick Charles prc9219
%class : CMPS 450
%due date  : 10/30/15
%assignment #2 - logic programming


%grandparent
%X is grandparent of Z  if X is a parent of Z's parent

grandparent(X, Z) :-
    parent(X, Y),
    parent(Y, Z).

%parent
%A is parent of B if  A is  father or mother of B
%according to fact database

parent(A,B) :- father(A,B).
parent(A,B) :- mother(A,B).

%sibling
%have at least one common parent
%test if siblings same person

sibling(X,Y) :- parent(Z,X), 
	parent(Z,Y), not(X=Y).

%cousin
%child1 and child2 are cousins if their parents are siblings

cousin(Child1,Child2) :-
    parent(Y1,Child1),
    parent(Y2,Child2),
    sibling(Y1,Y2).


%Fact database

%father

father(harry, richard).
father(harry, catherine).
father(robert, theresa).
father(robert, elaine).
father(robert, robertjr).
father(richard, patrick).
father(richard, chris).
father(roger, thomas).
father(roger, sara).
father(gary, russell).
father(gary, melissa).
father(robertjr, michael).
father(robertjr, daniel).

%mother

mother(emelda, richard).
mother(emelda, catherine).
mother(doralea,theresa).
mother(doralea, robertjr).
mother(doralea, elaine).
mother(theresa, patrick).
mother(theresa, chris).
mother(catherine,thomas).
mother(catherine, sara).
mother(elaine, russell).
mother(elaine, melissa).
mother(linda, michael).
mother(mother, daniel).
