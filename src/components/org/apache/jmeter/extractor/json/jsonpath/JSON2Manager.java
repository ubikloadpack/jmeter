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

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import io.burt.jmespath.Expression;
import io.burt.jmespath.JmesPath;
import io.burt.jmespath.jackson.JacksonRuntime;
/**
 * Handles the extractions
 * https://github.com/jayway/JsonPath/blob/master/json-path/src/test/java/com/jayway/jsonpath/ComplianceTest.java
 * @since 3.0
 */
public class JSON2Manager {

    private static final Logger log = LoggerFactory.getLogger(JSON2Manager.class);
    /**
     * This Map can hardly grow above 10 elements as it is used within JSONPostProcessor to
     * store the computed JsonPath for the set of JSON Path Expressions.
     * Usually there will be 1 to Maximum 10 elements
     */
    private final Map<String, JsonPath> expressionToJsonPath = new HashMap<>(2);
    public void reset() {
        expressionToJsonPath.clear();
    }
    /**
     *
     * @param jsonString JSON String from which data is extracted
     * @param jsonPath   JSON-PATH expression
     * @return List of JSON Strings of the extracted data
     * @throws ParseException when parsing fails
     * @throws IOException
     */
    public List<Object> extractWithJsonPath(String jsonString, String jsonPath) throws IOException {
        JmesPath<JsonNode> jmespath = new JacksonRuntime();
        ObjectMapper mapper = new ObjectMapper();
        List<Object> results = new ArrayList<>();
        JsonNode actualObj;
        try {
            actualObj = mapper.readValue(jsonString, JsonNode.class);
            Expression<JsonNode> expression = jmespath.compile(jsonPath);
            JsonNode result = expression.search(actualObj);
            results.add(result);
        } catch (JsonParseException | JsonMappingException e) {
            if (log.isDebugEnabled()) {
                log.debug("Could not find JSON Path {} in [{}]: {}", jsonPath, jsonString, e.getLocalizedMessage());
            }
            Collections.emptyList();
        }
        return results;
    }

}
