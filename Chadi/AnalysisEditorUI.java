/*
 * Created on Sep 7, 2006
 *
 */
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.SwingUtilities;
import javax.swing.*;
import javax.swing.event.*;

import configuration.Configurer;

import java.util.*;

/**
 * @author Sorin Cristescu
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AnalysisEditorUI extends JFrame {
	private JLabel analysisLabel;
	private JButton addAnalysisButton;
	private JButton clearFieldsButton;
	private JButton removeAnalysisButton;
	private JButton printAnalysis;
	private JLabel addAnalysisLabel;
	private JTextField analysisDay;
	private JComboBox analysisMonth;
	private JTextField analysisYear;
	private JTextArea analysisText;
	private JTextField analysisSpecialty;
	private JTextField analysisDoctorName;
	private JTextField analysisAmount;
	private JComboBox allDoctors;
	private ArrayList doctors = new ArrayList();
	private Patient patient;
	private DefaultListModel listModel;
	private ArrayList analyses = new ArrayList();
	private ActionListener observer;
	private final JList analysisList;
	private final JTextArea analysisDetails;
	
	public AnalysisEditorUI(Patient patient, final ArrayList analyses, ActionListener observer) {
		super ("Analizele pentru pacientul " + patient.getName());
		this.analyses = analyses;
		this.observer = observer;
		this.patient = patient;
		
		/* TODO: I GET SOME UPDATE ERRORS WITH THE WINDOWS LOOK AND FEEL; CHECK THAT */
		String lf = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
		
		//String lf = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
		try {
			UIManager.setLookAndFeel(lf);
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		analysisLabel = new JLabel("Analiza:");
		addAnalysisButton = new JButton("Adauga analiza");
		clearFieldsButton = new JButton("Anuleaza campurile");
		removeAnalysisButton = new JButton("Sterge analiza");
		
		JPanel panel = new JPanel();
			
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		panel.setLayout(gridbag);

		// a list containing all the analyses passed as parameter
		listModel = new DefaultListModel();
		for (int i = 0; i < analyses.size(); i++) {
			listModel.addElement(
				((Analysis)analyses.get(i)).getDay() + "-" +
				((Analysis)analyses.get(i)).getMonth() + "-" +
				((Analysis)analyses.get(i)).getYear() + ", " + 
				((Analysis)analyses.get(i)).getSpecialty()
			);
		}
		analysisList = new JList(listModel);
		
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
						StringBuffer toDisplay = 
							new StringBuffer(
								"Text: " + analysis.getText() + "\n " +
								"Data: " + analysis.getDay() + "-" +
								analysis.getMonth() + "-" + analysis.getYear() + "\n " +
								"Specialitatea: " + analysis.getSpecialty() + "\n " +
								"Doctor: " + ((analysis.getDoctor() != null)?analysis.getDoctor().getName():"unknown") + "\n " +
								"Suma incasata: " + analysis.getAmount() + " RON"
							);
						toDisplay.append("\n ");
						analysisDetails.setText(toDisplay.toString());
					}
				}
			}
		);
		analysisList.setSelectedIndex(0);
		JScrollPane listScrollPane = new JScrollPane(analysisList);
		
		// fill its display area horizontally
		c.fill = GridBagConstraints.HORIZONTAL; 
		Insets ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3; // make it 3 columns wide
		c.gridheight = 4;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(listScrollPane, c);
		panel.add(listScrollPane);
		
		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 3;
		c.gridy = 0;
		c.gridwidth = 3; // make it 3 columns wide
		c.gridheight = 4;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane analysisScrollPane = new JScrollPane(analysisDetails);
		gridbag.setConstraints(analysisScrollPane, c);
		panel.add(analysisScrollPane);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 3; // make it 3 columns wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(removeAnalysisButton, c);
		panel.add(removeAnalysisButton);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 3;
		c.gridy = 4;
		c.gridwidth = 1; 
		c.gridheight = 1;
		c.anchor = GridBagConstraints.EAST;
		printAnalysis = new JButton("Print");
		gridbag.setConstraints(printAnalysis, c);
		panel.add(printAnalysis);
		
		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (10, 5, 0, 0); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 3; // make it 3 columns wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		addAnalysisLabel = new JLabel("Adauga analiza noua: ");
		gridbag.setConstraints(addAnalysisLabel, c);
		panel.add(addAnalysisLabel);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 6;
		c.gridwidth = 3; // make it 3 columns wide
		c.gridheight = 9;
		c.anchor = GridBagConstraints.WEST;
		analysisText = new JTextArea();
		analysisText.setColumns(50);
		analysisText.setRows(15);
		JScrollPane analysisTextPane = new JScrollPane(analysisText);
		gridbag.setConstraints(analysisTextPane, c);
		panel.add(analysisTextPane);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 3;
		c.gridy = 6;
		c.gridwidth = 3; // make it 3 columns wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.EAST;
		JLabel specialtyLabel = new JLabel("Specialitatea: ");
		gridbag.setConstraints(specialtyLabel, c);
		panel.add(specialtyLabel);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 3;
		c.gridy = 7;
		c.gridwidth = 3; // make it 3 columns wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.EAST;
		analysisSpecialty = new JTextField();
		gridbag.setConstraints(analysisSpecialty, c);
		panel.add(analysisSpecialty);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 3;
		c.gridy = 8;
		c.gridwidth = 3; // make it 3 columns wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.EAST;
		JLabel dateLabel = new JLabel("Data: ");
		gridbag.setConstraints(dateLabel, c);
		panel.add(dateLabel);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 3;
		c.gridy = 9;
		c.gridwidth = 1; // make it 3 columns wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.EAST;
		analysisDay = new JTextField();
		analysisDay.setColumns(3);
		gridbag.setConstraints(analysisDay, c);
		panel.add(analysisDay);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 4;
		c.gridy = 9;
		c.gridwidth = 1; // make it 3 columns wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.EAST;
		String[] months = {"Ian", "Feb", "Mar", "Apr", "Mai", "Iun", "Iul", "Aug", "Sep", "Oct", "Noi", "Dec"};
		analysisMonth = new JComboBox(months);
		analysisMonth.setSelectedIndex(0);
		gridbag.setConstraints(analysisMonth, c);
		panel.add(analysisMonth);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 5;
		c.gridy = 9;
		c.gridwidth = 1; // make it 3 columns wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.EAST;
		analysisYear = new JTextField();
		analysisYear.setColumns(5);
		gridbag.setConstraints(analysisYear, c);
		panel.add(analysisYear);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 3;
		c.gridy = 10;
		c.gridwidth = 3; // make it 3 columns wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.EAST;
		JLabel doctorLabel = new JLabel("Numele doctorului: ");
		gridbag.setConstraints(doctorLabel, c);
		panel.add(doctorLabel);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 3;
		c.gridy = 11;
		c.gridwidth = 3; // make it 3 columns wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.EAST;
		analysisDoctorName = new JTextField();
		allDoctors = null;
		try {
			// TODO: replace this trick with something more reusable (i.e. this class
			// shouldn't know about PatientEditor)
			
			// get the doctors from the DB through the observer
			doctors = ((PatientEditor)observer).getDoctors();
			String[] doctorStrings = new String[doctors.size()];
			for (int i = 0; i < doctors.size(); i++) {
				doctorStrings[i] = ((Doctor)(doctors.get(i))).getName();
			}
			allDoctors = new JComboBox(doctorStrings);
			allDoctors.setSelectedIndex(0);
			gridbag.setConstraints(allDoctors, c);
			panel.add(allDoctors);
		} catch (Exception e) {
			gridbag.setConstraints(analysisDoctorName, c);
			panel.add(analysisDoctorName);
		}
		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 3;
		c.gridy = 12;
		c.gridwidth = 3; // make it 3 columns wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.EAST;
		JLabel amountLabel = new JLabel("Suma incasata: ");
		gridbag.setConstraints(amountLabel, c);
		panel.add(amountLabel);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 3;
		c.gridy = 13;
		c.gridwidth = 2; 
		c.gridheight = 1;
		c.anchor = GridBagConstraints.EAST;
		analysisAmount = new JTextField();
		gridbag.setConstraints(analysisAmount, c);
		panel.add(analysisAmount);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 5;
		c.gridy = 13;
		c.gridwidth = 1; 
		c.gridheight = 1;
		c.anchor = GridBagConstraints.EAST;
		JLabel amountCurrency = new JLabel("RON");
		gridbag.setConstraints(amountCurrency, c);
		panel.add(amountCurrency);
		
		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 15;
		c.gridwidth = 3; // make it 3 columns wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(addAnalysisButton, c);
		panel.add(addAnalysisButton);
		
		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 3;
		c.gridy = 15;
		c.gridwidth = 3; // make it 3 columns wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(clearFieldsButton, c);
		panel.add(clearFieldsButton);
		
		clearFieldsButton.addActionListener(observer);
		addAnalysisButton.addActionListener(observer);
		removeAnalysisButton.addActionListener(observer);
		printAnalysis.addActionListener(observer);
		
		getContentPane().add(panel, BorderLayout.CENTER);
		pack();
		setVisible(true);
	}

	public void addAnalysis(Analysis c) {
		analyses.add(c);
		listModel.addElement(c.getDay() + "-" + c.getMonth() + "-" + c.getYear() +
				", " + c.getSpecialty());
		analysisList.setSelectedIndex(analyses.size() - 1);
	}
	
	public void removeAnalysis(Analysis c) {
		analyses.remove(c);
		listModel.removeElement(c.getDay() + "-" + c.getMonth() + "-" + c.getYear() + 
				", " + c.getSpecialty());
		analysisDetails.setText("");
		analysisList.setSelectedIndex(0);
	}
	
	public Analysis getCurrentAnalysis() {
		int selectedanalysis = analysisList.getSelectedIndex();
		if ((selectedanalysis >= 0) && (selectedanalysis < analyses.size())) {
			return (Analysis)analyses.get(selectedanalysis);
		}
		return null;
	}
	
	/**
	 * 
	 * @uml.property name="observer"
	 */
	public void setObserver(ActionListener observer) {
		this.observer = observer;
	}

	/**
	 * @return Returns the addanalysisButton.
	 */
	public JButton getAddAnalysisButton() {
		return addAnalysisButton;
	}
	
	/**
	 * @return Returns the removeanalysisButton.
	 */
	public JButton getRemoveAnalysisButton() {
		return removeAnalysisButton;
	}

	/**
	 * @return Returns the clearFieldsButton.
	 */
	public JButton getClearFieldsButton() {
		return clearFieldsButton;
	}
	
	public String getNewAnalysisText() {
		return analysisText.getText();
	}
	
	public String getNewAnalysisSpecialty() {
		return analysisSpecialty.getText();
	}
	
	// TODO: check also how to return a Date from a String
	public Date getNewAnalysisDate() {
		int selectedMonth = analysisMonth.getSelectedIndex();
		return new Date(
			new Integer(analysisYear.getText()).intValue() - 1900, 
			selectedMonth, 
			new Integer(analysisDay.getText()).intValue()
		);
	}
	
	public float getNewAnalysisAmount() {
		return new Float(analysisAmount.getText()).floatValue();
	}
	
	public Doctor getNewAnalysisDoctor() {
		int selectedIndex = allDoctors.getSelectedIndex();
		return (Doctor)doctors.get(selectedIndex);
	}
	
	public Patient getPatient() {
		return patient;
	}
	
	public void clearFields() {
		analysisText.setText("");
		analysisAmount.setText("");
		analysisDay.setText("");
		analysisMonth.setSelectedIndex(0);
		analysisYear.setText("");
		analysisDoctorName.setText("");
		if (allDoctors != null) allDoctors.setSelectedIndex(0);
		analysisSpecialty.setText("");
	}
	
	/**
	 * @return Returns the printAnalysis.
	 */
	public JButton getPrintButton() {
		return printAnalysis;
	}
	
	public static void main(String[] args) {
		ArrayList patients = new ArrayList();
		ArrayList analyses = new ArrayList();
		//patients.add("patient 1");
		//patients.add("patient 2\ntel. 01212323423\ntest adresa");
		PatientEditor pe = new PatientEditor(Configurer.getConfiguration().getServerName());
		try {
			patients = pe.getPatientsForName("Gogu Gogescu");
			analyses = 
				pe.getAnalysesForPatient((Patient)patients.get(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
		AnalysisEditorUI editor = 
			new AnalysisEditorUI((Patient)patients.get(0), analyses, pe);
		pe.setAnalysisUI(editor);
		//ActionListener observer = new PatientEditor(editor);
		//editor.setObserver(observer); 
	}
}
