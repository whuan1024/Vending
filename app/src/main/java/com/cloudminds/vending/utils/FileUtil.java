package com.cloudminds.vending.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.Nullable;

public class FileUtil {

    public static SimpleDateFormat sFileNameDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());

    /**
     * 存储图片
     *
     * @param dirPath  存储到哪个目录
     * @param fileName 文件名（不需要文件名后缀）
     * @param bitmap
     */
    public static void saveBitmap(String dirPath, String fileName, Bitmap bitmap) {
        saveBitmap(dirPath + File.pathSeparator + fileName + File.separator + "jpeg", bitmap);
    }

    /**
     * 存储图片
     *
     * @param filePath 图片完整的绝对路径（含文件名后缀）
     * @param bitmap
     */
    public static void saveBitmap(String filePath, Bitmap bitmap) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 生成/获取一个新的照片路径
     *
     * @param context
     * @param cameraName 多个摄像头同时拍照需要区分照片来自于哪个摄像头
     * @return
     */
    public static String getPhotoPath(Context context, @Nullable String cameraName) {
        File storageDir = getOwnCacheDirectory(context, "Camera");
        String filename;
        if (TextUtils.isEmpty(cameraName)) {
            filename = sFileNameDateFormat.format(new Date()) + ".jpeg";
        } else {
            filename = cameraName + "_" + sFileNameDateFormat.format(new Date()) + ".jpeg";
        }
        return storageDir.getPath() + "/" + filename;
    }

    private static File getOwnCacheDirectory(Context context, String cacheDir) {
        File appCacheDir = null;
        //判断SD卡正常挂载并且有权限的时候创建文件
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) &&
                hasExternalStoragePermission(context)) {
            appCacheDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), cacheDir);
        }
        if (appCacheDir == null || (!appCacheDir.exists() && !appCacheDir.mkdirs())) {
            appCacheDir = new File(context.getCacheDir(), cacheDir);
        }
        return appCacheDir;
    }

    private static boolean hasExternalStoragePermission(Context context) {
        int permission = context.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE");
        return permission == PackageManager.PERMISSION_GRANTED;
    }
}
