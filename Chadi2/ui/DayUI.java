/*
 * Created on Oct 17, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package ui;

import agenda.*;

import java.sql.Time;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import configuration.Configurer;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.util.*;

/**
 * @author Sorin
 *
 * Search for patients by name and add new patients.
 * 
 */

class ExitListener extends WindowAdapter {
	
	private DayScheduleUI ui;
	
	public ExitListener(DayScheduleUI ui) {
		this.ui = ui;
	}
	
	public void windowClosing(WindowEvent event) {
		if (ui != null) ui.dispose();
	    //System.exit(0);
	}
}

public class DayUI extends JFrame implements ActionListener {
	private Date currentDate;
	private JLabel addLabel, addPatientLabel, nameLabel, sexLabel, birthLabel;
	private JLabel doctorLabel, patientLabel, hourLabel, addressLabel, 
		fixPhoneLabel, mobilePhoneLabel;
	private JTextField hourText, minuteText, nameText, birthDay, birthYear, 
		addressText, fixPhoneText, mobilePhoneText, durationText;
	private JTextField appointmentText;
	private JComboBox doctorNamesCombo, patientNamesCombo, birthMonth;
	private JButton addButton, removeAppointmentButton, showSchedule, print;
	private JRadioButton maleButton, femaleButton;
	private JTextArea patientDetails;
	private ArrayList appointments = new ArrayList();
	private ArrayList doctors = new ArrayList();
	private ArrayList patients = new ArrayList();
	private JList appointmentsList = new JList();
	private JList patientsList;
	private DefaultListModel patientsListModel;
	private DefaultListModel listModel;
	private JButton search;
	private JTextField searchText;
	private ActionListener observer;
	private DayScheduleUI daySchedule;
	private static String[] months = 
		new String[] {
			"Ianuarie",
			"Februarie",
			"Martie",
			"Aprilie",
			"Mai",
			"Iunie",
			"Iulie",
			"August",
			"Septembrie",
			"Octombrie",
			"Noiembrie",
			"Decembrie"
		};
	private static final String SRC_PAT = "cautati pacient";
	
