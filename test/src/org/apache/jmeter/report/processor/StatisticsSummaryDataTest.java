package org.apache.jmeter.report.processor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.HdrHistogram.Histogram;
import org.LatencyUtils.LatencyStats;
import org.junit.Before;
import org.junit.Test;

public class StatisticsSummaryDataTest {
    private StatisticsSummaryData statisticsSummaryData;

    @Before
    public void setUp() {
        statisticsSummaryData = new StatisticsSummaryData();
    }

    @Test
    public void testStatisticsSummaryData() {
        LatencyStats latencyStats = new LatencyStats(1, 3600000000000L, 2, 1024, 10000000000L, null);
        Histogram histogram = latencyStats.getIntervalHistogram();
        long[] values = new long[] { 10L, 9L, 5L, 6L, 3L, 8L, 2L, 7L, 4L };
        for (long l : values) {
            statisticsSummaryData.addValue(l);
            statisticsSummaryData.setMax(l);
            statisticsSummaryData.setMin(l);
            statisticsSummaryData.incTotal();
            histogram.recordValue(l);
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
        assertEquals(histogram.getValueAtPercentile(90), statisticsSummaryData.getPercentile(90));
        assertEquals(histogram.getValueAtPercentile(95), statisticsSummaryData.getPercentile(95));
        assertEquals(histogram.getValueAtPercentile(99), statisticsSummaryData.getPercentile(99));
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
