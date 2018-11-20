package com.example.simulation.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;

public class Permissions {
    public static boolean requestPermission(
            Activity activity, int requestCode, String... permissions) {
        boolean granted = true;
        ArrayList<String> permissionsNeeded = new ArrayList<>();
        System.out.println("------------4----------------");
        for (String s : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(activity, s);
            System.out.println("------------gfggg " + permissionCheck + " ----------------");
            boolean hasPermission = (permissionCheck == PackageManager.PERMISSION_GRANTED);
            System.out.println("------------boolean " + hasPermission + " ----------------");
            granted &= hasPermission;
            if (!hasPermission) {
                permissionsNeeded.add(s);
                System.out.println("------------5----------------");
            }
        }

        if (granted) {
            return true;
        } else {
            ActivityCompat.requestPermissions(activity,
                    permissionsNeeded.toArray(new String[permissionsNeeded.size()]),
                    requestCode);

            System.out.println("------------6----------------");
            return false;
        }
    }


    public static boolean permissionGranted(
            int requestCode, int permissionCode, int[] grantResults) {
        if (requestCode == permissionCode) {
            return grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }
}
