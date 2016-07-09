package ui;

import agenda.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.*;

public class Test extends JFrame {
	private GridBagLayout gridbag;
	private JPanel panelCalendar;
	private JScrollPane panel;
	
	// the minimum appointment interval in minutes
	private static final int INTERVAL = 10;
	// the start time of any appointment this day
	private static final int START_TIME = 8;
	// the end time of any appointment this day
	private static final int END_TIME = 21;
	
	private int currentDay = 1;
	private int currentMonth = 1;
	private int currentYear = 2007;

	private ArrayList doctors, appointments;
	
	private ActionListener observer;
	// TODO: create a mapping between doctors and their appointment text areas;
	// create a method that returns the AppointmentTextArea corresponding
	// to a certain doctor and a time slot; this will be need when adding/removing
	// an appointment in DayUI and updating this schedule on-the-fly
	private Hashtable appointmentsPerDoctor = new Hashtable();
	
	public Test(int d, int m, int y) {
		super("Programari " + d + "-" + m + "-" + y);
		currentDay = d;
		currentMonth = m;
		currentYear = y;
		
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
		panel = 
			new JScrollPane(
			    panelCalendar,
				JScrollPane.VERTICAL_SCROLLBAR_NEVER, 
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
			);
	}
	
	public void initialize() {
		
		try {
			
				GridBagConstraints c = new GridBagConstraints();
				// display doctor names in the first row
				for (int i = 0; i < 10; i++) {
					c.fill = GridBagConstraints.BOTH;
					Insets ins = new Insets (1, 1, 1, 1); 
					c.insets = ins;
					c.gridx = i;
					c.gridy = 0;
					c.gridwidth = 1;
					c.gridheight = 1;
					//c.fill = 1;
					c.weightx = 1;
					c.weighty = 0;
					c.anchor = GridBagConstraints.CENTER;
					JLabel doctorName = new JLabel(i + ": This is a test");
					doctorName.setPreferredSize(new Dimension(110, 20));
					gridbag.setConstraints(doctorName, c);
					panelCalendar.add(doctorName);
				}
					
				// display doctor names in the last row
				for (int i = 0; i < 10; i++) {
					c.fill = GridBagConstraints.BOTH;
					Insets ins = new Insets (1, 1, 1, 1); 
					c.insets = ins;
					c.gridx = i;
					c.gridy = 2;
					c.gridwidth = 1;
					c.gridheight = 1;
					//c.fill = 1;
					c.weightx = 1;
					c.weighty = 0;
					c.anchor = GridBagConstraints.CENTER;
					JLabel doctorName2 = new JLabel(i + ": This is another test");
					doctorName2.setPreferredSize(new Dimension(110, 20));
					gridbag.setConstraints(doctorName2, c);
					panelCalendar.add(doctorName2);
				}
				
					/*************************************/	
				// for all doctors
				// for all intervals between START_TIME and END_TIME	
				GridBagLayout gridbag2 = new GridBagLayout();
				JPanel panelAppointments = new JPanel();
				panelAppointments.setLayout(gridbag2);
				GridBagConstraints c2 = new GridBagConstraints();
				
				for (int i = 0; i < 10; i++) {
					for (int j = 0; j < 10; j++) {
						AppointmentTextArea text = new AppointmentTextArea(null);
						
							text.setText(j + ":" + i + ": Stuff in the middle");
							text.setBackground(Color.RED);
						
						c2.fill = GridBagConstraints.BOTH;
						Insets ins = new Insets (1, 1, 1, 1); 
						c2.insets = ins;
						c2.gridx = j;
						c2.gridy = i;
						c2.gridwidth = 1;
						c2.gridheight = 1;
						//c.fill = 1;
						c2.weightx = 1;
						c2.weighty = 0;
						c2.anchor = GridBagConstraints.CENTER;
						//text.setPreferredSize(new Dimension(0, 20));
						gridbag2.setConstraints(text, c2);
						panelAppointments.add(text);
						//panelCalendar.add(text);						
					}
				}

						c.fill = GridBagConstraints.BOTH;
						Insets ins = new Insets (1, 1, 1, 1); 
						c.insets = ins;
						c.gridx = 0;
						c.gridy = 1;
						c.gridwidth = 10;
						c.gridheight = 1;
						//c.fill = 1;
						c.weightx = 1;
						c.weighty = 0;
						c.anchor = GridBagConstraints.CENTER;
						JScrollPane panelAppointmentsScroll = 
							new JScrollPane(
								panelAppointments, 
								JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
								JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
							);
						gridbag.setConstraints(panelAppointmentsScroll, c);
						panelAppointmentsScroll.setPreferredSize(new Dimension(100, 40));
				        panelCalendar.add(panelAppointmentsScroll);
				        
				/*****************************************************/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		panel.setPreferredSize(new Dimension(500, 500));
		//panel.setSize(850, 600);
		//panelCalendar.setSize(850, 600);
		getContentPane().add(panel, BorderLayout.CENTER);
		pack();
		setVisible(true);
	}
	
	public void setObserver(ActionListener o) {
		observer = o;
	}
	
	public void setCurrentDate(int d, int m, int y) {
		currentDay = d;
		currentMonth = m;
		currentYear = y;
	}
	
	public Appointment getAppointment(String doctorName, int hour, int min) {
		for (int i = 0; i < appointments.size(); i++) {
			Appointment app = (Appointment)appointments.get(i);
			if (app.getDoctor().getName().equals(doctorName)) {
				int startHour = app.getTime().getHours();
				int startMin = app.getTime().getMinutes();
				int endHour = startHour + (startMin + app.getDuration()) / 60;
				int endMin = (startMin + app.getDuration()) % 60;
				Date startDate = new Date(currentDay, currentMonth, currentYear, startHour, startMin, 0);
				Date endDate = new Date(currentDay, currentMonth, currentYear, endHour, endMin, 0);
				Date crtDate = new Date(currentDay, currentMonth, currentYear, hour, min, 0);
				//if (
				//	((startHour < hour) && (hour < endHour)) ||
				//	((startHour < hour) && (hour == endHour) && (min < endMin)) ||
				//	((startHour == hour) && (hour == endHour) && (startMin <= min) && (min < endMin))
				//) 
				if (
					(
					 (startDate.compareTo(crtDate) == 0) || 
					  startDate.before(crtDate)
					 ) && 
					crtDate.before(endDate)
				)
					return app;
			}
		}
		return null;
	}
	
	private AppointmentTextArea getAppointmentForDoctorAndTime(Doctor d, int hour, int min) {
		Enumeration doctors = appointmentsPerDoctor.keys();
		while (doctors.hasMoreElements()) {
			Doctor doctor = (Doctor)doctors.nextElement();
			if (d.getId() == doctor.getId()) {
				ArrayList texts = (ArrayList)appointmentsPerDoctor.get(doctor);
				int intervals = ((hour - START_TIME) * 60 + min) / INTERVAL;
				if ((intervals >= 0) && (intervals < texts.size()))
					return (AppointmentTextArea)texts.get(intervals);
			}
		}
		return null;
	}
	
	public void addAppointment(Appointment a) {
		for (int i = 0; i < a.getDuration() / INTERVAL; i++) {
			int startHour = a.getTime().getHours();
			int startMin = a.getTime().getMinutes() + i * INTERVAL;
			
			AppointmentTextArea text = 
				getAppointmentForDoctorAndTime(a.getDoctor(), startHour, startMin);
			if (text != null) {
				text.setText(a.getPatient().getName());
				text.setBackground(Color.RED);
			}
		}
	}
	
	public void removeAppointment(Appointment a) {
		for (int i = 0; i < a.getDuration() / INTERVAL; i++) {
			int startHour = a.getTime().getHours();
			int startMin = a.getTime().getMinutes() + i * INTERVAL;
			
			AppointmentTextArea text = 
				getAppointmentForDoctorAndTime(a.getDoctor(), startHour, startMin);
			if (text != null) {
				text.setText("");
				text.setBackground(Color.WHITE);
			}
		}		
	}
	
	public static void main(String[] args) {
		Test ui = new Test(24, 3, 2007);
		//ui.setCurrentDate(24, 3, 2007);
		//AgendaEditor agenda = new AgendaEditor(null, Editor.serverName);
		//ui.setObserver(agenda);
		ui.initialize();
	}
}
