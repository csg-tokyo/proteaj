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
package org.apache.log4j.lf5.viewer.categoryexplorer;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.JCheckBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 * CategoryNodeEditor
 *
 * @author Michael J. Sikorsky
 * @author Robert Shaw
 */

// Contributed by ThoughtWorks Inc.

class CategoryNodeEditorCheckBoxActionListener implements ActionListener {
  public CategoryNodeEditorCheckBoxActionListener (CategoryNodeEditor cne) { this.cne = cne; }
  public void actionPerformed(ActionEvent e) {
    cne._categoryModel.update(cne._lastEditedNode, cne._checkBox.isSelected());
    cne.stopCellEditing();
  }
  private CategoryNodeEditor cne;
}

class CategoryNodeEditorRendererMouseListener extends MouseAdapter {
  public CategoryNodeEditorRendererMouseListener (CategoryNodeEditor cne) { this.cne = cne; }
  public void mousePressed(MouseEvent e) {
    if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
      cne.showPopup(cne._lastEditedNode, e.getX(), e.getY());
    }
    cne.stopCellEditing();
  }
  private CategoryNodeEditor cne;
}

class CategoryNodeEditorPropertiesMenuItemActionListener implements ActionListener {
  public CategoryNodeEditorPropertiesMenuItemActionListener (CategoryNodeEditor cne, CategoryNode node) { this.cne = cne; this.node = node; }
  public void actionPerformed(ActionEvent e) {
    cne.showPropertiesDialog(node);
  }
  private CategoryNodeEditor cne;
  private CategoryNode node;
}

class CategoryNodeEditorSelectDescendantsActionListener implements ActionListener {
  public CategoryNodeEditorSelectDescendantsActionListener (CategoryNodeEditor cne, CategoryNode node) { this.cne = cne; this.node = node; }
  public void actionPerformed(ActionEvent e) {
    cne._categoryModel.setDescendantSelection(node, true);
  }
  private CategoryNodeEditor cne;
  private CategoryNode node;
}

class CategoryNodeEditorUnselectDescendantsActionListener implements ActionListener {
  public CategoryNodeEditorUnselectDescendantsActionListener (CategoryNodeEditor cne, CategoryNode node) { this.cne = cne; this.node = node; }
  public void actionPerformed(ActionEvent e) {
    cne._categoryModel.setDescendantSelection(node, false);
  }
  private CategoryNodeEditor cne;
  private CategoryNode node;
}

class CategoryNodeEditorExpandMenuItemActionListener implements ActionListener {
  public CategoryNodeEditorExpandMenuItemActionListener (CategoryNodeEditor cne, CategoryNode node) { this.cne = cne; this.node = node; }
  public void actionPerformed(ActionEvent e) {
    cne.expandDescendants(node);    
  }
  private CategoryNodeEditor cne;
  private CategoryNode node;
}

class CategoryNodeEditorCollapseMenuItemActionListener implements ActionListener {
  public CategoryNodeEditorCollapseMenuItemActionListener (CategoryNodeEditor cne, CategoryNode node) { this.cne = cne; this.node = node; }
  public void actionPerformed(ActionEvent e) {
    cne.collapseDescendants(node);    
  }
  private CategoryNodeEditor cne;
  private CategoryNode node;
}

class CategoryNodeEditorRemoveMenuItemActionListener implements ActionListener {
  public CategoryNodeEditorRemoveMenuItemActionListener (CategoryNodeEditor cne) { this.cne = cne; }
  public void actionPerformed(ActionEvent e) {
    while (cne.removeUnusedNodes() > 0) {}
  }
  private CategoryNodeEditor cne;
}

public class CategoryNodeEditor extends CategoryAbstractCellEditor {
  //--------------------------------------------------------------------------
  //   Constants:
  //--------------------------------------------------------------------------

  //--------------------------------------------------------------------------
  //   Protected Variables:
  //--------------------------------------------------------------------------
  protected CategoryNodeEditorRenderer _renderer;
  protected CategoryNode _lastEditedNode;
  protected JCheckBox _checkBox;
  protected CategoryExplorerModel _categoryModel;
  protected JTree _tree;

  //--------------------------------------------------------------------------
  //   Private Variables:
  //--------------------------------------------------------------------------

  //--------------------------------------------------------------------------
  //   Constructors:
  //--------------------------------------------------------------------------

  public CategoryNodeEditor(CategoryExplorerModel model) {
    _renderer = new CategoryNodeEditorRenderer();
    _checkBox = _renderer.getCheckBox();
    _categoryModel = model;

    _checkBox.addActionListener(new CategoryNodeEditorCheckBoxActionListener(this));

    _renderer.addMouseListener(new CategoryNodeEditorRendererMouseListener(this));
  }

  //--------------------------------------------------------------------------
  //   Public Methods:
  //--------------------------------------------------------------------------

  public Component getTreeCellEditorComponent(JTree tree, Object value,
      boolean selected, boolean expanded,
      boolean leaf, int row) {
    _lastEditedNode = (CategoryNode) value;
    _tree = tree;

    return _renderer.getTreeCellRendererComponent(tree,
        value, selected, expanded,
        leaf, row, true);
    // hasFocus ignored
  }

  public Object getCellEditorValue() {
    return _lastEditedNode.getUserObject();
  }
  //--------------------------------------------------------------------------
  //   Protected Methods:
  //--------------------------------------------------------------------------

