#!/bin/bash

cat /dev/null > log.txt

for n in `seq 1 10`
do
    for i in `seq 1 20`
    do
        java -classpath lib/spoofax-aterm-0.2.1.jar:lib/spoofax-jsglr-0.2.0.jar:lib/spoofax-terms-0.6.0.jar:bin -Xmx5600m test.Main $i >> log.txt
    done
done
