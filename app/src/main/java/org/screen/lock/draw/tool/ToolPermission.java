package org.screen.lock.draw.tool;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import java.util.List;

public class ToolPermission {
	public static final int MY_PERMISSIONS_REQUEST = 123;

	private ToolPermission() {
	}

	public static boolean checkPermissionREAD_EXTERNAL_STORAGE(final Activity context) {
		return checkPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
	}

	public static void grantPermissionProvider(Context context, Intent intent, Uri uri) {
		List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		for (ResolveInfo resolveInfo : resInfoList) {
			String packageName = resolveInfo.activityInfo.packageName;
			context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
		}
	}

	private static boolean checkPermission(final Activity context, String permission) {
		int currentAPIVersion = Build.VERSION.SDK_INT;
		if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
			if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
				if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
					showDialog("External storage", context, permission);

				} else {
					ActivityCompat.requestPermissions((Activity) context, new String[] {permission}, MY_PERMISSIONS_REQUEST);
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
						ActivityCompat.requestPermissions((Activity) context, new String[] { permission }, MY_PERMISSIONS_REQUEST);
					}
				});
		AlertDialog alert = alertBuilder.create();
		alert.show();
	}}
