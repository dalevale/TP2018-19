package simulator.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;

import simulator.control.Controller;

public class BodiesTable extends JPanel {
	BodiesTable(Controller ctrl) {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createLineBorder(Color.black, 2),
			"Bodies",
			TitledBorder.LEFT, TitledBorder.TOP));
		
		// TODO complete
		BodiesTableModel model = new BodiesTableModel(ctrl);
		JTable table = new JTable(model);
		add(new JScrollPane(table));
		setMinimumSize(new Dimension(500,300));
		setPreferredSize(new Dimension(500,300));
	}

}
