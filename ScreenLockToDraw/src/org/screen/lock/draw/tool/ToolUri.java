package org.screen.lock.draw.tool;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class ToolUri {
	private ToolUri() {
	}

	public static String getPath(Context context, Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index =             cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }

	public static Uri getUri(Context context, File file) {
        String filePath = file.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
       } else {
            if (file.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }
	
	public URI toUriJ(Uri uri) throws URISyntaxException {
		URI ret = new java.net.URI(uri.getScheme(),
		        uri.getSchemeSpecificPart(),
		        uri.getFragment());
//		URI ret = new java.net.URI(uri.toString());
		return ret;
	}

	public Uri toUriA(URI uri) {
		Uri ret = new Uri.Builder().scheme(uri.getScheme())
            .encodedAuthority(uri.getRawAuthority())
            .encodedPath(uri.getRawPath())
            .query(uri.getRawQuery())
            .fragment(uri.getRawFragment())
            .build();
		return ret;
	}
}
