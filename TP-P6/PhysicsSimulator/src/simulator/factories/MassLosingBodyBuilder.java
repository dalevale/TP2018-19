package simulator.factories;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import simulator.misc.Vector;
import simulator.model.Body;
import simulator.model.MassLosingBody;

public class MassLosingBodyBuilder extends Builder<Body> {

	@Override
	public Body createInstance(JSONObject info) {	
		if (!info.getString("type").equals("mlb"))
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
			return new MassLosingBody(j.getString("id"), v, new Vector(p.dim()), p, j.getDouble("mass"), j.getDouble("factor"), j.getDouble("freq"));
		}
		catch(JSONException e){
			throw new IllegalArgumentException("There is an error in the provided values", e);
		}
	}

	@Override
	public JSONObject getBuilderInfo() {
		JSONObject j = new JSONObject();
		JSONObject subJ = new JSONObject();
		JSONArray jA1 = new JSONArray();
		JSONArray jA2 = new JSONArray();
		
		jA1.put(-3.5e10);
		jA1.put(0.0e00);
		
		jA2.put(0.0e00);
		jA2.put(1.4e03);
		
		j.put("type", "mlb");
		subJ.put("id", "b1");
		subJ.put("pos", jA1);
		subJ.put("vel", jA2);
		subJ.put("mass", 3.0e28);
		subJ.put("freq", 1e3);
		subJ.put("factor", 1e-3);
		j.put("data", subJ);
		j.put("desc", "Mass losing body");
		
		return j;
	}

}
