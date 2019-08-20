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

import org.apache.jmeter.assertions.gui.JmesPathAssertionGui;
import org.apache.jmeter.testelement.TestElement;
import org.junit.Test;


public class JmesPathAssertionGuiTest {


    @Test
    public void testInit() {
        JmesPathAssertionGui instance = new JmesPathAssertionGui();
        instance.init();
        instance.stateChanged(null);
    }

    @Test
    public void testClearGui() {
    	JmesPathAssertionGui instance = new JmesPathAssertionGui();
        instance.clearGui();
    }

    @Test
    public void testCreateTestElement() {
    	JmesPathAssertionGui instance = new JmesPathAssertionGui();
        instance.createTestElement();
    }

    @Test
    public void testGetLabelResource() {
    	JmesPathAssertionGui instance = new JmesPathAssertionGui();
        instance.getLabelResource();
    }

    @Test
    public void testGetStaticLabel() {
    	JmesPathAssertionGui instance = new JmesPathAssertionGui();
        instance.getStaticLabel();
    }

    @Test
    public void testModifyTestElement() {
        TestElement element = new JmesPathAssertion();
        JmesPathAssertionGui instance = new JmesPathAssertionGui();
        instance.modifyTestElement(element);
    }

    @Test
    public void testConfigure() {
        TestElement element = new JmesPathAssertion();
        JmesPathAssertionGui instance = new JmesPathAssertionGui();
        instance.configure(element);
    }
}
