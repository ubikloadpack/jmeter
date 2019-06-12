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

package org.apache.jmeter.protocol.http.control;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicHeader;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.sampler.HTTPSampleResult;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
import org.apache.jmeter.protocol.http.util.HTTPConstants;
import org.apache.jmeter.protocol.http.util.HTTPConstantsInterface;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test {@link CacheManager} that uses HTTPHC4Impl
 */
public class TestCacheManagerIternation extends TestCacheManagerBase {
    private JMeterContext jmctx;
    private JMeterVariables jmvars;
    private class HttpResponseStub extends AbstractHttpMessage implements HttpResponse {
        private org.apache.http.Header lastModifiedHeader;
        private org.apache.http.Header etagHeader;
        private String expires;
        private String cacheControl;
        private org.apache.http.Header dateHeader;

        private List<org.apache.http.Header> headers;

        public HttpResponseStub() {
            this.headers = new ArrayList<>();
            this.lastModifiedHeader = new BasicHeader(HTTPConstants.LAST_MODIFIED, currentTimeInGMT);
            this.dateHeader = new BasicHeader(HTTPConstants.DATE, currentTimeInGMT);
            this.etagHeader = new BasicHeader(HTTPConstants.ETAG, EXPECTED_ETAG);
        }

        /* (non-Javadoc)
         * @see org.apache.http.message.AbstractHttpMessage#getAllHeaders()
         */
        @Override
        public org.apache.http.Header[] getAllHeaders() {
            return headers.toArray(new org.apache.http.Header[headers.size()]);
        }

        /* (non-Javadoc)
         * @see org.apache.http.message.AbstractHttpMessage#addHeader(org.apache.http.Header)
         */
        @Override
        public void addHeader(org.apache.http.Header header) {
            headers.add(header);
        }

        /* (non-Javadoc)
         * @see org.apache.http.message.AbstractHttpMessage#getFirstHeader(java.lang.String)
         */
        @Override
        public Header getFirstHeader(String headerName) {
            Header[] headers = getHeaders(headerName);
            if (headers.length > 0) {
                return headers[0];
            }
            return null;
        }

        /* (non-Javadoc)
         * @see org.apache.http.message.AbstractHttpMessage#getLastHeader(java.lang.String)
         */
        @Override
        public Header getLastHeader(String headerName) {
            Header[] headers = getHeaders(headerName);
            if (headers.length > 0) {
                return headers[headers.length - 1];
            }
            return null;
        }

        /* (non-Javadoc)
         * @see org.apache.http.message.AbstractHttpMessage#getHeaders(java.lang.String)
         */
        @Override
        public Header[] getHeaders(String headerName) {
            org.apache.http.Header header = null;
            if (HTTPConstants.LAST_MODIFIED.equals(headerName)) {
                header = this.lastModifiedHeader;
            } else if (HTTPConstants.ETAG.equals(headerName)) {
                header = this.etagHeader;
            } else if (HTTPConstants.EXPIRES.equals(headerName)) {
                header = expires == null ? null : new BasicHeader(HTTPConstants.EXPIRES, expires);
            } else if (HTTPConstants.CACHE_CONTROL.equals(headerName)) {
                header = cacheControl == null ? null : new BasicHeader(HTTPConstants.CACHE_CONTROL, cacheControl);
            } else if (HTTPConstants.DATE.equals(headerName)) {
                header = this.dateHeader;
            } else if (HTTPConstants.VARY.equals(headerName)) {
                header = vary == null ? null : new BasicHeader(HTTPConstants.VARY, vary);
            }
            if (header != null) {
                return new org.apache.http.Header[]{header};
            } else {
                return super.getHeaders(headerName);
            }
        }

        @Override
        public ProtocolVersion getProtocolVersion() {
            return null;
        }

        @Override
        public StatusLine getStatusLine() {
            return null;
        }

        @Override
        public void setStatusLine(StatusLine statusline) {
        }

        @Override
        public void setStatusLine(ProtocolVersion ver, int code) {
        }

        @Override
        public void setStatusLine(ProtocolVersion ver, int code, String reason) {
        }

        @Override
        public void setStatusCode(int code) throws IllegalStateException {
        }

        @Override
        public void setReasonPhrase(String reason) throws IllegalStateException {
        }

        @Override
        public HttpEntity getEntity() {
            return null;
        }

        @Override
        public void setEntity(HttpEntity entity) {
        }

