/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.log4j.chainsaw;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.Level;

/**
 * Represents the controls for filtering, pausing, exiting, etc.
 *
 * @author <a href="mailto:oliver@puppycrawl.com">Oliver Burn</a>
 */
class ControlPanel extends JPanel {
    /** use the log messages **/
    private static final Logger LOG = 
                                  Logger.getLogger(ControlPanel.class);

    /**
     * Creates a new <code>ControlPanel</code> instance.
     *
     * @param aModel the model to control
     */
    ControlPanel(final MyTableModel aModel) {
        setBorder(BorderFactory.createTitledBorder("Controls: "));
        final GridBagLayout gridbag = new GridBagLayout();
        final GridBagConstraints c = new GridBagConstraints();
        setLayout(gridbag);

        // Pad everything
        c.ipadx = 5;
        c.ipady = 5;

        // Add the 1st column of labels
        c.gridx = 0;
        c.anchor = GridBagConstraints.EAST;

        c.gridy = 0;
        JLabel label = new JLabel("Filter Level:");
        gridbag.setConstraints(label, c);
        add(label);

        c.gridy++;
        label = new JLabel("Filter Thread:");
        gridbag.setConstraints(label, c);
        add(label);

        c.gridy++;
        label = new JLabel("Filter Logger:");
        gridbag.setConstraints(label, c);
        add(label);

        c.gridy++;
        label = new JLabel("Filter NDC:");
        gridbag.setConstraints(label, c);
        add(label);

        c.gridy++;
        label = new JLabel("Filter Message:");
        gridbag.setConstraints(label, c);
        add(label);

        // Add the 2nd column of filters
        c.weightx = 1;
        //c.weighty = 1;
        c.gridx = 1;
        c.anchor = GridBagConstraints.WEST;

        c.gridy = 0;
        final Level[] allPriorities = new Level[] {Level.FATAL, 
               Level.ERROR, 
               Level.WARN, 
			   Level.INFO, 
			   Level.DEBUG, 
			   Level.TRACE };
        
        final JComboBox priorities = new JComboBox(allPriorities);
        final Level lowest = allPriorities[allPriorities.length - 1];
        priorities.setSelectedItem(lowest);
        aModel.setPriorityFilter(lowest);
        gridbag.setConstraints(priorities, c);
        add(priorities);
        priorities.setEditable(false);
        priorities.addActionListener(new ControlPanelActionListener1(priorities, aModel));

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy++;
        final JTextField threadField = new JTextField("");
        threadField.getDocument()
          .addDocumentListener(new ControlPanelDocumentListener1(threadField, aModel));
        gridbag.setConstraints(threadField, c);
        add(threadField);

        c.gridy++;
        final JTextField catField = new JTextField("");
        catField.getDocument()
          .addDocumentListener(new ControlPanelDocumentListener2(catField, aModel));
        gridbag.setConstraints(catField, c);
        add(catField);

        c.gridy++;
        final JTextField ndcField = new JTextField("");
        ndcField.getDocument()
          .addDocumentListener(new ControlPanelDocumentListener3(ndcField, aModel));
        gridbag.setConstraints(ndcField, c);
        add(ndcField);

        c.gridy++;
        final JTextField msgField = new JTextField("");
        msgField.getDocument()
          .addDocumentListener(new ControlPanelDocumentListener4(msgField, aModel));
        gridbag.setConstraints(msgField, c);
        add(msgField);

        // Add the 3rd column of buttons
        c.weightx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.EAST;
        c.gridx = 2;

        c.gridy = 0;
        final JButton exitButton = new JButton("Exit");
        exitButton.setMnemonic('x');
        exitButton.addActionListener(ExitAction.INSTANCE);
        gridbag.setConstraints(exitButton, c);
        add(exitButton);

        c.gridy++;
        final JButton clearButton = new JButton("Clear");
        clearButton.setMnemonic('c');
        clearButton.addActionListener(new ControlPanelActionListener2(aModel));
        gridbag.setConstraints(clearButton, c);
        add(clearButton);

        c.gridy++;
        final JButton toggleButton = new JButton("Pause");
        toggleButton.setMnemonic('p');
        toggleButton.addActionListener(new ControlPanelActionListener3(toggleButton, aModel));
        gridbag.setConstraints(toggleButton, c);
        add(toggleButton);
    }
}

