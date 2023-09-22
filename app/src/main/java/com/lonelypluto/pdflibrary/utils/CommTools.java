package com.lonelypluto.pdflibrary.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;

/**
 * Project: PdfViewerDemo<br/>
 * Package: com.lonelypluto.pdflibrary.utils<br/>
 * ClassName: CommTools<br/>
 * Description: TODO<br/>
 * Date: 2023-09-22 17:14 <br/>
 * <p>
 * Author luohao<br/>
 * Version 1.0<br/>
 * since JDK 1.6<br/>
 * <p>
 */
public class CommTools {

    public static void saveToGallery(Context context, File saveFile, String title, String description) {
        String uri = MediaStore.Images.Media.insertImage(context.getContentResolver(), BitmapFactory.decodeFile(saveFile.getAbsolutePath()), title, description);
        if (TextUtils.isEmpty(uri)) {
            saveToGallery(context, saveFile.getAbsolutePath(), title, description);
        } else {
            saveToGallery(context, getRealPathByUri(context , Uri.parse(uri)), title, description);
        }
    }

    public static void scanFile(Context context, Uri imageUri) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(imageUri);
        context.sendBroadcast(scanIntent);

    }


    public static void saveToGallery(Context context, String filepath, String title, String description) {
        if (TextUtils.isEmpty(filepath))
            return;
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filepath);

        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (null != uri) {
            scanFile(context, uri);
        } else {
            scanFile(context, Uri.fromFile(new File(filepath)));
        }
    }

    @SuppressLint("Range")
    public static String getRealPathByUri(Context context, Uri uri) {

        if (null == uri)
            return "";
        String[] ps = {MediaStore.Images.Media.DATA};
        Cursor pathCursor = new CursorLoader(context, uri, ps, null, null, null).loadInBackground();
        if (null == pathCursor)
            return uri.getPath();
        pathCursor.moveToFirst();
        return pathCursor.getString(pathCursor.getColumnIndex(MediaStore.Images.Media.DATA));
    }
}
