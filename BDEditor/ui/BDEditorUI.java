/*
 * Created on January 27, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ui;

import actions.*;
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
public class BDEditorUI extends JFrame {

	/**
	 * 
	 * @uml.property name="nameLabel"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private JLabel nameLabel;

	/**
	 * 
	 * @uml.property name="nameText"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private JTextField nameText;

	/**
	 * 
	 * @uml.property name="browseButton"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private JButton browseButton;

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
	 * @uml.property name="tablesLabel"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private JLabel tablesLabel;

	/**
	 * 
	 * @uml.property name="tablesList"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private JList tablesList;

	/**
	 * 
	 * @uml.property name="newTableButton"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private JButton newTableButton;

	/**
	 * 
	 * @uml.property name="editTableButton"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private JButton editTableButton;

	/**
	 * 
	 * @uml.property name="deleteTableButton"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private JButton deleteTableButton;

	/**
	 * 
	 * @uml.property name="saveTableButton"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private JButton saveTableButton;

	/**
	 * 
	 * @uml.property name="loadTableButton"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private JButton loadTableButton;

	/**
	 * 
	 * @uml.property name="tablesScrollPane"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private JScrollPane tablesScrollPane;

	/**
	 * 
	 * @uml.property name="observer" multiplicity="(0 1)"
	 */
	private ActionListener observer;

	private ActionListener tableObserver;
	private String rdbms;
	
	public BDEditorUI(String rdbms)
	{
		super ("Database editor");
		this.rdbms = rdbms;
		
		/* TODO: I GET SOME UPDATE ERRORS WITH THE WINDOWS LOOK AND FEEL; CHECK THAT */
		//String lf = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
		/*
		String lf = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
		try 
		{
			UIManager.setLookAndFeel(lf);
			SwingUtilities.updateComponentTreeUI(this);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		*/
		
		tablesList = new JList();
		// wrap the tables list into a scrollable pane
		tablesScrollPane = new JScrollPane(tablesList);
		JPanel panel = new JPanel();
		
		nameLabel = new JLabel("Database name:");
		tablesLabel = new JLabel("Tables:");
		nameText = new JTextField();
		
		newButton = new JButton("New");
		editButton = new JButton("Edit");
		deleteButton = new JButton("Delete");
		browseButton = new JButton("Browse");
		newTableButton = new JButton("New");
		editTableButton = new JButton("Edit");
		deleteTableButton = new JButton("Delete");
		saveTableButton = new JButton("Save to file");
		loadTableButton = new JButton("Load from file");
		
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
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		// the 5th position, since the previous 2 items take up 4 positions
		c.gridx = 4; 
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(browseButton, c);
		if (rdbms.compareToIgnoreCase("mysql") == 0)
			browseButton.setEnabled(false);
		panel.add(browseButton);
		
		c.fill = GridBagConstraints.NONE;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		// the second position, since it should come under the
		// text field "nameText", not under the label "nameLabel"
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(newButton, c);
		panel.add(newButton);
		
		c.fill = GridBagConstraints.NONE;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 2;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(editButton, c);
		panel.add(editButton);

		c.fill = GridBagConstraints.NONE;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 3;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(deleteButton, c);
		panel.add(deleteButton);
		
		c.fill = GridBagConstraints.NONE;
		ins = new Insets (10, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(tablesLabel, c);
		panel.add(tablesLabel);
		
		c.fill = GridBagConstraints.NONE;
		ins = new Insets (10, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 3; // expand on 3 columns
		c.gridheight = 5; // and 3 rows
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(tablesScrollPane, c);
		panel.add(tablesScrollPane);
		
		c.fill = GridBagConstraints.NONE;
		ins = new Insets (10, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 4;
		c.gridy = 2;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		gridbag.setConstraints(newTableButton, c);
		panel.add(newTableButton);

		c.fill = GridBagConstraints.NONE;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 4;
		c.gridy = 3; // put this button under the previous one
		c.gridwidth = 1;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		gridbag.setConstraints(editTableButton, c);
		panel.add(editTableButton);

		c.fill = GridBagConstraints.NONE;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 4;
		c.gridy = 4; // put this button under the previous one
		c.gridwidth = 1;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		gridbag.setConstraints(deleteTableButton, c);
		panel.add(deleteTableButton);

		c.fill = GridBagConstraints.NONE;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 4;
		c.gridy = 5; // put this button under the previous one
		c.gridwidth = 1;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		gridbag.setConstraints(saveTableButton, c);
		//if (tablesList.isSelectionEmpty())
		//	saveTableButton.setEnabled(false);
		panel.add(saveTableButton);
		
		c.fill = GridBagConstraints.NONE;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 4;
		c.gridy = 6; // put this button under the previous one
		c.gridwidth = 1;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		gridbag.setConstraints(loadTableButton, c);
		//if (tablesList.isSelectionEmpty())
		//	loadTableButton.setEnabled(false);
		panel.add(loadTableButton);

		getContentPane().add(panel, BorderLayout.CENTER);
		pack();
		setVisible(true);
	}

	/**
	 * 
	 * @uml.property name="observer"
	 */
	public void setObserver(
		ActionListener observer,
		ActionListener tableObserver) {
		this.observer = observer;
		this.tableObserver = tableObserver;
		editButton.addActionListener(observer);
		browseButton.addActionListener(observer);
		editTableButton.addActionListener(tableObserver);
		newTableButton.addActionListener(tableObserver);
		deleteTableButton.addActionListener(tableObserver);
		saveTableButton.addActionListener(tableObserver);
		loadTableButton.addActionListener(tableObserver);
	}


	public String getRDBMS()
	{
		return rdbms;	
	}
	
	public String getNameText()
	{
		return nameText.getText();
	}

	public String getTableName()
	{
		return tablesList.getSelectedValue().toString();
	}

	public JButton getEditDBButton()
	{
		return editButton; 
	}

	/**
	 * 
	 * @uml.property name="editTableButton"
	 */
	public JButton getEditTableButton() {
		return editTableButton;
	}


	public JButton getBrowseDBButton()
	{
		return browseButton; 
	}

	/**
	 * 
	 * @uml.property name="newTableButton"
	 */
	public JButton getNewTableButton() {
		return newTableButton;
	}

	/**
	 * 
	 * @uml.property name="deleteTableButton"
	 */
	public JButton getDeleteTableButton() {
		return deleteTableButton;
	}

	/**
	 * 
	 * @uml.property name="saveTableButton"
	 */
	public JButton getSaveTableButton() {
		return saveTableButton;
	}

	/**
	 * 
	 * @uml.property name="loadTableButton"
	 */
	public JButton getLoadTableButton() {
		return loadTableButton;
	}

	public void removeTableFromList(String tableName)
	{
		// HOW TO REMOVE AN ITEM FROM A JList ?
		//tablesList.remove(tablesList.getSelectedIndex());
		//tablesList.clearSelection(); 
	}
		
	public void appendToTableList(Vector tables)
	{
		//tablesList = new JList(tables);
		tablesList.setListData(tables);
		tablesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tablesList.setSelectedIndex(0);
		//tablesScrollPane = new JScrollPane(tablesList);
		//tablesScrollPane.repaint();
		//pack();
		//show();
		//tablesList.addListSelectionListener(observer);
	}

	public void setDBName(String name)
	{
		nameText.setText(name); 
	}
	 		
	public static void main(String[] args) {
		BDEditorUI editor = new BDEditorUI("firebird");
		ActionListener observer = new BDEditor(editor);
		ActionListener tableObserver = new TableEditor(editor, (BDEditor)observer); 
		editor.setObserver(observer, tableObserver); 
	}
}
