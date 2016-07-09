/*
 * Created on Aug 20, 2006
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
 * @author Sorin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ConsultationEditorUI extends JFrame {
	private JLabel consultationLabel;
	private JButton addConsultationButton;
	private JButton clearFieldsButton;
	private JButton removeConsultationButton;
	private JButton printConsultation;
	private JLabel addConsultationLabel;
	private JTextField consultationDay;
	private JComboBox consultationMonth;
	private JTextField consultationYear;
	private JTextArea consultationText;
	private JTextField consultationSpecialty;
	private JTextField consultationDoctorName;
	private JTextField consultationAmount;
	private JComboBox allDoctors;
	private ArrayList doctors = new ArrayList();
	private Patient patient;
	private DefaultListModel listModel;
	private ArrayList consultations = new ArrayList();
	private ActionListener observer;
	private final JList consultationList;
	private final JTextArea consultationDetails;
	
	public ConsultationEditorUI(Patient patient, final ArrayList consultations, ActionListener observer) {
		super ("Consultatiile pentru pacientul " + patient.getName());
		this.consultations = consultations;
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
		
		consultationLabel = new JLabel("Consultatie:");
		addConsultationButton = new JButton("Adauga consultatie");
		clearFieldsButton = new JButton("Anuleaza campurile");
		removeConsultationButton = new JButton("Sterge consultatie");
		
		JPanel panel = new JPanel();
			
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		panel.setLayout(gridbag);

		// a list containing all the consultations passed as parameter
		listModel = new DefaultListModel();
		for (int i = 0; i < consultations.size(); i++) {
			listModel.addElement(
				((Consultation)consultations.get(i)).getDay() + "-" + 
				((Consultation)consultations.get(i)).getMonth() + "-" + 
				((Consultation)consultations.get(i)).getYear() + ", " + 
				((Consultation)consultations.get(i)).getSpecialty()
			);
		}
		consultationList = new JList(listModel);
		
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
						StringBuffer toDisplay = 
							new StringBuffer(
								"Text: " + consultation.getText() + "\n " +
								"Data: " + consultation.getDay() + "-" +
								consultation.getMonth() + "-" + consultation.getYear() + "\n " +
								"Specialitatea: " + consultation.getSpecialty() + "\n " +
								"Doctor: " + ((consultation.getDoctor() != null)?consultation.getDoctor().getName():"unknown") + "\n " +
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
		JScrollPane consultationScrollPane = new JScrollPane(consultationDetails);
		gridbag.setConstraints(consultationScrollPane, c);
		panel.add(consultationScrollPane);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 3; // make it 3 columns wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(removeConsultationButton, c);
		panel.add(removeConsultationButton);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 3;
		c.gridy = 4;
		c.gridwidth = 1; 
		c.gridheight = 1;
		c.anchor = GridBagConstraints.EAST;
		printConsultation = new JButton("Print");
		gridbag.setConstraints(printConsultation, c);
		panel.add(printConsultation);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (10, 5, 0, 0); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 3; // make it 3 columns wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		addConsultationLabel = new JLabel("Adauga consultatie noua: ");
		gridbag.setConstraints(addConsultationLabel, c);
		panel.add(addConsultationLabel);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 6;
		c.gridwidth = 3; // make it 3 columns wide
		c.gridheight = 9;
		c.anchor = GridBagConstraints.WEST;
		consultationText = new JTextArea();
		consultationText.setColumns(50);
		consultationText.setRows(15);
		JScrollPane consultationTextPane = new JScrollPane(consultationText);
		gridbag.setConstraints(consultationTextPane, c);
		panel.add(consultationTextPane);

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
		consultationSpecialty = new JTextField();
		gridbag.setConstraints(consultationSpecialty, c);
		panel.add(consultationSpecialty);

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
		consultationDay = new JTextField();
		consultationDay.setColumns(3);
		gridbag.setConstraints(consultationDay, c);
		panel.add(consultationDay);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 4;
		c.gridy = 9;
		c.gridwidth = 1; // make it 3 columns wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.EAST;
		String[] months = {"Ian", "Feb", "Mar", "Apr", "Mai", "Iun", "Iul", "Aug", "Sep", "Oct", "Noi", "Dec"};
		consultationMonth = new JComboBox(months);
		consultationMonth.setSelectedIndex(0);
		gridbag.setConstraints(consultationMonth, c);
		panel.add(consultationMonth);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 5;
		c.gridy = 9;
		c.gridwidth = 1; // make it 3 columns wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.EAST;
		consultationYear = new JTextField();
		consultationYear.setColumns(5);
		gridbag.setConstraints(consultationYear, c);
		panel.add(consultationYear);

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
		consultationDoctorName = new JTextField();
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
			gridbag.setConstraints(consultationDoctorName, c);
			panel.add(consultationDoctorName);
		}
		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 3;
		c.gridy = 12;
		c.gridwidth = 1; 
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
		consultationAmount = new JTextField();
		gridbag.setConstraints(consultationAmount, c);
		panel.add(consultationAmount);

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
		gridbag.setConstraints(addConsultationButton, c);
		panel.add(addConsultationButton);
		
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
		addConsultationButton.addActionListener(observer);
		removeConsultationButton.addActionListener(observer);
		printConsultation.addActionListener(observer);
		
		getContentPane().add(panel, BorderLayout.CENTER);
		pack();
		setVisible(true);
	}

	public void addConsultation(Consultation c) {
		consultations.add(c);
		listModel.addElement(c.getDay() + "-" + c.getMonth() + "-" + c.getYear() + 
				", " + c.getSpecialty());
		consultationList.setSelectedIndex(consultations.size() - 1);
	}
	
	public void removeConsultation(Consultation c) {
		consultations.remove(c);
		listModel.removeElement(c.getDay() + "-" + c.getMonth() + "-" + c.getYear() +
				", " + c.getSpecialty());
		consultationDetails.setText("");
		consultationList.setSelectedIndex(0);
	}
	
	public Consultation getCurrentConsultation() {
		int selectedConsultation = consultationList.getSelectedIndex();
		if ((selectedConsultation >= 0) && (selectedConsultation < consultations.size())) {
			return (Consultation)consultations.get(selectedConsultation);
		}
		return null;
	}
	
	public Patient getCurrentPatient() {
		return patient;
	}
	
	/**
	 * 
	 * @uml.property name="observer"
	 */
	public void setObserver(ActionListener observer) {
		this.observer = observer;
	}

	/**
	 * @return Returns the addConsultationButton.
	 */
	public JButton getAddConsultationButton() {
		return addConsultationButton;
	}
	
	/**
	 * @return Returns the removeConsultationButton.
	 */
	public JButton getRemoveConsultationButton() {
		return removeConsultationButton;
	}

	/**
	 * @return Returns the clearFieldsButton.
	 */
	public JButton getClearFieldsButton() {
		return clearFieldsButton;
	}
	
	public String getNewConsultationText() {
		return consultationText.getText();
	}
	
	public String getNewConsultationSpecialty() {
		return consultationSpecialty.getText();
	}
	
	public JButton getPrintButton() {
		return printConsultation;
	}
	
	// TODO: check also how to return a Date from a String
	public Date getNewConsultationDate() {
		int selectedMonth = consultationMonth.getSelectedIndex();
		return new Date(
			new Integer(consultationYear.getText()).intValue() - 1900, 
			selectedMonth, 
			new Integer(consultationDay.getText()).intValue()
		);
	}
	
	public float getNewConsultationAmount() {
		return new Float(consultationAmount.getText()).floatValue();
	}
	
	public Doctor getNewConsultationDoctor() {
		int selectedIndex = allDoctors.getSelectedIndex();
		return (Doctor)doctors.get(selectedIndex);
	}
	
	public Patient getPatient() {
		return patient;
	}
	
	public void clearFields() {
		consultationText.setText("");
		consultationAmount.setText("");
		consultationDay.setText("");
		consultationMonth.setSelectedIndex(0);
		consultationYear.setText("");
		consultationDoctorName.setText("");
		if (allDoctors != null) allDoctors.setSelectedIndex(0);
		consultationSpecialty.setText("");
	}
	
	public static void main(String[] args) {
		ArrayList patients = new ArrayList();
		ArrayList consultations = new ArrayList();
		//patients.add("patient 1");
		//patients.add("patient 2\ntel. 01212323423\ntest adresa");
		PatientEditor pe = new PatientEditor(Configurer.getConfiguration().getServerName());
		try {
			patients = pe.getPatientsForName("Gogu Gogescu");
			consultations = 
				pe.getConsultationsForPatient((Patient)patients.get(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
		ConsultationEditorUI editor = 
			new ConsultationEditorUI((Patient)patients.get(0), consultations, pe);
		pe.setConsultationUI(editor);
		//ActionListener observer = new PatientEditor(editor);
		//editor.setObserver(observer); 
	}

}
