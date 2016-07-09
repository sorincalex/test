/*
 * Created on Feb 1, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package actions;

import ui.*;
import java.io.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.event.*;

/**
 * @author Tup & Titi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TableEditor implements ActionListener, TableModelListener {
	private StringBuffer dbname;
	private String tablename;
	private String[] columnNames;
	private String[] columnTypes;

	/**
	 * 
	 * @uml.property name="dbui"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private BDEditorUI dbui;

	/**
	 * 
	 * @uml.property name="tableui"
	 * @uml.associationEnd multiplicity="(0 1)"
	 */
	private TableEditorUI tableui;

	/**
	 * 
	 * @uml.property name="data"
	 * @uml.associationEnd multiplicity="(0 1)"
	 */
	private Vector data;

	/**
	 * 
	 * @uml.property name="newtableui"
	 * @uml.associationEnd multiplicity="(0 1)"
	 */
	private NewTableUI newtableui;

	/**
	 *  
	 * @uml.property name="newrecordui"
	 * @uml.associationEnd inverse=":ui.NewRecordUI" multiplicity="(0 1)"
	 * 
	 */
	private NewRecordUI newrecordui;

	private NewFieldUI newfieldui;
	
	/**
	 * 
	 * @uml.property name="bdeditor"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private BDEditor bdeditor;

	
	public TableEditor(BDEditorUI ui, BDEditor bdeditor)
	{		
		dbui = ui;
		this.bdeditor = bdeditor;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if ((dbui != null) && (e.getSource() == dbui.getEditTableButton()))
		{
			selectRecords();
			showTable();
		} 
		else if ((dbui != null) && (e.getSource() == dbui.getNewTableButton()))
		{
			newTable();
		}
		else if ((dbui != null) && (e.getSource() == dbui.getDeleteTableButton()))
		{
			destroyTable();
			bdeditor.editDB(); // in order to display the new list of tables
		}
		else if ((dbui != null) && (e.getSource() == dbui.getSaveTableButton()))
		{
			saveToFile();
		}
		else if ((dbui != null) && (e.getSource() == dbui.getLoadTableButton()))
		{
			loadFromFile();
		}
		else if ((newtableui != null) && (e.getSource() == newtableui.getNewFieldButton()))
		{
			newField();
		}
		else if ((newtableui != null) && (e.getSource() == newtableui.getReadyButton()))
		{
			createTable();
			newtableui.dispose();
			bdeditor.editDB(); // in order to display the new list of tables
		}
		else if ((newfieldui != null) && (e.getSource() == newfieldui.getOkButton()))
		{
			newtableui.setNextValue(newfieldui.getNameField());
			newtableui.setNextValue(newfieldui.getTypeField());
			newfieldui.dispose();
		}
		else if ((tableui != null) && (e.getSource() == tableui.getDeleteRecordsButton()))
		{
			int confirmation = JOptionPane.showConfirmDialog(tableui, "Really delete this record ?");
			
			if (confirmation == JOptionPane.YES_OPTION)
			{
				deleteRecord(tableui.getRecordSelected());
				selectRecords();
				// now the updated table has to be displayed again
				tableui.updateContents(data);
				tableui.refresh();
			}
		}
		else if ((tableui != null) && (e.getSource() == tableui.getNewRecordButton()))
		{
			newrecordui = new NewRecordUI(columnNames, columnTypes);
			newrecordui.setObserver(this);
		}
		else if ((newrecordui != null) && (e.getSource() == newrecordui.getOkButton()))
		{			
			Object[] recordAdded = newrecordui.getFields();
			try
			{
				addNewRecord(recordAdded, true);
				//selectRecords();
				// now the updated table has to be displayed again
				//tableui.updateContents(data);
				tableui.addRow(recordAdded);
				tableui.refresh();
			}
			catch (Exception addException)
			{
				System.out.println(addException.toString());
			}			
		}
	}
	
	private void selectRecords()
	{
		tablename = new String(dbui.getTableName());
		dbname = new StringBuffer(dbui.getNameText());
		System.out.println("BDEditor: dbname = " + tablename);
		if (dbui.getRDBMS().compareToIgnoreCase("mysql") == 0) // MySQL
			dbname.insert(0, "jdbc:mysql://localhost/Romania?user=root&password=");
		else // Firebird
			dbname.insert(0, "jdbc:firebirdsql:localhost/3050:");
		
		String sql = "SELECT * FROM " + tablename;
		boolean columnsVectorReady = false;
		
		try
		{
			Connection conn;
			if (dbui.getRDBMS().compareToIgnoreCase("mysql") == 0)
			{	// MySQL RDBMS
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection(dbname.toString());
			}
			else
			{	// Firebird RDBMS
				Class.forName("org.firebirdsql.jdbc.FBDriver");
				conn = DriverManager.getConnection(dbname.toString(), "sysdba", "masterkey");
			} 
			
			Statement stmt = null;
			ResultSet rst = null;

			try
			{
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql);
                        
				// Show the result set through the standard output.
				int columnCount = rst.getMetaData().getColumnCount();
				System.out.println ("	columns = " + columnCount);
				int recordIndex = 0;         
			
				// getting data from the table
				data = new Vector();
				columnNames = new String[columnCount];
				columnTypes = new String[columnCount];
				for (int i = 1; i <= columnCount; i++) 
				{
					System.out.print(rst.getMetaData().getColumnName(i));
					System.out.print(": ");
					System.out.println(rst.getMetaData().getColumnTypeName(i) + " (" + rst.getMetaData().getColumnType(i) + ")");
					columnTypes[i-1] = rst.getMetaData().getColumnTypeName(i);
					columnNames[i-1] = rst.getMetaData().getColumnName(i);
				}
				while(rst.next()) 
				{
					Vector row = new Vector();
					recordIndex++;
					System.out.println("Record: " + recordIndex);
					for (int i = 1; i <= columnCount; i++) 
					{
						System.out.print(rst.getMetaData().getColumnName(i));
						System.out.print(": ");
						System.out.print(rst.getString(i) + " ");
						row.addElement(rst.getString(i));
					}
					data.addElement(row);			
					System.out.println();
				}
			}
			finally 
			{
				// close the database resources immediately, rather than waiting
				// for the finalizer to kick in later
				System.out.println (" .... BDEditor: finalizing ....");
				if (rst != null) rst.close();
				if (stmt != null) stmt.close();
				conn.close();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	private void newTable()
	{
		newtableui = new NewTableUI(this, dbui.getNameText());
	}

	private void newField()
	{
		newfieldui = new NewFieldUI(this);
	}

	private void addNewRecord(Object[] values, boolean check) throws Exception
	{
		try
		{
			Connection conn;
			if (dbui.getRDBMS().compareToIgnoreCase("mysql") == 0)
			{
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection(dbname.toString());
			}
			else
			{
				Class.forName("org.firebirdsql.jdbc.FBDriver");
				conn = DriverManager.getConnection(dbname.toString(), "sysdba", "masterkey");
			}
			
			// if the record is already in the table - finish the method
			if (recordPresent(values, check)) return;
						
			Statement stmt = null;
			StringBuffer sql = new StringBuffer("INSERT INTO " + tablename + " VALUES (" );
			for (int i = 0; i < values.length - 1; i++)
			{
				sql.append("'" + values[i].toString() + "', ");
			}
			sql.append("'" + values[values.length - 1].toString() + "')");

			System.out.println ("sql = " + sql.toString());
			
			try
			{
				stmt = conn.createStatement();
				stmt.executeUpdate(sql.toString());
				if (newrecordui != null)
					newrecordui.dispose();            
			}
			catch (Exception insertException)
			{
				StringBuffer str = new StringBuffer("An error has occured. The types of the columns are:");
				for (int i = 0; i < columnTypes.length - 1; i++)
					str.append(columnTypes[i] + ", ");
				str.append(columnTypes[columnTypes.length - 1]);
				JOptionPane.showMessageDialog(tableui, str.toString());
				throw insertException;	
			}
			finally 
			{
				// close the database resources immediately, rather than waiting
				// for the finalizer to kick in later
				System.out.println (" .... TableEditor: finalizing ....");
				if (stmt != null) stmt.close();
				conn.close();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			throw ex; 
			// throw this exception further, to be caught in "actionPerformed";
			// as a result, tableui.updateContents(data) will not be executed 
		}
	}

	private void deleteRecord(Object[] values)
	{
		try
		{
			Connection conn;
			if (dbui.getRDBMS().compareToIgnoreCase("mysql") == 0)
			{
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection(dbname.toString());
			}
			else
			{
				Class.forName("org.firebirdsql.jdbc.FBDriver");
				conn = DriverManager.getConnection(dbname.toString(), "sysdba", "masterkey");
			}
			
			Statement stmt = null;
			
			StringBuffer sql = new StringBuffer("DELETE FROM " + tablename + " WHERE " );
			for (int i = 0; i < values.length - 2; i++)
			{
				sql.append(columnNames[i] + "='" + values[i].toString() + "' AND ");
			}
			sql.append(columnNames[values.length - 2] + "='" + values[values.length - 2].toString() + "'");
			System.out.println ("sql = " + sql.toString());
			
			try
			{
				stmt = conn.createStatement();
				stmt.executeUpdate(sql.toString());
			}
			finally 
			{
				// close the database resources immediately, rather than waiting
				// for the finalizer to kick in later
				System.out.println (" .... TableEditor: finalizing ....");
				if (stmt != null) stmt.close();
				conn.close();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private boolean recordPresent(Object[] record, boolean check)
	{
		Connection conn;
		System.out.println (dbname);
		try
		{
			if (dbui.getRDBMS().compareToIgnoreCase("mysql") == 0)
			{
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection(dbname.toString());
			}
			else
			{
				Class.forName("org.firebirdsql.jdbc.FBDriver");
				conn = DriverManager.getConnection(dbname.toString(), "sysdba", "masterkey");
			}
			// check if the record is already in the table; in that case,
			// report error and return true
			StringBuffer sqltest = new StringBuffer("SELECT * FROM " + tablename + " WHERE ");
			for (int i = 0; i < record.length - 1; i++)
			{
				sqltest.append(columnNames[i] + "='" + record[i].toString() + "' AND ");
			}
			sqltest.append(columnNames[record.length - 1] + "='" + record[record.length - 1].toString() + "'");
			System.out.println ("sqltest = " + sqltest.toString());
			Statement stmttest = null;
			ResultSet rsttest = null;
			try
			{
				stmttest = conn.createStatement();
				rsttest = stmttest.executeQuery(sqltest.toString());
				if (rsttest.next()) 
				{  // the record already present
					if (check)
					{
						JOptionPane.showMessageDialog(tableui, "Record already present", "Add record error", JOptionPane.ERROR_MESSAGE);
						tableui.updateContents(data);
						tableui.refresh();
					}
					return true;
				} 
			}
			finally 
			{
				// close the database resources immediately, rather than waiting
				// for the finalizer to kick in later
				System.out.println (" .... TableEditor: finalizing ....");
				if (stmttest != null) stmttest.close();
				conn.close();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	private void updateRecord(Object[] record, String[] columnNames, int changedColumn)
	{
		try
		{
			Connection conn;
			if (dbui.getRDBMS().compareToIgnoreCase("mysql") == 0)
			{
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection(dbname.toString());
			}
			else
			{
				Class.forName("org.firebirdsql.jdbc.FBDriver");
				conn = DriverManager.getConnection(dbname.toString(), "sysdba", "masterkey");
			}

			// if the record already in the table - finish the method
			if (recordPresent(record, true)) return;
			
			StringBuffer sql = new StringBuffer("UPDATE " + tablename + " SET " + columnNames[changedColumn] + "='" + record[changedColumn] + "'" + " WHERE ");
			for (int i = 0; i < record.length - 2; i++)
			{
				if (i != changedColumn)
					sql.append(columnNames[i] + "='" + record[i].toString() + "' AND ");
			}
			if (changedColumn == record.length - 1)
			{
				sql.append(columnNames[record.length - 2] + "='" + record[record.length - 2].toString() + "'");
			}
			else if (changedColumn == record.length - 2)
			{
				sql.append(columnNames[record.length - 1] + "='" + record[record.length - 1].toString() + "'");
			}
			else
			{
				sql.append(columnNames[record.length - 2] + "='" + record[record.length - 2].toString() + "' AND ");
				sql.append(columnNames[record.length - 1] + "='" + record[record.length - 1].toString() + "'");
			}
				
			System.out.println ("sql = " + sql.toString());
			
			Statement stmt = null;
			try
			{
				stmt = conn.createStatement();
				stmt.executeUpdate(sql.toString());
			}
			finally 
			{
				// close the database resources immediately, rather than waiting
				// for the finalizer to kick in later
				System.out.println (" .... TableEditor: finalizing ....");
				if (stmt != null) stmt.close();
				conn.close();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	private void createTable()
	{
		dbname = new StringBuffer(newtableui.getDBName());
		System.out.println("BDEditor: dbname = " + dbname);
		if (dbui.getRDBMS().compareToIgnoreCase("mysql") == 0)
			dbname.insert(0, "jdbc:mysql://localhost/Romania?user=root&password=");
		else
			dbname.insert(0, "jdbc:firebirdsql:localhost/3050:");

		try
		{
			Connection conn;
			if (dbui.getRDBMS().compareToIgnoreCase("mysql") == 0)
			{
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection(dbname.toString());
			}
			else
			{
				Class.forName("org.firebirdsql.jdbc.FBDriver");
				conn = DriverManager.getConnection(dbname.toString(), "sysdba", "masterkey");
			}

			Statement stmt = null;
			StringBuffer sql = new StringBuffer("CREATE TABLE " + newtableui.getTableName() + " (" );
			Vector fieldTypes = newtableui.getFieldTypes();
			Vector fieldNames = newtableui.getFieldNames();
			for (int i = 0; i < fieldTypes.size() - 1; i++)
			{
				sql.append(fieldNames.elementAt(i).toString() + " " + fieldTypes.elementAt(i).toString() + ", ");
			}
			sql.append(fieldNames.elementAt(fieldTypes.size() - 1).toString() + " " + fieldTypes.elementAt(fieldTypes.size() - 1).toString() + ")");
			System.out.println ("sql = " + sql.toString());
			
			try
			{
				stmt = conn.createStatement();
				stmt.executeUpdate(sql.toString());
			}
			finally 
			{
				// close the database resources immediately, rather than waiting
				// for the finalizer to kick in later
				System.out.println (" .... TableEditor: finalizing ....");
				if (stmt != null) stmt.close();
				conn.close();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

	}
	
	private void destroyTable()
	{
		tablename = new String(dbui.getTableName());
		dbname = new StringBuffer(dbui.getNameText());
		
		try
		{
			Connection conn;
			if (dbui.getRDBMS().compareToIgnoreCase("mysql") == 0)
			{
				dbname.insert(0, "jdbc:mysql://localhost/Romania?user=root&password=");
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection(dbname.toString());
			}
			else
			{
				dbname.insert(0, "jdbc:firebirdsql:localhost/3050:");
				Class.forName("org.firebirdsql.jdbc.FBDriver");
				conn = DriverManager.getConnection(dbname.toString(), "sysdba", "masterkey");
			}

			Statement stmt = null;			
			StringBuffer sql = new StringBuffer("DROP TABLE "+ tablename.trim().toUpperCase());
			System.out.println ("sql = " + sql.toString());
			int confirmation = JOptionPane.showConfirmDialog(tableui, "Really delete this table ?");

			if (confirmation == JOptionPane.YES_OPTION)
			{
				try
				{
					stmt = conn.createStatement();
					stmt.executeUpdate(sql.toString());
				}
				finally 
				{
					// close the database resources immediately, rather than waiting
					// for the finalizer to kick in later
					System.out.println (" .... TableEditor: finalizing ....");
					if (stmt != null) stmt.close();
					conn.close();
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

	}
	
	private void showTable()
	{
		tableui = new TableEditorUI(columnNames, columnTypes, data);
		tableui.setObserver(this);		
	}

	// called when an item of a row in the table is changed by the user;
	// it will perform an UPDATE to the table in the DB	
	public void tableChanged(TableModelEvent e)
	{
		System.out.println ("tableChanged: " + e.getType() + "  " + e.getFirstRow());
		int row = e.getFirstRow();
		int column = e.getColumn();
		Object[] recordAdded = tableui.getUpdatedLine(row, column);
		System.out.println ("TableEditor: row = " + row + ", column = " + column + ", recordAdded = " + recordAdded);
		for (int i = 0; i < recordAdded.length; i++)
			System.out.print ("    " + (String)recordAdded[i]);
		System.out.println();
		
		updateRecord(recordAdded, columnNames, column);
		tableui.clearSelection();
	}
	
	private void save(String fileName)
	{
		PrintWriter out = null;
		selectRecords();
		try
		{
			out = new PrintWriter(new FileWriter(fileName));
			for (int i = 0; i < data.size(); i++)
			{
				Vector row = (Vector)data.elementAt(i);
				for (int j = 0; j < row.size(); j++)
				{
					String elem = row.elementAt(j).toString();
					out.print(elem);
					if (j < row.size() - 1) out.print(";");
				}
				out.println();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (out != null) out.close();
		}
	} 
		
	private void saveToFile()
	{
		if ((dbui.getTableName() != null) && (dbui.getTableName() != ""))
		{
			JFileChooser fc = new JFileChooser();
			int ret = fc.showSaveDialog(dbui);		
			System.out.println("ret = " + ret);
			if (ret == JFileChooser.APPROVE_OPTION)
			{
				File f = fc.getSelectedFile();
				System.out.println("Path = " + f.getAbsolutePath() + ", name = " + f.getName());
				save(f.getAbsolutePath());					
			}
		}		
	}

	private void load(String fileName)
	{
		BufferedReader in = null;
		selectRecords();
		try
		{
			in = new BufferedReader(new FileReader(fileName));
			String line;
			while ((line = in.readLine()) != null)
			{
				int i = 0;
				StringTokenizer st = new StringTokenizer(line, ";");
				Object[] record = new Object[columnNames.length];
				while (st.hasMoreTokens())
				{
					String token = st.nextToken();
					if ((token == null) || (token.length() < 1)) 
						record[i] = new String("");
					else 
						record[i] = new String(token);
					System.out.println ("record[" + i + "] = " + record[i]);
					i++;
				}
				if (i < columnNames.length)
					for (int j = i; j < columnNames.length; j++)
						record[j] = new String("");
				try
				{
					addNewRecord(record, false);
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (in != null) 
			try { in.close(); }
			catch (Exception ee) { ee.printStackTrace(); }
		}
	}
	
	private void loadFromFile()
	{
		if ((dbui.getTableName() != null) && (dbui.getTableName() != ""))
		{
			JFileChooser fc = new JFileChooser();
			int ret = fc.showOpenDialog(dbui);		
			System.out.println("ret = " + ret);
			if (ret == JFileChooser.APPROVE_OPTION)
			{
				File f = fc.getSelectedFile();
				System.out.println("Path = " + f.getAbsolutePath() + ", name = " + f.getName());
				load(f.getAbsolutePath());					
			}
		}
	}

	/**
	 *  
	 * @uml.property name="newFieldUI"
	 * @uml.associationEnd inverse="newfieldui:ui.NewFieldUI" multiplicity="(0 1)"
	 * 
	 */
	private NewFieldUI newFieldUI;

	/**
	 *  
	 * @uml.property name="newFieldUI"
	 * 
	 */
	public NewFieldUI getNewFieldUI() {
		return newFieldUI;
	}

	/**
	 *  
	 * @uml.property name="newFieldUI"
	 * 
	 */
	public void setNewFieldUI(NewFieldUI newFieldUI) {
		this.newFieldUI = newFieldUI;
	}

}
