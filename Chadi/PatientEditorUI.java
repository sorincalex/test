/*
 * Created on Aug 16, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import configuration.Configurer;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

class ListThread extends Thread {
	
}

/**
 * @author Sorin
 *
 * Search for patients by name and add new patients.
 * 
 */
public class PatientEditorUI extends JFrame implements ActionListener {
	private static final String[] months = 
		{"Ian", "Feb", "Mar", "Apr", "Mai", "Iun", "Iul", "Aug", "Sep", "Oct", "Noi", "Dec"};

	private JLabel searchLabel;
	private JTextField searchText;
	private final JButton searchButton;

	private JLabel addLabel;
	private JLabel nameLabel, nameLabel2;
	private JTextField nameText, patientName;
	private JLabel sexLabel;
	private JRadioButton maleButton, femaleButton;
	private JLabel birthLabel, birthLabel2;
	private JTextField birthDay, patientBirthDay, patientBirthMonth, patientSex;
	private JComboBox birthMonth;
	private JTextField birthYear, patientBirthYear;
	private JLabel addressLabel;
	private JTextField addressText, patientAddress;	
	private JLabel fixPhoneLabel;
	private JTextField fixPhoneText, patientFixPhone;
	private JLabel mobilePhoneLabel;
	private JTextField mobilePhoneText, patientMobilePhone;
	private JLabel emptyLabel;
	private JButton addButton;
	private final JTextArea patientDetails;
	private JButton displayAnalysesButton;
	private JButton displayConsultationsButton;
	private JButton editPatientButton;
	private JButton removePatientButton;
	private JButton showAllPatientsButton;
	private static ArrayList patients = new ArrayList();
	private JList patientsList = new JList();
	private DefaultListModel listModel;
	private ActionListener observer;
	private Thread listThread;
	
