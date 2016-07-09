/*
 * Created on Oct 14, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package ui;

import agenda.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.*;

import configuration.Configurer;

import agenda.AgendaEditor;

/**
 * @author Sorin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class AgendaUI extends JFrame implements ActionListener {
	
	private JTable table;
	private JScrollPane panel;
	private ActionListener observer;
	private int currentMonth;
	private int currentYear;
	private JPanel panelCalendar;
	private JPanel panelButtons;
	private GridBagLayout gridbag;
	private JLabel[] labels = 
		new JLabel[] {
			new JLabel("Duminica"),
			new JLabel("Luni"), 
			new JLabel("Marti"), 
			new JLabel("Miercuri"), 
			new JLabel("Joi"), 
			new JLabel("Vineri"), 
			new JLabel("Sambata")
		};
	private String[] months = 
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
	private JButton next = new JButton(">>");
	private JButton previous = new JButton("<<");
	private JComboBox monthsCombo;
	private JTextField yearField;
	private JButton go = new JButton("Direct");
	private ArrayList currentAppointments = new ArrayList();
	private ArrayList textAreas = new ArrayList();
	private DayUI ui;
	
	public AgendaUI() {
		super ("Agenda");
		
		/* TODO: I GET SOME UPDATE ERRORS WITH THE WINDOWS LOOK AND FEEL; CHECK THAT */
		String lf = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
		
		//String lf = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
		try {
			UIManager.setLookAndFeel(lf);
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception e) {
			e.printStackTrace();
		}	
		gridbag = new GridBagLayout();
		panelCalendar = new JPanel();
		panelCalendar.setLayout(gridbag);
		panel = new JScrollPane(panelCalendar);
		panelButtons = new JPanel();
		panelButtons.setLayout(new GridBagLayout());
		currentMonth = new Date().getMonth();
		currentYear = new Date().getYear();	
	}
	
	public void initialize() {
		setTitle(months[currentMonth] + " " + (currentYear + 1900));
		
		Date currentDate = new Date(currentYear, currentMonth, 1);
		int dayOfWeek = currentDate.getDay();
		
		ArrayList appointmentDays = new ArrayList();
		
		for (int i = 0; i < dayOfWeek; i++) {
			AppointmentTextArea text = new AppointmentTextArea(null);
			text.addMouseListener(
					new MouseAdapter() {
						public void mousePressed(MouseEvent e) {
							if (e.getClickCount() == 2) {
								//System.out.println("PRESSED: " + ((AppointmentTextArea)e.getSource()).getText() + ", " + ((AppointmentTextArea)e.getSource()).getOwnText());
							}
						}
					}
			);
			appointmentDays.add(text);
		}
		
		int numberOfDays = 
			(((currentMonth <= 6) && (currentMonth % 2 == 0)) ||
			((currentMonth > 6) && (currentMonth % 2 != 0))) ? 31 : 30;
		if (currentMonth == 1) {
			if (((currentYear + 1900) % 4 == 0) && ((currentYear + 1900) % 400 != 0)) {
				// leap year
				numberOfDays = 29;
			} else {
				numberOfDays = 28;
			}
		}
		
		// get all appointments in the current month from the database
		ArrayList p = new ArrayList();
		try {
			// call getAppointments2 in order to retieve the appointments from the 
			// table corresponding to currentYear + 1900
			currentAppointments = 
				((AgendaEditor)observer).getAppointments2(currentMonth + 1, currentYear + 1900, p);
			if (currentAppointments == null) currentAppointments = new ArrayList();
			else {
				Editor.sortList(currentAppointments);
				for (int i = 0; i < currentAppointments.size(); i++) {
					//System.out.println((Appointment)currentAppointments.get(i));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		long t1 = System.currentTimeMillis();
		for (int i = 0; i < numberOfDays; i++) {
			currentDate = new Date(currentYear, currentMonth, i + 1);
			AppointmentTextArea text = new AppointmentTextArea(currentDate);
			textAreas.add(text);
			// add the appointments for the current day
			if (currentAppointments != null) {
				for (int j = 0; j < currentAppointments.size(); j++) {
					Appointment a = (Appointment)currentAppointments.get(j);
					if (a.getDate().getDate() == i + 1) {
						Appointment app = new Appointment(a.getDate(), a.getTime(), a.getDoctor(), a.getPatient());
						app.setId(a.getId());
						app.setDuration(a.getDuration());
						app.setText(a.getText());
						text.addAppointment(app);
					}
				}
			}
			
			text.addMouseListener(
					new MouseAdapter() {
						public void mousePressed(MouseEvent e) {
							if (e.getClickCount() == 2) {
								//System.out.println("PRESSED: " + ((AppointmentTextArea)e.getSource()).getText() + ", " + ((AppointmentTextArea)e.getSource()).getOwnText() + ", " + ((AppointmentTextArea)e.getSource()).getDate());
								if (ui != null) ui.dispose();
								ui = new DayUI((AppointmentTextArea)e.getSource(), observer);
								((AgendaEditor)observer).setUI(ui);
							}
						}
					}
			);
			appointmentDays.add(text);		
		}
		if (appointmentDays.size() % 7 != 0) {
			int restDays = (appointmentDays.size() / 7 + 1) * 7 - appointmentDays.size();
			for (int i = 0; i < restDays; i++) {
				AppointmentTextArea text = new AppointmentTextArea(null);
				text.addMouseListener(
						new MouseAdapter() {
							public void mousePressed(MouseEvent e) {
								if (e.getClickCount() == 2) {
									//System.out.println("PRESSED: " + ((AppointmentTextArea)e.getSource()).getText() + ", " + ((AppointmentTextArea)e.getSource()).getOwnText());
								}
							}
						}
				);
				appointmentDays.add(text);				
			}
		}
		long t2 = System.currentTimeMillis();
		//System.out.println("AgendaUI, initialize(), adding the appointments took " + (t2 - t1));

		GridBagConstraints c = new GridBagConstraints();
		
		for (int j = 0; j < labels.length; j++) {
			c.fill = GridBagConstraints.BOTH;
			Insets ins = new Insets (1, 1, 1, 1); 
			c.insets = ins;
			c.gridx = j;
			c.gridy = 0;
			c.gridwidth = 1;
			c.gridheight = 1;
			//c.fill = 1;
			c.weightx = 1;
			c.weighty = 0;
			c.anchor = GridBagConstraints.CENTER;
			labels[j].setPreferredSize(new Dimension(110, 20));
			gridbag.setConstraints(labels[j], c);
			panelCalendar.add(labels[j]);
		}
		
		int nrLines = appointmentDays.size() / 7 + ((appointmentDays.size() % 7 == 0) ? 0 : 1);
		
		for (int i = 0; i < nrLines; i++) {
			for (int j = 0; j < labels.length; j++) {
				c.fill = GridBagConstraints.BOTH;
				Insets ins = new Insets (1, 1, 1, 1); 
				c.insets = ins;
				c.gridx = j;
				c.gridy = i + 1;
				c.gridwidth = 1;
				c.gridheight = 1;
				//c.fill = 1;
				c.weightx = 1;
				c.weighty = 1;
				c.anchor = GridBagConstraints.CENTER;
				//JTextArea text = new JTextArea();
				AppointmentTextArea text = 
					(AppointmentTextArea)appointmentDays.get(i * 7 + j);
				//text.setSize(120, 120);
				JScrollPane paneText = new JScrollPane(text);
				paneText.setPreferredSize(new Dimension(140, 120));
				gridbag.setConstraints(paneText, c);
				panelCalendar.add(paneText);
			}
		}
		panel.setPreferredSize(new Dimension(1050, 700));
		//panel.setSize(850, 600);
		//panelCalendar.setSize(850, 600);
		getContentPane().add(panel, BorderLayout.CENTER);
		
		
		c.fill = GridBagConstraints.BOTH;
		Insets ins = new Insets (5, 5, 5, 5);  
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		//c.fill = 1;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(previous, c);
		//c.fill = GridBagConstraints.BOTH;
		panelButtons.add(previous);
		
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		//c.fill = 1;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(next, c);
		panelButtons.add(next);
		
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 2;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		//c.fill = 1;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.CENTER;
		monthsCombo = new JComboBox(months);
		monthsCombo.setSelectedIndex(currentMonth);
		gridbag.setConstraints(monthsCombo, c);
		panelButtons.add(monthsCombo);
		
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 3;
		c.gridy = 0;
		c.ipadx = 15;
		c.gridwidth = 1;
		c.gridheight = 1;
		//c.fill = 1;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.CENTER;
		yearField = new JTextField();
		yearField.setText(new Integer(currentYear + 1900).toString());
		yearField.setSize(100, 20);
		yearField.setEditable(true);
		gridbag.setConstraints(yearField, c);
		panelButtons.add(yearField);
		
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.ipadx = 15;
		c.gridx = 5;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		//c.fill = 1;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(go, c);
		panelButtons.add(go);
		
		getContentPane().add(panelButtons, BorderLayout.SOUTH);
		pack();
		setVisible(true);
		//System.out.println("AgendaUI, initialize(), UI creation took " + (System.currentTimeMillis() - t2));
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == next) {
			currentMonth++;
			if (currentMonth >= 12) {
				currentMonth = currentMonth % 12;
				currentYear++;
			}
		} else if (e.getSource() == previous) {
			currentMonth--;
			if (currentMonth < 0) {
				currentMonth = 12 + currentMonth;
				currentYear--;
			}
		} else if (e.getSource() == go) {
			currentMonth = monthsCombo.getSelectedIndex();
			currentYear = new Integer(yearField.getText()).intValue() - 1900;
		}
		dispose();
		AgendaUI ui = new AgendaUI();
		ui.setCurrentDate(currentMonth, currentYear);
		ActionListener agenda = new AgendaEditor(ui, Configurer.getConfiguration().getServerName());
		ui.setObserver(agenda);
		ui.initialize();
	}
	
	public void addAppointment(Appointment appointment) {
		currentAppointments.add(appointment);
		for (int i = 0; i < textAreas.size(); i++) {
			AppointmentTextArea textArea = (AppointmentTextArea)textAreas.get(i);
			if (
				(textArea.getDate().getDate() == appointment.getDate().getDate()) &&
				(textArea.getDate().getMonth() == appointment.getDate().getMonth()) &&
				(textArea.getDate().getYear() == appointment.getDate().getYear())
			) {
				textArea.addAppointment(appointment);
			}
		}
	}
	
	public void removeAppointment(Appointment appointment) {
		//System.out.println("AgendaUI, removeAppointment: " + appointment);
		currentAppointments.remove(appointment);
		for (int i = 0; i < textAreas.size(); i++) {
			AppointmentTextArea textArea = (AppointmentTextArea)textAreas.get(i);
			if (
				(textArea.getDate().getDate() == appointment.getDate().getDate()) &&
				(textArea.getDate().getMonth() == appointment.getDate().getMonth()) &&
				(textArea.getDate().getYear() == appointment.getDate().getYear())
			) {
				textArea.removeAppointment(appointment);
			}
		}
	}
	
	/**
	 * 
	 * @uml.property name="observer"
	 */
	public void setObserver(ActionListener obs) {
		observer = obs;
		next.addActionListener(this);
		previous.addActionListener(this);
		go.addActionListener(this);
	}
	
	public void setCurrentDate(int month, int year) {
		currentMonth = month;
		currentYear = year;
	}
	
	public static void main(String[] args) {
		AgendaUI ui = new AgendaUI();
		AgendaEditor agenda = new AgendaEditor(ui, Configurer.getConfiguration().getServerName());
		ui.setObserver(agenda);
		ui.initialize();
	}
	
}
