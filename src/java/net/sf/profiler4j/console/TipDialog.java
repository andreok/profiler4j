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

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;

import org.jdom.Element;

/**
 * CLASS_COMMENT
 * 
 * @author Antonio S. R. Gomes
 */
public class TipDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private JPanel jContentPane = null;
    private JScrollPane jScrollPane = null;
    private JTextPane messageTextPane = null;
    private JButton closeButton = null;
    private JButton nextButton = null;
    private JButton previousButton = null;
    private JCheckBox showNextTimeCheckBox = null;
    /**
     * @param owner
     */
    public TipDialog(Frame owner) {
        super(owner);
        initialize();
    }
    /**
     * This method initializes this
     */
    private void initialize() {
        this.setSize(453, 348);
        this.setModal(true);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setResizable(true);
        this.setTitle("Tip of the Day");
        this.setContentPane(getJContentPane());
    }
    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.insets = new Insets(4, 8, 4, 8);
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.gridy = 1;
            gridBagConstraints4.anchor = GridBagConstraints.WEST;
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.gridwidth = 4;
            GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
            gridBagConstraints3.insets = new Insets(4, 8, 8, 8);
            gridBagConstraints3.gridy = 2;
            gridBagConstraints3.gridx = 0;
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.insets = new Insets(4, 4, 8, 8);
            gridBagConstraints2.gridy = 2;
            gridBagConstraints2.anchor = GridBagConstraints.WEST;
            gridBagConstraints2.gridx = 1;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.insets = new Insets(4, 8, 8, 8);
            gridBagConstraints1.gridy = 2;
            gridBagConstraints1.anchor = GridBagConstraints.EAST;
            gridBagConstraints1.gridx = 2;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.gridwidth = 3;
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.ipadx = 393;
            gridBagConstraints.ipady = 177;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.weighty = 1.0;
            gridBagConstraints.insets = new Insets(8, 8, 8, 8);
            jContentPane = new JPanel();
            jContentPane.setLayout(new GridBagLayout());
            jContentPane.add(getJScrollPane(), gridBagConstraints);
            jContentPane.add(getCloseButton(), gridBagConstraints1);
            jContentPane.add(getNextButton(), gridBagConstraints2);
            jContentPane.add(getPreviousButton(), gridBagConstraints3);
            jContentPane.add(getShowNextTimeCheckBox(), gridBagConstraints4);
        }
        return jContentPane;
    }
    /**
     * This method initializes jScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane() {
        if (jScrollPane == null) {
            jScrollPane = new JScrollPane();
            jScrollPane.setViewportView(getMessageTextPane());
        }
        return jScrollPane;
    }
    /**
     * This method initializes messageTextPane
     * 
     * @return javax.swing.JTextPane
     */
    private JTextPane getMessageTextPane() {
        if (messageTextPane == null) {
            messageTextPane = new JTextPane();
            messageTextPane.setBackground(SystemColor.info);
            messageTextPane.setEditable(false);
            messageTextPane.setMargin(new Insets(8, 8, 8, 8));
            messageTextPane.setContentType("text/html");
        }
        return messageTextPane;
    }
    /**
     * This method initializes closeButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getCloseButton() {
        if (closeButton == null) {
            closeButton = new JButton();
            closeButton.setText("Close");
            closeButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    setVisible(false);
                }
            });
        }
        return closeButton;
    }
    /**
     * This method initializes nextButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getNextButton() {
        if (nextButton == null) {
            nextButton = new JButton();
            nextButton.setToolTipText("Show next tip");
            nextButton.setIcon(new ImageIcon(getClass()
                .getResource("/net/sf/profiler4j/console/images/next.gif")));
            nextButton.setText("Next");
            nextButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    index++;
                    updateNav();
                }
            });
        }
        return nextButton;
    }
    /**
     * This method initializes previousButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getPreviousButton() {
        if (previousButton == null) {
            previousButton = new JButton();
            previousButton.setToolTipText("Show previous tip");
            previousButton.setIcon(new ImageIcon(getClass()
                .getResource("/net/sf/profiler4j/console/images/previous.gif")));
            previousButton.setText("Previous");
            previousButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    index--;
                    updateNav();
                }
            });
        }
        return previousButton;
    }
    /**
     * This method initializes showNextTimeCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getShowNextTimeCheckBox() {
        if (showNextTimeCheckBox == null) {
            showNextTimeCheckBox = new JCheckBox();
            showNextTimeCheckBox.setSelected(true);
            showNextTimeCheckBox.setText("Show random tips when the application starts");
            showNextTimeCheckBox.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    System.out.println(app);
                    System.out.println(app.getPrefs());
                    app.getPrefs().setShowTip(showNextTimeCheckBox.isSelected());
                }
            });
        }
        return showNextTimeCheckBox;
    }

    private List tips; // @jve:decl-index=0:
    private int index;
    private Console app;

    public void showTip(Console app) {
        this.app = app;
        setLocationRelativeTo(getOwner());
        if (app.getTipDoc() != null) {
            showNextTimeCheckBox.setSelected(app.getPrefs().isShowTip());
            Element rootElement = app.getTipDoc().getRootElement();
            tips = new ArrayList(rootElement.getChildren());
            Collections.shuffle(tips);
            index = 0;
            updateNav();
            setVisible(true);
        }
        dispose();
    }

    private void updateNav() {
        Element el = (Element) tips.get(index);
        messageTextPane.setText("<html>" + el.getText());
        messageTextPane.setCaretPosition(0);
        previousButton.setEnabled(index > 0);
        nextButton.setEnabled(index < (tips.size() - 1));
    }

} // @jve:decl-index=0:visual-constraint="10,10"
