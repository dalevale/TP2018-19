package simulator.model;

import java.util.List;

import simulator.misc.Vector;

public class NewtonUniversalGravitation implements GravityLaws {
	public void apply(List<Body> bodies) {		
		for (Body i : bodies) {
			// initialize to 0
			Vector F = new Vector(i.getAcceleration().dim()); 
			
			// get force vector
			for (Body j : bodies) {
				if (i != j) {
					F = F.plus(j.getPosition().minus(i.getPosition())
							.direction().scale(6.67E-11*i.getMass()*j.getMass()/(i.getPosition().distanceTo(j.getPosition())
									*i.getPosition().distanceTo(j.getPosition()))));
				}
			}
			
			// turn force vector to acceleration vector
			i.setAcceleration(F.scale(1/i.getMass()));
		}
	}
	
	public String toString() {
		return "Newton's Law of Universal Gravitation (NLUG)";
	}
}
