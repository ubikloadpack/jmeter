package org.apache.jmeter.assertions;

import static org.junit.Assert.*;

import org.apache.jmeter.samplers.SampleResult;
import org.junit.Test;

public class TestJmesPathAssertion {

	@Test
    public void should_find_the_right_result_with_string_values() {
    	SampleResult samplerResult = new SampleResult();
    	samplerResult.setResponseData("{\"a\": \"foo\", \"b\": \"bar\", \"c\": \"baz\"}".getBytes());
    	JmesPathAssertion instance = new JmesPathAssertion();
    	instance.setJmesPath("a");
    	instance.setJsonValidationBool(true);
    	instance.setIsRegex(false);
    	instance.setExpectedValue("foo");
    	AssertionResult expResult = new AssertionResult("");
    	AssertionResult result = instance.getResult(samplerResult);
    	assertEquals(expResult.getName(), result.getName());
        assertEquals(false, result.isFailure());
    }
	
	@Test
    public void should_find_the_right_result_with_regex() {
    	SampleResult samplerResult = new SampleResult();
    	samplerResult.setResponseData("{\"a\": \"123\"}".getBytes());
    	JmesPathAssertion instance = new JmesPathAssertion();
    	instance.setJmesPath("a");
    	instance.setJsonValidationBool(true);
    	instance.setIsRegex(true);
    	instance.setExpectedValue("123|456");
    	AssertionResult expResult = new AssertionResult("");
    	AssertionResult result = instance.getResult(samplerResult);
    	assertEquals(expResult.getName(), result.getName());
        assertEquals(false, result.isFailure());
    }
	
	
	 @Test
	    public void should_find_the_right_result_with_number_values() {
	    	SampleResult samplerResult = new SampleResult();
	    	String str = "{\"one\": \"1\",\"two\": \"2\"}";
	    	samplerResult.setResponseData(str.getBytes());
	    	JmesPathAssertion instance = new JmesPathAssertion();
	    	instance.setJmesPath("[one,two]");
	    	instance.setJsonValidationBool(true);
	    	instance.setIsRegex(false);
	    	instance.setExpectedValue("[\"1\",\"2\"]");
	    	AssertionResult expResult = new AssertionResult("");
	    	AssertionResult result = instance.getResult(samplerResult);
	    	assertEquals(expResult.getName(), result.getName());
	        assertEquals(false, result.isFailure());
	    }
	 
	 @Test
	    public void should_find_the_right_result_with_a_json_array() {
	    	SampleResult samplerResult = new SampleResult();
	    	String str = "{\n" + 
	    			"  \"people\": [\n" + 
	    			"    {\n" + 
	    			"      \"name\": \"b\",\n" + 
	    			"      \"age\": 30\n" + 
	    			"    },\n" + 
	    			"    {\n" + 
	    			"      \"name\": \"a\",\n" + 
	    			"      \"age\": 50\n" + 
	    			"    },\n" + 
	    			"    {\n" + 
	    			"      \"name\": \"c\",\n" + 
	    			"      \"age\": 40\n" + 
	    			"    }\n" + 
	    			"  ]\n" + 
	    			"}";
	    	samplerResult.setResponseData(str.getBytes());
	    	JmesPathAssertion instance = new JmesPathAssertion();
	    	instance.setJmesPath("max_by(people, &age).name");
	    	instance.setJsonValidationBool(true);
	    	instance.setIsRegex(false);
	    	instance.setExpectedValue("a");
	    	AssertionResult expResult = new AssertionResult("");
	    	AssertionResult result = instance.getResult(samplerResult);
	    	assertEquals(expResult.getName(), result.getName());
	        assertEquals(false, result.isFailure());
	    }
	 
	@Test
	public void should_find_null() {
		SampleResult samplerResult = new SampleResult();
		String str = "{\"one\": \"\"}";
		samplerResult.setResponseData(str.getBytes());
		JmesPathAssertion instance = new JmesPathAssertion();
		instance.setJmesPath("one");
		instance.setJsonValidationBool(true);
		instance.setIsRegex(false);
		instance.setExpectNull(true);
		AssertionResult expResult = new AssertionResult("");
		AssertionResult result = instance.getResult(samplerResult);
		assertEquals(expResult.getName(), result.getName());
		assertEquals(false, result.isFailure());
	}
	
	@Test
	public void should_invert_result_an_error_become_true() {
		SampleResult samplerResult = new SampleResult();
		String str = "{\"one\": \"1\"}";
		samplerResult.setResponseData(str.getBytes());
		JmesPathAssertion instance = new JmesPathAssertion();
		instance.setJmesPath("one");
		instance.setJsonValidationBool(true);
		instance.setIsRegex(false);
		instance.setExpectNull(false);
		instance.setInvert(true);
		instance.setExpectedValue("2");
		AssertionResult expResult = new AssertionResult("");
		AssertionResult result = instance.getResult(samplerResult);
		assertEquals(expResult.getName(), result.getName());
		assertEquals(false, result.isFailure());
	}
	
