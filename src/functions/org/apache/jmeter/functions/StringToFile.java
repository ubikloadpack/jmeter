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

package org.apache.jmeter.functions;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FileToString Function to read a complete file into a String.
 *
 * Parameters: - file name - file encoding (optional) - variable name (optional)
 *
 * Returns: - the whole text from a file - or **ERR** if an error occurs - value
 * is also optionally saved in the variable for later re-use.
 *
 * @since 2.4
 */
public class StringToFile extends AbstractFunction {
    private static final Logger log = LoggerFactory.getLogger(StringToFile.class);

    private static final List<String> desc = new LinkedList<>();

    private static final String KEY = "__StringToFile";//$NON-NLS-1$

    static final String ERR_IND = "**ERR**";//$NON-NLS-1$

    static String myValue = ERR_IND;

    static {
        desc.add(JMeterUtils.getResString("string_from_file_file_name"));
        desc.add("Charset (optional)");//$NON-NLS-1$
        desc.add("The value of String");//$NON-NLS-1$
        desc.add("Append or overwrite (append or overwrite,default append, optional)");//$NON-NLS-1$
    }
    private Object[] values;

    public StringToFile() {
    }

    /**
     * 
     */
    private synchronized void openFile() {

        String tn = Thread.currentThread().getName();
        String fileName = ((CompoundVariable) values[0]).execute();
        String charcode = ((CompoundVariable) values[1]).execute();
        String content = ((CompoundVariable) values[2]).execute() + System.lineSeparator();
        String myName = "res";
        String optionalWriter = ((CompoundVariable) values[3]).execute().toLowerCase().trim();
        Boolean optionalWriterBool = true;
        if (optionalWriter.equals("overwrite")) {
            optionalWriterBool = false;
        }
        JMeterVariables vars = getVariables();
        Charset cst = Charset.defaultCharset();
        if (charcode.trim().length() > 0) {
            cst = Charset.forName(charcode);
        }
        log.info("the encoding is {}", cst.toString());
        try {
            File file = new File(fileName);
            FileUtils.writeStringToFile(file, content, cst, optionalWriterBool);
            myValue = "<StringToFile> true";
        } catch (IOException e) {
          log.warn("Could not read file: " + fileName + " " + e.getMessage(), e);
        }

        if (myName.length() > 0) {
            if (vars != null) {
             vars.put(myName, myValue);
            }
        }

        if (log.isDebugEnabled()) {
            tn = Thread.currentThread().getName();
            log.debug(tn + " name:" //$NON-NLS-1$
                 + myName + " value:" + myValue);//$NON-NLS-1$
        }
    
    }

    /** {@inheritDoc} */
    @Override
     public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
            throws InvalidVariableException {
            this.openFile();
            return myValue;
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
        checkParameterCount(parameters, 2, 4);
        values = parameters.toArray();
    }

    /** {@inheritDoc} */
    @Override
    public String getReferenceKey() {
        return KEY;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getArgumentDesc() {
        return desc;
    }
}
