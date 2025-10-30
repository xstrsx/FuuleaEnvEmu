package com.fr0z863xf.FuEmu.hook;

import static de.robv.android.xposed.XC_MethodReplacement.DO_NOTHING;

import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.net.http.X509TrustManagerExtensions;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
public class MainHook implements IXposedHookLoadPackage {

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

                // 关键hook，修改版本和品牌。直接修改常量更方便，无需对具体的获取函数进行hook
                // 实际上没几个是真正需要的:)
                XposedHelpers.setStaticObjectField(XposedHelpers.findClass("android.os.Build$VERSION", finalClassLoader), "RELEASE", prefs.getString("android_version", Build.VERSION.RELEASE));
                XposedHelpers.setStaticObjectField(XposedHelpers.findClass("android.os.Build", finalClassLoader),"BRAND",prefs.getString("brand", Build.BRAND));
                XposedHelpers.setStaticObjectField(XposedHelpers.findClass("android.os.Build", finalClassLoader),"MODEL",prefs.getString("model", Build.MODEL));
                XposedHelpers.setStaticObjectField(XposedHelpers.findClass("android.os.Build", finalClassLoader),"PRODUCT",prefs.getString("product", Build.PRODUCT));
                XposedHelpers.setStaticObjectField(XposedHelpers.findClass("android.os.Build", finalClassLoader),"MANUFACTURER",prefs.getString("manufacturer", Build.MANUFACTURER));
                XposedHelpers.setStaticObjectField(XposedHelpers.findClass("android.os.Build", finalClassLoader),"HARDWARE",prefs.getString("hardware", Build.HARDWARE));
                XposedHelpers.setStaticObjectField(XposedHelpers.findClass("android.os.Build", finalClassLoader),"FINGERPRINT",prefs.getString("fingerprint", Build.FINGERPRINT));
                XposedHelpers.setStaticObjectField(XposedHelpers.findClass("android.os.Build", finalClassLoader),"DISPLAY",prefs.getString("display", Build.DISPLAY));
                XposedHelpers.setStaticObjectField(XposedHelpers.findClass("android.os.Build", finalClassLoader),"BOARD",prefs.getString("board", Build.BOARD));
                XposedHelpers.setStaticObjectField(XposedHelpers.findClass("android.os.Build", finalClassLoader),"DEVICE",prefs.getString("device_info", Build.DEVICE));
                //XposedHelpers.setStaticObjectField(XposedHelpers.findClass("android.os.Build", finalClassLoader),"SERIAL",prefs.getString("serial_number", Build.SERIAL));
                //XposedHelpers.setStaticObjectField(XposedHelpers.findClass("android.os.Build", finalClassLoader),"BOOTLOADER",prefs.getString("bootloader", Build.BOOTLOADER));
                //XposedHelpers.setStaticObjectField(XposedHelpers.findClass("android.os.Build", finalClassLoader),"HOST",prefs.getString("host", Build.HOST));
                //XposedHelpers.setStaticObjectField(XposedHelpers.findClass("android.os.Build", finalClassLoader),"ID",prefs.getString("id", Build.ID));
                //XposedHelpers.setStaticObjectField(XposedHelpers.findClass("android.os.Build", finalClassLoader),"TAGS",prefs.getString("tags", Build.TAGS));
                //XposedHelpers.setStaticObjectField(XposedHelpers.findClass("android.os.Build", finalClassLoader),"TYPE",prefs.getString("type", Build.TYPE));
                //XposedHelpers.setStaticObjectField(XposedHelpers.findClass("android.os.Build", finalClassLoader),"USER",prefs.getString("user", Build.USER));
                //XposedHelpers.setStaticLongField(XposedHelpers.findClass("android.os.Build", finalClassLoader),"TIME", Long.parseLong(prefs.getString("time", String.valueOf(Build.TIME))));
                //XposedHelpers.setStaticObjectField(XposedHelpers.findClass("android.os.Build$VERSION", finalClassLoader),"CODENAME",prefs.getString("codename", Build.VERSION.CODENAME));
                //XposedHelpers.setStaticObjectField(XposedHelpers.findClass("android.os.Build$VERSION", finalClassLoader),"INCREMENTAL",prefs.getString("incremental", Build.VERSION.INCREMENTAL));
                //XposedHelpers.setStaticIntField(XposedHelpers.findClass("android.os.Build$VERSION", finalClassLoader),"SDK_INT", Integer.parseInt(prefs.getString("sdk_int", String.valueOf(Build.VERSION.SDK_INT))));
                //XposedHelpers.setStaticIntField(XposedHelpers.findClass("android.os.Build$VERSION", finalClassLoader),"SDK", Integer.parseInt(prefs.getString("sdk_int", String.valueOf(Build.VERSION.SDK_INT))));

