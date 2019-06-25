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

import org.HdrHistogram.Histogram;
import org.LatencyUtils.LatencyStats;

/**
 * The class ApdexSummaryData provides information for
 * StatisticsSummaryConsumer.
 * 
 * @since 3.0
 *
 */
public class StatisticsSummaryData {
    LatencyStats latencyStats = new LatencyStats(1,3600000000000L,2,1024,10000000000L,null);
    Histogram histogram = latencyStats.getIntervalHistogram();   
    private long firstTime = Long.MAX_VALUE;
    private long endTime = Long.MIN_VALUE;
    private long bytes = 0L;
    private long sentBytes = 0L;
    private long errors = 0L;
    private long total = 0L;
    private long min = Long.MAX_VALUE;
    private long max = Long.MIN_VALUE;

    public long getElapsedTime() {
        return endTime - firstTime;
    }

    /**
     * Gets the first time.
     *
     * @return the firstTime
     */
    public final long getFirstTime() {
        return firstTime;
    }

    /**
     * Sets the first time.
     *
     * @param firstTime
     *            the firstTime to set
     */
    public final void setFirstTime(long firstTime) {
        this.firstTime = Math.min(this.firstTime, firstTime);
    }

    /**
     * Gets the end time.
     *
     * @return the endTime
     */
    public final long getEndTime() {
        return endTime;
    }

    /**
     * Sets the end time.
     *
     * @param endTime
     *            the endTime to set
     */
    public final void setEndTime(long endTime) {
        this.endTime = Math.max(this.endTime, endTime);
    }

    /**
     * Gets the bytes.
     *
     * @return the bytes
     */
    public final long getBytes() {
        return bytes;
    }

    /**
     * Sets the bytes.
     *
     * @param bytes
     *            the bytes to set
     */
    public final void setBytes(long bytes) {
        this.bytes = bytes;
    }

    /**
     * @return the errors
     */
    public final long getErrors() {
        return errors;
    }

    /**
     * @param errors
     *            the errors to set
     */
    public final void setErrors(long errors) {
        this.errors = errors;
    }

    /**
     * @return the total
     */
    public final long getTotal() {
        return total;
    }

    /**
     * @param total
     *            the total to set
     */
    public final void setTotal(long total) {
        this.total = total;
    }

    /**
     * @return the min
     */
    public final long getMin() {
        return min;
    }

    /**
     * @param min
     *            the min to set
     */
    public final void setMin(long min) {
        this.min = Math.min(this.min, min);
    }

    /**
     * @return the max
     */
    public final long getMax() {
        return max;
    }

    /**
     * @param max
     *            the max to set
     */
    public final void setMax(long max) {
        this.max = Math.max(this.max, max);
    }

    /**
     * @return the percentile1
     */
    public final long getPercentile(double percent) {
        return histogram.getValueAtPercentile(percent);
    }

    /**
     * Gets the bytes per second.
     *
     * @return the bytes per second
     */
    public double getBytesPerSecond() {
        return bytes / ((double) getElapsedTime() / 1000);
    }

    /**
     * Gets the kilo bytes per second.
     *
     * @return the kilo bytes per second
     */
    public double getKBytesPerSecond() {
        return getBytesPerSecond() / 1024;
    }

    /**
     * Gets the throughput.
     *
     * @return the throughput
     */
    public double getThroughput() {
        return (total / (double) getElapsedTime()) * 1000.0;
    }

    public void incTotal() {
        total++;
    }

    /**
     * Increment received bytes
     * @param value bytes
     */
    public void incBytes(long value) {
        bytes += value;
    }
    

    /**
     * Increment sent bytes
     * @param value bytes
     */
    public void incSentBytes(long value) {
        sentBytes += value;
    }
    

    public void incErrors() {
        errors++;
    }

    public double getMean() {
        return histogram.getMean();
    }

    /**
     * @return the sentBytes
     */
    public long getSentBytes() {
        return sentBytes;
    }
    
    /**
     * Gets the sent bytes per second.
     *
     * @return the sent bytes per second
     */
    public double getSentBytesPerSecond() {
        return sentBytes / ((double) getElapsedTime() / 1000);
    }

    /**
     * Gets the sent kilo bytes per second.
     *
     * @return the sent kilo bytes per second
     */
    public double getSentKBytesPerSecond() {
        return getSentBytesPerSecond() / 1024;
    }
    
    public void addValue(Long val) {
        histogram.recordValue(val);
    }
}
