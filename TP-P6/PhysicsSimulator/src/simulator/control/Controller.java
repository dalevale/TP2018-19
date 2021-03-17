package simulator.control;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import simulator.factories.Factory;
import simulator.model.*;

public class Controller {

	PhysicsSimulator ps;
	Factory<Body> bf;
	Factory<GravityLaws> glf;
	
	public Controller(PhysicsSimulator ps, Factory<Body> bf, Factory<GravityLaws> glf) {
		this.ps = ps;
		this.bf = bf;
		this.glf = glf;
	}
	
	public void loadBodies(InputStream in) {
		JSONObject jsonInput = new JSONObject(new JSONTokener(in));
		JSONArray jA = jsonInput.getJSONArray("bodies");
		JSONObject o;
		
		for (int i = 0; i < jA.length(); ++i) {
			o = jA.getJSONObject(i);
			ps.addBody(bf.createInstance(o));
		}
	}
	
	public void reset() {
		ps.reset();
	}
	
	public void setDeltaTime(double dt) {
		ps.setDeltaTime(dt);
	}
	
	public void addObserver(SimulatorObserver o) {
		ps.addObserver(o);
	}
	
	public Factory<GravityLaws> getGravityLawsFactory() {
		return glf;
	}
	
	public void setGravityLaws(JSONObject info) {
		ps.setGravityLaws(glf.createInstance(info));
	}
	
	public void run(int n) {
		for (int i = 0; i < n; ++i)
			ps.advance();
	}
	
	public void run(int n, OutputStream out) {
		PrintStream p = new PrintStream(out);
		
		p.println("{");
		p.println("\"states\": [");
		p.println(ps.toString() + ",");
		
		for (int i = 0; i < n - 1; ++i) {
			ps.advance();
			p.println(ps.toString() + ",");
		}
		
		ps.advance();
		p.println(ps.toString());
		
		p.println("]");
		p.println("}");
	}
	
}
