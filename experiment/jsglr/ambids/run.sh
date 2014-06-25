#!/bin/bash

cat /dev/null > log.txt

for i in `seq 1000 1000 10000`
do
    for j in `seq 0 20`
    do
        for n in `seq 1 10`
        do
            java -classpath lib/spoofax-aterm-0.2.1.jar:lib/spoofax-jsglr-0.2.0.jar:lib/spoofax-terms-0.6.0.jar:bin -Xmx5600m -Xss10m test.Main 8 1 $i $j >> log.txt
        done
    done
done
