/*
 * Copyright 2006 Antonio S. R. Gomes
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *      
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package net.sf.profiler4j.console;

import java.awt.GridBagLayout;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.FlowLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.border.EtchedBorder;
import javax.swing.JTextField;
import java.awt.SystemColor;

/**
 * CLASS_COMMENT
 *
 * @author Antonio S. R. Gomes
 */
public class StatusBarPanel extends JPanel {

    private static final long serialVersionUID = 1L;	
	private JTextField messageTextField = null;
	/**
     * This is the default constructor
     */
    public StatusBarPanel() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.fill = GridBagConstraints.BOTH;
        gridBagConstraints2.gridy = 0;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints2.ipadx = 2;
        gridBagConstraints2.ipady = 2;
        gridBagConstraints2.gridx = 0;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        this.setLayout(new GridBagLayout());
        this.setSize(277, 29);
        this.setPreferredSize(null);
        this.add(getMessageTextField(), gridBagConstraints2);        
    }

	/**
	 * This method initializes messageTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getMessageTextField() {
	    if (messageTextField == null) {
	        messageTextField = new JTextField();
	        messageTextField.setColumns(32);
	        messageTextField.setText(" ");
	        messageTextField.setBackground(SystemColor.control);
	        messageTextField.setEditable(false);
	    }
	    return messageTextField;
	}

    public void setMessage(String s) {
        messageTextField.setText(s);
    }
    
    public String getMessage() {
        return messageTextField.getText();
    }
    
}  //  @jve:decl-index=0:visual-constraint="11,11"