	public PatientEditorUI(ActionListener observer) {
		super ("Cauta pacienti dupa nume/ adauga pacient nou/ afiseaza toti pacientii");
		this.observer = observer;
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
		
		c.fill = GridBagConstraints.NONE;
		Insets ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 4;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		searchLabel = new JLabel("Cauta pacient dupa nume:");
		gridbag.setConstraints(searchLabel, c);
		panel.add(searchLabel);

		c.fill = GridBagConstraints.NONE; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1; // make it 1 column wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		nameLabel = new JLabel("Nume: ");
		gridbag.setConstraints(nameLabel, c);
		panel.add(nameLabel);
		
		// fill its display area horizontally
		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 2; // make it 3 columns wide
		c.gridheight = 1;
		c.weightx = 1;
		c.anchor = GridBagConstraints.WEST;
		searchText = new JTextField();
		searchText.setSize(120, 10);
		gridbag.setConstraints(searchText, c);
		panel.add(searchText);
		
		c.fill = GridBagConstraints.NONE;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 4; 
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		searchButton = new JButton("Cauta");
		gridbag.setConstraints(searchButton, c);
		panel.add(searchButton);

		c.fill = GridBagConstraints.NONE;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0; 
		c.gridy = 2;
		c.gridwidth = 3;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		showAllPatientsButton = new JButton("Afiseaza toti pacientii");
		gridbag.setConstraints(showAllPatientsButton, c);
		panel.add(showAllPatientsButton);

		c.fill = GridBagConstraints.NONE;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0; 
		c.gridy = 3;
		c.gridwidth = 4;
		c.gridheight = 2;
		c.anchor = GridBagConstraints.CENTER;
		emptyLabel = new JLabel();
		gridbag.setConstraints(emptyLabel, c);
		panel.add(emptyLabel);
		
////////////////////////////////////////////////////////////////////
		// a list containing all the names of the patients passed as parameter
		listModel = new DefaultListModel();
		// TODO: replace this hack by something more reusable (i.e., this class
		// should'n know about PatientEditor)
		try {
			// Disable the full list of patients; only the search is enabled
			//patients = ((PatientEditor)observer).getPatients();
			//for (int i = 0; i < patients.size(); i++) {
			//	listModel.addElement(((Patient)patients.get(i)).getName());
			//}
			patientsList = new JList(listModel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// the details about the selected patient will be displayed in patientDetails
		patientDetails = new JTextArea();
		patientDetails.setColumns(50);
		patientDetails.setRows(10);
		patientDetails.setEditable(false);
		
		patientName = new JTextField();
		patientAddress = new JTextField();
		patientBirthDay = new JTextField();
		patientBirthMonth = new JTextField();
		patientBirthYear = new JTextField();
		patientFixPhone = new JTextField();
		patientMobilePhone = new JTextField();
		patientSex = new JTextField();
		
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
						//patientDetails.setText(toDisplay.toString());
						patientName.setText("");
						patientSex.setText("");
						patientBirthDay.setText("");
						patientBirthMonth.setText("");
						patientBirthYear.setText("");
						patientAddress.setText("");
						patientFixPhone.setText("");
						patientMobilePhone.setText("");
						
						patientName.setText(patient.getName());
						patientSex.setText(patient.getSex() + "");
						patientBirthDay.setText(new Integer(patient.getBirthDay()).toString());
						patientBirthMonth.setText(new Integer(patient.getBirthMonth()).toString());
						patientBirthYear.setText(new Integer(patient.getBirthYear()).toString());
						patientAddress.setText(patient.getAddress());
						if (patient.getPhoneNumbers().size() > 0)
							patientFixPhone.setText((String)(patient.getPhoneNumbers().get(0)));
						if (patient.getPhoneNumbers().size() > 1)
							patientMobilePhone.setText((String)(patient.getPhoneNumbers().get(1)));
						
					}
				}
			}
		);
		patientsList.setSelectedIndex(0);
		JScrollPane listScrollPane = new JScrollPane(patientsList);
		
		// fill its display area horizontally
		c.fill = GridBagConstraints.HORIZONTAL;  
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 3; // make it 3 columns wide
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
		c.gridx = 3;
		c.gridy = 5;
		c.gridwidth = 1; 
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		nameLabel2 = new JLabel("Nume: ");
		gridbag.setConstraints(nameLabel2, c);
		panel.add(nameLabel2);

		c.fill = GridBagConstraints.HORIZONTAL;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 4;
		c.gridy = 5;
		c.gridwidth = 3; // make it 3 columns wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(patientName, c);
		panel.add(patientName);

		/*
		c.fill = GridBagConstraints.HORIZONTAL;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 5;
		c.gridy = 5;
		c.gridwidth = 1; // make it 2 columns wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(patientSex, c);
		panel.add(patientSex);
		*/
		
		c.fill = GridBagConstraints.HORIZONTAL;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 3;
		c.gridy = 6;
		c.gridwidth = 1; 
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		birthLabel2 = new JLabel("Data nasterii: ");
		gridbag.setConstraints(birthLabel2, c);
		panel.add(birthLabel2);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 4;
		c.gridy = 6;
		c.gridwidth = 1; 
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(patientBirthDay, c);
		panel.add(patientBirthDay);

		c.fill = GridBagConstraints.HORIZONTAL;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 5;
		c.gridy = 6;
		c.gridwidth = 1; 
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(patientBirthMonth, c);
		panel.add(patientBirthMonth);

		c.fill = GridBagConstraints.HORIZONTAL;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 6;
		c.gridy = 6;
		c.gridwidth = 1; 
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(patientBirthYear, c);
		panel.add(patientBirthYear);

		c.fill = GridBagConstraints.HORIZONTAL;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 3;
		c.gridy = 7;
		c.gridwidth = 3; // make it 3 columns wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		JLabel addressLabel2 = new JLabel("Adresa: ");
		gridbag.setConstraints(addressLabel2, c);
		panel.add(addressLabel2);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 4;
		c.gridy = 7;
		c.gridwidth = 3; // make it 3 columns wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(patientAddress, c);
		panel.add(patientAddress);

		c.fill = GridBagConstraints.HORIZONTAL;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 3;
		c.gridy = 8;
		c.gridwidth = 1; 
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		JLabel phonesLabel = new JLabel("Telefoane: ");
		gridbag.setConstraints(phonesLabel, c);
		panel.add(phonesLabel);

		c.fill = GridBagConstraints.HORIZONTAL;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 4;
		c.gridy = 8;
		c.gridwidth = 1; 
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(patientFixPhone, c);
		panel.add(patientFixPhone);

		c.fill = GridBagConstraints.HORIZONTAL;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 5;
		c.gridy = 8;
		c.gridwidth = 2; 
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(patientMobilePhone, c);
		panel.add(patientMobilePhone);

		
		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 9;
		c.gridwidth = 3; // make it 3 columns wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		displayAnalysesButton = new JButton("Afiseaza analizele");
		gridbag.setConstraints(displayAnalysesButton, c);
		panel.add(displayAnalysesButton);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 3;
		c.gridy = 9;
		c.gridwidth = 3; // make it 3 columns wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.EAST;
		editPatientButton = new JButton("Modifica pacient");
		gridbag.setConstraints(editPatientButton, c);
		panel.add(editPatientButton);
	/////////////////////////////////////////////////////////////////////
		
		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 11;
		c.gridwidth = 3; // make it 3 columns wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		displayConsultationsButton = new JButton("Afiseaza consultatiile");
		gridbag.setConstraints(displayConsultationsButton, c);
		panel.add(displayConsultationsButton);

		
		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 3;
		c.gridy = 11;
		c.gridwidth = 3; // make it 3 columns wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.EAST;
		removePatientButton = new JButton("Sterge pacient");
		gridbag.setConstraints(removePatientButton, c);
		panel.add(removePatientButton);
		
		c.fill = GridBagConstraints.NONE;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		// the 5th position, since the previous 2 items take up 4 positions
		c.gridx = 0; 
		c.gridy = 12;
		c.gridwidth = 4;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		addLabel = new JLabel("Adauga pacient nou:");
		gridbag.setConstraints(addLabel, c);
		panel.add(addLabel);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 13;
		c.gridwidth = 1; // make it 1 column wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		nameLabel2 = new JLabel("Nume: ");
		gridbag.setConstraints(nameLabel2, c);
		panel.add(nameLabel2);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 1;
		c.gridy = 13;
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
		c.gridy = 14;
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
		c.gridy = 14;
		c.gridwidth = 1; // make it 1 column wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(maleButton, c);
		panel.add(maleButton);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 2;
		c.gridy = 14;
		c.gridwidth = 1; // make it 1 column wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(femaleButton, c);
		panel.add(femaleButton);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 15;
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
		c.gridy = 15;
		c.gridwidth = 1; // make it 3 columns wide
		c.gridheight = 1;
		c.weightx = 1;
		c.anchor = GridBagConstraints.CENTER;
		birthDay = new JTextField();
		gridbag.setConstraints(birthDay, c);
		panel.add(birthDay);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 2;
		c.gridy = 15;
		c.gridwidth = 1; 
		c.gridheight = 1;
		c.anchor = GridBagConstraints.EAST;
		birthMonth = new JComboBox(months);
		birthMonth.setSelectedIndex(0);
		gridbag.setConstraints(birthMonth, c);
		panel.add(birthMonth);
		
		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 3;
		c.gridy = 15;
		c.gridwidth = 1; // make it 3 columns wide
		c.gridheight = 1;
		c.weightx = 1;
		c.anchor = GridBagConstraints.CENTER;
		birthYear = new JTextField();
		gridbag.setConstraints(birthYear, c);
		panel.add(birthYear);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 16;
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
		c.gridy = 16;
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
		c.gridy = 17;
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
		c.gridy = 17;
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
		c.gridy = 18;
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
		c.gridy = 18;
		c.gridwidth = 3; // make it 3 columns wide
		c.gridheight = 1;
		c.weightx = 1;
		c.anchor = GridBagConstraints.CENTER;
		mobilePhoneText = new JTextField();
		gridbag.setConstraints(mobilePhoneText, c);
		panel.add(mobilePhoneText);

		c.fill = GridBagConstraints.NONE;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0; 
		c.gridy = 19;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.EAST;
		addButton = new JButton("Adauga");
		gridbag.setConstraints(addButton, c);
		panel.add(addButton);
		
		searchButton.addActionListener(observer);
		showAllPatientsButton.addActionListener(observer);
		addButton.addActionListener(observer);
		displayConsultationsButton.addActionListener(this);
		displayAnalysesButton.addActionListener(this);
		removePatientButton.addActionListener(observer);
		editPatientButton.addActionListener(observer);
		Dimension d = new Dimension();
		d.setSize(640, 570);
		panel.setPreferredSize(d);
		getContentPane().add(panel, BorderLayout.CENTER);
		pack();
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == displayConsultationsButton) {
			final PatientEditor pe = new PatientEditor(Configurer.getConfiguration().getServerName());
			//synchronized(patients) {
				try {
					final int selectedPatientIndex = patientsList.getSelectedIndex();
					Thread consultThread = new Thread(
						new Runnable() {
							public void run() {
								displayConsultationsButton.setEnabled(false);
								ArrayList consultations = new ArrayList();
								try {
									if ((patients != null) && (patients.size() > 0) && (patients.get(selectedPatientIndex) != null)) {
										consultations = 
											pe.getConsultationsForPatient(
												(Patient)patients.get(selectedPatientIndex)
											);
										ConsultationEditorUI editor = 
											new ConsultationEditorUI(
												(Patient)patients.get(selectedPatientIndex), 
												consultations, 
												pe
											);
										pe.setConsultationUI(editor);
									}
								} catch (Exception e) {
									displayConsultationsButton.setEnabled(true);
									e.printStackTrace();
								}
								displayConsultationsButton.setEnabled(true);
							}
						}
					);
					consultThread.start();
					
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			//}
		} else if (e.getSource() == displayAnalysesButton) {
			final PatientEditor pe = new PatientEditor(Configurer.getConfiguration().getServerName());
			try {
				final int selectedPatientIndex = patientsList.getSelectedIndex();
				Thread consultThread = new Thread(
					new Runnable() {
						public void run() {
							displayAnalysesButton.setEnabled(false);
							ArrayList analyses = new ArrayList();
							try {
								if ((patients != null) && (patients.size() > 0) && (patients.get(selectedPatientIndex) != null)) {
									analyses = 
										pe.getAnalysesForPatient(
											(Patient)patients.get(selectedPatientIndex)
										);
									AnalysisEditorUI editor = 
										new AnalysisEditorUI(
											(Patient)patients.get(selectedPatientIndex), 
											analyses, 
											pe
										);
									pe.setAnalysisUI(editor);
								}
							} catch (Exception e) {
								displayAnalysesButton.setEnabled(true);
								e.printStackTrace();
							}
							displayAnalysesButton.setEnabled(true);
						}
					}
				);
				consultThread.start();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @uml.property name="observer"
	 */
	public void setObserver(ActionListener observer) {
		this.observer = observer;
	}

	public JButton getSearchButton() {
		return searchButton; 
	}
	
	/**
	 * @return Returns the showAllPatientsButton.
	 */
	public JButton getShowAllPatientsButton() {
		return showAllPatientsButton;
	}
	
	/**
	 * @return Returns the addButton.
	 */
	public JButton getAddButton() {
		return addButton;
	}
	
	/**
	 * @return Returns the removePatientButton.
	 */
	public JButton getRemoveButton() {
		return removePatientButton;
	}
	
	/**
	 * @return Returns the editPatientButton.
	 */
	public JButton getEditPatientButton() {
		return editPatientButton;
	}
	/**
	 * @param editPatientButton The editPatientButton to set.
	 */
	public void setEditPatientButton(JButton editPatientButton) {
		this.editPatientButton = editPatientButton;
	}
	
	public String getPatientName() {
		return searchText.getText();
	}
	
	public void setPatients(final ArrayList patients) {
			if ((listThread != null) && listThread.isAlive()) {
				listThread.interrupt();
			}
			listThread = new Thread(
					new Runnable() {
						public void run() {
							synchronized (PatientEditorUI.patients) {
								searchButton.setEnabled(false);
								showAllPatientsButton.setEnabled(false);
								PatientEditorUI.patients.clear();
								listModel.clear();
								
								for (int i = 0; i < patients.size(); i++) {
									if (Thread.interrupted()) break;
									PatientEditorUI.patients.add((Patient)patients.get(i));
									final int j = i;
									try {
										SwingUtilities.invokeAndWait(
												new Runnable() {
													public void run() {
														listModel.addElement(((Patient)patients.get(j)).getName());
													}
												}
										);
									} catch (Exception e) {
										searchButton.setEnabled(true);
										showAllPatientsButton.setEnabled(true);
										e.printStackTrace();
									}	
								}
								searchButton.setEnabled(true);
								showAllPatientsButton.setEnabled(true);
							}
						}
					}
			);
			listThread.start();
		
		patientDetails.setText("");
		patientsList.setSelectedIndex(0);
	}
	
	private Date getNewPatientBirthDate() {
		int selectedMonth = birthMonth.getSelectedIndex();
		return new Date(
			new Integer(birthYear.getText()).intValue() - 1900, 
			selectedMonth, 
			new Integer(birthDay.getText()).intValue()
		);
	}
	
	public Patient getNewPatient() {
		if (
			(nameText.getText() == null) ||
			(nameText.getText().length() <= 1)
		) return null;
				
		Patient patient = 
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
		
		return patient;
	}
	
	public Patient getSelectedPatient() {
		int selectedIndex = patientsList.getSelectedIndex();
		if ((selectedIndex >= 0) && (selectedIndex < patients.size())) {
			Patient patient = (Patient)(patients.get(selectedIndex));
			return patient;
		}
		return null;
	}
	
	public Patient getUpdatedPatient() {
		Patient patient = null;
		Patient selectedPatient = getSelectedPatient();
		if (selectedPatient != null) {
			Date birthDate = new Date(
					new Integer(patientBirthYear.getText()).intValue() - 1900, 
					new Integer(patientBirthMonth.getText()).intValue() - 1, 
					new Integer(patientBirthDay.getText()).intValue()
				);
			patient = 
				new Patient(
					patientName.getText(), 
					selectedPatient.getSex(), 
					birthDate, 
					patientAddress.getText()
				);
			patient.setId(selectedPatient.getId());
			ArrayList phoneNumbers = new ArrayList();
			if (patientFixPhone.getText().length() > 0) {
				phoneNumbers.add(patientFixPhone.getText());
			}
			if (patientMobilePhone.getText().length() > 0) {
				phoneNumbers.add(patientMobilePhone.getText());
			}
			patient.setPhoneNumbers(phoneNumbers);
		}
		return patient;
	}
	
	public void addPatient(Patient patient) {
		this.patients.add(patient);
		listModel.addElement(patient.getName());
		patientsList.setSelectedIndex(patients.size() - 1);
		clearFields();
	}
	
	public void updatePatient(Patient oldPatient, Patient newPatient) {
		int index = this.patients.indexOf(oldPatient);
		this.patients.remove(index);
		this.patients.add(index, newPatient);
		int indexList = listModel.indexOf(oldPatient.getName());
		listModel.remove(indexList);
		listModel.add(indexList, newPatient.getName());
	}
	
	public void removePatient(Patient patient) {
		int selectedIndex = patientsList.getSelectedIndex();
		this.patients.remove(patient);
		patientDetails.setText("");

		listModel.removeElement(patient.getName());
		patientsList.setSelectedIndex(selectedIndex);
		clearFields();
	}
	
	private void clearFields() {
		nameText.setText("");
		birthDay.setText("");
		birthYear.setText("");
		addressText.setText("");
		fixPhoneText.setText("");
		mobilePhoneText.setText("");
	}
	
	public static void main(String[] args) {
		ActionListener observer = new PatientEditor(Configurer.getConfiguration().getServerName());
		PatientEditorUI editor = new PatientEditorUI(observer);
		//editor.setObserver(observer);
		((PatientEditor)observer).setPatientUI(editor);
	}

}