                // Hook for Baseband version (基带版本)
                XposedHelpers.findAndHookMethod("android.os.Build", finalClassLoader, "getRadioVersion", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        String baseband = prefs.getString("baseband", null);
                        if (baseband != null) {
                            param.setResult(baseband);
                            XposedBridge.log("[FuEmu] Hooked getRadioVersion to: " + baseband);
                        }
                    }
                });

                // Hook for Kernel version (内核版本)
                XposedHelpers.findAndHookMethod("java.lang.System", finalClassLoader, "getProperty", String.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        if (param.args[0] != null && param.args[0].equals("os.version")) {
                            String kernelVersion = prefs.getString("kernel_version", null);
                            if (kernelVersion != null) {
                                param.setResult(kernelVersion);
                                XposedBridge.log("[FuEmu] Hooked os.version to: " + kernelVersion);
                            }
                        }
                    }
                });

                // Hook for Android ID
                XposedHelpers.findAndHookMethod("android.provider.Settings.Secure", finalClassLoader, "getString", ContentResolver.class, String.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        if (param.args[1] != null && param.args[1].equals("android_id")) {
                            String androidId = prefs.getString("android_id", null);
                            if (androidId != null) {
                                param.setResult(androidId);
                                XposedBridge.log("[FuEmu] Hooked android_id to: " + androidId);
                            }
                        }
                    }
                });


                // 关键hook，修改设备名称、管控环境、设备型号
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

                //绕过证书验证，方便抓包