	public DayUI(AppointmentTextArea textArea, final ActionListener observer) {
		super(
			textArea != null 
			? 
			textArea.getDate().getDate() + " " + months[textArea.getDate().getMonth()] + " " + (textArea.getDate().getYear() + 1900)
			:
			"Test"
		);
		currentDate = (textArea != null) ? textArea.getDate() : new Date();
		this.observer = observer;
		daySchedule = 
			new DayScheduleUI(currentDate.getDate(), currentDate.getMonth() + 1, currentDate.getYear() + 1900);

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
				
////////////////////////////////////////////////////////////////////
		// a list containing all the appointments for this day
		listModel = new DefaultListModel();
		// TODO: replace this hack by something more reusable (i.e., this class
		// should'n know about PatientEditor)
		try {
			if (textArea != null) {
				appointments = textArea.getAppointments();
				Editor.sortListByDoctor(appointments);
//System.out.println("DayUI - in constructor *****************************");				
				for (int i = 0; i < appointments.size(); i++) {
//System.out.println((Appointment)appointments.get(i));					
					listModel.addElement(((Appointment)appointments.get(i)).toString());
				}
//System.out.println("*****************************************************");				
			}
			appointmentsList = new JList(listModel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// TODO: don't mix UI code with processing logic
		appointmentsList.addListSelectionListener(
			new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					int selectedIndex = appointmentsList.getSelectedIndex();
					if ((selectedIndex >= 0) && (selectedIndex < appointments.size())) {
						Appointment appointment = (Appointment)(appointments.get(selectedIndex));
						/* Maybe we should add a text area with the appointment details
						StringBuffer toDisplay = 
							new StringBuffer(
								patient.getName() + ", " + patient.getSex() + "\n " +
								"Data nasterii: " + patient.getBirthDay() + "-" + 
								patient.getBirthMonth() + "-" + patient.getBirthYear() + "\n " +
								"Adresa: " + patient.getAddress() + "\n " +
								"Telefoane: "
							);
						for (int i = 0; i < patient.getPhoneNumbers().size(); i++) {
							toDisplay.append((String)(patient.getPhoneNumbers().get(i)));
							toDisplay.append(", ");
						}
						toDisplay.append("\n ");
						appointmentDetails.setText(toDisplay.toString());
						*/
					}
				}
			}
		);
		appointmentsList.setSelectedIndex(0);
		//appointmentsList.setPreferredSize(new Dimension(200, 500));
		JScrollPane listScrollPane = new JScrollPane(appointmentsList);
		//listScrollPane.setPreferredSize(new Dimension(100, 400));
		
		// fill its display area horizontally
		c.fill = GridBagConstraints.HORIZONTAL;  
		Insets ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 7; // make it 7 columns wide
		c.gridheight = 4;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(listScrollPane, c);
		panel.add(listScrollPane);
		
		/*
		c.fill = GridBagConstraints.HORIZONTAL;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 3;
		c.gridy = 5;
		c.gridwidth = 3; // make it 3 columns wide
		c.gridheight = 4;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane patientDetailsPane = new JScrollPane(patientDetails);
		gridbag.setConstraints(patientDetailsPane, c);
		panel.add(patientDetailsPane);
		*/
		
		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 1; 
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		removeAppointmentButton = new JButton("Anuleaza programarea");
		gridbag.setConstraints(removeAppointmentButton, c);
		panel.add(removeAppointmentButton);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 1;
		c.gridy = 4;
		c.gridwidth = 1; 
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		print = new JButton("Tipareste");
		gridbag.setConstraints(print, c);
		panel.add(print);

	/////////////////////////////////////////////////////////////////////
		
		c.fill = GridBagConstraints.NONE;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		// the 5th position, since the previous 2 items take up 4 positions
		c.gridx = 0; 
		c.gridy = 5;
		c.gridwidth = 4;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		addLabel = new JLabel("Programare noua:");
		gridbag.setConstraints(addLabel, c);
		panel.add(addLabel);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 6;
		c.gridwidth = 1; // make it 1 column wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		doctorLabel = new JLabel("Doctor: ");
		gridbag.setConstraints(doctorLabel, c);
		panel.add(doctorLabel);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 1;
		c.gridy = 6;
		c.gridwidth = 5; 
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		
		if (observer != null) {
			try {
				doctors = ((AgendaEditor)observer).getDoctors();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String[] doctorNames = new String[doctors.size()];
		for (int i = 0; i < doctors.size(); i++) {
			doctorNames[i] = ((Doctor)doctors.get(i)).getName();
		}
		//doctorNames[doctors.size()] = "--------";
		
		doctorNamesCombo = new JComboBox(doctorNames);
		if (doctorNames.length > 0) doctorNamesCombo.setSelectedIndex(0);
		gridbag.setConstraints(doctorNamesCombo, c);
		panel.add(doctorNamesCombo);
		
		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 7;
		c.gridwidth = 1; // make it 1 column wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		patientLabel = new JLabel("Pacient: ");
		gridbag.setConstraints(patientLabel, c);
		panel.add(patientLabel);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 1;
		c.gridy = 7;
		c.gridwidth = 3; 
		c.gridheight = 3;
		c.anchor = GridBagConstraints.WEST;
		patientsListModel = new DefaultListModel();
		patientsListModel.addElement(SRC_PAT);
		patientsList = new JList(patientsListModel);
		JScrollPane scrollPane = new JScrollPane(patientsList);
		gridbag.setConstraints(scrollPane, c);
		panel.add(scrollPane);

		// TODO: don't mix UI code with processing logic
		patientsList.addListSelectionListener(
			new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					int selectedIndex = patientsList.getSelectedIndex();
					if ((selectedIndex >= 0) && (selectedIndex < patients.size())) {
						Patient patient = (Patient)(patients.get(selectedIndex));
						//String patientName = (String)(list.getSelectedValue());
						//patientDetails.setText(patientName);
						StringBuffer toDisplay = 
							new StringBuffer(
								patient.getName() + "\n " +
								" " + patient.getBirthDay() + "-" + 
								patient.getBirthMonth() + "-" + patient.getBirthYear() + "\n " +
								patient.getAddress() + "\n " +
								"Telefoane: "
							);
						for (int i = 0; i < patient.getPhoneNumbers().size(); i++) {
							toDisplay.append((String)(patient.getPhoneNumbers().get(i)));
							toDisplay.append("\n ");
						}
						//toDisplay.append("\n ");
						patientDetails.setText(toDisplay.toString());
					}
				}
			}
		);
		
		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 5;
		c.gridy = 7;
		c.gridwidth = 3; 
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		searchText = new JTextField();
		searchText.setColumns(12);
		gridbag.setConstraints(searchText, c);
		panel.add(searchText);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 5;
		c.gridy = 8;
		c.gridwidth = 3; 
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		patientDetails = new JTextArea();
		//patientDetails.setColumns(30);
		//patientDetails.setRows(4);
		patientDetails.setEditable(false);
		patientDetails.setEnabled(false);
		//patientDetails.setSize(100, 50);
		patientDetails.setPreferredSize(new Dimension(180, 90));
		JScrollPane scrollPane2 = new JScrollPane();
		//scrollPane2.add(patientDetails);
		//scrollPane2.setPreferredSize(new Dimension(150, 100));
		gridbag.setConstraints(patientDetails, c);
		panel.add(patientDetails);
		
		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 5;
		c.gridy = 9;
		c.gridwidth = 2; 
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		search = new JButton("Cauta");
		gridbag.setConstraints(search, c);
		panel.add(search);

		
//		if (observer != null) {
//			try {
//				patients = ((AgendaEditor)observer).getPatients();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		String[] patientNames = new String[patients.size()];
//		for (int i = 0; i < patients.size(); i++) {
//			patientNames[i] = ((Patient)patients.get(i)).getName();
//		}
//		//patientNames[patients.size()] = "--------";
//		
//		patientNamesCombo = new JComboBox(patientNames);
//		patientNamesCombo.setSelectedIndex(0);
//		gridbag.setConstraints(patientNamesCombo, c);
//		panel.add(patientNamesCombo);
		
		//////////////////
		// Add a new patient
		c.fill = GridBagConstraints.NONE;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0; 
		c.gridy = 10;
		c.gridwidth = 4;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		addPatientLabel = new JLabel("Adauga pacient nou:");
		gridbag.setConstraints(addPatientLabel, c);
		panel.add(addPatientLabel);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 11;
		c.gridwidth = 1; // make it 1 column wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		nameLabel = new JLabel("Nume: ");
		gridbag.setConstraints(nameLabel, c);
		panel.add(nameLabel);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 1;
		c.gridy = 11;
		c.gridwidth = 3; // make it 3 columns wide
		c.gridheight = 1;
		c.weightx = 1;
		c.anchor = GridBagConstraints.CENTER;
		nameText = new JTextField();
		gridbag.setConstraints(nameText, c);
		panel.add(nameText);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 12;
		c.gridwidth = 1; // make it 1 column wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		sexLabel = new JLabel("Sex: ");
		gridbag.setConstraints(sexLabel, c);
		panel.add(sexLabel);

		maleButton = new JRadioButton("Masculin");
		maleButton.setActionCommand("Masculin");
		maleButton.setSelected(true);
		femaleButton = new JRadioButton("Feminin");
		femaleButton.setActionCommand("Feminin");
		femaleButton.setSelected(true);
		ButtonGroup group = new ButtonGroup();
		group.add(maleButton);
		group.add(femaleButton);
		
		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 1;
		c.gridy = 12;
		c.gridwidth = 1; // make it 1 column wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(maleButton, c);
		panel.add(maleButton);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 2;
		c.gridy = 12;
		c.gridwidth = 1; // make it 1 column wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(femaleButton, c);
		panel.add(femaleButton);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 13;
		c.gridwidth = 1; // make it 1 column wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		birthLabel = new JLabel("Data nasterii (zz-ll-aaaa): ");
		gridbag.setConstraints(birthLabel, c);
		panel.add(birthLabel);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 1;
		c.gridy = 13;
		c.gridwidth = 1; 
		c.gridheight = 1;
		c.weightx = 1;
		c.anchor = GridBagConstraints.CENTER;
		birthDay = new JTextField();
		birthDay.setColumns(1);
		gridbag.setConstraints(birthDay, c);
		panel.add(birthDay);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 2;
		c.gridy = 13;
		c.gridwidth = 1; 
		c.gridheight = 1;
		c.weightx = 0;
		c.weighty = 0;
		c.anchor = GridBagConstraints.EAST;
		String[] months = {"Ian", "Feb", "Mar", "Apr", "Mai", "Iun", "Iul", "Aug", "Sep", "Oct", "Noi", "Dec"};
		birthMonth = new JComboBox(months);
		birthMonth.setSelectedIndex(0);
		gridbag.setConstraints(birthMonth, c);
		panel.add(birthMonth);
		
		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 3;
		c.gridy = 13;
		c.gridwidth = 1; // make it 3 columns wide
		c.gridheight = 1;
		c.weightx = 1;
		c.anchor = GridBagConstraints.CENTER;
		birthYear = new JTextField();
		birthYear.setColumns(3);
		gridbag.setConstraints(birthYear, c);
		panel.add(birthYear);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 14;
		c.gridwidth = 1; // make it 1 column wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		addressLabel = new JLabel("Adresa: ");
		gridbag.setConstraints(addressLabel, c);
		panel.add(addressLabel);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 1;
		c.gridy = 14;
		c.gridwidth = 3; // make it 3 columns wide
		c.gridheight = 1;
		c.weightx = 1;
		c.anchor = GridBagConstraints.CENTER;
		addressText = new JTextField();
		gridbag.setConstraints(addressText, c);
		panel.add(addressText);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 15;
		c.gridwidth = 1; // make it 1 column wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		fixPhoneLabel = new JLabel("Telefon fix: ");
		gridbag.setConstraints(fixPhoneLabel, c);
		panel.add(fixPhoneLabel);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 1;
		c.gridy = 15;
		c.gridwidth = 3; // make it 3 columns wide
		c.gridheight = 1;
		c.weightx = 1;
		c.anchor = GridBagConstraints.CENTER;
		fixPhoneText = new JTextField();
		gridbag.setConstraints(fixPhoneText, c);
		panel.add(fixPhoneText);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 16;
		c.gridwidth = 1; // make it 1 column wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		mobilePhoneLabel = new JLabel("Telefon mobil: ");
		gridbag.setConstraints(mobilePhoneLabel, c);
		panel.add(mobilePhoneLabel);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 1;
		c.gridy = 16;
		c.gridwidth = 3; // make it 3 columns wide
		c.gridheight = 1;
		c.weightx = 1;
		c.anchor = GridBagConstraints.CENTER;
		mobilePhoneText = new JTextField();
		gridbag.setConstraints(mobilePhoneText, c);
		panel.add(mobilePhoneText);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 17;
		c.gridwidth = 1; // make it 1 column wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		hourLabel = new JLabel("Ora: ");
		gridbag.setConstraints(hourLabel, c);
		panel.add(hourLabel);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 1;
		c.gridy = 17;
		c.gridwidth = 1; // make it 1 column wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.EAST;
		hourText = new JTextField();
		//hourText.setSize(40, 10);
		hourText.setColumns(2);
		hourText.setEditable(true);
		gridbag.setConstraints(hourText, c);
		panel.add(hourText);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 2;
		c.gridy = 17;
		c.gridwidth = 1; // make it 1 column wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		JLabel colon = new JLabel(":");
		gridbag.setConstraints(colon, c);
		panel.add(colon);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 2;
		c.gridy = 17;
		c.gridwidth = 1; // make it 1 column wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.EAST;
		minuteText = new JTextField();
		minuteText.setColumns(2);
		//minuteText.setSize(40, 10);
		minuteText.setEditable(true);
		gridbag.setConstraints(minuteText, c);
		panel.add(minuteText);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 18;
		c.gridwidth = 1; // make it 1 column wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.EAST;
		JLabel durationLabel = new JLabel("Durata (min): ");
		//minuteText.setSize(40, 10);
		gridbag.setConstraints(durationLabel, c);
		panel.add(durationLabel);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 1;
		c.gridy = 18;
		c.gridwidth = 1; // make it 1 column wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.EAST;
		durationText = new JTextField("60");
		//minuteText.setSize(40, 10);
		gridbag.setConstraints(durationText, c);
		panel.add(durationText);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 19;
		c.gridwidth = 1; // make it 1 column wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.EAST;
		JLabel textLabel = new JLabel("Text: ");
		//minuteText.setSize(40, 10);
		gridbag.setConstraints(textLabel, c);
		panel.add(textLabel);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 1;
		c.gridy = 19;
		c.gridwidth = 1; // make it 1 column wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.EAST;
		appointmentText = new JTextField();
		gridbag.setConstraints(appointmentText, c);
		panel.add(appointmentText);

		
		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 20;
		c.gridwidth = 1; 
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		addButton = new JButton("Adauga");
		gridbag.setConstraints(addButton, c);
		panel.add(addButton);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 1;
		c.gridy = 20;
		c.gridwidth = 1; 
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		showSchedule = new JButton("Grafic programari");
		gridbag.setConstraints(showSchedule, c);
		panel.add(showSchedule);
		
		showSchedule.addActionListener(this);
		print.addActionListener(this);
		search.addActionListener(observer);
		addButton.addActionListener(observer);
		removeAppointmentButton.addActionListener(observer);

		addWindowListener(new ExitListener(daySchedule));
		
		getContentPane().add(panel, BorderLayout.CENTER);
		pack();
		setVisible(true);
	}
	
