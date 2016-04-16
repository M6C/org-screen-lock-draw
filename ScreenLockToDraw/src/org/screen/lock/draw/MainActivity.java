package org.screen.lock.draw;

import java.io.File;
import java.util.List;

import org.screen.lock.draw.listener.AbstractGestureListener;
import org.screen.lock.draw.listener.OnClickSendApkListenerOk;
import org.screen.lock.draw.manager.HistoryManager;
import org.screen.lock.draw.manager.LockManager;
import org.screen.lock.draw.tool.ToolUri;
import org.screen.lock.draw.view.TouchImageView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity
		implements NavigationDrawerFragment.NavigationDrawerCallbacks {

	private static final String EXTRA_IMAGE_URI = "EXTRA_IMAGE_URI";
	private static final String EXTRA_IMAGE_PATH = "EXTRA_IMAGE_PATH";

	private static MainActivity activity;

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	private TouchImageView ivMain;

	private DialogFactory dialogFactory;

	private Menu menu;

	private boolean backPressedToExitOnce = false;
	private Toast toast = null;
	private static Uri uri;
	private static String path;

	private static LockManager lockManager;

	public MainActivity() {
		activity = this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		lockManager = LockManager.getInstance();
		dialogFactory = new DialogFactory();
		initialize(savedInstanceState);

		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment)
				getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(
				dialogFactory,
				R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		if (ivMain != null) {
			String str = HistoryManager.getInstance(getApplicationContext()).getHistory().get(position);
			uri = Uri.parse(str);
			path = ToolUri.getPath(this, uri);
			if (uri != null) {
				ivMain.setImageURI(uri);
			}
		} else {
			// update the main content by replacing fragments
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
					.commit();
		}
	}
	@Override
	public void onBackPressed() {
	    if (backPressedToExitOnce) {
	        super.onBackPressed();
	    } else if (this.toast == null) {
	        this.backPressedToExitOnce = true;
	        this.toast = Toast.makeText(this, R.string.press_again_to_exit, Toast.LENGTH_SHORT);
	        this.toast.show();
	        new Handler().postDelayed(new Runnable() {

	            @Override
	            public void run() {
	                backPressedToExitOnce = false;
	                MainActivity.this.toast = null;
	            }
	        }, 2000);
	    }
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(EXTRA_IMAGE_URI, uri);
		outState.putString(EXTRA_IMAGE_PATH, path);
		lockManager.onSaveInstanceState(outState);
		
	}

	public void onSectionAttached(int number) {
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setTitle(mTitle);
		lockUnLockMenu();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menu = menu;
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_open_image) {
			dialogFactory.showDialogChooseImageSource(this);
			return true;
		}
		else if (id == R.id.action_share_apk) {
			onClickSendApk(null);
			return true;
		}
		else if (id == R.id.action_lock_unlock) {
			if (lockManager.isLocked()) {
				lockManager.setLocked(false);
				item.setIcon(getResources().getDrawable(R.drawable.ic_unlock));
				lockUnLock();
			} else {
				lockManager.setLocked(true);
				item.setIcon(getResources().getDrawable(R.drawable.ic_lock));
				lockUnLock();
			}
			return true;
		} else if (id == R.id.action_move_right) {
			ivMain.moveLeft(true);
			return true;
		} else if (id == R.id.action_move_left) {
			ivMain.moveLeft(false);
			return true;
		} else if (id == R.id.action_move_up) {
			ivMain.movedown(true);
			return true;
		} else if (id == R.id.action_move_down) {
			ivMain.movedown(false);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onClickSendApk(View view) {
		OnClickSendApkListenerOk listener = new OnClickSendApkListenerOk(this);
		new DialogFactory().buildOkCancelDialog(this, listener, R.string.dialog_send_apk_title, R.string.dialog_send_apk_message)
			.show();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode == RESULT_OK)    {

	        switch (requestCode) {
		        case DialogFactory.ACTION_REQUEST_GALLERY: {
		        	if (data != null && data.getExtras() != null) {
		        		for(String key : data.getExtras().keySet()) {
		        			System.out.println("key '" + key + "' => '" + data.getExtras().get(key) + "'");
		        		}
		        	}
//key 'mimeType' => 'image/*'
//key 'selectedCount' => '1'
//key 'selectedItems' => '[content://media/external/images/media/9061]'
//		        	if (data.hasExtra(MediaStore.EXTRA_OUTPUT)) {
		        	if (data.hasExtra("selectedItems")) {
		        		List<Uri> str = (List<Uri>) data.getSerializableExtra("selectedItems");
		        		if (str != null && str.size() > 0) {
		        			uri = str.get(0);
		        			path = ToolUri.getPath(this, uri);
		        			HistoryManager.getInstance(getApplicationContext()).addHistory(uri.toString());
							ivMain.setImageURI(uri);
		        		}
		        	}
		        }
		        break;
	
		        case DialogFactory.ACTION_REQUEST_CAMERA: {
		        	ivMain.setImageURI(dialogFactory.getCameraPhotoURI());
		        }
		        break;          
	        }
	    }
	};

	private void intializeTouchListener() {
		if (this.ivMain != null) {
			final GestureDetector gestureDetector = new GestureDetector(this, new AbstractGestureListener(this) {
				
				@Override
				protected Intent getFlingRight(Context context) {
					Uri newUri = slideFileURI(1);
					if (newUri != null) {
						MainActivity.this.uri = newUri;
						MainActivity.this.path = ToolUri.getPath(MainActivity.this, newUri);
						MainActivity.this.ivMain.setImageURI(MainActivity.this.uri);
					}
					return null;
				}
				
				@Override
				protected Intent getFlingLeft(Context context) {
					Uri newUri = slideFileURI(-1);
					if (newUri != null) {
						MainActivity.this.uri = newUri;
						MainActivity.this.path = ToolUri.getPath(MainActivity.this, newUri);
						MainActivity.this.ivMain.setImageURI(MainActivity.this.uri);
					}
					return null;
				}

				private Uri slideFileURI(int pos) {
					if (uri == null || path == null) {
						return null;
					}
					if (MainActivity.this.ivMain.isZoomed()) {
						return null;
					}
					Uri ret = uri;
					String dirPath = path.substring(0, path.lastIndexOf("/"));
					File dir = new File(dirPath);
					File[] listFile = dir.listFiles();
					int idxFile = -1;
					for(int i = 0 ; i<listFile.length ; i++) {
						File file = listFile[i];
						if (file.isFile() && file.getPath().equals(path)) {
							idxFile = i + pos;
							break;
						}
					}
					if (idxFile >= 0 && idxFile < listFile.length) {
						ret = ToolUri.getUri(MainActivity.this, listFile[idxFile]);
					}
					return ret;
				}
			});
	
			OnTouchListener onTouchListener = new View.OnTouchListener() {
				//http://savagelook.com/blog/android/swipes-or-flings-for-navigation-in-android
	
				public boolean onTouch(View v, MotionEvent event) {
			        if (gestureDetector.onTouchEvent(event)) {
			            return true;
			        }
			        return false;
			    }
			};
			this.ivMain.setOnTouchListener(onTouchListener);
		}
	}

	private void lockUnLock() {
		boolean unLock = !lockManager.isLocked();
		if (ivMain != null) {
			ivMain.setEnabledTouchListner(unLock);
		}
		lockUnLockMenu();
	}

	private void lockUnLockMenu() {
		boolean unLock = !lockManager.isLocked();
		if (menu != null) {
			menu.getItem(0).setVisible(!unLock);
			menu.getItem(1).setVisible(!unLock);
			menu.getItem(2).setVisible(!unLock);
			menu.getItem(3).setVisible(!unLock);
			menu.getItem(4).setVisible(unLock);
			if (unLock) {
				menu.getItem(5).setIcon(getResources().getDrawable(R.drawable.ic_unlock));
			} else {
				menu.getItem(5).setIcon(getResources().getDrawable(R.drawable.ic_lock));
			}
		}
	}

	private void initialize(Bundle bundle) {
		if (bundle != null) {
			uri = (Uri) bundle.getParcelable(EXTRA_IMAGE_URI);
			path = bundle.getString(EXTRA_IMAGE_PATH);
		}
		lockManager.initialize(bundle);
	}

	public TouchImageView getIvMain() {
		return ivMain;
	}

	public void setIvMain(TouchImageView ivMain) {
		this.ivMain = ivMain;
		intializeTouchListener();
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			activity.setIvMain((TouchImageView) rootView.findViewById(R.id.ivMain));
			if (activity.getIvMain() != null && uri != null) {
				activity.getIvMain().setImageURI(uri);
			}
			activity.lockUnLock();
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
		}
	}
}
