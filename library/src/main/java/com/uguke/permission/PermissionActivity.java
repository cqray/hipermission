package com.uguke.permission;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * 功能描述：权限请求Activity
 */
public final class PermissionActivity extends Activity {

    private String key;
    private HiPermission hiPermission;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置透明背景
        getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        key = getIntent().getStringExtra(PermissionActivity.class.getName());
        hiPermission = HiCache.getInstance().get(key);
        hiPermission.request(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        hiPermission.notifyHiPermission(requestCode, permissions, grantResults);
        finish();
    }

    @Override
    protected void onDestroy() {
        HiCache.getInstance().remove(key);
        overridePendingTransition(0,0);
        super.onDestroy();
    }
}
