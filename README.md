# HiPermission
权限管理
## 导入
1. 在build.gradle添加如下代码：<br>
```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
2. 添加依赖关系
```
dependencies {
	implementation 'com.github.uguker:hipermission:1.0.7'
}
```
# 使用方法
```
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        if (!HiPermission.checkSelf(this, permissions)) {

            // 需要什么权限增加什么权限
            HiPermission.with(this)
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

```
