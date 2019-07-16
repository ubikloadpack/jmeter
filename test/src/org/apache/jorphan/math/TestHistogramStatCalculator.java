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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.HdrHistogram.Histogram;
import org.LatencyUtils.LatencyStats;
import org.LatencyUtils.SimplePauseDetector;
import org.junit.Before;
import org.junit.Test;

public class TestHistogramStatCalculator {

    private HistogramStatCalculatorLong calc;
    private HistogramStatCalculatorLong calc1;
    private SimplePauseDetector defaultPauseDetector = new SimplePauseDetector();
    private static long lowestTrackableLatency = 1000000L;
    private static long highestTrackableLatency = 3600000000000L;
    private static int numberOfSignificantValueDigits = 2;
    private static int intervalEstimatorWindowLength = 1024;
    private static long intervalEstimatorTimeCap = 10000000000L;
    @Before
    public void setUp() {
        calc = new HistogramStatCalculatorLong();
        calc1 = new HistogramStatCalculatorLong();
    }

    @Test
    public void testPercentage() {
        long[] values = new long[] { 10L, 9L, 5L, 6L, 3L, 8L, 2L, 7L, 4L };
        for (long l : values) {
            calc.addValue(l);
        }
        long[] values1 = new long[] { 11L, 23L, 2L };
        for (long l : values1) {
            calc1.addValue(l);
        }
        calc.addAll(calc1);
        assertEquals(Long.valueOf(23), calc.getMax());
        assertEquals(Long.valueOf(2), calc.getMin());
        assertEquals(12, calc.getCount());
        assertEquals(11, calc.getPercentPoint(0.8999999).intValue());
    }
    
    @Test
    public void testPercentage1()  {
        long[] values = new long[] { 10L, 9L, 5L, 6L, 1L, 3L, 8L, 2L, 7L, 4L };

        LatencyStats latencyStats = new LatencyStats(lowestTrackableLatency, highestTrackableLatency,
                numberOfSignificantValueDigits, intervalEstimatorWindowLength, intervalEstimatorTimeCap,
                defaultPauseDetector);
        Histogram histogram = new Histogram(latencyStats.getIntervalHistogram());
        for (long l : values) {
            calc.addValue(l);
            histogram.recordValue(l*1000000);
        }
        assertEquals(9, calc.getPercentPoint(0.8999999).intValue());
        assertEquals(Math.round(histogram.getValueAtPercentile(90)/1000000), calc.getPercentPoint(0.9).intValue());
        assertEquals(Math.round(histogram.getValueAtPercentile(90)/1000000), calc.getPercentPoint(0.9f).intValue());
    }
    

    @Test
    public void testPercentile2() {
        long[] values = new long[] {
            10L, 20L, 30L, 40L, 50L, 60L, 80L, 90L
        };
        LatencyStats latencyStats = new LatencyStats(lowestTrackableLatency, highestTrackableLatency,
                numberOfSignificantValueDigits, intervalEstimatorWindowLength, intervalEstimatorTimeCap,
                defaultPauseDetector);
        Histogram histogram = new Histogram(latencyStats.getIntervalHistogram());
        for (long l : values) {
            calc.addValue(l);
            histogram.recordValue(l*1000000);
        }
        assertEquals((int) histogram.getValueAtPercentile(50)/1000000, calc.getMedian().intValue());
    }

    @Test
    public void testPercentile3() {
        long[] values = new long[] { 5L,5L,5L, 1L, 7L };
        LatencyStats latencyStats = new LatencyStats(lowestTrackableLatency, highestTrackableLatency,
                numberOfSignificantValueDigits, intervalEstimatorWindowLength, intervalEstimatorTimeCap,
                defaultPauseDetector);
        Histogram histogram = new Histogram(latencyStats.getIntervalHistogram());
        for (long l : values) {
            histogram.recordValue(l*1000000);
        }
        calc.addValue(5L, 3);
        calc.addValue(1L);
        calc.addValue(7L);
        assertEquals(Long.valueOf(7), calc.getMax());
        assertEquals(Long.valueOf(1), calc.getMin());
        assertEquals(5, calc.getCount());
        assertEquals((int) histogram.getValueAtPercentile(50) / 1000000, calc.getMedian().intValue());
    }

    @Test
    public void testLong() {
        calc.addValue(0L);
        calc.addValue(2L);
        calc.addValue(2L);
        final Long long0 = Long.valueOf(0);
        final Long long2 = Long.valueOf(2);
        assertEquals(long2, calc.getMax());
        assertEquals(long0, calc.getMin());
        Map<Number, Number[]> map = calc.getDistribution();
        assertTrue(map.containsKey(long0));
        assertTrue(map.containsKey(long2));
        assertEquals(1, map.get(long0)[1].longValue());
        assertEquals(2, map.get(long2)[1].longValue());
        HistogramStatCalculatorLong calc2 = new  HistogramStatCalculatorLong();
        calc2.addValue(2L);
        calc2.addValue(2L);
        calc2.addValue(2L);
        calc.addAll(calc2);
        map = calc.getDistribution();
        assertEquals(1,map.get(long0)[1].longValue());
        assertEquals(5,map.get(long2)[1].longValue());
    }
    
    @Test
    public void testStandardDeviation(){ 
        calc.addValue(1L);
        calc.addValue(2L);
        calc.addValue(3L);
        calc.addValue(2L);
        calc.addValue(2L);
        calc.addValue(2L);
        assertEquals(6, calc.getCount());
        assertEquals(12.0, calc.getSum(), 0.000000000001);
        assertEquals(0.5787915367575983, calc.getStandardDeviation(), 0.000000000000001);
    }

    @Test
    public void testStandardDeviation1(){ 
        calc.addValue(1L);
        calc.addValue(2L);
        calc.addValue(3L);
        HistogramStatCalculatorLong calc2 = new  HistogramStatCalculatorLong();
        calc2.addValue(2L);
        calc2.addValue(2L);
        calc2.addValue(2L);
        calc.addAll(calc2);
        assertEquals(6, calc.getCount());
        assertEquals(12.0, calc.getSum(), 0.000000000001);
        assertEquals(0.5787915367575983, calc.getStandardDeviation(), 0.000000000000001);
        assertEquals(2.0032853333333334,calc.getMean(),0.0);
    }
    @Test
    public void testClear(){ 
        calc.addValue(1L);
        calc.addValue(2L);
        calc.addValue(3L);
        assertEquals(3, calc.getCount());
        assertEquals(6.0, calc.getSum(), 0.000000000001);
        calc.clear();
        assertEquals(0, calc.getCount());
        assertEquals(0.0, calc.getSum(), 0.000000000001);

    }
    @Test
    public void addBytes(){ 
        calc.addBytes(300);
        calc.addBytes(200);
        calc.addBytes(100);
        assertEquals(600, calc.getTotalBytes());
        calc.addSentBytes(300);
        calc.addSentBytes(200);
        calc.addSentBytes(100);
        assertEquals(600, calc.getTotalSentBytes());

    }
}
