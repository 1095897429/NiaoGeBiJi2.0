package com.qmkj.niaogebiji.common.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;


/**
 * Date:2018/7/19
 * author:zl
 * 备注：相册路径获取工具类 -- 获取相册的路径
 */
public class PicPathHelper {


    /**
     * 选择图片后，获取图片的路径 并做一些判断 -- 一般在onActivityResult中调用
     * @return 图片文件的绝对路径 比如:storage/emulate/0/zl.png
     */
    @SuppressLint("NewApi")
    public static String handleSelectedPhoto(Context context, Intent data) {

        if (data == null) {
//            Log.d("tag","选择图片文件出错");
            Toast.makeText(context,"选择图片文件出错",Toast.LENGTH_SHORT).show();
            return null;
        }

        Uri photoUri = data.getData();
        if (photoUri == null) {
//            Log.d("tag","选择图片文件出错");
            Toast.makeText(context,"选择图片文件出错",Toast.LENGTH_SHORT).show();
            return null;
        }

        String picPath;
        //判断手机系统版本号
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            picPath = getFilePath_above19(context,photoUri); //4.4及以上系统使用这个方法处理图片
        }else
            picPath  = getFilePath_below19(context,photoUri); //4.4以下系统使用这个放出处理图片

        if(picPath != null &&(picPath.endsWith(".png")||picPath.endsWith(".PNG")
                || picPath.endsWith(".jpg")||picPath.endsWith(".JPG") || picPath.endsWith(".jpeg"))) {
            return picPath;
        }else{
//            Log.d("tag","选择图片文件类型出错");
            Toast.makeText(context,"选择图片文件类型出错",Toast.LENGTH_SHORT).show();
            return null;
        }
    }



    /**
     * 获取小于api19时获取相册中图片真正的uri
     * 对于路径是：content://media/external/images/media/33517
     * @param context
     * @param uri
     * @return  /storage/emulated/0/DCIM/Camera/IMG_20160807_133403.jpg路径
     */
   public static String getFilePath_below19(Context context, Uri uri){
       Cursor cursor ;
       String path ;
       String [] proj = {MediaStore.Images.Media.DATA};
       cursor = context.getContentResolver().query(uri,proj,null,null,null);
       int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA); //获得用户选择的图片的索引值
       cursor.moveToFirst();//将光标移至开头 ，这个很重要，不小心很容易引起越界
       path = cursor.getString(column_index);//最后根据索引值获取图片路径   结果类似：/emulated/0/DCIM/Camera/IMG_20151124_013332.jpg
       cursor.close();
       return path;
   }


    /***
     * 对于Android 4.4及以上机型获取path
     * 返回路径是：content://com.domain.example.provider/images/default_image.jpg，注意域名
     * @param context
     * @param uri
     * @return  /storage/emulated/0/DCIM/Camera/IMG_20160807_133403.jpg路径
     */
   @RequiresApi(api = Build.VERSION_CODES.KITKAT)
   public static String getFilePath_above19(Context context, Uri uri){
       boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

       // 1. DocumentProvider
       if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
           // 1.1 ExternalStorageProvider
           if (isExternalStorageDocument(uri)) {
               final String docId = DocumentsContract.getDocumentId(uri);
               final String[] split = docId.split(":");
               final String type = split[0];
               if ("primary".equalsIgnoreCase(type)) {
                   return  Environment.getExternalStorageDirectory() + "/" + split[1];
               }
           }
           // 1.2 DownloadsProvider
           else if (isDownloadsDocument(uri)) {
               final String id = DocumentsContract.getDocumentId(uri);
               final Uri contentUri = ContentUris.
                       withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
               return  getDataColumn(context,
                       contentUri, null, null);
           }
           // 1.3 MediaProvider
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
               final String[] selectionArgs = new String[]{split[1]};

               return getDataColumn(context, contentUri, selection, selectionArgs);
           }
       } // 2. MediaStore (and general)
       else if ("content".equalsIgnoreCase(uri.getScheme())) {
           if (isGooglePhotosUri(uri)) {//判断是否是google相册图片
               return uri.getLastPathSegment();
           } else if (isGooglePlayPhotosUri(uri)) {//判断是否是Google相册图片
               return getImageUrlWithAuthority(context, uri);
           } else {//其他类似于media这样的图片，和android4.4以下获取图片path方法类似
               return getFilePath_below19(context, uri);
           }
       }
       // 3. 判断是否是文件形式 File
       else if ("file".equalsIgnoreCase(uri.getScheme())) {
           return  uri.getPath();
       }

       return null;
   }



    /**
     * Try to return the absolute file path from the given Uri
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String getRealFilePath( final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            data = getFPUriToPath(context,uri);
        }
        return data;

    }



    /**
     * 获取FileProvider path
     * author zx
     * version 1.0
     * since 2018/5/4
     */
    private static String getFPUriToPath(Context context, Uri uri) {
        try {
            List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
            if (packs != null) {
                String fileProviderClassName = FileProvider.class.getName();
                for (PackageInfo pack : packs) {
                    //获取到每个包下的providers
                    ProviderInfo[] providers = pack.providers;
                    if (providers != null) {

                        for (ProviderInfo provider : providers) {

                            if (uri.getAuthority().equals(provider.authority)) {
                                //TODO 11.16 注释掉 下面 fileProviderClassName 默认为 android.support.v4.content.FileProvider，有时自定义就不能满足需求了
//                                if (provider.name.equalsIgnoreCase(fileProviderClassName)) {
                                    Class<FileProvider> fileProviderClass = FileProvider.class;
                                    try {
                                        Method getPathStrategy = fileProviderClass.getDeclaredMethod("getPathStrategy", Context.class, String.class);
                                        getPathStrategy.setAccessible(true);
                                        Object invoke = getPathStrategy.invoke(null, context, uri.getAuthority());
                                        if (invoke != null) {
                                            String PathStrategyStringClass = FileProvider.class.getName() + "$PathStrategy";
                                            Class<?> PathStrategy = Class.forName(PathStrategyStringClass);
                                            Method getFileForUri = PathStrategy.getDeclaredMethod("getFileForUri", Uri.class);
                                            getFileForUri.setAccessible(true);
                                            Object invoke1 = getFileForUri.invoke(invoke, uri);
                                            if (invoke1 instanceof File) {
                                                String filePath = ((File) invoke1).getAbsolutePath();
                                                return filePath;
                                            }
                                        }
                                    } catch (NoSuchMethodException e) {
                                        e.printStackTrace();
                                    } catch (InvocationTargetException e) {
                                        e.printStackTrace();
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    break;
//                                }
//                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


   /** ======================================================================================================== */

    /**
     * @param uri
     *         The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *         The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *         The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     *  判断是否是Google相册的图片，类似于content://com.google.android.apps.photos.content/...
     **/
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     *  判断是否是Google相册的图片，类似于content://com.google.android.apps.photos.contentprovider/0/1/mediakey:/local%3A821abd2f-9f8c-4931-bbe9-a975d1f5fabc/ORIGINAL/NONE/1075342619
     **/
    public static boolean isGooglePlayPhotosUri(Uri uri) {
        return "com.google.android.apps.photos.contentprovider".equals(uri.getAuthority());
    }

    /**
     * Google相册图片获取路径
     **/
    public static String getImageUrlWithAuthority(Context context, Uri uri) {
        InputStream is = null;
        if (uri.getAuthority() != null) {
            try {
                is = context.getContentResolver().openInputStream(uri);
                Bitmap bmp = BitmapFactory.decodeStream(is);
                return writeToTempImageAndGetPathUri(context, bmp).toString();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    /**
     * 将图片流读取出来保存到boas中
     **/
    public static Uri writeToTempImageAndGetPathUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
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


}
