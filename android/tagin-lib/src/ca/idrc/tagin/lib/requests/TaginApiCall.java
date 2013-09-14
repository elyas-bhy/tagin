package ca.idrc.tagin.lib.requests;

public interface TaginApiCall {
	
	/**
	 * Executes the API call, and returns the result if successful, or null.
	 * @return
	 */
	public String execute();
	
	/**
	 * Returns the broadcast action specific to the API call.
	 * @return
	 */
	public String getBroadcastAction();

}
