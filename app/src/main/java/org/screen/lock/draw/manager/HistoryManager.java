package org.screen.lock.draw.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.screen.lock.draw.tool.ToolPermission;
import org.screen.lock.draw.tool.ToolUri;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

public class HistoryManager {
	
	private static final String SHARED_KEY = "org.screen.lock.draw";
	private static final String SHARED_KEY_HISTORY = "org.screen.lock.draw.history";

	private static HistoryManager instance = null;
	private List<String> history = new ArrayList<String>();
	private List<String> historyPath = new ArrayList<String>();
	private SharedPreferences prefs = null;
	private Context context;

	public HistoryManager(Activity context) {
		this.context = context;
		prefs = context.getSharedPreferences(SHARED_KEY, Context.MODE_PRIVATE);
		if (!ToolPermission.checkPermissionREAD_EXTERNAL_STORAGE(context)) {
			return;
		}
		history.addAll(prefs.getStringSet(SHARED_KEY_HISTORY, new HashSet<String>()));
		for(String uri : history) {
			historyPath.add(ToolUri.getPath(context, Uri.parse(uri)));
		}
	}

	public static HistoryManager getInstance(Activity context) {
		if (instance == null) {
			instance = new HistoryManager(context);
		}
		return instance;
	}

	public static void clean() {
		instance = null;
	}

	public void addHistory(String arg) {
		int idx = history.indexOf(arg);
		if (idx >= 0) {
			history.remove(idx);
			historyPath.remove(idx);
		}
		history.add(0, arg);
		historyPath.add(0, ToolUri.getPath(context, Uri.parse(arg)));
		prefs.edit().putStringSet(SHARED_KEY_HISTORY, new HashSet<String>(history)).apply();
	}

	public void cleanHistory() {
		prefs.edit().remove(SHARED_KEY_HISTORY).apply();
		history.clear();
		historyPath.clear();
	}

	public List<String> getHistory() {
		return history;
	}

	public List<String> getHistoryPath() {
		return historyPath;
	}
}