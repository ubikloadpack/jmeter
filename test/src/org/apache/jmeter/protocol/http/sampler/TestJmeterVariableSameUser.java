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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.apache.jmeter.protocol.http.control.CacheManager;
import org.apache.jmeter.protocol.http.control.Cookie;
import org.apache.jmeter.protocol.http.control.CookieManager;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.testelement.property.CollectionProperty;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.junit.Before;
import org.junit.Test;

public class TestJmeterVariableSameUser {
    private JMeterContext jmctx;
    private JMeterVariables jmvars;
    public static String url="http://example.com";
    @Before
    public void setUp() {
        jmctx = JMeterContextService.getContext();     
        jmvars = new JMeterVariables();
    }
    @Test
    public void testJmeterVariableCookieForDifferentUser() {
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
    public void testJmeterVariableCookieForSameUser() {
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
    public void testCookieManagerForDifferentUser()throws NoSuchFieldException,IllegalAccessException  {
        jmvars.putObject("__jmv_SAME_USER", false);
        jmctx.setVariables(jmvars);
        CookieManager cookieManager = new CookieManager();
        cookieManager.setThreadContext(jmctx);
        cookieManager.getCookies().clear();
        cookieManager.testStarted();
        Cookie cookie = new Cookie();
        cookie.setName("test");
        cookieManager.getCookies().addItem(cookie);
        cookieManager.setControlledByThread(true);
        Field privateStringField = CookieManager.class.getDeclaredField("initialCookies");
        privateStringField.setAccessible(true);
        CookieManager cookieManager1 = new CookieManager();
        Cookie cookie1 = new Cookie();
        cookie1.setName("test1");
        cookieManager1.getCookies().addItem(cookie1);
        CollectionProperty initialCookies = cookieManager1.getCookies();
        privateStringField.set(cookieManager, initialCookies);
        assertEquals("Before the iteration, the value of cookie should be what user have set","test",
                cookieManager.getCookies().get(0).getName());
        cookieManager.testIterationStart(null);
        assertEquals("After the iteration, the value of cookie should be the initial cookies", "test1",
                cookieManager.getCookies().get(0).getName());
    }
    @Test
    public void testCookieManagerForSameUser()throws NoSuchFieldException,IllegalAccessException  {
        jmvars.putObject("__jmv_SAME_USER", true);
        jmctx.setVariables(jmvars);
        CookieManager cookieManager = new CookieManager();
        cookieManager.setThreadContext(jmctx);
        cookieManager.getCookies().clear();
        cookieManager.testStarted();
        Cookie cookie = new Cookie();
        cookie.setName("test");
        cookieManager.getCookies().addItem(cookie);
        cookieManager.setControlledByThread(true);
        Field privateStringField = CookieManager.class.getDeclaredField("initialCookies");
        privateStringField.setAccessible(true);
        CookieManager cookieManager1 = new CookieManager();
        Cookie cookie1 = new Cookie();
        cookie1.setName("test1");
        cookieManager1.getCookies().addItem(cookie1);
        CollectionProperty initialCookies = cookieManager1.getCookies();
        privateStringField.set(cookieManager, initialCookies);
        assertEquals("Before the iteration, the value of cookie should be what user have set","test",
                cookieManager.getCookies().get(0).getName());
        cookieManager.testIterationStart(null);
        assertEquals("After the iteration, the value of cookie should be what user have set", "test",
                cookieManager.getCookies().get(0).getName());
    }
    
    @Test
    public void testJmeterVariableCacheManagerForDifferentUser() {
        jmvars.putObject("__jmv_SAME_USER", false);
        jmctx.setVariables(jmvars);
        HTTPSamplerBase sampler = (HTTPSamplerBase) new HttpTestSampleGui().createTestElement();
        CacheManager cacheManager = new CacheManager();
        cacheManager.setControlledByThread(true);
        sampler.setCacheManager(cacheManager);
        sampler.setThreadContext(jmctx);
        boolean res = (boolean) cacheManager.getThreadContext().getVariables().getObject("__jmv_SAME_USER");
        assertFalse("When test different users on the different iternation, the cache should be cleared", res);
    }

    @Test
    public void testJmeterVariableCacheManagerForSameUser() {
        jmvars.putObject("__jmv_SAME_USER", true);
        jmctx.setVariables(jmvars);        
        HTTPSamplerBase sampler = (HTTPSamplerBase) new HttpTestSampleGui().createTestElement();
        CacheManager cacheManager = new CacheManager();
        cacheManager.setControlledByThread(true);
        sampler.setCacheManager(cacheManager);
        sampler.setThreadContext(jmctx);
        boolean res=(boolean) cacheManager.getThreadContext().getVariables().getObject("__jmv_SAME_USER");
        assertTrue("When test different users on the same iternation, the cache shouldn't be cleared",
                res);
    }


}
