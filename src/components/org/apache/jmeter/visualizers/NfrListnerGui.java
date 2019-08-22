/*

 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 */
package org.apache.jmeter.visualizers;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.apache.jmeter.config.NfrArgument;
import org.apache.jmeter.gui.GUIMenuSortOrder;
import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.gui.SavePropertyDialog;
import org.apache.jmeter.gui.util.FilePanel;
import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
import org.apache.jmeter.gui.util.TextAreaCellRenderer;
import org.apache.jmeter.gui.util.TextAreaTableCellEditor;
import org.apache.jmeter.reporters.NfrResultCollector;
import org.apache.jmeter.samplers.Clearable;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.JMeterProperty;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.visualizers.gui.NfrAbstractVisualizer;
import org.apache.jorphan.gui.ComponentUtil;
import org.apache.jorphan.gui.GuiUtils;
import org.apache.jorphan.gui.ObjectTableModel;
import org.apache.jorphan.reflect.Functor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Aggregate Table-Based Reporting Visualizer for JMeter.
 */
@GUIMenuSortOrder(3)
public class NfrListnerGui extends NfrAbstractVisualizer implements Clearable, ActionListener {
    private static final long serialVersionUID = 242L;
    private static final String TOTAL_ROW_LABEL = JMeterUtils.getResString("aggregate_report_total_label"); //$NON-NLS-1$
    private final JCheckBox useGroupName = new JCheckBox(JMeterUtils.getResString("aggregate_graph_use_group_name")); //$NON-NLS-1$
    /** Lock used to protect tableRows update + model update */
    private final transient Object lock = new Object();
    public static Map<String, SamplingStatCalculator> tableRows = new ConcurrentHashMap<>();
    private Deque<SamplingStatCalculator> newRows = new ConcurrentLinkedDeque<>();
    private final JButton add = new JButton("add"); //$NON-NLS-1$
    private final JButton delete = new JButton("delete"); //$NON-NLS-1$
    private JTable stringTable;
    /** Table model for the pattern table. */
    private ObjectTableModel tableModel;
    /** Logging. */
    private static final Logger log = LoggerFactory.getLogger(NfrAbstractVisualizer.class);
    /** File Extensions */
    private static final String[] EXTS = { ".xml", ".jtl", ".csv" }; // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
    /** A panel allowing results to be saved. */
    private final FilePanel filePanel;
    /** A checkbox choosing whether or not only errors should be logged. */
    private final JCheckBox errorLogging;
    /* A checkbox choosing whether or not only successes should be logged. */
    private final JCheckBox successOnlyLogging;
    protected NfrResultCollector collector = new NfrResultCollector();
    protected boolean isStats = false;

