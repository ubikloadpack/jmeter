package org.apache.jmeter.threads;

import java.util.LinkedList;
import java.util.List;

import org.apache.jmeter.assertions.Assertion;
import org.apache.jmeter.extractor.Extractor;
import org.apache.jmeter.samplers.SampleListener;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.timers.Timer;

/**
 * Title:        JMeter
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:      Apache
 * @author Michael Stover
 * @version 1.0
 */

public class SamplePackage
{
	List sampleListeners = new LinkedList();
	List timers = new LinkedList();
	List assertions = new LinkedList();
    List extractors = new LinkedList();
	Sampler sampler;

	public SamplePackage()
	{
	}

	public List getSampleListeners()
	{
		return sampleListeners;
	}

	public void addSampleListener(SampleListener listener)
	{
		sampleListeners.add(listener);
	}

	public List getTimers()
	{
		return timers;
	}
    
    public void addExtractor(Extractor ex)
    {
        extractors.add(ex);
    }

	public void addTimer(Timer timer)
	{
		timers.add(timer);
	}

	public void addAssertion(Assertion asser)
	{
		assertions.add(asser);
	}

	public List getAssertions()
	{
		return assertions;
	}
    
    public List getExtractors()
    {
        return extractors;
    }

	public Sampler getSampler()
	{
		return sampler;
	}

	public void setSampler(Sampler s)
	{
		sampler = s;
	}
}