package lys.sepr;

import lys.sepr.ui.ApplicationWindow;

public class Application {

	public Application() {
		ApplicationWindow window = new ApplicationWindow();
		window.setVisible(true);
	}

	public static void main(String[] args) {
		Application app = new Application();
	}

}
