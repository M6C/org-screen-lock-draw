package org.screen.lock.draw;

import java.util.List;

import org.screen.lock.draw.manager.HistoryManager;
import org.screen.lock.draw.manager.LockManager;
import org.screen.lock.draw.view.TouchImageView;
import org.screenlocktodraw.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends ActionBarActivity
		implements NavigationDrawerFragment.NavigationDrawerCallbacks {

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
	private static TouchImageView ivMain;

	private DialogFactory dialogFactory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dialogFactory = new DialogFactory();

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
			Uri uri = Uri.parse(str);
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

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
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
		else if (id == R.id.action_lock_unlock) {
			if (LockManager.getInstance().isLocked()) {
				LockManager.getInstance().setLocked(false);
				item.setIcon(getResources().getDrawable(R.drawable.ic_unlock));
				lockUnLock(false);
			} else {
				LockManager.getInstance().setLocked(true);
				item.setIcon(getResources().getDrawable(R.drawable.ic_lock));
				lockUnLock(true);
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
		        			Uri uri = str.get(0);
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

	private void lockUnLock(boolean lock) {
		ivMain.setEnabledTouchListner(!lock);
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
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			ivMain = (TouchImageView) rootView.findViewById(R.id.ivMain);
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
		}
	}

}
