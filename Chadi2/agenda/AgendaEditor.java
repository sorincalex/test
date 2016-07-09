/*
 * Created on Oct 14, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package agenda;

import ui.*;
import configuration.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import javax.swing.JOptionPane;

/**
 * @author Sorin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AgendaEditor implements ActionListener {

	private AgendaUI agendaUI;
	private DayUI dayUI;
	private StringBuffer dbname;
	private String serverName;

	private static String tableAppointments = "appointments";
	private static String tableAppointmentsDuration = "appointments_duration";
	private static String tablePatients = "patients";
	private static String tablePatientPhones = "patient_phones";
	private static String tableDoctors = "doctors";
	private static String tableDoctorPhones = "doctor_phones";
	private static String tableDoctorCabinet = "doctor_cabinet";
	private static String tableSequences = "sequences";
	
	private PreparedStatement selectPatients, selectDoctors, consultationsForPatient, analysesForPatient,
			insertPatient, insertPatientPhone, appointmentsPerDay, appointmentsPerMonth, 
			appointmentsPerMonth2, insertAppointment, insertAppointmentDuration,
			patientsForName, insertDoctor, insertDoctorPhone, insertConsultation,
			insertAnalysis, removeConsultation, removeAnalysis, removeAppointment,
			removeAppointmentDuration, selectDoctorsCabinets;
	private static PreparedStatement selectSequenceID, updateSequenceID;
	
	public AgendaEditor(AgendaUI agenda, String serverName) {
		agendaUI = agenda;
		
		dbname = new StringBuffer(Configurer.getConfiguration().getDbname());
		dbname.insert(0, "jdbc:firebirdsql:" + serverName + "/3050:");
		//System.out.println("AgendaEditor: dbname = " + dbname);
		this.serverName = serverName;

		Connection conn;
		try {
			Class.forName("org.firebirdsql.jdbc.FBDriver");
			conn = DriverManager.getConnection(dbname.toString(), "sysdba", "masterkey");
		
			String sql = "SELECT * FROM " + tablePatients + " LEFT JOIN " + tablePatientPhones +
			" ON " + tablePatients + ".patient_id=" + tablePatientPhones + ".patient_id" +
			" ORDER BY " + tablePatients + ".patient_id ASC;";
			selectPatients = conn.prepareStatement(sql);
			
			sql = "SELECT * FROM " + tableDoctors + " LEFT JOIN " + tableDoctorPhones +
			" ON " + tableDoctors + ".doctor_id=" + tableDoctorPhones + ".doctor_id" +
			" ORDER BY " + tableDoctors + ".doctor_id ASC;";
			selectDoctors = conn.prepareStatement(sql);
			
			sql = "SELECT * FROM " + tableDoctors + " LEFT JOIN " + tableDoctorPhones +
			" ON " + tableDoctors + ".doctor_id=" + tableDoctorPhones + ".doctor_id" +
			" LEFT JOIN " + tableDoctorCabinet + " ON " + tableDoctors + ".doctor_id=" +
			tableDoctorCabinet + ".doctor_id" + 
			" ORDER BY " + tableDoctors + ".doctor_id ASC;";
			selectDoctorsCabinets = conn.prepareStatement(sql);
			
			sql = "INSERT INTO " + tablePatients + " VALUES (?,?,?,?,?,?,?);";
			insertPatient = conn.prepareStatement(sql);
			
			sql = "INSERT INTO " + tablePatientPhones + " VALUES (?,?);";
			insertPatientPhone = conn.prepareStatement(sql);
			
//			sql = "SELECT * FROM " + tableDoctors + " LEFT JOIN " + tableAppointments +
//			" ON " + tableDoctors + ".doctor_id=" + tableAppointments + ".doctor_id" +
//			" LEFT JOIN " + tablePatients + " ON " + tableAppointments + ".patient_id=" + 
//			tablePatients + ".patient_id WHERE (" + tableAppointments + ".date_month=? AND " +
//			tableAppointments + ".date_year=?) ORDER BY " + tableAppointments + ".date_day ASC;";
//			appointmentsPerMonth = conn.prepareStatement(sql);

//			sql = "SELECT * FROM " + tableDoctors + " LEFT JOIN " + tableAppointments +
//			" ON " + tableDoctors + ".doctor_id=" + tableAppointments + ".doctor_id" +
//			" JOIN " + tablePatients + " ON " + tableAppointments + ".patient_id=" + 
//			tablePatients + ".patient_id" + " LEFT JOIN " + tableAppointmentsDuration + 
//			" ON " + tableAppointments + ".appointment_id=" + tableAppointmentsDuration +
//			".appointment_id" +
//			" WHERE (" + tableAppointments + ".date_month=? AND " +
//			tableAppointments + ".date_year=?) ORDER BY " + tableAppointments + ".date_day ASC;";
			
//			sql = "SELECT * FROM " + tableDoctors + " LEFT JOIN " + tableAppointments +
//			" ON " + tableDoctors + ".doctor_id=" + tableAppointments + ".doctor_id" +
//			" JOIN " + tablePatients + " ON " + tableAppointments + ".patient_id=" + 
//			tablePatients + ".patient_id" + 
//			" WHERE (" + tableAppointments + ".date_month=? AND " +
//			tableAppointments + ".date_year=?) ORDER BY " + tableAppointments + ".date_day ASC;";
//			appointmentsPerMonth2 = conn.prepareStatement(sql);

			
//			sql = "SELECT * FROM " + tableDoctors + " LEFT JOIN " + tableAppointments +
//			" ON " + tableDoctors + ".doctor_id=" + tableAppointments + ".doctor_id" +
//			" JOIN " + tablePatients + " ON " + tableAppointments + ".patient_id=" + 
//			tablePatients + ".patient_id" + " LEFT JOIN " + tableAppointmentsDuration + 
//			" ON " + tableAppointments + ".appointment_id=" + tableAppointmentsDuration +
//			".appointment_id" +
//			" WHERE (" + tableAppointments + ".date_day=? AND " + tableAppointments + ".date_month=? AND " +
//			tableAppointments + ".date_year=?) ORDER BY " + tableAppointments + ".date_day ASC;";
			
//			sql = "SELECT * FROM " + tableDoctors + " LEFT JOIN " + tableAppointments +
//			" ON " + tableDoctors + ".doctor_id=" + tableAppointments + ".doctor_id" +
//			" JOIN " + tablePatients + " ON " + tableAppointments + ".patient_id=" + 
//			tablePatients + ".patient_id" + 
//			" WHERE (" + tableAppointments + ".date_day=? AND " + tableAppointments + ".date_month=? AND " +
//			tableAppointments + ".date_year=?) ORDER BY " + tableAppointments + ".date_day ASC;";
//			appointmentsPerDay = conn.prepareStatement(sql);

//			sql = "SELECT * FROM " + tablePatients + " LEFT JOIN " + tablePatientPhones +
//			" ON " + tablePatients + ".patient_id=" + tablePatientPhones + ".patient_id" +
//			" WHERE Name LIKE ? ORDER BY " + tablePatients + ".patient_id ASC;";
			
			sql = "INSERT INTO " + tableDoctors + " VALUES (?,?,?,?,?,?,?);";
			insertDoctor = conn.prepareStatement(sql);
			
			sql = "INSERT INTO " + tableDoctorPhones + " VALUES (?,?);";
			insertDoctorPhone = conn.prepareStatement(sql);

//			sql = "INSERT INTO " + tableAppointments + " VALUES (?,?,?,?,?,?,?,?,?,?);";
//			insertAppointment = conn.prepareStatement(sql);

			//sql = "INSERT INTO " + tableAppointmentsDuration + " VALUES (?,?);";
			//insertAppointmentDuration = conn.prepareStatement(sql);

//			sql = "DELETE FROM " + tableAppointments + " WHERE appointment_id=?;";
//			removeAppointment = conn.prepareStatement(sql);

//			sql = "DELETE FROM " + tableAppointmentsDuration + " WHERE appointment_id=?;";
//			removeAppointmentDuration = conn.prepareStatement(sql);
		
			sql = "SELECT * FROM " + tablePatients + " LEFT JOIN " + tablePatientPhones +
			" ON " + tablePatients + ".patient_id=" + tablePatientPhones + ".patient_id" +
			" WHERE UPPER(Name) LIKE '%?%' ORDER BY " + tablePatients + ".patient_id ASC;";
			patientsForName = conn.prepareStatement(sql);
			
			sql = "SELECT sequence_id FROM " + tableSequences + " WHERE table_name=?;";
			selectSequenceID = conn.prepareStatement(sql);
			
			sql = "UPDATE " +  tableSequences + " SET sequence_id=? WHERE table_name=?;";
			updateSequenceID = conn.prepareStatement(sql);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setUI(DayUI ui) {
		if (dayUI != null) dayUI.dispose();
		dayUI = ui;
	}
	
	// called when an item of a row in the table is changed by the user;
	// it will perform an UPDATE to the table in the DB	
	public void actionPerformed(ActionEvent e) {
		if (dayUI != null) {
			if (e.getSource() == dayUI.getSearchButton()) {
				// search patient by name
				String patientName = dayUI.getPatientName();
				try {
					ArrayList patients = getPatientsForName(patientName);
					dayUI.setPatients(patients);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else if (e.getSource() == dayUI.getAddButton()) {
				try {
					Appointment appointment = dayUI.getCurrentAppointment();
					if (appointment != null) {
						if (dayUI.isNewPatient()) {
							Patient p = appointment.getPatient();
							// generate a unique ID for the patient,
							// in order to use it as primary key in DB
							int id = -1;
							try {
								id = getSequenceID("patients");
								p.setId(id);
								// update also the appointment, by setting the patient's ID
								appointment.setPatientId(id);
							} catch (Exception ex) {
								id = -1;
							}
							// add the patient to the DB
							addPatient(p);
							dayUI.addPatient(p);
						}
						
						// generate a unique ID for the appointment,
						// in order to use it as primary key in DB
						int id = -1;
						try {
							id = getSequenceID("appointments");
							appointment.setId(id);
							if (id >= Integer.MAX_VALUE - 1) throw new Exception("out of bounds, starting from 0");
						} catch (Exception ex) {
							ex.printStackTrace();
							JOptionPane.showMessageDialog(
								dayUI, 
								"Atentie: ati atins maximul admis de programari, este posibil sa pierdeti date"
							);
						}
						
						// add the appointment to the DB
						// (add it to the table corresponding to agendaUI.currentYear + 1900)
						addAppointment2(appointment);
						dayUI.addAppointment(appointment);

						if (agendaUI != null) {
							agendaUI.addAppointment(appointment);
						}
					}
				} catch(Exception exc) {
					exc.printStackTrace();
				}
			} else if (e.getSource() == dayUI.getRemoveButton()) {
				try {
					int confirmation = JOptionPane.showConfirmDialog(
							dayUI,
							"Doriti sa anulati aceasta programare ?"
						);
					if (confirmation == JOptionPane.YES_OPTION) {
						Appointment appointment = dayUI.getSelectedAppointment();
						removeAppointment2(appointment);
						dayUI.removeAppointment(appointment);
						if (agendaUI != null) {
							agendaUI.removeAppointment(appointment);
						}
					}
				} catch(Exception exc) {
					exc.printStackTrace();
				}
			}
		}
	}
	
	private static ArrayList fillInData(ResultSet rst) throws Exception {
		String[] columnNames;
		String[] columnTypes;
		ArrayList data = new ArrayList();
		try {   
			int columnCount = rst.getMetaData().getColumnCount();
			//////System.out.println ("	columns = " + columnCount);
			int recordIndex = 0;           
			
			columnNames = new String[columnCount];
			columnTypes = new String[columnCount];
			for (int i = 1; i <= columnCount; i++) {
				columnTypes[i-1] = rst.getMetaData().getColumnTypeName(i);
				columnNames[i-1] = rst.getMetaData().getColumnName(i);
			}

			// getting data from the table
			while(rst.next()) {
				ArrayList row = new ArrayList();
				recordIndex++;
				//////System.out.println("Record: " + recordIndex);
				for (int i = 1; i <= columnCount; i++) {
					////System.out.print(rst.getMetaData().getColumnName(i));
					////System.out.print(": ");
					////System.out.print(rst.getString(i) + " ");
					row.add(rst.getString(i));
				}
				data.add(row);			
				//////System.out.println();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return data;		
	}
	
	public ArrayList getPatients() throws Exception {
		ResultSet result = selectPatients.executeQuery();
		ArrayList data = fillInData(result);
		
		ArrayList patients = new ArrayList();
		int crtId, prevId = -1;
		Patient patient = null;
		for (int i = 0; i < data.size(); i++) {
			ArrayList subData = (ArrayList)data.get(i);
			crtId = new Integer(((String)subData.get(0))).intValue();
			//System.out.println(crtId);
			
			if (crtId != prevId) {
				if (patient != null) {
					patients.add(patient);
				}
				// create a new Patient
				Date birthDate = 
					new Date(
							new Integer(((String)subData.get(5))).intValue() - 1900,
							new Integer(((String)subData.get(4))).intValue() - 1,
							new Integer(((String)subData.get(3))).intValue()
					);
				//System.out.println(birthDate);
				patient = 
					new Patient(
							(String)(subData.get(1)), 
							((String)(subData.get(2))).charAt(0),
							birthDate,
							(String)(subData.get(6))
					);
				patient.setId(crtId);
				if ((subData.get(8) != null) && ((String)subData.get(8)).length() > 1) {
					patient.addPhoneNumber((String)subData.get(8));
				}
			} else {
				// the same doctor, just add a phone number
				if ((subData.get(8) != null) && ((String)subData.get(8)).length() > 1) {
					patient.addPhoneNumber((String)subData.get(8));
				}				
			}
			prevId = crtId;
		}
		if (patient != null) {
			patients.add(patient);
		}
		sortList(patients);
		return patients;
	}
	
//	public ArrayList getPatientsForName(String name) throws Exception {
//		String nameToUpper = name.toUpperCase();
//
//		patientsForName.clearParameters();
//		patientsForName.setString(1, nameToUpper);
//		ResultSet result = patientsForName.executeQuery();
//		ArrayList data = fillInData(result);
//
//		ArrayList patients = new ArrayList();
//		int crtId, prevId = -1;
//		Patient patient = null;
//		for (int i = 0; i < data.size(); i++) {
//			ArrayList subData = (ArrayList)data.get(i);
//			crtId = new Integer(((String)subData.get(0))).intValue();
//			//System.out.println(crtId);
//			
//			if (crtId != prevId) {
//				if (patient != null) {
//					patients.add(patient);
//				}
//				// create a new Patient
//				Date birthDate = 
//					new Date(
//							new Integer(((String)subData.get(5))).intValue() - 1900,
//							new Integer(((String)subData.get(4))).intValue() - 1,
//							new Integer(((String)subData.get(3))).intValue()
//					);
//				//System.out.println(birthDate);
//				patient = 
//					new Patient(
//							(String)(subData.get(1)), 
//							((String)(subData.get(2))).charAt(0),
//							birthDate,
//							(String)(subData.get(6))
//					);
//				patient.setId(crtId);
//				if ((subData.get(8) != null) && ((String)subData.get(8)).length() > 1) {
//					patient.addPhoneNumber((String)subData.get(8));
//				}
//			} else {
//				// the same patient, just add a phone number
//				if ((subData.get(8) != null) && ((String)subData.get(8)).length() > 1) {
//					patient.addPhoneNumber((String)subData.get(8));
//				}				
//			}
//			
//			prevId = crtId;
//		}
//		if (patient != null) {
//			patients.add(patient);
//		}
//		return patients;
//	}
//	
	
	private ArrayList performOperation(String sql) throws Exception {
		//////System.out.println ("Database = " + dbname.toString());
		//////System.out.println ("Command = " + sql);
		ArrayList data = new ArrayList();
		try {
			Connection conn;
			Class.forName("org.firebirdsql.jdbc.FBDriver");
			conn = DriverManager.getConnection(dbname.toString(), "sysdba", "masterkey");
			Statement stmt = null;
			ResultSet rst = null;
			try {
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql);
				data = fillInData(rst);				
			} finally {
				// close the database resources immediately, rather than waiting
				// for the finalizer to kick in later
				//////System.out.println (" .... finalizing ....");
				if (rst != null) rst.close();
				if (stmt != null) stmt.close();
				conn.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			//////System.out.println ("----" + ex.getStackTrace() + "----");
			//////System.out.println ("^^^^" + ex.getMessage() + "^^^^");
			//////System.out.println ("====" + ex.toString() + "====");
			throw ex;
		}
		return data;
	}
	
	public ArrayList getPatientsForName(String name) throws Exception {
		String nameToUpper = name.toUpperCase();
		String sql = "SELECT * FROM " + tablePatients + " LEFT JOIN " + tablePatientPhones +
			" ON " + tablePatients + ".patient_id=" + tablePatientPhones + ".patient_id" +
			" WHERE UPPER(Name) LIKE '%" + nameToUpper + "%' ORDER BY " + tablePatients + ".patient_id ASC;";
		ArrayList data = performOperation(sql);
		/*
		patientsForName.clearParameters();
		patientsForName.setString(1, name);
		ResultSet result = patientsForName.executeQuery();
		ArrayList data = fillInData(result);
		*/
		ArrayList patients = new ArrayList();
		int crtId, prevId = -1;
		Patient patient = null;
		for (int i = 0; i < data.size(); i++) {
			ArrayList subData = (ArrayList)data.get(i);
			crtId = new Integer(((String)subData.get(0))).intValue();
			//System.out.println(crtId);
			
			if (crtId != prevId) {
				if (patient != null) {
					patients.add(patient);
				}
				// create a new Patient
				Date birthDate = 
					new Date(
							new Integer(((String)subData.get(5))).intValue() - 1900,
							new Integer(((String)subData.get(4))).intValue() - 1,
							new Integer(((String)subData.get(3))).intValue()
					);
				//System.out.println(birthDate);
				patient = 
					new Patient(
							(String)(subData.get(1)), 
							((String)(subData.get(2))).charAt(0),
							birthDate,
							(String)(subData.get(6))
					);
				patient.setId(crtId);
				if ((subData.get(8) != null) && ((String)subData.get(8)).length() > 1) {
					patient.addPhoneNumber((String)subData.get(8));
				}
			} else {
				// the same patient, just add a phone number
				if ((subData.get(8) != null) && ((String)subData.get(8)).length() > 1) {
					patient.addPhoneNumber((String)subData.get(8));
				}				
			}
			
			prevId = crtId;
		}
		if (patient != null) {
			patients.add(patient);
		}
		return patients;
	}
	
	public ArrayList getDoctors() throws Exception {
		/*
		String sql = "SELECT * FROM " + tableDoctors + " LEFT JOIN " + tableDoctorPhones +
			" ON " + tableDoctors + ".doctor_id=" + tableDoctorPhones + ".doctor_id" +
			" ORDER BY " + tableDoctors + ".doctor_id ASC;";
		ArrayList data = performOperation(sql);
		*/
		ResultSet result = selectDoctorsCabinets.executeQuery();
		ArrayList data = fillInData(result);
		
		ArrayList doctors = new ArrayList();
		int crtId, prevId = -1;
		Doctor doctor = null;
		for (int i = 0; i < data.size(); i++) {
			ArrayList subData = (ArrayList)data.get(i);
			crtId = new Integer(((String)subData.get(0))).intValue();
			//System.out.println(crtId);
			
			if (crtId != prevId) {
				if (doctor != null) {
					doctors.add(doctor);
				}
				// create a new Doctor
				Date birthDate = 
					new Date(
							new Integer(((String)subData.get(5))).intValue() - 1900,
							new Integer(((String)subData.get(4))).intValue() - 1,
							new Integer(((String)subData.get(3))).intValue()
					);
				//System.out.println(birthDate);
				doctor = 
					new Doctor(
							(String)(subData.get(1)), 
							((String)(subData.get(2))).charAt(0),
							birthDate,
							(String)(subData.get(6))
					);
				doctor.setId(crtId);
				if ((subData.get(8) != null) && ((String)subData.get(8)).length() > 1) {
					doctor.addPhoneNumber((String)subData.get(8));
				}
				if ((subData.get(10) != null) && ((String)subData.get(10)).length() > 0) {
					doctor.setCabinet((String)subData.get(10));
				}
			} else {
				// the same doctor, just add a phone number
				if ((subData.get(8) != null) && ((String)subData.get(8)).length() > 1) {
					doctor.addPhoneNumber((String)subData.get(8));
				}				
			}
			prevId = crtId;
		}
		if (doctor != null) {
			doctors.add(doctor);
		}
		sortList(doctors);
		return doctors;
	}

	// deprecated
	public ArrayList getAppointments(int month, int year, ArrayList patients) throws Exception {
		appointmentsPerMonth.clearParameters();
		appointmentsPerMonth.setInt(1, month);
		appointmentsPerMonth.setInt(2, year);
		ResultSet result = appointmentsPerMonth.executeQuery();
		ArrayList data = fillInData(result);
		//System.out.println("AgendaEditor.getAppointments");
		for (int i = 0; i < data.size(); i++) {
			ArrayList subData = (ArrayList)data.get(i);
			for (int j = 0; j < subData.size(); j++) {
				System.out.print(subData.get(j) + " : ");
			}
			//System.out.println();
		}
		
		ArrayList appointments = new ArrayList();
		
		for (int i = 0; i < data.size(); i++) {
			ArrayList subData = (ArrayList)data.get(i);
			// create a new Doctor
			Date birthDate = 
				new Date(
						new Integer(((String)subData.get(5))).intValue() - 1900,
						new Integer(((String)subData.get(4))).intValue() - 1,
						new Integer(((String)subData.get(3))).intValue()
				);
			Doctor doctor = new Doctor(
				(String)subData.get(1),
				((String)subData.get(2)).charAt(0),
				birthDate,
				(String)subData.get(6)
			);
			doctor.setId(new Integer((String)subData.get(0)).intValue());
			
			// create a new Appointment
			Date date = 
				new Date(
						new Integer(((String)subData.get(12))).intValue() - 1900,
						new Integer(((String)subData.get(11))).intValue() - 1,
						new Integer(((String)subData.get(10))).intValue()
				);
			Time time = 
				new Time(
					new Integer(((String)subData.get(13))).intValue(), 
					new Integer(((String)subData.get(14))).intValue(),
					0
				);
			
			// create a new Patient
			birthDate = 
				new Date(
						new Integer(((String)subData.get(21))).intValue() - 1900,
						new Integer(((String)subData.get(20))).intValue() - 1,
						new Integer(((String)subData.get(19))).intValue()
				);
			
			Patient patient = new Patient(
				(String)subData.get(17),
				((String)subData.get(18)).charAt(0),
				birthDate,
				(String)subData.get(22)
			);
			patient.setId(new Integer((String)subData.get(16)).intValue());
			
			patients.add(patient);
			
			Appointment appointment = new Appointment(date, time, doctor, patient);
			appointment.setId(new Integer((String)subData.get(7)).intValue());
			appointment.setText((String)subData.get(15));
			appointments.add(appointment);	
		}
		
		return appointments;
	}
	
	// TODO: check if it works
	public ArrayList getAppointments2(int month, int year, ArrayList patients) throws Exception {
		try {
			Class.forName("org.firebirdsql.jdbc.FBDriver");
			Connection conn = DriverManager.getConnection(dbname.toString(), "sysdba", "masterkey");
			String tableApps = null;
			//if (year > 2008) {
				tableApps = tableAppointments + "_" + year;
			//} else {
			//	tableApps = tableAppointments;
			//}
			String sql = "SELECT * FROM " + tableDoctors + " LEFT JOIN " + tableApps +
				" ON " + tableDoctors + ".doctor_id=" + tableApps + ".doctor_id" +
				" JOIN " + tablePatients + " ON " + tableApps + ".patient_id=" + 
				tablePatients + ".patient_id" + 
				" WHERE (" + tableApps + ".date_month=? AND " + 
				tableApps + ".date_year=?) ORDER BY " + tableApps + ".date_day ASC;";
			
			ArrayList appointments = new ArrayList();
			
			PreparedStatement appointmentsMonth = null;
			try {
				appointmentsMonth = conn.prepareStatement(sql);
			} catch (Exception e) {
				return appointments;
			}
			appointmentsMonth.clearParameters();
			appointmentsMonth.setInt(1, month);
			appointmentsMonth.setInt(2, year);
			
			ResultSet result = appointmentsMonth.executeQuery();
			
			ArrayList data = fillInData(result);
			//System.out.println("AgendaEditor.getAppointments2");
			for (int i = 0; i < data.size(); i++) {
				ArrayList subData = (ArrayList)data.get(i);
				for (int j = 0; j < subData.size(); j++) {
					System.out.print("NEW2 " + subData.get(j) + " : ");
				}
				//System.out.println();
			}
			
			
			for (int i = 0; i < data.size(); i++) {
				ArrayList subData = (ArrayList)data.get(i);
				// create a new Doctor
				Date birthDate = 
					new Date(
							new Integer(((String)subData.get(5))).intValue() - 1900,
							new Integer(((String)subData.get(4))).intValue() - 1,
							new Integer(((String)subData.get(3))).intValue()
					);
				Doctor doctor = new Doctor(
					(String)subData.get(1),
					((String)subData.get(2)).charAt(0),
					birthDate,
					(String)subData.get(6)
				);
				doctor.setId(new Integer((String)subData.get(0)).intValue());
				
				// create a new Appointment
				Date date = 
					new Date(
							new Integer(((String)subData.get(12))).intValue() - 1900,
							new Integer(((String)subData.get(11))).intValue() - 1,
							new Integer(((String)subData.get(10))).intValue()
					);
				Time time = 
					new Time(
						new Integer(((String)subData.get(13))).intValue(), 
						new Integer(((String)subData.get(14))).intValue(),
						0
					);
				
				// create a new Patient
				birthDate = 
					new Date(
							new Integer(((String)subData.get(22))).intValue() - 1900,
							new Integer(((String)subData.get(21))).intValue() - 1,
							new Integer(((String)subData.get(20))).intValue()
					);
				
				Patient patient = new Patient(
					(String)subData.get(18),
					((String)subData.get(19)).charAt(0),
					birthDate,
					(String)subData.get(23)
				);
				patient.setId(new Integer((String)subData.get(17)).intValue());
				
				patients.add(patient);
				
				Appointment appointment = new Appointment(date, time, doctor, patient);
				appointment.setId(new Integer((String)subData.get(7)).intValue());
				appointment.setText((String)subData.get(15));
				// the duration
				if (subData.get(16) != null)
					appointment.setDuration(new Integer((String)subData.get(16)).intValue());
				else appointment.setDuration(0);
				appointments.add(appointment);	
			}
			return appointments;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
//	 TODO: check if it works
	public ArrayList getAppointments2(int day, int month, int year, ArrayList patients) throws Exception {
		try {
			Class.forName("org.firebirdsql.jdbc.FBDriver");
			Connection conn = DriverManager.getConnection(dbname.toString(), "sysdba", "masterkey");
			String tableApps = null;
			//if (year > 2008) {
				tableApps = tableAppointments + "_" + year;
			//} else {
			//	tableApps = tableAppointments;
			//}
			String sql = "SELECT * FROM " + tableDoctors + " LEFT JOIN " + tableApps +
				" ON " + tableDoctors + ".doctor_id=" + tableApps + ".doctor_id" +
				" JOIN " + tablePatients + " ON " + tableApps + ".patient_id=" + 
				tablePatients + ".patient_id" + 
				" WHERE (" + tableApps + ".date_day=? AND " + 
				tableApps + ".date_month=? AND " +
				tableApps + ".date_year=?) ORDER BY " + tableApps + ".date_day ASC;";
			
			PreparedStatement appointmentsMonth = conn.prepareStatement(sql);
			appointmentsMonth.clearParameters();
			appointmentsMonth.setInt(1, day);
			appointmentsMonth.setInt(2, month);
			appointmentsMonth.setInt(3, year);
			
			ResultSet result = appointmentsMonth.executeQuery();
			
			ArrayList data = fillInData(result);
			//System.out.println("AgendaEditor.getAppointments2");
			for (int i = 0; i < data.size(); i++) {
				ArrayList subData = (ArrayList)data.get(i);
				for (int j = 0; j < subData.size(); j++) {
					System.out.print("NEW2 " + subData.get(j) + " : ");
				}
				//System.out.println();
			}
			
			ArrayList appointments = new ArrayList();
			
			for (int i = 0; i < data.size(); i++) {
				ArrayList subData = (ArrayList)data.get(i);
				// create a new Doctor
				Date birthDate = 
					new Date(
							new Integer(((String)subData.get(5))).intValue() - 1900,
							new Integer(((String)subData.get(4))).intValue() - 1,
							new Integer(((String)subData.get(3))).intValue()
					);
				Doctor doctor = new Doctor(
					(String)subData.get(1),
					((String)subData.get(2)).charAt(0),
					birthDate,
					(String)subData.get(6)
				);
				doctor.setId(new Integer((String)subData.get(0)).intValue());
				
				// create a new Appointment
				Date date = 
					new Date(
							new Integer(((String)subData.get(12))).intValue() - 1900,
							new Integer(((String)subData.get(11))).intValue() - 1,
							new Integer(((String)subData.get(10))).intValue()
					);
				Time time = 
					new Time(
						new Integer(((String)subData.get(13))).intValue(), 
						new Integer(((String)subData.get(14))).intValue(),
						0
					);
				
				// create a new Patient
				birthDate = 
					new Date(
							new Integer(((String)subData.get(22))).intValue() - 1900,
							new Integer(((String)subData.get(21))).intValue() - 1,
							new Integer(((String)subData.get(20))).intValue()
					);
				
				Patient patient = new Patient(
					(String)subData.get(18),
					((String)subData.get(19)).charAt(0),
					birthDate,
					(String)subData.get(23)
				);
				patient.setId(new Integer((String)subData.get(17)).intValue());
				
				if (patients != null) patients.add(patient);
				
				Appointment appointment = new Appointment(date, time, doctor, patient);
				appointment.setId(new Integer((String)subData.get(7)).intValue());
				appointment.setText((String)subData.get(15));
				// the duration
				if (subData.get(16) != null)
					appointment.setDuration(new Integer((String)subData.get(16)).intValue());
				else appointment.setDuration(0);
				appointments.add(appointment);	
			}
			return appointments;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// deprecated
	public ArrayList getAppointmentsWithDuration(int month, int year, ArrayList patients) throws Exception {
		long t1 = System.currentTimeMillis();
		appointmentsPerMonth2.clearParameters();
		appointmentsPerMonth2.setInt(1, month);
		appointmentsPerMonth2.setInt(2, year);
		ResultSet result = appointmentsPerMonth2.executeQuery();
		long t2 = System.currentTimeMillis();
		//System.out.println("AgendaEditor.getAppointmentsWithDuration took " + (t2 - t1) + " milliseconds");
		
		ArrayList data = fillInData(result);
		//System.out.println("AgendaEditor.getAppointmentsWithDuration");
		for (int i = 0; i < data.size(); i++) {
			ArrayList subData = (ArrayList)data.get(i);
			for (int j = 0; j < subData.size(); j++) {
				System.out.print("NEW " + subData.get(j) + " : ");
			}
			//System.out.println();
		}
		
		ArrayList appointments = new ArrayList();
		
		for (int i = 0; i < data.size(); i++) {
			ArrayList subData = (ArrayList)data.get(i);
			// create a new Doctor
			Date birthDate = 
				new Date(
						new Integer(((String)subData.get(5))).intValue() - 1900,
						new Integer(((String)subData.get(4))).intValue() - 1,
						new Integer(((String)subData.get(3))).intValue()
				);
			Doctor doctor = new Doctor(
				(String)subData.get(1),
				((String)subData.get(2)).charAt(0),
				birthDate,
				(String)subData.get(6)
			);
			doctor.setId(new Integer((String)subData.get(0)).intValue());
			
			// create a new Appointment
			Date date = 
				new Date(
						new Integer(((String)subData.get(12))).intValue() - 1900,
						new Integer(((String)subData.get(11))).intValue() - 1,
						new Integer(((String)subData.get(10))).intValue()
				);
			Time time = 
				new Time(
					new Integer(((String)subData.get(13))).intValue(), 
					new Integer(((String)subData.get(14))).intValue(),
					0
				);
			
			// create a new Patient
			birthDate = 
				new Date(
						new Integer(((String)subData.get(22))).intValue() - 1900,
						new Integer(((String)subData.get(21))).intValue() - 1,
						new Integer(((String)subData.get(20))).intValue()
				);
			
			Patient patient = new Patient(
				(String)subData.get(18),
				((String)subData.get(19)).charAt(0),
				birthDate,
				(String)subData.get(23)
			);
			patient.setId(new Integer((String)subData.get(17)).intValue());
			
			patients.add(patient);
			
			Appointment appointment = new Appointment(date, time, doctor, patient);
			appointment.setId(new Integer((String)subData.get(7)).intValue());
			appointment.setText((String)subData.get(15));
			// the duration
			appointment.setDuration(new Integer((String)subData.get(16)).intValue());
			appointments.add(appointment);	
		}
		//System.out.println("AgendaEditor.getAppointmentsWithDuration, the rest took " + (System.currentTimeMillis() - t2));
		return appointments;
	}

	// deprecated
	public ArrayList getAppointmentsPerDay(int day, int month, int year, ArrayList patients) throws Exception {
		appointmentsPerDay.clearParameters();
		appointmentsPerDay.setInt(1, day);
		appointmentsPerDay.setInt(2, month);
		appointmentsPerDay.setInt(3, year);
		ResultSet result = appointmentsPerDay.executeQuery();
		ArrayList data = fillInData(result);
		for (int i = 0; i < data.size(); i++) {
			ArrayList subData = (ArrayList)data.get(i);
			for (int j = 0; j < subData.size(); j++) {
				System.out.print("NEW " + subData.get(j) + " : ");
			}
			//System.out.println();
		}
		
		ArrayList appointments = new ArrayList();
		
		for (int i = 0; i < data.size(); i++) {
			ArrayList subData = (ArrayList)data.get(i);
			// create a new Doctor
			Date birthDate = 
				new Date(
						new Integer(((String)subData.get(5))).intValue() - 1900,
						new Integer(((String)subData.get(4))).intValue() - 1,
						new Integer(((String)subData.get(3))).intValue()
				);
			Doctor doctor = new Doctor(
				(String)subData.get(1),
				((String)subData.get(2)).charAt(0),
				birthDate,
				(String)subData.get(6)
			);
			doctor.setId(new Integer((String)subData.get(0)).intValue());
			
			// create a new Appointment
			Date date = 
				new Date(
						new Integer(((String)subData.get(12))).intValue() - 1900,
						new Integer(((String)subData.get(11))).intValue() - 1,
						new Integer(((String)subData.get(10))).intValue()
				);
			Time time = 
				new Time(
					new Integer(((String)subData.get(13))).intValue(), 
					new Integer(((String)subData.get(14))).intValue(),
					0
				);
			
			// create a new Patient
			birthDate = 
				new Date(
						new Integer(((String)subData.get(22))).intValue() - 1900,
						new Integer(((String)subData.get(21))).intValue() - 1,
						new Integer(((String)subData.get(20))).intValue()
				);
			
			Patient patient = new Patient(
				(String)subData.get(18),
				((String)subData.get(19)).charAt(0),
				birthDate,
				(String)subData.get(23)
			);
			patient.setId(new Integer((String)subData.get(17)).intValue());
			
			if (patients != null) patients.add(patient);
			
			Appointment appointment = new Appointment(date, time, doctor, patient);
			appointment.setId(new Integer((String)subData.get(7)).intValue());
			appointment.setText((String)subData.get(15));
			// the duration
			appointment.setDuration(new Integer((String)subData.get(16)).intValue());
			appointments.add(appointment);	
		}
		return appointments;
	}

	// deprecated
	public ArrayList getAppointmentsPerDay2(int day, int month, int year, ArrayList patients) throws Exception {
		ArrayList appointments = null;
		try {
			Class.forName("org.firebirdsql.jdbc.FBDriver");
			Connection conn = DriverManager.getConnection(dbname.toString(), "sysdba", "masterkey");
			String tableApps = tableAppointments + "_" + year; //((year > 2008) ? (tableAppointments + "_" + year) : tableAppointments);
			String sql = "SELECT * FROM " + tableDoctors + " LEFT JOIN " + tableApps +
				" ON " + tableDoctors + ".doctor_id=" + tableApps + ".doctor_id" +
				" JOIN " + tablePatients + " ON " + tableApps + ".patient_id=" + 
				tablePatients + ".patient_id" + 
				" WHERE (" + tableApps + ".date_day=? AND " + tableApps + ".date_month=? AND " +
				tableApps + ".date_year=?) ORDER BY " + tableApps + ".date_day ASC;";
			appointmentsPerDay = conn.prepareStatement(sql);
			appointmentsPerDay.clearParameters();
			appointmentsPerDay.setInt(1, day);
			appointmentsPerDay.setInt(2, month);
			appointmentsPerDay.setInt(3, year);
			ResultSet result = appointmentsPerDay.executeQuery();
			ArrayList data = fillInData(result);
			for (int i = 0; i < data.size(); i++) {
				ArrayList subData = (ArrayList)data.get(i);
				for (int j = 0; j < subData.size(); j++) {
					System.out.print("NEW " + subData.get(j) + " : ");
				}
				//System.out.println();
			}
			
			appointments = new ArrayList();
			
			for (int i = 0; i < data.size(); i++) {
				ArrayList subData = (ArrayList)data.get(i);
				// create a new Doctor
				Date birthDate = 
					new Date(
							new Integer(((String)subData.get(5))).intValue() - 1900,
							new Integer(((String)subData.get(4))).intValue() - 1,
							new Integer(((String)subData.get(3))).intValue()
					);
				Doctor doctor = new Doctor(
					(String)subData.get(1),
					((String)subData.get(2)).charAt(0),
					birthDate,
					(String)subData.get(6)
				);
				doctor.setId(new Integer((String)subData.get(0)).intValue());
				
				// create a new Appointment
				Date date = 
					new Date(
							new Integer(((String)subData.get(12))).intValue() - 1900,
							new Integer(((String)subData.get(11))).intValue() - 1,
							new Integer(((String)subData.get(10))).intValue()
					);
				Time time = 
					new Time(
						new Integer(((String)subData.get(13))).intValue(), 
						new Integer(((String)subData.get(14))).intValue(),
						0
					);
				
				// create a new Patient
				birthDate = 
					new Date(
							new Integer(((String)subData.get(22))).intValue() - 1900,
							new Integer(((String)subData.get(21))).intValue() - 1,
							new Integer(((String)subData.get(20))).intValue()
					);
				
				Patient patient = new Patient(
					(String)subData.get(18),
					((String)subData.get(19)).charAt(0),
					birthDate,
					(String)subData.get(23)
				);
				patient.setId(new Integer((String)subData.get(17)).intValue());
				
				if (patients != null) patients.add(patient);
				
				Appointment appointment = new Appointment(date, time, doctor, patient);
				appointment.setId(new Integer((String)subData.get(7)).intValue());
				appointment.setText((String)subData.get(15));
				// the duration
				appointment.setDuration(new Integer((String)subData.get(16)).intValue());
				appointments.add(appointment);	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return appointments;
	}
	
	// TODO: optimize this method
	// Add an appointment to the corresponding appointments table (e.g. an
	// appointment made for 2009 is added to appointments_2009)
	public void addAppointment2(Appointment appointment) throws Exception {
		// check if the table appointments_<<year>> exists; if not, create it
		// SELECT RDB$RELATION_NAME FROM RDB$RELATIONS /* all tables will be shown */
		// WHERE RDB$RELATION_NAME = 'your_TABLE' /* particular table is checked*/
		try {
			Class.forName("org.firebirdsql.jdbc.FBDriver");
			Connection conn = DriverManager.getConnection(dbname.toString(), "sysdba", "masterkey");
			
			//if (appointment.getDate().getYear() + 1900 > 2008) {
			
				String tableApps = tableAppointments + "_" + (appointment.getDate().getYear() + 1900);
				
				String sql = "SELECT RDB$RELATION_NAME FROM RDB$RELATIONS WHERE RDB$RELATION_NAME = '" +
						tableApps.toUpperCase() + "'";
			
				PreparedStatement appointmentsYear = conn.prepareStatement(sql);
				ResultSet result = appointmentsYear.executeQuery();
				
				PreparedStatement createTable = null;
				PreparedStatement createIndex1 = null;
				try {
					if (result.next() == false) {
						// table doesn't exist, create it
						sql = "CREATE TABLE " + tableApps + "(" +
							"APPOINTMENT_ID INTEGER," +
							"DOCTOR_ID INTEGER," +
							"PATIENT_ID INTEGER," +
							"DATE_DAY INTEGER," +
							"DATE_MONTH INTEGER," +
							"DATE_YEAR INTEGER," +
							"TIME_HOUR INTEGER," +
							"TIME_MINUTE INTEGER," +
							"APPOINTMENT_TEXT VARCHAR(100)," +
							"APPOINTMENT_DURATION INTEGER);";
						createTable = conn.prepareStatement(sql);
						createTable.executeUpdate();
						
						// create indices too for the new table
						sql = 
							"create index " + tableApps + "_idx1" +
							" on " + tableApps + "(date_day, date_month, date_year);";
						createIndex1 = conn.prepareStatement(sql);
						createIndex1.executeUpdate();
						createIndex1.close();
						
						sql = 
							"create index " + tableApps + "_idx2" + " " +
							" on " + tableApps + "(date_month, date_year);";
						createIndex1 = conn.prepareStatement(sql);
						createIndex1.executeUpdate();
						
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					appointmentsYear.close();
					if (createTable != null) createTable.close();
					if (createIndex1 != null) createIndex1.close();
				}

				//}
			addAppointment(appointment);
			
//			insertAppointment.clearParameters();
//			insertAppointment.setInt(1, appointment.getId());
//			insertAppointment.setInt(2, appointment.getDoctor().getId());
//			insertAppointment.setInt(3, appointment.getPatient().getId());
//			insertAppointment.setInt(4, appointment.getDate().getDate());
//			insertAppointment.setInt(5, appointment.getDate().getMonth() + 1);
//			insertAppointment.setInt(6, appointment.getDate().getYear() + 1900);
//			insertAppointment.setInt(7, appointment.getTime().getHours());
//			insertAppointment.setInt(8, appointment.getTime().getMinutes());
//			insertAppointment.setString(9, appointment.getText());
//			insertAppointment.setInt(10, appointment.getDuration());
//			insertAppointment.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void addAppointment(Appointment appointment) throws Exception {
		String tableApps = null;
		//if (appointment.getDate().getYear() + 1900 > 2008) {
			tableApps = tableAppointments + "_" + (appointment.getDate().getYear() + 1900);
		//} else {
		//	tableApps = tableAppointments;
		//}
		String sql = "INSERT INTO " + tableApps + " VALUES (?,?,?,?,?,?,?,?,?,?);";
		Class.forName("org.firebirdsql.jdbc.FBDriver");
		Connection conn = DriverManager.getConnection(dbname.toString(), "sysdba", "masterkey");
		insertAppointment = conn.prepareStatement(sql);

		insertAppointment.clearParameters();
		insertAppointment.setInt(1, appointment.getId());
		insertAppointment.setInt(2, appointment.getDoctor().getId());
		insertAppointment.setInt(3, appointment.getPatient().getId());
		insertAppointment.setInt(4, appointment.getDate().getDate());
		insertAppointment.setInt(5, appointment.getDate().getMonth() + 1);
		insertAppointment.setInt(6, appointment.getDate().getYear() + 1900);
		insertAppointment.setInt(7, appointment.getTime().getHours());
		insertAppointment.setInt(8, appointment.getTime().getMinutes());
		insertAppointment.setString(9, appointment.getText());
		insertAppointment.setInt(10, appointment.getDuration());
		insertAppointment.executeUpdate();
		//addAppointmentDuration(appointment.getId(), appointment.getDuration());
	}
	
//	private void addAppointmentDuration(int id, int duration) throws Exception {
//		insertAppointmentDuration.clearParameters();
//		insertAppointmentDuration.setInt(1, id);
//		insertAppointmentDuration.setInt(2, duration);
//		insertAppointmentDuration.executeUpdate();
//	}
	
	public void removeAppointment(Appointment appointment) throws Exception {
		removeAppointment.clearParameters();
		removeAppointment.setInt(1, appointment.getId());
		removeAppointment.executeUpdate();
		//removeAppointmentDuration(appointment.getId());
	}
	
	public void removeAppointment2(Appointment appointment) throws Exception {
		try {
			Class.forName("org.firebirdsql.jdbc.FBDriver");
			Connection conn = DriverManager.getConnection(dbname.toString(), "sysdba", "masterkey");
			StringBuffer tableApps = new StringBuffer(tableAppointments);
			//if (appointment.getDate().getYear() + 1900 > 2008) {			
				tableApps.append("_");
				tableApps.append(appointment.getDate().getYear() + 1900);
			//}
			String sql = "DELETE FROM " + tableApps + " WHERE appointment_id=?;";
			removeAppointment = conn.prepareStatement(sql);
			removeAppointment(appointment);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
//	private void removeAppointmentDuration(int id) throws Exception {
//		removeAppointmentDuration.clearParameters();
//		removeAppointmentDuration.setInt(1, id);
//		removeAppointmentDuration.executeUpdate();
//	}
	
	public void addPatient(Patient patient) throws Exception {
		insertPatient.clearParameters();
		insertPatient.setInt(1, patient.getId());
		insertPatient.setString(2, patient.getName());
		insertPatient.setString(3, new String(patient.getSex() + ""));
		insertPatient.setInt(4, patient.getBirthDay());
		insertPatient.setInt(5, patient.getBirthMonth());
		insertPatient.setInt(6, patient.getBirthYear());
		insertPatient.setString(7, patient.getAddress());
		insertPatient.executeUpdate();
		
		if (patient.getPhoneNumbers().size() > 0) {
			for (int i = 0; i < patient.getPhoneNumbers().size(); i++) {
				insertPatientPhone.clearParameters();
				insertPatientPhone.setInt(1, patient.getId());
				insertPatientPhone.setString(2, (String)patient.getPhoneNumbers().get(i));
				insertPatientPhone.executeUpdate();
			}
		}
	}
	
	public static synchronized int getSequenceID(String tableName) throws Exception {
		selectSequenceID.clearParameters();
		selectSequenceID.setString(1, tableName);
		int id = -1;
		ResultSet result = selectSequenceID.executeQuery();
		ArrayList data = fillInData(result);
		if (data != null) {
			ArrayList subData = (ArrayList)data.get(0);
			if (subData != null) {
				id = new Integer((String)subData.get(0)).intValue();
				updateSequenceID.clearParameters();
				updateSequenceID.setInt(1, (id + 1) % Integer.MAX_VALUE);
				updateSequenceID.setString(2, tableName);
				updateSequenceID.executeUpdate();
				return id;
			}
		}
		return -1;
	}
	
	private void sortList(ArrayList list) {
		Collections.sort(
				list, 
				new Comparator() {
					public int compare(Object a, Object b) {
						if (a instanceof Patient) {
							return ((Patient)a).getName().compareTo(((Patient)b).getName());
						} else if (a instanceof Doctor) {
							return ((Doctor)a).getName().compareTo(((Doctor)b).getName());
						}
						return 0;
					}
				}
		);
	}
	
	
	public static void main(String[] args) {
		AgendaEditor ae = new AgendaEditor(null, Configurer.getConfiguration().getServerName());
		try {
			ArrayList appointments = 
				ae.getAppointmentsPerDay(2, 3, 2007, new ArrayList());
			for (int i = 0; i < appointments.size(); i++) {
				//System.out.println("appointment: " + (Appointment)appointments.get(i));
			}
			Date d = new Date();
			d.setYear(108);
			Appointment appointment = new Appointment(d, new Time(9, 9, 8), new Doctor("fifi", 'm', null, ""), new Patient("gigi", 'm', null, ""));
			ae.addAppointment2(appointment);
			
			ArrayList p = new ArrayList();
				// TO DO: call getAppointments2 in order to retrieve the appointments from the 
				// table corresponding to currentYear + 1900
			ArrayList currentAppointments = 
					ae.getAppointments2(7, 2010, p);
			Editor.sortList(currentAppointments);
			for (int i = 0; i < currentAppointments.size(); i++) {
				//System.out.println((Appointment)currentAppointments.get(i));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
