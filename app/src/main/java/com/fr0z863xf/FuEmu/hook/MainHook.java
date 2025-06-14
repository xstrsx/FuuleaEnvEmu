package com.fr0z863xf.FuEmu.hook;

import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Objects;



import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
public class MainHook implements IXposedHookLoadPackage {

    //private static final XSharedPreferences prefs = new XSharedPreferences("com.fr0z863xf.FuEmu");

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable  {
        if (!Objects.equals(lpparam.packageName, "com.fuulea.venus.g")) return;
        prefsUtils prefs = new prefsUtils();
        XposedHelpers.findAndHookMethod("com.stub.StubApp", lpparam.classLoader, "onCreate", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedBridge.log("[FuEmu]app已加载，开始hook外壳");
                XposedBridge.log("[FuEmu]com.stub.StubApp->beforeHookedMethod->onCreate: " + param.thisObject);
                XposedBridge.log("[FuEmu]com.stub.StubApp: " + lpparam.classLoader);
                ClassLoader finalClassLoader = getClassloader();
                XposedBridge.log("[FuEmu]finalClassLoader: " + finalClassLoader);
                XposedBridge.log("[FuEmu]解密完成");
                //XposedBridge.log("[FuEmu]测试Xprefs:device_info" + prefs.getString("device_info", "ERR!"));
                //Class MainActivityC = XposedHelpers.findClass("com.fuulea.venus.MainActivity",finalClassLoader);
                XposedHelpers.findAndHookMethod("com.fuulea.venus.MainActivity", finalClassLoader, "onCreate", Bundle.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        XposedBridge.log("[FuEmu]com.fuulea.venus.g.MainActivity->onCreate");
                        Toast.makeText((Activity) param.thisObject, "[FuEmu]:模块已加载", Toast.LENGTH_SHORT).show();
                    }
                });
                //关键hook，修改版本和品牌。直接修改常量更方便，无需对具体的获取函数进行hook
                //Class<?> buildVersionClass = XposedHelpers.findClass("android.os.Build$VERSION", finalClassLoader);
                XposedHelpers.setStaticObjectField(XposedHelpers.findClass("android.os.Build$VERSION", finalClassLoader), "RELEASE", prefs.getString("android_version", Build.VERSION.RELEASE));
                XposedHelpers.setStaticObjectField(XposedHelpers.findClass("android.os.Build", finalClassLoader),"BRAND",prefs.getString("brand", Build.BRAND));
                XposedHelpers.setStaticObjectField(XposedHelpers.findClass("android.os.Build", finalClassLoader),"DEVICE",prefs.getString("device_info", Build.DEVICE));
                XposedHelpers.setStaticObjectField(XposedHelpers.findClass("android.os.Build", finalClassLoader),"SERIAL",prefs.getString("serial_number", Build.SERIAL));

                //关键hook，修改设备名称、管控环境、设备型号

                XposedHelpers.findAndHookMethod("com.learnium.RNDeviceInfo.RNDeviceModule",finalClassLoader,"getDeviceNameSync", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        XposedBridge.log("[FuEmu]com.learnium.RNDeviceInfo.RNDeviceModule->getDeviceNameSync");
                        param.setResult(prefs.getString("device_name", "Oops"));
                    }
                });


                /*
                XposedHelpers.findAndHookMethod("android.provider.Settings.Secure",finalClassLoader,"getString", ContentResolver.class, String.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        XposedBridge.log("[FuEmu]android.provider.Settings.Secure->getString");
                        if (param.args[1].equals("bluetooth_name") || param.args[1].equals("device_name"))  param.setResult(prefs.getString("device_name", Build.DEVICE));
                    }
                });

                 */
                /*
                XposedHelpers.findAndHookMethod("com.fuulea.venus.reactNative.packages.GovernanceModule",finalClassLoader,"getGovernanceName", Bundle.class, new XC_MethodHook() {
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        XposedBridge.log("[FuEmu]com.fuulea.venus.reactNative.packages.GovernanceModule->getGovernanceName");
                        param.setResult(prefs.getString("governance_environment", "HEM"));
                    }
                });

                 */
                /*Class<?> GovC = XposedHelpers.findClass("com.fuulea.venus.reactNative.packages.governance.IGovernancePlugin", finalClassLoader);
                XposedBridge.hookAllMethods(GovC,"getName", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        XposedBridge.log("[FuEmu]com.fuulea.venus.reactNative.packages.governance.IGovernancePlugin->getName");
                        param.setResult(prefs.getString("governance_environment", "HEM"));
                    }
                });

                 */
                XposedHelpers.findAndHookMethod("com.fuulea.venus.reactNative.packages.governance.SystemGovernance",finalClassLoader,"getCurrentGovernanceName", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        XposedBridge.log("[FuEmu]com.fuulea.venus.reactNative.packages.governance.SystemGovernance->getCurrentGovernanceName");
                        param.setResult(prefs.getString("governance_environment", "HEM"));
                    }
                });
                /*
                XposedHelpers.findAndHookMethod("com.fuulea.venus.reactNative.packages.governance.IGovernancePlugin",finalClassLoader,"getSerialNumber", new XC_MethodHook() {
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        XposedBridge.log("[FuEmu]com.fuulea.venus.reactNative.packages.governance.IGovernancePlugin->getSerialNumber");
                        param.setResult(prefs.getString("serial_number", "unspecified"));
                    }
                });

                 */
                XposedHelpers.findAndHookMethod("com.fuulea.venus.reactNative.packages.governance.SystemGovernance",finalClassLoader,"getSN", new XC_MethodHook() {
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        XposedBridge.log("[FuEmu]com.fuulea.venus.reactNative.packages.governance.SystemGovernance->getSN");
                        param.setResult(prefs.getString("serial_number", "unspecified"));
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