	public void dispose() {
		//System.out.println("DISPOSING " + this);
		super.dispose();
		if (daySchedule != null) daySchedule.dispose();
	}
	
	public String getPatientName() {
		return searchText.getText();
	}
	
	public void setPatients(ArrayList patients) {
		this.patients.clear();
		patientsListModel.clear();
		for (int i = 0; i < patients.size(); i++) {
			this.patients.add((Patient)patients.get(i));
			patientsListModel.addElement(((Patient)patients.get(i)).getName());
		}
		patientsList.setSelectedIndex(0);
	}
	
	/**
	 * Returns null if the user tried to insert a new patient, but he/she hasn't filled in
	 * all fields.
	 * Otherwise, returns the new patient introduced by the user if it exists, else the
	 * patient chosen from the list.
	 */
	private Patient getCurrentPatient() {
		Patient patient = null;
		try {
			patient = getNewPatient();
		} catch (Exception e) {
			// the user tried to add a new patient, but hasn't filled in all fields
			// error message and patient is not added, appointment is not made
			JOptionPane.showMessageDialog(
				this, 
				"Eroare ! Ati introdus corect datele pacientului ?"
			);
			return null;
		}
		if (patient != null) return patient;
		
		Patient chosenPatient = null;
		try {
			chosenPatient = (Patient)patients.get(patientsList.getSelectedIndex());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(
					this, 
					"Eroare ! Ati introdus corect datele pacientului ?"
				);
			return null;
		}
		return chosenPatient;
				//(Patient)patients.get(patientNamesCombo.getSelectedIndex());
	}
	
