package ca.idrc.tagin.lib.tags;

import java.util.List;

public interface GetLabelsTaskListener {
	
	/**
	 * Callback method when GetLabelsTask is executed.
	 * @param urn the specified URN for which the labels are requested.
	 * @param labels a list of labels related to the URN.
	 */
	public void onGetLabelsTaskComplete(String urn, List<String> labels);

}
