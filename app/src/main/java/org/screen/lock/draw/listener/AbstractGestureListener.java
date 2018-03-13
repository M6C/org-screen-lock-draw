package org.screen.lock.draw.listener;

import org.screen.lock.draw.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public abstract class AbstractGestureListener extends SimpleOnGestureListener {
//http://savagelook.com/blog/android/swipes-or-flings-for-navigation-in-android

	private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	private Activity context = null;

	public AbstractGestureListener(Activity context) {
		this.context =  context;
	}

	protected abstract Intent getFlingRight(Context context);
	protected abstract Intent getFlingLeft(Context context);

	@Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

		if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
            return false;
        }

        // right to left swipe
        if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            Intent intent = getFlingRight(context.getApplicationContext());//new Intent(GPSApplication.getAppContext(), SessionManagerActivity.class);
            if (intent!=null) {
        		context.setVisible(false);
	        	context.startActivity(intent);
	        	context.overridePendingTransition(
					R.anim.slide_in_right,
					R.anim.slide_out_left
				);
            }
	    // right to left swipe
        }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            Intent intent = getFlingLeft(context.getApplicationContext());//new Intent(GPSApplication.getAppContext(), DbToolActivity.class);
            if (intent!=null) {
        		context.setVisible(false);
	        	context.startActivity(intent);
	        	context.overridePendingTransition(
					R.anim.slide_in_left, 
					R.anim.slide_out_right
				);
            }
        }

        return false;
    }

    // It is necessary to return true from onDown for the onFling event to register
    @Override
    public boolean onDown(MotionEvent e) {
        	return true;
    }
}
