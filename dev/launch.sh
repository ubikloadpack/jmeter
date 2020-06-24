THREADS=${1:-500}
RAMPUP=${2:-60}
DURATION=${3:-300}
RING_BUFFER_SIZE=${4:-65536}
STRATEGY=${5:-BlockingWaitStrategy}
WITH_SUMMARIZER=$6

REV=$(git rev-parse --short HEAD)
if [ -z "$WITH_SUMMARIZER" ]
then
  SUMMARIZER_ARG="-Jsummariser.name="
  TEST_OUTDIR="test-$REV-$THREADS-$RING_BUFFER_SIZE-$STRATEGY-$(date +%Y%m%d-%H%M%S)"
else
  TEST_OUTDIR="test-$REV-$THREADS-$RING_BUFFER_SIZE-$STRATEGY-$(date +%Y%m%d-%H%M%S)-WITH-SUMMMARIZER"
fi

HEAP="-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$TEST_OUTDIR"
# Flight recorder
HEAP="$HEAP -XX:StartFlightRecording=disk=true,delay=2m,duration=2m,dumponexit=true,filename=$TEST_OUTDIR/recording.jfr,maxsize=2g,maxage=1d,settings=profile,path-to-gc-roots=true"
HEAP="$HEAP -Duser.timezone=Europe/Paris -Djava.awt.headless=true"
HEAP="$HEAP -Dcom.sun.management.jmxremote.authenticate=false"
HEAP="$HEAP -Dcom.sun.management.jmxremote.ssl=false -Xlog:gc*,gc+age=trace,gc+heap=debug:file=$TEST_OUTDIR/gc_jmeter.log"
HEAP="$HEAP -Djava.net.preferIPv4Stack=true -Djava.net.preferIPv6Addresses=false"
#HEAP="$HEAP -verbose:gc"
HEAP="$HEAP -XX:MaxMetaspaceSize=1g -XX:G1HeapRegionSize=32m -XX:MaxGCPauseMillis=50 -Xms5g -Xmx5g -Xss256k"
HEAP="$HEAP -XX:ParallelGCThreads=10 -XX:ConcGCThreads=8"
export HEAP

mkdir $TEST_OUTDIR

#-o $TEST_OUTDIR/report
../bin/jmeter -Lcom.lmax.disruptor=DEBUG $SUMMARIZER_ARG \
  -Jthreads=$THREADS \
  -Jrampup=$RAMPUP \
  -Jduration=$DURATION \
  -Jjmeter.save.disruptor.ringbuffer.size=$RING_BUFFER_SIZE \
  -Jjmeter.save.disruptor.wait-strategy=$STRATEGY \
  -f -n -t 'test.jmx' -l $TEST_OUTDIR/results.csv -j $TEST_OUTDIR/jmeter.log
