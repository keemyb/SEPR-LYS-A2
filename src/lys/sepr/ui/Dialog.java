package lys.sepr.ui;

import java.awt.Component;

import javax.swing.JOptionPane;

public class Dialog {

	static Component parent = null;
	
	protected static void setParent(Component parent){
		Dialog.parent = parent;
	}
	
	public static void info(String message) {
		JOptionPane.showMessageDialog(parent, message);
	}
	
	/**
	 * Displays an error message
	 * @param message - error message
	 */
	public static void error(String message) {
		JOptionPane.showMessageDialog(parent, message, "", JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * asks the user for an input
	 * @param message - message
	 * @return user input
	 * @throws NumberFormatException - if user types input which cannot be parsed to an integer
	 */
	public static int intInput(String message) throws NumberFormatException {
		return Integer.parseInt(stringInput(message));
	}
	
	/**
	 * asks the user for an input
	 * @param message - message
	 * @return user input
	 */
	public static String stringInput(String message) {
		return JOptionPane.showInputDialog(parent, message);
	}
	
}
