package org.screen.lock.draw.tool;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

public class ToolPermission {
	public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

	private ToolPermission() {
	}

	public static boolean checkPermissionREAD_EXTERNAL_STORAGE(final Activity context) {
		int currentAPIVersion = Build.VERSION.SDK_INT;
		if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
			if (ContextCompat.checkSelfPermission(context,
					Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
					showDialog("External storage", context, Manifest.permission.READ_EXTERNAL_STORAGE);

				} else {
					ActivityCompat
							.requestPermissions(
									(Activity) context,
									new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
									MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
				}
				return false;
			} else {
				return true;
			}

		} else {
			return true;
		}
	}

	private static void showDialog(final String msg, final Context context, final String permission) {
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
		alertBuilder.setCancelable(true);
		alertBuilder.setTitle("Permission necessary");
		alertBuilder.setMessage(msg + " permission is necessary");
		alertBuilder.setPositiveButton(android.R.string.yes,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						ActivityCompat.requestPermissions((Activity) context,
								new String[] { permission },
								MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
					}
				});
		AlertDialog alert = alertBuilder.create();
		alert.show();
	}}
