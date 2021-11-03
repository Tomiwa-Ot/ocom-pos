package com.szzcs.smartpos.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.szzcs.smartpos.MyApp;
import com.szzcs.smartpos.utils.update.UpdateConfig;
import com.szzcs.smartpos.utils.update.base.InstallStrategy;
import com.szzcs.smartpos.utils.update.base.UpdateParser;
import com.szzcs.smartpos.utils.update.model.CheckEntity;
import com.szzcs.smartpos.utils.update.model.Update;
import com.szzcs.smartpos.utils.update.util.ActivityManager;
import com.zcs.sdk.Sys;

import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class Config {

    public static final String BASE_URL = "http://tms.szzcs.com:7099/pay/";
    public static final String CHECK_FIRMWARE_UPDATE = BASE_URL + "tms/getUpgradeInf.json";// 检查更新
    public static final String CHECK_UPDATE = BASE_URL + "tms/getAppUpgradeInf.json";// 检查APP更新
    /* 服务端返回的结果 */
    public static final String RES_OK = "000000";
    public static final String RES_ERR = "999999";

    public static final String NET_CONN_ERROR = "netException";
    public static final String SERVER_CONN_ERROR = "webException";
    public static final String NET_EXCEPTION = "exception";
    public static final String REQUEST_YES = "yes";
    public static final String REQUEST_NO = "no";
    public static final String REQUEST_NULL = "null";
    public static final String ERROR = "ERROR";
    public static final String SUCCESS = "SUCCESS";
    public static final String EXIST = "EXIST";// 用户已存在
    /* inten跳转状态码 */
    public static final String REQUEST_CODE = "requstCode";
    public static final int SEARCH_TO_RESULT = 1;
    public static final int LIST_TO_RESULT = 2;
    public static final int COLLECT_TO_RESULT = 3;
    public static final int PUBLISH_TO_RESULT = 7;

    // update
    public static final String APP_NAME = "appName";
    public static final String APP_VERSION = "appVersion";
    public static final String SYS_TYPE = "sysType";
    public static final String SYS_TYPE_VALUE = "Android";
    public static final String CHECK_STATE = "checkState";
    public static final String FILE_URL = "fileUrl";
    public static final String FILE_DESC = "fileDesc";

    public static final String FIRMWARE_NAME = "firmWareName";
    public static final String FIRMV_ERSION = "firmVersion";
    public static final String PID = "pid";

    private static Config sUtils;
    private static Application sApplication;

    public static Config getInstance() {
        if (sUtils == null)
            sUtils = new Config();
        return sUtils;
    }

    public static Config init(final Application app) {
        getInstance();
        if (sApplication == null) {
            if (app == null) {
                sApplication = getApplicationByReflect();
            } else {
                sApplication = app;
            }
        } else {
            if (app != null && app.getClass() != sApplication.getClass()) {
                sApplication = app;
            }
        }
        return sUtils;
    }

    public static Application getApp() {
        if (sApplication != null)
            return sApplication;
        Application app = getApplicationByReflect();
        init(app);
        return app;
    }

    private static Application getApplicationByReflect() {
        try {
            @SuppressLint("PrivateApi")
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Object thread = activityThread.getMethod("currentActivityThread").invoke(null);
            Object app = activityThread.getMethod("getApplication").invoke(thread);
            if (app == null) {
                throw new NullPointerException("u should init first");
            }
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
        throw new NullPointerException("u should init first");
    }

    public Config setUpdate() {
        // Update
        String versionName = Kits.Package.getVersionName(sApplication);
        if (versionName != null && versionName.length() > 5) {
            versionName = versionName.substring(0, 5);
        }
        String appName = "smartpos";
        final int versionCode = Kits.Package.getVersionCode(sApplication);
        final String pkgName = Kits.Package.getPackageName(sApplication);

        final HashMap<String, String> params = new HashMap<>();
        params.put(Config.APP_NAME, appName);
        params.put(Config.APP_VERSION, "V" + versionName);
        params.put(Config.SYS_TYPE, Config.SYS_TYPE_VALUE);

        UpdateConfig.getConfig()
                .setCheckEntity(new CheckEntity().setUrl(CHECK_UPDATE).setMethod("POST").setParams(params))
                .setUpdateParser(new UpdateParser() {
                    @Override
                    public Update parse(String response) throws Exception {
                        Log.d("CheckUpdate params", params.toString());
                        Log.d("CheckUpdate res", response);
                        Update update = null;
                        JSONObject jsonObject = new JSONObject(response);
                        String state = jsonObject.getString(CHECK_STATE);
                        if (!"2".equals(state)) {
                            update = new Update();
                            String url = jsonObject.getString(FILE_URL);
                            String desc = jsonObject.getString(FILE_DESC);
                            update.setUpdateUrl(BASE_URL + url);
                            update.setUpdateContent(desc);
                            update.setForced(false);
                            update.setVersionCode(versionCode + 1);
                            //update.setVersionName(finalVersionName);
                            update.setPkg(pkgName);
                        } else {
                            Looper.prepare();
                            Toast.makeText(getApp(), "No update", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                        return update;
                    }
                })
                .setInstallStrategy(new InstallStrategy() {
                    @Override
                    public void install(Context context, String filename, Update update) {
                        ProgressDialog.show(ActivityManager.get().topActivity(), null, "Installing");
                        silentInstall(filename);
                    }
                });
        return this;
    }

    public static void silentInstall(String fileName) {
        Sys sys = MyApp.sDriverManager.getBaseSysDevice();
        sys.installApp2NoAuth(sApplication, fileName);
    }

    public static void startApp(String pkg, String cls) {
        PackageInfo packageInfo = null;
        PackageManager pm = getApp().getPackageManager();
        try {
            packageInfo = pm.getPackageInfo(pkg, 0);
            if (packageInfo != null) {
                Intent i = new Intent();
                i.setComponent(new ComponentName(pkg, cls))
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                getApp().startActivity(i);
            } else {
                ToastManager.showShort(getApp(), "Without this application");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastManager.showShort(getApp(), "Without this application");
        }
    }
}
