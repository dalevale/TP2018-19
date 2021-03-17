package simulator.model;

import org.json.JSONObject;

import simulator.misc.Vector;

public class Body {
	
	protected String id;
	protected Vector v;
	protected Vector a;
	protected Vector p;
	protected double m;
	
	public Body(String id, Vector v, Vector a, Vector p, double m) {
		this.id = id;
		this.v = v;
		this.a = a;
		this.p = p;
		this.m = m;
	}
	
	public String getId() {
		return id;
	}
	
	public Vector getVelocity() {
		return v;
	}
	
	public Vector getAcceleration() {
		return a;
	}
	
	public Vector getPosition() {
		return p;
	}
	
	public double getMass() {
		return m;
	}
	
	void setVelocity(Vector v) {
		this.v = new Vector(v);
	}
	
	void setAcceleration(Vector a) {
		this.a = new Vector(a);
	}
	
	void setPosition(Vector p) {
		this.p = new Vector(p);
	}
	
	void move(double t) {
		setPosition(getPosition().plus(getVelocity().scale(t)).plus(getAcceleration().scale(0.5*t*t)));
		setVelocity(getVelocity().plus(getAcceleration().scale(t)));
	}
	
	public String toString() {
		return "{  \"id\": \"" + id + "\", \"mass\": "+ m +", \"pos\": " + p + ", \"vel\": " + v + ", \"acc\": "+ a +" }";
	}
}
