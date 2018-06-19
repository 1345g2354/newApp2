package com.hangyjx.syygzapp.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;

import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Hashtable;

public class Utils {
      
        public static final boolean isChineseCharacter(String chineseStr) {  
            char[] charArray = chineseStr.toCharArray();  
            for (int i = 0; i < charArray.length; i++) {  
                // 是否是Unicode编码,除了"�"这个字符.这个字符要另外处理  
                if ((charArray[i] >= '\u0000' && charArray[i] < '\uFFFD')  
                        || ((charArray[i] > '\uFFFD' && charArray[i] < '\uFFFF'))) {  
                    continue;  
                } else {  
                    return false;  
                }  
            }  
            return true;  
        }  
      
        /** 
         * Get a file path from a Uri. This will get the the path for Storage Access 
         * Framework Documents, as well as the _data field for the MediaStore and 
         * other file-based ContentProviders. 
         *  
         * @param context 
         *            The context. 
         * @param uri 
         *            The Uri to query. 
         * @author paulburke 
         */  
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public static String getPath(final Context context, final Uri uri) {
      
            final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
      
            // DocumentProvider  
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider  
                if (isExternalStorageDocument(uri)) {  
                    final String docId = DocumentsContract.getDocumentId(uri);  
                    final String[] split = docId.split(":");  
                    final String type = split[0];  
      
                    if ("primary".equalsIgnoreCase(type)) {  
                        return Environment.getExternalStorageDirectory() + "/"
                                + split[1];  
                    }  
      
                    // TODO handle non-primary volumes  
                }  
                // DownloadsProvider  
                else if (isDownloadsDocument(uri)) {  
      
                    final String id = DocumentsContract.getDocumentId(uri);  
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"),  
                            Long.valueOf(id));  
      
                    return getDataColumn(context, contentUri, null, null);  
                }  
                // MediaProvider  
                else if (isMediaDocument(uri)) {  
                    final String docId = DocumentsContract.getDocumentId(uri);  
                    final String[] split = docId.split(":");  
                    final String type = split[0];  
      
                    Uri contentUri = null;  
                    if ("image".equals(type)) {  
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {  
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;  
                    } else if ("audio".equals(type)) {  
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;  
                    }  
      
                    final String selection = "_id=?";  
                    final String[] selectionArgs = new String[] { split[1] };  
      
                    return getDataColumn(context, contentUri, selection,  
                            selectionArgs);  
                }  
            }  
            // MediaStore (and general)  
            else if ("content".equalsIgnoreCase(uri.getScheme())) {  
                return getDataColumn(context, uri, null, null);  
            }  
            // File  
            else if ("file".equalsIgnoreCase(uri.getScheme())) {  
                return uri.getPath();  
            }  
      
            return null;  
        }  
      
        /** 
         * Get the value of the data column for this Uri. This is useful for 
         * MediaStore Uris, and other file-based ContentProviders. 
         *  
         * @param context 
         *            The context. 
         * @param uri 
         *            The Uri to query. 
         * @param selection 
         *            (Optional) Filter used in the query. 
         * @param selectionArgs 
         *            (Optional) Selection arguments used in the query. 
         * @return The value of the _data column, which is typically a file path. 
         */  
        public static String getDataColumn(Context context, Uri uri,  
                String selection, String[] selectionArgs) {  
      
            Cursor cursor = null;
            final String column = "_data";  
            final String[] projection = { column };  
      
            try {  
                cursor = context.getContentResolver().query(uri, projection,  
                        selection, selectionArgs, null);  
                if (cursor != null && cursor.moveToFirst()) {  
                    final int column_index = cursor.getColumnIndexOrThrow(column);  
                    return cursor.getString(column_index);  
                }  
            } finally {  
                if (cursor != null)  
                    cursor.close();  
            }  
            return null;  
        }  
      
        /** 
         * @param uri 
         *            The Uri to check. 
         * @return Whether the Uri authority is ExternalStorageProvider. 
         */  
        public static boolean isExternalStorageDocument(Uri uri) {  
            return "com.android.externalstorage.documents".equals(uri  
                    .getAuthority());  
        }  
      
        /** 
         * @param uri 
         *            The Uri to check. 
         * @return Whether the Uri authority is DownloadsProvider. 
         */  
        public static boolean isDownloadsDocument(Uri uri) {  
            return "com.android.providers.downloads.documents".equals(uri  
                    .getAuthority());  
        }  
      
        /** 
         * @param uri 
         *            The Uri to check. 
         * @return Whether the Uri authority is MediaProvider. 
         */  
        public static boolean isMediaDocument(Uri uri) {  
            return "com.android.providers.media.documents".equals(uri  
                    .getAuthority());  
        }




    public static  String  scanningImage(String path) {
        if (TextUtils.isEmpty(path)) {

            return null;

        }
        // DecodeHintType 和EncodeHintType
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        Bitmap scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小

        int sampleSize = (int) (options.outHeight / (float) 200);

        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);
      return   getReult(scanBitmap);

//
//        int px[] = new int[scanBitmap.getWidth() * scanBitmap.getHeight()];
//        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap.getWidth(),scanBitmap.getHeight(),px);
//        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
//        QRCodeReader reader = new QRCodeReader();
//        try {
//
//            return reader.decode(bitmap1, hints);
//
//        } catch (NotFoundException e) {
//
//            e.printStackTrace();
//
//        } catch (ChecksumException e) {
//
//            e.printStackTrace();
//
//        } catch (FormatException e) {
//
//            e.printStackTrace();
//
//        }

//        return null;

    }

    /**
     * 进行中文乱码处理
     * @param str
     * @return
     */
    public static String recode(String str) {
        String formart = "";

        try {
            boolean ISO = Charset.forName("ISO-8859-1").newEncoder()
                    .canEncode(str);
            if (ISO) {
                formart = new String(str.getBytes("ISO-8859-1"), "GB2312");
                Log.i("1234      ISO8859-1", formart);
            } else {
                formart = str;
                Log.i("1234      stringExtra", str);
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return formart;
    }


    public static String getReult(Bitmap mBitmap) {
        Result result = null;
        if (mBitmap != null) {
            result = scanBitmap(mBitmap);
        }
        if (result != null) {
            return recode(result.toString());
        }
        return null;
    }

    private static Result scanBitmap(Bitmap mBitmap) {
        Result result = scan(mBitmap);

        return result;//recode(result);
    }


    private static Result scan(Bitmap mBitmap) {
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码
        Bitmap scanBitmap = Bitmap.createBitmap(mBitmap);

        int px[] = new int[scanBitmap.getWidth() * scanBitmap.getHeight()];
        scanBitmap.getPixels(px, 0, scanBitmap.getWidth(), 0, 0,
                scanBitmap.getWidth(), scanBitmap.getHeight());
        RGBLuminanceSource source = new RGBLuminanceSource(
                scanBitmap.getWidth(), scanBitmap.getHeight(), px);
        BinaryBitmap tempBitmap = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(tempBitmap, hints);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return null;
    }
}