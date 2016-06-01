""" 
    Author:harry7

"""
import string
import random
import sys

if len(sys.argv)!=4:
    print "Format python2.7 test_gen.py [Number of keys] [Length of string] [Number of test queries]"
    sys.exit(1)
else:
    lis = sys.argv
    n = int(lis[1])
    N = int(lis[2])
    t = int(lis[3])

# Initialising
print 1

# Inserting keys

for i in range(1,n+1):
    print 2
    print i
    print ''.join(random.choice(string.letters) for _ in range(N))

# Generating Tests 
for i in range(t):
    print 3
    print random.randint(1,n)
# Exitting
print 4
