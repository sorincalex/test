/*
 * Created on Feb 1, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.*;
import javax.swing.event.*;
import java.util.Vector;

/**
 * @author Tup & Titi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */


class MyRowSelector implements ListSelectionListener
{
	private int selectedRow = -1;

	/**
	 * 
	 * @uml.property name="recordSelected" multiplicity="(0 1)"
	 */
	private Object[] recordSelected;

	/**
	 * 
	 * @uml.property name="sorter"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private TableSorter sorter;

	 	 
	public MyRowSelector(int nrColumns, TableSorter sorter)
	{
		recordSelected = new Object[nrColumns];
		for (int i = 0; i < nrColumns; i++)
		{
			recordSelected[i] = new String("null");
		}
		this.sorter = sorter;
	}
	
	public void valueChanged(ListSelectionEvent e)
	{
		ListSelectionModel lsm = (ListSelectionModel)e.getSource();
		if (lsm.isSelectionEmpty())
		{
			System.out.println(this + ": No rows selected");
		}
		else
		{
			selectedRow = lsm.getMinSelectionIndex();			 
			if (selectedRow > -1)
			{
				for (int i = 0; i < sorter.getColumnCount(); i++)
				{
					recordSelected[i] = sorter.getValueAt(selectedRow, i);
				}
			}
			System.out.println(this + ": ROW SELECTED: " + selectedRow + " FROM " + sorter.getRowCount() + " (" + recordSelected + ")" );
			for (int i = 0; i < recordSelected.length; i++)
				System.out.print ("    " + (String)recordSelected[i]);
			System.out.println();
		}
	}
	
	public int getRowSelected()
	{
		return selectedRow;
	}

	/**
	 * 
	 * @uml.property name="recordSelected"
	 */
	public Object[] getRecordSelected() {
		return recordSelected;
	}

}


public class TableEditorUI extends JFrame
{

	/**
	 * 
	 * @uml.property name="table"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private JTable table;

	/**
	 * 
	 * @uml.property name="panel"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private JScrollPane panel;

	/**
	 * 
	 * @uml.property name="panelButtons"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private JPanel panelButtons;

	/**
	 * 
	 * @uml.property name="updateRecords"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private JButton updateRecords;

	/**
	 * 
	 * @uml.property name="deleteRecords"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private JButton deleteRecords;

	/**
	 * 
	 * @uml.property name="newRecord"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private JButton newRecord;

	/**
	 * 
	 * @uml.property name="observer" multiplicity="(0 1)"
	 */
	private ActionListener observer;

	/**
	 * 
	 * @uml.property name="atm"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private AbstractTableModel atm;

	/**
	 * 
	 * @uml.property name="sorter"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private TableSorter sorter;

	private Object[] recordSelected;
	private int selectedRow = -1;

	/**
	 * 
	 * @uml.property name="sellistener"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private ListSelectionListener sellistener;

	
	public TableEditorUI(String[] columnNames, String[] columnTypes, Vector data)
	{
		super ("Table editor");
		atm = new FieldsTableModel(data.size(), columnNames.length);
		for (int i = 0; i < columnNames.length; i++)
		{
			((FieldsTableModel)atm).setColumnName(columnNames[i], i);
		}
		((FieldsTableModel)atm).updateData(data);		    

		recordSelected = new Object[atm.getColumnCount()];
		for (int i = 0; i < atm.getColumnCount(); i++)
		{
			recordSelected[i] = new String("null");
		}
		sorter = new TableSorter(atm);
		table = new JTable(sorter);
		sorter.setTableHeader(table.getTableHeader());
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ListSelectionModel rowSM = table.getSelectionModel();
		sellistener = new MyRowSelector(atm.getColumnCount(), sorter);
		rowSM.addListSelectionListener(sellistener);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		panel = new JScrollPane(table);
		table.setPreferredScrollableViewportSize(new Dimension(800, 650));
		
		panelButtons = new JPanel();
		panelButtons.setLayout(new GridLayout(3, 1));
		updateRecords = new JButton("Update");
		deleteRecords = new JButton("Delete");
		newRecord = new JButton("New");

		panelButtons.add(updateRecords);
		panelButtons.add(deleteRecords);
		panelButtons.add(newRecord);
		getContentPane().add(panel, BorderLayout.CENTER);
		getContentPane().add(panelButtons, BorderLayout.EAST);

		pack();
		setVisible(true);		
	}

	/**
	 * 
	 * @uml.property name="observer"
	 */
	public void setObserver(ActionListener obs) {
		observer = obs;
		updateRecords.addActionListener(observer);
		deleteRecords.addActionListener(observer);
		newRecord.addActionListener(observer);
		atm.addTableModelListener((TableModelListener) observer);
	}

		
	public JButton getNewRecordButton()
	{
		return newRecord;
	}

	public JButton getUpdateRecordsButton()
	{
		return updateRecords;
	}

	public JButton getDeleteRecordsButton()
	{
		return deleteRecords;
	}
	
	public Object[] getRecordSelected()
	{
		table.clearSelection();
		return ((MyRowSelector)sellistener).getRecordSelected();
	}
	
	public void clearSelection()
	{
		table.clearSelection();
	}
	
	public void refresh()
	{
		pack();
		setVisible(true);
		SwingUtilities.updateComponentTreeUI(this);
	}
	
	public void updateContents(Vector data)
	{
		atm.removeTableModelListener((TableModelListener)observer);
		((FieldsTableModel)atm).updateData(data);
		sorter = new TableSorter(atm);
		table = new JTable(sorter);
		sorter.setTableHeader(table.getTableHeader());
		//table.setModel(atm);
		pack();
		atm.addTableModelListener((TableModelListener)observer);
	}
		
	public void addRow(Object[] values) {
		// disable the notification
		atm.removeTableModelListener((TableModelListener)observer);
		// add the row of records to the table model
		int nrRows = ((FieldsTableModel)atm).getRowCount();
		((FieldsTableModel)atm).expandRows();
		for (int i = 0; i < values.length; i++) {
			((FieldsTableModel)atm).setValueAt(values[i], nrRows, i);
		}
		// re-enable the notification
		atm.addTableModelListener((TableModelListener)observer);
	}

	public Object[] getUpdatedLine(int row, int col)
	{
		Object[] updatedLine = new Object[atm.getColumnCount()];
		Object data = atm.getValueAt(row, col);
		System.out.println("TABLE CHANGED: " + data.toString() + " AT ROW = " + row + " AND COLUMN = " + col);
		if (!data.toString().startsWith("null"))
		{
			updatedLine[col] = data;
			for (int i = 0; i < atm.getColumnCount(); i++)
			{
				if (i != col)
					updatedLine[i] = atm.getValueAt(row, i);
			}
		}
		
		return updatedLine;
		
		//return recordSelected;
	}
}