#!/bin/bash

cat /dev/null > log.txt

script_dir="../../"

for n in `seq 1 20`
do
    echo "syntax ${n}" >> log.txt
    for l in `seq 1000 1000 10000`
    do
        echo "size ${l}" >> log.txt
        for i in `seq 1 10`
        do
            ( time java -classpath ${script_dir}/lib/javassist.jar:${script_dir}/out/production/proteaj:${script_dir}/bin:./bin:./bin${n} proteaj.Compiler src/Test${l}.pj ) &>> log.txt
        done
    done
done