//                try {
//                    XposedHelpers.findAndHookMethod("javax.net.ssl.X509TrustManager",finalClassLoader, "checkServerTrusted", java.security.cert.X509Certificate[].class, String.class, new XC_MethodHook() {
//                        @Override
//                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                            XposedBridge.log("[FuEmu][Util] Universal TrustManager->checkServerTrusted called. Bypassing check.");
//                            param.setResult(null);
//                        }
//                    });
//                    XposedBridge.log("[FuEmu][Util] Universal javax.net.ssl.X509TrustManager->checkServerTrusted hooked.");
//                } catch (Throwable e) {
//                    XposedBridge.log("[FuEmu][Util] Error hooking universal javax.net.ssl.X509TrustManager: " + e.getMessage());
//                }

                try {
                    XposedHelpers.findAndHookMethod("javax.net.ssl.SSLContext", finalClassLoader, "init", KeyManager[].class, TrustManager[].class, SecureRandom.class, new XC_MethodHook() {
                                @Override
                                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                    XposedBridge.log("[FuEmu][Util][SSLContext] SSLContext.init(KeyManager[], TrustManager[], SecureRandom) called.");
                                    param.args[1] = new TrustManager[]{ new NBTrustManager() };
                                }
                            }
                    );
                    XposedBridge.log("[FuEmu][Util] SSLContext hooks have been set up successfully.");
                } catch (Throwable e) {
                    XposedBridge.log("[FuEmu][Util] Error hooking SSLContext.init: " + e.getMessage());
                }

                try {
                    XposedHelpers.findAndHookConstructor(SSLSocketFactory.class, String.class, KeyStore.class, String.class, KeyStore.class,SecureRandom.class, new XC_MethodHook() {});
                } catch (Throwable ignored) {

                }


                try {
                    XposedHelpers.findAndHookMethod("com.android.org.conscrypt.TrustManagerImpl", finalClassLoader, "checkServerTrusted",X509Certificate[].class, String.class, String.class, new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            XposedBridge.log("[FuEmu][Util] Conscrypt TrustManagerImpl->checkServerTrusted(Cert,String,String) called. Bypassing check.");
                            return new ArrayList<X509Certificate>();
                        }
                    });
                    XposedBridge.log("[FuEmu][Util] Conscrypt TrustManagerImpl->checkServerTrusted(Cert,String,String) hooked.");
                } catch (Throwable e) {
                    XposedBridge.log("[FuEmu][Util] Error hooking Conscrypt TrustManagerImpl->checkServerTrusted(Cert,String,String): " + e.getMessage());
                }

                try {
                    XposedHelpers.findAndHookMethod("com.android.org.conscrypt.TrustManagerImpl", finalClassLoader, "checkServerTrusted", X509Certificate[].class, String.class, new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            XposedBridge.log("[FuEmu][Util] Conscrypt TrustManagerImpl->checkServerTrusted(Cert[],String) called. Bypassing check.");
                            return null;
                        }
                    });
                    XposedBridge.log("[FuEmu][Util] Conscrypt TrustManagerImpl->checkServerTrusted(Cert,String) hooked.");
                } catch (Throwable e) {
                    XposedBridge.log("[FuEmu][Util] Error hooking Conscrypt TrustManagerImpl->checkServerTrusted(Cert,String): " + e.getMessage());
                }

                try {
                    XposedHelpers.findAndHookMethod("com.android.org.conscrypt.TrustManagerImpl", finalClassLoader, "checkServerTrusted", X509Certificate[].class, String.class, SSLSession.class, new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            XposedBridge.log("[FuEmu][Util] Conscrypt TrustManagerImpl->checkServerTrusted(Cert[],String,SSLSession) called. Bypassing check.");
                            return new ArrayList<X509Certificate>();
                        }
                    });
                    XposedBridge.log("[FuEmu][Util] Conscrypt TrustManagerImpl->checkServerTrusted(Cert,String,SSLSession) hooked.");
                } catch (Throwable e) {
                    XposedBridge.log("[FuEmu][Util] Error hooking Conscrypt TrustManagerImpl->checkServerTrusted(Cert,String,SSLSession): " + e.getMessage());
                }

                try {
                    XposedHelpers.findAndHookMethod("com.android.org.conscrypt.TrustManagerImpl", finalClassLoader, "checkTrusted", X509Certificate[].class, String.class, SSLSession.class, SSLParameters.class, boolean.class, new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            XposedBridge.log("[FuEmu][Util] Conscrypt TrustManagerImpl->checkTrusted(Cert[],String,SSLSession,SSLParameters,boolean) called. Bypassing check.");
                            return new ArrayList<X509Certificate>();
                        }
                    });
                    XposedBridge.log("[FuEmu][Util] Conscrypt TrustManagerImpl->checkTrusted(Cert,String,SSLSession...) hooked.");
                } catch (Throwable e) {
                    XposedBridge.log("[FuEmu][Util] Error hooking Conscrypt TrustManagerImpl->checkTrusted(Cert,String,SSLSession...): " + e.getMessage());
                }

                try {
                    XposedHelpers.findAndHookMethod("com.android.org.conscrypt.TrustManagerImpl", finalClassLoader, "checkTrusted", X509Certificate[].class, byte[].class, byte[].class, String.class, String.class, boolean.class, new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            XposedBridge.log("[FuEmu][Util] Conscrypt TrustManagerImpl->checkTrusted(Cert,byte,byte,String,String,boolean) called. Bypassing check.");
                            return new ArrayList<X509Certificate>();
                        }
                    });
                    XposedBridge.log("[FuEmu][Util] Conscrypt TrustManagerImpl->checkTrusted(Cert,byte,byte...) hooked.");
                } catch (Throwable e) {
                    XposedBridge.log("[FuEmu][Util] Error hooking Conscrypt TrustManagerImpl->checkTrusted(Cert,byte,byte...): " + e.getMessage());
                }




                XposedHelpers.findAndHookMethod(X509TrustManagerExtensions.class, "checkServerTrusted", X509Certificate[].class, String.class, String.class, new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("[FuEmu][Util]X509TrustManagerExtensions->checkServerTrusted called");
                        return new ArrayList<X509Certificate>();
                    }
                });


                XposedHelpers.findAndHookMethod("android.security.net.config.NetworkSecurityTrustManager", finalClassLoader, "checkPins", List.class, DO_NOTHING);



                try {
                    XposedHelpers.findAndHookMethod("okhttp3.CertificatePinner", finalClassLoader, "check$okhttp", String.class, List.class, new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            XposedBridge.log("[FuEmu][Util]okhttp3.CertificatePinner->check$okhttp(String,List) called");
                            return null;
                        }
                    });
                    XposedBridge.log("[FuEmu][Util]okhttp3.CertificatePinner->check$okhttp(String,List) hooked");
                } catch(Throwable e) {
                    XposedBridge.log("[FuEmu][Util]okhttp3.CertificatePinner->check$okhttp(String,List) not found");
                }

                try {
                    XposedHelpers.findAndHookMethod("okhttp3.CertificatePinner",finalClassLoader,"check$okhttp", String.class, "kotlin.jvm.functions.Function0", new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            XposedBridge.log("[FuEmu][Util]okhttp3.CertificatePinner->check$okhttp(String,Function0) called");
                            return null;
                        }
                    });
                    XposedBridge.log("[FuEmu][Util]okhttp3.CertificatePinner->check$okhttp(String,Function0) hooked");
                } catch(Throwable e) {
                    XposedBridge.log("[FuEmu][Util]okhttp3.CertificatePinner->check$okhttp(String,Function0) not found");
                }

                try {
                    XposedHelpers.findAndHookMethod("okhttp3.CertificatePinner", finalClassLoader, "check", String.class, List.class, new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            XposedBridge.log("[FuEmu][Util]okhttp3.CertificatePinner->check(String,List) called");
                            return null;
                        }
                    });
                    XposedBridge.log("[FuEmu][Util]okhttp3.CertificatePinner->check(String,List) hooked");
                } catch(Throwable e) {
                    XposedBridge.log("[FuEmu][Util]okhttp3.CertificatePinner->check(String,List) not found");
                }

                try {
                    XposedHelpers.findAndHookMethod("okhttp3.internal.tls.OkHostnameVerifier", finalClassLoader, "verify", String.class, X509Certificate.class, new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                            XposedBridge.log("[FuEmu][Util]okhttp3.internal.tls.OkHostnameVerifier->verify(String,X509Certificate) called");
                            return true;
                        }
                    });
                    XposedBridge.log("[FuEmu][Util]okhttp3.internal.tls.OkHostnameVerifier->verify(String,X509Certificate) hooked");
                } catch(Throwable e) {
                    XposedBridge.log("[FuEmu][Util]okhttp3.internal.tls.OkHostnameVerifier->verify(String,X509Certificate) not found");
                }

                try {
                    XposedHelpers.findAndHookMethod("okhttp3.internal.tls.OkHostnameVerifier", finalClassLoader, "verify", String.class, SSLSession.class, new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                            XposedBridge.log("[FuEmu][Util]okhttp3.internal.tls.OkHostnameVerifier->verify(String,SSLSession) called");
                            return true;
                        }
                    });
                    XposedBridge.log("[FuEmu][Util]okhttp3.internal.tls.OkHostnameVerifier->verify(String,SSLSession) hooked");
                } catch (Throwable e) {
                    XposedBridge.log("[FuEmu][Util]okhttp3.internal.tls.OkHostnameVerifier->verify(String,SSLSession) not found");
                }


                if(prefs.getBoolean("utils_set_UseDeveloperSupport_true", false)) {
                    XposedHelpers.findAndHookMethod("com.fuulea.venus.reactNative.RNHost", finalClassLoader, "getUseDeveloperSupport", new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            return true;
                        }
                    });
                    XposedBridge.log("[FuEmu][Util]com.fuulea.venus.reactNative.RNHost->getUseDeveloperSupport() set to true");
                }


                if(prefs.getBoolean("utils_enableWebviewDebugging", false)) {
                    XposedHelpers.findAndHookMethod("com.fuulea.venus.reactNative.packages.NativeAbilityModule", finalClassLoader, "enableWebviewDebugging",  new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            return true;
                        }
                    });
                }


                if(prefs.getBoolean("utils_set_isWifiProxy_false", false)) {
                    XposedHelpers.findAndHookMethod("com.fuulea.venus.reactNative.packages.NativeAbilityModule", finalClassLoader, "isWifiProxy", "com.facebook.react.bridge.Promise", new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            XposedHelpers.callMethod(param.args[0], "resolve", false);
                            return null;
                        }
                    });
                }



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


    private class NBTrustManager extends X509ExtendedTrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {
            XposedBridge.log("[FuEmu][Util]NBTrustManager->checkClientTrusted called");
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {
            XposedBridge.log("[FuEmu][Util]NBTrustManager->checkServerTrusted called");
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {
            XposedBridge.log("[FuEmu][Util]NBTrustManager->checkClientTrusted called");
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {
            XposedBridge.log("[FuEmu][Util]NBTrustManager->checkServerTrusted called");
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            XposedBridge.log("[FuEmu][Util]NBTrustManager->checkClientTrusted called");
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            XposedBridge.log("[FuEmu][Util]NBTrustManager->checkServerTrusted called");
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
