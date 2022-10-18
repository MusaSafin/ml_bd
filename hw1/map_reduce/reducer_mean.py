#!/usr/bin/python3

import sys
mean = 0
n = 0

for line in sys.stdin:
    cur_n, cur_mean = map(float, line.split('\t'))
    mean = (n * mean + cur_n * cur_mean) / (n + cur_n)
    n += cur_n

print(str(n), str(mean), sep='\t')