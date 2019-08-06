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
package org.apache.jmeter.extractor;

import static org.junit.Assert.assertThat;

import org.apache.jmeter.extractor.json.jsonpath.JMESExtractor;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

public class TestJMESExtractorGui {
    private static final String VAR_NAME = "varName";

    @Test
    public void testProcessWith2PostProcessor() {
        JMeterContext context = JMeterContextService.getContext();
        JMESExtractor processor = setupProcessor(context, "-1", true);
        JMeterVariables vars = new JMeterVariables();
        processor.setDefaultValues("NONE");
        processor.setJsonPathExpressions("a.b.c.d");
        processor.setRefNames("varname");
        processor.setScopeVariable("contentvar");
        context.setVariables(vars);
        vars.put("contentvar", "{\"a\": {\"b\": {\"c\": {\"d\": \"value\"}}}}");
        processor.process();
        assertThat(vars.get("varname"), CoreMatchers.is(CoreMatchers.nullValue()));
        assertThat(vars.get("varname_1"), CoreMatchers.is("\"value\""));
        assertThat(vars.get("varname_matchNr"), CoreMatchers.is("1"));
    }

    @Test
    public void testProcessWith2PostProcessor1() {
        JMeterContext context = JMeterContextService.getContext();
        JMESExtractor processor = setupProcessor(context, "-1", true);
        JMeterVariables vars = new JMeterVariables();
        processor.setComputeConcatenation(true);
        processor.setDefaultValues("NONE");
        processor.setJsonPathExpressions("[0:3]");
        processor.setRefNames("varname");
        processor.setScopeVariable("contentvar");
        context.setVariables(vars);
        vars.put("contentvar", "[\"a\", \"b\", \"c\", \"d\", \"e\", \"f\"]");
        processor.process();
        assertThat(vars.get("varname_1"), CoreMatchers.is("[\"a\",\"b\",\"c\"]"));
        assertThat(vars.get("varname_matchNr"), CoreMatchers.is("1"));
    }

    @Test
    public void testPR235CaseEmptyResponse() {
        JMeterContext context = JMeterContextService.getContext();
        JMESExtractor processor = setupProcessor(context, "-1", true);
        JMeterVariables vars = new JMeterVariables();
        processor.setDefaultValues("NONE");
        processor.setJsonPathExpressions("[*]");
        processor.setRefNames("varname");
        processor.setScopeVariable("contentvar");
        context.setVariables(vars);
        vars.put("contentvar", "");
        processor.process();
        assertThat(vars.get("varname_matchNr"), CoreMatchers.is(CoreMatchers.nullValue()));
        assertThat(vars.get("varname_1"), CoreMatchers.is(CoreMatchers.nullValue()));
        assertThat(vars.get("varname_2"), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testProcessWith2PostProcessor2() {
        JMeterContext context = JMeterContextService.getContext();
        JMESExtractor processor = setupProcessor(context, "-1", true);
        JMeterVariables vars = new JMeterVariables();
        processor.setDefaultValues("NONE");
        processor.setJsonPathExpressions("people[:2].first");
        processor.setRefNames("varname");
        processor.setScopeVariable("contentvar");
        context.setVariables(vars);
        vars.put("contentvar",
                "{\r\n" + "  \"people\": [\r\n" + "    {\"first\": \"James\", \"last\": \"d\"},\r\n"
                        + "    {\"first\": \"Jacob\", \"last\": \"e\"},\r\n"
                        + "    {\"first\": \"Jayden\", \"last\": \"f\"},\r\n" + "    {\"missing\": \"different\"}\r\n"
                        + "  ],\r\n" + "  \"foo\": {\"bar\": \"baz\"}\r\n" + "}");
        processor.process();
        assertThat(vars.get("varname_1"), CoreMatchers.is("[\"James\",\"Jacob\"]"));
    }

    @Test
    public void testProcessWith2PostProcessor3() {
        JMeterContext context = JMeterContextService.getContext();
        JMESExtractor processor = setupProcessor(context, "-1", true);
        JMeterVariables vars = new JMeterVariables();
        processor.setDefaultValues("NONE");
        processor.setJsonPathExpressions("people[2]");
        processor.setRefNames("varname");
        processor.setScopeVariable("contentvar");
        context.setVariables(vars);
        vars.put("contentvar",
                "{\r\n" + "  \"people\": [\r\n" + "    {\"first\": \"James\", \"last\": \"d\"},\r\n"
                        + "    {\"first\": \"Jacob\", \"last\": \"e\"},\r\n"
                        + "    {\"first\": \"Jayden\", \"last\": \"f\"},\r\n" + "    {\"missing\": \"different\"}\r\n"
                        + "  ],\r\n" + "  \"foo\": {\"bar\": \"baz\"}\r\n" + "}");
        processor.process();
        assertThat(vars.get("varname_1"), CoreMatchers.is("{\"first\":\"Jayden\",\"last\":\"f\"}"));
    }
    @Test
    public void testProcessWith2PostProcessor4() {
        JMeterContext context = JMeterContextService.getContext();
        JMESExtractor processor = setupProcessor(context, "-1", true);
        JMeterVariables vars = new JMeterVariables();
        processor.setDefaultValues("NONE");
        processor.setJsonPathExpressions("k");
        processor.setRefNames("varname");
        processor.setScopeVariable("contentvar");
        context.setVariables(vars);
        vars.put("contentvar", "{\"a\": {\"b\": {\"c\": {\"d\": \"value\"}}}}");
        processor.process();
        assertThat(vars.get("varname"), CoreMatchers.is(CoreMatchers.nullValue()));
        assertThat(vars.get("varname_matchNr"), CoreMatchers.is("1"));
    }
    private JMESExtractor setupProcessor(JMeterContext context, String matchNumbers) {
        return setupProcessor(context, matchNumbers, true);
    }

    private JMESExtractor setupProcessor(JMeterContext context, String matchNumbers,
            boolean computeConcatenation) {
        JMESExtractor processor = new JMESExtractor();
        processor.setThreadContext(context);
        processor.setRefNames(VAR_NAME);
        processor.setMatchNumbers(matchNumbers);
        processor.setComputeConcatenation(computeConcatenation);
        return processor;
    }
}
