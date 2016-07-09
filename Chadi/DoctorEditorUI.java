/*
 * Created on Aug 24, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import configuration.Configurer;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * @author Sorin
 *
 * Show doctors and add new doctors.
 * 
 */
public class DoctorEditorUI extends JFrame {
	private JLabel addLabel;
	private JLabel nameLabel, nameLabel2;
	private JTextField nameText, doctorName;
	private JLabel sexLabel;
	private JRadioButton maleButton, femaleButton;
	private JLabel birthLabel;
	private JTextField birthDay, doctorBirthDay, doctorBirthMonth, doctorSex;
	private JComboBox birthMonth;
	private JTextField birthYear, doctorBirthYear;
	private JLabel addressLabel;
	private JTextField addressText, doctorAddress;	
	private JLabel fixPhoneLabel;
	private JTextField fixPhoneText, doctorFixPhone;
	private JLabel mobilePhoneLabel;
	private JTextField mobilePhoneText, doctorMobilePhone;
	private JLabel cabinetLabel;
	private JTextField cabinetText, cabinetText2;
	private JLabel emptyLabel;
	private JButton addButton;
	private final JTextArea doctorDetails;
	private JButton displayAnalysesButton;
	private JButton displayConsultationsButton;
	private JButton editDoctorButton;
	private JButton removeDoctorButton;
	private JButton showAllDoctorsButton;
	private ArrayList doctors = new ArrayList();
	private JList doctorsList = new JList();
	private DefaultListModel listModel;
	private ActionListener observer;
	
