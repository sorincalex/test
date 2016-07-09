import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import configuration.Configurer;

/*
 * Created on Mar 6, 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author Sorin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Authentication extends JFrame implements ActionListener {

	private JLabel passwordLabel;
	private JPasswordField password;
	private JButton go;
	
	public Authentication() {
		super("Autentificare");
		
		/* TODO: I GET SOME UPDATE ERRORS WITH THE WINDOWS LOOK AND FEEL; CHECK THAT */
		String lf = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
		
		//String lf = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
		try {
			UIManager.setLookAndFeel(lf);
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JPanel panel = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		panel.setLayout(gridbag);
		
		c.fill = GridBagConstraints.HORIZONTAL; 
		Insets ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2; // make it 3 columns wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		passwordLabel = new JLabel("Parola: ");
		gridbag.setConstraints(passwordLabel, c);
		panel.add(passwordLabel);
		
		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2; // make it 3 columns wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		password = new JPasswordField();
		gridbag.setConstraints(password, c);
		panel.add(password);
		
		c.fill = GridBagConstraints.HORIZONTAL; 
		ins = new Insets (5, 5, 5, 5); 
		c.insets = ins;
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2; // make it 3 columns wide
		c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
		go = new JButton("Statistici");
		gridbag.setConstraints(go, c);
		panel.add(go);
		
		go.addActionListener(this);
		
		getContentPane().add(panel, BorderLayout.CENTER);
		pack();
		setVisible(true);
	}

	public boolean authenticate() {
		if (password.getText().equals("CHADI2909")) {
			return true;
		}
		return false;
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == go) {
			if (!authenticate()) {
				JOptionPane.showMessageDialog(
						this, 
						"Eroare: parola gresita"
					);
			} else {
				PatientEditor pe = new PatientEditor(Configurer.getConfiguration().getServerName());
				StatisticsUI ui = new StatisticsUI(pe);
				pe.setStatisticsUI(ui);
			}
		}
	}
	
	public static void main(String args[]) {
		Authentication a = new Authentication();
	}
}
