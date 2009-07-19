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

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import java.awt.Dimension;
import javax.swing.JComboBox;

public class OptionsDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private JPanel jContentPane = null;
    private JPanel httpProxyPanel = null;
    private JLabel jLabel = null;
    private JLabel jLabel2 = null;
    private JTextField httpProxyHostTextField = null;
    private JTextField httpProxyPortTextField = null;
    private JCheckBox httpProxyCheckBox = null;
    private JPanel commandPanel = null;
    private JButton okButton = null;
    private JButton cancelButton = null;
    private JPanel generalPanel = null;
    private JCheckBox checkVersionsCheckBox = null;
    private JCheckBox showTipsOnStartupCheckBox = null;
	private JCheckBox antialiasingCheckBox = null;
	private JLabel jLabel1 = null;
	private JComboBox lafComboBox = null;
	private InfoPanel infoPanel = null;
    /**
     * @param owner
     */
    public OptionsDialog(Frame owner) {
        super(owner);
        initialize();
    }
    /**
     * This method initializes this
     */
    private void initialize() {
        this.setSize(527, 310);
        this.setResizable(false);
        this.setTitle("Options");
        this.setModal(true);
        this.setContentPane(getJContentPane());
    }
    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
            gridBagConstraints13.gridx = 0;
            gridBagConstraints13.gridwidth = 2;
            gridBagConstraints13.fill = GridBagConstraints.BOTH;
            gridBagConstraints13.gridy = 0;
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.gridx = 0;
            gridBagConstraints7.fill = GridBagConstraints.BOTH;
            gridBagConstraints7.anchor = GridBagConstraints.WEST;
            gridBagConstraints7.weightx = 1.0;
            gridBagConstraints7.insets = new Insets(8, 8, 8, 8);
            gridBagConstraints7.gridy = 1;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.gridx = 0;
            gridBagConstraints6.fill = GridBagConstraints.BOTH;
            gridBagConstraints6.anchor = GridBagConstraints.CENTER;
            gridBagConstraints6.weightx = 1.0;
            gridBagConstraints6.insets = new Insets(8, 8, 8, 8);
            gridBagConstraints6.gridwidth = 2;
            gridBagConstraints6.gridy = 3;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.insets = new Insets(8, 8, 8, 8);
            gridBagConstraints5.gridy = 1;
            gridBagConstraints5.ipadx = 0;
            gridBagConstraints5.ipady = 0;
            gridBagConstraints5.weightx = 1.0;
            gridBagConstraints5.fill = GridBagConstraints.BOTH;
            gridBagConstraints5.gridx = 1;
            jContentPane = new JPanel();
            jContentPane.setLayout(new GridBagLayout());
            jContentPane.add(getHttpProxyPanel(), gridBagConstraints5);
            jContentPane.add(getCommandPanel(), gridBagConstraints6);
            jContentPane.add(getGeneralPanel(), gridBagConstraints7);
            jContentPane.add(getInfoPanel(), gridBagConstraints13);
        }
        return jContentPane;
    }
    /**
     * This method initializes httpProxyPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getHttpProxyPanel() {
        if (httpProxyPanel == null) {
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.insets = new Insets(4, 8, 8, 8);
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.gridwidth = 2;
            gridBagConstraints4.gridy = 2;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.gridy = 1;
            gridBagConstraints3.weightx = 1.0;
            gridBagConstraints3.insets = new Insets(4, 4, 4, 8);
            gridBagConstraints3.gridx = 1;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridy = 0;
            gridBagConstraints2.weightx = 1.0;
            gridBagConstraints2.insets = new Insets(8, 4, 4, 8);
            gridBagConstraints2.gridx = 1;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.insets = new Insets(4, 8, 4, 4);
            gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.anchor = GridBagConstraints.EAST;
            gridBagConstraints1.gridy = 1;
            jLabel2 = new JLabel();
            jLabel2.setText("Port");
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.insets = new Insets(8, 8, 4, 4);
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = GridBagConstraints.EAST;
            gridBagConstraints.gridy = 0;
            jLabel = new JLabel();
            jLabel.setText("Host");
            httpProxyPanel = new JPanel();
            httpProxyPanel.setLayout(new GridBagLayout());
            httpProxyPanel.setBorder(BorderFactory
                .createTitledBorder(null,
                                    "HTTP Proxy",
                                    TitledBorder.DEFAULT_JUSTIFICATION,
                                    TitledBorder.DEFAULT_POSITION,
                                    new Font("Tahoma", Font.PLAIN, 11),
                                    new Color(0, 70, 213)));
            httpProxyPanel.setPreferredSize(null);
            httpProxyPanel.add(jLabel, gridBagConstraints);
            httpProxyPanel.add(jLabel2, gridBagConstraints1);
            httpProxyPanel.add(getHttpProxyHostTextField(), gridBagConstraints2);
            httpProxyPanel.add(getHttpProxyPortTextField(), gridBagConstraints3);
            httpProxyPanel.add(getHttpProxyCheckBox(), gridBagConstraints4);
        }
        return httpProxyPanel;
    }
    /**
     * This method initializes httpProxyHostTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getHttpProxyHostTextField() {
        if (httpProxyHostTextField == null) {
            httpProxyHostTextField = new JTextField();
            httpProxyHostTextField.setColumns(20);
        }
        return httpProxyHostTextField;
    }
    /**
     * This method initializes httpProxyPortTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getHttpProxyPortTextField() {
        if (httpProxyPortTextField == null) {
            httpProxyPortTextField = new JTextField();
            httpProxyPortTextField.setColumns(20);
        }
        return httpProxyPortTextField;
    }
    /**
     * This method initializes httpProxyCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getHttpProxyCheckBox() {
        if (httpProxyCheckBox == null) {
            httpProxyCheckBox = new JCheckBox();
            httpProxyCheckBox.setText("Enable HTTP proxy");
            httpProxyCheckBox.setPreferredSize(null);
        }
        return httpProxyCheckBox;
    }
    /**
     * This method initializes commandPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCommandPanel() {
        if (commandPanel == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setAlignment(FlowLayout.RIGHT);
            commandPanel = new JPanel();
            commandPanel.setLayout(flowLayout);
            commandPanel.add(getOkButton(), null);
            commandPanel.add(getCancelButton(), null);
        }
        return commandPanel;
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
        }
        return okButton;
    }
    /**
     * This method initializes cancelButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new JButton();
            cancelButton.setText("Cancel");
            cancelButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                   dispose();
                }
            });
        }
        return cancelButton;
    }
    /**
     * This method initializes generalPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getGeneralPanel() {
        if (generalPanel == null) {
            GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
            gridBagConstraints12.fill = GridBagConstraints.BOTH;
            gridBagConstraints12.gridy = 3;
            gridBagConstraints12.weightx = 1.0;
            gridBagConstraints12.insets = new Insets(8, 8, 8, 8);
            gridBagConstraints12.ipadx = 120;
            gridBagConstraints12.gridx = 1;
            GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.anchor = GridBagConstraints.WEST;
            gridBagConstraints11.insets = new Insets(8, 8, 4, 8);
            gridBagConstraints11.gridy = 3;
            jLabel1 = new JLabel();
            jLabel1.setText("Look and Feel");
            GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
            gridBagConstraints10.gridx = 0;
            gridBagConstraints10.insets = new Insets(4, 8, 8, 8);
            gridBagConstraints10.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints10.gridwidth = 2;
            gridBagConstraints10.gridy = 6;
            GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
            gridBagConstraints9.gridx = 0;
            gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints9.anchor = GridBagConstraints.WEST;
            gridBagConstraints9.insets = new Insets(4, 8, 4, 8);
            gridBagConstraints9.gridwidth = 2;
            gridBagConstraints9.gridy = 4;
            GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
            gridBagConstraints8.anchor = GridBagConstraints.CENTER;
            gridBagConstraints8.insets = new Insets(8, 8, 4, 8);
            gridBagConstraints8.weightx = 1.0;
            gridBagConstraints8.gridx = 0;
            gridBagConstraints8.gridy = 5;
            gridBagConstraints8.gridwidth = 2;
            gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
            generalPanel = new JPanel();
            generalPanel.setLayout(new GridBagLayout());
            generalPanel.setBorder(BorderFactory
                .createTitledBorder(null,
                                    "General Options",
                                    TitledBorder.DEFAULT_JUSTIFICATION,
                                    TitledBorder.DEFAULT_POSITION,
                                    new Font("Tahoma", Font.PLAIN, 11),
                                    new Color(0, 70, 213)));
            generalPanel.add(getCheckVersionsCheckBox(), gridBagConstraints8);
            generalPanel.add(getShowTipsOnStartupCheckBox(), gridBagConstraints9);
            generalPanel.add(getAntialiasingCheckBox(), gridBagConstraints10);
            generalPanel.add(jLabel1, gridBagConstraints11);
            generalPanel.add(getLafComboBox(), gridBagConstraints12);
        }
        return generalPanel;
    }
    /**
     * This method initializes checkVersionsCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getCheckVersionsCheckBox() {
        if (checkVersionsCheckBox == null) {
            checkVersionsCheckBox = new JCheckBox();
            checkVersionsCheckBox.setText("Check for new versions on startup");
        }
        return checkVersionsCheckBox;
    }
    /**
     * This method initializes showTipsOnStartupCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getShowTipsOnStartupCheckBox() {
        if (showTipsOnStartupCheckBox == null) {
            showTipsOnStartupCheckBox = new JCheckBox();
            showTipsOnStartupCheckBox.setText("Show tips when the application starts");
        }
        return showTipsOnStartupCheckBox;
    }
	/**
	 * This method initializes antialiasingCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getAntialiasingCheckBox() {
	    if (antialiasingCheckBox == null) {
	        antialiasingCheckBox = new JCheckBox();
	        antialiasingCheckBox.setText("Use antialiasing");
	    }
	    return antialiasingCheckBox;
	}
	/**
	 * This method initializes lafComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getLafComboBox() {
	    if (lafComboBox == null) {
	        lafComboBox = new JComboBox();
	        lafComboBox.setMaximumRowCount(4);
	    }
	    return lafComboBox;
	}
	/**
	 * This method initializes infoPanel	
	 * 	
	 * @return net.sf.profiler4j.console.InfoPanel	
	 */
	private InfoPanel getInfoPanel() {
	    if (infoPanel == null) {
	        infoPanel = new InfoPanel();
	        infoPanel.setTitle("Global Options");
	        infoPanel.setDescription("Change settings that apply to all profiling sessions");
	    }
	    return infoPanel;
	}

}  //  @jve:decl-index=0:visual-constraint="-61,18"
