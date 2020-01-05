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
    private static final String MANUFACTURER_Sony = "openSony";
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
    public static boolean openSetting(Activity act) {
        switch (Build.MANUFACTURER) {
            case MANUFACTURER_HUAWEI: return openHuawei(act);
            case MANUFACTURER_MEIZU: return openMeizu(act);
            case MANUFACTURER_XIAOMI: return openXiaomi(act);
            case MANUFACTURER_Sony: return openSony(act);
            case MANUFACTURER_OPPO: return openOppo(act);
            case MANUFACTURER_LG: return openLG(act);
            case MANUFACTURER_LETV: return openLetv(act);
            default:
                try {
                    // 防止应用详情页也找不到，捕获异常后跳转到设置，
                    // 这里跳转最好是两级，太多用户也会觉得麻烦，还不如不跳
                    openNativeSetting(act);
                } catch (Exception e) {
                    SystemConfig(act);
                }
                break;
        }
        return false;
    }

    /**
     * 打开华为权限设置页
     */
    private static boolean openHuawei(Activity act) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", act.getPackageName());
            ComponentName comp = new ComponentName("com.huawei.systemmanager",
                    "com.huawei.permissionmanager.ui.MainActivity");
            intent.setComponent(comp);
            act.startActivityForResult(intent, PERMISSION_SETTING_FOR_RESULT);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 打开魅族权限设置页，测试时，点击无反应，具体原因不明
     */
    private static boolean openMeizu(Activity act) {
        try {
            Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra("packageName", act.getPackageName());
            act.startActivity(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 小米，功能正常
     */
    private static boolean openXiaomi(Activity act) {
        try {
            // MIUI 8 9
            Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            localIntent.setClassName("com.miui.securitycenter",
                    "com.miui.permcenter.permissions.PermissionsEditorActivity");
            localIntent.putExtra("extra_pkgname", act.getPackageName());
            act.startActivityForResult(localIntent, PERMISSION_SETTING_FOR_RESULT);
            return true;
        } catch (Exception e) {
            try {
                // MIUI 5/6/7
                Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                localIntent.setClassName("com.miui.securitycenter",
                        "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                localIntent.putExtra("extra_pkgname", act.getPackageName());
                act.startActivityForResult(localIntent, PERMISSION_SETTING_FOR_RESULT);
                return true;
            } catch (Exception e1) {
                return false;
            }
        }
    }

    /**
     * 索尼，6.0以上的手机非常少，基本没看见
     */
    private static boolean openSony(Activity act) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", act.getPackageName());
            ComponentName comp = new ComponentName("com.sonymobile.cta",
                    "com.sonymobile.cta.SomcCTAMainActivity");
            intent.setComponent(comp);
            act.startActivity(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 打开OPPO权限管理页
     */
    private static boolean openOppo(Activity act) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", act.getPackageName());
            ComponentName comp = new ComponentName("com.color.safecenter",
                    "com.color.safecenter.permission.PermissionManagerActivity");
            intent.setComponent(comp);
            act.startActivity(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * LG经过测试，正常使用
     */
    private static boolean openLG(Activity act) {
        try {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", act.getPackageName());
            ComponentName comp = new ComponentName("com.android.settings",
                    "com.android.settings.Settings$AccessLockSummaryActivity");
            intent.setComponent(comp);
            act.startActivity(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 乐视6.0以上很少，基本都可以忽略了，现在乐视手机不多
     */
    private static boolean openLetv(Activity act) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packageName", act.getPackageName());
            ComponentName comp = new ComponentName("com.letv.android.letvsafe",
                    "com.letv.android.letvsafe.PermissionAndApps");
            intent.setComponent(comp);
            act.startActivity(intent);
            return true;
        } catch (Exception e) {
            return false;
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
    private static void openNativeSetting(Activity act) {
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
    }
}