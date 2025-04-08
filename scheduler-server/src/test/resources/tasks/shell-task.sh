#!/usr/bin/env bash

if [[ $# -eq 0 ]];then
    echo "参数个数为0"
else
    while [[ $# -ge 1 ]] ; do
        echo "${#} === ${1}"
        shift
    done
fi