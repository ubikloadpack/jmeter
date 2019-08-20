package org.apache.jmeter.assertions.gui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.jmeter.assertions.JmesPathAssertion;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.JLabeledTextArea;
import org.apache.jorphan.gui.JLabeledTextField;

/**
 *
 * Java class representing GUI for the {@link JmesPathAssertion} component in JMeter
 *
 */
public class JmesPathAssertionGui extends AbstractAssertionGui implements ChangeListener {

	private static final long serialVersionUID = 3719848809836264945L;

	private JLabeledTextField jmesPath = null;
	private JLabeledTextArea jsonValue = null;
	private JCheckBox jsonValidation = null;
	private JCheckBox expectNull = null;
	private JCheckBox invert = null;
	private JCheckBox isRegex;

	public JmesPathAssertionGui() {
		init();
	}

	public void init() {
		setLayout(new BorderLayout());
		setBorder(makeBorder());
		add(makeTitlePanel(), BorderLayout.NORTH);

		VerticalPanel panel = new VerticalPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

		jmesPath = new JLabeledTextField(JMeterUtils.getResString("jmespath_assertion_path"));
		jsonValidation = new JCheckBox(JMeterUtils.getResString("jmespath_assertion_validation"));
		isRegex = new JCheckBox(JMeterUtils.getResString("jmespath_assertion_regex"));
		jsonValue = new JLabeledTextArea(JMeterUtils.getResString("jmespath_assertion_expected_value"));
		expectNull = new JCheckBox(JMeterUtils.getResString("jmespath_assertion_null"));
		invert = new JCheckBox(JMeterUtils.getResString("jmespath_assertion_invert"));

		jsonValidation.addChangeListener(this);
		expectNull.addChangeListener(this);

		panel.add(jmesPath);
		panel.add(jsonValidation);
		panel.add(isRegex);
		panel.add(jsonValue);
		panel.add(expectNull);
		panel.add(invert);

		add(panel, BorderLayout.CENTER);
	}

	/**
         * {@inheritDoc}
         */
	@Override
	public void clearGui() {
		super.clearGui();
		jmesPath.setText("");
		jsonValue.setText("");
		jsonValidation.setSelected(false);
		expectNull.setSelected(false);
		invert.setSelected(false);
		isRegex.setSelected(true);
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
		return "jmespath_assertion_title";
	}

	/**
         * {@inheritDoc}
         */
	@Override
	public void modifyTestElement(TestElement element) {
		super.configureTestElement(element);
		if (element instanceof JmesPathAssertion) {
			JmesPathAssertion jmesAssertion = (JmesPathAssertion) element;
			jmesAssertion.setJmesPath(jmesPath.getText());
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
		JmesPathAssertion jmesAssertion = (JmesPathAssertion) element;
		jmesPath.setText(jmesAssertion.getJmesPath());
		jsonValue.setText(jmesAssertion.getExpectedValue());
		jsonValidation.setSelected(jmesAssertion.isJsonValidationBool());
		expectNull.setSelected(jmesAssertion.isExpectNull());
		invert.setSelected(jmesAssertion.isInvert());
		isRegex.setSelected(jmesAssertion.isUseRegex());
	}

	/**
         * {@inheritDoc}
         */
	@Override
	public void stateChanged(ChangeEvent e) {
		jsonValue.setEnabled(jsonValidation.isSelected() && !expectNull.isSelected());
		isRegex.setEnabled(jsonValidation.isSelected() && !expectNull.isSelected());
	}

}
