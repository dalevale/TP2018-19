package simulator.factories;

import org.json.JSONObject;

import simulator.model.GravityLaws;
import simulator.model.FallingToCenterGravity;

public class FallingToCenterGravityBuilder extends Builder<GravityLaws>{

	@Override
	public GravityLaws createInstance(JSONObject info) {
		if (!info.getString("type").equals("ftcg"))
			return null;
		if (!info.getJSONObject("data").isEmpty())
			throw new IllegalArgumentException("There is an error in the provided values\"");
		
		return new FallingToCenterGravity();
	}

	@Override
	public JSONObject getBuilderInfo() {
		JSONObject j = new JSONObject();
		
		j.put("type", "ftcg");
		j.put("data", new JSONObject());
		j.put("desc", "Falling to center gravity");
		
		return j;
	}

}
