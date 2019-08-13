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

import org.apache.commons.lang3.tuple.Triple;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.CacheLoader;

import io.burt.jmespath.Expression;
import io.burt.jmespath.JmesPath;

public class JMESCacheLoader implements CacheLoader<Triple<JmesPath<JsonNode>, String, String>, JsonNode> {
    @Override
    public JsonNode load(Triple<JmesPath<JsonNode>, String, String> triple) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj;
        String jsonResponse = triple.getMiddle();
        actualObj = mapper.readValue(jsonResponse, JsonNode.class);
        JmesPath<JsonNode> jmespath = triple.getLeft();
        String jsonPathExpression = triple.getRight();
        Expression<JsonNode> expression = jmespath.compile(jsonPathExpression);
        return expression.search(actualObj);
    }
}
