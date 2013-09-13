package ca.idrc.tagin.lib;

import java.io.IOException;

import com.google.api.client.json.gson.GsonFactory;

import android.util.Log;

public class TaginUtils {
	
	/**
	 * Deserializes a JSON response and returns it in its original container.
	 * @param result the JSON response.
	 * @param clazz the type of the original container.
	 * @return the deserialized result if the operation is successful, or null.
	 */
	public static <T> T deserialize(String result, Class<T> clazz) {
		T container = null;
		if (result != null) {
			try {
				container = new GsonFactory().fromString(result, clazz);
			} catch (IOException e) {
				Log.e(TaginManager.TAG, "Deserialization error: " + e.getMessage());
			}
		}
		return container;
	}

}
