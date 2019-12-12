package com.cloudminds.vending.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class DeviceUnityCodeUtil {
    /**
     * 获取设备的唯一码方式（三种方式获取，前者获取到，不再往下执行）
     *
     * @param context
     * @return 返回设备的唯一码
     */
    @TargetApi(26)
    public static String getDeviceUnityCode(Context context) {
        // 先使用默认的imei, 获取系统imei的方式一直在变化，所以获取imei仅作为首选方案
        String unityCode = "";
        // the first, get imei of phone
        try {
            TelephonyManager telephonyManager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                unityCode = telephonyManager.getImei(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }

        // the second, get mac of phone
        if (unityCode == null || unityCode == "") {
            unityCode = DeviceUnityCodeUtil.getMac(context);//获取mac号
            unityCode = unityCode.replace(":", "");//过滤掉mac中间的：号
            unityCode = unityCode.toUpperCase();//统一把mac地址转换成为大写
        }
        return unityCode;
    }


    private static String getMac(Context context) {
        String strMac = null;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            strMac = getMacDefault(context);
            if (TextUtils.isEmpty(strMac)) {
                strMac = getMacAddress();
            }
            if (TextUtils.isEmpty(strMac)) {
                strMac = getMacFromHardware();
            }
            return strMac;
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            strMac = getMacAddress();
            if (TextUtils.isEmpty(strMac)) {
                strMac = getMacFromHardware();
            }
            return strMac;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            strMac = getMacFromHardware();
            return strMac;
        }
        return "02:00:00:00:00:00";
    }

    /**
     * Android  6.0 之前（不包括6.0）
     * 必须的权限  <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
     *
     * @param context
     * @return
     */
    private static String getMacDefault(Context context) {
        String mac = "";
        if (context == null) {
            return mac;
        }

        WifiManager wifi = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        if (wifi == null) {
            return mac;
        }
        WifiInfo info = null;
        try {
            info = wifi.getConnectionInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (info == null) {
            return null;
        }
        mac = info.getMacAddress();
        if (!TextUtils.isEmpty(mac)) {
            mac = mac.toUpperCase(Locale.ENGLISH);
        }
        return mac;
    }

    /**
     * Android 6.0（包括） - Android 7.0（不包括）
     * 通过读取文件的当时获取mac，在7.0之后可能会有权限访问问题
     *
     * @return
     */
    private static String getMacAddress() {
        String WifiAddress = "";
        try {
            WifiAddress = new BufferedReader(new FileReader(new File("/sys/class/net/wlan0/address"))).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return WifiAddress;
    }

    /**
     * 遍历循环所有的网络接口，找到接口是 wlan0
     * 必须的权限 <uses-permission android:name="android.permission.INTERNET" />
     *
     * @return
     */
    private static String getMacFromHardware() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) {
                    continue;
                }

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getQrCodeContent(Context context) {
        return "https://sit137.cloudminds.com?rcuCode=" + getDeviceUnityCode(context);
    }
}
