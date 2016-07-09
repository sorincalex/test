/*
 * Created on Oct 14, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package ui;

import agenda.Appointment;
import agenda.Editor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import javax.swing.JTextArea;

/**
 * @author Sorin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class AppointmentTextArea extends JTextArea {
	private Date date;
	private ArrayList appointments;
	private String text;
	
	public AppointmentTextArea(Date d) {
		super();
		if (d != null) {
			this.date = new Date(d.getYear(), d.getMonth(), d.getDate());
			append("" + d.getDate() + "\n\n");
			Date today = Calendar.getInstance().getTime();
			if (
				(d.getYear() == today.getYear()) && 
				(d.getMonth() == today.getMonth()) &&
				(d.getDate() == today.getDate()) 
			)
				this.setBackground(Color.YELLOW);
		} else {
			append("");
		}
		appointments = new ArrayList();
	}
	
	public Date getDate() {
		return new Date(date.getYear(), date.getMonth(), date.getDate());
	}
	
	public void setOwnText(String t) {
		text = t;
	}
	
	public String getOwnText() {
		return text;
	}
	
	public void addAppointment(Appointment a) {
		if (!appointments.contains(a)) {
			appointments.add(a);
			Editor.sortList(appointments);
			setText("");
			append("" + date.getDate() + "\n\n");
			for (int i = 0; i < appointments.size(); i++) {
				append(
					(
					 (((Appointment)appointments.get(i)).getTime().getHours() < 10) ?
					 		" "
					 		:
					 		""
					) +	
					((Appointment)appointments.get(i)).getTime().getHours() + ":" + 
					(
					(((Appointment)appointments.get(i)).getTime().getMinutes() < 10) ?
						"0"
						:
						""
					)
					+ 
					 ((Appointment)appointments.get(i)).getTime().getMinutes()
						
				);
				append("  " + ((Appointment)appointments.get(i)).getDoctor().getName() + "\n");
			}
			//append(a.getTime().getHours() + ":" + a.getTime().getMinutes());
			//append("  " + a.getDoctor().getName() + "\n");
		}
	}
	
	public void removeAppointment(Appointment a) {
		appointments.remove(a);
		setText("");
		append("" + date.getDate() + "\n\n");
		for (int i = 0; i < appointments.size(); i++) {
			append(
				((Appointment)appointments.get(i)).getTime().getHours() + ":" + 
				((Appointment)appointments.get(i)).getTime().getMinutes()
			);
			append("  " + ((Appointment)appointments.get(i)).getDoctor().getName() + "\n");
		}
	}
	
	public ArrayList getAppointments() {
		return (ArrayList)appointments.clone();
	}
}