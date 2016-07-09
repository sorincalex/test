/*
 * Created on Jun 26, 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package configuration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * @author Sorin Cristescu
 *
 * This class deals with saving and loading a list of web sites to/from an xml file.
 * 
 */
public class Configurer {
	private static DocumentBuilder builder;
	private static Configuration config;
	
	public static Configuration getConfiguration() {
		if (config != null) return config;
		
		String filePath = System.getProperty("java.class.path", ".");
		int index = filePath.indexOf(";");
		if (index != -1) filePath = filePath.substring(0, index) + "\\config.xml";
		else filePath = filePath + "\\config.xml";
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			factory.setIgnoringElementContentWhitespace(true);
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new File(filePath));
			Node root = doc.getDocumentElement();
			NodeList children = root.getChildNodes();
			String type = "";
			String path = "";
			String server = "";

			for (int i = 0; i < children.getLength(); i++) {
				if (children.item(i) instanceof Element) {
					Element child = (Element)children.item(i);
					if (child.getNodeName().equalsIgnoreCase("database")) {
						type = child.getAttribute("type");
						path = child.getAttribute("name");				
					} else if (child.getNodeName().equalsIgnoreCase("server")) {
						server = child.getAttribute("name");
					}
				}
			}
			config = new Configuration(path, type, server);
		} catch (ParserConfigurationException pe) {
			pe.printStackTrace();
		} catch (IOException fe) {
			fe.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		}
		return config;
	}
	
	public static void main(String[] args) {
		//Configurer config = new Configurer();
		Configuration c = Configurer.getConfiguration();
		System.out.println(c);
	}
}