  protected JMenuItem createPropertiesMenuItem(final CategoryNode node) {
    JMenuItem result = new JMenuItem("Properties");
    result.addActionListener(new CategoryNodeEditorPropertiesMenuItemActionListener(this, node));
    return result;
  }

  protected void showPropertiesDialog(CategoryNode node) {
    JOptionPane.showMessageDialog(
        _tree,
        getDisplayedProperties(node),
        "Category Properties: " + node.getTitle(),
        JOptionPane.PLAIN_MESSAGE
    );
  }

  protected Object getDisplayedProperties(CategoryNode node) {
    ArrayList result = new ArrayList();
    result.add("Category: " + node.getTitle());
    if (node.hasFatalRecords()) {
      result.add("Contains at least one fatal LogRecord.");
    }
    if (node.hasFatalChildren()) {
      result.add("Contains descendants with a fatal LogRecord.");
    }
    result.add("LogRecords in this category alone: " +
        node.getNumberOfContainedRecords());
    result.add("LogRecords in descendant categories: " +
        node.getNumberOfRecordsFromChildren());
    result.add("LogRecords in this category including descendants: " +
        node.getTotalNumberOfRecords());
    return result.toArray();
  }

  protected void showPopup(CategoryNode node, int x, int y) {
    JPopupMenu popup = new JPopupMenu();
    popup.setSize(150, 400);
    //
    // Configure the Popup
    //
    if (node.getParent() == null) {
      popup.add(createRemoveMenuItem());
      popup.addSeparator();
    }
    popup.add(createSelectDescendantsMenuItem(node));
    popup.add(createUnselectDescendantsMenuItem(node));
    popup.addSeparator();
    popup.add(createExpandMenuItem(node));
    popup.add(createCollapseMenuItem(node));
    popup.addSeparator();
    popup.add(createPropertiesMenuItem(node));
    popup.show(_renderer, x, y);
  }

  protected JMenuItem createSelectDescendantsMenuItem(final CategoryNode node) {
    JMenuItem selectDescendants =
        new JMenuItem("Select All Descendant Categories");
    selectDescendants.addActionListener(new CategoryNodeEditorSelectDescendantsActionListener(this, node));
    return selectDescendants;
  }

  protected JMenuItem createUnselectDescendantsMenuItem(final CategoryNode node) {
    JMenuItem unselectDescendants =
        new JMenuItem("Deselect All Descendant Categories");
    unselectDescendants.addActionListener(new CategoryNodeEditorUnselectDescendantsActionListener(this, node));
    return unselectDescendants;
  }

  protected JMenuItem createExpandMenuItem(final CategoryNode node) {
    JMenuItem result = new JMenuItem("Expand All Descendant Categories");
    result.addActionListener(new CategoryNodeEditorExpandMenuItemActionListener(this, node));
    return result;
  }

  protected JMenuItem createCollapseMenuItem(final CategoryNode node) {
    JMenuItem result = new JMenuItem("Collapse All Descendant Categories");
    result.addActionListener(new CategoryNodeEditorCollapseMenuItemActionListener(this, node));
    return result;
  }

  /**
   * This featured was moved from the LogBrokerMonitor class
   * to the CategoryNodeExplorer so that the Category tree
   * could be pruned from the Category Explorer popup menu.
   * This menu option only appears when a user right clicks on
   * the Category parent node.
   *
   * See removeUnusedNodes()
   */
  protected JMenuItem createRemoveMenuItem() {
    JMenuItem result = new JMenuItem("Remove All Empty Categories");
    result.addActionListener(new CategoryNodeEditorRemoveMenuItemActionListener(this));
    return result;
  }

  protected void expandDescendants(CategoryNode node) {
    Enumeration descendants = node.depthFirstEnumeration();
    CategoryNode current;
    while (descendants.hasMoreElements()) {
      current = (CategoryNode) descendants.nextElement();
      expand(current);
    }
  }

  protected void collapseDescendants(CategoryNode node) {
    Enumeration descendants = node.depthFirstEnumeration();
    CategoryNode current;
    while (descendants.hasMoreElements()) {
      current = (CategoryNode) descendants.nextElement();
      collapse(current);
    }
  }

  /**
   * Removes any inactive nodes from the Category tree.
   */
  protected int removeUnusedNodes() {
    int count = 0;
    CategoryNode root = _categoryModel.getRootCategoryNode();
    Enumeration enumeration = root.depthFirstEnumeration();
    while (enumeration.hasMoreElements()) {
      CategoryNode node = (CategoryNode) enumeration.nextElement();
      if (node.isLeaf() && node.getNumberOfContainedRecords() == 0
          && node.getParent() != null) {
        _categoryModel.removeNodeFromParent(node);
        count++;
      }
    }

    return count;
  }

  protected void expand(CategoryNode node) {
    _tree.expandPath(getTreePath(node));
  }

  protected TreePath getTreePath(CategoryNode node) {
    return new TreePath(node.getPath());
  }

  protected void collapse(CategoryNode node) {
    _tree.collapsePath(getTreePath(node));
  }

  //-----------------------------------------------------------------------
  //   Private Methods:
  //--------------------------------------------------------------------------

  //--------------------------------------------------------------------------
  //   Nested Top-Level Classes or Interfaces:
  //--------------------------------------------------------------------------

}
