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

package org.apache.jmeter.extractor.json.jsonpath.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.jmeter.extractor.json.jsonpath.JMESExtractor;
import org.apache.jmeter.gui.GUIMenuSortOrder;
import org.apache.jmeter.processor.gui.AbstractPostProcessorGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.JLabeledTextField;

/**
 * GUI for {@link JSON2PostProcessor}
 * @since 3.0
 */
@GUIMenuSortOrder(2)
public class JMESExtractorGui extends AbstractPostProcessorGui {

    private static final long serialVersionUID = -2845056031828291476L;

    private JLabeledTextField defaultValuesField;
    private JLabeledTextField jsonPathExpressionsField;
    private JLabeledTextField refNamesField;
    private JLabeledTextField matchNumbersField;
    
    public JMESExtractorGui() {
        super();
        init();
    }


    @Override
    public String getLabelResource() {
        return "jmes_extractor_title";//$NON-NLS-1$
    }

    @Override
    public void configure(TestElement element) {
        super.configure(element);
        JMESExtractor config = (JMESExtractor) element;
        showScopeSettings(config, true);
        refNamesField.setText(config.getRefNames());
        jsonPathExpressionsField.setText(config.getJsonPathExpressions());
        matchNumbersField.setText(config.getMatchNumbers());
        defaultValuesField.setText(config.getDefaultValues());
    }

    /**
     * @see org.apache.jmeter.gui.JMeterGUIComponent#createTestElement()
     */
    @Override
    public TestElement createTestElement() {
        JMESExtractor config = new JMESExtractor();
        modifyTestElement(config);
        return config;
    }

    /**
     * Modifies a given TestElement to mirror the data in the gui components.
     *
     * @see org.apache.jmeter.gui.JMeterGUIComponent#modifyTestElement(TestElement)
     */
    @Override
    public void modifyTestElement(TestElement c) {
        super.configureTestElement(c);
        if (c instanceof JMESExtractor) {
            JMESExtractor config = (JMESExtractor) c;
            saveScopeSettings(config);
            config.setRefNames(refNamesField.getText());
            config.setJsonPathExpressions(jsonPathExpressionsField.getText());
            config.setDefaultValues(defaultValuesField.getText());
            config.setMatchNumbers(matchNumbersField.getText());
        }
    }

    /**
     * Implements JMeterGUIComponent.clearGui
     */
    @Override
    public void clearGui() {
        super.clearGui();
        refNamesField.setText(""); //$NON-NLS-1$
        jsonPathExpressionsField.setText(""); //$NON-NLS-1$
        matchNumbersField.setText(""); //$NON-NLS-1$
        defaultValuesField.setText(""); //$NON-NLS-1$
    }

    private void init() { // WARNING: called from ctor so must not be overridden (i.e. must be private or final)

        setLayout(new BorderLayout());
        setBorder(makeBorder());

        Box box = Box.createVerticalBox();
        box.add(makeTitlePanel());
        box.add(createScopePanel(true));
        add(box, BorderLayout.NORTH);
        add(makeParameterPanel(), BorderLayout.CENTER);

    }

    private JPanel makeParameterPanel() {
        refNamesField = new JLabeledTextField(JMeterUtils.getResString("jsonpp_variable_names"));//$NON-NLS-1$
        jsonPathExpressionsField = new JLabeledTextField(JMeterUtils.getResString("jsonpp_json_path_expressions"));//$NON-NLS-1$
        matchNumbersField = new JLabeledTextField(JMeterUtils.getResString("jsonpp_match_numbers"));//$NON-NLS-1$
        defaultValuesField = new JLabeledTextField(JMeterUtils.getResString("jsonpp_default_values"));//$NON-NLS-1$
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        initConstraints(gbc);
        addField(panel, refNamesField, gbc);
        nextLine(gbc);
        addField(panel, jsonPathExpressionsField, gbc);
        nextLine(gbc);
        addField(panel, matchNumbersField, gbc);
        nextLine(gbc);
        nextLine(gbc);
        gbc.weighty = 1;
        addField(panel, defaultValuesField, gbc);
        return panel;
    }

    private void addField(JPanel panel, JLabeledTextField field, GridBagConstraints gbc) {
        List<JComponent> item = field.getComponentList();
        panel.add(item.get(0), gbc.clone());
        gbc.gridx++;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(item.get(1), gbc.clone());
    }

    private void addField(JPanel panel, JLabel label, JCheckBox checkBox, GridBagConstraints gbc) {
        panel.add(label, gbc.clone());
        gbc.gridx++;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(checkBox, gbc.clone());
    }

    private void nextLine(GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
    }

    private void initConstraints(GridBagConstraints gbc) {
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.weighty = 0;
    }
}
