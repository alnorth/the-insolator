package uk.co.alasdairnorth.insolator;

import javax.swing.JFrame;
import javax.swing.UIManager;

public class Main {

	public static void main(String[] args) {
				
		try { UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName()); }
		catch (Exception e) { }
		MainWindow mw = new MainWindow();
		mw.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mw.pack();
		mw.setVisible(true);
				
	}

}