    public NfrListnerGui() {
        super();
        // errorLogging and successOnlyLogging are mutually exclusive
        errorLogging = new JCheckBox(JMeterUtils.getResString("log_errors_only")); // $NON-NLS-1$
        errorLogging.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (errorLogging.isSelected()) {
                    successOnlyLogging.setSelected(false);
                }
            }
        });
        successOnlyLogging = new JCheckBox(JMeterUtils.getResString("log_success_only")); // $NON-NLS-1$
        successOnlyLogging.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (successOnlyLogging.isSelected()) {
                    errorLogging.setSelected(false);
                }
            }
        });
        JButton saveConfigButton = new JButton(JMeterUtils.getResString("config_save_settings")); // $NON-NLS-1$
        saveConfigButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SavePropertyDialog d = new SavePropertyDialog(GuiPackage.getInstance().getMainFrame(),
                        JMeterUtils.getResString("sample_result_save_configuration"), // $NON-NLS-1$
                        true, collector.getSaveConfig());
                d.pack();
                ComponentUtil.centerComponentInComponent(GuiPackage.getInstance().getMainFrame(), d);
                d.setVisible(true);
            }
        });
        filePanel = new FilePanel(JMeterUtils.getResString("file_visualizer_output_file"), EXTS); // $NON-NLS-1$
        filePanel.addChangeListener(this);
        filePanel.add(new JLabel(JMeterUtils.getResString("log_only"))); // $NON-NLS-1$
        filePanel.add(errorLogging);
        filePanel.add(successOnlyLogging);
        filePanel.add(saveConfigButton);
        init();
    }

    @Override
    public String getLabelResource() {
        return "non_function_test"; //$NON-NLS-1$
    }

    @Override
    public void add(final SampleResult res) {
        SamplingStatCalculator row = tableRows.computeIfAbsent(res.getSampleLabel(useGroupName.isSelected()), label -> {
            SamplingStatCalculator newRow = new SamplingStatCalculator(label);
            newRows.add(newRow);
            return newRow;
        });
        synchronized (row) {
            /*
             * Synch is needed because multiple threads can update the counts.
             */
            row.addSample(res);
        }
    }

    /**
     * Clears this visualizer and its model, and forces a repaint of the table.
     */
    @Override
    public void clearData() {
        synchronized (lock) {
            tableModel.clearData();
            tableRows.clear();
            newRows.clear();
        }
    }

    /**
     * Create a panel allowing the user to supply a list of string patterns to test
     * against.
     *
     * @return a new panel for adding string patterns
     */
    /** A table of patterns to test against. */
    private JScrollPane createStringPanel() {
        tableModel = new ObjectTableModel(new String[] { "Name", "Criteria", "Symbol", "Value" }, NfrArgument.class,
                new Functor[] { new Functor("getName"), // $NON-NLS-1$
                        new Functor("getCriteria"), new Functor("getSymbol"), new Functor("getValue") }, // $NON-NLS-1$
                new Functor[] { new Functor("setName"), // $NON-NLS-1$
                        new Functor("setCriteria"), new Functor("setSymbol"), new Functor("setValue") }, // $NON-NLS-1$
                new Class[] { String.class, String.class, String.class, String.class });
        stringTable = new JTable(tableModel);
        stringTable.getTableHeader().setDefaultRenderer(new HeaderAsPropertyRenderer());
        stringTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JMeterUtils.applyHiDPI(stringTable);
        TextAreaCellRenderer renderer = new TextAreaCellRenderer();
        stringTable.setRowHeight(renderer.getPreferredHeight());
        stringTable.setDefaultRenderer(String.class, renderer);
        stringTable.setDefaultEditor(String.class, new TextAreaTableCellEditor());
        stringTable.setPreferredScrollableViewportSize(new Dimension(100, 70));
        return new JScrollPane(stringTable);
    }

    /**
     * Main visualizer setup.
     */
    private void init() { // WARNING: called from ctor so must not be overridden (i.e. must be private or
                          // final)
        this.setLayout(new BorderLayout());
        // MAIN PANEL
        JPanel mainPanel = new JPanel();
        Border margin = new EmptyBorder(10, 10, 5, 10);
        mainPanel.setBorder(margin);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(makeTitlePanel());
        this.add(mainPanel, BorderLayout.NORTH);
        this.add(createStringPanel(), BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        add.addActionListener(new AddPatternListener());
        delete.addActionListener(new ClearPatternsListener());
        buttonPanel.add(add);
        buttonPanel.add(delete);
        add.setEnabled(true);
        this.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * An ActionListener for deleting a pattern.
     *
     */
    private class ClearPatternsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            GuiUtils.cancelEditing(stringTable);
            int[] rowsSelected = stringTable.getSelectedRows();
            stringTable.clearSelection();
            if (rowsSelected.length > 0) {
                for (int i = rowsSelected.length - 1; i >= 0; i--) {
                    tableModel.removeRow(rowsSelected[i]);
                }
                tableModel.fireTableDataChanged();
            } else {
                if (tableModel.getRowCount() > 0) {
                    tableModel.removeRow(0);
                    tableModel.fireTableDataChanged();
                }
            }
            if (stringTable.getModel().getRowCount() == 0) {
                delete.setEnabled(false);
            }
        }
    }

    /**
     * An ActionListener for adding a pattern.
     */
    private class AddPatternListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // If a table cell is being edited, we should accept the current value
            // and stop the editing before adding a new row.
            GuiUtils.stopTableEditing(stringTable);
            tableModel.addRow(new NfrArgument("", "", "", ""));
            checkButtonsStatus();
            // Highlight (select) and scroll to the appropriate row.
            int rowToSelect = tableModel.getRowCount() - 1;
            stringTable.setRowSelectionInterval(rowToSelect, rowToSelect);
         stringTable.scrollRectToVisible(stringTable.getCellRect(rowToSelect, 0, true));
        }
    }
    protected void checkButtonsStatus() {
        // Disable DELETE if there are no rows in the table to delete.
        if (tableModel.getRowCount() == 0) {
            delete.setEnabled(false);
        } else {
            delete.setEnabled(true);
        }
    }
    @Override
    public TestElement createTestElement() {
        NfrResultCollector args = getUnclonedParameters();
        super.configureTestElement(args);
        return (TestElement) args.clone();
    }

    private NfrResultCollector getUnclonedParameters() {
        @SuppressWarnings("unchecked") // only contains Argument (or HTTPArgument)
        Iterator<NfrArgument> modelData = (Iterator<NfrArgument>) tableModel.iterator();
        NfrResultCollector args = new NfrResultCollector();
        while (modelData.hasNext()) {
            NfrArgument arg = modelData.next();
            args.addNfrArgument(arg);
        }
        return args;
    }
