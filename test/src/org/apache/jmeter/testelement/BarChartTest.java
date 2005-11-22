/*
 * Copyright 2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
package org.apache.jmeter.testelement;

import javax.swing.JComponent;

import org.apache.jmeter.save.SaveGraphicsService;
import org.apache.jmeter.junit.JMeterTestCase;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/**
 * @author peter lin
 *
 */
public class BarChartTest extends JMeterTestCase {
	
	private static final Logger log = LoggingManager.getLoggerForClass();

	/**
	 * @param arg0
	 */
	public BarChartTest(String arg0) {
		super(arg0);
	}

    public void testGenerateBarChart() {
        log.info("jtl version=" + JMeterUtils.getProperty("file_format.testlog"));
        // String sampleLog = "C:/eclipse3/workspace/jmeter-21/bin/testfiles/sample_log1.jtl";
        String sampleLog = "testfiles/sample_log1.jtl";
        String sampleLog2 = "testfiles/sample_log1b.jtl";
        JTLData input = new JTLData();
        JTLData input2 = new JTLData();
        input.setDataSource(sampleLog);
        input.loadData();
        input2.setDataSource(sampleLog2);
        input2.loadData();

        assertTrue((input.getStartTimestamp() > 0));
        assertTrue((input.getEndTimestamp() > input.getStartTimestamp()));
        assertTrue((input.getURLs().size() > 0));
        log.info("URL count=" + input.getURLs().size());
        java.util.ArrayList list = new java.util.ArrayList();
        list.add(input);
        list.add(input2);

        BarChart bchart = new BarChart();
        bchart.setTitle("Sample Chart");
        bchart.setCaption("Sample");
        bchart.setName("Sample");
        bchart.setYAxis("milliseconds");
        bchart.setYLabel("Test Runs");
        bchart.setXAxis(AbstractTable.REPORT_TABLE_MEAN);
        bchart.setXLabel("x label");
        bchart.setURL("jakarta_home");
        JComponent gr = bchart.renderChart(list);
        assertNotNull(gr);
        SaveGraphicsService serv = new SaveGraphicsService();
        String filename = bchart.getTitle();
        filename = filename.replace(' ','_');
        serv.saveJComponent(filename,SaveGraphicsService.PNG,gr);
    }
}
