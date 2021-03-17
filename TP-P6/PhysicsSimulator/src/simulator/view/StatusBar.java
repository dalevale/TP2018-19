package simulator.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import simulator.control.Controller;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

public class StatusBar extends JPanel implements SimulatorObserver {

	private JLabel _currTime; // for current time
	private JLabel _currLaws; // for gravity laws
	private JLabel _numOfBodies; // for number of bodies
	private JToolBar toolbar;
	
	StatusBar(Controller ctrl) {
		initGUI();
		ctrl.addObserver(this);
	}
	
	private void initGUI() {
		this.setLayout( new FlowLayout( FlowLayout.LEFT ));
		this.setBorder( BorderFactory.createBevelBorder( 1 ));
		
		// TODO complete the code to build the tool bar
		toolbar = new JToolBar();
		add(toolbar);
		_currTime = new JLabel();
		_currLaws = new JLabel();
		_numOfBodies = new JLabel();
		
		toolbar.add(_currTime);
		toolbar.add(Box.createRigidArea(new Dimension(50,0)));
		toolbar.add(new JSeparator(SwingConstants.VERTICAL));
		toolbar.add(Box.createRigidArea(new Dimension(10,0)));
		toolbar.add(_numOfBodies);
		toolbar.add(Box.createRigidArea(new Dimension(50,0)));
		toolbar.add(new JSeparator(SwingConstants.VERTICAL));
		toolbar.add(Box.createRigidArea(new Dimension(10,0)));
		toolbar.add(_currLaws);
		toolbar.setFloatable(false);
	}
	
	// other private/protected methods
	// ...

	// SimulatorObserver methods
	@Override
	public void onRegister(List<Body> bodies, double time, double dt, String gLawsDesc) {
		SwingUtilities.invokeLater(
			new Runnable() {
				@Override
				public void run() {
					_currTime.setText("Time:  " + String.valueOf(time));
					_currLaws.setText("Laws:  " + gLawsDesc);
					_numOfBodies.setText("Bodies:  " + String.valueOf(bodies.size()));
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
					_currTime.setText("Time:  " + String.valueOf(time));
					_currLaws.setText("Laws:  " + gLawsDesc);
					_numOfBodies.setText("Bodies:  " + String.valueOf(bodies.size()));
				}
			}
		);
	}

	@Override
	public void onBodyAdded(List<Body> bodies, Body b) {
		SwingUtilities.invokeLater(
			new Runnable() {
				@Override
				public void run() {
					_numOfBodies.setText("Bodies:  " + String.valueOf(bodies.size()));
				}
			}
		);
		
	}

	@Override
	public void onAdvance(List<Body> bodies, double time) {
		SwingUtilities.invokeLater(
			new Runnable() {
				@Override
				public void run() {
					_currTime.setText("Time:  " + String.valueOf(time));
				}
			}
		);
		
	}

	@Override
	public void onDeltaTimeChanged(double dt) {
	}

	@Override
	public void onGravityLawChanged(String gLawsDesc) {
		SwingUtilities.invokeLater(
			new Runnable() {
				@Override
				public void run() {
					_currLaws.setText("Laws:  " + gLawsDesc);
				}
			}
		);
		
	}

}
