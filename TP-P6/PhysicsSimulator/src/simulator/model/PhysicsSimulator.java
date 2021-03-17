package simulator.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import simulator.misc.Vector;

public class PhysicsSimulator {
	private double time;
	private double dt;
	private GravityLaws law;
	private List<Body> bodies = new ArrayList<Body>();
	private List<SimulatorObserver> observers = new ArrayList<SimulatorObserver>();
	
	public PhysicsSimulator(double dt, GravityLaws law) {
		if (dt <= 0)
			throw new IllegalArgumentException("Real time per step is nonpositive");
		if (law == null)
			throw new IllegalArgumentException("Gravity law is null");
		
		this.dt = dt;
		this.law = law;
	}
	
	public void advance() {
		// Apply the law
		law.apply(bodies);
		
		// Move each body accordingly
		for (Body i : bodies) {
			i.move(dt);
		}
		
		// Increment time by dt seconds
		time += dt;
		
		// Notify all observers
		for (SimulatorObserver i : observers)
			i.onAdvance(bodies, time);
	}
	
	public void addBody(Body b) {
		// Check if the existing bodies already has the id
		for (Body i : bodies) {
			if (i != null && i.getId().equals(b.getId()))
				throw new IllegalArgumentException("Body id already exists");
		}
		
		// Add the body
		bodies.add(b);
		
		// Notify all observers
		for (SimulatorObserver i : observers)
			i.onBodyAdded(bodies, b);
	}
	
	public void addObserver(SimulatorObserver o) {
		// Check if o is in the list of observers already
		if (observers.contains(o))
			throw new IllegalArgumentException("Observer is already in the list");
		
		// Add the observer
		observers.add(o);
		
		// Notify the new observer
		o.onRegister(bodies, time, dt, law.toString());
	}
	
	public void reset() {
		bodies.clear();
		time = 0.0;
		
		// Notify all observers
		for (SimulatorObserver i : observers)
			i.onReset(bodies, time, dt, law.toString());
	}
	
	public void setDeltaTime(double dt) {
		// Check if dt is valid
		if (dt <= 0)
			throw new IllegalArgumentException("Value of dt is not valid");
		
		// Set dt
		this.dt = dt;
		
		// Notify all observers
		for (SimulatorObserver i : observers)
			i.onDeltaTimeChanged(dt);
	}
	
	public void setGravityLaws(GravityLaws gravityLaws) {
		// Check if gravityLaws is valid
		if (gravityLaws == null)
			throw new IllegalArgumentException("Gravity law is not valid");
		
		// Set law to gravityLaws
		this.law = gravityLaws;
		
		// Notify all observers
		for (SimulatorObserver i : observers)
			i.onGravityLawChanged(law.toString());
	}
	
	public String toString() {
		String s = "{ \"time\": " + time + ", \"bodies\": [ ";

		Iterator<Body> i = bodies.iterator();
		
		if (i.hasNext()) s += i.next();
		
		while (i.hasNext()) {
			s += ", ";
			s += i.next();
		}
		
		s += " ] }";
		
		return s;
	}
}
