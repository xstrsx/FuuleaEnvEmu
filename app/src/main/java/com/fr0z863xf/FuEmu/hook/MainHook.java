package com.fr0z863xf.FuEmu.hook;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Objects;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
public class MainHook implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable  {
        if (!Objects.equals(lpparam.packageName, "com.fuulea.venus.g")) return;
        XposedHelpers.findAndHookMethod("com.stub.StubApp", lpparam.classLoader, "onCreate", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedBridge.log("app已加载，开始hook外壳");
                XposedBridge.log("com.stub.StubApp->beforeHookedMethod->onCreate: " + param.thisObject);
                XposedBridge.log("com.stub.StubApp: " + lpparam.classLoader);
                ClassLoader finalClassLoader = getClassloader();
                XposedBridge.log("finalClassLoader: " + finalClassLoader);
                XposedBridge.log("解密完成");
                //Class MainActivityC = XposedHelpers.findClass("com.fuulea.venus.MainActivity",finalClassLoader);
                XposedHelpers.findAndHookMethod("com.fuulea.venus.MainActivity", finalClassLoader, "onCreate", Bundle.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        XposedBridge.log("com.fuulea.venus.g.MainActivity->onCreate");
                        Toast.makeText((Activity) param.thisObject, "[FuEmu]:模块已加载", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public static ClassLoader getClassloader() {
        ClassLoader resultClassloader = null;
        Class<?> activityThreadClass = XposedHelpers.findClass("android.app.ActivityThread", null);
        Object currentActivityThread = XposedHelpers.callStaticMethod(activityThreadClass, "currentActivityThread");
        Object mBoundApplication = XposedHelpers.getObjectField(currentActivityThread, "mBoundApplication");
        Object loadedApkInfo = XposedHelpers.getObjectField(mBoundApplication, "info");
        Application mApplication = (Application) XposedHelpers.getObjectField(loadedApkInfo, "mApplication");
        resultClassloader = mApplication.getClassLoader();
        return resultClassloader;
    }
}
