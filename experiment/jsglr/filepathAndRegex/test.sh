#!/bin/bash

java -classpath lib/spoofax-aterm-0.2.1.jar:lib/spoofax-jsglr-0.2.0.jar:lib/spoofax-terms-0.6.0.jar:bin -Xmx5600m test.Main $1 $2 $3


