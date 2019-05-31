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

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.jmeter.protocol.http.control.CacheManager;
import org.apache.jmeter.protocol.http.control.CookieManager;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.threads.JMeterThread;
import org.junit.Test;

public class TestHTTPHC4Impl {
    public static String url="https://www.google.com";
    @Test
    public void testCookieManagerForDifferentUserOnSameIternation() throws MalformedURLException {
        HTTPSamplerBase sampler = (HTTPSamplerBase) new HttpTestSampleGui().createTestElement();
        CookieManager cookieManager = new CookieManager();
        cookieManager.setControlledByThread(true);
        sampler.setCookieManager(cookieManager);
        HTTPHC4Impl hc = new HTTPHC4Impl(sampler);
        JMeterThread.threadLocal4SameUser.set(false);
        hc.sample(new URL(url), "GET", true, 0);
        assertTrue("When test different user on the different iternation, the cookie should be cleared",
                hc.getCookieManager().getClearEachIteration());
    }

    @Test
    public void testCookieManagerForSameUserOnIternation() throws MalformedURLException {
        HTTPSamplerBase sampler = (HTTPSamplerBase) new HttpTestSampleGui().createTestElement();
        CookieManager cookieManager = new CookieManager();
        cookieManager.setControlledByThread(true);
        sampler.setCookieManager(cookieManager);
        HTTPHC4Impl hc = new HTTPHC4Impl(sampler);
        JMeterThread.threadLocal4SameUser.set(true);
        hc.sample(new URL(url), "GET", true, 0);
        assertFalse("When test different user on the same iternation, the cookie shouldn't be cleared",
                hc.getCookieManager().getClearEachIteration());
    }

    @Test
    public void testCacheManagerForDifferentUserOnSameIternation() throws MalformedURLException {
        HTTPSamplerBase sampler = (HTTPSamplerBase) new HttpTestSampleGui().createTestElement();
        CacheManager cacheManager = new CacheManager();
        cacheManager.setControlledByThread(true);
        sampler.setCacheManager(cacheManager);
        HTTPHC4Impl hc = new HTTPHC4Impl(sampler);
        JMeterThread.threadLocal4SameUser.set(false);
        hc.sample(new URL(url), "GET", true, 0);
        assertTrue("When test different user on the different iternation, the cache should be cleared",
                hc.getCacheManager().getClearEachIteration());
    }

    @Test
    public void testCacheManagerForSameUserOnSameIternation() throws MalformedURLException {
        HTTPSamplerBase sampler = (HTTPSamplerBase) new HttpTestSampleGui().createTestElement();
        CacheManager cacheManager = new CacheManager();
        cacheManager.setControlledByThread(true);
        sampler.setCacheManager(cacheManager);
        HTTPHC4Impl hc = new HTTPHC4Impl(sampler);
        JMeterThread.threadLocal4SameUser.set(true);
        hc.sample(new URL(url), "GET", true, 0);
        assertFalse("When test different user on the same iternation, the cache shouldn't be cleared",
                hc.getCacheManager().getClearEachIteration());
    }
}
