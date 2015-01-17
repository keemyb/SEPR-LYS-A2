package lys.sepr;

import java.awt.Font;

import javax.swing.UIManager;

import lys.sepr.ui.ApplicationWindow;

public class Application {

	public Application() {
		//set universal fonts + button colours
		Font f = new Font("Courier New", Font.PLAIN, 12);
		UIManager.put("OptionPane.messageFont", f);
		UIManager.put("OptionPane.buttonFont", f);
		UIManager.put("Button.font", f);
		UIManager.put("ComboBox.font", f);
		ApplicationWindow window = new ApplicationWindow();
		window.setVisible(true);
	}

	public static void main(String[] args) {
		Application app = new Application();
	}

}
