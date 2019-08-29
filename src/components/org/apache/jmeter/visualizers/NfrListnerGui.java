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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.jmeter.config.NfrArgument;
import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.gui.tree.JMeterTreeNode;
import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
import org.apache.jmeter.gui.util.TextAreaCellRenderer;
import org.apache.jmeter.gui.util.TextAreaTableCellEditor;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.reporters.AbstractListenerElement;
import org.apache.jmeter.reporters.NfrArguments;
import org.apache.jmeter.samplers.Clearable;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.testelement.property.JMeterProperty;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.visualizers.gui.AbstractListenerGui;
import org.apache.jorphan.gui.GuiUtils;
import org.apache.jorphan.gui.ObjectTableModel;
import org.apache.jorphan.reflect.Functor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interface for Non-function-requirement Test.
 */
/**
 * @author sqq94
 *
 */
public class NfrListnerGui extends AbstractListenerGui
        implements Clearable, ActionListener, Visualizer, ChangeListener {
    private static final long serialVersionUID = 242L;
    private final JCheckBox useGroupName = new JCheckBox(JMeterUtils.getResString("aggregate_graph_use_group_name")); //$NON-NLS-1$
    /** Lock used to protect tableRows update + model update */
    private final transient Object lock = new Object();
    private Deque<SamplingStatCalculator> newRows = new ConcurrentLinkedDeque<>();
    private final JButton add = new JButton("add"); //$NON-NLS-1$
    private final JButton delete = new JButton("delete"); //$NON-NLS-1$
    private final JButton update = new JButton("update"); //$NON-NLS-1$
    private JTable stringTable;
    /** Table model for the pattern table. */
    private ObjectTableModel tableModel;
    protected NfrArguments collector = new NfrArguments();
    /** Logging. */
    private static final Logger log = LoggerFactory.getLogger(NfrListnerGui.class);
    List<HTTPSamplerProxy> NodesOfTypeHTTPSamplerProxy = new ArrayList<>();
    JComboBox<String> nameComboBox = new JComboBox<>();

    @Override
    public boolean isStats() {
        return false;
    }

    protected NfrArguments getModel() {
        return collector;
    }

    /**
     * Invoked when the target of the listener has changed its state. This
     * implementation assumes that the target is the FilePanel, and will update the
     * result collector for the new filename.
     *
     * @param e the event that has occurred
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        log.debug("getting new collector");
        collector = (NfrArguments) createTestElement();
    }

    /* Implements JMeterGUIComponent.createTestElement() */
    @Override
    public TestElement createTestElement() {
        if (collector == null) {
            collector = new NfrArguments();
        }
        modifyTestElement(collector);
        return (TestElement) collector.clone();
    }

    public NfrListnerGui() {
        super();
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
        add.setActionCommand("ADD");
        add.addActionListener(this);
        delete.setActionCommand("DELETE");
        delete.addActionListener(this);
        update.setActionCommand("UPDATE");
        update.addActionListener(this);
        buttonPanel.add(add);
        buttonPanel.add(delete);
        buttonPanel.add(update);
        add.setEnabled(true);
        this.add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    public String getLabelResource() {
        return "non_function_test"; //$NON-NLS-1$
    }

    @Override
    public void add(final SampleResult res) {
        SamplingStatCalculator row = collector.getNfrResult()
                .computeIfAbsent(res.getSampleLabel(useGroupName.isSelected()), label -> {
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
            collector.clearNfrResult();
            newRows.clear();
        }
    }

    /**
     * Initialization of table
     *
     * @return a new panel with the table
     */
    /** A table of patterns to test against. */
    private JScrollPane createStringPanel() {
        tableModel = new ObjectTableModel(new String[] { "Name", "Criteria", "Symbol", "Value", "Message" },
                NfrArgument.class, new Functor[] { new Functor("getName"), // $NON-NLS-1$
                        new Functor("getCriteria"), new Functor("getSymbol"), new Functor("getValue"),
                        new Functor("getMessage") }, // $NON-NLS-1$
                new Functor[] { new Functor("setName"), // $NON-NLS-1$
                        new Functor("setCriteria"), new Functor("setSymbol"), new Functor("setValue"),
                        new Functor("setMessage") }, // $NON-NLS-1$
                new Class[] { String.class, String.class, String.class, String.class, String.class });
        stringTable = new JTable(tableModel);
        stringTable.getTableHeader().setDefaultRenderer(new HeaderAsPropertyRenderer());
        stringTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JMeterUtils.applyHiDPI(stringTable);
        TextAreaCellRenderer renderer = new TextAreaCellRenderer();
        stringTable.setRowHeight(renderer.getPreferredHeight());
        stringTable.setDefaultRenderer(String.class, renderer);
        stringTable.setDefaultEditor(String.class, new TextAreaTableCellEditor());
        stringTable.setPreferredScrollableViewportSize(new Dimension(100, 70));
        JComboBox<String> criteriaComboBox = new JComboBox<>();
        criteriaComboBox.addItem("Avg");
        criteriaComboBox.addItem("Min");
        criteriaComboBox.addItem("Max");
        criteriaComboBox.addItem("Error Rate");
        criteriaComboBox.addItem("Sample Rate");
        stringTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(criteriaComboBox));
        JComboBox<String> symbolComboBox = new JComboBox<>();
        symbolComboBox.addItem(">");
        symbolComboBox.addItem(">=");
        symbolComboBox.addItem("<");
        symbolComboBox.addItem("<=");
        symbolComboBox.addItem("=");
        stringTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(symbolComboBox));
        return new JScrollPane(stringTable);
    }

    private List<HTTPSamplerProxy> findNodesOfTypeHTTPSamplerProxy() {
        JMeterTreeNode parent = (JMeterTreeNode) GuiPackage.getInstance().getCurrentNode().getParent();
        List<HTTPSamplerProxy> listHTTPSamplerProxy = new ArrayList<>();
        if (parent.getTestElement().getClass().equals(HTTPSamplerProxy.class)) {
            listHTTPSamplerProxy.add((HTTPSamplerProxy) parent.getTestElement());
        } else {
            if (parent.getTestElement().getClass().equals(TestPlan.class)) {
                List<JMeterTreeNode> res = GuiPackage.getInstance().getTreeModel()
                        .getNodesOfType(HTTPSamplerProxy.class);
                for (JMeterTreeNode jm : res) {
                    listHTTPSamplerProxy.add((HTTPSamplerProxy) jm.getTestElement());
                }
            } else {
                for (int i = 0; i < parent.getChildCount(); i++) {
                    JMeterTreeNode child = (JMeterTreeNode) parent.getChildAt(i);
                    if (child.getTestElement().getClass().equals(HTTPSamplerProxy.class)) {
                        listHTTPSamplerProxy.add((HTTPSamplerProxy) child.getTestElement());
                    }
                }
            }
        }
        return listHTTPSamplerProxy;
    }

    /**
     * An ActionListener for deleting a row of table
     */
    protected void checkButtonsStatus() {
        // Disable DELETE if there are no rows in the table to delete.
        if (tableModel.getRowCount() == 0) {
            delete.setEnabled(false);
        } else {
            delete.setEnabled(true);
        }
    }

    @Override
    public void modifyTestElement(TestElement args) {
        configureTestElement((AbstractListenerElement) args);
        if (args instanceof NfrArguments) {
            NfrArguments rc = (NfrArguments) args;
            collector = rc;
            @SuppressWarnings("unchecked")
            Iterator<NfrArgument> modelData = (Iterator<NfrArgument>) tableModel.iterator();
            rc.removeAllNfrArguments();
            while (modelData.hasNext()) {
                NfrArgument arg = modelData.next();
                rc.addNfrArgument(arg);
            }
        }
        super.configureTestElement(args);
    }

    @Override
    public void configure(TestElement el) {
        super.configure(el);
        if (el instanceof NfrArguments) {
            NfrArguments rc = (NfrArguments) el;
            if (collector == null) {
                collector = new NfrArguments();
            }
            tableModel.clearData();
            for (JMeterProperty jMeterProperty : rc.getNfrArguments()) {
                NfrArgument arg = (NfrArgument) jMeterProperty.getObjectValue();
                tableModel.addRow(arg);
            }
        }
        checkButtonsStatus();
    }

    protected void configureTestElement(AbstractListenerElement mc) {
        super.configureTestElement(mc);
        mc.setListener(this);
    }

    @Override
    protected Container makeTitlePanel() {
        Container panel = super.makeTitlePanel();
        // Note: the file panel already includes the error logging checkbox,
        // so we don't have to add it explicitly.
        return panel;
    }

    protected void setModel(NfrArguments collector) {
        this.collector = collector;
    }

    /**
     * Clear all rows from the table.
     */
    public void clear() {
        GuiUtils.stopTableEditing(stringTable);
        if (tableModel != null) {
            tableModel.clearData();
        }
    }

    @Override
    public void clearGui() {
        GuiUtils.stopTableEditing(stringTable);
        if (tableModel != null) {
            tableModel.clearData();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("UPDATE")) {
            NodesOfTypeHTTPSamplerProxy = findNodesOfTypeHTTPSamplerProxy();
            nameComboBox.removeAllItems();
            if(findNodesOfTypeHTTPSamplerProxy().isEmpty()){
                stringTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JTextField()));               
            }
            else {
            for (HTTPSamplerProxy httpSamplerProxy : NodesOfTypeHTTPSamplerProxy) {
                nameComboBox.addItem(httpSamplerProxy.getName());
            }
            stringTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(nameComboBox));}
        }
        if (e.getActionCommand().equals("ADD")) {
            GuiUtils.stopTableEditing(stringTable);
            tableModel.addRow(new NfrArgument("", "", "", "", ""));
            checkButtonsStatus();
            // Highlight (select) and scroll to the appropriate row.
            int rowToSelect = tableModel.getRowCount() - 1;
            stringTable.setRowSelectionInterval(rowToSelect, rowToSelect);
            stringTable.scrollRectToVisible(stringTable.getCellRect(rowToSelect, 0, true));
        }
        if (e.getActionCommand().equals("DELETE")) {
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
}
