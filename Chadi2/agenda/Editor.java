/*
 * Created on Aug 19, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package agenda;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * @author Sorin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Editor {
	// the name of the DB used
	//public static String dbname = "c:\\Chadi\\Chadi.fdb";
	//public static String serverName = "gabisorin";

	public static void sortList(ArrayList list) {
		Collections.sort(
				list, 
				new Comparator() {
					public int compare(Object a, Object b) {
						if (a instanceof Appointment) {
							return ((Appointment)a).getTime().compareTo(((Appointment)b).getTime());
						} 
						return 0;
					}
				}
		);
	}

	public static void sortListByDoctor(ArrayList list) {
		Collections.sort(
				list, 
				new Comparator() {
					public int compare(Object a, Object b) {
						if (a instanceof Appointment) {
							return ((Appointment)a).getDoctor().getName().compareTo(((Appointment)b).getDoctor().getName());
						} 
						return 0;
					}
				}
		);
	}

	public static void sortListByCabinet(ArrayList list) {
		Collections.sort(
				list, 
				new Comparator() {
					public int compare(Object a, Object b) {
						if (a instanceof Doctor) {
							return ((Doctor)a).getCabinet().compareTo(((Doctor)b).getCabinet());
						} 
						return 0;
					}
				}
		);
	}
}
