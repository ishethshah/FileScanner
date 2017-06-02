package com.ishita.filescanner.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by ishita on 5/29/17.
 */

public class PermissionsUtil {

    private static String READ_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE;

    public static String[] PERMISSIONS = new String[]{READ_PERMISSION};

    public static boolean isPermissionRequired(Context ctx) {
        return (ContextCompat.checkSelfPermission(ctx, READ_PERMISSION)) != PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isRationaleRequired(Activity activity) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, READ_PERMISSION);
    }
}