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
package org.apache.jmeter.report.processor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.HdrHistogram.Histogram;
import org.LatencyUtils.LatencyStats;
import org.LatencyUtils.SimplePauseDetector;
import org.junit.Before;
import org.junit.Test;

public class StatisticsSummaryDataTest {
    private StatisticsSummaryData statisticsSummaryData;
    private SimplePauseDetector defaultPauseDetector = new SimplePauseDetector();
    private static long lowestTrackableLatency = 1000000L;
    private static long highestTrackableLatency = 3600000000000L;
    private static int numberOfSignificantValueDigits = 2;
    private static int intervalEstimatorWindowLength = 1024;
    private static long intervalEstimatorTimeCap = 10000000000L;
    @Before
    public void setUp() {
        statisticsSummaryData = new StatisticsSummaryData();
    }

    @Test
    public void testStatisticsSummaryData() {
        LatencyStats latencyStats = new LatencyStats(lowestTrackableLatency, highestTrackableLatency,
                numberOfSignificantValueDigits, intervalEstimatorWindowLength, intervalEstimatorTimeCap,
                defaultPauseDetector);
        Histogram histogram = new Histogram(latencyStats.getIntervalHistogram());
        long[] values = new long[] { 10L, 9L, 5L, 6L, 3L, 8L, 2L, 7L, 4L };
        for (long l : values) {
            statisticsSummaryData.addValue(l);
            statisticsSummaryData.setMax(l);
            statisticsSummaryData.setMin(l);
            statisticsSummaryData.incTotal();
            histogram.recordValue(l*1000000L);
        }
        // test max/min
        assertTrue(10 == statisticsSummaryData.getMax());
        assertTrue(2 == statisticsSummaryData.getMin());
        // test total
        assertTrue(9 == statisticsSummaryData.getTotal());
        // test sentByte
        statisticsSummaryData.incSentBytes(100);
        statisticsSummaryData.incSentBytes(200);
        statisticsSummaryData.incSentBytes(300);
        assertTrue(600 == statisticsSummaryData.getSentBytes());
        // test percentiles
        assertEquals(histogram.getValueAtPercentile(90)/1000000, statisticsSummaryData.getPercentile(90));
        assertEquals(histogram.getValueAtPercentile(95)/1000000, statisticsSummaryData.getPercentile(95));
        assertEquals(histogram.getValueAtPercentile(99)/1000000, statisticsSummaryData.getPercentile(99));
        // test bytes
        statisticsSummaryData.incBytes(100);
        statisticsSummaryData.incBytes(200);
        statisticsSummaryData.incBytes(300);
        assertTrue(600 == statisticsSummaryData.getBytes());
        // test elapsedTime
        statisticsSummaryData.setEndTime(1000);
        statisticsSummaryData.setFirstTime(400);
        assertTrue(statisticsSummaryData.getEndTime() - statisticsSummaryData.getFirstTime() == statisticsSummaryData
                .getElapsedTime());
        double kbytesPerSecond = statisticsSummaryData.getBytes()
                / ((double) statisticsSummaryData.getElapsedTime() / 1000) / 1024;
        assertTrue(statisticsSummaryData.getEndTime() - statisticsSummaryData.getFirstTime() == statisticsSummaryData
                .getElapsedTime());
        assertTrue(kbytesPerSecond == statisticsSummaryData.getKBytesPerSecond());
        // test SentBytesPerSecond
        double bytesPerSecond = statisticsSummaryData.getSentBytes()
                / ((double) statisticsSummaryData.getElapsedTime() / 1000);
        assertTrue(bytesPerSecond == statisticsSummaryData.getBytesPerSecond());
        // test SentKBytesPerSecond
        double tKBytesPerSecond = bytesPerSecond / 1024;
        assertTrue(tKBytesPerSecond == statisticsSummaryData.getKBytesPerSecond());
    }
}
