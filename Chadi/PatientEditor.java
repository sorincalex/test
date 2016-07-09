/*
 * Created on Aug 19, 2006
 *
 */

import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.JFrame;

import java.io.*;
import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.print.*;
import java.text.*;

import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;

import configuration.Configurer;

/**
 * @author Sorin Cristescu
 *
 * Search the DB for a given patient name; add a new patient to the DB
 * 
 */
public class PatientEditor implements ActionListener {
	
	// the UIs for which this class acts as an observer
	private PatientEditorUI patientUI;
	private DoctorEditorUI doctorUI;
	private ConsultationEditorUI consultationUI;
	private AnalysisEditorUI analysisUI;
	private StatisticsUI statisticsUI;
	
	private ArrayList allConsultations = null, patientsForConsultations = null;
	private ArrayList allAnalyses = null, patientsForAnalyses = null;
	private Hashtable consultationsForMonths = new Hashtable();
	private Hashtable patientsConsultationsForMonths = new Hashtable();
	private Hashtable analysesForMonths = new Hashtable();
	private Hashtable patientsAnalysesForMonths = new Hashtable();
	private int oldMonth = -2;
	private int oldYear = -2;
	
	private static StringBuffer dbname;
	private static String serverName;

	private static String tablePatients = "patients";
	private static String tablePatientPhones = "patient_phones";
	private static String tableConsultations = "consultations";
	private static String tableAnalyses = "analyses";
	private static String tableDoctors = "doctors";
	private static String tableAppointments = "appointments";
	private static String tableDoctorCabinet = "doctor_cabinet";
	private static String tableDoctorPhones = "doctor_phones";
	private static String tableSequences = "sequences";
	
	private static PreparedStatement selectPatients, selectDoctors, consultationsForPatient, analysesForPatient,
			insertPatient, insertPatientPhone, consultationsPerMonth, analysesPerMonth,
			patientsForName, insertDoctor, insertDoctorPhone, insertDoctorCabinet, insertConsultation, 
			removeDoctor, removeDoctorPhone, insertAnalysis, removeConsultation, removeAnalysis, 
			removePatient, removePatientPhone, selectDoctorsCabinets, selectPatientIDs;
	private static PreparedStatement selectSequenceID, updateSequenceID;
	
