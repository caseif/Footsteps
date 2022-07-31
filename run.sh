#!/usr/bin/bash

cd ./target

find ./natives -name '*.so' -exec cp {} ./natives/ \;

java -Djava.library.path=./natives -jar ./footsteps.jar 