        @Override
        public Locale getLocale() {
            return null;
        }

        @Override
        public void setLocale(Locale loc) {
        }
    }

    private class HttpPostStub extends HttpPost {

        HttpPostStub() {
        }

        @Override
        public java.net.URI getURI() {
            try {
                return url.toURI();
            } catch (URISyntaxException e) {
                throw new IllegalStateException();
            }
        }
    }

    private HttpRequestBase httpMethod;
    private HttpResponse httpResponse;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.httpMethod = new HttpPostStub();
        this.httpResponse = new HttpResponseStub();
        this.httpMethod.setURI(this.url.toURI());
        jmctx = JMeterContextService.getContext();
        jmvars = new JMeterVariables();
    }

    @Override
    public void tearDown() throws Exception {
        this.httpMethod = null;
        this.httpResponse = null;
        super.tearDown();
    }

    @Override
    protected void setExpires(String expires) {
        ((HttpResponseStub) httpResponse).expires = expires;
    }

    @Override
    protected void setCacheControl(String cacheControl) {
        ((HttpResponseStub) httpResponse).cacheControl = cacheControl;
    }

    @Override
    protected void setLastModified(String lastModified) {
        ((HttpResponseStub) httpResponse).lastModifiedHeader =
                new BasicHeader(HTTPConstants.LAST_MODIFIED, lastModified);
    }

    @Override
    protected void cacheResult(HTTPSampleResult result) {
        this.cacheManager.saveDetails(httpResponse, result);
    }

    @Override
    protected void addRequestHeader(String requestHeader, String value) {
        this.httpMethod.addHeader(new BasicHeader(requestHeader, value));
    }

    @Override
    protected void setRequestHeaders() {
        this.cacheManager.setHeaders(this.url, this.httpMethod);
    }

    @Override
    protected void checkRequestHeader(String requestHeader, String expectedValue) {
        org.apache.http.Header header = this.httpMethod.getLastHeader(requestHeader);
        assertEquals("Wrong name in header for " + requestHeader, requestHeader, header.getName());
        assertEquals("Wrong value for header " + header, expectedValue, header.getValue());
    }

    protected void checkIfModifiedSinceHeader(HttpRequestBase httpMethod) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        try {
            assertEquals("Should have found 1 header "+HTTPConstantsInterface.IF_MODIFIED_SINCE, 
                    1, 
                    httpMethod.getHeaders(HTTPConstantsInterface.IF_MODIFIED_SINCE).length);
            Date date = dateFormat.parse(httpMethod.getHeaders(HTTPConstantsInterface.IF_MODIFIED_SINCE)[0].getValue());
            assertNotNull("Should have found a valid entry", date);
        } catch(ParseException e) {
            Assert.fail("Invalid header format for:"+ HTTPConstantsInterface.IF_MODIFIED_SINCE);
        }
    }

    @Test
    public void testJmeterVariableCacheForDifferentUser() {
        jmvars.putObject("__jmv_SAME_USER", true);
        jmctx.setVariables(jmvars);
        HTTPSamplerBase sampler = (HTTPSamplerBase) new HttpTestSampleGui().createTestElement();
        CacheManager cacheManager = new CacheManager();
        cacheManager.setControlledByThread(true);
        sampler.setCacheManager(cacheManager);
        sampler.setThreadContext(jmctx);
        boolean res = (boolean) cacheManager.getThreadContext().getVariables().getObject("__jmv_SAME_USER");
        assertTrue("When test different user on the different iternation, the cache should be cleared", res);
    }
    @Test
    public void testJmeterVariableCacheForSameUser() {
        jmvars.putObject("__jmv_SAME_USER", false);
        jmctx.setVariables(jmvars);
        HTTPSamplerBase sampler = (HTTPSamplerBase) new HttpTestSampleGui().createTestElement();
        CacheManager cacheManager = new CacheManager();
        cacheManager.setControlledByThread(true);
        sampler.setCacheManager(cacheManager);
        sampler.setThreadContext(jmctx);
        boolean res = (boolean) cacheManager.getThreadContext().getVariables().getObject("__jmv_SAME_USER");
        assertFalse("When test different user on the different iternation, the cache shouldn't be cleared", res);
    }

    @Test
    public void testCacheControlDifferentUser() throws Exception {
        jmvars.putObject("__jmv_SAME_USER", false);
        jmctx.setVariables(jmvars);        
        this.cacheManager.setUseExpires(true);
        this.cacheManager.testIterationStart(null);
        assertNull("Should not find entry", getThreadCacheEntry(LOCAL_HOST));
        Header[] headers=new Header[1];
        assertFalse("Should not find valid entry", this.cacheManager.inCache(url,headers));
        long start = System.currentTimeMillis();
        setExpires(makeDate(new Date(start)));
        setCacheControl("public, max-age=1");
        cacheResult(sampleResultOK);
        this.cacheManager.setThreadContext(jmctx);
        this.cacheManager.setControlledByThread(true);
        assertNotNull("Before iternation, should find entry", getThreadCacheEntry(LOCAL_HOST));
        assertTrue("Before iternation, should find valid entry", this.cacheManager.inCache(url,headers));
        this.cacheManager.testIterationStart(null);
        assertNull("After iterantion, should not find entry", getThreadCacheEntry(LOCAL_HOST));
        assertFalse("After iterantion, should not find valid entry", this.cacheManager.inCache(url,headers));
    }
    @Test
    public void testCacheControlSameUser() throws Exception {
        jmvars.putObject("__jmv_SAME_USER", true);
        jmctx.setVariables(jmvars);        
        this.cacheManager.setUseExpires(true);
        this.cacheManager.testIterationStart(null);
        assertNull("Should not find entry", getThreadCacheEntry(LOCAL_HOST));
        Header[] headers=new Header[1];
        assertFalse("Should not find valid entry", this.cacheManager.inCache(url,headers));
        long start = System.currentTimeMillis();
        setExpires(makeDate(new Date(start)));
        setCacheControl("public, max-age=1");
        cacheResult(sampleResultOK);
        this.cacheManager.setThreadContext(jmctx);
        this.cacheManager.setControlledByThread(true);
        assertNotNull("Before iternation, should find entry", getThreadCacheEntry(LOCAL_HOST));
        assertTrue("Before iternation, should find valid entry", this.cacheManager.inCache(url,headers));
        this.cacheManager.testIterationStart(null);
        assertNotNull("After iterantion, should find entry", getThreadCacheEntry(LOCAL_HOST));
        assertTrue("After iterantion, should find valid entry", this.cacheManager.inCache(url,headers));
    }

    @Test
    public void testCacheControlClear() throws Exception {
        this.cacheManager.setUseExpires(true);
        this.cacheManager.testIterationStart(null);
        assertNull("Should not find entry", getThreadCacheEntry(LOCAL_HOST));
        Header[] headers = new Header[1];
        assertFalse("Should not find valid entry", this.cacheManager.inCache(url, headers));
        long start = System.currentTimeMillis();
        setExpires(makeDate(new Date(start)));
        setCacheControl("public, max-age=1");
        cacheResult(sampleResultOK);
        assertNotNull("Before iternation, should find entry", getThreadCacheEntry(LOCAL_HOST));
        assertTrue("Before iternation, should find valid entry", this.cacheManager.inCache(url, headers));
        this.cacheManager.setClearEachIteration(true);
        this.cacheManager.testIterationStart(null);
        assertNull("After iterantion, should not find entry", getThreadCacheEntry(LOCAL_HOST));
        assertFalse("After iterantion, should not find valid entry", this.cacheManager.inCache(url, headers));
    }
    
    @Test
    public void testCacheControlNotClear() throws Exception {
        this.cacheManager.setUseExpires(true);
        this.cacheManager.testIterationStart(null);
        assertNull("Should not find entry", getThreadCacheEntry(LOCAL_HOST));
        Header[] headers=new Header[1];
        assertFalse("Should not find valid entry", this.cacheManager.inCache(url,headers));
        long start = System.currentTimeMillis();
        setExpires(makeDate(new Date(start)));
        setCacheControl("public, max-age=1");
        cacheResult(sampleResultOK);
        assertNotNull("Before iternation, should find entry", getThreadCacheEntry(LOCAL_HOST));
        assertTrue("Before iternation, should find valid entry", this.cacheManager.inCache(url,headers));
        this.cacheManager.testIterationStart(null);
        this.cacheManager.setClearEachIteration(true);
        assertNotNull("After iterantion, should find entry", getThreadCacheEntry(LOCAL_HOST));
        assertTrue("After iterantion, should find valid entry", this.cacheManager.inCache(url,headers));
    }

}
