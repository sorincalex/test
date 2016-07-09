/*
 * Created on Oct 15, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package agenda;

import java.sql.Time;
import java.util.Date;

/**
 * @author Sorin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Appointment {
	private int id;
	private Date date;
	private Time time;
	private int duration = 60; // in minutes
	private Doctor doctor;
	private Patient patient;
	private String text;
	
	public Appointment(Date d, Time t, Doctor doc, Patient p) {
		date = new Date(d.getYear(), d.getMonth(), d.getDate());
		time = new Time(t.getHours(), t.getMinutes(), t.getSeconds());
		doctor = new Doctor(doc.getName(), doc.getSex(), doc.getBirthDate(), doc.getAddress());
		doctor.setId(doc.getId());
		
		patient = new Patient(p.getName(), p.getSex(), p.getBirthDate(), p.getAddress());
		patient.setId(p.getId());
		for (int i = 0; i < p.getPhoneNumbers().size(); i++) {
			patient.addPhoneNumber((String)p.getPhoneNumbers().get(i));
		}

		id = generateID();
	}
	
	/**
	 * 
	 */
	private int generateID() {
		// generate a unique id (10 digits)
		long crtTime = System.currentTimeMillis();
		long rest = 0;
		int x = 0;
		for (int i = 0; i < 10; i++) {
			rest = crtTime % 10;
			crtTime = crtTime / 10;
			x = x * 10 + (int)rest;
		}
		return x;
	}
	
	public void setText(String t) {
		text = t;
	}
	
	public String getText() {
		return text;
	}
	
	public void setDuration(int d) {
		duration = d;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public Date getDate() {
		return new Date(date.getYear(), date.getMonth(), date.getDate());
	}
	
	public Time getTime() {
		return new Time(time.getHours(), time.getMinutes(), time.getSeconds());
	}
	
	public Doctor getDoctor() {
		Doctor doc = 
			new Doctor(doctor.getName(), doctor.getSex(), doctor.getBirthDate(), doctor.getAddress());
		doc.setId(doctor.getId());
		return doc;
	}

	public Patient getPatient() {
		Patient p = 
			new Patient(patient.getName(), patient.getSex(), patient.getBirthDate(), patient.getAddress());
		p.setId(patient.getId());
		for (int i = 0; i < patient.getPhoneNumbers().size(); i++) {
			p.addPhoneNumber((String)patient.getPhoneNumbers().get(i));
		}
		return p;
	}

	public String toString() {
		return ((time.getHours() < 10) ? " " : "") + 
			time.getHours() + ":" + ((time.getMinutes() < 10) ? "0" : "") + time.getMinutes() + 
			"  Dr. " + doctor.getName() + ", pacient " + patient.getName() + " " + 
			((getText() != null) ? (", " + getText()) : "");
	}
	
	/**
	 * @return Returns the id.
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @param id The id to set.
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	public void setPatientId(int id) {
		patient.setId(id);
	}
}
