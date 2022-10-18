#!/usr/bin/python3

import sys
import csv

IDX = 9
n = 0
mean = 0

for line in csv.reader(sys.stdin):
    try:
        val = float(line[IDX])
    except ValueError:
        continue

    mean = (n * mean + val)
    n += 1
    mean /= n

print(str(n), str(mean), sep='\t')