	static {
		dbname = new StringBuffer(Configurer.getConfiguration().getDbname());
		serverName = Configurer.getConfiguration().getServerName();
		dbname.insert(0, "jdbc:firebirdsql:" + serverName + "/3050:");
		System.out.println("PatientEditor: dbname = " + dbname);

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
			
			sql = "SELECT * FROM " + tableConsultations + " WHERE patient_id=?;";
			consultationsForPatient = conn.prepareStatement(sql);
			
			sql = "SELECT * FROM " + tableAnalyses + " WHERE patient_id=?;";
			analysesForPatient = conn.prepareStatement(sql);
			
			sql = "INSERT INTO " + tablePatients + " VALUES (?,?,?,?,?,?,?);";
			insertPatient = conn.prepareStatement(sql);
			
			sql = "INSERT INTO " + tablePatientPhones + " VALUES (?,?);";
			insertPatientPhone = conn.prepareStatement(sql);
			
			sql = "SELECT * FROM " + tableDoctors + " LEFT JOIN " + tableConsultations +
			" ON " + tableDoctors + ".doctor_id=" + tableConsultations + ".doctor_id" +
			" LEFT JOIN " + tablePatients + " ON " + tableConsultations + ".patient_id=" + 
			tablePatients + ".patient_id WHERE (" + tableConsultations + ".date_month=? AND " +
			tableConsultations + ".date_year=?) ORDER BY " + tableDoctors + ".name ASC;";
			consultationsPerMonth = conn.prepareStatement(sql);
			
			sql = "SELECT * FROM " + tableDoctors + " LEFT JOIN " + tableAnalyses +
			" ON " + tableDoctors + ".doctor_id=" + tableAnalyses + ".doctor_id" +
			" LEFT JOIN " + tablePatients + " ON " + tableAnalyses + ".patient_id=" + 
			tablePatients + ".patient_id WHERE (" + tableAnalyses + ".date_month=? AND " +
			tableAnalyses + ".date_year=?) ORDER BY " + tableDoctors + ".name ASC;";
			analysesPerMonth = conn.prepareStatement(sql);
			
			sql = "SELECT * FROM " + tablePatients + " LEFT JOIN " + tablePatientPhones +
			" ON " + tablePatients + ".patient_id=" + tablePatientPhones + ".patient_id" +
			" WHERE Name LIKE ? ORDER BY " + tablePatients + ".patient_id ASC;";
			patientsForName = conn.prepareStatement(sql);

			sql = "INSERT INTO " + tableDoctors + " VALUES (?,?,?,?,?,?,?);";
			insertDoctor = conn.prepareStatement(sql);
			
			sql = "INSERT INTO " + tableDoctorCabinet + " VALUES (?,?);";
			insertDoctorCabinet = conn.prepareStatement(sql);
			
			sql = "INSERT INTO " + tableDoctorPhones + " VALUES (?,?);";
			insertDoctorPhone = conn.prepareStatement(sql);

			sql = "INSERT INTO " + tableConsultations + " VALUES (?,?,?,?,?,?,?,?);";
			insertConsultation = conn.prepareStatement(sql);

			sql = "INSERT INTO " + tableAnalyses + " VALUES (?,?,?,?,?,?,?,?);";
			insertAnalysis = conn.prepareStatement(sql);

			sql = "DELETE FROM " + tableConsultations + " WHERE patient_id=? AND doctor_id=?" + 
			" AND date_day=? AND date_month=? AND date_year=? AND specialty=?;";
			removeConsultation = conn.prepareStatement(sql);

			sql = "DELETE FROM " + tableAnalyses + " WHERE patient_id=? AND doctor_id=?" + 
			" AND date_day=? AND date_month=? AND date_year=? AND specialty=?;";
			removeAnalysis = conn.prepareStatement(sql);

			sql = "DELETE FROM " + tablePatients + " WHERE patient_id=?;";
			removePatient = conn.prepareStatement(sql);
			sql = "DELETE FROM " + tablePatientPhones + " WHERE patient_id=?;";
			removePatientPhone = conn.prepareStatement(sql);
			
			sql = "DELETE FROM " + tableDoctors + " WHERE doctor_id=?;";
			removeDoctor = conn.prepareStatement(sql);
			sql = "DELETE FROM " + tableDoctorPhones + " WHERE doctor_id=?;";
			removeDoctorPhone = conn.prepareStatement(sql);
				
			//sql = "SELECT MAX(appointment_id) FROM " + tableAppointments;
			//selectPatientIDs = conn.prepareStatement(sql);
			
			sql = "SELECT sequence_id FROM " + tableSequences + " WHERE table_name=?;";
			selectSequenceID = conn.prepareStatement(sql);
			
			sql = "UPDATE " +  tableSequences + " SET sequence_id=? WHERE table_name=?;";
			updateSequenceID = conn.prepareStatement(sql);
			
			sql = "SELECT * FROM " + tableDoctors + " WHERE doctor_id=?;";
			selectDoctors = conn.prepareStatement(sql);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public PatientEditor(String serverName) {

	}
	
	public PatientEditor(
		PatientEditorUI patientUI, 
		ConsultationEditorUI consultationUI, 
		String serverName
	) {
		this.patientUI = patientUI;
		this.consultationUI = consultationUI;
		this.serverName = serverName;
		dbname = new StringBuffer(Configurer.getConfiguration().getDbname());
		dbname.insert(0, "jdbc:firebirdsql:" + serverName + "/3050:");
		System.out.println("PatientEditor: dbname = " + dbname);
	}
	
	/**
	 * @param consultationUI The consultationUI to set.
	 */
	public void setConsultationUI(ConsultationEditorUI consultationUI) {
		this.consultationUI = consultationUI;
	}
	
	/**
	 * @param analysisUI The analysisUI to set.
	 */
	public void setAnalysisUI(AnalysisEditorUI analysisUI) {
		this.analysisUI = analysisUI;
	}
	
	/**
	 * @param patientUI The patientUI to set.
	 */
	public void setPatientUI(PatientEditorUI patientUI) {
		this.patientUI = patientUI;
	}
	
	/**
	 * @param doctorUI The doctorUI to set.
	 */
	public void setDoctorUI(DoctorEditorUI doctorUI) {
		this.doctorUI = doctorUI;
	}
	
	/**
	 * @param statisticsUI The statisticsUI to set.
	 */
	public void setStatisticsUI(StatisticsUI statisticsUI) {
		this.statisticsUI = statisticsUI;
	}
	
	public void actionPerformed(ActionEvent e) {
		if (consultationUI != null) {
			if (e.getSource() == consultationUI.getAddConsultationButton()) {
				// the user wants to add a new consultation
				System.out.println("ADDING A CONSULTATION");
				try {
					if (
						(consultationUI.getNewConsultationSpecialty() == null) ||
						(consultationUI.getNewConsultationSpecialty().length() <= 1) ||
						(consultationUI.getNewConsultationText() == null) ||
						(consultationUI.getNewConsultationText().length() <= 1)
					) {
						throw new Exception();
					}
					Patient patient = consultationUI.getPatient();
					Consultation consultation =
						new Consultation(
							consultationUI.getNewConsultationDate(),
							consultationUI.getNewConsultationText(),
							consultationUI.getNewConsultationSpecialty(),
							consultationUI.getNewConsultationAmount()
						);
					Doctor doctor = consultationUI.getNewConsultationDoctor();
					consultation.setDoctor(doctor);
					addConsultation(consultation, patient);
					consultationUI.addConsultation(consultation);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(
						consultationUI, 
						"Eroare ! Ati introdus toate campurile ?"
					);
					ex.printStackTrace();
				}
			} else if (e.getSource() == consultationUI.getClearFieldsButton()) {
				consultationUI.clearFields();
			} else if (e.getSource() == consultationUI.getRemoveConsultationButton()) {
				try {
					int confirmation = JOptionPane.showConfirmDialog(
						consultationUI,
						"Doriti sa stergeti aceasta consultatie ?"
					);
					if (confirmation == JOptionPane.YES_OPTION) {
						Consultation c = consultationUI.getCurrentConsultation();
						removeConsultation(c, consultationUI.getPatient());
						consultationUI.removeConsultation(c);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else if (e.getSource() == consultationUI.getPrintButton()) {
				// print the current consultation
				Consultation c = consultationUI.getCurrentConsultation();
				StringBuffer consultationText = new StringBuffer(); //= c.getText();
				consultationText.append(PrintText.logo);
				//consultationText.append("                                                                               ");
				//consultationText.append("                                                                                                          ");
				consultationText.append("\n \n\t\tConsultatie pentru pacientul: ");
				consultationText.append(consultationUI.getCurrentPatient().getName());
				//consultationText.append("                                                                               ");
				consultationText.append("\n\t\tData: " + c.getDay() + "-" + c.getMonth() + "-" + c.getYear());
				//consultationText.append("                                                                               ");
				consultationText.append("\n\t\t" + c.getSpecialty());
				//consultationText.append("                                                                                                          ");
				consultationText.append("\n\tDoctor: " + c.getDoctor().getName());
				//consultationText.append("                                                                               ");
				//consultationText.append("                                                                                                          ");
				consultationText.append("\n \n\t\t" + c.getText());
				PrinterJob printerJob = PrinterJob.getPrinterJob();
				/* Build a book containing pairs of page painters (Printables)
				 * and PageFormats. This example has a single page containing
				 * text.
				 */
				Book book = new Book();
				PageFormat format = new PageFormat();
				PrintText textToPrint = 
					new PrintText(consultationText.toString(), format, 0);
				
				for (int i = 0; i < textToPrint.getNumberOfPages(); i++) {
					//textToPrint.setPageNumber(i);
					book.append(textToPrint, format);
					textToPrint = 
						new PrintText(consultationText.toString(), format, i + 1);
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
		if (analysisUI != null) {
			if (e.getSource() == analysisUI.getAddAnalysisButton()) {
				// the user wants to add a new consultation
				System.out.println("ADDING AN ANALYSIS");
				try {
					if (
						(analysisUI.getNewAnalysisSpecialty() == null) ||
						(analysisUI.getNewAnalysisSpecialty().length() <= 1) ||
						(analysisUI.getNewAnalysisText() == null) ||
						(analysisUI.getNewAnalysisText().length() <= 1)
					) {
						throw new Exception();
					}
					Patient patient = analysisUI.getPatient();
					Analysis analysis =
						new Analysis(
							analysisUI.getNewAnalysisDate(),
							analysisUI.getNewAnalysisText(),
							analysisUI.getNewAnalysisSpecialty(),
							analysisUI.getNewAnalysisAmount()
						);
					Doctor doctor = analysisUI.getNewAnalysisDoctor();
					analysis.setDoctor(doctor);
					addAnalysis(analysis, patient);
					analysisUI.addAnalysis(analysis);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(
						analysisUI, 
						"Eroare ! Ati introdus toate campurile ?"
					);
					ex.printStackTrace();
				}
			} else if (e.getSource() == analysisUI.getClearFieldsButton()) {
				analysisUI.clearFields();
			} else if (e.getSource() == analysisUI.getRemoveAnalysisButton()) {
				try {
					int confirmation = JOptionPane.showConfirmDialog(
						analysisUI,
						"Doriti sa stergeti aceasta analiza ?"
					);
					if (confirmation == JOptionPane.YES_OPTION) {
						Analysis c = analysisUI.getCurrentAnalysis();
						removeAnalysis(c, analysisUI.getPatient());
						analysisUI.removeAnalysis(c);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else if (e.getSource() == analysisUI.getPrintButton()) {
				// print the current consultation
				Analysis a = analysisUI.getCurrentAnalysis();
				StringBuffer analysisText = new StringBuffer(); //= c.getText();
				analysisText.append(PrintText.logo);
				//consultationText.append("                                                                               ");
				//consultationText.append("                                                                                                          ");
				analysisText.append("\n \n\t\tAnaliza pentru pacientul: ");
				analysisText.append(analysisUI.getPatient().getName());
				//consultationText.append("                                                                               ");
				analysisText.append("\n\t\tData: " + a.getDay() + "-" + a.getMonth() + "-" + a.getYear());
				//consultationText.append("                                                                               ");
				analysisText.append("\n\t\t" + a.getSpecialty());
				//consultationText.append("                                                                                                          ");
				analysisText.append("\n\tDoctor: " + a.getDoctor().getName());
				//consultationText.append("                                                                               ");
				//consultationText.append("                                                                                                          ");
				analysisText.append("\n \n\t\t" + a.getText());
				PrinterJob printerJob = PrinterJob.getPrinterJob();
				/* Build a book containing pairs of page painters (Printables)
				 * and PageFormats. This example has a single page containing
				 * text.
				 */
				Book book = new Book();
				PageFormat format = new PageFormat();
				PrintText textToPrint = 
					new PrintText(analysisText.toString(), format, 0);
				
				for (int i = 0; i < textToPrint.getNumberOfPages(); i++) {
					//textToPrint.setPageNumber(i);
					book.append(textToPrint, format);
					textToPrint = 
						new PrintText(analysisText.toString(), format, i + 1);
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
		if (patientUI != null) {
			if (e.getSource() == patientUI.getSearchButton()) {
				// search patient by name
				String patientName = patientUI.getPatientName();
				try {
					ArrayList patients = getPatientsForName(patientName);
					patientUI.setPatients(patients);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else if (e.getSource() == patientUI.getShowAllPatientsButton()) {
				try {
					ArrayList patients = getPatients();
					patientUI.setPatients(patients);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else if (e.getSource() == patientUI.getAddButton()) {
				// add a new patient
				Patient patient = patientUI.getNewPatient();
				// set the unique id to this patient
				try {
					int id = -1;
					id = getSequenceID("patients");
					patient.setId(id);
					if (id >= Integer.MAX_VALUE - 1) throw new Exception("out of bounds, starting from 0");
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(
						patientUI, 
						"Atentie: ati atins maximul admis de pacienti, este posibil sa pierdeti date"
					);
				}
				
				try {
					addPatient(patient);
					patientUI.addPatient(patient);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(
						patientUI, 
						"Eroare ! Ati introdus toate campurile ?"
					);
					ex.printStackTrace();
				}
			} else if (e.getSource() == patientUI.getRemoveButton()) {
				Patient patient = patientUI.getSelectedPatient();
				try {
					int confirmation = JOptionPane.showConfirmDialog(
							patientUI,
							"Doriti sa stergeti acest pacient ?"
						);
					if (confirmation == JOptionPane.YES_OPTION) {
						if (patient != null) {
							removePatient(patient);
							patientUI.removePatient(patient);
						}
					}
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			} else if (e.getSource() == patientUI.getEditPatientButton()) {
				Patient oldPatient = patientUI.getSelectedPatient();
				Patient newPatient = patientUI.getUpdatedPatient();
				try {
					if ((oldPatient != null) && (newPatient != null)) {
						updatePatient(oldPatient, newPatient);
						patientUI.updatePatient(oldPatient, newPatient);
					}
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		if (statisticsUI != null) {
			if (e.getSource() == statisticsUI.getStatisticsButton()) {
				
				try {
					String key = statisticsUI.getMonth() + "-" + statisticsUI.getYear();
					
					if (
						(allConsultations == null) || (allConsultations.size() == 0) ||
						(statisticsUI.getMonth() != oldMonth) ||
						(statisticsUI.getYear() != oldYear)
					) {
						if (consultationsForMonths.get(key) != null) {
							// this is a month already retrieved, the consultations are cached
							patientsForConsultations = (ArrayList)patientsConsultationsForMonths.get(key);
							allConsultations = (ArrayList)consultationsForMonths.get(key);
						} else {
							patientsForConsultations = new ArrayList();
							allConsultations = getConsultations(
								statisticsUI.getMonth(),
								statisticsUI.getYear(),
								patientsForConsultations
							);
							patientsConsultationsForMonths.put(key, patientsForConsultations);
							consultationsForMonths.put(key, allConsultations);
						}
					}
					statisticsUI.setConsultationsAndPatients(allConsultations, patientsForConsultations);

					if (
						(allAnalyses == null) || (allAnalyses.size() == 0) ||
						(statisticsUI.getMonth() != oldMonth) ||
						(statisticsUI.getYear() != oldYear)
					) {
						if (analysesForMonths.get(key) != null) {
							// this is a month already retrieved, the analyses are cached
							patientsForAnalyses = (ArrayList)patientsAnalysesForMonths.get(key);
							allAnalyses = (ArrayList)analysesForMonths.get(key);
						} else {
							patientsForAnalyses = new ArrayList();
							allAnalyses = getAnalyses(
								statisticsUI.getMonth(),
								statisticsUI.getYear(),
								patientsForAnalyses
							);
							patientsAnalysesForMonths.put(key, patientsForAnalyses);
							analysesForMonths.put(key, allAnalyses);
						}
					}
					statisticsUI.setAnalysesAndPatients(allAnalyses, patientsForAnalyses);
					
					oldMonth = statisticsUI.getMonth();
					oldYear = statisticsUI.getYear();
					
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(
						consultationUI, 
						"Eroare ! Ati introdus luna si anul ?"
					);
					ex.printStackTrace();
				}
			}
		}
		if (doctorUI != null) {
			if (e.getSource() == doctorUI.getAddButton()) {
				// add a new doctor
				Doctor doctor = doctorUI.getNewDoctor();
				// set the unique id to this doctor
				try {
					int id = -1;
					id = getSequenceID("doctors");
					doctor.setId(id);
					if (id >= Integer.MAX_VALUE - 1) throw new Exception("out of bounds, starting from 0");
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(
						patientUI, 
						"Atentie: ati atins maximul admis de doctori, este posibil sa pierdeti date"
					);
				}
				
				try {
					addDoctor(doctor);
					doctorUI.addDoctor(doctor);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else if (e.getSource() == doctorUI.getRemoveButton()) {
				Doctor doctor = doctorUI.getSelectedDoctor();
				try {
					int confirmation = JOptionPane.showConfirmDialog(
							doctorUI,
							"Doriti sa stergeti acest doctor ?"
						);
					if (confirmation == JOptionPane.YES_OPTION) {
						if (doctor != null) {
							removeDoctor(doctor);
							doctorUI.removeDoctor(doctor);
						}
					}
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			} else if (e.getSource() == doctorUI.getEditDoctorButton()) {
				Doctor oldDoctor = doctorUI.getSelectedDoctor();
				Doctor newDoctor = doctorUI.getUpdatedDoctor();
				try {
					if ((oldDoctor != null) && (newDoctor != null)) {
						updateDoctor(oldDoctor, newDoctor);
						doctorUI.updateDoctor(oldDoctor, newDoctor);
					}
				} catch(Exception ex) {
					ex.printStackTrace();
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
			////System.out.println ("	columns = " + columnCount);
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
				////System.out.println("Record: " + recordIndex);
				for (int i = 1; i <= columnCount; i++) {
					////System.out.print(rst.getMetaData().getColumnName(i));
					////System.out.print(": ");
					////System.out.print(rst.getString(i) + " ");
					row.add(rst.getString(i));
				}
				data.add(row);			
				////System.out.println();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return data;		
	}
	
	private ArrayList performOperation(String sql) throws Exception {
		////System.out.println ("Database = " + dbname.toString());
		////System.out.println ("Command = " + sql);
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
				////System.out.println (" .... finalizing ....");
				if (rst != null) rst.close();
				if (stmt != null) stmt.close();
				conn.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			////System.out.println ("----" + ex.getStackTrace() + "----");
			////System.out.println ("^^^^" + ex.getMessage() + "^^^^");
			////System.out.println ("====" + ex.toString() + "====");
			throw ex;
		}
		return data;
	}
	
	private void performUpdate(String sql) throws Exception {
		//System.out.println ("Database = " + dbname.toString());
		System.out.println ("Command = " + sql);
		try {
			Connection conn;
			Class.forName("org.firebirdsql.jdbc.FBDriver");
			conn = DriverManager.getConnection(dbname.toString(), "sysdba", "masterkey");
	
			Statement stmt = null;
			try {
				stmt = conn.createStatement();
				stmt.executeUpdate(sql);			
			} finally {
				// close the database resources immediately, rather than waiting
				// for the finalizer to kick in later
				//System.out.println (" .... finalizing ....");
				if (stmt != null) stmt.close();
				conn.close();
			}
		} catch (Exception ex) {
			//ex.printStackTrace();
			////System.out.println ("----" + ex.getStackTrace() + "----");
			////System.out.println ("^^^^" + ex.getMessage() + "^^^^");
			////System.out.println ("====" + ex.toString() + "====");
			throw ex;
		}
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
			System.out.println(crtId);
			
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
				System.out.println(birthDate);
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
		System.out.println("Callback getPatientsForName(), thread = " + Thread.currentThread().getName() + " " + Thread.currentThread().hashCode());
		return patients;
	}
	
	public void addPatient(Patient patient) throws Exception {
		/*
		String sql = "INSERT INTO " + tablePatients + " VALUES ('" +
			patient.getId() + "', '" + patient.getName() + "', '" +
			patient.getSex() + "', '" + patient.getBirthDate().getDate() + "', '" +
			(patient.getBirthDate().getMonth() + 1) + "', '" + 
			(patient.getBirthDate().getYear() + 1900) + "', '" +
			patient.getAddress() + "');";
		performUpdate(sql);
		*/
		insertPatient.clearParameters();
		insertPatient.setInt(1, patient.getId());
		insertPatient.setString(2, patient.getName());
		insertPatient.setString(3, new String(patient.getSex() + ""));
		insertPatient.setInt(4, patient.getBirthDay());
		insertPatient.setInt(5, patient.getBirthMonth());
		insertPatient.setInt(6, patient.getBirthYear());
		insertPatient.setString(7, patient.getAddress());
		insertPatient.executeUpdate();
		
		for (int i = 0; i < patient.getPhoneNumbers().size(); i++) {
			/*
			String sqlPhone = "INSERT INTO " + tablePatientPhones + " VALUES ('" +
				patient.getId() + "', '" + patient.getPhoneNumbers().get(i) + "');";
			performUpdate(sqlPhone);
			*/
			insertPatientPhone.clearParameters();
			insertPatientPhone.setInt(1, patient.getId());
			insertPatientPhone.setString(2, (String)patient.getPhoneNumbers().get(i));
			insertPatientPhone.executeUpdate();
		}
	}
	
	public void updatePatient(Patient oldPatient, Patient newPatient) throws Exception {
		removePatient(oldPatient);
		addPatient(newPatient);
	}
	
	public void updateDoctor(Doctor oldDoctor, Doctor newDoctor) throws Exception {
		removeDoctor(oldDoctor);
		addDoctor(newDoctor);
	}
	
	public void addDoctor(Doctor doctor) throws Exception {
		/*
		String sql = "INSERT INTO " + tableDoctors + " VALUES ('" +
			doctor.getId() + "', '" + doctor.getName() + "', '" +
			doctor.getSex() + "', '" + doctor.getBirthDate().getDate() + "', '" +
			(doctor.getBirthDate().getMonth() + 1) + "', '" + 
			(doctor.getBirthDate().getYear() + 1900) + "', '" +
			doctor.getAddress() + "');";
		performUpdate(sql);
		*/
		insertDoctor.clearParameters();
//		int id = -1;
//		try {
//			id = getSequenceID("doctors");
//		} catch (Exception e) {
//			id = doctor.getId();
//		}
//		insertDoctor.setInt(1, id);
		insertDoctor.setInt(1, doctor.getId());
		insertDoctor.setString(2, doctor.getName());
		insertDoctor.setString(3, new String(doctor.getSex() + ""));
		insertDoctor.setInt(4, doctor.getBirthDay());
		insertDoctor.setInt(5, doctor.getBirthMonth());
		insertDoctor.setInt(6, doctor.getBirthYear());
		insertDoctor.setString(7, doctor.getAddress());
		insertDoctor.executeUpdate();
		for (int i = 0; i < doctor.getPhoneNumbers().size(); i++) {
			/*
			String sqlPhone = "INSERT INTO " + tableDoctorPhones + " VALUES ('" +
				doctor.getId() + "', '" + doctor.getPhoneNumbers().get(i) + "');";
			performUpdate(sqlPhone);
			*/
			insertDoctorPhone.clearParameters();
			insertDoctorPhone.setInt(1, doctor.getId());
			insertDoctorPhone.setString(2, (String)doctor.getPhoneNumbers().get(i));
			insertDoctorPhone.executeUpdate();			
		}
		addDoctorCabinet(doctor);
	}
	
	private void addDoctorCabinet(Doctor doctor) throws Exception {
		if (doctor.getCabinet() != null) {
			insertDoctorCabinet.clearParameters();
			insertDoctorCabinet.setInt(1, doctor.getId());
			insertDoctorCabinet.setString(2, doctor.getCabinet());
			insertDoctorCabinet.executeUpdate();
		}
	}
	
	public ArrayList getConsultationsForPatient(Patient patient) throws Exception {
		/*
		String sql = "SELECT * FROM " + tableConsultations + 
			" WHERE patient_id='" + patient.getId() + "';";
		ArrayList data = performOperation(sql);
		*/
		
		System.out.println("Start getConsultationsForPatient()");
		Calendar c = new GregorianCalendar(); 
		long startC = c.getTimeInMillis();
		Date startDate = new Date();
		long start = startDate.getTime();

		consultationsForPatient.clearParameters();
		consultationsForPatient.setInt(1, patient.getId());
		ResultSet result = consultationsForPatient.executeQuery();
		ArrayList data = fillInData(result);
		
		ArrayList consultations = new ArrayList();
		for (int i = 0; i < data.size(); i++) {
			ArrayList subData = (ArrayList)data.get(i);		
			// create a new Consultation
			Date consultationDate = 
				new Date(
						new Integer(((String)subData.get(3))).intValue() - 1900,
						new Integer(((String)subData.get(2))).intValue() - 1,
						new Integer(((String)subData.get(1))).intValue()
				);
			System.out.println(consultationDate);
			Consultation consultation = 
				new Consultation(
						consultationDate, 
						(String)(subData.get(4)),
						(String)(subData.get(5)),
						new Float((String)(subData.get(7))).floatValue()
				);
			// find the doctor who made the consultation
			//String sqlDoctor = 
			//	"SELECT * FROM " + tableDoctors + " WHERE doctor_id='" + 
			//	(String)(subData.get(6)) + "';";
			selectDoctors.clearParameters();
			selectDoctors.setInt(1, new Integer((String)subData.get(6)).intValue());
			//ArrayList dataDoctor = performOperation(sqlDoctor);
			ResultSet resultDoctor = selectDoctors.executeQuery();
			ArrayList dataDoctor = fillInData(resultDoctor);
			
			Doctor doctor = null;
			for (int j = 0; j < dataDoctor.size(); j++) {
				ArrayList subDataDoctor = (ArrayList)dataDoctor.get(j);
				Date birthDate = 
					new Date(
							new Integer(((String)subDataDoctor.get(5))).intValue() - 1900,
							new Integer(((String)subDataDoctor.get(4))).intValue() - 1,
							new Integer(((String)subDataDoctor.get(3))).intValue()
					);
				System.out.println(birthDate);
				doctor = 
					new Doctor(
							(String)(subDataDoctor.get(1)), 
							((String)(subDataDoctor.get(2))).charAt(0),
							birthDate,
							(String)(subDataDoctor.get(6))
					);
				doctor.setId(new Integer(((String)subDataDoctor.get(0))).intValue());
				break;
			}
			if (doctor != null) {
				consultation.setDoctor(doctor);
			}
			consultations.add(consultation);
		}
		
		c = new GregorianCalendar();
		long endC = c.getTimeInMillis();
		Date endDate = new Date();
		long end = endDate.getTime();
		System.out.println("End getConsultationsForPatient() : " + (end - start) + " (with Date)");
		System.out.println("End getConsultationsForPatient() : " + (endC - startC) + " (with Calendar)");
		
		return consultations;
	}
	
	public ArrayList getAnalysesForPatient(Patient patient) throws Exception {
		/*
		String sql = "SELECT * FROM " + tableAnalyses + 
			" WHERE patient_id='" + patient.getId() + "';";
		ArrayList data = performOperation(sql);
		*/
		analysesForPatient.clearParameters();
		analysesForPatient.setInt(1, patient.getId());
		ResultSet result = analysesForPatient.executeQuery();
		ArrayList data = fillInData(result);
		
		ArrayList analyses = new ArrayList();
		for (int i = 0; i < data.size(); i++) {
			ArrayList subData = (ArrayList)data.get(i);		
			// create a new Consultation
			Date analysisDate = 
				new Date(
						new Integer(((String)subData.get(3))).intValue() - 1900,
						new Integer(((String)subData.get(2))).intValue() - 1,
						new Integer(((String)subData.get(1))).intValue()
				);
			System.out.println(analysisDate);
			Analysis analysis = 
				new Analysis(
						analysisDate, 
						(String)(subData.get(4)),
						(String)(subData.get(5)),
						new Float((String)(subData.get(7))).floatValue()
				);
			// find the doctor who made the analysis
			//String sqlDoctor = 
			//	"SELECT * FROM " + tableDoctors + " WHERE doctor_id='" + 
			//	(String)(subData.get(6)) + "';";
			selectDoctors.clearParameters();
			selectDoctors.setInt(1, new Integer((String)subData.get(6)).intValue());
			//ArrayList dataDoctor = performOperation(sqlDoctor);
			ResultSet resultDoctor = selectDoctors.executeQuery();
			ArrayList dataDoctor = fillInData(resultDoctor);
			
			//ArrayList dataDoctor = performOperation(sqlDoctor);
			Doctor doctor = null;
			for (int j = 0; j < dataDoctor.size(); j++) {
				ArrayList subDataDoctor = (ArrayList)dataDoctor.get(j);
				Date birthDate = 
					new Date(
							new Integer(((String)subDataDoctor.get(5))).intValue() - 1900,
							new Integer(((String)subDataDoctor.get(4))).intValue() - 1,
							new Integer(((String)subDataDoctor.get(3))).intValue()
					);
				System.out.println(birthDate);
				doctor = 
					new Doctor(
							(String)(subDataDoctor.get(1)), 
							((String)(subDataDoctor.get(2))).charAt(0),
							birthDate,
							(String)(subDataDoctor.get(6))
					);
				doctor.setId(new Integer(((String)subDataDoctor.get(0))).intValue());
				break;
			}
			if (doctor != null) {
				analysis.setDoctor(doctor);
			}
			analyses.add(analysis);
		}
		return analyses;
	}
	
	public void addConsultation(Consultation consultation, Patient patient) throws Exception {
		/*
		String sql = "INSERT INTO " + tableConsultations + " VALUES ('" +
			patient.getId() + "', '" + consultation.getDate().getDate() + "', '" +
			(consultation.getDate().getMonth() + 1) + "', '" + 
			(consultation.getDate().getYear() + 1900) + "', '" +
			consultation.getText() + "', '" + consultation.getSpecialty() + "', '" +
			consultation.getDoctor().getId() + "', '" + consultation.getAmount() + "');";
		performUpdate(sql);	
		*/
		insertConsultation.clearParameters();
		insertConsultation.setInt(1, patient.getId());
		insertConsultation.setInt(2, consultation.getDay());
		insertConsultation.setInt(3, consultation.getMonth());
		insertConsultation.setInt(4, consultation.getYear());
		insertConsultation.setString(5, consultation.getText());
		insertConsultation.setString(6, consultation.getSpecialty());
		insertConsultation.setInt(7, consultation.getDoctor().getId());
		insertConsultation.setFloat(8, consultation.getAmount());
		insertConsultation.executeUpdate();
	}
	
	public void addAnalysis(Analysis analysis, Patient patient) throws Exception {
		/*
		String sql = "INSERT INTO " + tableAnalyses + " VALUES ('" +
			patient.getId() + "', '" + analysis.getDate().getDate() + "', '" +
			(analysis.getDate().getMonth() + 1) + "', '" + 
			(analysis.getDate().getYear() + 1900) + "', '" +
			analysis.getText() + "', '" + analysis.getSpecialty() + "', '" +
			analysis.getDoctor().getId() + "', '" + analysis.getAmount() + "');";
		performUpdate(sql);	
		*/
		insertAnalysis.clearParameters();
		insertAnalysis.setInt(1, patient.getId());
		insertAnalysis.setInt(2, analysis.getDay());
		insertAnalysis.setInt(3, analysis.getMonth());
		insertAnalysis.setInt(4, analysis.getYear());
		insertAnalysis.setString(5, analysis.getText());
		insertAnalysis.setString(6, analysis.getSpecialty());
		insertAnalysis.setInt(7, analysis.getDoctor().getId());
		insertAnalysis.setFloat(8, analysis.getAmount());
		insertAnalysis.executeUpdate();
	}
	
	public void removeConsultation(Consultation consultation, Patient patient) throws Exception {
		/*
		String sql = "DELETE FROM " + tableConsultations + " WHERE patient_id='" + 
			patient.getId() + "' AND doctor_id='" + consultation.getDoctor().getId() + 
			"' AND date_day='" + consultation.getDate().getDate() +
			"' AND date_month='" + (consultation.getDate().getMonth() + 1) +
			"' AND date_year='" + (consultation.getDate().getYear() + 1900) +
			"' AND specialty='" + consultation.getSpecialty() + "';";
		performUpdate(sql);
		*/
		removeConsultation.clearParameters();
		removeConsultation.setInt(1, patient.getId());
		removeConsultation.setInt(2, consultation.getDoctor().getId());
		removeConsultation.setInt(3, consultation.getDay());
		removeConsultation.setInt(4, consultation.getMonth());
		removeConsultation.setInt(5, consultation.getYear());
		removeConsultation.setString(6, consultation.getSpecialty());
		removeConsultation.executeUpdate();
	}

	public void removeAnalysis(Analysis analysis, Patient patient) throws Exception {
		/*
		String sql = "DELETE FROM " + tableAnalyses + " WHERE patient_id='" + 
			patient.getId() + "' AND doctor_id='" + analysis.getDoctor().getId() + 
			"' AND date_day='" + analysis.getDate().getDate() +
			"' AND date_month='" + (analysis.getDate().getMonth() + 1) +
			"' AND date_year='" + (analysis.getDate().getYear() + 1900) +
			"' AND specialty='" + analysis.getSpecialty() + "';";
		performUpdate(sql);
		*/
		removeAnalysis.clearParameters();
		removeAnalysis.setInt(1, patient.getId());
		removeAnalysis.setInt(2, analysis.getDoctor().getId());
		removeAnalysis.setInt(3, analysis.getDay());
		removeAnalysis.setInt(4, analysis.getMonth());
		removeAnalysis.setInt(5, analysis.getYear());
		removeAnalysis.setString(6, analysis.getSpecialty());
		removeAnalysis.executeUpdate();
	}

	public void removePatient(Patient patient) throws Exception {
		/*
		String sql = "DELETE FROM " + tablePatients + " WHERE patient_id='" + 
			patient.getId() + "';";
		performUpdate(sql);
		sql = "DELETE FROM " + tablePatientPhones + " WHERE patient_id='" +
			patient.getId() + "';";
		performUpdate(sql);
		*/
		removePatient.clearParameters();
		removePatient.setInt(1, patient.getId());
		removePatient.executeUpdate();
		removePatientPhone.clearParameters();
		removePatientPhone.setInt(1, patient.getId());
		removePatientPhone.executeUpdate();
		// Also remove the consultations for that patient ? Or remove them
		// only when the doctor is removed ?
	}

	public void removeDoctor(Doctor doctor) throws Exception {
		/*
		String sql = "DELETE FROM " + tablePatients + " WHERE patient_id='" + 
			patient.getId() + "';";
		performUpdate(sql);
		sql = "DELETE FROM " + tablePatientPhones + " WHERE patient_id='" +
			patient.getId() + "';";
		performUpdate(sql);
		*/
		removeDoctor.clearParameters();
		removeDoctor.setInt(1, doctor.getId());
		removeDoctor.executeUpdate();
		removeDoctorPhone.clearParameters();
		removeDoctorPhone.setInt(1, doctor.getId());
		removeDoctorPhone.executeUpdate();
		// Also remove the consultations for that patient ? Or remove them
		// only when the doctor is removed ?
	}
	
	public ArrayList getPatients() throws Exception {
		/*
		String sql = "SELECT * FROM " + tablePatients + " LEFT JOIN " + tablePatientPhones +
			" ON " + tablePatients + ".patient_id=" + tablePatientPhones + ".patient_id" +
			" ORDER BY " + tablePatients + ".patient_id ASC;";
		ArrayList data = performOperation(sql);
		*/
		System.out.println("Start getPatients()");
		Calendar c = new GregorianCalendar(); 
		long startC = c.getTimeInMillis();
		Date startDate = new Date();
		long start = startDate.getTime();

		ResultSet result = selectPatients.executeQuery();
		ArrayList data = fillInData(result);
		
		ArrayList patients = new ArrayList();
		int crtId, prevId = -1;
		Patient patient = null;
		for (int i = 0; i < data.size(); i++) {
			ArrayList subData = (ArrayList)data.get(i);
			crtId = new Integer(((String)subData.get(0))).intValue();
			System.out.println(crtId);
			
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
				System.out.println(birthDate);
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
		c = new GregorianCalendar();
		long endC = c.getTimeInMillis();
		Date endDate = new Date();
		long end = endDate.getTime();
		System.out.println("End getPatients() : " + (end - start) + " (with Date)");
		System.out.println("End getPatients() : " + (endC - startC) + " (with Calendar)");
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
			System.out.println(crtId);
			
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
				System.out.println(birthDate);
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

	public ArrayList getConsultations(int month, int year, ArrayList patients) throws Exception {
		/*
		String sql = "SELECT * FROM " + tableDoctors + " LEFT JOIN " + tableConsultations +
			" ON " + tableDoctors + ".doctor_id=" + tableConsultations + ".doctor_id" +
			" LEFT JOIN " + tablePatients + " ON " + tableConsultations + ".patient_id=" + 
			tablePatients + ".patient_id " + 
			" WHERE (" + tableConsultations + ".date_month='" + month + "' AND " +
			tableConsultations + ".date_year='" + year + "')" +
			" ORDER BY " + tableDoctors + ".name ASC;";
			//" ORDER BY " + tableConsultations + ".date_day ASC;";
		ArrayList data = performOperation(sql);
		*/
		System.out.println("Start getConsultations()");
		Calendar c = new GregorianCalendar(); 
		long startC = c.getTimeInMillis();
		Date startDate = new Date();
		long start = startDate.getTime();
		
		consultationsPerMonth.clearParameters();
		consultationsPerMonth.setInt(1, month);
		consultationsPerMonth.setInt(2, year);
		ResultSet result = consultationsPerMonth.executeQuery();
		ArrayList data = fillInData(result);
		
		ArrayList consultations = new ArrayList();
		
		for (int i = 0; i < data.size(); i++) {
			Consultation consultation = null;
			Patient patient = null;
			Doctor doctor = null;
			ArrayList subData = (ArrayList)data.get(i);
			
			for (int j = 0; j < subData.size(); j++) {
				System.out.print(subData.get(j) + " ");
			}
			System.out.println();
			Date birthDate = null;
			
			try {
				// create a new Doctor
				birthDate = 
					new Date(
							new Integer(((String)subData.get(5))).intValue() - 1900,
							new Integer(((String)subData.get(4))).intValue() - 1,
							new Integer(((String)subData.get(3))).intValue()
					);
			} catch (Exception e) {
				birthDate = null;
			}
			
			try {
				doctor = new Doctor(
					(String)subData.get(1),
					((String)subData.get(2)).charAt(0),
					birthDate,
					(String)subData.get(6)
				);
				doctor.setId(new Integer((String)subData.get(0)).intValue());
			} catch (Exception e) {
				doctor = null;
			}
			
			Date date = null;
			try {
				// create a new Consultation
				date = 
					new Date(
							new Integer(((String)subData.get(10))).intValue() - 1900,
							new Integer(((String)subData.get(9))).intValue() - 1,
							new Integer(((String)subData.get(8))).intValue()
					);
			} catch (Exception e) {
				date = null;
			}
			
			try {
				consultation = new Consultation(
					date,
					(String)subData.get(11),
					(String)subData.get(12),
					new Float((String)subData.get(14)).floatValue()
				);
				if (doctor != null)
					consultation.setDoctor(doctor);
			} catch (Exception e) {
				consultation = null;
			}
			
			try {
				// create a new Patient
				birthDate = 
					new Date(
							new Integer(((String)subData.get(20))).intValue() - 1900,
							new Integer(((String)subData.get(19))).intValue() - 1,
							new Integer(((String)subData.get(18))).intValue()
					);
			} catch (Exception e) {
				birthDate = null;
			}
			
			try {
				patient = new Patient(
					(String)subData.get(16),
					((String)subData.get(17)).charAt(0),
					birthDate,
					(String)subData.get(21)
				);
			} catch (Exception e) {
				patient = null;
			}
			
			if (consultation != null) {
				consultations.add(consultation);
				patients.add(patient);
				System.out.println("**** PATIENT: " + patient);		
			}
		}
		
		c = new GregorianCalendar();
		long endC = c.getTimeInMillis();
		Date endDate = new Date();
		long end = endDate.getTime();
		System.out.println("End getConsultations() : " + (end - start) + " (with Date)");
		System.out.println("End getConsultations() : " + (endC - startC) + " (with Calendar)");
		return consultations;
	}
	
	public ArrayList getAnalyses(int month, int year, ArrayList patients) throws Exception {
		/*
		String sql = "SELECT * FROM " + tableDoctors + " LEFT JOIN " + tableAnalyses +
			" ON " + tableDoctors + ".doctor_id=" + tableAnalyses + ".doctor_id" +
			" LEFT JOIN " + tablePatients + " ON " + tableAnalyses + ".patient_id=" + 
			tablePatients + ".patient_id " + 
			" WHERE (" + tableAnalyses + ".date_month='" + month + "' AND " +
			tableAnalyses + ".date_year='" + year + "')" +
			" ORDER BY " + tableDoctors + ".name ASC;";
			//" ORDER BY " + tableAnalyses + ".date_day ASC;";
		ArrayList data = performOperation(sql);
		*/
		analysesPerMonth.clearParameters();
		analysesPerMonth.setInt(1, month);
		analysesPerMonth.setInt(2, year);
		ResultSet result = analysesPerMonth.executeQuery();
		ArrayList data = fillInData(result);
		ArrayList analyses = new ArrayList();
		
		for (int i = 0; i < data.size(); i++) {
			Analysis analysis = null;
			Patient patient = null;
			ArrayList subData = (ArrayList)data.get(i);
			for (int j = 0; j < subData.size(); j++) {
				System.out.print(subData.get(j) + " ");
			}
			System.out.println();

			Date birthDate = null;
			// create a new Doctor
			try {
				birthDate = 
					new Date(
							new Integer(((String)subData.get(5))).intValue() - 1900,
							new Integer(((String)subData.get(4))).intValue() - 1,
							new Integer(((String)subData.get(3))).intValue()
					);
			} catch (Exception e) {
				birthDate = null;
			}
			
			Doctor doctor = null;
			try {
				doctor = new Doctor(
					(String)subData.get(1),
					((String)subData.get(2)).charAt(0),
					birthDate,
					(String)subData.get(6)
				);
				doctor.setId(new Integer((String)subData.get(0)).intValue());
			} catch (Exception e) {
				doctor = null;
			}
			
			// create a new Analysis
			Date date = null;
			try {
				date = 
					new Date(
							new Integer(((String)subData.get(10))).intValue() - 1900,
							new Integer(((String)subData.get(9))).intValue() - 1,
							new Integer(((String)subData.get(8))).intValue()
					);
			} catch (Exception e) {
				date = null;
			}
			
			try {
				analysis = new Analysis(
					date,
					(String)subData.get(11),
					(String)subData.get(12),
					new Float((String)subData.get(14)).floatValue()
				);
				if (doctor != null) analysis.setDoctor(doctor);
			} catch (Exception e) {
				analysis = null;
			}
			
			try {
				// create a new Patient
				birthDate = 
					new Date(
							new Integer(((String)subData.get(20))).intValue() - 1900,
							new Integer(((String)subData.get(19))).intValue() - 1,
							new Integer(((String)subData.get(18))).intValue()
					);
			} catch (Exception e) {
				birthDate = null;
			}
			
			try {
				patient = new Patient(
					(String)subData.get(16),
					((String)subData.get(17)).charAt(0),
					birthDate,
					(String)subData.get(21)
				);
			} catch (Exception e) {
				patient = null;
			}
				
			if (analysis != null) {
				analyses.add(analysis);
				patients.add(patient);
			}
			System.out.println("**** ANALYSIS - PATIENT: " + patient);
			
		}
		return analyses;
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
	
//	private int getMaxPatientID() throws Exception {
//		ResultSet result = selectPatientIDs.executeQuery();
//		ArrayList data = fillInData(result);
//		for (int i = 0; i < data.size(); i++) {
//			ArrayList subData = (ArrayList)data.get(i);
//			for (int j = 0; j < subData.size(); j++) {
//				System.out.print(subData.get(j) + " ");
//			}
//			System.out.println();
//		}
//		return 1;
//	}
	
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
		PatientEditor pe = new PatientEditor(Configurer.getConfiguration().getServerName());
		try {
			/*
			ArrayList patients = pe.getPatientsForName("Gogu Gogescu");
			for (int i = 0; i < patients.size(); i++) {
				System.out.println((Patient)patients.get(i));
				ArrayList consultations = 
					pe.getConsultationsForPatient((Patient)patients.get(i));
				for (int j = 0; j < consultations.size(); j++) {
					System.out.println("    " + (Consultation)(consultations.get(j)));
				}
				Consultation c = new Consultation(new Date(1999, 13, 1), "yet another consultation", "ORL", (float)122.5);
				Doctor d = new Doctor("Ion Ionescu", 'm', new Date(1955, 13, 3), "address, etc.");
				d.setId(176018);
				c.setDoctor(d);
				pe.removeConsultation(c, (Patient)patients.get(i));
			}
			Patient p = new Patient("Cucu Rigu", 'm', new Date(1991, 11, 3), "Adresa lui Cucu");
			p.setId(817657);
			pe.removePatient(p);
			*/
			//ArrayList patients = new ArrayList();
			//pe.getConsultations(3, 2006, patients);
			
			//pe.getMaxPatientID();
//			int id = PatientEditor.getSequenceID("patients");
//			System.out.println(id);
//			id = PatientEditor.getSequenceID("doctors");
//			System.out.println(id);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