	public DoctorEditorUI(ActionListener observer) {
		super ("Adauga doctor nou/ afiseaza toti doctorii");
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
		Insets ins = new Insets (10, 5, 0, 0); 
		c.insets = ins;
		c.gridx = 0; 
		c.gridy = 0;
		c.gridwidth = 3;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		JLabel doctorsLabel = new JLabel("Doctori: ");
		//showAllDoctorsButton = new JButton("Afiseaza toti doctorii");
		gridbag.setConstraints(doctorsLabel, c);
		panel.add(doctorsLabel);

		c.fill = GridBagConstraints.NONE;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0; 
		c.gridy = 1;
		c.gridwidth = 4;
		c.gridheight = 2;
		c.anchor = GridBagConstraints.CENTER;
		emptyLabel = new JLabel();
		gridbag.setConstraints(emptyLabel, c);
		panel.add(emptyLabel);
		
////////////////////////////////////////////////////////////////////
		// a list containing all the names of the doctors passed as parameter
		listModel = new DefaultListModel();
		// TODO: replace this hack by something more reusable (i.e., this class
		// should'n know about DoctorEditor)
		try {
			doctors = ((PatientEditor)observer).getDoctors();
			for (int i = 0; i < doctors.size(); i++) {
				listModel.addElement(((Doctor)doctors.get(i)).getName());
			}
			doctorsList = new JList(listModel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// the details about the selected doctor will be displayed in doctorDetails
		doctorDetails = new JTextArea();
		doctorDetails.setColumns(50);
		doctorDetails.setRows(10);
		doctorDetails.setEditable(false);
		
		doctorName = new JTextField();
		doctorAddress = new JTextField();
		doctorBirthDay = new JTextField();
		doctorBirthMonth = new JTextField();
		doctorBirthYear = new JTextField();
		doctorFixPhone = new JTextField();
		doctorMobilePhone = new JTextField();
		doctorSex = new JTextField();
		cabinetText = new JTextField();
		
		// TODO: don't mix UI code with processing logic
		doctorsList.addListSelectionListener(
			new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					int selectedIndex = doctorsList.getSelectedIndex();
					if ((selectedIndex >= 0) && (selectedIndex < doctors.size())) {
						Doctor doctor = (Doctor)(doctors.get(selectedIndex));
						//String doctorName = (String)(list.getSelectedValue());
						//doctorDetails.setText(doctorName);
						StringBuffer toDisplay = 
							new StringBuffer(
								doctor.getName() + ", " + doctor.getSex() + "\n " +
								"Data nasterii: " + doctor.getBirthDay() + "-" +
								doctor.getBirthMonth() + "-" + doctor.getBirthYear() + "\n " +
								"Adresa: " + doctor.getAddress() + "\n " +
								"Telefon: "
							);
						for (int i = 0; i < doctor.getPhoneNumbers().size(); i++) {
							toDisplay.append((String)(doctor.getPhoneNumbers().get(i)));
							toDisplay.append(", ");
						}
						toDisplay.append("\n ");
						doctorDetails.setText(toDisplay.toString());
						doctorName.setText("");
						doctorSex.setText("");
						doctorBirthDay.setText("");
						doctorBirthMonth.setText("");
						doctorBirthYear.setText("");
						doctorAddress.setText("");
						doctorFixPhone.setText("");
						doctorMobilePhone.setText("");
						
						doctorName.setText(doctor.getName());
						doctorSex.setText(doctor.getSex() + "");
						doctorBirthDay.setText(new Integer(doctor.getBirthDay()).toString());
						doctorBirthMonth.setText(new Integer(doctor.getBirthMonth()).toString());
						doctorBirthYear.setText(new Integer(doctor.getBirthYear()).toString());
						doctorAddress.setText(doctor.getAddress());
						if (doctor.getPhoneNumbers().size() > 0)
							doctorFixPhone.setText((String)(doctor.getPhoneNumbers().get(0)));
						if (doctor.getPhoneNumbers().size() > 1)
							doctorMobilePhone.setText((String)(doctor.getPhoneNumbers().get(1)));
						cabinetText.setText(doctor.getCabinet());
					}
				}
			}
		);
		doctorsList.setSelectedIndex(0);
		JScrollPane listScrollPane = new JScrollPane(doctorsList);
		
		// fill its display area horizontally
		c.fill = GridBagConstraints.BOTH; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 3;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridwidth = 3; // make it 3 columns wide
		c.gridheight = 9;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(listScrollPane, c);
		panel.add(listScrollPane);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 3;
		c.gridy = 3;
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
		c.gridy = 3;
		c.gridwidth = 3; // make it 3 columns wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(doctorName, c);
		panel.add(doctorName);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 3;
		c.gridy = 4;
		c.gridwidth = 1; 
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		JLabel birthLabel2 = new JLabel("Data nasterii: ");
		gridbag.setConstraints(birthLabel2, c);
		panel.add(birthLabel2);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 4;
		c.gridy = 4;
		c.gridwidth = 1; 
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(doctorBirthDay, c);
		panel.add(doctorBirthDay);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 5;
		c.gridy = 4;
		c.gridwidth = 1; 
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		doctorBirthMonth.setColumns(2);
		gridbag.setConstraints(doctorBirthMonth, c);
		panel.add(doctorBirthMonth);

		c.fill = GridBagConstraints.HORIZONTAL;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 6;
		c.gridy = 4;
		c.gridwidth = 1; 
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(doctorBirthYear, c);
		panel.add(doctorBirthYear);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 3;
		c.gridy = 5;
		c.gridwidth = 1; // make it 3 columns wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		JLabel addressLabel2 = new JLabel("Adresa: ");
		gridbag.setConstraints(addressLabel2, c);
		panel.add(addressLabel2);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 4;
		c.gridy = 5;
		c.gridwidth = 3; // make it 3 columns wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(doctorAddress, c);
		panel.add(doctorAddress);

		c.fill = GridBagConstraints.HORIZONTAL;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 3;
		c.gridy = 6;
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
		c.gridy = 6;
		c.gridwidth = 1; 
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		doctorFixPhone.setColumns(10);
		gridbag.setConstraints(doctorFixPhone, c);
		panel.add(doctorFixPhone);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 5;
		c.gridy = 6;
		c.gridwidth = 2; 
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		doctorMobilePhone.setColumns(10);
		gridbag.setConstraints(doctorMobilePhone, c);
		panel.add(doctorMobilePhone);
					
		c.fill = GridBagConstraints.HORIZONTAL;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 3;
		c.gridy = 7;
		c.gridwidth = 1; 
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		cabinetLabel = new JLabel("Cabinet: ");
		gridbag.setConstraints(cabinetLabel, c);
		panel.add(cabinetLabel);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 4;
		c.gridy = 7;
		c.gridwidth = 3; 
		c.gridheight = 1;
		c.anchor = GridBagConstraints.CENTER;
		cabinetText.setColumns(10);
		gridbag.setConstraints(cabinetText, c);
		panel.add(cabinetText);
	
		
		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 3;
		c.gridy = 8;
		c.gridwidth = 4; // make it 4 columns wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.EAST;
		editDoctorButton = new JButton("Modifica doctor");
		gridbag.setConstraints(editDoctorButton, c);
		panel.add(editDoctorButton);
	/////////////////////////////////////////////////////////////////////
		
		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 3;
		c.gridy = 9;
		c.gridwidth = 4; // make it 3 columns wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.EAST;
		removeDoctorButton = new JButton("Sterge doctor");
		gridbag.setConstraints(removeDoctorButton, c);
		panel.add(removeDoctorButton);
		
		c.fill = GridBagConstraints.NONE;
		ins = new Insets (10, 0, 0, 0); 
		c.insets = ins;
		// the 5th position, since the previous 2 items take up 4 positions
		c.gridx = 0; 
		c.gridy = 12;
		c.gridwidth = 4;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		addLabel = new JLabel("Adauga doctor:");
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
		String[] months = {"Ian", "Feb", "Mar", "Apr", "Mai", "Iun", "Iul", "Aug", "Sep", "Oct", "Noi", "Dec"};
		birthMonth = new JComboBox(months);
		birthMonth.setSelectedIndex(0);
		gridbag.setConstraints(birthMonth, c);
		panel.add(birthMonth);
		
		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 3;
		c.gridy = 15;
		c.gridwidth = 1; 
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

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 19;
		c.gridwidth = 1; // make it 1 column wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		JLabel cabinetLabel2 = new JLabel("Cabinet: ");
		gridbag.setConstraints(cabinetLabel2, c);
		panel.add(cabinetLabel2);

		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 1;
		c.gridy = 19;
		c.gridwidth = 1; // make it 3 columns wide
		c.gridheight = 1;
		c.weightx = 1;
		c.anchor = GridBagConstraints.CENTER;
		cabinetText2 = new JTextField();
		gridbag.setConstraints(cabinetText2, c);
		panel.add(cabinetText2);
		
		c.fill = GridBagConstraints.NONE;
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0; 
		c.gridy = 20;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.EAST;
		addButton = new JButton("Adauga");
		gridbag.setConstraints(addButton, c);
		panel.add(addButton);
		
		//showAllDoctorsButton.addActionListener(observer);
		addButton.addActionListener(observer);
		removeDoctorButton.addActionListener(observer);
		editDoctorButton.addActionListener(observer);
		Dimension d = new Dimension();
		d.setSize(600, 550);
		panel.setPreferredSize(d);
		getContentPane().add(panel, BorderLayout.CENTER);
		pack();
		setVisible(true);
	}
	
