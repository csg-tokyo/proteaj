#!/bin/bash

cat /dev/null > log.txt

for i in `seq 1 20`
do
    for j in `seq 1 5`
    do
        for k in `seq 100 100 1000`
        do
            for n in `seq 1 10`
            do
                java -classpath lib/spoofax-aterm-0.2.1.jar:lib/spoofax-jsglr-0.2.0.jar:lib/spoofax-terms-0.6.0.jar:bin -Xmx5600m test.Main $i $j $k >> log.txt
            done
        done
    done
done
