package net.minecraft.src.betatweaks.references.json;

import org.json.JSONException;
import org.json.JSONObject;

import net.minecraft.src.betatweaks.dummy.HandlerJSON;

public class ConcreteHandler extends HandlerJSON {

	@Override
	public int getPlayerCount(String jsonText) throws Exception {
		if(jsonText.equals("Not Found")) {
			throw new Exception("Invalid proxy name");
		}
		if(jsonText.equals("")) {
			throw new Exception("Proxy backend down");
		}
		try {
			JSONObject json = new JSONObject(jsonText);
			if(!json.getBoolean("isOnline")) {
				throw new Exception("Server offline");
			}
			return Integer.parseInt(json.getString("playerCount"));
		}
		catch(JSONException e) {
			System.out.println(jsonText);
			e.printStackTrace();
			throw new Exception("Error parsing JSON");
		}
	}
}
