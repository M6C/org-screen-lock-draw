package org.screen.lock.draw.manager;

import android.os.Bundle;

public class FilterManager {

	private static final String EXTRA_FILTER = "EXTRA_FILTER";
	private static FilterManager instance = null;
	private boolean filtred = true;

	public FilterManager() {
	}

	public static FilterManager getInstance() {
		if (instance == null) {
			instance = new FilterManager();
		}
		return instance;
	}

	public void initialize(Bundle bundle) {
		if (bundle != null) {
			filtred = bundle.getBoolean(EXTRA_FILTER, false);
		}
	}

	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(EXTRA_FILTER, filtred);
	}

	public static void clean() {
		instance = null;
	}

	public boolean isFiltred() {
		return filtred;
	}

	public void setFiltred(boolean filtred) {
		this.filtred = filtred;
	}
}
