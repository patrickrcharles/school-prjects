--author: Patrick Charles prc9212
--class : CMPS 450
--date  : 9/22/15

--This program will write a tail-recursive function to 
--compute the length of an arbitrary list.

len :: [a] -> Int -> Int
len [] acc = acc
len (x : xs) acc = len xs (1 + acc)

