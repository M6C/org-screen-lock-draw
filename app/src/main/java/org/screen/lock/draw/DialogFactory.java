package org.screen.lock.draw;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.webkit.MimeTypeMap;

import org.screen.lock.draw.tool.ToolPermission;

public class DialogFactory {

	public static final int ACTION_REQUEST_GALLERY = 1;
	public static final int ACTION_REQUEST_CAMERA = 2;

    private Uri cameraPhotoURI;

    // From : https://github.com/M6C/com-cameleon-common/blob/master/src/com/cameleon/common/android/factory/FactoryDialog.java
	public Dialog buildOkCancelDialog(Context context, OnClickListener onClickOkListener, int titleId, int messageId) {
		Context ctx = context;
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle(titleId);
		builder.setMessage(messageId);
		builder.setPositiveButton("OK", onClickOkListener);
		builder.setNeutralButton("Cancel", null);
		return builder.create();
	}

	public void showDialogChooseImageSource(final Activity activity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("Choose Image Source");
		builder.setItems(new CharSequence[] {"Gallery", "Camera"}, 
		        new DialogInterface.OnClickListener() {

			@Override
		    public void onClick(DialogInterface dialog, int which) {
		        switch (which) {
		        case 0:
		            showGallery(activity);
		            break;

		        case 1:
		            File cameraFolder;

		            if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
						cameraFolder = new File(android.os.Environment.getExternalStorageDirectory(), "some_directory_to_save_images/");
					}
		            else {
						cameraFolder = activity.getCacheDir();
					}
		            if(!cameraFolder.exists()) {
						cameraFolder.mkdirs();
					}

		            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
		            String timeStamp = dateFormat.format(new Date());
		            String imageFileName = "picture_" + timeStamp + ".jpg";

					Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
					intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		            File photo = new File(cameraFolder, imageFileName);
					cameraPhotoURI = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID+".org.screen.lock.draw.provider", photo);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPhotoURI);

		            activity.startActivityForResult(intent, ACTION_REQUEST_CAMERA);

		            break;

		        default:
		            break;
		        }
		    }
		}).show();
	}

	public Uri getCameraPhotoURI() {
		return cameraPhotoURI;
	}

	public void setCameraPhotoURI(Uri cameraPhotoURI) {
		this.cameraPhotoURI = cameraPhotoURI;
	}

	private void showGallery(final Activity activity) {
		// GET IMAGE FROM THE GALLERY
//            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//            intent.setType("image/*");
//
//            Intent chooser = Intent.createChooser(intent, "Choose a Picture");
//            activity.startActivityForResult(chooser, ACTION_REQUEST_GALLERY);

		Intent galleryIntent = new Intent(
		        Intent.ACTION_PICK,
		        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		galleryIntent.setType("image/*");
		activity.startActivityForResult(galleryIntent, ACTION_REQUEST_GALLERY);
	}
}