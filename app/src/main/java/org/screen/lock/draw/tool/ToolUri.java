package org.screen.lock.draw.tool;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class ToolUri {
	private ToolUri() {
	}

	public static String getPath(Context context, Uri uri) {
		String ret = null;
		if ("file".equals(uri.getScheme().toLowerCase(Locale.getDefault()))) {
			ret = uri.getPath();
		} else if ("content".equals(uri.getScheme().toLowerCase(Locale.getDefault()))) {
	        String[] projection = { MediaStore.Images.Media.DATA };
	        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
	        if (cursor == null) return null;
			int column_index = getColumnIndex(cursor);
			if (column_index == -1) return null;
			if (!(column_index < cursor.getCount())) return null;
	        cursor.moveToFirst();
	        ret =cursor.getString(column_index);
	        cursor.close();
		}
        return ret;
    }

	public static Uri getUri(Context context, File file) {
		Uri ret = null;
        String filePath = file.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            ret = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
       } else {
            if (file.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                ret = context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            }
        }
        if (file != null && ret == null) {
        	ret = toUriA(file.toURI());
        }
        return ret;
    }
	
	public static URI toUriJ(Uri uri) throws URISyntaxException {
		URI ret = new java.net.URI(uri.getScheme(),
		        uri.getSchemeSpecificPart(),
		        uri.getFragment());
//		URI ret = new java.net.URI(uri.toString());
		return ret;
	}

	public static Uri toUriA(URI uri) {
		Uri ret = new Uri.Builder().scheme(uri.getScheme())
            .encodedAuthority(uri.getRawAuthority())
            .encodedPath(uri.getRawPath())
            .query(uri.getRawQuery())
            .fragment(uri.getRawFragment())
            .build();
		return ret;
	}

	private static int getColumnIndex(Cursor cursor) {
		int column_index = -1;
		switch (cursor.getColumnCount()) {
			case 0: {
				break;
			}
			case 1: {
				column_index = 0;
				break;
			}
			default: try {
				column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return column_index;
	}
}