	private Doctor getCurrentDoctor() {
		Doctor doctor = null;
		try {
			doctor = (Doctor)doctors.get(doctorNamesCombo.getSelectedIndex());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(
				this, 
				"Eroare ! Ati selectat doctorul ?"
			);
		}
		return doctor;
	}
		
	
	private Time getCurrentTime() {
		Time t = null;
		try {
			t = new Time(
					new Integer(hourText.getText()).intValue(), 
					(minuteText.getText().length() < 1) ? 
					0 : 
					new Integer(minuteText.getText()).intValue(),
					0
				);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(
				this, 
				"Eroare ! Ati introdus corect ora:minute ?"
			);
		}
		return t;
	}
	
	public Appointment getCurrentAppointment() throws Exception {
		Patient p = getCurrentPatient();
		Appointment appointment = null;
		try {
			appointment = 
				new Appointment(currentDate, getCurrentTime(), getCurrentDoctor(), p);
			if (
				(durationText.getText() != null) && 
				!durationText.getText().equals("")
			) {
				appointment.setDuration(
					new Integer(durationText.getText()).intValue()
				);
			}
			if (
				(appointmentText.getText() != null) && 
				!appointmentText.getText().equals("")
			) {
				appointment.setText(appointmentText.getText());
			}
		} catch (Exception e) {
//			JOptionPane.showMessageDialog(
//					this, 
//					"Eroare ! Ati introdus toate campurile ?"
//				);
			return null;
		}
		return (p != null) ? appointment : null;
	}
	
