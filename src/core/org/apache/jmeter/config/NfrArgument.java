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

import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.testelement.property.StringProperty;

/**
 * Class representing an argument. Each argument consists of a name/value pair,
 * as well as (optional) metadata.
 *
 */
public class NfrArgument extends AbstractTestElement implements Serializable {
    private static final long serialVersionUID = 240L;

    /** Name used to store the argument's name. */
    public static final String ARG_NAME = "NfrArgument.name"; // $NON-NLS-1$

    /** Name used to store the argument's value. */
    public static final String VALUE = "NfrArgument.value"; // $NON-NLS-1$
    
    /** Name used to store the argument's value. */
    public static final String CRITERIA = "NfrArgument.criteria"; // $NON-NLS-1$
    
    /** Name used to store the argument's value. */
    public static final String SYMBOL = "NfrArgument.symbol"; // $NON-NLS-1$

    /**
     * Create a new Argument with the specified name, value, and metadata.
     *
     * @param name
     *            the argument name
     * @param value
     *            the argument value
     * @param metadata
     *            the argument metadata
     * @param description
     *            the argument description
     */
    public NfrArgument(String name,  String criteria, String symbol, String value) {
        if(name != null) {
            setProperty(new StringProperty(ARG_NAME, name));
        }
        if(value != null) {
            setProperty(new StringProperty(VALUE, value));
        }
        if(symbol != null) {
            setProperty(new StringProperty(SYMBOL, symbol));
        }
        if(criteria != null) {
            setProperty(new StringProperty(CRITERIA, criteria));
        }
    }

    /**
     * Set the name of the Argument.
     *
     * @param newName
     *            the new name
     */
    @Override
    public void setName(String newName) {
        setProperty(new StringProperty(ARG_NAME, newName));
    }

    /**
     * Get the name of the Argument.
     *
     * @return the attribute's name
     */
    @Override
    public String getName() {
        return getPropertyAsString(ARG_NAME);
    }

    /**
     * Sets the value of the Argument.
     *
     * @param newValue
     *            the new value
     */
    public void setValue(String newValue) {
        setProperty(new StringProperty(VALUE, newValue));
    }

    /**
     * Gets the value of the Argument object.
     *
     * @return the attribute's value
     */
    public String getValue() {
        return getPropertyAsString(VALUE);
    }
    public void setCriteria(String criteria) {
        setProperty(new StringProperty(VALUE, criteria));
    }

    /**
     * Gets the Meta Data attribute of the Argument.
     *
     * @return the MetaData value
     */
    public String getCriteria() {
        return getPropertyAsString(CRITERIA);
    }

    /**
     * Sets the Meta Data attribute of the Argument.
     *
     * @param newMetaData
     *            the new metadata
     */
    public void setSymbol(String symbol) {
        setProperty(new StringProperty(SYMBOL, symbol));
    }

    /**
     * the signe of nfr test
     * @return
     */
    public String getSymbol() {
        return getPropertyAsString(SYMBOL);
    }

}
