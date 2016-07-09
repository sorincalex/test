/*
 * Created on Jan 29, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package actions;

import ui.*;
import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.io.*;

/**
 * @author Tup & Titi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class BDEditor implements ActionListener {
	private StringBuffer dbname;

	/**
	 * 
	 * @uml.property name="dbui"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private BDEditorUI dbui;

	
	public BDEditor(BDEditorUI ui)
	{		
		dbui = ui;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == dbui.getEditDBButton())
		{
			editDB();
		}
		else if (e.getSource() == dbui.getBrowseDBButton())
		{
			openDB();
		}
	}
	
	public void editDB()
	{
		dbname = new StringBuffer(dbui.getNameText());
		System.out.println("BDEditor: dbname = " + dbname.toString());
		String sql;
		
		if (dbui.getRDBMS().compareToIgnoreCase("mysql") == 0)
		{	// MySQL RDBMS
			dbname.insert(0, "jdbc:mysql://localhost/Romania?user=root&password=");
			sql = "SHOW TABLES;";
		} 
		else 
		{	// Firebird RDBMS
			dbname.insert(0, "jdbc:firebirdsql:localhost/3050:");
			sql = "SELECT RDB$RELATION_NAME FROM RDB$RELATIONS WHERE RDB$SYSTEM_FLAG=0 AND RDB$VIEW_BLR IS NULL";
		}
		
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
			
			System.out.println ("After DriverManager.getConnection(), conn = " + conn);
			Statement stmt = null;
			ResultSet rst = null;

			try
			{
				stmt = conn.createStatement();
				//System.out.println ("After conn.createStatement(), stmt = " + stmt);
				rst = stmt.executeQuery(sql);
				//System.out.println ("After stmt.executeQuery(sql), rst = " + rst + " sql = " + sql);
                        
				// Show the result set through the standard output.
				int columnCount = rst.getMetaData().getColumnCount();
				System.out.println ("	columns = " + columnCount);
				int recordIndex = 0;
				Vector tables = new Vector();            
			
				// getting data from the table
				while(rst.next()) 
				{
					recordIndex++;
					System.out.println("Record: " + recordIndex);
					for (int i=1;i<=columnCount;i++) 
					{
						System.out.print(rst.getMetaData().getColumnName(i));
						System.out.print(": ");
						System.out.print(rst.getString(i) + " ");
						tables.addElement(rst.getString(i));		
					}				
					System.out.println ();
				}
			
				dbui.appendToTableList(tables);            
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
	
	private void openDB()
	{
		JFileChooser fc = new JFileChooser();
		int ret = fc.showOpenDialog(dbui);		
		System.out.println("ret = " + ret);
		if (ret == JFileChooser.APPROVE_OPTION)
		{
			File f = fc.getSelectedFile();
			System.out.println("Path = " + f.getAbsolutePath() + ", name = " + f.getName());
			dbui.setDBName(f.getAbsolutePath());					
		}
		
		File ff = new File(this.getClass().getName());
		try
		{
			System.out.println (ff.getAbsolutePath() + " " + ff.getCanonicalPath() + " " + ff.getPath() + " " + ff.getParent() + " " + ff.toURL() + " " + ff.toURI());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