class ControlPanelActionListener1 implements ActionListener {
  public ControlPanelActionListener1 (JComboBox priorities, MyTableModel aModel) {
    this.priorities = priorities;
    this.aModel = aModel;
  }
  public void actionPerformed(ActionEvent aEvent) {
    aModel.setPriorityFilter((Priority) priorities.getSelectedItem());
  }
  private JComboBox priorities;
  private MyTableModel aModel;
}

class ControlPanelActionListener2 implements ActionListener {
  public ControlPanelActionListener2 (MyTableModel aModel) {
    this.aModel = aModel;
  }
  public void actionPerformed(ActionEvent aEvent) {
    aModel.clear();
  }
  private MyTableModel aModel;
}

class ControlPanelActionListener3 implements ActionListener {
  public ControlPanelActionListener3 (JButton toggleButton, MyTableModel aModel) {
    this.toggleButton = toggleButton;
    this.aModel = aModel;
  }
  public void actionPerformed(ActionEvent aEvent) {
    aModel.toggle();
    if (aModel.isPaused()) toggleButton.setText("Resume");
    else toggleButton.setText("Pause");
  }
  private JButton toggleButton;
  private MyTableModel aModel;
}

class ControlPanelDocumentListener1 implements DocumentListener {
  public ControlPanelDocumentListener1 (JTextField threadField, MyTableModel aModel) {
    this.threadField = threadField;
    this.aModel = aModel;
  }
  public void insertUpdate(DocumentEvent aEvent) {
    aModel.setThreadFilter(threadField.getText());
  }
  public void removeUpdate(DocumentEvent aEvente) {
    aModel.setThreadFilter(threadField.getText());
  }
  public void changedUpdate(DocumentEvent aEvent) {
    aModel.setThreadFilter(threadField.getText());
  }
  private JTextField threadField;
  private MyTableModel aModel;
}

class ControlPanelDocumentListener2 implements DocumentListener {
  public ControlPanelDocumentListener2 (JTextField catField, MyTableModel aModel) {
    this.catField = catField;
    this.aModel = aModel;
  }
  public void insertUpdate(DocumentEvent aEvent) {
    aModel.setCategoryFilter(catField.getText());
  }
  public void removeUpdate(DocumentEvent aEvent) {
    aModel.setCategoryFilter(catField.getText());
  }
  public void changedUpdate(DocumentEvent aEvent) {
    aModel.setCategoryFilter(catField.getText());
  }
  private JTextField catField;
  private MyTableModel aModel;
}

class ControlPanelDocumentListener3 implements DocumentListener {
  public ControlPanelDocumentListener3 (JTextField ndcField, MyTableModel aModel) {
    this.ndcField = ndcField;
    this.aModel = aModel;
  }
  public void insertUpdate(DocumentEvent aEvent) {
    aModel.setNDCFilter(ndcField.getText());
  }
  public void removeUpdate(DocumentEvent aEvent) {
    aModel.setNDCFilter(ndcField.getText());
  }
  public void changedUpdate(DocumentEvent aEvent) {
    aModel.setNDCFilter(ndcField.getText());
  }
  private JTextField ndcField;
  private MyTableModel aModel;
}

class ControlPanelDocumentListener4 implements DocumentListener {
  public ControlPanelDocumentListener4 (JTextField msgField, MyTableModel aModel) {
    this.msgField = msgField;
    this.aModel = aModel;
  }
  public void insertUpdate(DocumentEvent aEvent) {
    aModel.setMessageFilter(msgField.getText());
  }
  public void removeUpdate(DocumentEvent aEvent) {
    aModel.setMessageFilter(msgField.getText());
  }
  public void changedUpdate(DocumentEvent aEvent) {
    aModel.setMessageFilter(msgField.getText());
  }
  private JTextField msgField;
  private MyTableModel aModel;
}

