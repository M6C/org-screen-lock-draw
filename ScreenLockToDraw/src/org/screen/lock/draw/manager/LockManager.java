package org.screen.lock.draw.manager;

import android.os.Bundle;

public class LockManager {

	private static final String EXTRA_LOCKED = "EXTRA_LOCKED";
	private static LockManager instance = null;
	private boolean locked = false;

	public LockManager() {
	}

	public static LockManager getInstance() {
		if (instance == null) {
			instance = new LockManager();
		}
		return instance;
	}

	public void initialize(Bundle bundle) {
		if (bundle != null) {
			locked = bundle.getBoolean(EXTRA_LOCKED, false);
		}
	}

	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(EXTRA_LOCKED, locked);
	}

	public static void clean() {
		instance = null;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}
}
