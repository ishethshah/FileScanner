package com.ishita.filescanner.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by ishita on 5/29/17.
 */

public class AppUtils {
    /**
     * Checks if external storage is available to at least read
     *
     * @return whether the external storage is readable or not.
     */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }

    /**
     * Get extension from string.
     *
     * @param title file name with extention
     * @return extension of the file
     */
    public static String getExtension(String title) {
        String[] extTokens = title.split(Pattern.quote("."));

        if (extTokens.length > 1) {
            String ext = extTokens[extTokens.length - 1];
            if (TextUtils.isDigitsOnly(ext)) {
                ext = "Unknown";
            }
            return ext;
        }
        return "Unknown";
    }

    /**
     * Convert bytes into Kb or Mb for better readability
     *
     * @param size size of the file
     * @return convert meaningful size string.
     */
    public static String getConvertedSize(double size) {
        if (size > 1023) {
            size = size / 1024;
            if (size > 1023) {
                size = size / 1024;
                return String.format(Locale.US, "%.2f Mb", size);
            } else {
                return String.format(Locale.US, "%.2f Kb", size);
            }
        } else {
            return String.format(Locale.US, "%.2f bytes", size);
        }
    }

    public static void launchApplicationSettings(Context ctx) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", ctx.getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        ctx.startActivity(intent);
    }

}