	@Test
	public void should_be_true_if_result_and_value_are_empty() {
		SampleResult samplerResult = new SampleResult();
		String str = "{\"one\": \"\"}";
		samplerResult.setResponseData(str.getBytes());
		JmesPathAssertion instance = new JmesPathAssertion();
		instance.setJmesPath("one");
		instance.setJsonValidationBool(true);
		instance.setIsRegex(false);
		instance.setExpectNull(true);
		instance.setExpectedValue("");
		AssertionResult expResult = new AssertionResult("");
		AssertionResult result = instance.getResult(samplerResult);
		assertEquals(expResult.getName(), result.getName());
		assertEquals(false, result.isFailure());
	}
	
	@Test
	public void should_fail_if_value_and_result_are_empty() {
		
		SampleResult samplerResult = new SampleResult();
		String str = "{\"one\": \"\"}";
		samplerResult.setResponseData(str.getBytes());
		JmesPathAssertion instance = new JmesPathAssertion();
		instance.setJmesPath("one");
		instance.setJsonValidationBool(true);
		instance.setIsRegex(false);
		instance.setExpectNull(false);
		instance.setExpectedValue("");
		AssertionResult expResult = new AssertionResult("");
		AssertionResult result = instance.getResult(samplerResult);
		assertEquals(expResult.getName(), result.getName());
		assertEquals(true, result.isFailure());
		
	}
	
	@Test
	public void should_fail_if_value_and_result_are_different() {
		
		SampleResult samplerResult = new SampleResult();
		String str = "{\"one\": \"1\"}";
		samplerResult.setResponseData(str.getBytes());
		JmesPathAssertion instance = new JmesPathAssertion();
		instance.setJmesPath("one");
		instance.setJsonValidationBool(true);
		instance.setIsRegex(false);
		instance.setExpectNull(false);
		instance.setExpectedValue("2");
		AssertionResult expResult = new AssertionResult("");
		AssertionResult result = instance.getResult(samplerResult);
		assertEquals(expResult.getName(), result.getName());
		assertEquals(true, result.isFailure());
		
	}
	
	@Test
	public void should_fail_if_value_and_result_are_same_but_invert_true() {
		
		SampleResult samplerResult = new SampleResult();
		String str = "{\"one\": \"1\"}";
		samplerResult.setResponseData(str.getBytes());
		JmesPathAssertion instance = new JmesPathAssertion();
		instance.setJmesPath("one");
		instance.setJsonValidationBool(true);
		instance.setIsRegex(false);
		instance.setExpectNull(false);
		instance.setInvert(true);
		instance.setExpectedValue("1");
		AssertionResult expResult = new AssertionResult("");
		AssertionResult result = instance.getResult(samplerResult);
		assertEquals(expResult.getName(), result.getName());
		assertEquals(true, result.isFailure());
		
	}
	
	@Test
	public void should_fail_with_a_json_parse_exception() {
		SampleResult samplerResult = new SampleResult();
		String str = "{'one': '1'}";
		samplerResult.setResponseData(str.getBytes());
		JmesPathAssertion instance = new JmesPathAssertion();
		instance.setJmesPath("one");
		instance.setJsonValidationBool(true);
		instance.setIsRegex(false);
		instance.setExpectNull(false);
		instance.setInvert(false);
		instance.setExpectedValue("2");
		AssertionResult expResult = new AssertionResult("");
		AssertionResult result = instance.getResult(samplerResult);
		assertEquals(expResult.getName(), result.getName());
		assertEquals(true, result.isFailure());
	}
	
	@Test
	public void should_fail_if_result_is_empty() {
		
		SampleResult samplerResult = new SampleResult();
		String str = "{\"one\": \"\"}";
		samplerResult.setResponseData(str.getBytes());
		JmesPathAssertion instance = new JmesPathAssertion();
		instance.setJmesPath("one");
		instance.setJsonValidationBool(true);
		instance.setIsRegex(false);
		instance.setExpectNull(false);
		instance.setExpectedValue("1");
		AssertionResult expResult = new AssertionResult("");
		AssertionResult result = instance.getResult(samplerResult);
		assertEquals(expResult.getName(), result.getName());
		assertEquals(true, result.isFailure());
		
	}
	
	@Test
	public void should_fail_if_expected_value_is_empty() {
		
		SampleResult samplerResult = new SampleResult();
		String str = "{\"one\": \"1\"}";
		samplerResult.setResponseData(str.getBytes());
		JmesPathAssertion instance = new JmesPathAssertion();
		instance.setJmesPath("one");
		instance.setJsonValidationBool(true);
		instance.setIsRegex(false);
		instance.setExpectNull(false);
		instance.setExpectedValue("");
		AssertionResult expResult = new AssertionResult("");
		AssertionResult result = instance.getResult(samplerResult);
		assertEquals(expResult.getName(), result.getName());
		assertEquals(true, result.isFailure());
		
	}
	
	@Test
	public void should_be_true_if_result_and_expected_value_null() {
	    
	    SampleResult samplerResult = new SampleResult();
            String str = "{\"\":\"\"}";
            samplerResult.setResponseData(str.getBytes());
            JmesPathAssertion instance = new JmesPathAssertion();
            instance.setJmesPath("foo");
            instance.setJsonValidationBool(true);
            instance.setIsRegex(false);
            instance.setExpectNull(true);
            instance.setExpectedValue(null);
            AssertionResult expResult = new AssertionResult("");
            AssertionResult result = instance.getResult(samplerResult);
            assertEquals(expResult.getName(), result.getName());
            assertEquals(false, result.isFailure());
	    
	}

}
