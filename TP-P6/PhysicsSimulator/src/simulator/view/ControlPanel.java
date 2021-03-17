package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.json.JSONObject;

import simulator.control.Controller;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

public class ControlPanel extends JPanel implements SimulatorObserver {
	private Controller _ctrl;
	private JToolBar toolbar;
	private JButton openFileButton, gLawsButton, runButton, stopButton, exitButton;
	private JLabel delayLabel, stepsLabel, dtLabel;
	private JSpinner delaySpinner, stepsSpinner;
	private JTextField dtText;
	private JFileChooser fc;
	private String[] ls;
	private volatile Thread _thread;
	
	ControlPanel(Controller ctrl) {
		_ctrl = ctrl;
		initGUI();
		_ctrl.addObserver(this);
	}
	
	private void initGUI() {
		toolbar = new JToolBar();
		add(toolbar);
		
		fc = new JFileChooser("resources/examples");
		
		// Button for opening files
		openFileButton = new JButton();
		openFileButton.setLocation(0,0);
		openFileButton.setIcon(new ImageIcon("resources/icons/open.png"));
		openFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// JFileChooser part
				chooseFile();
			}
		});
		
		// Needed information for gLawsButton
		ls = new String[_ctrl.getGravityLawsFactory().getInfo().size()];
		
		int i = 0;
		for (JSONObject j : _ctrl.getGravityLawsFactory().getInfo()) {
			ls[i] = j.getString("desc") + " (" + j.getString("type") +")";
			++i;
		}
		
		// Button for setting gravity laws
		gLawsButton = new JButton();
		gLawsButton.setLocation(0,0);
		gLawsButton.setIcon(new ImageIcon("resources/icons/physics.png"));
		gLawsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// action when clicked
				chooseLaw();
			}
		});
		
		// Button for running the simulator
		runButton = new JButton();
		runButton.setLocation(0,0);
		runButton.setIcon(new ImageIcon("resources/icons/run.png"));
		runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// action when clicked
				runAction();
			}
		});
		
		// Button for stopping the simulator
		stopButton = new JButton();
		stopButton.setLocation(0,0);
		stopButton.setIcon(new ImageIcon("resources/icons/stop.png"));
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// action when clicked
				stopAction();
			}
		});
		
		// Label for delay
		delayLabel = new JLabel("Steps:");
		
		// Spinner for delay
		delaySpinner = new JSpinner(new SpinnerNumberModel(10,0,1000,1));
		
		// Label for steps
		stepsLabel = new JLabel("Steps:");
		
		// Spinner for steps
		stepsSpinner = new JSpinner(new SpinnerNumberModel(10000,0,100000,100));
		
		// Label for delta-time
		dtLabel = new JLabel("Delta-Time:");
		
		// Text field for delta-time
		dtText = new JTextField("2500", 7);
		dtText.setHorizontalAlignment(JTextField.RIGHT);
		
		// Button for exiting the program
		exitButton = new JButton();
		exitButton.setLocation(0, 0);
		exitButton.setIcon(new ImageIcon("resources/icons/exit.png"));
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//action when clicked
				exit();
			}
		});
		
		// put all components together
		toolbar.add(openFileButton);
		toolbar.add(Box.createRigidArea(new Dimension(2,0)));
		toolbar.add(new JSeparator(SwingConstants.VERTICAL));
		toolbar.add(Box.createRigidArea(new Dimension(2,0)));
		toolbar.add(gLawsButton);
		toolbar.add(Box.createRigidArea(new Dimension(2,0)));
		toolbar.add(new JSeparator(SwingConstants.VERTICAL));
		toolbar.add(Box.createRigidArea(new Dimension(2,0)));
		toolbar.add(runButton);
		toolbar.add(stopButton);
		toolbar.add(Box.createRigidArea(new Dimension(5,0)));
		toolbar.add(delayLabel);
		toolbar.add(Box.createRigidArea(new Dimension(2,0)));
		toolbar.add(delaySpinner);
		toolbar.add(Box.createRigidArea(new Dimension(5,0)));
		toolbar.add(stepsLabel);
		toolbar.add(Box.createRigidArea(new Dimension(2,0)));
		toolbar.add(stepsSpinner);
		toolbar.add(Box.createRigidArea(new Dimension(5,0)));
		toolbar.add(dtLabel);
		toolbar.add(Box.createRigidArea(new Dimension(2,0)));
		toolbar.add(dtText);
		toolbar.add(Box.createRigidArea(new Dimension(498,0)));
		toolbar.add(new JSeparator(SwingConstants.VERTICAL));
		toolbar.add(Box.createRigidArea(new Dimension(2,0)));
		toolbar.add(exitButton);
	}
	
	// Other private/protected methods
	private synchronized void run_sim(int n, long delay) {	
		while ( n>0 && !_thread.isInterrupted() ) {
			// 1. execute the simulator one step, i.e., call method
			// _ctrl.run(1) and handle exceptions if any
			try {
				_ctrl.run(1);
			} catch (Exception e) {
				// show the error in a dialog box
				JOptionPane.showMessageDialog(ControlPanel.this, 
						"An error has occured while running the simulator!", "Warning", JOptionPane.WARNING_MESSAGE);
				
				// enable all buttons
				enableButtons();
				
				// exit method
				return;
			}
			
			// 2. sleep the current thread for ’delay’ milliseconds
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				_thread.interrupt();
			}
			n--;
		}
		
		// enable all buttons
		enableButtons();
	}
	
	private void disableButtons() {
		gLawsButton.setEnabled(false);
		openFileButton.setEnabled(false);
		runButton.setEnabled(false);
	}
	
	private void enableButtons() {
		gLawsButton.setEnabled(true);
		openFileButton.setEnabled(true);
		runButton.setEnabled(true);
	}
	
	private void chooseFile() {
		int returnVal = fc.showOpenDialog(ControlPanel.this);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			FileInputStream is;
			try {
				is = new FileInputStream(fc.getSelectedFile());
				_ctrl.reset();
				SwingUtilities.invokeLater(
					new Runnable(){
						public void run() {
							_ctrl.loadBodies(is);
						}
					}
				);
				
			} catch (FileNotFoundException e1) {
				JOptionPane.showMessageDialog(ControlPanel.this, 
						"File not found!", "Warning", JOptionPane.WARNING_MESSAGE);
			} catch (Exception e2) {
				JOptionPane.showMessageDialog(ControlPanel.this, 
						"Invalid file!", "Warning", JOptionPane.WARNING_MESSAGE);
			}
		}
	}
	
	private void chooseLaw() {
		String s = (String)JOptionPane.showInputDialog(ControlPanel.this,
				"Choose Gravity Law",
				"Gravity Laws",
				JOptionPane.PLAIN_MESSAGE,
				null,
				ls,
				ls[0]);
		if(s != null) {
			for (JSONObject j : _ctrl.getGravityLawsFactory().getInfo()) {
				if (s.equals(j.getString("desc") + " (" + j.getString("type") +")")) {
					_ctrl.setGravityLaws(j);
					return;
				}
			}
		}
	}
	
	private void runAction() {
		try {
			double d = Double.parseDouble(dtText.getText());
			disableButtons();

			_ctrl.setDeltaTime(d);
			
			_thread = new Thread() {
				public void run() {
					//run the simulator
					run_sim(((int)(stepsSpinner.getValue())), ((int)delaySpinner.getValue()));
					gLawsButton.setEnabled(true);
					openFileButton.setEnabled(true);
					runButton.setEnabled(true);
					_thread = null;
				}
			};
			
			_thread.start();
		}
		catch(Exception ex) {
			JOptionPane.showMessageDialog(ControlPanel.this, 
					"Invalid delta time value!", "Warning", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	private void stopAction() {
		if (!_thread.equals(null))
			_thread.interrupt();
	}
	
	private void exit() {
		int n = JOptionPane.showOptionDialog(ControlPanel.this,
				"Are you sure you want to exit?",
				"Exit",
				JOptionPane.YES_NO_OPTION, 
				JOptionPane.QUESTION_MESSAGE, null, null, null);
		if(n == 0)
			System.exit(0);
	}
	
	// SimulatorObserver methods
	@Override
	public void onRegister(List<Body> bodies, double time, double dt, String gLawsDesc) {
		SwingUtilities.invokeLater(
			new Runnable() {
				@Override
				public void run() {
					dtText.setText(String.valueOf(dt));
				}
			}
		);
	}

	@Override
	public void onReset(List<Body> bodies, double time, double dt, String gLawsDesc) {
		SwingUtilities.invokeLater(
			new Runnable() {
				@Override
				public void run() {
					dtText.setText(String.valueOf(dt));
				}
			}
		);
	}

	@Override
	public void onBodyAdded(List<Body> bodies, Body b) {
	}

	@Override
	public void onAdvance(List<Body> bodies, double time) {
	}

	@Override
	public void onDeltaTimeChanged(double dt) {
		SwingUtilities.invokeLater(
			new Runnable() {
				@Override
				public void run() {
					dtText.setText(String.valueOf(dt));
				}
			}
		);
	}

	@Override
	public void onGravityLawChanged(String gLawsDesc) {
	}

}
