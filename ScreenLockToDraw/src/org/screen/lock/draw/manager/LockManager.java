package org.screen.lock.draw.manager;

public class LockManager {
	
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

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}
}
