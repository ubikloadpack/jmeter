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
package org.apache.jmeter.extractor.json.jsonpath;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.processor.PostProcessor;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.AbstractScopedTestElement;
import org.apache.jmeter.testelement.ThreadListener;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import io.burt.jmespath.Expression;
import io.burt.jmespath.JmesPath;
import io.burt.jmespath.jackson.JacksonRuntime;

/**
 * JSON-PATH based extractor
 * 
 * @since 3.0
 */
public class JMESExtractor extends AbstractScopedTestElement implements Serializable, PostProcessor, ThreadListener {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(JMESExtractor.class);
    private static final String JSON_PATH_EXPRESSIONS = "JMESExtractor.jsonPathExprs"; // $NON-NLS-1$
    private static final String REFERENCE_NAMES = "JMESExtractor.referenceNames"; // $NON-NLS-1$
    private static final String DEFAULT_VALUES = "JMESExtractor.defaultValues"; // $NON-NLS-1$
    private static final String MATCH_NUMBERS = "JMESExtractor.match_numbers"; // $NON-NLS-1$
    private static final String REF_MATCH_NR = "_matchNr"; // $NON-NLS-1$
    private static final String ALL_SUFFIX = "_ALL"; // $NON-NLS-1$
    public static final boolean COMPUTE_CONCATENATION_DEFAULT_VALUE = false;
    LoadingCache<Expression<JsonNode>, JsonNode> JMES_EXTRACTOR_CACHE;

    @Override
    public void process() {
        JmesPath<JsonNode> jmespath = new JacksonRuntime();
        JMeterContext context = getThreadContext();
        JMeterVariables vars = context.getVariables();
        String jsonResponse;
        if (isScopeVariable()) {
            jsonResponse = vars.get(getVariableName());
            if (log.isDebugEnabled()) {
                log.debug("JSON Extractor is using variable: {}, which content is: {}", getVariableName(),
                        jsonResponse);
            }
        } else {
            SampleResult previousResult = context.getPreviousResult();
            if (previousResult == null) {
                return;
            }
            jsonResponse = previousResult.getResponseDataAsString();
            if (log.isDebugEnabled()) {
                log.debug("JSON Extractor {} working on Response: {}", getName(), jsonResponse);
            }
        }
        String refNames = getRefNames();
        String jsonPathExpressions = getJsonPathExpressions().trim();
        String defaultValues = getDefaultValues();
        int matchNumber = Integer.parseInt(getMatchNumbers());
        clearOldRefVars(vars, refNames);
        try {
            if (StringUtils.isEmpty(jsonResponse)) {
                if (log.isDebugEnabled()) {
                    log.debug("Response or source variable is null or empty for {}", getName());
                }
                vars.put(refNames, defaultValues);
            } else {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode actualObj;
                JsonNode result = null;
                try {
                    actualObj = mapper.readValue(jsonResponse, JsonNode.class);
                    JMES_EXTRACTOR_CACHE = Caffeine.newBuilder().maximumSize(400).build(e -> e.search(actualObj));
                    Expression<JsonNode> expression = jmespath.compile(jsonPathExpressions);
                    result = JMES_EXTRACTOR_CACHE.get(expression);
                    if (result.isNull()) {
                        vars.put(refNames, defaultValues);
                        vars.put(refNames + REF_MATCH_NR, "0"); //$NON-NLS-1$
                        if (matchNumber < 0) {
                            log.debug("No value extracted, storing empty in: {}{}", refNames, ALL_SUFFIX);
                            vars.put(refNames + ALL_SUFFIX, "");
                        }
                    } else {
                        String suffix = (matchNumber < 0) ? "_1" : "";
                        placeObjectIntoVars(vars, refNames + suffix, result, 0);
                        if (matchNumber < 0) {
                            vars.put(refNames + ALL_SUFFIX, vars.get(refNames + suffix));
                        }
                        if (matchNumber != 0) {
                            vars.put(refNames + REF_MATCH_NR, Integer.toString(1));
                        }
                    }
                } catch (JsonParseException | JsonMappingException e) {
                    if (log.isDebugEnabled()) {
                        log.debug("Could not find JSON Path {} in [{}]: {}", jsonPathExpressions, jsonResponse,
                                e.getLocalizedMessage());
                    }
                }
            }
        } catch (Exception e) {
            // if something wrong, default value added
            if (log.isDebugEnabled()) {
                log.error("Error processing JSON content in {}, message: {}", getName(), e.getLocalizedMessage(), e);
            } else {
                log.error("Error processing JSON content in {}, message: {}", getName(), e.getLocalizedMessage());
            }
            vars.put(refNames, defaultValues);
        }
    }

    private void clearOldRefVars(JMeterVariables vars, String refName) {
        vars.remove(refName + REF_MATCH_NR);
        for (int i = 1; vars.get(refName + "_" + i) != null; i++) {
            vars.remove(refName + "_" + i);
        }
    }

    private void placeObjectIntoVars(JMeterVariables vars, String currentRefName, Object extractedValues, int matchNr) {
        vars.put(currentRefName, stringify(extractedValues));
    }

    private String stringify(Object obj) {
        return obj == null ? "" : obj.toString(); //$NON-NLS-1$
    }

    public String getJsonPathExpressions() {
        return getPropertyAsString(JSON_PATH_EXPRESSIONS);
    }

    public void setJsonPathExpressions(String jsonPath) {
        setProperty(JSON_PATH_EXPRESSIONS, jsonPath);
    }

    public String getRefNames() {
        return getPropertyAsString(REFERENCE_NAMES);
    }

    public void setRefNames(String refName) {
        setProperty(REFERENCE_NAMES, refName);
    }

    public String getDefaultValues() {
        return getPropertyAsString(DEFAULT_VALUES);
    }

    public void setDefaultValues(String defaultValue) {
        setProperty(DEFAULT_VALUES, defaultValue, ""); // $NON-NLS-1$
    }

    @Override
    public void threadStarted() {
        // NOOP
    }

    @Override
    public void threadFinished() {
        JMES_EXTRACTOR_CACHE.cleanUp();
    }

    public void setMatchNumbers(String matchNumber) {
        setProperty(MATCH_NUMBERS, matchNumber);
    }

    public String getMatchNumbers() {
        return getPropertyAsString(MATCH_NUMBERS);
    }
}
