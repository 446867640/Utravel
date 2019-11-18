package com.utravel.app.ui.camera;

import android.net.Uri;

import com.utravel.app.delegates.PermissionCheckerDelegate;
import com.utravel.app.utils.Util;

/**
 * 照相机调用类
 */
public class LatteCamera {

    public static Uri creareCropFile(){
        return Uri.parse(Util.createFile("crop_image",
                Util.getFileNameByTime("IMG","jpg") ).getPath());
    }

    public static void start(PermissionCheckerDelegate delegate){
        new CameraHandler(delegate).beginCameraDialog();
    }
}
