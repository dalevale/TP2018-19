package simulator.factories;

import org.json.JSONObject;

import simulator.model.GravityLaws;
import simulator.model.NoGravity;

public class NoGravityBuilder extends Builder<GravityLaws> {

	@Override
	public GravityLaws createInstance(JSONObject info) {
		if (!info.getString("type").equals("ng"))
			return null;
		if (!info.getJSONObject("data").isEmpty())
			throw new IllegalArgumentException("There is an error in the provided values\"");
		
		return new NoGravity();
	}

	@Override
	public JSONObject getBuilderInfo() {
		JSONObject j = new JSONObject();
		
		j.put("type", "ng");
		j.put("data", new JSONObject());
		j.put("desc", "No gravity");
		
		return j;
	}

}
