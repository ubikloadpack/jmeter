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
package org.apache.jmeter.assertions;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.extractor.json.jsonpath.JSONPostProcessor;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.oro.text.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import io.burt.jmespath.Expression;
import io.burt.jmespath.JmesPath;
import io.burt.jmespath.jackson.JacksonRuntime;

/**
 * This is main class for JSON JmesPath Assertion which verifies assertion on
 * previous sample result using JmesPath expression </br>
 * <a href="https://github.com/burtcorp/jmespath-java">JmesPath-java sources and
 * doc</a> </br>
 * <a href="http://jmespath.org/">JmesPath tutorial</a>
 * 
 */
public class JmesPathAssertion extends AbstractTestElement implements Serializable, Assertion {
    private static final Logger log = LoggerFactory.getLogger(JSONPostProcessor.class);
    private static final long serialVersionUID = 1L;
    private static final String JMESPATH = "JMES_PATH";
    private static final String EXPECTEDVALUE = "EXPECTED_VALUE";
    private static final String JSONVALIDATION = "JSONVALIDATION";
    private static final String EXPECT_NULL = "EXPECT_NULL";
    private static final String INVERT = "INVERT";
    private static final String ISREGEX = "ISREGEX";
    private static final String RESULT_VALUE_EMPTY_SUCCESS = "Result is empty and expected to be null or empty.";
    private static final String RESULT_VALUE_EMPTY_INVERT_SUCCESS = "Assertion has been successfully inverted. Expected value and result are both null or empty.";
    private static final String RESULT_VALUE_EQUALS_SUCCESS = "Query went well, result %s and expected value %s are matching.";
    private static final String RESULT_VALUE_EQUALS_INVERT_SUCCESS = "Assertion has been successfully inverted. Result %s matches with expected value %s";
    private static final String RESULT_VALUE_DIFFERENT_FAIL = "Expected value %s does not match with the result data %s.";
    private static final String RESULT_VALUE_DIFFERENT_INVERT_SUCCESS = "Invert went well, result %s and expected value %s are different.";
    private static final String RESULT_EMPTY_FAIL = "The result data should not be null or empty.";
    private static final String VALUE_EMPTY_FAIL = "Value expected to be null, but found %s.";
    private static final String REGEX_SUCCESS = "Result %s and expected value %s pattern are matching.";
    private static final String REGEX_FAIL = "Result %s and expected value %s pattern are different.";
    /**
     * Initialize error message
     */
    private String errorMessage;
    /**
     * initialize cache for compiled JMESPath expression
     */
    private static final LoadingCache<String, Expression<JsonNode>> JSON_EXPRESSION_CACHE;
    static {
        final int cacheSize = JMeterUtils.getPropDefault("jmesassertion.parser.cache.size", 400);
        JSON_EXPRESSION_CACHE = Caffeine.newBuilder().maximumSize(cacheSize).build(new JMESCacheLoader());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AssertionResult getResult(SampleResult samplerResult) {
        // initiate the AssertionResult that we will return
        AssertionResult result = new AssertionResult(getName());
        // get the response data from the sender
        String responseData = samplerResult.getResponseDataAsString();
        try {
            /*
             * get the result from JmesPath query as boolean value true if query returns a
             * result, else false
             */
            if (doAssert(responseData)) {
                result.setFailure(false);
                result.setFailureMessage("");
                // if assertion failed, we put a message in the error
            } else {
                result.setFailure(true);
                result.setFailureMessage(errorMessage);
            }
        } catch (Exception e) {
            log.debug("Assertion failed.", e);
            result.setFailure(true);
            result.setFailureMessage(e.getMessage());
        }
        return result;
    }

    /**
     * Used to do a JmesPath query
     * 
     * @param responseDataAsJsonString the response data from the sender
     * @return true if the expectedValue matches with the JmesPath query result
     * @throws Exception
     */
    private boolean doAssert(String responseDataAsJsonString) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        // cast the response data to JsonNode
        JsonNode input = mapper.readValue(responseDataAsJsonString, JsonNode.class);
        // get the JmesPath expression from the cache
        // if it does not exist, compile it.
        // Expression does not compile if JmesPath expression is empty or null
        Expression<JsonNode> expression = JSON_EXPRESSION_CACHE.get(getJmesPath());
        // get the result from the JmesPath query
        JsonNode currentValue = expression.search(input);
        // cast JsonNode as String, and remove the extra ' " ' in the String
        StringBuilder sb  =  new StringBuilder(mapper.writeValueAsString(currentValue));
        // check if first and last characters are ", if yes, delete them
        if (sb.charAt(0) == '\"' && sb.charAt(sb.length() - 1) == '\"') {
            sb.deleteCharAt(0);
            sb.deleteCharAt(sb.length() - 1);
        }
        String result = sb.toString();
        log.debug("JmesPath query {} invoked on response {}. Query result is {}. ", expression,
                responseDataAsJsonString, result);
        return checkResult(result);
    }

