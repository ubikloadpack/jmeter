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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.io.FileUtils;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.junit.JMeterTestCase;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jmeter.util.JMeterUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ubik
 *
 */
public class TestStringtoFile extends JMeterTestCase {
    protected AbstractFunction function;
    private SampleResult result;
    private Collection<CompoundVariable> params;
    private static final Logger log = LoggerFactory.getLogger(TestSimpleFunctions.class);
    private static final String DIR_NAME = "dirTest";
    private static final String FILENAME = "test.txt";
    private static final String STRING_TO_WRITE = "test";
    private static final String ENCODING = StandardCharsets.UTF_8.toString();
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    @Before
    public void setUp() {
        function = new StringToFile();
        result = new SampleResult();
        JMeterContext jmctx = JMeterContextService.getContext();
        JMeterVariables vars = new JMeterVariables();
        jmctx.setVariables(vars);
        jmctx.setPreviousResult(result);
        params = new LinkedList<>();
    }

    @Before
    @After
    public void deleteFileBeforeAfterTest() {
        File file = new File(FILENAME);
        try {
            Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            log.error("File test.txt should not exist");
        }
    }

    @Test
    public void testParameterCount() {
        try {
            checkInvalidParameterCounts(function, 2, 4);
        } catch (Exception e) {
            log.error("The quantity of parameters should be right");
        }
    }

    @Test
    public void testWriteToFile() {
        params.add(new CompoundVariable(FILENAME));
        params.add(new CompoundVariable(STRING_TO_WRITE));
        params.add(new CompoundVariable("true"));
        params.add(new CompoundVariable(ENCODING));
        try {
            function.setParameters(params);
            String returnValue = function.execute(result, null);
            Assert.assertTrue("This method 'Stringtofile' should have successfully run",
                    Boolean.parseBoolean(returnValue));
        } catch (InvalidVariableException e) {
            log.error("This method 'Stringtofile' should have successfully run");
        }
    }


    @Test
    public void testWriteToFileWhenDirectoryDoesntExist() {
        String pathDirectory = File.separator + DIR_NAME;
        File dir = new File(pathDirectory);
        if (dir.exists()) {
            deleteDir(dir);
        }
        String pathname = pathDirectory + File.separator + FILENAME;
        params.add(new CompoundVariable(pathname));
        params.add(new CompoundVariable(STRING_TO_WRITE));
        params.add(new CompoundVariable("true"));
        params.add(new CompoundVariable(ENCODING));
        try {
            function.setParameters(params);
            String returnValue = function.execute(result, null);
            Assert.assertFalse("This method 'Stringtofile' should fail to run", Boolean.parseBoolean(returnValue));
        } catch (InvalidVariableException e) {
            log.error("This method 'Stringtofile' should fail to run");
        }

    }
    
    @Test
    public void testWriteToFileWhenDirectoryExist() {
        File dir=null;
        try {
        dir = tempFolder.newFolder(DIR_NAME);
        } catch (IOException e1) {
            log.error("can't create the directory");
        }
        String pathname = dir.getAbsolutePath() + File.separator + FILENAME;
        params.add(new CompoundVariable(pathname));
        params.add(new CompoundVariable(STRING_TO_WRITE));
        params.add(new CompoundVariable("true"));
        params.add(new CompoundVariable(ENCODING));
        try {
            function.setParameters(params);
            String returnValue = function.execute(result, null);
            Assert.assertTrue( "This method 'Stringtofile' should have successfully run",Boolean.parseBoolean(returnValue));
        } catch (InvalidVariableException e) {
            log.error("This method 'Stringtofile' should have successfully run");
        }
    }


    @Test
    public void testWriteToFileOptParamWayToWriteIsNull() {
        File file = new File(FILENAME);
        params.add(new CompoundVariable(FILENAME));
        params.add(new CompoundVariable(STRING_TO_WRITE));
        params.add(new CompoundVariable(null));
        params.add(new CompoundVariable(ENCODING));
        try {
            function.setParameters(params);
            String returnValue = function.execute(result, null);
            Assert.assertTrue("This method 'Stringtofile' should have successfully run",
                    Boolean.parseBoolean(returnValue));
        } catch (InvalidVariableException e) {
            log.error("This method 'Stringtofile' should have successfully run");
        }
    }

    @Test
    public void testWriteToFileOptParamWayToWriteIsIllegal() {
        params.add(new CompoundVariable(FILENAME));
        params.add(new CompoundVariable(STRING_TO_WRITE));
        params.add(new CompoundVariable("trrue"));
        params.add(new CompoundVariable(ENCODING));
        try {
            function.setParameters(params);
            String returnValue = function.execute(result, null);
            Assert.assertFalse("This method 'Stringtofile' should fail to run", Boolean.parseBoolean(returnValue));
        } catch (InvalidVariableException e) {
            log.error("This method 'Stringtofile' should fail to run");
        }
    }

    @Test
    public void testWriteToFileOptParamEncodingIsNull() {
        params.add(new CompoundVariable(FILENAME));
        params.add(new CompoundVariable(STRING_TO_WRITE));
        params.add(new CompoundVariable("true"));
        params.add(new CompoundVariable(null));
        try {
            function.setParameters(params);
            String returnValue = function.execute(result, null);
            Assert.assertTrue("This method 'Stringtofile' should have successfully run",
                    Boolean.parseBoolean(returnValue));
        } catch (InvalidVariableException e) {
            log.error("This method 'Stringtofile' should have successfully run");
        }
    }