	public Appointment getSelectedAppointment() {
		//System.out.println("DAYUI, getSelectedAppointment: " + appointmentsList.getSelectedIndex());
		//System.out.println("DAYUI, getSelectedAppointment: " + (Appointment)appointments.get(appointmentsList.getSelectedIndex()));
		return (Appointment)appointments.get(appointmentsList.getSelectedIndex());
	}
	
	public void addAppointment(Appointment appointment) {
		appointments.add(appointment);
		Editor.sortListByDoctor(appointments);
//System.out.println("DAYUI, new appointment: " + appointment);
		listModel.clear();
		for (int i = 0; i < appointments.size(); i++) {
			listModel.addElement(((Appointment)appointments.get(i)).toString());
		}
		//listModel.addElement(appointment.toString());
		//appointmentsList = new JList(listModel);
		appointmentsList.setSelectedIndex(appointments.size() - 1);
		
		// also add the appointment to the graphical schedule
		if (daySchedule != null) {
			if (appointment != null) {
				daySchedule.addAppointment(appointment);
			}
		}
	}
	
	public void removeAppointment(Appointment appointment) {
		//System.out.println("DAYUI, removeAppointment: " + appointment);
		appointments.remove(appointment);
		//Editor.sortList(appointments);
		listModel.removeElement(appointment.toString());
		/*
		listModel.clear();
		for (int i = 0; i < appointments.size(); i++) {
			listModel.addElement(((Appointment)appointments.get(i)).toString());
		}
		*/
		
		appointmentsList.setSelectedIndex(appointments.size() - 1);
		
		// also remove the appointment from the graphical schedule
		if (daySchedule != null) {
			if (appointment != null) {
				daySchedule.removeAppointment(appointment);
			}
		}
	}
	
