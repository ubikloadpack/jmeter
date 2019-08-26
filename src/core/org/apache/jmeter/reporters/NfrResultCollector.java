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
package org.apache.jmeter.reporters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.jmeter.config.NfrArgument;
import org.apache.jmeter.engine.util.NoThreadClone;
import org.apache.jmeter.samplers.Clearable;
import org.apache.jmeter.samplers.Remoteable;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleListener;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.SampleSaveConfiguration;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.testelement.property.CollectionProperty;
import org.apache.jmeter.testelement.property.ObjectProperty;
import org.apache.jmeter.testelement.property.PropertyIterator;
import org.apache.jmeter.testelement.property.TestElementProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles all saving of samples. The class must be thread-safe
 * because it is shared between threads (NoThreadClone).
 */
public class NfrResultCollector extends AbstractListenerElement
        implements SampleListener, Clearable, Serializable, TestStateListener, Remoteable, NoThreadClone {

    private static final class ShutdownHook implements Runnable {
        @Override
        public void run() {
            log.info("Shutdown hook started");
            log.info("Shutdown hook ended");
        }
    }

    private static final Logger log = LoggerFactory.getLogger(NfrResultCollector.class);
    private static final long serialVersionUID = 234L;
    // This string is used to identify local test runs, so must not be a valid host
    // name
    private static final String TEST_IS_LOCAL = "*local*"; // $NON-NLS-1$
    private static final String SAVE_CONFIG = "saveConfig"; // $NON-NLS-1$
    // Static variables
    // Lock used to guard static mutable variables
    private static final Object LOCK = new Object();
    /**
     * Shutdown Hook that ensures PrintWriter is flushed is CTRL+C or kill is called
     * during a test
     */
    private static Thread shutdownHook;
    /**
     * The instance count is used to keep track of whether any tests are currently
     * running. It's not possible to use the constructor or threadStarted etc as
     * tests may overlap e.g. a remote test may be started, and then a local test
     * started whilst the remote test is still running.
     */
    private static int instanceCount; // Keep track of how many instances are active
    /**
     * Is a test running ?
     */
    private volatile boolean inTest = false;
    private volatile boolean isStats = false;
    /** the summarizer to which this result collector will forward the samples */
    private volatile Summariser summariser;
    public static final String NFRARGUMENTS = "NfrResultCollector.nfrarguments"; //$NON-NLS-1$

    /**
     * No-arg constructor.
     */
    public NfrResultCollector() {
        this(null);
    }

    /**
     * Constructor which sets the used {@link Summariser}
     * 
     * @param summer The {@link Summariser} to use
     */
    public NfrResultCollector(Summariser summer) {
        setProperty(new ObjectProperty(SAVE_CONFIG, new SampleSaveConfiguration()));
        setProperty(new CollectionProperty(NFRARGUMENTS, new ArrayList<NfrArgument>()));
        summariser = summer;
    }

    // Ensure that the sample save config is not shared between copied nodes
    // N.B. clone only seems to be used for client-server tests
    @Override
    public Object clone() {
        NfrResultCollector clone = (NfrResultCollector) super.clone();
        clone.setSaveConfig((SampleSaveConfiguration) clone.getSaveConfig().clone());
        // Unfortunately AbstractTestElement does not call super.clone()
        clone.summariser = this.summariser;
        return clone;
    }
    /**
     * Get the nfrarguments.
     *
     * @return the nfrarguments
     */
    public CollectionProperty getNfrArguments() {
        return (CollectionProperty) getProperty(NFRARGUMENTS);
    }

    /**
     * Set the list of nfrarguments. Any existing arguments will be lost.
     *
     * @param arguments the new arguments
     */
    public void setNfrArguments(List<NfrArgument> arguments) {
        setProperty(new CollectionProperty(NFRARGUMENTS, arguments));
    }

    /**
     * Get the arguments as a Map. Each argument name is used as the key, and its
     * value as the value.
     *
     * @return a new Map with String keys and values containing the arguments
     */
    public Map<String, String> getNfrArgumentsAsMap() {
        PropertyIterator iter = getNfrArguments().iterator();
        Map<String, String> argMap = new LinkedHashMap<>();
        while (iter.hasNext()) {
            NfrArgument arg = (NfrArgument) iter.next().getObjectValue();
            // Because CollectionProperty.mergeIn will not prevent adding two
            // properties of the same name, we need to select the first value so
            // that this element's values prevail over defaults provided by
            // configuration
            // elements:
            if (!argMap.containsKey(arg.getName())) {
                argMap.put(arg.getName(), arg.getValue());
            }
        }
        return argMap;
    }

    /**
     * Add a new argument.
     *
     * @param arg the new argument
     */
    public void addNfrArgument(NfrArgument arg) {
        TestElementProperty newArg = new TestElementProperty(arg.getName(), arg);
        if (isRunningVersion()) {
            this.setTemporary(newArg);
        }
        getNfrArguments().addItem(newArg);
    }

    /**
     * Add a new argument with the given name, value, criteria and symbol
     * 
     * @param name
     * @param criteria
     * @param symbol
     * @param value
     */
    public void addNfrArgument(String name, String criteria, String symbol, String value) {
        addNfrArgument(new NfrArgument(name, criteria, symbol, value));
    }

    /**
     * Get a PropertyIterator of the nfrarguments.
     *
     * @return an iteration of the nfrarguments
     */
    public PropertyIterator iterator() {
        return getNfrArguments().iterator();
    }

    /**
     * Create a string representation of the nfrarguments.
     *
     * @return the string representation of the nfrarguments
     */
    /**
     * Remove the specified argument from the list.
     *
     * @param row the index of the argument to remove
     */
    public void removeNfrArgument(int row) {
        if (row < getNfrArguments().size()) {
            getNfrArguments().remove(row);
        }
    }

    /**
     * Remove the specified argument from the list.
     *
     * @param arg the argument to remove
     */
    public void removeNfrArgument(NfrArgument arg) {
        PropertyIterator iter = getNfrArguments().iterator();
        while (iter.hasNext()) {
            NfrArgument item = (NfrArgument) iter.next().getObjectValue();
            if (arg.equals(item)) {
                iter.remove();
            }
        }
    }

    /**
     * Remove the argument with the specified name.
     *
     * @param argName the name of the argument to remove
     */
    public void removeNfrArgument(String argName) {
        PropertyIterator iter = getNfrArguments().iterator();
        while (iter.hasNext()) {
            NfrArgument arg = (NfrArgument) iter.next().getObjectValue();
            if (arg.getName().equals(argName)) {
                iter.remove();
            }
        }
    }

    /**
     * Remove the argument with the specified name and value.
     *
     * @param argName  the name of the argument to remove
     * @param argValue the value to compare - must not be null
     */
    public void removeNfrArgument(String argName, String argValue) {
        PropertyIterator iter = getNfrArguments().iterator();
        while (iter.hasNext()) {
            NfrArgument arg = (NfrArgument) iter.next().getObjectValue();
            if (arg.getName().equals(argName) && argValue.equals(arg.getValue())) {
                iter.remove();
            }
        }
    }

    /**
     * Remove all arguments from the list.
     */
    public void removeAllNfrArguments() {
        setProperty(new CollectionProperty(NFRARGUMENTS, new ArrayList<NfrArgument>()));
    }

    /**
     * Add a new empty argument to the list. The new argument will have the empty
     * string as its name and value, and null metadata.
     */
    public void addEmptyNfrArgument() {
        addNfrArgument(new NfrArgument("", "", "", ""));
    }

    /**
     * Get the number of arguments in the list.
     *
     * @return the number of arguments
     */
    public int getNfrArgumentCount() {
        return getNfrArguments().size();
    }

    /**
     * Get a single argument.
     *
     * @param row the index of the argument to return.
     * @return the argument at the specified index, or null if no argument exists at
     *         that index.
     */
    public NfrArgument getNfrArgument(int row) {
        NfrArgument argument = null;
        if (row < getNfrArguments().size()) {
            argument = (NfrArgument) getNfrArguments().get(row).getObjectValue();
        }
        return argument;
    }

    @Override
    public void testEnded(String host) {
        synchronized (LOCK) {
            instanceCount--;
            if (instanceCount <= 0) {
                // No need for the hook now
                // Bug 57088 - prevent (im?)possible NPE
                if (shutdownHook != null) {
                    Runtime.getRuntime().removeShutdownHook(shutdownHook);
                } else {
                    log.warn("Should not happen: shutdownHook==null, instanceCount={}", instanceCount);
                }
                inTest = false;
            }
        }
        if (summariser != null) {
            summariser.testEnded(host);
        }
    }

    @Override
    public void testStarted(String host) {
        synchronized (LOCK) {
            if (instanceCount == 0) { // Only add the hook once
                shutdownHook = new Thread(new ShutdownHook());
                Runtime.getRuntime().addShutdownHook(shutdownHook);
            }
            instanceCount++;
            try {
                if (getVisualizer() != null) {
                    this.isStats = getVisualizer().isStats();
                }
            } catch (Exception e) {
                log.error("Exception occurred while initializing file output.", e);
            }
        }
        inTest = true;
        if (summariser != null) {
            summariser.testStarted(host);
        }
    }

    @Override
    public void testEnded() {
        testEnded(TEST_IS_LOCAL);
    }

    @Override
    public void testStarted() {
        testStarted(TEST_IS_LOCAL);
    }

    @Override
    public void sampleStarted(SampleEvent e) {
        // NOOP
    }

    @Override
    public void sampleStopped(SampleEvent e) {
        // NOOP
    }

    /**
     * When a test result is received, display it and save it.
     *
     * @param event the sample event that was received
     */
    @Override
    public void sampleOccurred(SampleEvent event) {
        SampleResult result = event.getResult();
        sendToVisualizer(result);
        if (!this.isStats) {
            SampleSaveConfiguration config = getSaveConfig();
            result.setSaveConfig(config);
        }
        if (summariser != null) {
            summariser.sampleOccurred(event);
        }
    }

    protected final void sendToVisualizer(SampleResult r) {
        if (getVisualizer() != null) {
            getVisualizer().add(r);
        }
    }
    /**
     * @return Returns the saveConfig.
     */
    public SampleSaveConfiguration getSaveConfig() {
        try {
            return (SampleSaveConfiguration) getProperty(SAVE_CONFIG).getObjectValue();
        } catch (ClassCastException e) {
            setSaveConfig(new SampleSaveConfiguration());
            return getSaveConfig();
        }
    }

    /**
     * @param saveConfig The saveConfig to set.
     */
    public void setSaveConfig(SampleSaveConfiguration saveConfig) {
        getProperty(SAVE_CONFIG).setObjectValue(saveConfig);
    }

    // This is required so that
    // @see org.apache.jmeter.gui.tree.JMeterTreeModel.getNodesOfType()
    // can find the Clearable nodes - the userObject has to implement the interface.
    @Override
    public void clearData() {
        super.clear();
    }
}
