package com.uguke.demo.permission;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.uguke.permission.HiPermission;
import com.uguke.permission.Permission;
import com.uguke.permission.RomUtils;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        List<Permission> permissions = Arrays.asList(
                Permission.PHONE,
                Permission.CALENDAR,
                Permission.STORAGE,
                Permission.CAMERA,
                Permission.MICROPHONE,
                Permission.SENSORS,
                Permission.SMS,
                Permission.LOCATION,
                Permission.CONTACTS
        );

        if (!HiPermission.checkSelf(permissions)) {

            // 需要什么权限增加什么权限
            HiPermission.with()
                    .permissions()                  // 可以清空权限
                    .permission(Permission.CAMERA)  // 单个权限请求
                    .permissions(
                            Permission.CAMERA,
                            Permission.CALENDAR)    // 多个权限请求
                    .permissions(permissions)       // 多个权限请求
                    .onGranted(new HiPermission.OnGrantedListener() {
                        @Override
                        public void onGranted(List<Permission> permissions) {

                        }
                    })
                    .onDenied(new HiPermission.OnDeniedListener() {
                        @Override
                        public void onDenied(List<Permission> permissions) {

                        }
                    })
                    .onRationale(new HiPermission.OnRationaleListener() {
                        @Override
                        public void onRationale(List<Permission> permissions) {

                        }
                    })
                    .request();
        }


    }
}
