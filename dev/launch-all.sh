#!/bin/bash
THREADS=${1:-500}
shift

echo "***NO DISRUPTOR***"
./launch.sh $THREADS 60 300 0 IRRELEVANT $1

echo "***WITH DISRUPTOR***"
for SIZE in 1024 65536 131072 262144 524288
do
  for STRATEGY in BlockingWaitStrategy SleepingWaitStrategy YieldingWaitStrategy BusySpinWaitStrategy
  do 
    ./launch.sh $THREADS 60 300 $SIZE $STRATEGY $1
  done
done

echo "***STATS***"
for dir in test-*-*
do
  echo "$dir: $(./count-trans.sh $dir/results.csv RJ-0)"
done

echo "***ERRORS***"
for dir in test-*-*
do
  echo "$dir: $(grep -c 'ERROR\|xception' $dir/jmeter.log)"
done

