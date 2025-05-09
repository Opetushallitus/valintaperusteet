#!/usr/bin/env bash

file_list=''

for file in $@;do
  file_list="$file_list,.*$file"
done

mvn --batch-mode -Dstyle.color=always -DspotlessFiles=$file_list spotless:apply