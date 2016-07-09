/*
 * Created on Feb 15, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ui;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
/**
 * @author Tup & Titi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class NewRecordUI extends JFrame
{

	/**
	 * 
	 * @uml.property name="labels"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 */
	private JLabel[] labels;

	/**
	 * 
	 * @uml.property name="fields"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 */
	private JTextField[] fields;

	/**
	 * 
	 * @uml.property name="okButton"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private JButton okButton;

	/**
	 * 
	 * @uml.property name="observer" multiplicity="(0 1)"
	 */
	private ActionListener observer;

	
	public NewRecordUI(String[] columnNames, String[] columnTypes)
	{
		super ("New record");
		//JScrollPane panel = new JScrollPane();
		getContentPane().setLayout(new GridLayout(0, 2, 2, 5)); // as many rows as necessary
		//panel.setLayout(new ScrollPaneLayout());
		labels = new JLabel[columnNames.length];
		fields = new JTextField[columnNames.length];
		for (int i = 0; i < columnNames.length; i++)
		{
			labels[i] = new JLabel(columnNames[i] + " (" + columnTypes[i] + "):");
			getContentPane().add(labels[i]);
			fields[i] = new JTextField(25);
			getContentPane().add(fields[i]);
		}
		okButton = new JButton("Ok");
		getContentPane().add(okButton);
		//getContentPane().add(panel);	
		pack();
		setVisible(true);
	}

	/**
	 * 
	 * @uml.property name="observer"
	 */
	public void setObserver(ActionListener obs) {
		observer = obs;
		okButton.addActionListener(observer);
	}

	
	public String[] getFields()
	{
		String[] fieldsString = new String[fields.length];
		
		for (int i = 0; i < fields.length; i++)
			fieldsString[i] = fields[i].getText();
		
		return fieldsString;
	}

	/**
	 * 
	 * @uml.property name="okButton"
	 */
	public JButton getOkButton() {
		return okButton;
	}

}