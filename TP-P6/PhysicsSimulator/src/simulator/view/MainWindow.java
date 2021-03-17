package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import simulator.control.Controller;

public class MainWindow extends JFrame {
	// ...
	private Controller _ctrl;
	
	public MainWindow(Controller ctrl) {
		super("Physics Simulator");
		_ctrl = ctrl;
		initGUI();
	}
	
	private void initGUI() {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		
		setContentPane(mainPanel);
		// TODO complete this method to build the GUI
		// ..
		mainPanel.add(new ControlPanel(_ctrl), BorderLayout.PAGE_START);
		centerPanel.add(new BodiesTable(_ctrl));
		centerPanel.add(new Viewer(_ctrl));
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(new StatusBar(_ctrl), BorderLayout.PAGE_END);
		mainPanel.setOpaque(true);
		
		pack();
		setMinimumSize(getSize());
		setVisible(true);
	}
}
