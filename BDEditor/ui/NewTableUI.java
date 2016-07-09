/*
 * Created on Feb 3, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
/**
 * @author Tup & Titi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

public class NewTableUI extends JFrame {

	/**
	 * 
	 * @uml.property name="fieldsTable"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private JTable fieldsTable;

	/**
	 * 
	 * @uml.property name="fieldsScrollPane"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private JScrollPane fieldsScrollPane;

	/**
	 * 
	 * @uml.property name="nameLabel"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private JLabel nameLabel;

	/**
	 * 
	 * @uml.property name="fieldsLabel"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private JLabel fieldsLabel;

	/**
	 * 
	 * @uml.property name="nameText"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private JTextField nameText;

	/**
	 * 
	 * @uml.property name="newButton"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private JButton newButton;

	/**
	 * 
	 * @uml.property name="editButton"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private JButton editButton;

	/**
	 * 
	 * @uml.property name="deleteButton"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private JButton deleteButton;

	/**
	 * 
	 * @uml.property name="readyButton"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private JButton readyButton;
 
	private static int crtRow = 0;
	private static int crtCol = 0;
	private final static int NR_ROWS = 10;
	private final static int NR_COLS = 2;
	private ActionListener observer;
	private String dbname;
		
	public NewTableUI(ActionListener observer, String dbname)
	{
		super ("New table");
		this.observer = observer;
		this.dbname = dbname;
		FieldsTableModel atm = new FieldsTableModel(NR_ROWS, NR_COLS);
		atm.setColumnName(new String("Name"), 0);
		atm.setColumnName(new String("Type"), 1);
		fieldsTable = new JTable(atm);
		// wrap the fields list into a scrollable pane
		fieldsScrollPane = new JScrollPane(fieldsTable);
		JPanel panel = new JPanel();
		
		nameLabel = new JLabel("Table name:");
		fieldsLabel = new JLabel("Fields:");
		nameText = new JTextField();
		
		newButton = new JButton("New");
		newButton.addActionListener(this.observer);
		editButton = new JButton("Edit");
		deleteButton = new JButton("Delete");
		readyButton = new JButton("Ready");
		readyButton.addActionListener(this.observer);
		
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
		c.gridwidth = 3; // make it 3 columns wide
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
		gridbag.setConstraints(fieldsLabel, c);
		panel.add(fieldsLabel);
		
		c.fill = GridBagConstraints.NONE;
		ins = new Insets (10, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 3; // expand on 3 columns
		c.gridheight = 3; // and 3 rows
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(fieldsScrollPane, c);
		panel.add(fieldsScrollPane);
		
		c.fill = GridBagConstraints.NONE;
		ins = new Insets (10, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 4;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		gridbag.setConstraints(newButton, c);
		panel.add(newButton);

		c.fill = GridBagConstraints.NONE;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 4;
		c.gridy = 2; // put this button under the previous one
		c.gridwidth = 1;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		gridbag.setConstraints(editButton, c);
		panel.add(editButton);

		c.fill = GridBagConstraints.NONE;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 4;
		c.gridy = 3; // put this button under the previous one
		c.gridwidth = 1;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		gridbag.setConstraints(deleteButton, c);
		panel.add(deleteButton);

		c.fill = GridBagConstraints.NONE;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 1;
		c.gridy = 4;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		gridbag.setConstraints(readyButton, c);
		panel.add(readyButton);
		
		getContentPane().add(panel, BorderLayout.CENTER);
		pack();
		setVisible(true);
	}
	
	public void setNextValue(Object val)
	{
		((FieldsTableModel)(fieldsTable.getModel())).setValueAt(val, crtRow, crtCol);
		crtCol++;
		if (crtCol == NR_COLS)
		{
			crtRow++;
			crtCol = 0;
		}
		if (crtRow == NR_ROWS)
			crtRow = 0;
	}
	
	public JButton getNewFieldButton()
	{
		return newButton;
	}

	/**
	 * 
	 * @uml.property name="readyButton"
	 */
	public JButton getReadyButton() {
		return readyButton;
	}

	public Vector getFieldNames()
	{
		int rows = ((FieldsTableModel)(fieldsTable.getModel())).getRowCount();
		Vector fieldNames = new Vector();
		for (int i = 0; i < rows; i++)
		{
			Object data = ((FieldsTableModel)(fieldsTable.getModel())).getValueAt(i, 0);
			if ((data != null) && (data.toString().compareTo("") != 0))
				fieldNames.add(data);  
		}
		
		return fieldNames;
	}
	
	public Vector getFieldTypes()
	{
		int rows = ((FieldsTableModel)(fieldsTable.getModel())).getRowCount();
		Vector fieldTypes = new Vector();
		for (int i = 0; i < rows; i++)
		{
			Object data = ((FieldsTableModel)(fieldsTable.getModel())).getValueAt(i, 1);
			
			if ((data != null) && (data.toString().compareTo("") != 0))
				fieldTypes.add(data);  
		}
		
		return fieldTypes;
	}
	
	public String getTableName()
	{
		return nameText.getText();
	}
	
	public String getDBName()
	{
		return dbname;
	}
}
