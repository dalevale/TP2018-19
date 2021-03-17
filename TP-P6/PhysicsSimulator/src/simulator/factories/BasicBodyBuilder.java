package simulator.factories;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import simulator.misc.Vector;
import simulator.model.Body;

public class BasicBodyBuilder extends Builder<Body> {

	
	@Override
	public Body createInstance(JSONObject info) {
		if (!info.getString("type").equals("basic"))
			return null;
		
		JSONObject j = info.getJSONObject("data");
		
		try {
			JSONArray jA;
			Vector p;
			Vector v;
			
			jA = j.getJSONArray("pos");
			double[] pos = JSONArrayToDoubleArray(jA);
			p = new Vector(pos);
			
			jA = j.getJSONArray("vel");
			double[] vel = JSONArrayToDoubleArray(jA);
			v = new Vector(vel);
			
			// return the corresponding body (assume for now that acceleration is a zero vector)
			return new Body(j.getString("id"), v, new Vector(p.dim()), p, j.getDouble("mass"));
		}
		catch (JSONException e) {
			throw new IllegalArgumentException("There is an error in the provided values", e);
		}
	}

	@Override
	public JSONObject getBuilderInfo() {
		JSONObject j = new JSONObject();
		JSONObject subJ = new JSONObject();
		JSONArray jA1 = new JSONArray();
		JSONArray jA2 = new JSONArray();
		
		jA1.put(0.0e00);
		jA1.put(0.0e00);
		
		jA2.put(0.05e04);
		jA2.put(0.0e00);
		
		j.put("type", "basic");
		subJ.put("id", "b1");
		subJ.put("pos", jA1);
		subJ.put("vel", jA2);
		subJ.put("mass", 5.97e24);
		j.put("data", subJ);
		j.put("desc", "Basic body");
		
		return j;
	}
	

}
