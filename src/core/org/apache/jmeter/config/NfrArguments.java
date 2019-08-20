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
package org.apache.jmeter.config;

 import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.jmeter.testelement.property.CollectionProperty;
import org.apache.jmeter.testelement.property.JMeterProperty;
import org.apache.jmeter.testelement.property.PropertyIterator;
import org.apache.jmeter.testelement.property.TestElementProperty;

 /**
 * A set of NfrArgument objects.
 *
 */
public class NfrArguments extends ConfigTestElement implements Serializable, Iterable<JMeterProperty> {
    private static final long serialVersionUID = 240L;
    /** The name of the property used to store the nfrarguments. */
    public static final String NFRARGUMENTS = "NfrArguments.nfrarguments"; //$NON-NLS-1$

     /**
     * Create a new NfrArguments object with no arguments.
     */
    public NfrArguments() {
        setProperty(new CollectionProperty(NFRARGUMENTS, new ArrayList<NfrArgument>()));
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
     * Clear the nfrarguments.
     */
    @Override
    public void clear() {
        super.clear();
        setProperty(new CollectionProperty(NFRARGUMENTS, new ArrayList<NfrArgument>()));
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
     * Add a new argument with the given name, value, metadata and description
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
    @Override
    public PropertyIterator iterator() {
        return getNfrArguments().iterator();
    }

     /**
     * Create a string representation of the nfrarguments.
     *
     * @return the string representation of the nfrarguments
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        PropertyIterator iter = getNfrArguments().iterator();
        while (iter.hasNext()) {
            NfrArgument arg = (NfrArgument) iter.next().getObjectValue();
            final String criteria = arg.getCriteria();
            str.append(arg.getName());
            if (criteria == null) {
                str.append("="); //$NON-NLS-1$
            } else {
                str.append(criteria);
            }
            str.append(arg.getValue());
            final String symbol = arg.getSymbol();
            if (symbol != null) {
                str.append("(");
                str.append(symbol);
                str.append(")");
            }
            if (iter.hasNext()) {
                str.append("&"); //$NON-NLS-1$
            }
        }
        return str.toString();
    }

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
        getNfrArguments().clear();
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
}