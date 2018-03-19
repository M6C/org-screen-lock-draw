package org.screen.lock.draw.tool;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.webkit.MimeTypeMap;

import org.screen.lock.draw.BuildConfig;

import java.io.File;

public class ToolFile {
	private ToolFile() {
	}

	public static void setDataTypeFromFileExtention(Context context, File file, Intent intent) {
		MimeTypeMap mime = MimeTypeMap.getSingleton();
		String ext = file.getName().substring(file.getName().lastIndexOf(".") + 1);
		String type = mime.getMimeTypeFromExtension(ext);
		Uri uri = getUri(context, file);
		intent.setDataAndType(uri, type);
	}

	public static Uri getUri(Context context, File file) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID+".org.screen.lock.draw.provider", file);
		} else {
			return Uri.fromFile(file);
		}
	}
}
