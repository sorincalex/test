import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.*;

import configuration.Configurer;

import java.util.*;

/*
 * Created on Aug 23, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author Sorin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StatisticsUI extends JFrame implements ActionListener {
	private JLabel consultationsLabel, analysesLabel;
	private DefaultListModel listModel, listModel2;
	private JList consultationList;
	private JTextArea consultationDetails;
	private JList analysisList;
	private JTextArea analysisDetails;
	private JComboBox months;
	private JTextField year;
	private JLabel monthLabel;
	private JButton statistics, print;
	private JComboBox allDoctors;
	private JTextField totalAmount, totalAmount2;
	private ArrayList doctors = new ArrayList();
	private ArrayList consultations = new ArrayList();
	private ArrayList analyses = new ArrayList();
	private ArrayList patientsForConsultations = new ArrayList();
	private ArrayList patientsForAnalyses = new ArrayList();
	
	public StatisticsUI(ActionListener observer) {
		super("Statistici lunare");
		
		/* TODO: I GET SOME UPDATE ERRORS WITH THE WINDOWS LOOK AND FEEL; CHECK THAT */
		String lf = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
		
		//String lf = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
		try {
			UIManager.setLookAndFeel(lf);
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JPanel panel = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		panel.setLayout(gridbag);
		
		// a list containing all the consultations passed as parameter
		listModel = new DefaultListModel();
		consultationList = new JList(listModel);
		
		// a list containing all the analyses passed as parameter
		listModel2 = new DefaultListModel();
		analysisList = new JList(listModel2);
		
		// the details about the selected consultation will be displayed in consultationDetails
		consultationDetails = new JTextArea();
		consultationDetails.setColumns(50);
		consultationDetails.setRows(10);
		consultationDetails.setEditable(false);
		
		consultationList.addListSelectionListener(
			new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					int selectedIndex = consultationList.getSelectedIndex();
					if ((selectedIndex >= 0) && (selectedIndex < consultations.size())) {
						Consultation consultation = (Consultation)(consultations.get(selectedIndex));
						Patient patient = (Patient)(patientsForConsultations.get(selectedIndex));
						StringBuffer toDisplay = 
							new StringBuffer(
								"Text: " + consultation.getText() + "\n " +
								"Data: " + consultation.getDay() + "-" + 
								consultation.getMonth() + "-" + consultation.getYear() + "\n " +
								"Specialitatea: " + consultation.getSpecialty() + "\n " +
								"Doctor: " + ((consultation.getDoctor() != null)?consultation.getDoctor().getName():"necunoscut") + "\n " +
								"Pacient: " + patient.getName() + "\n" +
								"Suma incasata: " + consultation.getAmount() + " RON"
							);
						toDisplay.append("\n ");
						consultationDetails.setText(toDisplay.toString());
					}
				}
			}
		);
		consultationList.setSelectedIndex(0);
		JScrollPane listScrollPane = new JScrollPane(consultationList);
		
		// the details about the selected analysis will be displayed in analysisDetails
		analysisDetails = new JTextArea();
		analysisDetails.setColumns(50);
		analysisDetails.setRows(10);
		analysisDetails.setEditable(false);
		
		analysisList.addListSelectionListener(
			new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					int selectedIndex = analysisList.getSelectedIndex();
					if ((selectedIndex >= 0) && (selectedIndex < analyses.size())) {
						Analysis analysis = (Analysis)(analyses.get(selectedIndex));
						Patient patient = (Patient)(patientsForAnalyses.get(selectedIndex));
						StringBuffer toDisplay = 
							new StringBuffer(
								"Text: " + analysis.getText() + "\n " +
								"Data: " + analysis.getDay() + "-" + 
								analysis.getMonth() + "-" + analysis.getYear() + "\n " +
								"Specialitatea: " + analysis.getSpecialty() + "\n " +
								"Doctor: " + ((analysis.getDoctor() != null)?analysis.getDoctor().getName():"necunoscut") + "\n " +
								"Pacient: " + patient.getName() + "\n" +
								"Suma incasata: " + analysis.getAmount() + " RON"
							);
						toDisplay.append("\n ");
						analysisDetails.setText(toDisplay.toString());
					}
				}
			}
		);
		analysisList.setSelectedIndex(0);
		JScrollPane listScrollPane2 = new JScrollPane(analysisList);
		
		int y = 0;
		
		c.fill = GridBagConstraints.HORIZONTAL; 
		Insets ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = y++;
		c.gridwidth = 3; // make it 3 columns wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		monthLabel = new JLabel("Alegeti luna si anul: ");
		gridbag.setConstraints(monthLabel, c);
		panel.add(monthLabel);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = y;
		c.gridwidth = 2; 
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		String[] monthStrings = {"Ian", "Feb", "Mar", "Apr", "Mai", "Iun", "Iul", "Aug", "Sep", "Oct", "Noi", "Dec"};
		months = new JComboBox(monthStrings);
		Date now = new Date();
		months.setSelectedIndex(now.getMonth());
		gridbag.setConstraints(months, c);
		panel.add(months);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 2;
		c.gridy = y;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		String yearString = new String(new Integer(now.getYear() + 1900).toString());
		year = new JTextField(yearString);
		gridbag.setConstraints(year, c);
		panel.add(year);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 3;
		c.gridy = y++;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		print = new JButton("Tiparire");
		gridbag.setConstraints(print, c);
		panel.add(print);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = y++;
		c.gridwidth = 2; 
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		statistics = new JButton("Bilant lunar");
		gridbag.setConstraints(statistics, c);
		panel.add(statistics);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (10, 5, 0, 0); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = y++;
		c.gridwidth = 2; 
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		allDoctors = new JComboBox(new String[] {"Toti doctorii"});
		try {
			doctors = ((PatientEditor)observer).getDoctors();
			for (int i = 0; i < doctors.size(); i++) {
				allDoctors.addItem(((Doctor)doctors.get(i)).getName());
			}
		} catch(Exception e) {
			
		}
		gridbag.setConstraints(allDoctors , c);
		panel.add(allDoctors);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (10, 5, 0, 0); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = y++;
		c.gridwidth = 2; 
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		consultationsLabel = new JLabel("Consultatii: ");
		gridbag.setConstraints(consultationsLabel, c);
		panel.add(consultationsLabel);

		// fill its display area horizontally
		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = y;
		c.gridwidth = 3; // make it 3 columns wide
		c.gridheight = 4;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(listScrollPane, c);
		panel.add(listScrollPane);
		
		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 3;
		c.gridy = y;
		c.gridwidth = 3; // make it 3 columns wide
		c.gridheight = 4;
		y += c.gridheight;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane consultationScrollPane = new JScrollPane(consultationDetails);
		gridbag.setConstraints(consultationScrollPane, c);
		panel.add(consultationScrollPane);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = y++;
		c.gridwidth = 4; // make it 4 columns wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		totalAmount = new JTextField();
		totalAmount.setEditable(false);
		gridbag.setConstraints(totalAmount, c);
		panel.add(totalAmount);
		
		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (10, 5, 0, 0); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = y++;
		c.gridwidth = 2; 
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		analysesLabel = new JLabel("Analize: ");
		gridbag.setConstraints(analysesLabel, c);
		panel.add(analysesLabel);
		
		// fill its display area horizontally
		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = y;
		c.gridwidth = 3; // make it 3 columns wide
		c.gridheight = 4;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(listScrollPane2, c);
		panel.add(listScrollPane2);
		
		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 3;
		c.gridy = y;
		c.gridwidth = 3; // make it 3 columns wide
		c.gridheight = 4;
		y += c.gridheight;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane analysisScrollPane = new JScrollPane(analysisDetails);
		gridbag.setConstraints(analysisScrollPane, c);
		panel.add(analysisScrollPane);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = y++;
		c.gridwidth = 4; // make it 4 columns wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		totalAmount2 = new JTextField();
		totalAmount2.setEditable(false);
		gridbag.setConstraints(totalAmount2, c);
		panel.add(totalAmount2);

		statistics.addActionListener(observer);
		print.addActionListener(this);
		
		getContentPane().add(panel, BorderLayout.CENTER);
		pack();
		setVisible(true);
	}
	
	public JButton getStatisticsButton() {
		return statistics;
	}
	
	public JButton getPrintButton() {
		return print;
	}
	
	public int getMonth() {
		return (months.getSelectedIndex() + 1);
	}
	
	public int getYear() {
		return new Integer(year.getText()).intValue();
	}
	
	public void setConsultationsAndPatients(ArrayList cons, ArrayList patients) {
		this.consultations.clear(); // = consultations;
		this.patientsForConsultations.clear(); // = patients;
		listModel.clear();
		consultationDetails.setText("");
System.out.println("------------ setConsultationsAndPatients: " + cons.size() + ", " + patients.size());		
		int index = allDoctors.getSelectedIndex();
		
		float total = 0;
		for (int i = 0; i < cons.size(); i++) {
			if (
				(index == 0) ||
				(
			     (index > 0) && 
			     ((Doctor)doctors.get(index - 1)).equals(((Consultation)cons.get(i)).getDoctor())
			    )
			) {
				this.consultations.add(cons.get(i));
				try {
					this.patientsForConsultations.add(patients.get(i));
				} catch (Exception e) {
					
				}
				listModel.addElement(
					((Consultation)cons.get(i)).getDoctor().getName() + ", " +
					((Consultation)cons.get(i)).getDay() + "-" +
					((Consultation)cons.get(i)).getMonth() + "-" +
					((Consultation)cons.get(i)).getYear() + ", " +
					((Consultation)cons.get(i)).getAmount() + " RON"
				);
				total += ((Consultation)cons.get(i)).getAmount();
			}
		}

		consultationList.setSelectedIndex(0);
		totalAmount.setText("Total incasari din consultatii: " + total);
	}
	
	public void setAnalysesAndPatients(ArrayList ans, ArrayList patients) {
		this.analyses.clear(); // = analyses;
		this.patientsForAnalyses.clear(); // = patients;
		listModel2.clear();
		analysisDetails.setText("");
System.out.println("--------------- setAnalysesAndPatients: " + ans.size() + ", " + patients.size());
		int index = allDoctors.getSelectedIndex();
		
		float total = 0;
		for (int i = 0; i < ans.size(); i++) {
			if (
				(index == 0) ||
				(
				 (index > 0) && 
				 ((Doctor)doctors.get(index - 1)).equals(((Analysis)ans.get(i)).getDoctor())
				)
			) {
				this.analyses.add(ans.get(i));
				try {
					this.patientsForAnalyses.add(patients.get(i));
				} catch (Exception e) {
					
				}
				listModel2.addElement(
					((Analysis)ans.get(i)).getDoctor().getName() + ", " +
					((Analysis)ans.get(i)).getDay() + "-" +
					((Analysis)ans.get(i)).getMonth() + "-" +
					((Analysis)ans.get(i)).getYear() + ", " +
					((Analysis)ans.get(i)).getAmount() + " RON"
				);
				total += ((Analysis)ans.get(i)).getAmount();
			}
		}
		analysisList.setSelectedIndex(0);
		totalAmount2.setText("Total incasari din analize: " + total);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == getPrintButton()) {
			StringBuffer toPrint = new StringBuffer();
			int index = allDoctors.getSelectedIndex();
			toPrint.append(PrintText.logo);
			toPrint.append("\n\nData: " + months.getSelectedItem() + " " + year.getText() + "\n\n");
			toPrint.append("Doctor: " + allDoctors.getSelectedItem() + "\n\n");
			toPrint.append("Consultatii:\n\n");

			for (int i = 0; i < consultations.size(); i++) {
				if (
					(index == 0) ||
					(
				     (index > 0) && 
				     ((Doctor)doctors.get(index - 1)).equals(((Consultation)consultations.get(i)).getDoctor())
				    )
				) {
					Consultation c = (Consultation)consultations.get(i);
					toPrint.append(c.getDay() + " " + months.getSelectedItem());
					if (index == 0) {
						// all doctors
						Doctor d = c.getDoctor();
						toPrint.append(", doctor " + ((d != null) ? d.getName() : " sters din BD"));
					}
					Patient p = (Patient)patientsForConsultations.get(i);
					toPrint.append(", consultatie pentru pacientul: " + ((p != null) ? p.getName() : " sters din BD "));
					toPrint.append(", suma: " + c.getAmount() + " RON\n");
				}
			}
			toPrint.append("\n");
			
			toPrint.append("Analize:\n\n");

			for (int i = 0; i < analyses.size(); i++) {
				if (
					(index == 0) ||
					(
				     (index > 0) && 
				     ((Doctor)doctors.get(index - 1)).equals(((Analysis)analyses.get(i)).getDoctor())
				    )
				) {
					Analysis a = (Analysis)analyses.get(i);
					toPrint.append(a.getDay() + " " + months.getSelectedItem());
					if (index == 0) {
						// all doctors
						Doctor d = a.getDoctor();
						toPrint.append(", doctor " + ((d != null) ? d.getName() : " sters din BD"));
					}
					Patient p = (Patient)patientsForAnalyses.get(i);
					toPrint.append(", analiza pentru pacientul: " + ((p != null) ? p.getName() : " sters din BD"));
					toPrint.append(", suma: " + a.getAmount() + " RON\n");
				}
			}
			toPrint.append("\n");

			PrinterJob printerJob = PrinterJob.getPrinterJob();
			/* Build a book containing pairs of page painters (Printables)
			 * and PageFormats. This example has a single page containing
			 * text.
			 */
			Book book = new Book();
			PageFormat format = new PageFormat();
			PrintText textToPrint = 
				new PrintText(toPrint.toString(), format, 0);
			
			for (int i = 0; i < textToPrint.getNumberOfPages(); i++) {
				//textToPrint.setPageNumber(i);
				book.append(textToPrint, format);
				textToPrint = 
					new PrintText(toPrint.toString(), format, i + 1);
			}
			/* Set the object to be printed (the Book) into the PrinterJob.
			 * Doing this before bringing up the print dialog allows the
			 * print dialog to correctly display the page range to be printed
			 * and to dissallow any print settings not appropriate for the
			 * pages to be printed.
			 */
			printerJob.setPageable(book); 


			/* Show the print dialog to the user. This is an optional step
			 * and need not be done if the application wants to perform
			 * 'quiet' printing. If the user cancels the print dialog then false
			 * is returned. If true is returned we go ahead and print.
			 */
			boolean doPrint = printerJob.printDialog();
			if (doPrint) { 
				try {
					// first save the consultation to a file
					/*
					FileOutputStream fos = 
						  new FileOutputStream("c:\\Chadi\\something.ps");
					PrintWriter out = 
						  new PrintWriter(new OutputStreamWriter(fos));

					// the header for postscript
					out.println("%!PS-2.0");
					out.println(consultationText.toString());
					out.flush();
					out.close();
					*/
					//new PrintPS();
					printerJob.print();
				} catch (Exception exception) {
					System.err.println("Printing error: " + exception);
				}
			}
		}
	}
	
	public static void main(String[] args) {
		PatientEditor pe = new PatientEditor(Configurer.getConfiguration().getServerName());
		StatisticsUI ui = new StatisticsUI(pe);
		pe.setStatisticsUI(ui);
	}
}
