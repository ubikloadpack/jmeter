#!/bin/bash
file=$1
shift

for transaction in $@
do
  total=$(grep ",$transaction," $file |wc -l)
  ok=$(grep ",$transaction," $file | grep ",true," |wc -l)
  ko=$(grep ",$transaction," $file | grep ",false," |wc -l)
  echo "$transaction Total: $total, OK: $ok, KO: $ko"
done
