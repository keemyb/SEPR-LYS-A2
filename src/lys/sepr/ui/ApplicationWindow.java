package lys.sepr.ui;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ApplicationWindow extends JFrame {

	JButton newGameButton = new JButton("New Game");
	JButton loadGameButton = new JButton("Load Game");
	JButton tutorialButton = new JButton("Tutorial");
	JButton exitButton = new JButton("Exit");

	JButton settingsButton = new JButton(new ImageIcon(new ImageIcon(
			"files/gearicon.png").getImage().getScaledInstance(52, 52,
			Image.SCALE_SMOOTH)));

	JLabel titleLabel = new JLabel("World War Trains");

	JPanel menuPanel = new JPanel();

	public static final String TITLE = "World War Trains";

	public ApplicationWindow() {
		super(TITLE);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(660, 400);
		addActionListeners();

		titleLabel.setHorizontalAlignment(JLabel.CENTER);
		Font f = titleLabel.getFont();
		Font font = new Font("Courier New", Font.PLAIN, f.getSize()+6);
		Font bigFont = new Font("Courier New", Font.PLAIN, f.getSize() + 10);
		titleLabel.setFont(bigFont);
		newGameButton.setFont(font);
		loadGameButton.setFont(font);
		tutorialButton.setFont(font);
		exitButton.setFont(font);
		
		settingsButton.setFocusPainted(false);
		
		menuPanel.setLayout(null);
		menuPanel.add(titleLabel);
		menuPanel.add(newGameButton);
		menuPanel.add(loadGameButton);
		menuPanel.add(tutorialButton);
		menuPanel.add(exitButton);
		menuPanel.add(settingsButton);
		add(menuPanel);
	}

	private void addActionListeners() {
		newGameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newGame();
			}
		});
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
	}

	private void newGame() {
		// TODO - this
	}

	private void setMenuLayout() {
		int vgap = (getHeight() - 360) / 5;
		int hinset = (getWidth() - 400) / 2;
		titleLabel.setBounds(hinset, 0, 400, 60);
		newGameButton.setBounds(hinset, vgap + 60, 400, 60);
		loadGameButton.setBounds(hinset, 2 * vgap + 120, 400, 60);
		tutorialButton.setBounds(hinset, 3 * vgap + 180, 400, 60);
		exitButton.setBounds(hinset, 4 * vgap + 240, 400, 60);

		settingsButton.setBounds(getWidth() - 96, 0, 80, 60);

	}

	public void paint(Graphics g) {
		setMenuLayout();
		super.paint(g);
	}

	public static void main(String[] args) {
		new ApplicationWindow().setVisible(true);
	}
}
