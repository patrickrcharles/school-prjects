--author: Patrick Charles prc9212
--class : CMPS 450
--date  : 9/22/15

merge :: (Ord a) => [a] -> [a] -> [a]
merge [] xs = xs
merge xs [] = xs
merge (x:xs) (y:ys)
    | (x <= y) =  x : merge xs ( y : ys)
    | otherwise = y : merge (x :xs) ys
 
split :: [a] -> ([a], [a])
split [] = ([], [])
split [x] = ([x], [])
split ( x : y : xys) = (x : xs, y : ys)
    where (xs, ys) = split xys
 
mergesort :: (Ord a) => [a] -> [a]
mergesort xs 
    | (length xs) > 1 = merge (mergesort ls) (mergesort rs)
    | otherwise = xs
    where (ls, rs) = split xs

