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
public class Doctor {
	// the unique ID of a doctor
	private int id;
	// the doctor's name
	private String name;
	// the doctor's sex ('m' or 'f')
	private char sex;
	// the doctor's address
	private String address;
	// the doctor's phone numbers
	private ArrayList phoneNumbers = new ArrayList();
	// the doctor's date of birth
	private Date birthDate;
	// the doctor's cabinet
	private String cabinet;
	
	public Doctor(
			String name, 
			char sex, 
			Date birthDate,
			String address
	) {
		this.name = name;
		this.sex = sex;
		this.birthDate = birthDate;
		this.address = address;
		//try {
		//	id = PatientEditor.getSequenceID("doctors");
		//	if (id == -1) id = generateID();
		//} catch (Exception e) {
		//	e.printStackTrace();
			id = generateID();			
		//}
		System.out.println("Doctor " + name + ", id = " + id);
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
	/**
	 * @return Returns the address.
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * @param address The address to set.
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	/**
	 * @return Returns the birthDate.
	 */
	public Date getBirthDate() {
		return birthDate;
	}
	
	public int getBirthDay() {
		return birthDate.getDate();
	}
	
	public int getBirthMonth() {
		return (birthDate.getMonth() + 1);
	}
	
	public int getBirthYear() {
		return (birthDate.getYear() + 1900);
	}
	
	/**
	 * @param birthDate The birthDate to set.
	 */
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}
	/**
	 * @return Returns the phoneNumbers.
	 */
	public ArrayList getPhoneNumbers() {
		return phoneNumbers;
	}
	/**
	 * @param phoneNumbers The phoneNumbers to set.
	 */
	public void setPhoneNumbers(ArrayList phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}
	/**
	 * @param phone The phone number to add.
	 */
	public void addPhoneNumber(String phone) {
		phoneNumbers.add(phone);
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return Returns the sex.
	 */
	public char getSex() {
		return sex;
	}
	/**
	 * @param sex The sex to set.
	 */
	public void setSex(char sex) {
		this.sex = sex;
	}

	public String toString() {
		StringBuffer patientString = 
			new StringBuffer("[" + id + " " + name + " " + birthDate + " " + address);
		for (int i = 0; i < phoneNumbers.size(); i++) {
			patientString.append(" ");
			patientString.append((String)phoneNumbers.get(i));
		}
		patientString.append("]");
		return patientString.toString();
	}
	
	public boolean equals(Object o) {
		if (o == null) return false;
		if (!(o instanceof Doctor)) return false;
		Doctor newDoctor = (Doctor)o;
		return (newDoctor.getId() == getId());
	}
	
	/**
	 * @return Returns the cabinet.
	 */
	public String getCabinet() {
		return cabinet;
	}
	/**
	 * @param cabinet The cabinet to set.
	 */
	public void setCabinet(String cabinet) {
		this.cabinet = cabinet;
	}
}
