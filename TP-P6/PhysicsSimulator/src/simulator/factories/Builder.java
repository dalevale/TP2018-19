package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class Builder<T> {
	public abstract T createInstance(JSONObject info);
	
	public abstract JSONObject getBuilderInfo();
	
	protected static double[] JSONArrayToDoubleArray(JSONArray jA){
		double[] dA = new double[jA.length()];
		for (int i = 0; i < jA.length(); i++) {
			dA[i] = jA.getDouble(i);
		}
		return dA;
	}
}