//
//    @Override
//    public void modifyTestElement(TestElement c) {
//        GuiUtils.stopTableEditing(stringTable);
//        if (c instanceof NfrResultCollector) {
//            NfrResultCollector rc = (NfrResultCollector) c;
//            NfrArgument nfrArgument=new NfrArgument("test", "", "", "hhhhh");
//            rc.removeAllNfrArguments();
        //    rc.addNfrArgument(nfrArgument);
//            System.out.println(rc.getNfrArguments());
//            collector = rc;
//            configureTestElement((AbstractListenerElement) c);
//        }
//        NfrResultCollector nfrResultCollector = (NfrResultCollector) c;
//        Iterator<NfrArgument> modelData = (Iterator<NfrArgument>) tableModel.iterator();
//        while (modelData.hasNext()) {
//            NfrArgument arg = modelData.next();
//            if (arg.getName() != null) {
//                nfrResultCollector.addNfrArgument(arg);
//                System.out.println(arg);
//            }
//        }
     
    //}
//    @Override
//    public void modifyTestElement(TestElement args) {
//        GuiUtils.stopTableEditing(stringTable);
//        if (args instanceof NfrResultCollector) {
//            NfrResultCollector arguments = (NfrResultCollector) args;
//            arguments.clear();
//            @SuppressWarnings("unchecked") // only contains Argument (or HTTPArgument)
//            Iterator<NfrArgument> modelData = (Iterator<NfrArgument>) tableModel.iterator();
//            while (modelData.hasNext()) {
//                NfrArgument arg = modelData.next();
//                if(arg.getName()!=null&&!arg.getName().isEmpty())
//                {arguments.addNfrArgument(arg);}
//            }
//            NfrArgument.convertNfrArgumentsToHTTP(arguments);
//        }
//       // super.configureTestElement(args);
//    }

    @Override
    public void configure(TestElement el) {
        super.configure(el);   
        if (el instanceof NfrResultCollector) {
            tableModel.clearData();
            System.out.println(((NfrResultCollector) el).getNfrArguments());
            NfrArgument.convertNfrArgumentsToHTTP((NfrResultCollector) el);
            for (JMeterProperty jMeterProperty : ((NfrResultCollector) el).getNfrArguments()) {
                NfrArgument arg = (NfrArgument) jMeterProperty.getObjectValue();
                tableModel.addRow(arg);
            }
        }
        checkButtonsStatus();
    }
    /**
     * Clear all rows from the table.
     */
    public void clear() {
        GuiUtils.stopTableEditing(stringTable);
        tableModel.clearData();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }
}