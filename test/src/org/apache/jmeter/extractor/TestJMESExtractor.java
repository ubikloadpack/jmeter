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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.apache.jmeter.extractor.json.jsonpath.JMESExtractor;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

public class TestJMESExtractor {
    private static final String VAR_NAME = "varName";

    private JMESExtractor setupProcessor(JMeterContext context, String matchNumbers) {
        return setupProcessor(context, matchNumbers, true);
    }

    private JMESExtractor setupProcessor(JMeterContext context, String matchNumbers, boolean computeConcatenation) {
        JMESExtractor processor = new JMESExtractor();
        processor.setThreadContext(context);
        processor.setRefNames(VAR_NAME);
        processor.setMatchNumbers(matchNumbers);
        return processor;
    }

    @Test
    public void testJMESExtractor() {
        // test1
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
        // test2
        context.setVariables(vars);
        vars = new JMeterVariables();
        vars.put("contentvar", "[\"a\", \"b\", \"c\", \"d\", \"e\", \"f\"]");
        context.setVariables(vars);
        processor.setJsonPathExpressions("[0:3]");
        processor.process();
        assertThat(vars.get("varname_1"), CoreMatchers.is("[\"a\",\"b\",\"c\"]"));
        assertThat(vars.get("varname_matchNr"), CoreMatchers.is("1"));
        // test3
        processor.setJsonPathExpressions("people[:2].first");
        context.setVariables(vars);
        vars = new JMeterVariables();
        vars.put("contentvar",
                "{\r\n" + "  \"people\": [\r\n" + "    {\"first\": \"James\", \"last\": \"d\"},\r\n"
                        + "    {\"first\": \"Jacob\", \"last\": \"e\"},\r\n"
                        + "    {\"first\": \"Jayden\", \"last\": \"f\"},\r\n" + "    {\"missing\": \"different\"}\r\n"
                        + "  ],\r\n" + "  \"foo\": {\"bar\": \"baz\"}\r\n" + "}");
        context.setVariables(vars);
        processor.process();
        assertThat(vars.get("varname_1"), CoreMatchers.is("[\"James\",\"Jacob\"]"));
        // test4
        vars = new JMeterVariables();
        processor.setJsonPathExpressions("people[2]");
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
    public void testMatchNumberOneInJMESExtractor() {
        JMeterContext context = JMeterContextService.getContext();
        JMESExtractor processor = setupProcessor(context, "-1", true);
        JMeterVariables vars = new JMeterVariables();
        processor.setMatchNumbers("1");
        processor.setDefaultValues("NONE");
        processor.setJsonPathExpressions("a.b.c.d");
        processor.setRefNames("varname");
        processor.setScopeVariable("contentvar");
        context.setVariables(vars);
        vars.put("contentvar", "{\"a\": {\"b\": {\"c\": {\"d\": \"value\"}}}}");
        processor.process();
        assertThat(vars.get("varname"),CoreMatchers.is("\"value\""));
        assertThat(vars.get("varname_1"), CoreMatchers.is(CoreMatchers.nullValue()));
        assertThat(vars.get("varname_matchNr"), CoreMatchers.is("1"));
    }
    @Test
    public void testMatchNumberZeroInJMESExtractor() {
        JMeterContext context = JMeterContextService.getContext();
        JMESExtractor processor = setupProcessor(context, "-1", true);
        JMeterVariables vars = new JMeterVariables();
        processor.setMatchNumbers("0");
        processor.setDefaultValues("NONE");
        processor.setJsonPathExpressions("a.b.c.d");
        processor.setRefNames("varname");
        processor.setScopeVariable("contentvar");
        context.setVariables(vars);
        vars.put("contentvar", "{\"a\": {\"b\": {\"c\": {\"d\": \"value\"}}}}");
        processor.process();
        assertThat(vars.get("varname"),CoreMatchers.is("\"value\""));
        assertThat(vars.get("varname_1"), CoreMatchers.is(CoreMatchers.nullValue()));
    }
    @Test
    public void testMatchNumberMoreThanOneInJMESExtractor() {
        JMeterContext context = JMeterContextService.getContext();
        JMESExtractor processor = setupProcessor(context, "-1", true);
        JMeterVariables vars = new JMeterVariables();
        processor.setMatchNumbers("2");
        processor.setDefaultValues("NONE");
        processor.setJsonPathExpressions("a.b.c.d");
        processor.setRefNames("varname");
        processor.setScopeVariable("contentvar");
        context.setVariables(vars);
        vars.put("contentvar", "{\"a\": {\"b\": {\"c\": {\"d\": \"value\"}}}}");
        processor.process();
        assertThat(vars.get("varname"), CoreMatchers.is("NONE"));
        assertThat(vars.get("varname_1"), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testEmptyExpression() {
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
        assertEquals("NONE",vars.get("varname"));
    }

    @Test
    public void testErrorJsonPath() {
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
        assertThat(vars.get("varname"), CoreMatchers.is("NONE"));
        assertThat(vars.get("varname_matchNr"), CoreMatchers.is("0"));
    }
    @Test
    public void testNullResponse() {
        JMeterContext context = JMeterContextService.getContext();
        JMESExtractor processor = setupProcessor(context, "-1", true);
        JMeterVariables vars = new JMeterVariables();
        processor.setMatchNumbers("0");
        processor.setDefaultValues("NONE");
        processor.setJsonPathExpressions("a.b.c.e");
        processor.setRefNames("varname");
        processor.setScopeVariable("contentvar");
        context.setVariables(vars);
        vars.put("contentvar", "{\"a\": {\"b\": {\"c\": {\"d\": \"value\"}}}}");
        processor.process();
        assertThat(vars.get("varname"), CoreMatchers.is("NONE"));
        assertThat(vars.get("varname_matchNr"), CoreMatchers.is("0"));
    }
}
