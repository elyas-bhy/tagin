package ca.idrc.tagin.lib.tags;

public interface SetLabelTaskListener {

	/**
	 * Callback method when SetLabelTask is executed.
	 * @param isSuccessful true if the operation was successful, else false.
	 */
	public void onSetLabelTaskComplete(Boolean isSuccessful);
	
}
