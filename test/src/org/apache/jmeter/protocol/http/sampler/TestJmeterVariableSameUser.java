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
package org.apache.jmeter.protocol.http.sampler;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.jmeter.protocol.http.control.CacheManager;
import org.apache.jmeter.protocol.http.control.CookieManager;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.junit.Before;
import org.junit.Test;

public class TestJmeterVariableSameUser {
    private JMeterContext jmctx;
    private JMeterVariables jmvars;
    @Before
    public void setUp() {
        jmctx = JMeterContextService.getContext();     
        jmvars = new JMeterVariables();
    }
    @Test
    public void testCookieManagerForDifferentUserOnSameIternation() {
        jmvars.putObject("__jmv_SAME_USER", true);
        jmctx.setVariables(jmvars);        
        HTTPSamplerBase sampler = (HTTPSamplerBase) new HttpTestSampleGui().createTestElement();
        CookieManager cookieManager = new CookieManager();
        cookieManager.setControlledByThread(true);
        sampler.setCookieManager(cookieManager);
        sampler.setThreadContext(jmctx);
        boolean res=(boolean) cookieManager.getThreadContext().getVariables().getObject("__jmv_SAME_USER");
        assertTrue("When test different user on the different iternation, the cookie should be cleared",
                res);
    }

    @Test
    public void testCookieManagerForSameUserOnIternation() {
        jmvars.putObject("__jmv_SAME_USER", false);
        jmctx.setVariables(jmvars);        
        HTTPSamplerBase sampler = (HTTPSamplerBase) new HttpTestSampleGui().createTestElement();
        CookieManager cookieManager = new CookieManager();
        cookieManager.setControlledByThread(true);
        sampler.setCookieManager(cookieManager);
        sampler.setThreadContext(jmctx);
        boolean res=(boolean) cookieManager.getThreadContext().getVariables().getObject("__jmv_SAME_USER");
        assertFalse("When test different user on the same iternation, the cookie shouldn't be cleared",
                res);
    }

    @Test
    public void testCacheManagerForDifferentUserOnSameIternation() {
        jmvars.putObject("__jmv_SAME_USER", false);
        jmctx.setVariables(jmvars);        
        HTTPSamplerBase sampler = (HTTPSamplerBase) new HttpTestSampleGui().createTestElement();
        CacheManager cacheManager = new CacheManager();
        cacheManager.setControlledByThread(true);
        sampler.setCacheManager(cacheManager);
        sampler.setThreadContext(jmctx);
        boolean res=(boolean) cacheManager.getThreadContext().getVariables().getObject("__jmv_SAME_USER");
        assertFalse("When test different user on the different iternation, the cache should be cleared",
                res);
    }

    @Test
    public void testCacheManagerForSameUserOnSameIternation() {
        jmvars.putObject("__jmv_SAME_USER", true);
        jmctx.setVariables(jmvars);        
        HTTPSamplerBase sampler = (HTTPSamplerBase) new HttpTestSampleGui().createTestElement();
        CacheManager cacheManager = new CacheManager();
        cacheManager.setControlledByThread(true);
        sampler.setCacheManager(cacheManager);
        sampler.setThreadContext(jmctx);
        boolean res=(boolean) cacheManager.getThreadContext().getVariables().getObject("__jmv_SAME_USER");
        assertTrue("When test different user on the same iternation, the cache shouldn't be cleared",
                res);
    }
}
