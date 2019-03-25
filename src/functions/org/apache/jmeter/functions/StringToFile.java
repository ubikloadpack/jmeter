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
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
    private static final ConcurrentHashMap<String, Lock> lockMap = new ConcurrentHashMap<>();
    static {
        desc.add(JMeterUtils.getResString("string_to_file_pathname"));
        desc.add(JMeterUtils.getResString("string_to_file_content"));//$NON-NLS-1$
        desc.add(JMeterUtils.getResString("string_to_file_way_to_write"));//$NON-NLS-1$
        desc.add(JMeterUtils.getResString("string_to_file_encoding"));//$NON-NLS-1$
    }
    private Object[] values;

    public StringToFile() {
        super();
    }

    /**
     * @return
     */
    /**
     * @return
     * @throws IOException
     */
    private boolean writeToFile() throws IOException {
        String fileName = ((CompoundVariable) values[0]).execute();
        String content = ((CompoundVariable) values[1]).execute();
        String charcode = ((CompoundVariable) values[3]).execute();
        Charset cst = StandardCharsets.UTF_8;
        Boolean append = Boolean.parseBoolean(((CompoundVariable) values[2]).execute().toLowerCase().trim());
        if (fileName.equals("") || content.equals("")) {
            return false;
        }
        if (charcode.trim().length() > 0) {
            cst = Charset.forName(charcode);
        }
        Lock localLock = new ReentrantLock();
        Lock lock = lockMap.putIfAbsent(fileName, localLock);
        try {
            if (lock == null) {
                localLock.lock();
            } else {
                lock.lock();
            }
            File file = new File(fileName);
            File fileParent = file.getParentFile();
            if (fileParent == null || (fileParent.exists() && fileParent.isDirectory() && fileParent.canWrite())) {
                FileUtils.writeStringToFile(file, content, cst, append);
            } else {
                log.error("The parent file doesn't exist or is not writable");
                return false;
            }
        } finally {
            if (lock == null) {
                localLock.unlock();
            } else {
                lock.unlock();
            }
        }
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public String execute(SampleResult previousResult, Sampler currentSampler) throws InvalidVariableException {
        boolean executionResult;
        try {
            executionResult = this.writeToFile();
        } catch (UnsupportedCharsetException ue) {
            executionResult = false;
            log.error("The encoding of file is not supported");
        } catch (IllegalCharsetNameException ie) {
            executionResult = false;
            log.error("The encoding of file contains illegal characters");
        } catch (IOException e) {
            executionResult = false;
        }
        return String.valueOf(executionResult);
    }

    /** {@inheritDoc} */
    @Override
    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
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
