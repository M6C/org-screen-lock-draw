package org.screen.lock.draw.tool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

public class ToolImage {
    private String mFilePath;
    private String mDestinationDirectory;
    private String mFinishedImageName;
    public enum Direction { VERTICAL, HORIZONTAL, NONE };
    
    public ToolImage(String sourcePath) {
    	this(sourcePath, null, null);
    }
 
    public ToolImage(String sourcePath, String destDir, String destName) {
        mFilePath = sourcePath;
        mDestinationDirectory = destDir;
        mFinishedImageName = destName;
 
    }

    public static Bitmap process(Bitmap bitmap, float rotateFactor, float scaleFactor, Direction rotateType) {
        return resizeAndRotate(rotateFactor, scaleFactor, bitmap, rotateType);
    }

    public Bitmap processAndSaveImage(float scaleFactor, Direction rotateType) {
        Bitmap bitmap = BitmapFactory.decodeFile(mFilePath);
        bitmap = resizeAndRotate(scaleFactor, bitmap, rotateType);
        if (mDestinationDirectory != null && mFinishedImageName != null) {
        	saveBitmapToDisk(bitmap);
        }
        return bitmap;
    }
 
    private void saveBitmapToDisk(Bitmap bitmap) {
        File outFile = new File(mDestinationDirectory, mFinishedImageName);
 
        FileOutputStream fos;
 
        try {
            fos = new FileOutputStream(outFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (FileNotFoundException e) {
            e.getMessage();
        }
    }
    
    private static Bitmap resizeAndRotate(float scaleFactor, Bitmap src, Direction rotateType) {
    	return resizeAndRotate(-1, scaleFactor, src, rotateType);
    }
 
    private static Bitmap resizeAndRotate(float rotateFactor, float scaleFactor, Bitmap src, Direction rotateType) {
        Matrix matrix = rotateLogic(rotateFactor, scaleFactor, rotateType);
        return  Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }
 
    private static Matrix rotateLogic(float rotateFactor, float scaleFactor, Direction rotateType) {
        Matrix matrix = new Matrix();
 
        // perform error checking here for scale factor
 
        if(rotateType.equals(Direction.VERTICAL)) {
//            matrix.preScale(scaleFactor, rotateFactor*scaleFactor);
            matrix.preRotate(rotateFactor);
            matrix.preScale(scaleFactor, scaleFactor);
        } else if (rotateType.equals(Direction.HORIZONTAL)) {
//            matrix.preScale(rotateFactor*scaleFactor, scaleFactor);
            matrix.preRotate(rotateFactor);
            matrix.preScale(scaleFactor, scaleFactor);
        } else if(rotateType.equals(Direction.NONE)) {
            matrix.preRotate(rotateFactor);
            matrix.preScale(scaleFactor, scaleFactor);
        } else {
            imageProcessorError();
        }
 
        return matrix;
 
    }
 
    private static void imageProcessorError() {
    	Log.d("ERROR", "There was an error in your request");
    }
}