	public boolean isNewPatient() {
		Patient p = null;
		try {
			p = getNewPatient();
		} catch (Exception e) {
			return false;
		}
		return (p != null);
	}
	
	/**
	 * 
	 * @uml.property name="observer"
	 */
	public void setObserver(ActionListener observer) {
		this.observer = observer;
	}

	/**
	 * @return Returns the addButton.
	 */
	public JButton getAddButton() {
		return addButton;
	}
	
	public JButton getRemoveButton() {
		return removeAppointmentButton;
	}
	
	public JButton getSearchButton() {
		return search;
	}
	
	private Date getNewPatientBirthDate() {
		int selectedMonth = birthMonth.getSelectedIndex();
		return new Date(
			new Integer(birthYear.getText()).intValue() - 1900, 
			selectedMonth, 
			new Integer(birthDay.getText()).intValue()
		);
	}
	
	private Patient getNewPatient() throws Exception {
		if (
			(nameText.getText() == null) ||
			(nameText.getText().length() <= 1)
		) return null;
		
		Patient patient = null;
		try {
			patient = 			
				new Patient(
					nameText.getText(),
					maleButton.isSelected() ? 'm' : 'f',
					getNewPatientBirthDate(),
					addressText.getText()
				);
			String fixPhone = fixPhoneText.getText();
			String mobilePhone = mobilePhoneText.getText();

			if ((fixPhone != null) && (fixPhone.length() > 0))
				patient.addPhoneNumber(fixPhone);
		
			if ((mobilePhone != null) && (mobilePhone.length() > 0))
				patient.addPhoneNumber(mobilePhone);
		} catch (Exception e) {
			patient = null;
			throw e;
		}
		return patient;
	}
	
