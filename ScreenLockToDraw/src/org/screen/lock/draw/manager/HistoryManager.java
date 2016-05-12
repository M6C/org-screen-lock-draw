package org.screen.lock.draw.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;

public class HistoryManager {
	
	private static final String SHARED_KEY = "org.screen.lock.draw";
	private static final String SHARED_KEY_HISTORY = "org.screen.lock.draw.history";

	private static HistoryManager instance = null;
	private List<String> history = new ArrayList<String>();
	private SharedPreferences prefs = null;

	public HistoryManager(Context context) {
		prefs = context.getSharedPreferences(SHARED_KEY, Context.MODE_PRIVATE);
		history.addAll(prefs.getStringSet(SHARED_KEY_HISTORY, new HashSet<String>()));
	}

	public static HistoryManager getInstance(Context context) {
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
		}
		history.add(0, arg);
		prefs.edit().putStringSet(SHARED_KEY_HISTORY, new HashSet<String>(history)).apply();
	}

	public void cleanHistory() {
		prefs.edit().remove(SHARED_KEY_HISTORY).apply();
		history.clear();
	}

	public List<String> getHistory() {
		return history;
	}

	public void setHistory(List<String> history) {
		this.history = history;
	}
}