	/**
	 * 
	 * @uml.property name="observer"
	 */
	public void setObserver(ActionListener observer) {
		this.observer = observer;
	}
	
	/**
	 * @return Returns the showAllDoctorsButton.
	 */
	public JButton getShowAllDoctorsButton() {
		return showAllDoctorsButton;
	}
	
	/**
	 * @return Returns the addButton.
	 */
	public JButton getAddButton() {
		return addButton;
	}
		
	/**
	 * @return Returns the removedoctorButton.
	 */
	public JButton getRemoveButton() {
		return removeDoctorButton;
	}
	
	/**
	 * @return Returns the editdoctorButton.
	 */
	public JButton getEditDoctorButton() {
		return editDoctorButton;
	}
	
	public Doctor getSelectedDoctor() {
		int selectedIndex = doctorsList.getSelectedIndex();
		if ((selectedIndex >= 0) && (selectedIndex < doctors.size())) {
			Doctor doctor = (Doctor)(doctors.get(selectedIndex));
			return doctor;
		}
		return null;
	}
	
	public void removeDoctor(Doctor doctor) {
		int selectedIndex = doctorsList.getSelectedIndex();
		this.doctors.remove(doctor);

		listModel.removeElement(doctor.getName());
		doctorsList.setSelectedIndex(selectedIndex);
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
	
	public void setDoctors(ArrayList doctors) {
		this.doctors.clear();
		listModel.clear();
		for (int i = 0; i < doctors.size(); i++) {
			this.doctors.add((Doctor)doctors.get(i));
			listModel.addElement(((Doctor)doctors.get(i)).getName());
		}
		doctorDetails.setText("");
		doctorsList.setSelectedIndex(0);
	}
	
	private Date getNewDoctorBirthDate() {
		int selectedMonth = birthMonth.getSelectedIndex();
		return new Date(
			new Integer(birthYear.getText()).intValue() - 1900, 
			selectedMonth, 
			new Integer(birthDay.getText()).intValue()
		);
	}
	
	public Doctor getNewDoctor() {
		if (
			(nameText.getText() == null) ||
			(nameText.getText().length() <= 1)
		) return null;
		
		Doctor doctor = 
			new Doctor(
					nameText.getText(),
					maleButton.isSelected() ? 'm' : 'f',
					getNewDoctorBirthDate(),
					addressText.getText()
			);
		String fixPhone = fixPhoneText.getText();
		String mobilePhone = mobilePhoneText.getText();

		if ((fixPhone != null) && (fixPhone.length() > 0))
			doctor.addPhoneNumber(fixPhone);
		
		if ((mobilePhone != null) && (mobilePhone.length() > 0))
			doctor.addPhoneNumber(mobilePhone);
		String cabinet = cabinetText2.getText();
		if ((cabinet != null) && (cabinet.length() > 0)) 
			doctor.setCabinet(cabinet);
		return doctor;
	}
	
	public void addDoctor(Doctor doctor) {
		this.doctors.add(doctor);
		listModel.addElement(doctor.getName());
		doctorsList.setSelectedIndex(doctors.size() - 1);
		nameText.setText("");
		birthDay.setText("");
		birthYear.setText("");
		addressText.setText("");
		fixPhoneText.setText("");
		mobilePhoneText.setText("");
		cabinetText2.setText("");
	}
	
	public Doctor getUpdatedDoctor() {
		Doctor doctor = null;
		Doctor selecteddoctor = getSelectedDoctor();
		if (selecteddoctor != null) {
			Date birthDate = new Date(
					new Integer(doctorBirthYear.getText()).intValue() - 1900, 
					new Integer(doctorBirthMonth.getText()).intValue() - 1, 
					new Integer(doctorBirthDay.getText()).intValue()
				);
			doctor = 
				new Doctor(
					doctorName.getText(), 
					selecteddoctor.getSex(), 
					birthDate, 
					doctorAddress.getText()
				);
			doctor.setId(selecteddoctor.getId());
			ArrayList phoneNumbers = new ArrayList();
			if (doctorFixPhone.getText().length() > 0) {
				phoneNumbers.add(doctorFixPhone.getText());
			}
			if (doctorMobilePhone.getText().length() > 0) {
				phoneNumbers.add(doctorMobilePhone.getText());
			}
			doctor.setPhoneNumbers(phoneNumbers);
			if (cabinetText.getText().length() > 0)
				doctor.setCabinet(cabinetText.getText());
		}
		return doctor;
	}
	
	public void updateDoctor(Doctor oldDoctor, Doctor newDoctor) {
		int index = this.doctors.indexOf(oldDoctor);
		this.doctors.remove(index);
		this.doctors.add(index, newDoctor);
		int indexList = listModel.indexOf(oldDoctor.getName());
		listModel.remove(indexList);
		listModel.add(indexList, newDoctor.getName());
	}
	
	public static void main(String[] args) {
		ActionListener observer = new PatientEditor(Configurer.getConfiguration().getServerName());
		DoctorEditorUI editor = new DoctorEditorUI(observer);
		//editor.setObserver(observer);
		((PatientEditor)observer).setDoctorUI(editor);
	}

}
