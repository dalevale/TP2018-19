package simulator.model;

import simulator.misc.Vector;

public class MassLosingBody extends Body {
	
	protected double lossFactor;
	protected double lossFrequency;
	protected double c = 0.0; // counter for time
	
	public MassLosingBody(String id, Vector v, Vector a, Vector p, double m, double lossFactor, double lossFrequency) {
		super(id, v, a, p, m);
		this.lossFactor = lossFactor;
		this.lossFrequency = lossFrequency;
	}
	
	void move(double t) {
		setPosition(getPosition().plus(getVelocity().scale(t)).plus(getAcceleration().scale(0.5*t*t)));
		setVelocity(getVelocity().plus(getAcceleration().scale(t)));
		
		c += t;
		if (c >= lossFrequency) {
			this.m *= (1 - this.lossFactor);
			this.c = 0.0;
		}
	}
}