    /**
     * Used to check expected value and result data from JmesPath query.
     * 
     * @param result the JmesPath query result data.
     * @return true if both match and {@link #isInvert()} is false or if both
     *         are different and {@link #isInvert()} is true.
     */
    private boolean checkResult(String result) {
        boolean isResultEmpty = StringUtils.isEmpty(result) || result == null || result.equals("null");
        String expectedValue = null;
        if (isResultEmpty && isExpectNull() && !isInvert()) {
            setIsRegex(false);
            return setBooleanResult(true, RESULT_VALUE_EMPTY_SUCCESS);
        } else {
            if (!isExpectNull()) {
                expectedValue = getExpectedValue().trim();
            }
            boolean isExpectedValueEmpty = StringUtils.isEmpty(expectedValue) || StringUtils.isBlank(expectedValue);
            // ======================= CHECK REGEX ================================
            if (isUseRegex()) {
                if (checkIfRegexIsValid(result, expectedValue)) {
                    if (isInvert()) {
                        return setBooleanResult(false, String.format(REGEX_FAIL, result, expectedValue));
                    } else {
                        return setBooleanResult(true, String.format(REGEX_SUCCESS, result, expectedValue));
                    }
                } else {
                    if (isInvert()) {
                        return setBooleanResult(true, String.format(REGEX_SUCCESS, result, expectedValue));
                    } else {
                        return setBooleanResult(false, String.format(REGEX_FAIL, result, expectedValue));
                    }
                }
                // ======================= CHECK INVERT ================================
            } else if (isInvert()) {
                if (isExpectedValueEmpty && isResultEmpty) {
                    return setBooleanResult(false, RESULT_VALUE_EMPTY_INVERT_SUCCESS);
                } else if (result.equals(expectedValue)) {
                    return setBooleanResult(false,
                            String.format(RESULT_VALUE_EQUALS_INVERT_SUCCESS, result, expectedValue));
                } else if (!result.equals(expectedValue)) {
                    return setBooleanResult(true,
                            String.format(RESULT_VALUE_DIFFERENT_INVERT_SUCCESS, result, expectedValue));
                }
                // ======================= CHECK EMPTY =================================
            } else if (isResultEmpty) {
                return setBooleanResult(false, RESULT_EMPTY_FAIL);
            } else if (isExpectedValueEmpty) {
                return setBooleanResult(false, String.format(VALUE_EMPTY_FAIL, result));
                // ======================= CHECK MATCHE ================================
            } else if (!result.equals(expectedValue)) {
                return setBooleanResult(false, String.format(RESULT_VALUE_DIFFERENT_FAIL, expectedValue, result));
            }
        }
        return setBooleanResult(true, String.format(RESULT_VALUE_EQUALS_SUCCESS, result, expectedValue));
    }

    /**
     * 
     * Allow to set a message if an error occurred during a request.
     * 
     * @param result  the request result.
     * @param message the message for logger or to set an error message.
     * @return true if query went well.
     */
    private boolean setBooleanResult(boolean result, String message) {
        // set the error message and boolean to false.
        if (message != null) {
            errorMessage = message;
            log.debug(errorMessage);
            return result;
        }
        log.debug(message);
        return result;
    }

    /**
     * Check if pattern and result match
     * 
     * @param result               the query result
     * @param expectedValuePattern the expected value as pattern
     * @return true if both match
     */
    private boolean checkIfRegexIsValid(String result, String expectedValuePattern) {
        Pattern pattern = JMeterUtils.getPatternCache().getPattern(expectedValuePattern);
        return JMeterUtils.getMatcher().matches(result, pattern);
    }

    /*
     * ------------------------ GETTER/SETTER ------------------------
     */
    public String getJmesPath() {
        return getPropertyAsString(JMESPATH);
    }

    public void setJmesPath(String jmesPath) {
        setProperty(JMESPATH, jmesPath);
    }

    public String getExpectedValue() {
        return getPropertyAsString(EXPECTEDVALUE);
    }

    public void setExpectedValue(String expectedValue) {
        setProperty(EXPECTEDVALUE, expectedValue);
    }

    public void setJsonValidationBool(boolean jsonValidation) {
        setProperty(JSONVALIDATION, jsonValidation);
    }

    public void setExpectNull(boolean val) {
        setProperty(EXPECT_NULL, val);
    }

    public boolean isExpectNull() {
        return getPropertyAsBoolean(EXPECT_NULL);
    }

    public boolean isJsonValidationBool() {
        return getPropertyAsBoolean(JSONVALIDATION);
    }

    public void setInvert(boolean invert) {
        setProperty(INVERT, invert);
    }

    public boolean isInvert() {
        return getPropertyAsBoolean(INVERT);
    }

    public void setIsRegex(boolean flag) {
        setProperty(ISREGEX, flag);
    }

    public boolean isUseRegex() {
        return getPropertyAsBoolean(ISREGEX, true);
    }
}

/**
 * 
 * Provide cache implementation
 * Call {@link JMESCacheLoader#load(String)} when an expression
 * does not exist in the cache
 *
 */
class JMESCacheLoader implements CacheLoader<String, Expression<JsonNode>> {
    final JmesPath<JsonNode> jmespath = new JacksonRuntime();
    @Override
    public Expression<JsonNode> load(String jsonPathExpression) {
        return jmespath.compile(jsonPathExpression);
    }
}