	// add a new patient to the local array pf patients
	public void addPatient(Patient patient) {
		if (
			(patientsListModel.size() > 0) &&
			(patientsListModel.get(0) != null) && 
			(((String)patientsListModel.get(0)).equals(SRC_PAT))
		) {
			patientsListModel.clear();
			patients.clear();
		}
		patients.add(patient);
		patientsListModel.addElement(patient.getName());
		String[] patientNames = new String[patients.size()];
		//for (int i = 0; i < patients.size(); i++) {
		//	patientNames[i] = ((Patient)patients.get(i)).getName();
		//}
		//patientNames[patients.size()] = "--------";
		
		//patientNamesCombo.addItem(patient.getName()); 
		//patientNamesCombo = new JComboBox(patientNames);
		clearFields();
		//patientNamesCombo.setSelectedIndex(0);
		pack();
	}
	
	private void clearFields() {
		nameText.setText("");
		birthDay.setText("");
		birthYear.setText("");
		addressText.setText("");
		fixPhoneText.setText("");
		mobilePhoneText.setText("");
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == showSchedule) {
			if (observer != null) {
				daySchedule.setObserver(observer);
				daySchedule.initialize();
			}
		} else if (e.getSource() == print) {
			int selectedIndex = appointmentsList.getSelectedIndex();
			Appointment appointment = (Appointment)(appointments.get(selectedIndex));
			Doctor doctor = appointment.getDoctor();
			StringBuffer toPrint = new StringBuffer();
			toPrint.append(PrintText.logo);
			toPrint.append("\n\nProgramari Dr. " + doctor.getName() + "\n\n");
			toPrint.append("Data: " + currentDate.getDate() + "-" + (currentDate.getMonth() + 1) + "-" + (currentDate.getYear() + 1900) + "\n\n");
			for (int i = 0; i < appointments.size(); i++) {
				Appointment a = (Appointment)appointments.get(i);
				if (a.getDoctor().getId() == doctor.getId()) {
					toPrint.append(
						a.getTime().getHours() + ":" + 
						((a.getTime().getMinutes() == 0) ? "00" : a.getTime().getMinutes() + "") +
						", " + a.getPatient().getName() + ((a.getText() != null) ? (", " + a.getText()) : "") + "\n");
				}
			}
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
		ActionListener observer = new AgendaEditor(new AgendaUI(), Configurer.getConfiguration().getServerName());
		DayUI editor = new DayUI(null, observer);
		//editor.setObserver(observer);
		//((AgendaEditor)observer).setDayUI(editor);
	}

}
