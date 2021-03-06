/*
 * Copyright 2006 Antonio S. R. Gomes
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package net.sf.profiler4j.console;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.JComboBox;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JCheckBox;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.Font;
import java.awt.Color;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JToolBar;
import javax.swing.ImageIcon;

import org.apache.commons.lang.SystemUtils;

import net.sf.profiler4j.console.util.Validator;

public class ProjectDialog extends JDialog {

    private JPanel jContentPane = null;
    private JPanel jPanel = null;
    private JButton okButton = null;
    private JButton cancelButton = null;
    private JPanel jPanel1 = null;
    private JPanel exportPanel = null;
    private JPanel exportActivationPanel = null;
    private JPanel exportPatternPanel = null;
    private JCheckBox exportCheckBox = null;
    private Validator validator = new Validator(this);
    private Console app;

    private JTextField exportPatternText = null;

    public ProjectDialog(JFrame owner, Console app) {
        super(owner);
        this.app = app;
        initialize();
    }

    /**
     * This method initializes this
     */
    private void initialize() {
        this.setSize(712, 600);
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        this.setModal(true);
        this.setResizable(false);
        this.setTitle("Profiling Project Details");
        this.setContentPane(getJContentPane());
        setLocationRelativeTo(null);
    }

    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(new BorderLayout());
            jContentPane.add(getJPanel(), java.awt.BorderLayout.SOUTH);
            jContentPane.add(getJPanel1(), java.awt.BorderLayout.CENTER);
        }
        return jContentPane;
    }

    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel() {
        if (jPanel == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setAlignment(java.awt.FlowLayout.RIGHT);
            jPanel = new JPanel();
            jPanel.setLayout(flowLayout);
            jPanel.add(getOkButton(), null);
            jPanel.add(getJButton(), null);
        }
        return jPanel;
    }

    /**
     * This method initializes okButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getOkButton() {
        if (okButton == null) {
            okButton = new JButton();
            okButton.setText("Ok");
            okButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (validator.validate(true)) {
                        edited = true;
                        setVisible(false);
                    }
                }
            });
        }
        return okButton;
    }

    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getJButton() {
        if (cancelButton == null) {
            cancelButton = new JButton();
            cancelButton.setText("Cancel");
            cancelButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    setVisible(false);
                }
            });
        }
        return cancelButton;
    }

    /**
     * This method initializes jPanel1
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel1() {
        if (jPanel1 == null) {
            jPanel1 = new JPanel();
            jPanel1.setLayout(null);
            jPanel1.add(getJPanel3(), null);
        }
        return jPanel1;
    }

    private boolean edited = false;
    private JScrollPane tableScrollPane = null;
    private JTable rulesTable = null;
    private JComboBox accessComboBox = null;
    private JPanel defaultOptionsPanel = null;
    private JLabel jLabel = null;
    private JCheckBox beanpropsCheckBox = null;
    private JPanel rulesPanel = null;
    private JToolBar toolBar = null;
    private JButton addRuleButton = null;
    private JButton removeRuleButton = null;
    private JButton moveRuleUpButton = null;
    private JButton moveRuleDownButton = null;

    /**
     * This method initializes tableScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getTableScrollPane() {
        if (tableScrollPane == null) {
            tableScrollPane = new JScrollPane();
            tableScrollPane.setBorder(null);
            tableScrollPane.setViewportView(getRulesTable());
        }
        return tableScrollPane;
    }

    RuleTableModel ruleTableModel = new RuleTableModel(); // @jve:decl-index=0:visual-constraint="861,7"
    private JPanel jPanel3 = null;
    private InfoPanel infoPanel = null;
    private JPanel connectionPanel = null;
    private JLabel hostLabel = null;
    private JTextField hostTextField = null;
    private JTextField portTextField = null;
    private JLabel portLabel = null;

    /**
     * This method initializes rulesTable
     * 
     * @return javax.swing.JTable
     */
    private JTable getRulesTable() {
        if (rulesTable == null) {
            rulesTable = new JTable();
            rulesTable.setModel(ruleTableModel);
            rulesTable.setRowMargin(4);
            rulesTable.setRowHeight(24);
            rulesTable.setFont(new Font("Monospaced", Font.PLAIN, 14));
            TableColumn c;

            c = rulesTable.getColumnModel().getColumn(0);
            c.setMinWidth(300);

            c = rulesTable.getColumnModel().getColumn(1);
            c.setMinWidth(80);
            c.setMaxWidth(80);

            JComboBox editorCb = new JComboBox();
            for (Rule.Action a : Rule.Action.values()) {
                editorCb.addItem(a);
            }
            c.setCellEditor(new DefaultCellEditor(editorCb));
            c.setCellRenderer(new DefaultTableCellRenderer() {
                Font font = new Font("Monospaced", Font.BOLD, 13);
                @Override
                public Component getTableCellRendererComponent(JTable table,
                                                               Object value,
                                                               boolean isSelected,
                                                               boolean hasFocus,
                                                               int row,
                                                               int column) {
                    super.getTableCellRendererComponent(table,
                                                        value,
                                                        isSelected,
                                                        hasFocus,
                                                        row,
                                                        column);
                    Rule.Action r = (Rule.Action) value;
                    setHorizontalAlignment(CENTER);
                    setFont(font);
                    if (r == Rule.Action.ACCEPT) {
                        setBackground(Color.GREEN);
                    } else {
                        setBackground(Color.RED);
                    }
                    if (isSelected) {
                        setForeground(Color.YELLOW);
                    } else {
                        setForeground(Color.BLACK);
                    }
                    return this;
                }
            });

        }
        return rulesTable;
    }

    /**
     * This method initializes accessComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getAccessComboBox() {
        if (accessComboBox == null) {
            accessComboBox = new JComboBox();
            accessComboBox.setBounds(new java.awt.Rectangle(152, 32, 105, 23));
            for (Rule.AccessOption op : Rule.AccessOption.values()) {
                accessComboBox.addItem(op);
            }
        }
        return accessComboBox;
    }

    /**
     * This method initializes defaultOptionsPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getDefaultOptionsPanel() {
        if (defaultOptionsPanel == null) {
            jLabel = new JLabel();
            jLabel.setText("Minimum method visibility");
            jLabel.setBounds(new java.awt.Rectangle(16, 32, 129, 22));
            defaultOptionsPanel = new JPanel();
            defaultOptionsPanel.setLayout(null);
            defaultOptionsPanel.setBorder(BorderFactory
                .createTitledBorder(null,
                                    "Rule Options (applies to all ACCEPT rules)",
                                    TitledBorder.DEFAULT_JUSTIFICATION,
                                    TitledBorder.DEFAULT_POSITION,
                                    new Font("Tahoma", Font.PLAIN, 11),
                                    new Color(0, 70, 213)));
            defaultOptionsPanel.setBounds(new java.awt.Rectangle(408, 56, 289, 103));
            defaultOptionsPanel.add(jLabel, null);
            defaultOptionsPanel.add(getAccessComboBox(), null);
            defaultOptionsPanel.add(getBeanpropsCheckBox(), null);
        }
        return defaultOptionsPanel;
    }

    /**
     * This method initializes beanpropsCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getBeanpropsCheckBox() {
        if (beanpropsCheckBox == null) {
            beanpropsCheckBox = new JCheckBox();
            beanpropsCheckBox.setText("Include getters/setters");
            beanpropsCheckBox.setBounds(new java.awt.Rectangle(16, 64, 137, 23));
        }
        return beanpropsCheckBox;
    }

    /**
     * This method initializes jPanel2
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getRulesPanel() {
        if (rulesPanel == null) {
            rulesPanel = new JPanel();
            rulesPanel.setLayout(new BorderLayout());
            rulesPanel.setBounds(new java.awt.Rectangle(7, 165, 691, 211));
            rulesPanel
                .setBorder(javax.swing.BorderFactory
                    .createTitledBorder(null,
                                        "Method Rules (evaluated top-down)",
                                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                                        javax.swing.border.TitledBorder.DEFAULT_POSITION,
                                        new java.awt.Font("Tahoma", java.awt.Font.PLAIN,
                                                11),
                                        new java.awt.Color(0, 70, 213)));
            rulesPanel.add(getTableScrollPane(), BorderLayout.CENTER);
            rulesPanel.add(getToolBar(), BorderLayout.NORTH);
        }
        return rulesPanel;
    }

    /**
     * This method initializes toolBar
     * 
     * @return javax.swing.JToolBar
     */
    private JToolBar getToolBar() {
        if (toolBar == null) {
            toolBar = new JToolBar();
            toolBar.setFloatable(false);
            toolBar.add(getAddRuleButton());
            toolBar.add(getRemoveRuleButton());
            toolBar.add(getMoveRuleDownButton());
            toolBar.add(getMoveRuleUpButton());
        }
        return toolBar;
    }

    /**
     * This method initializes addRuleButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddRuleButton() {
        if (addRuleButton == null) {
            addRuleButton = new JButton();
            addRuleButton.setIcon(new ImageIcon(getClass()
                .getResource("/net/sf/profiler4j/console/images/plus.gif")));
            addRuleButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    int row = rulesTable.getSelectedRow();
                    ruleTableModel.insert(row, new Rule("*(*)", Rule.Action.ACCEPT));
                }
            });
        }
        return addRuleButton;
    }

    /**
     * This method initializes removeRuleButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getRemoveRuleButton() {
        if (removeRuleButton == null) {
            removeRuleButton = new JButton();
            removeRuleButton.setIcon(new ImageIcon(getClass()
                .getResource("/net/sf/profiler4j/console/images/delete.gif")));
            removeRuleButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    int row = rulesTable.getSelectedRow();
                    if (row != -1) {
                        ruleTableModel.remove(row);
                        rulesTable.getSelectionModel().addSelectionInterval(row, row);
                    }
                }
            });
        }
        return removeRuleButton;
    }

    /**
     * This method initializes moveRuleUpButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getMoveRuleUpButton() {
        if (moveRuleUpButton == null) {
            moveRuleUpButton = new JButton();
            moveRuleUpButton.setIcon(new ImageIcon(getClass()
                .getResource("/net/sf/profiler4j/console/images/up.gif")));
            moveRuleUpButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    int row = rulesTable.getSelectedRow();
                    if (row != -1) {
                        ruleTableModel.moveUp(row);
                        row = Math.max(row - 1, 0);
                        rulesTable.getSelectionModel().addSelectionInterval(row, row);
                    }
                }
            });
        }
        return moveRuleUpButton;
    }
    /**
     * This method initializes moveRuleDownButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getMoveRuleDownButton() {
        if (moveRuleDownButton == null) {
            moveRuleDownButton = new JButton();
            moveRuleDownButton.setIcon(new ImageIcon(getClass()
                .getResource("/net/sf/profiler4j/console/images/down.gif")));
            moveRuleDownButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    int row = rulesTable.getSelectedRow();
                    if (row != -1) {
                        ruleTableModel.moveDown(row);
                        row = Math.min(row + 1, ruleTableModel.getRowCount());
                        rulesTable.getSelectionModel().addSelectionInterval(row, row);
                    }
                }
            });
        }
        return moveRuleDownButton;
    }

    /**
     * This method initializes exportPanel, which shows the options
     * to enable the automatic export of the call graph to an imagefile.
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getExportPanel() {
        if (exportPanel == null) {
            exportPanel = new JPanel();
            exportPanel.setLayout(new BoxLayout(exportPanel, BoxLayout.Y_AXIS));
            exportPanel.setBounds(new java.awt.Rectangle(7, 
                                                         390,
                                                         691, 
                                                         101));
            exportPanel
                .setBorder(javax.swing.BorderFactory
                    .createTitledBorder(null,
                                        "Automatic Export",
                                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                                        javax.swing.border.TitledBorder.DEFAULT_POSITION,
                                        new java.awt.Font("Tahoma", java.awt.Font.PLAIN,
                                                11),
                                        new java.awt.Color(0, 70, 213)));
            exportPanel.add(getExportActivationPanel());
            exportPanel.add(getExportPatternPanel());
        }
        return exportPanel;
    }
    
    private JPanel getExportActivationPanel() {
        if (exportActivationPanel == null) {
            exportActivationPanel = new JPanel();
            exportActivationPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
            exportActivationPanel.setBounds(new java.awt.Rectangle(7, 
                                                         490,
                                                         691, 
                                                         111));
            exportActivationPanel.add(getExportCheckBox());
        }
        return exportActivationPanel;
    }
    
    JCheckBox getExportCheckBox() {
        if (null == exportCheckBox) {
            exportCheckBox = new JCheckBox();
            exportCheckBox.setSelected(false);
            exportCheckBox.setText("Export call graph on each snapshot");
            exportCheckBox.addChangeListener(new ChangeListener() {
                
                public void stateChanged(ChangeEvent e) {
                    exportPatternText.setEnabled(exportCheckBox.isSelected());
                }
            });
        }
        return exportCheckBox;
    }
    
    private JPanel getExportPatternPanel() {
        if (exportPatternPanel == null) {
            exportPatternPanel = new JPanel();
            exportPatternPanel.setLayout(new BoxLayout(exportPatternPanel,BoxLayout.Y_AXIS));
            exportPatternPanel.setBounds(new java.awt.Rectangle(
                                                         7, 
                                                         490,
                                                         691, 
                                                         50));
            exportPatternPanel.add(new JLabel("Specify a pattern for the image files to be named."));
            exportPatternPanel.add(getExportPatternText());
        }
        return exportPatternPanel;
    }
    
    JTextField getExportPatternText() {
        if (null == exportPatternText) {
            
            exportPatternText = new JTextField();
            exportPatternText.setBounds(new Rectangle(7,510,691, 40));
            exportPatternText.setEnabled(false);
        }
        return exportPatternText;
    }

    /**
     * This method initializes jPanel3
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel3() {
        if (jPanel3 == null) {
            jPanel3 = new JPanel();
            jPanel3.setLayout(null);
            jPanel3.setBounds(new java.awt.Rectangle(0, 0, 706, 581));
            jPanel3.add(getDefaultOptionsPanel(), null);
            jPanel3.add(getRulesPanel(), null);
            jPanel3.add(getHeadingPanel(), null);
            jPanel3.add(getConnectionPanel(), null);
            jPanel3.add(getExportPanel(), null);
        }
        return jPanel3;
    }

    /**
     * This method initializes infoPanel
     * 
     * @return net.sf.profiler4j.console.InfoPanel
     */
    private InfoPanel getHeadingPanel() {
        if (infoPanel == null) {
            infoPanel = new InfoPanel();
            infoPanel.setTitle("Bytecode Instrumentation Rules");
            infoPanel.setBounds(new java.awt.Rectangle(0, 0, 706, 46));
            infoPanel.setDescription("Please specify the rules to be used by the "
                    + "profiling agent in the remote JVM");
        }
        return infoPanel;
    }

    /**
     * This method initializes jPanel4
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getConnectionPanel() {
        if (connectionPanel == null) {
            portLabel = new JLabel();
            portLabel.setBounds(new java.awt.Rectangle(16, 64, 49, 17));
            portLabel.setText("Port");
            hostLabel = new JLabel();
            hostLabel.setText("Host");
            hostLabel.setBounds(new java.awt.Rectangle(16, 32, 49, 17));
            connectionPanel = new JPanel();
            connectionPanel.setLayout(null);
            connectionPanel.setBounds(new java.awt.Rectangle(16, 56, 385, 103));
            connectionPanel
                .setBorder(javax.swing.BorderFactory
                    .createTitledBorder(null,
                                        "Remote JVM",
                                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                                        javax.swing.border.TitledBorder.DEFAULT_POSITION,
                                        null,
                                        null));
            connectionPanel.add(hostLabel, null);
            connectionPanel.add(getHostTextField(), null);
            connectionPanel.add(getPortTextField(), null);
            connectionPanel.add(portLabel, null);
        }
        return connectionPanel;
    }

    /**
     * This method initializes hostTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getHostTextField() {
        if (hostTextField == null) {
            hostTextField = new JTextField();
            hostTextField.setBounds(new java.awt.Rectangle(64, 37, 297, 19));
            validator.newNonEmpty(hostTextField);
        }
        return hostTextField;
    }

    /**
     * This method initializes jTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getPortTextField() {
        if (portTextField == null) {
            portTextField = new JTextField();
            portTextField.setBounds(new java.awt.Rectangle(64, 64, 49, 19));
            validator.newInteger(portTextField, 1024, 65535);
        }
        return portTextField;
    }

    public boolean edit(Project p) {

        beanpropsCheckBox.setSelected(p.isBeanprops());
        accessComboBox.setSelectedItem(p.getAccess());
        hostTextField.setText(p.getHostname());
        portTextField.setText(String.valueOf(p.getPort()));

        if (app.getClient().isConnected()) {
            hostTextField.setEnabled(false);
            portTextField.setEnabled(false);
        }

        for (Rule r : p.getRules()) {
            ruleTableModel.insert(r);
        }
        
        exportCheckBox.setSelected(p.isExportAutomaticallyEnabled());
        exportPatternText.setText(p.getExportPattern());

        setVisible(true);

        // (mk) I really don't understand why this works - shouldn't all 
        // values possibly edited by the user have been overwritten in the
        // UI updates above? This method should basically perform a sync
        // with the UI, reading from the given project, not updating it.
        if (edited) {
            p.setHostname(hostTextField.getText());
            p.setPort(Integer.parseInt(portTextField.getText()));
            p.setBeanprops(beanpropsCheckBox.isSelected());
            p.setAccess((Rule.AccessOption) accessComboBox.getSelectedItem());
            p.getRules().clear();
            for (Rule r : ruleTableModel.getRules()) {
                p.getRules().add(r);
            }
            p.setExportAutomaticallyEnabled(exportCheckBox.isSelected());
            p.setExportPattern(exportPatternText.getText());
        }
        
        return edited;
    }

} // @jve:decl-index=0:visual-constraint="13,0"

