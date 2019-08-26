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

public class NfrArgument extends AbstractTestElement implements Serializable {
    @Override
    public String toString() {
        return "NfrArgument [getName()=" + getName() + ", getValue()=" + getValue() + ", getCriteria()=" + getCriteria()
                + ", getSymbol()=" + getSymbol() + "]";
    }

    private static final long serialVersionUID = 240L;

     /** Name used to store the argument's name. */
    public static final String NAME = "NfrArgument.name"; // $NON-NLS-1$

     /** Name used to store the argument's value. */
    public static final String VALUE = "NfrArgument.value"; // $NON-NLS-1$

     /** Name used to store the argument's criteria. */
    public static final String CRITERIA = "NfrArgument.criteria"; // $NON-NLS-1$

     /** Name used to store the argument's symbol. */
    public static final String SYMBOL = "NfrArgument.symbol"; // $NON-NLS-1$
     /**
     * Set the name of the Argument.
     *
     * @param newName
     *            the new name
     */
    @Override
    public void setName(String newName) {
        setProperty(new StringProperty(NAME, newName));
    }

     /**
     * Get the name of the Argument.
     *
     * @return the attribute's name
     */
    @Override
    public String getName() {
        return getPropertyAsString(NAME);
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
    
    /**
     * Sets the criteria of the Argument.
     * 
     * @param newCriteria
     */
    public void setCriteria(String newCriteria) {
        setProperty(new StringProperty(CRITERIA, newCriteria));
    }

     /**
     * Gets the criteria attribute of the Argument.
     *
     * @return the criteria value
     */
    public String getCriteria() {
        return getPropertyAsString(CRITERIA);
    }

     /**
     * Sets the Symbol attribute of the Argument.
     *
     * @param newSymbol
     *            the new symbol
     */
    public void setSymbol(String symbol) {
        setProperty(new StringProperty(SYMBOL, symbol));
    }

     /**
     * Gets the symbol of NFR test
     * 
     * @return the attribute's symbol
     */
    public String getSymbol() {
        return getPropertyAsString(SYMBOL);
    }
    /**
    * Create a new Argument with the specified name, value, symbol, criteria.
    *
    * @param name
    *            the argument name
    * @param value
    *            the argument value
    * @param symbol
    *            the argument symbol
    * @param criteria
    *            the argument criteria
    */
   public NfrArgument(String name,  String criteria, String symbol, String value) {
       if(name != null) {
           setProperty(new StringProperty(NAME, name));
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

 }