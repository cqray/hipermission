package com.uguke.permission;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.util.Stack;

final class Utils {

    static {
        initLifecycleCallbacks();
    }

    static class Holder {
        static final Utils INSTANCE = new Utils();
    }

    private Stack<Activity> mStack = new Stack<>();

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (getApplicationByReflect() == null) {
                mHandler.sendEmptyMessage(0);
                Log.e("数据", "注册3");
            } else {
                mHandler.removeCallbacksAndMessages(null);
                registerActivityLifecycleCallbacks();
                Log.e("数据", "注册4");
            }
            return false;
        }
    });

    private Utils() {}

    private static void initLifecycleCallbacks() {
        Utils utils = Holder.INSTANCE;
        utils.mHandler.sendEmptyMessage(0);
    }

    private void registerActivityLifecycleCallbacks() {
        Application application = getApplicationByReflect();
        assert application != null;
        Log.e("数据", "注册");
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                mStack.add(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {}

            @Override
            public void onActivityResumed(Activity activity) {}

            @Override
            public void onActivityPaused(Activity activity) {}

            @Override
            public void onActivityStopped(Activity activity) {}

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

            @Override
            public void onActivityDestroyed(Activity activity) {
                mStack.remove(activity);
            }
        });
    }

    /**
     * 反射获取Application
     */
    private static Application getApplicationByReflect() {
        try {
            @SuppressLint("PrivateApi")
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Object thread = activityThread.getMethod("currentActivityThread").invoke(null);
            Object app = activityThread.getMethod("getApplication").invoke(thread);
            return (Application) app;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    static Activity getTopActivity() {
        Utils utils = Holder.INSTANCE;
        if (utils.mStack.size() > 0) {
            return utils.mStack.lastElement();
        }
        Log.e("数据", "注册2");
        Log.e("数据", (getApplicationByReflect() == null) + "");
        throw new NullPointerException("you should init first.");
    }
}
