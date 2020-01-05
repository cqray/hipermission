package com.uguke.permission;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

/**
 * Created by zyy on 2018/3/12.
 * 直接跳转到权限后返回，可以监控权限授权情况，但是，跳转到应用详情页，无法监测权限情况
 * 是否要加以区分，若是应用详情页，则跳转回来后，onRestart检测所求权限，如果授权，则收回提示，如果没授权，则继续提示
 */
public class SettingUtil {

    private static final int PERMISSION_SETTING_FOR_RESULT = 9999;
    /** 华为 **/
    private static final String MANUFACTURER_HUAWEI = "Huawei";
    /** 魅族 **/
    private static final String MANUFACTURER_MEIZU = "Meizu";
    /** 小米 **/
    private static final String MANUFACTURER_XIAOMI = "Xiaomi";
    /** 索尼 **/
    private static final String MANUFACTURER_openSony = "openSony";
    /** OPPO **/
    private static final String MANUFACTURER_OPPO = "OPPO";
    /** LG **/
    private static final String MANUFACTURER_LG = "LG";
    /** vivo **/
    private static final String MANUFACTURER_VIVO = "vivo";
    /** 三星 **/
    private static final String MANUFACTURER_SAMSUNG = "samsung";
    /** 乐视 **/
    private static final String MANUFACTURER_LETV = "Letv";
    /** 中兴 **/
    private static final String MANUFACTURER_ZTE = "ZTE";
    /** 酷派 **/
    private static final String MANUFACTURER_YULONG = "YuLong";
    /** 联想 **/
    private static final String MANUFACTURER_LENOVO = "LENOVO";

    private static boolean isAppSettingOpen = false;

    /**
     * 跳转到相应品牌手机系统权限设置页，如果跳转不成功，则跳转到应用详情页
     * 这里需要改造成返回true或者false，应用详情页:true，应用权限页:false
     */
    public static void openSetting(Activity act){
        switch (Build.MANUFACTURER){
            case MANUFACTURER_HUAWEI: openHuawei(act); break;
            case MANUFACTURER_MEIZU: openMeizu(act); break;
            case MANUFACTURER_XIAOMI: openXiaomi(act); break;
            case MANUFACTURER_openSony: openSony(act); break;
            case MANUFACTURER_OPPO: openOppo(act); break;
            case MANUFACTURER_LG: openLG(act); break;
            case MANUFACTURER_LETV: openLetv(act); break;
            default:
                try {
                    //防止应用详情页也找不到，捕获异常后跳转到设置，这里跳转最好是两级，太多用户也会觉得麻烦，还不如不跳
                    openNativeSetting(act);
//                    activity.startActivityForResult(getNativeSettingIntent(activity), PERMISSION_SETTING_FOR_RESULT);
                } catch (Exception e) {
                    SystemConfig(act);
                }
                break;
        }
    }

    /**
     * 打开华为权限设置页
     */
    private static void openHuawei(Activity act) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", act.getPackageName());
            ComponentName comp = new ComponentName("com.huawei.systemmanager",
                    "com.huawei.permissionmanager.ui.MainActivity");
            intent.setComponent(comp);
            act.startActivityForResult(intent, PERMISSION_SETTING_FOR_RESULT);
            isAppSettingOpen = false;
        } catch (Exception e) {
            openNativeSetting(act);
        }
    }

    /**
     * 打开魅族权限设置页，测试时，点击无反应，具体原因不明
     */
    private static void openMeizu(Activity act) {
        try {
            Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            act.startActivity(intent);
            isAppSettingOpen = false;
        } catch (Exception e) {
            openNativeSetting(act);

        }
    }

    /**
     * 小米，功能正常
     */
    private static void openXiaomi(Activity act) {
        try {
            // MIUI 8 9
            Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
            localIntent.putExtra("extra_pkgname", act.getPackageName());
            act.startActivityForResult(localIntent, PERMISSION_SETTING_FOR_RESULT);
            isAppSettingOpen = false;
        } catch (Exception e) {
            try {
                // MIUI 5/6/7
                Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                localIntent.putExtra("extra_pkgname", act.getPackageName());
                act.startActivityForResult(localIntent, PERMISSION_SETTING_FOR_RESULT);
                isAppSettingOpen = false;
            } catch (Exception e1) {
                // 否则跳转到应用详情
                openNativeSetting(act);
//                activity.startActivityForResult(getNativeSettingIntent(activity), PERMISSION_SETTING_FOR_RESULT);
                //这里有个问题，进入活动后需要再跳一级活动，就检测不到返回结果
//                activity.startActivity(getNativeSettingIntent());
            }
        }
    }

    /**
     * 索尼，6.0以上的手机非常少，基本没看见
     */
    private static void openSony(Activity act) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            ComponentName comp = new ComponentName("com.openSonymobile.cta", "com.openSonymobile.cta.SomcCTAMainActivity");
            intent.setComponent(comp);
            act.startActivity(intent);
            isAppSettingOpen = false;
        } catch (Exception e) {
            openNativeSetting(act);

        }
    }

    /**
     * 打开OPPO权限管理页
     */
    private static void openOppo(Activity act) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            ComponentName comp = new ComponentName("com.color.safecenter",
                    "com.color.safecenter.permission.PermissionManagerActivity");
            intent.setComponent(comp);
            act.startActivity(intent);
            isAppSettingOpen = false;
        } catch (Exception e) {
            openNativeSetting(act);
        }
    }

    /**
     * LG经过测试，正常使用
     */
    private static void openLG(Activity act) {
        try {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            ComponentName comp = new ComponentName("com.android.settings",
                    "com.android.settings.Settings$AccessLockSummaryActivity");
            intent.setComponent(comp);
            act.startActivity(intent);
            isAppSettingOpen = false;
        } catch (Exception e){
            openNativeSetting(act);

        }
    }

    /**
     * 乐视6.0以上很少，基本都可以忽略了，现在乐视手机不多
     */
    private static void openLetv(Activity act) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            ComponentName comp = new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.PermissionAndApps");
            intent.setComponent(comp);
            act.startActivity(intent);
            isAppSettingOpen = false;
        } catch (Exception e) {
            openNativeSetting(act);
        }
    }

    /**
     * 360只能打开到自带安全软件
     */
    public static void _360(Activity act) {
        try {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            ComponentName comp = new ComponentName("com.qihoo360.mobilesafe", "com.qihoo360.mobilesafe.ui.index.AppEnterActivity");
            intent.setComponent(comp);
            act.startActivity(intent);
        } catch (Exception e) {
            openNativeSetting(act);

        }
    }

    /**
     * 系统设置界面
     */
    private static void SystemConfig(Activity act) {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        act.startActivity(intent);
    }

    /**
     * 打开原生应用详情页
     */
    private static void openNativeSetting(Activity act){
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", act.getPackageName(), null));
        } else {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", act.getPackageName());
        }
        act.startActivityForResult(intent, PERMISSION_SETTING_FOR_RESULT);
        isAppSettingOpen = true;
    }
}