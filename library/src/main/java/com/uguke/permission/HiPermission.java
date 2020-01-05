package com.uguke.permission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述：权限管理
 */
public class HiPermission {

    /** 单个权限请求 **/
    private static final int PERMISSION_SINGLE = 0;
    /** 多个权限请求 **/
    private static final int PERMISSION_MULTI = 1;

    /** Manifest文件申请的权限 **/
    private static String [] manifestPermissions;

    private Activity act;
    /** 权限列表 **/
    private List<Permission> permissions;

    /** 请求成功的权限回调 **/
    private OnGrantedListener grantedListener;
    /** 请求失败的权限回调 **/
    private OnDeniedListener deniedListener;
    /** 需要解释的权限回调 **/
    private OnRationaleListener rationaleListener;

    public static HiPermission with(Activity act) {
        return new HiPermission(act);
    }

    public static HiPermission with(Fragment fragment) {
        return new HiPermission(fragment.getActivity());
    }

    public static HiPermission with(android.app.Fragment fragment) {
        return new HiPermission(fragment.getActivity());
    }

    /**
     * 功能描述：开启应用详情页
     * @param context   上下文
     */
    public static void openAppDetail(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        if (! (context instanceof Activity))
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static boolean checkSelf(@NonNull Context context,
                                    @NonNull final Permission permission) {
        initPermissions(context);
        // 筛选有效权限
        filterPermission(new ArrayList<Permission>() {
            {
                add(permission);
            }
        });
        return Build.VERSION.SDK_INT < 23 || ActivityCompat.checkSelfPermission(context,
                permission.getContent()) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkSelf(@NonNull Context context,
                                    @NonNull List<Permission> permissions) {
        initPermissions(context);
        for (Permission permission : permissions) {
            if (!checkSelf(context, permission))
                return false;
        }
        return true;
    }

    public static boolean checkSelf(@NonNull Context context,
                                    @NonNull Permission... permissions) {
        initPermissions(context);
        for (Permission permission : permissions) {
            if (!checkSelf(context, permission))
                return false;
        }
        return true;
    }

    // 初始化Manifest中的权限
    private static void initPermissions(Context context) {
        if (manifestPermissions != null)
            return;
        PackageInfo pi;
        try {
            pi = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_PERMISSIONS);
            if (pi != null) {
                manifestPermissions = pi.requestedPermissions;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void filterPermission(List<Permission> permissions) {
        // 获取需要请求的有效的权限
        for (int i = permissions.size() - 1; i >= 0; i--) {
            Permission permission = permissions.get(i);
            for (String p : manifestPermissions) {
                if (permission.getGroup().contains(p)) {
                    permission.setContent(p);
                    break;
                }
            }
            if (permission.getContent() == null)
                permissions.remove(permission);
        }
    }

    private HiPermission(Activity act) {
        this.act = act;
        this.permissions = new ArrayList<>();
        initPermissions(act);
    }

    public HiPermission permission(@NonNull Permission permission) {
        this.permissions.clear();
        permissions.add(permission);
        filterPermission(this.permissions);
        return this;
    }

    public HiPermission permissions(@NonNull List<Permission> permissions) {
        this.permissions.clear();
        for (Permission permission : permissions) {
            if (!this.permissions.contains(permission)) {
                this.permissions.add(permission);
            }
        }
        filterPermission(this.permissions);
        return this;
    }

    public HiPermission permissions(@NonNull Permission... permissions) {
        this.permissions.clear();
        for (Permission permission : permissions) {
            if (!this.permissions.contains(permission)) {
                this.permissions.add(permission);
            }
        }
        filterPermission(this.permissions);
        return this;
    }

    public HiPermission onGranted(OnGrantedListener listener) {
        this.grantedListener = listener;
        return this;
    }

    public HiPermission onDenied(OnDeniedListener listener) {
        this.deniedListener = listener;
        return this;
    }

    public HiPermission onRationale(OnRationaleListener listener) {
        this.rationaleListener = listener;
        return this;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void request() {
        if (checkSelf(act, permissions))
            return;
        HiCache.getInstance().put(act.getClass().getName(), this);
        // 启动PermissionActivity
        Intent intent = new Intent(act, PermissionActivity.class);
        intent.putExtra(PermissionActivity.class.getName(),
                act.getClass().getName());
        act.overridePendingTransition(0, 0);
        act.startActivity(intent);
    }

    void request(Activity act) {
        // 如果不需要做权限请求
        if (Build.VERSION.SDK_INT < 23 ||
                manifestPermissions == null)
            return;

        // 如果是请求单一权限
        if (permissions.size() == 1) {
            Permission permission = permissions.get(0);
            if (!checkSelf(act, permission)) {
                ActivityCompat.requestPermissions(act, new String[] {
                        permission.getContent() }, PERMISSION_SINGLE);
            }
        } else if (permissions.size() > 1) {
            List<String> denied = new ArrayList<>();
            for (Permission permission : permissions) {
                if (!checkSelf(act, permission)) {
                    denied.add(permission.getContent());
                }
            }
            // 如果有被拒绝的权限
            if (denied.size() > 0) {
                ActivityCompat.requestPermissions(act,
                        denied.toArray(new String[denied.size()]), PERMISSION_MULTI);
            }
        }
    }

    void notifyHiPermission(int code, String[] permissions, int[] results) {
        if (Build.VERSION.SDK_INT < 23)
            return;
        // 请求成功的权限
        List<Permission> granted = new ArrayList<>();
        // 请求失败的权限
        List<Permission> denied = new ArrayList<>();
        // 需要解释的权限
        List<Permission> ration = new ArrayList<>();

        if ((this.permissions.size() == 1 && PERMISSION_SINGLE == code) ||
                this.permissions.size() > 0 && PERMISSION_MULTI == code) {
            for (String content : permissions) {
                if (results[0] == PackageManager.PERMISSION_GRANTED) {
                    granted.add(Permission.getPermission(content));
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(act, content)) {
                        denied.add(Permission.getPermission(content));
                    } else {
                        ration.add(Permission.getPermission(content));
                    }
                }
            }
            // 请求成功的权限回调
            if (granted.size() > 0 && grantedListener != null)
                grantedListener.onGranted(granted);
            // 请求失败的权限回调
            if (denied.size() > 0 && deniedListener != null)
                deniedListener.onDenied(denied);
            // 需要解释的权限回调
            if (ration.size() > 0 && rationaleListener != null)
                rationaleListener.onRationale(ration);
        }
    }

    public interface OnGrantedListener {
        void onGranted(List<Permission> permissions);
    }

    public interface OnDeniedListener {
        void onDenied(List<Permission> permissions);
    }

    public interface OnRationaleListener {
        void onRationale(List<Permission> permissions);
    }

}
