package org.apache.jmeter.visualizers;

import java.util.HashMap;
import java.util.Map;

import org.HdrHistogram.Histogram;
import org.LatencyUtils.LatencyStats;
import org.apache.jorphan.math.IStatCalculator;
import org.slf4j.LoggerFactory;

public class HistogramStatCalculator implements IStatCalculator<Long> {
    LatencyStats latencyStats = new LatencyStats(1,3600000000000L,2,1024,10000000000L,null);
    Histogram histogram = latencyStats.getIntervalHistogram();
    private long bytes = 0;
    private long sentBytes = 0;
    private long sum = 0;
    private long min = Long.MAX_VALUE;
    private long max = Long.MIN_VALUE;

    public HistogramStatCalculator() {
        LoggerFactory.getLogger(this.getClass()).info("HistogramStatCalculator used.");
    }

    @Override
    public void clear() {
        histogram.reset();
        bytes = 0;
        sentBytes = 0;
        sum = 0;
        min = Long.MAX_VALUE;
        max = Long.MIN_VALUE;
    }

    @Override
    public void addBytes(long newValue) {
        bytes += newValue;
    }

    @Override
    public void addSentBytes(long newValue) {
        sentBytes += newValue;
    }

    @Override
    public void addAll(IStatCalculator<Long> calc) {
        if (calc instanceof HistogramStatCalculator) {
            HistogramStatCalculator histoCalc = (HistogramStatCalculator) calc;
            sum += histoCalc.sum;
            bytes += histoCalc.bytes;
            sentBytes += histoCalc.sentBytes;
            histogram.add(histoCalc.histogram);
            max = Math.max(histoCalc.max, max);
            min = Math.min(histoCalc.min, min);
        } else {
            throw new IllegalArgumentException("Only instances of HistogramStatCalculator allowed.");
        }
    }

    @Override
    public Long getMedian() {
        return histogram.getValueAtPercentile(50);
    }

    @Override
    public long getTotalBytes() {
        return bytes;
    }

    @Override
    public long getTotalSentBytes() {
        return sentBytes;
    }

    @Override
    public Long getPercentPoint(float percent) {
        return getPercentPoint((double) percent);
    }

    @Override
    public Long getPercentPoint(double percent) {
        return histogram.getValueAtPercentile(100.0 * percent);
    }

    @Override
    public Map<Number, Number[]> getDistribution() {
        Map<Number, Number[]> result = new HashMap<>();
        histogram.percentiles(5).forEach(p -> {
            result.put(p.getValueIteratedTo(),
                    new Number[] { p.getValueIteratedTo(), p.getCountAddedInThisIterationStep() });
        });
        return result;
    }

    @Override
    public double getMean() {
        return histogram.getMean();
    }

    @Override
    public double getStandardDeviation() {
        return histogram.getStdDeviation();
    }

    @Override
    public Long getMin() {
        return min;
    }

    @Override
    public Long getMax() {
        return max;
    }

    @Override
    public long getCount() {
        return histogram.getTotalCount();
    }

    @Override
    public double getSum() {
        return sum;
    }

    @Override
    public void addValue(Long val, long sampleCount) {
        sum += val * sampleCount;
        histogram.recordValueWithCount(val, sampleCount);
        max = Math.max(val, max);
        min = Math.min(val, min);
    }

    @Override
    public void addValue(Long val) {
        sum += val;
        histogram.recordValue(val);
        max = Math.max(val, max);
        min = Math.min(val, min);
    }

}