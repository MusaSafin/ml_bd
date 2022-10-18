#!/usr/bin/python3

import sys

n_cur = 0
mean_cur = 0
var_cur = 0

for line in sys.stdin:
    n, mean, var= map(float, line.split('\t'))
    
    var_cur = (n_cur * var_cur + n * var) / (n_cur + n) + n * n_cur * ((mean_cur - mean) / (n + n_cur)) ** 2
    mean_cur = (n_cur * mean_cur + n * mean) / (n + n_cur)
    n_cur += n

print(str(n_cur), str(mean_cur), str(var_cur), sep='\t')