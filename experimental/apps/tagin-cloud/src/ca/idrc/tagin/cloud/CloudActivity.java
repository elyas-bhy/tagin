package ca.idrc.tagin.cloud;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import ca.idrc.tagin.cloud.tag.Tag;
import ca.idrc.tagin.cloud.tag.TagCloudView;
import ca.idrc.tagin.cloud.util.TagAdderDialog;
import ca.idrc.tagin.cloud.util.TagMap;
import ca.idrc.tagin.lib.TaginManager;
import ca.idrc.tagin.lib.TaginService;
import ca.idrc.tagin.lib.tags.GetLabelsTask;
import ca.idrc.tagin.lib.tags.GetLabelsTaskListener;
import ca.idrc.tagin.lib.tags.SetLabelTask;
import ca.idrc.tagin.lib.tags.SetLabelTaskListener;

import com.github.espiandev.showcaseview.ShowcaseView;

public class CloudActivity extends Activity implements GetLabelsTaskListener, SetLabelTaskListener,
		ShowcaseView.OnShowcaseEventListener {
	
	private ShowcaseView mShowcaseView;
	
	private CloudActivity mInstance;
	private TagMap mTagMap;
	private TaginManager mTaginManager;
	private TagCloudView mTagCloudView;
	private TagAdderDialog mTagAdderDialog;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mInstance = this;
		mTaginManager = new TaginManager(this);
		mTagAdderDialog = new TagAdderDialog(this);
		initCloud();
		createTagCloud();
	}
	
	/**
	 * Initializes the tags map according to the current connectivity state.
	 */
	private void initCloud() {
		if (TaginCloudApp.persistence.isConnected()) {
			mTagMap = (TagMap) getIntent().getSerializableExtra(LauncherActivity.EXTRA_TAGS);
			saveData();
		} else {
			mTagMap = loadData();
		}
	}

	/**
	 * Creates and displays a new tag cloud.
	 */
	private void createTagCloud() {
		Display display = getWindowManager().getDefaultDisplay();
		mTagCloudView = new TagCloudView(this, display.getWidth(), display.getHeight(), mTagMap);
		setContentView(mTagCloudView);
		mTagCloudView.requestFocus();
		updateTagCloud();
	}
	
	/**
	 * Initiates a server request for assigning a tag's label.
	 * @param tag
	 */
	public void submitTag(Tag tag) {
		SetLabelTask<CloudActivity> task = new SetLabelTask<CloudActivity>(mInstance, tag.getID(), tag.getText());
		task.execute();
		addTagToCloud(tag);
	}
	
	/**
	 * Adds a new tag to the cloud.
	 * @param tag
	 */
	public void addTagToCloud(Tag tag) {
		mTagCloudView.addTag(tag);
		mTagMap.put(tag.getID(), tag);
		updateTagCloud();
		saveData();
		Toast.makeText(this, "\"" + tag.getText() + "\" tag successfully added", Toast.LENGTH_SHORT).show();
		if (mTagMap.size() == 1) {
			Toast.makeText(this, R.string.to_rotate_sphere, Toast.LENGTH_LONG).show();
		}
	}
	
	/**
	 * Updates the state of the cloud.
	 */
	private void updateTagCloud() {
		for (Tag tag : mTagMap.values()) {
			mTagCloudView.setTagRGBT(tag);
		}
	}
	
	/**
	 * Initiates an API request for a new URN.
	 * @param view
	 */
	public void onGetURNClick(View view) {
		mTaginManager.apiRequest(TaginService.REQUEST_URN);
		mTagAdderDialog.getURNTextView().setText(R.string.fetching_urn);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mReceiver);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
        registerReceiver(mReceiver, new IntentFilter(TaginService.ACTION_URN_READY));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.options_menu, menu);
		if (mTagMap.isEmpty()) {
			ShowcaseView.ConfigOptions options = new ShowcaseView.ConfigOptions();
			options.hideOnClickOutside = false;
			mShowcaseView = ShowcaseView.insertShowcaseViewWithType(ShowcaseView.ITEM_ACTION_ITEM, 
					R.id.menu_add_tag, this, R.string.showcase_title, R.string.showcase_message, options);
			mShowcaseView.setOnShowcaseEventListener(this);
		} else {
			Toast.makeText(this, R.string.to_rotate_sphere, Toast.LENGTH_LONG).show();
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.menu_add_tag:
			mTagAdderDialog.showDialog();
			break;
		case R.id.menu_settings:
			break;
		}
		return true;
	}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);
	}
	
	/**
	 * Writes the tags map to the device's memory.
	 */
	public void saveData() {
		ObjectOutputStream oos = null;
		try {
			File history = new File(getFilesDir() + "/tags.dat");
			history.getParentFile().createNewFile();
			FileOutputStream fout = new FileOutputStream(history);
			oos = new ObjectOutputStream(fout);
			oos.writeObject(mTagMap);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();  
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (oos != null) {
					oos.flush();
					oos.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * Loads a map of tags stored on the device's memory.
	 * @return saved data if found, or null.
	 */
	private TagMap loadData() {
		File file = new File(getFilesDir() + "/tags.dat");
		TagMap feedsEntry = null;

		if (file.exists()) {
			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
				feedsEntry = (TagMap) ois.readObject();
				ois.close();
			} catch (Exception e) { 
				e.printStackTrace();
			}
		}
		return feedsEntry;
	}

	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(TaginService.ACTION_URN_READY)) {
				String urn = intent.getStringExtra(TaginService.EXTRA_QUERY_RESULT);
				if (urn != null) {
					mTagAdderDialog.getURNTextView().setText(urn);
					GetLabelsTask<CloudActivity> task = new GetLabelsTask<CloudActivity>(mInstance, urn);
					task.execute();
				}
			}
		}
	};

	@Override
	public void onGetLabelsTaskComplete(String urn, List<String> labels) {
		TextView textView = mTagAdderDialog.getLabelTextView();
		if (labels.size() > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append(labels.get(0));
			for (int i = 1; i < labels.size(); i++) {
				sb.append(", " + labels.get(i));
			}
			textView.setText(sb.toString());
		} else {
			textView.setText(R.string.no_labels_assigned);
		}
	}

	@Override
	public void onSetLabelTaskComplete(Boolean isSuccessful) {
		
	}

	@Override
	public void onShowcaseViewHide(ShowcaseView showcaseView) {
		
	}

	@Override
	public void onShowcaseViewShow(ShowcaseView showcaseView) {
		
	}

}