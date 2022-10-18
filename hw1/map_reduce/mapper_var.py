#!/usr/bin/python3

import sys
import csv

IDX = 9
n = 0
var = 0
mean = 0
mean_of_squares = 0

for line in csv.reader(sys.stdin):
    try:
        val = float(line[IDX])
    except ValueError:
        continue

    mean = (n * mean + val) / (n + 1)
    mean_of_squares = (mean_of_squares * n + val ** 2) / (n + 1)
    n += 1

print(str(n), str(mean), str(mean_of_squares - mean ** 2), sep='\t')
