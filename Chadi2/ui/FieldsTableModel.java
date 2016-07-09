/*
 * Created on Feb 5, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ui;
import javax.swing.table.*;
import java.util.Vector;
/**
 * @author Tup & Titi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

public class FieldsTableModel extends AbstractTableModel
{
	private String[] columnNames;
	private Object[][] rowData;
	private int crtRows;
	private int crtCols;
	private static FieldsTableModel ftm = null; // singleton (not used)
	
	public static FieldsTableModel getModel(int rows, int cols)
	{
		if (ftm != null)
			return ftm;
		
		ftm = new FieldsTableModel(rows, cols);
		return ftm;
	}
	
	public FieldsTableModel(int rows, int cols)
	{
		columnNames = new String[cols];
		rowData = new Object[rows][cols];
		crtRows = rows;
		crtCols = cols;
	}
	
	public int getRowCount()
	{
		return rowData.length;
	}

	public int getColumnCount()
	{
		return columnNames.length;
	}
	
	public String getColumnName(int col)
	{
		return columnNames[col].toString();
	}
	
	public void setColumnName(String name, int col)
	{
		columnNames[col] = name;
	}
		
	public Object getValueAt(int row, int col)
	{
		return rowData[row][col];
	}
	
	public boolean isCellEditable(int row, int col)
	{
		return true;
	}
	
	public void setValueAt(Object value, int row, int col)
	{
		rowData[row][col] = value;
		fireTableCellUpdated(row, col);
	}
	
	public void expandRows()
	{
		Object[][] tempData = new Object[crtRows][crtCols];
		for (int i = 0; i < crtRows; i++)
		 for (int j = 0; j < crtCols; j++)
		 	tempData[i][j] = rowData[i][j];
		
		crtRows++;
		rowData = new Object[crtRows][crtCols];
		
		for (int i = 0; i < crtRows - 1; i++)
		 for (int j = 0; j < crtCols; j++)
			rowData[i][j] = tempData[i][j];
		
	}
	
	public void updateData(Vector data)
	{
		rowData = null;
		crtRows = data.size();
		rowData = new Object[crtRows][crtCols];
		for (int i = 0; i < data.size(); i++)
		{
			for (int j = 0; j < crtCols; j++)
			{
				setValueAt(((Vector)data.elementAt(i)).elementAt(j), i, j);
			}
		} 
	}
	
	public void initialize (int rows, int cols)
	{
		crtRows = rows;
		crtCols = cols;
		columnNames = new String[crtCols];
		rowData = new Object[crtRows][crtCols];
	}
}
