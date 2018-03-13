package org.screen.lock.draw;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.screen.lock.draw.listener.AbstractGestureListener;
import org.screen.lock.draw.listener.OnClickSendApkListenerOk;
import org.screen.lock.draw.manager.FilterManager;
import org.screen.lock.draw.manager.HistoryManager;
import org.screen.lock.draw.manager.LockManager;
import org.screen.lock.draw.tool.ToolImage;
import org.screen.lock.draw.tool.ToolImage.Direction;
import org.screen.lock.draw.tool.ToolUri;
import org.screen.lock.draw.view.TouchImageView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.androidquery.AQuery;

public class MainActivity extends AppCompatActivity
		implements NavigationDrawerFragment.NavigationDrawerCallbacks {

	private static final String EXTRA_IMAGE_URI = "EXTRA_IMAGE_URI";
	private static final String EXTRA_IMAGE_PATH = "EXTRA_IMAGE_PATH";
	private static final String EXTRA_FROM_CREATE = "EXTRA_FROM_CREATE";

	private static MainActivity activity;
	private AQuery aq;

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
	private boolean fromCreate = true;
	private Toast toast = null;
	private static Uri uri;
	private static String path;
	private File[] listFiles = null;
    private int idxFile;

	private static LockManager lockManager;
	private static FilterManager filterManager;

	public MainActivity() {
		activity = this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		lockManager = LockManager.getInstance();
		filterManager = FilterManager.getInstance();
		dialogFactory = new DialogFactory();

		setContentView(R.layout.activity_main);

		initialize(savedInstanceState);

    	aq = new AQuery(this);

    	mNavigationDrawerFragment = (NavigationDrawerFragment)
				getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(
				dialogFactory,
				R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));

		if (uri == null) {
			dialogFactory.showDialogChooseImageSource(activity);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (uri != null) {
			setImage(uri, fromCreate, true);
		}
		int idText = filterManager.isFiltred() ? R.string.action_filter_on : R.string.action_filter_off;
		aq.id(R.id.action_filter).text(idText);
		fromCreate = false;
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		if (ivMain != null) {
			String str = HistoryManager.getInstance(getApplicationContext()).getHistory().get(position);
			Uri newUri = Uri.parse(str);
			setImage(newUri, false, true);
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
	    	finish();
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
		if (!backPressedToExitOnce) {
			outState.putParcelable(EXTRA_IMAGE_URI, uri);
			outState.putString(EXTRA_IMAGE_PATH, path);
			outState.putBoolean(EXTRA_FROM_CREATE, fromCreate);
			lockManager.onSaveInstanceState(outState);
			filterManager.onSaveInstanceState(outState);
		}
	}

	@Override
	public void finish() {
		clean();
		super.finish();
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
		if (id == R.id.action_rotate_left) {
			rotateLeft();
			return true;
		}
		else if (id == R.id.action_rotate_right) {
			rotateRight();
			return true;
		}
		else if (id == R.id.action_open_image) {
			dialogFactory.showDialogChooseImageSource(this);
			return true;
		}
		else if (id == R.id.action_share_apk) {
			onClickSendApk(null);
			return true;
		}
		else if (id == R.id.action_filter) {
			if (filterManager.isFiltred()) {
				filterManager.setFiltred(false);
				item.setTitle(getString(R.string.action_filter_off));
			} else {
				filterManager.setFiltred(true);
				item.setTitle(getString(R.string.action_filter_on));
			}
			setImage(uri, false, true);
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
		        			setImage(str.get(0), true, true);
		        		}
		        	}
		        }
		        break;
	
		        case DialogFactory.ACTION_REQUEST_CAMERA: {
//		        	ivMain.setImageURI(dialogFactory.getCameraPhotoURI());
		        	setImage(dialogFactory.getCameraPhotoURI(), true, true);
		        }
		        break;          
	        }
	    }
	}

	private void clean() {
		uri = null;
		path = null;
		listFiles = null;
		lockManager = null;
		filterManager = null;
		FilterManager.clean();
		LockManager.clean();
		HistoryManager.clean();
		aq.clear();
	}

	private void setImage(Uri newUri, final boolean hitoryze, final boolean updListFile) {
		uri = newUri;
		path = ToolUri.getPath(this, uri);
		new AsyncTask<Void, Void, String>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				startProgress();
			}

			@Override
			protected String doInBackground(Void... params) {
				if ((updListFile || listFiles == null) && path != null) {
					new UpdateListFileTask().execute();
				}
				if (hitoryze) {
					HistoryManager.getInstance(getApplicationContext()).addHistory(uri.toString());
				}
				return ToolUri.getPath(MainActivity.this, uri);
			}

			@Override
			protected void onPostExecute(String url) {
				boolean cache = (url == null) ? false : url.startsWith("http://") || url.startsWith("https://");
				boolean exist = (url == null) ? false : aq.getCachedFile(url) != null;
				log("setImage - url:" + url + " Cache(Exist:" + exist + ",Put:" + cache + ")");

				ivMain.setImageURI(uri);
				ivMain.postDelayed(new Runnable() {
					@Override
					public void run() {
						stopProgress();
					}
				}, 1000);
				super.onPostExecute(url);
			}
		}.execute();
	};

	private void intializeTouchListener() {
		if (this.ivMain != null) {
			final GestureDetector gestureDetector = new GestureDetector(this, new AbstractGestureListener(this) {
				
				@Override
				protected Intent getFlingRight(Context context) {
					slideFileURI(1);
					return null;
				}
				
				@Override
				protected Intent getFlingLeft(Context context) {
					slideFileURI(-1);
					return null;
				}

				private void slideFileURI(int pos) {
					if (uri == null || path == null) {
						return;
					}
					if (MainActivity.this.ivMain.isZoomed()) {
						return;
					}
					if (listFiles == null || listFiles.length == 0) {
						return;
					}

					int idx = idxFile + pos;
					if (idx < 0) {
						idx = listFiles.length - 1;
					}
					if (idx >= listFiles.length) {
						idx = 0;
					}

					Uri ret = ToolUri.getUri(MainActivity.this, listFiles[idx]);
					log("-listFiles["+idx+"]:" + listFiles[idx] + " pos:"+pos+" uri:" + ((ret == null) ? ret : ret.toString()));
					if (ret != null) {
						setImage(ret, false, false);
						idxFile = idx;
					}
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
			menu.getItem(5).setVisible(unLock);
			menu.getItem(6).setVisible(unLock);
			if (unLock) {
				menu.getItem(7).setIcon(getResources().getDrawable(R.drawable.ic_unlock));
			} else {
				menu.getItem(7).setIcon(getResources().getDrawable(R.drawable.ic_lock));
			}
		}
	}

	private void rotateRight() {
		rotate(90f);
	}

	private void rotateLeft() {
		rotate(-90f);
	}

	private void rotate(final float degrees) {
		new AsyncTask<Void, Void, Bitmap>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				startProgress();
			}

			@Override
			protected Bitmap doInBackground(Void... params) {
				Bitmap ret = null;
				if (ivMain.getDrawable() != null) {
					Bitmap bmp = ((BitmapDrawable)ivMain.getDrawable()).getBitmap();
					ret = ToolImage.process(bmp, degrees, 1f, Direction.HORIZONTAL);
				}
				return ret;
			}

			@Override
			protected void onPostExecute(Bitmap bmp) {
				if (bmp != null) {
					ivMain.setImageBitmap(bmp);
					ivMain.postDelayed(new Runnable() {
						@Override
						public void run() {
							stopProgress();
						}
					}, 1000);
				} else {
					stopProgress();
				}
				super.onPostExecute(bmp);
			}
		}.execute();
	}

	private void initialize(Bundle bundle) {
		if (bundle != null) {
			uri = (Uri) bundle.getParcelable(EXTRA_IMAGE_URI);
			path = bundle.getString(EXTRA_IMAGE_PATH);
			fromCreate = bundle.getBoolean(EXTRA_FROM_CREATE);
		} else {
			Intent intent = getIntent();
			if (intent.getType() != null && intent.getData() != null && intent.getType().indexOf("image/") != -1) {
				uri = intent.getData();
			}
	    }
		lockManager.initialize(bundle);
		filterManager.initialize(bundle);
	}

	private void startProgress() {
//		aq.id(R.id.ll_progress).visibility(View.VISIBLE);
//		aq.id(R.id.ll_content).visibility(View.INVISIBLE);
		aq.id(R.id.ll_progress).visible();
		aq.id(R.id.ll_content).invisible();
		if (aq.id(R.id.progress_view) != null && aq.id(R.id.progress_view).getImageView() != null) {
			((AnimationDrawable)aq.id(R.id.progress_view).getImageView().getBackground()).start();
		}
	}

	private void stopProgress() {
//		aq.id(R.id.ll_progress).visibility(View.GONE);
//		aq.id(R.id.ll_content).visibility(View.VISIBLE);
		aq.id(R.id.ll_progress).gone();
		aq.id(R.id.ll_content).visible();
		if (aq.id(R.id.progress_view) != null && aq.id(R.id.progress_view).getImageView() != null) {
			((AnimationDrawable)aq.id(R.id.progress_view).getImageView().getBackground()).stop();
		}
	}

	private void log(String text) {
		System.out.println(text);
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
				activity.setImage(uri, false, true);
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

	private final class UpdateListFileTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			idxFile = -1;
			String dirPath = path.substring(0, path.lastIndexOf("/"));
			File dir = new File(dirPath);
			File[] list = dir.listFiles();
			if (list == null) {
				return null;
			}
			listFiles = (filterManager.isFiltred() ? filterListFile(list) : list);
			sortListFile(listFiles);
			log("-listFiles length:" + listFiles.length);
			for(int i = 0 ; i<listFiles.length ; i++) {
				log("-listFiles["+i+"]" + listFiles[i]);
			}
			for(int i = 0 ; i<listFiles.length ; i++) {
				File file = listFiles[i];
				if (file.isFile() && file.getPath().equals(path)) {
					idxFile = i;
					break;
				}
			}
			log("-listFiles idxFile:" + idxFile);
			return null;
		}

		private File[] filterListFile(File[] list) {
			ArrayList<File> arrayFile = new ArrayList<File>();
			for(int i = 0 ; i<list.length ; i++) {
				File file = list[i];
				if (file.isFile()) {
					try {
						String extension = MimeTypeMap.getFileExtensionFromUrl(file.toURI().toURL().toString());
						if (extension != null) {
							String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
							log("-listFiles i:" + i + " extension:" + extension + " mimeType:" + mimeType + " file.path:" + file.getAbsolutePath());
							if (mimeType != null && mimeType.toLowerCase(Locale.getDefault()).startsWith("image/")) {
								arrayFile.add(file);
							}
						}
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
			}
			return arrayFile.toArray(new File[0]);
		}

		private void sortListFile(File[] list) {
			Arrays.sort(list, new Comparator<File>()
			{
			    public int compare(File o1, File o2) {

			        if (((File)o1).lastModified() > ((File)o2).lastModified()) {
			            return -1;
			        } else if (((File)o1).lastModified() < ((File)o2).lastModified()) {
			            return +1;
			        } else {
			            return 0;
			        }
			    }

			});
		}
	}
}
