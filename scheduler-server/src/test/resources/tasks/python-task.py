#-*- encoding=utf8 -*-

import sys

if __name__=="__main__":
    if  len(sys.argv) == 1:
        print("{}:参数个数为0".format(sys.argv[0]))
    else:
        for arg in sys.argv:
            print(arg)