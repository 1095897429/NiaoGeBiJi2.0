package com.qmkj.niaogebiji.common.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-22
 * 描述: 一些常用的存储位置
 */
public class FileHelper {

    /** 圈子拍照的图片文件 /storage/mnt/Android/data/包名/files/Download/out_image.png */
    public static File getOutputCircleImageFile(Context context){
        return new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), System.currentTimeMillis() + ".png");
    }

    /** 头像拍照的图片文件 /storage/mnt/Android/data/包名/files/Download/out_image.png */
    public static File getOutputHeadImageFile(Context context){
        return new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "output_image.png");
    }


    /** 头像裁剪保存的图片文件 /scard/emulated/0/Android/data/包名/file/Download/headedit.png */
    public static File getOutputEditImageFile(Context context) {
        return new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "headedit.png");
    }

    /** 邀请好友的图片文件 /storage/mnt/Android/data/包名/files/Download/xxx.png */
    public static File getOutputInviteImageFile(Context context){
        return new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "invite.png");
    }

    /** 邀请好友的图片目录 /storage/mnt/Android/data/包名/files/Download/*/
    public static File getOutputInviteDirFile(Context context){
        return new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "");
    }


    /** 保存图片目录 /storage/mnt/Android/data/DCIM/ngbj/*/
    public static File getOutputImgDirFile(Context context){
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "");
    }










}
