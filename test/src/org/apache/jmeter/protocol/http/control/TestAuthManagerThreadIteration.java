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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.junit.Before;
import org.junit.Test;

public class TestAuthManagerThreadIteration {
    private JMeterContext jmctx;
    private JMeterVariables jmvars;
    private static final String SAME_USER = "__jmv_SAME_USER";

    @Before
    public void setUp() {
        jmctx = JMeterContextService.getContext();
        jmvars = new JMeterVariables();
    }

    @Test
    public void testJmeterVariableCookieWhenThreadIterationIsADifferentUser() {
          AuthManager authManager=new AuthManager();
          authManager.setControlledByThread(false);
          authManager.setClearEachIteration(true);
          assertFalse("Before the iteration, the AuthManager shouldn't be cleared",
                  authManager.authManagerIsCleared);
          authManager.testIterationStart(null);
          assertTrue("After the iteration, the AuthManager should be cleared",
                  authManager.authManagerIsCleared);
          
          jmvars.putObject(SAME_USER, false);
          jmctx.setVariables(jmvars);
          authManager=new AuthManager();
          assertFalse("Before the iteration, the AuthManager shouldn't be cleared",
                  authManager.authManagerIsCleared);
          authManager.setThreadContext(jmctx);
          authManager.setControlledByThread(true);
          authManager.testIterationStart(null);
          assertTrue("After the iteration, the AuthManager should be cleared",
                  authManager.authManagerIsCleared);
    }
    @Test
    public void testJmeterVariableCookieWhenThreadIterationIsASameUser() {
          AuthManager authManager=new AuthManager();
          authManager.setControlledByThread(false);
          authManager.setClearEachIteration(false);
          assertFalse("Before the iteration, the AuthManager shouldn't be cleared",
                  authManager.authManagerIsCleared);
          authManager.testIterationStart(null);
          assertFalse("After the iteration, the AuthManager shouldn't be cleared",
                  authManager.authManagerIsCleared);
          
          jmvars.putObject(SAME_USER, true);
          jmctx.setVariables(jmvars);
          authManager=new AuthManager();
          assertFalse("Before the iteration, the AuthManager shouldn't be cleared",
                  authManager.authManagerIsCleared);
          authManager.setThreadContext(jmctx);
          authManager.setControlledByThread(true);
          authManager.testIterationStart(null);
          assertFalse("After the iteration, the AuthManager shouldn't be cleared",
                  authManager.authManagerIsCleared);
    }


}
