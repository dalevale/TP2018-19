package simulator.factories;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class BuilderBasedFactory<T> implements Factory<T> {

	List<Builder<T>> builders;
	
	public BuilderBasedFactory(List<Builder<T>> builders){
		this.builders = builders;
	}
	
	@Override
	public T createInstance(JSONObject info) {
		T ret = null;
		
		for (Builder<T> builder : builders) {
			if (ret != null) continue;
			else ret = builder.createInstance(info);
		}
		
		if (ret == null)
			throw new IllegalArgumentException("Instance cannot be created");
		
		return ret;
	}

	@Override
	public List<JSONObject> getInfo() {
		List<JSONObject> info = new ArrayList<JSONObject>();
		
		for (Builder<T> builder : builders) {
			if (builder != null) 
				info.add(builder.getBuilderInfo());
		}
		
		return info;
	}

}