    @Test
    public void testWriteToFileEncodingNotSupported() {
        File file=null;
        try {
            file = tempFolder.newFile(FILENAME);
        } catch (IOException e1) {
            log.error("cant create the file successfully");
        }

        params.add(new CompoundVariable(file.getAbsolutePath()));
        params.add(new CompoundVariable(STRING_TO_WRITE));
        params.add(new CompoundVariable("true"));
        params.add(new CompoundVariable("UTF-20"));
        try {
            function.setParameters(params);
            String returnValue = function.execute(result, null);
            Assert.assertFalse("This method 'Stringtofile' should have failed to run",
                    Boolean.parseBoolean(returnValue));
        } catch (InvalidVariableException e) {
            log.error("This method 'Stringtofile' should fail to run");
        }
    }

    @Test
    public void testWriteToFileEncodingNotLegal() {
        File file=null;
        try {
            file = tempFolder.newFile(FILENAME);
        } catch (IOException e1) {
            log.error("cant create the file successfully");
        }

        params.add(new CompoundVariable(file.getAbsolutePath()));
        params.add(new CompoundVariable(STRING_TO_WRITE));
        params.add(new CompoundVariable("true"));
        params.add(new CompoundVariable("UTFéé"));
        try {
            function.setParameters(params);
            String returnValue = function.execute(result, null);
            Assert.assertFalse("This method 'Stringtofile' should have failed to run",
                    Boolean.parseBoolean(returnValue));
        } catch (InvalidVariableException e) {
            log.error("This method 'Stringtofile' should fail to run");
        }
    }

    @Test
    public void testWriteToFileIOException() {
        File file=null;
        try {
            file = tempFolder.newFile(FILENAME);
        } catch (IOException e1) {
            log.error("cant create the file successfully");
        }

        file.setWritable(false);
        params.add(new CompoundVariable(file.getAbsolutePath()));
        params.add(new CompoundVariable(STRING_TO_WRITE));
        params.add(new CompoundVariable("true"));
        params.add(new CompoundVariable("UTF-8"));

        try {
            function.setParameters(params);
            function.execute(result, null);
        } catch (InvalidVariableException e) {
            log.error("The file is not writable");
        }
    }

    @Test
    public void testWriteToFileRequiredFilePathIsNull() {
        params.add(new CompoundVariable(null));
        params.add(new CompoundVariable(STRING_TO_WRITE));
        params.add(new CompoundVariable("true"));
        params.add(new CompoundVariable(ENCODING));
        try {
            function.setParameters(params);
            String returnValue = function.execute(result, null);
            Assert.assertFalse("This method 'Stringtofile' should fail to run", Boolean.parseBoolean(returnValue));
        } catch (InvalidVariableException e) {
            log.error("This method 'Stringtofile' should fail to run");
        }
    }

    @Test
    public void testWriteToFileRequiredStringIsNull() {
        File file=null;
        try {
            file = tempFolder.newFile(FILENAME);
        } catch (IOException e1) {
            log.error("cant create the file successfully");
        }
        params.add(new CompoundVariable(file.getAbsolutePath()));
        params.add(new CompoundVariable(""));
        params.add(new CompoundVariable("true"));
        params.add(new CompoundVariable(ENCODING));
        try {
            function.setParameters(params);
            String returnValue = function.execute(result, null);
            Assert.assertFalse("This method 'Stringtofile' should fail to run", Boolean.parseBoolean(returnValue));
        } catch (InvalidVariableException e) {
            log.error("This method 'Stringtofile' should fail to run");
        }
    }

    @Test
    public void testOverwrite() {
        File file=null;
        try {
            file = tempFolder.newFile(FILENAME);
        } catch (IOException e1) {
            log.error("cant create the file successfully");
        }
        params.add(new CompoundVariable(file.getAbsolutePath()));
        params.add(new CompoundVariable(STRING_TO_WRITE));
        params.add(new CompoundVariable("false"));
        params.add(new CompoundVariable(ENCODING));
        try {
            function.setParameters(params);
            String returnValue = function.execute(result, null);
            Assert.assertTrue("This method 'Stringtofile' should have successfully run",
                    Boolean.parseBoolean(returnValue));
            try {
                String res= FileUtils.readFileToString(file, ENCODING).trim();
                Assert.assertEquals("The string should be 'test'", "test", res);
            } catch (IOException e) { 
                log.error("Failed to read string from file");
            }
        } catch (InvalidVariableException e) {
            log.error("This method 'Stringtofile' should have successfully run");
        }
    }
    @Test
    public void testAppend() {
        File file=null;
        try {
            file = tempFolder.newFile(FILENAME);
        } catch (IOException e1) {
            log.error("cant create the file successfully");
        }
        params.add(new CompoundVariable(file.getAbsolutePath()));
        params.add(new CompoundVariable(STRING_TO_WRITE));
        params.add(new CompoundVariable("true"));
        params.add(new CompoundVariable(ENCODING));
        try {
            function.setParameters(params);
            String returnValue = function.execute(result, null);
            returnValue = function.execute(result, null);
            Assert.assertTrue("This method 'Stringtofile' should have successfully run",
                    Boolean.parseBoolean(returnValue));
            try {
                String res= FileUtils.readFileToString(file, ENCODING).trim();
                Assert.assertEquals("The string should be 'testtest'", "testtest", res);
            } catch (IOException e) { 
                log.error("Failed to read string from file");
            }
        } catch (InvalidVariableException e) {
            log.error("This method 'Stringtofile' should have successfully run");
        }
    }

    @Test
    public void testDesc() {
        String des = "Function 'stringtofile' should have successfully read the configuration file 'messages.properties'";
        Assert.assertEquals(des, JMeterUtils.getResString("string_to_file_pathname"),
                function.getArgumentDesc().get(0));
    }

    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        try {
            Files.deleteIfExists(dir.toPath());
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
