--author: Patrick Charles prc9212
--class : CMPS 450
--date  : 9/22/15

max1 :: Ord a => [a] -> a
max1 [] = undefined
max1 [x] = x
max1 (x1 : x2 : xs)
  | x1 > x2 = max1 (x1 : xs)
  | otherwise = max1 (x2 : xs)
  
min1 :: Ord a => [a] -> a
min1 [] = undefined
min1 [x] = x
min1 (x1 : x2 : xs)
  | x1 < x2 = min1 (x1 : xs)
  | otherwise = min1 (x2 : xs) 
  
  
--minmax [x] = min1[x] max1[x]

