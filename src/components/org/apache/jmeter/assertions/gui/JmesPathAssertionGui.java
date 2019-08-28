package org.apache.jmeter.assertions.gui;

import javax.swing.JCheckBox;

import org.apache.jmeter.assertions.JmesPathAssertion;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.JLabeledTextArea;
import org.apache.jorphan.gui.JLabeledTextField;

/**
 *
 * Java class representing GUI for the {@link JmesPathAssertion} component in
 * JMeter</br>
 * This class extends {@link JSONPathAssertionGui} to avoid code duplication 
 * because they work the same way, except that field names are different and some
 * method that we must {@link Override}.
 *
 */
public class JmesPathAssertionGui extends JSONPathAssertionGui {
    private static final long serialVersionUID = 3719848809836264945L;
    
    private static final String JMES_ASSERTION_PATH = "jmespath_assertion_path";
    private static final String JMES_ASSERTION_VALIDATION = "jmespath_assertion_validation";
    private static final String JMES_ASSERTION_REGEX = "jmespath_assertion_regex";
    private static final String JMES_ASSERTION_EXPECTED_VALUE = "jmespath_assertion_expected_value";
    private static final String JMES_ASSERTION_NULL = "jmespath_assertion_null";
    private static final String JMES_ASSERTION_INVERT = "jmespath_assertion_invert";
    private static final String JMES_ASSERTION_TITLE = "jmespath_assertion_title";


    /**
     * constructor
     */
    public JmesPathAssertionGui() {
        // get the superclass fields and set their name to current component fields.
        super.jsonPath =  new JLabeledTextField(JMeterUtils.getResString(JMES_ASSERTION_PATH));
        super.jsonValue = new JLabeledTextArea(JMeterUtils.getResString(JMES_ASSERTION_EXPECTED_VALUE));
        super.jsonValidation = new JCheckBox(JMeterUtils.getResString(JMES_ASSERTION_VALIDATION));
        super.expectNull = new JCheckBox(JMeterUtils.getResString(JMES_ASSERTION_NULL));
        super.invert = new JCheckBox(JMeterUtils.getResString(JMES_ASSERTION_INVERT));
        super.isRegex = new JCheckBox(JMeterUtils.getResString(JMES_ASSERTION_REGEX));
        
        // when we are calling init method from super class
        // the interface is build with fields above
        super.init();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearGui() {
        super.clearGui();
        // JmesPath expression can't be null or empty so we set a default value
        // to avoid exception
        super.jsonPath.setText("foo");
        
        // other values are set by superclass
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TestElement createTestElement() {
        JmesPathAssertion jmesAssertion = new JmesPathAssertion();
        modifyTestElement(jmesAssertion);
        return jmesAssertion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLabelResource() {
        return JMES_ASSERTION_TITLE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void modifyTestElement(TestElement element) {
        super.configureTestElement(element);
        if (element instanceof JmesPathAssertion) {
            JmesPathAssertion jmesAssertion = (JmesPathAssertion) element;
            jmesAssertion.setJmesPath(jsonPath.getText());
            jmesAssertion.setExpectedValue(jsonValue.getText());
            jmesAssertion.setJsonValidationBool(jsonValidation.isSelected());
            jmesAssertion.setExpectNull(expectNull.isSelected());
            jmesAssertion.setInvert(invert.isSelected());
            jmesAssertion.setIsRegex(isRegex.isSelected());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(TestElement element) {
        super.configure(element);
        if(element instanceof JmesPathAssertion) {
            JmesPathAssertion jmesAssertion = (JmesPathAssertion) element;
            jsonPath.setText(jmesAssertion.getJmesPath());
            jsonValue.setText(jmesAssertion.getExpectedValue());
            jsonValidation.setSelected(jmesAssertion.isJsonValidationBool());
            expectNull.setSelected(jmesAssertion.isExpectNull());
            invert.setSelected(jmesAssertion.isInvert());
            isRegex.setSelected(jmesAssertion.isUseRegex());
        }
    }
    
    
}
