/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.jorphan.math;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.HdrHistogram.Histogram;
import org.LatencyUtils.LatencyStats;

public class HistogramStatCalculatorLong implements IStatCalculator<Long> {
    private LatencyStats latencyStats = new LatencyStats();
    private Histogram histogram = new Histogram(latencyStats.getIntervalHistogram());
    private long bytes = 0;
    private long sentBytes = 0;
    private long sum = 0;
    private long min = Long.MAX_VALUE;
    private long max = Long.MIN_VALUE;
    private Map<Long, Long> valuesMap = new TreeMap<>();
    public HistogramStatCalculatorLong() {
    }

    @Override
    public void clear() {
        bytes = 0;
        sentBytes = 0;
        sum = 0;
        min = Long.MAX_VALUE;
        max = Long.MIN_VALUE;
        latencyStats.stop();
        histogram.reset();
        valuesMap.clear();
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
        if (calc instanceof HistogramStatCalculatorLong) {
            HistogramStatCalculatorLong histoCalc = (HistogramStatCalculatorLong) calc;
            sum += histoCalc.sum;
            bytes += histoCalc.bytes;
            sentBytes += histoCalc.sentBytes;
            histogram.add(histoCalc.histogram);
            max = Math.max(histoCalc.max, max);
            min = Math.min(histoCalc.min, min);
            valuesMap = updateAllValueCount(valuesMap, histoCalc.valuesMap);
        } else {
            throw new IllegalArgumentException("Only instances of HistogramStatCalculator allowed.");
        }
    }

    @Override
    public Long getMedian() {
        return histogram.getValueAtPercentile(50)/1000000;
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
        return histogram.getValueAtPercentile(100.0 * percent)/1000000;
    }

    @Override
    public Map<Number, Number[]> getDistribution() {
        Map<Number, Number[]> result = new HashMap<>();
        for (Map.Entry<Long,Long> entry : valuesMap.entrySet()) {
            Number[] dis = new Number[2];
            dis[0] = entry.getKey();
            dis[1] = entry.getValue();
            result.put(entry.getKey(), dis);   
        }
        return result;
    }

    @Override
    public double getMean() {
        return histogram.getMean()/1000000;
    }

    @Override
    public double getStandardDeviation() {
        return histogram.getStdDeviation()/1000000;
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
    /**
     * @return the valuesMap
     */
    public Map<Long, Long> getValuesMap() {
        return valuesMap;
    }


    @Override
    public void addValue(Long val, long sampleCount) {
        sum += val * sampleCount;
        for (int i = 0; i < sampleCount; i++) {
            latencyStats.recordLatency(val*1000000);
        }
        histogram.add(latencyStats.getIntervalHistogram());
        max = Math.max(val, max);
        min = Math.min(val, min);
        updateValueCount(val,sampleCount);
    }

    @Override
    public void addValue(Long val) {
        sum += val;
        latencyStats.recordLatency(val*1000000);
        histogram.add(latencyStats.getIntervalHistogram());
        max = Math.max(val, max);
        min = Math.min(val, min);
        updateValueCount(val,1);
    }
    private void updateValueCount(Long actualValue, long sampleCount) {
        Long count = valuesMap.get(actualValue);
        if (count != null) {
            valuesMap.put(actualValue, count+sampleCount);
        } else {
            // insert new value
            valuesMap.put(actualValue, sampleCount);
        }
    }
    
    private Map<Long, Long> updateAllValueCount(Map<Long, Long> oldValuesMap, Map<Long, Long> newValuesMap) {
        for (Map.Entry<Long, Long> entry : newValuesMap.entrySet()) {
            Long key = entry.getKey();
            if (oldValuesMap.get(key) != null) {
                oldValuesMap.put(key, entry.getValue() + oldValuesMap.get(key));
            } else {
                oldValuesMap.put(key, entry.getValue());
            }
        }
        return oldValuesMap;
    }
}
