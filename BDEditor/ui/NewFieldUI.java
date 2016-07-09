/*
 * Created on Feb 4, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


import actions.TableEditor;

/**
 * @author Tup & Titi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class NewFieldUI extends JFrame {

	/**
	 * 
	 * @uml.property name="nameLabel"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private JLabel nameLabel;

	/**
	 * 
	 * @uml.property name="typeLabel"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private JLabel typeLabel;

	/**
	 * 
	 * @uml.property name="nameText"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private JTextField nameText;

	/**
	 * 
	 * @uml.property name="typeChoice"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private JComboBox typeChoice;

	/**
	 * 
	 * @uml.property name="size"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private JTextField size;

	/**
	 * 
	 * @uml.property name="okButton"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private JButton okButton;
 
	private ActionListener observer;
	
	public NewFieldUI(ActionListener obs)
	{
		super ("New field");
		observer = obs;
		JPanel panel = new JPanel();
		nameLabel = new JLabel("Name:");
		typeLabel = new JLabel("Type:");
		nameText = new JTextField();
		size = new JTextField(3);
		size.setEditable(false);
		okButton = new JButton("OK");
		okButton.addActionListener(observer);
		typeChoice = new JComboBox();
		typeChoice.addItem("VARCHAR");
		typeChoice.addItem("LONGVARCHAR");
		typeChoice.setSelectedIndex(0);
		typeChoice.addItem("INTEGER");
		typeChoice.addItem("CHAR");
		typeChoice.addItem("DATE");
		typeChoice.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					if ((typeChoice.getSelectedItem().toString().compareToIgnoreCase("varchar") == 0) ||
						(typeChoice.getSelectedItem().toString().compareToIgnoreCase("char") == 0)
					   )
						size.setEditable(true);
					else
					{
						size = new JTextField(3);
						size.setEditable(false);
					}
				}
			}
		);
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		panel.setLayout(gridbag);
		
		c.fill = GridBagConstraints.NONE;
		//c.weightx = 1;
		//c.weighty = 0.5;
		Insets ins = new Insets (5, 5, 5, 5); 
		//c.ipady = 10;
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(nameLabel, c);
		panel.add(nameLabel);
				
		// fill its display area horizontally
		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 4; // make it 3 columns wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(nameText, c);
		panel.add(nameText);
				
		c.fill = GridBagConstraints.NONE;
		ins = new Insets (10, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(typeLabel, c);
		panel.add(typeLabel);
		
		c.fill = GridBagConstraints.NONE;
		ins = new Insets (10, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 1; // expand on 2 columns
		c.gridheight = 3; // and 3 rows
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(typeChoice, c);
		panel.add(typeChoice);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		ins = new Insets (10, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 3;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(size, c);
		panel.add(size);

		c.fill = GridBagConstraints.NONE;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 4; 
		c.gridwidth = 1;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		gridbag.setConstraints(okButton, c);
		panel.add(okButton);

		getContentPane().add(panel, BorderLayout.CENTER);
		pack();
		setVisible(true);
	}

	/**
	 * 
	 * @uml.property name="okButton"
	 */
	public JButton getOkButton() {
		return okButton;
	}

	
	public String getNameField()
	{
		return nameText.getText();
	}
	
	public String getTypeField()
	{
		return (((typeChoice.getSelectedItem().toString().compareToIgnoreCase("varchar") == 0) ||
				 (typeChoice.getSelectedItem().toString().compareToIgnoreCase("char") == 0)
			    ) 
			    ?
				typeChoice.getSelectedItem().toString() + "(" + size.getText() + ")"
				:
				typeChoice.getSelectedItem().toString()
			   );
	}

	/**
	 *  
	 * @uml.property name="newfieldui"
	 * @uml.associationEnd inverse="newFieldUI:actions.TableEditor" multiplicity="(0 1)"
	 * 
	 */
	private TableEditor newfieldui;

	/**
	 *  
	 * @uml.property name="newfieldui"
	 * 
	 */
	public TableEditor getNewfieldui() {
		return newfieldui;
	}

	/**
	 *  
	 * @uml.property name="newfieldui"
	 * 
	 */
	public void setNewfieldui(TableEditor newfieldui) {
		this.newfieldui = newfieldui;
	}

}
