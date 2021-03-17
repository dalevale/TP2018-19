package simulator.factories;

import org.json.JSONObject;

import simulator.model.GravityLaws;
import simulator.model.NewtonUniversalGravitation;

public class NewtonUniversalGravitationBuilder extends Builder<GravityLaws>{

	@Override
	public GravityLaws createInstance(JSONObject info) {
		if (!info.getString("type").equals("nlug"))
			return null;
		if (!info.getJSONObject("data").isEmpty())
			throw new IllegalArgumentException("There is an error in the provided values");
		
		return new NewtonUniversalGravitation();
	}

	@Override
	public JSONObject getBuilderInfo() {
		JSONObject j = new JSONObject();
		
		j.put("type", "nlug");
		j.put("data", new JSONObject());
		j.put("desc", "Newton's law of universal gravitation");
		
		return j;
	}

}
