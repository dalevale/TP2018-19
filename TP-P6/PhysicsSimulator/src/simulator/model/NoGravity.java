package simulator.model;

import java.util.List;

public class NoGravity implements GravityLaws {
	public void apply(List<Body> bodies) {} // empty
	
	public String toString() {
		return "No Gravity (NG)";
	}
}
