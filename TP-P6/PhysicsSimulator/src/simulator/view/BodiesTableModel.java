package simulator.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

public class BodiesTableModel extends AbstractTableModel implements SimulatorObserver {
	private List<Body> _bodies;
	private String header[] = {"Id", "Mass", "Position", "Velocity", "Acceleration"};
	
	BodiesTableModel(Controller ctrl) {
		_bodies = new ArrayList<>();
		ctrl.addObserver(this);
	}

	@Override
	public int getRowCount() {
		return _bodies.size();
	}

	@Override
	public int getColumnCount() {
		return header.length;
	}
	
	@Override
	public String getColumnName(int column) {
		return header[column];
	}


	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Body x = _bodies.get(rowIndex);
		Object r = null;
		
		switch(columnIndex) {
		case 0:
			r = x.getId();
			break;
		case 1:
			r = x.getMass();
			break;
		case 2:
			r = x.getPosition();
			break;
		case 3:
			r = x.getVelocity();
			break;
		case 4:
			r = x.getAcceleration();
		}
		
		return r;
	}

	// SimulatorObserver methods
	@Override
	public void onRegister(List<Body> bodies, double time, double dt, String gLawsDesc) {
		SwingUtilities.invokeLater(
			new Runnable() {
				@Override
				public void run() {
					_bodies = bodies;
					fireTableStructureChanged();
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
					_bodies.clear();
					fireTableStructureChanged();
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
					_bodies = bodies;
					fireTableStructureChanged();
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
					_bodies = bodies;
					fireTableStructureChanged();
				}
			}
		);
	}

	@Override
	public void onDeltaTimeChanged(double dt) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onGravityLawChanged(String gLawsDesc) {
		// TODO Auto-generated method stub
	}

}
