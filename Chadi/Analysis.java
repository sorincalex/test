/*
 * Created on Aug 19, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

import java.util.*;

/**
 * @author Sorin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Analysis {
	// the consultation date
	private Date date;
	// the consultation text
	private String text;
	// the consultation specialty
	private String specialty;
	// the Doctor who made the consultation
	private Doctor doctor;
	// the amount charged for the consultation
	private float amount;
	
	public Analysis(Date date, String text, String specialty, float amount) {
		this.date = date;
		this.text = text;
		this.specialty = specialty;
		this.amount = amount;
	}
	
	/**
	 * @return Returns the amount.
	 */
	public float getAmount() {
		return amount;
	}
	/**
	 * @param amount The amount to set.
	 */
	public void setAmount(float amount) {
		this.amount = amount;
	}
	/**
	 * @return Returns the date.
	 */
	public Date getDate() {
		return date;
	}
	/**
	 * @param date The date to set.
	 */
	public void setDate(Date date) {
		this.date = date;
	}
		
	public int getDay() {
		return date.getDate();
	}
	
	public int getMonth() {
		return (date.getMonth() + 1);
	}
	
	public int getYear() {
		return (date.getYear() + 1900);
	}

	/**
	 * @return Returns the doctor.
	 */
	public Doctor getDoctor() {
		return doctor;
	}
	/**
	 * @param doctor The doctor to set.
	 */
	public void setDoctor(Doctor doctor) {
		this.doctor = 
			new Doctor(doctor.getName(), doctor.getSex(), doctor.getBirthDate(), doctor.getAddress());
		this.doctor.setPhoneNumbers(doctor.getPhoneNumbers());
		this.doctor.setId(doctor.getId());
	}
	/**
	 * @return Returns the specialty.
	 */
	public String getSpecialty() {
		return specialty;
	}
	/**
	 * @param specialty The specialty to set.
	 */
	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}
	/**
	 * @return Returns the text.
	 */
	public String getText() {
		return text;
	}
	/**
	 * @param text The text to set.
	 */
	public void setText(String text) {
		this.text = text;
	}
	
	public String toString() {
		return "Consultation: [" + date + " " + text + " " + specialty + " " + 
			amount + " doctor: " + ((doctor != null)?doctor.getName():"null") + "]";
	}
}
