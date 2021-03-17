package simulator.model;

import java.util.List;

public class FallingToCenterGravity implements GravityLaws {
	public void apply(List<Body> bodies) {
		for (Body i : bodies) {
			i.setAcceleration(i.getPosition().direction().scale(-9.81));
		}
	}
	
	public String toString() {
		return "Falling to Center Gravity (FTCG)";
	}
}
