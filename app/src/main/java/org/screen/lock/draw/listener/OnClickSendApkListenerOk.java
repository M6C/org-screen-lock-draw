package org.screen.lock.draw.listener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;

public class OnClickSendApkListenerOk implements OnClickListener {

	private Context context;

	public OnClickSendApkListenerOk(Activity context) {
		this.context = context.getApplicationContext();
	}

	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_POSITIVE) {
			try {
				String sourceDir = querySourceDir(context.getPackageName());
				if (sourceDir != null) {
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_SEND);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setType("application/octet-stream");
					intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(sourceDir)));
					context.startActivity(intent);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// From : https://github.com/M6C/com-cameleon-common/blob/master/src/com/cameleon/common/tool/ApkTool.java
	private synchronized String querySourceDir(String packageNameFilter) throws IOException {
		String ret = null;
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		final List<ResolveInfo> pkgAppsList = context.getPackageManager().queryIntentActivities(mainIntent, 0);
		for (ResolveInfo info : pkgAppsList) {
			if (packageNameFilter==null || packageNameFilter.equals(info.activityInfo.packageName)) {
				ret = info.activityInfo.applicationInfo.publicSourceDir;
				break;
			}
		}
		return ret;
	}
}