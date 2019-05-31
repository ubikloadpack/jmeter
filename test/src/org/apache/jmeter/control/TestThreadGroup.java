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
package org.apache.jmeter.control;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.jmeter.junit.JMeterTestCase;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterThread;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jmeter.threads.ListenerNotifier;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jorphan.collections.ListedHashTree;
import org.junit.Before;
import org.junit.Test;

public class TestThreadGroup extends JMeterTestCase {
    ListedHashTree hashTree = new ListedHashTree();
    ThreadGroup threadGroup = new ThreadGroup();
    ListenerNotifier notifier = new ListenerNotifier();
    @Before
    public void setup() {
        JMeterContextService.getContext().setVariables(new JMeterVariables());
        TransactionController transactionController = new TransactionController();
        transactionController.setGenerateParentSample(true);
        LoopController loop = new LoopController();
        loop.setLoops(1);
        loop.setContinueForever(false);
        hashTree.add(loop);
        hashTree.add(loop, transactionController);
        threadGroup.setNumThreads(1);

    }

    @Test
    public void testThreadLoaclForSameUserOnIternation() {
        JMeterThread thread = new JMeterThread(hashTree, threadGroup, notifier, true);
        thread.setThreadGroup(threadGroup);
        thread.setOnErrorStopThread(true);
        thread.run();
        assertTrue(
                "When set same user on eatch iteration, the value of threadlocal 'threadLocal4SameUser' should be true ",
                JMeterThread.threadLocal4SameUser.get());
    }
    
    @Test
    public void testThreadLoaclForDifferentUserOnIternation() {
        JMeterThread thread = new JMeterThread(hashTree, threadGroup, notifier, false);
        thread.setThreadGroup(threadGroup);
        thread.setOnErrorStopThread(true);
        thread.run();
        assertFalse(
                "When set same user on eatch iteration, the value of threadlocal 'threadLocal4SameUser' should be false",
                JMeterThread.threadLocal4SameUser.get());
    }
}
