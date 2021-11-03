package com.szzcs.smartpos;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.szzcs.smartpos.qr.QRTestActivity;
import com.szzcs.smartpos.scan.PermissionsManager;
import com.szzcs.smartpos.utils.Config;
import com.szzcs.smartpos.utils.DialogUtils;
import com.szzcs.smartpos.utils.FeatureSupport;
import com.szzcs.smartpos.utils.FilePickerUtils;
import com.szzcs.smartpos.utils.Kits;
import com.szzcs.smartpos.utils.SDK_Result;
import com.szzcs.smartpos.utils.SystemInfoUtils;
import com.szzcs.smartpos.utils.update.UpdateBuilder;
import com.szzcs.smartpos.utils.update.UpdateConfig;
import com.szzcs.smartpos.utils.update.base.FileChecker;
import com.szzcs.smartpos.utils.update.base.FileCreator;
import com.szzcs.smartpos.utils.update.base.InstallStrategy;
import com.szzcs.smartpos.utils.update.base.UpdateChecker;
import com.szzcs.smartpos.utils.update.base.UpdateParser;
import com.szzcs.smartpos.utils.update.model.CheckEntity;
import com.szzcs.smartpos.utils.update.model.Update;
import com.zcs.sdk.Beeper;
import com.zcs.sdk.DriverManager;
import com.zcs.sdk.Led;
import com.zcs.sdk.LedLightModeEnum;
import com.zcs.sdk.SdkResult;
import com.zcs.sdk.Sys;
import com.zcs.sdk.util.PowerHelper;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yyzz on 2018/5/16.
 */

public class SettingsFragment extends PreferenceFragment {
    private static final String TAG = "SettingsFragment";
    private static final int BEEP_FREQUENCE = 4000;
    private static final int BEEP_TIME = 600;
    private static final int REQ_CODE_READ_PHONE = 0x01;
    private static final int REQ_CODE_PERMISSIONS = 0x02;
    private final String[] mPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private PermissionsManager mPermissionsManager;

    private DriverManager mDriverManager = MyApp.sDriverManager;
    private Sys mSys = mDriverManager.getBaseSysDevice();
    private Activity mActivity;
    private ProgressDialog mDialogInit;
    private Beeper mBeeper;
    private Led mLed;

    String title = "测试";
    private boolean mIsBeepFlag = true;
    // led blue is open
    boolean mIsBlue = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        addPreferencesFromResource(R.xml.pref_settings);
        mActivity = getActivity();

        //initSdk();
        checkPermission();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findPreference(getString(R.string.key_power_on)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                mSys.sysPowerOn();
                return true;
            }
        });
        findPreference(getString(R.string.key_power_off)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                mSys.sysPowerOff();
                return true;
            }
        });
        findPreference(getString(R.string.key_init)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final EditText editText = new EditText(mActivity);
                new AlertDialog.Builder(mActivity)
                        .setTitle("波特率")
                        .setView(editText)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                speed = Integer.valueOf(editText.getText().toString());
                                initSdk();
                            }
                        })
                        .show();
                return true;
            }
        });


        findPreference(getString(R.string.key_os)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                title = getString(R.string.pref_os);
                switchFragment(SettingsFragment.this, new InfoFragment());
                return true;
            }
        });
        findPreference(getString(R.string.key_card)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (PowerHelper.isZ91()) {
                    //final String[] items = {"Bank card", "Memory card"};
                    //new AlertDialog.Builder(getActivity())
                    //        .setTitle("Select card type")
                    //        .setSingleChoiceItems(items, 0,
                    //                new DialogInterface.OnClickListener() {
                    //                    @Override
                    //                    public void onClick(DialogInterface dialog, int which) {
                    //                        if (which == 0) {
                    //                            Config.startApp("com.sinpo.xnfc", "com.sinpo.xnfc.MainActivity");
                    //                        } else {
                    //                            Config.startApp("cc.metapro.nfc", "cc.metapro.nfc.home.MainActivity");
                    //                        }
                    //                        dialog.dismiss();
                    //                    }
                    //                }).show();
                    //Config.startApp("com.sinpo.xnfc", "com.sinpo.xnfc.MainActivity");
                    Config.startApp("com.glorious.gloriousnfc1", "com.glorious.gloriousnfc1.ActivityMain");


                } else {
                    title = getString(R.string.pref_card);
                    switchFragment(SettingsFragment.this, new CardFragment());
                }
                return true;
            }
        });


        findPreference(getString(R.string.key_print)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                title = getString(R.string.pref_print);
                switchFragment(SettingsFragment.this, new PrintFragment());
                return true;
            }
        });

        final Preference padPreference = findPreference(getString(R.string.key_pinPad));
        padPreference.setEnabled(false);
        padPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                title = getString(R.string.pref_pinPad);
                switchFragment(SettingsFragment.this, new PinpadFragment());
                return true;
            }
        });

        findPreference(getString(R.string.key_scan)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                title = getString(R.string.pref_scan);
                startActivity(new Intent(getActivity(), QRTestActivity.class));

                return true;
            }
        });
        findPreference(getString(R.string.key_whole_engine_test)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getActivity(), TestActivity.class));
                return true;
            }
        });
        findPreference(getString(R.string.key_update_app)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                checkUpdateApp();
                return true;
            }
        });
        findPreference(getString(R.string.key_update_firmware)).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                ListPreference listPreference = (ListPreference) preference;
                int i = listPreference.findIndexOfValue((String) newValue);
                if (i == 0) { // Network
                    checkUpdateFirmware();
                } else if (i == 1) { // Local
                    updateFirmwareLocal();
                }
                return false;
            }
        });


        // set beep
        findPreference(getString(R.string.key_beep)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (mBeeper == null) {
                    mBeeper = mDriverManager.getBeeper();
                }
                if (mIsBeepFlag) {
                    mIsBeepFlag = false;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int beep = mBeeper.beep(BEEP_FREQUENCE, BEEP_TIME);
                            Log.i(TAG, "set beep:\t" + beep);
                            /*if (beep != SdkResult.SDK_OK) {
                                DialogUtils.show(getActivity(), SDK_Result.obtainMsg(getActivity(), beep));
                            }*/
                            mIsBeepFlag = true;
                        }
                    }).start();
                }

                return false;
            }
        });
        // set led light
        findPreference(getString(R.string.key_led)).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                if (mLed == null)
                    mLed = mDriverManager.getLedDriver();
                ListPreference listPreference = (ListPreference) preference;
                final int index = listPreference.findIndexOfValue((String) newValue);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mIsBlue = false;
                        mLed.setLed(LedLightModeEnum.ALL, false);
                        if (index == 0) {
                            mLed.setLed(LedLightModeEnum.RED, true);

                        } else if (index == 1) {
                            mLed.setLed(LedLightModeEnum.GREEN, true);
                        } else if (index == 2) {
                            mLed.setLed(LedLightModeEnum.YELLOW, true);
                        } else if (index == 3) {
                            mLed.setLed(LedLightModeEnum.BLUE, true);
                            mIsBlue = true;
                        } else if (index == 4) {
                            mLed.setLed(LedLightModeEnum.ALL, true);
                        }

                    }
                }).start();
                return false;
            }
        });

        findPreference(getString(R.string.key_fingerprint)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(mActivity, FingerprintActivity.class));
                return true;
            }
        });

        FeatureSupport.optimize(this);
    }

    private void checkPermission() {
        mPermissionsManager = new PermissionsManager(mActivity) {
            @Override
            public void authorized(int requestCode) {
                if (requestCode == REQ_CODE_READ_PHONE) {
                    initSdk();
                }
            }

            @Override
            public void noAuthorization(int requestCode, String[] lacksPermissions) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setTitle("Warning");
                builder.setMessage("Please open permission");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PermissionsManager.startAppSettings(mActivity.getApplicationContext());
                    }
                });
                builder.create().show();
            }

            @Override
            public void ignore(int requestCode) {
                if (requestCode == REQ_CODE_READ_PHONE) {
                    initSdk();
                }
            }
        };
        mPermissionsManager.checkPermissions(this, REQ_CODE_READ_PHONE, Manifest.permission.READ_PHONE_STATE);
        mPermissionsManager.checkPermissions(this, REQ_CODE_PERMISSIONS, mPermissions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionsManager.recheckPermissions(requestCode, permissions, grantResults);
    }

    /**
     * update app
     */
    private void checkUpdateApp() {
        if (!Kits.NetWork.isNetworkConnected(mActivity)) {
            Toast.makeText(Config.getApp(), R.string.net_error, Toast.LENGTH_SHORT).show();
            return;
        }
        UpdateBuilder.create().check();
    }

    private void checkUpdateFirmware() {
        if (!Kits.NetWork.isNetworkConnected(mActivity)) {
            Toast.makeText(Config.getApp(), R.string.net_error, Toast.LENGTH_SHORT).show();
            return;
        }
        String[] fName = new String[1];
        final String[] fVerion = new String[1];
        String[] pid = new String[1];
        int ret = -1;
        ret = mSys.getDevName(fName);
        if (ret != SdkResult.SDK_OK) {
            Toast.makeText(Config.getApp(), R.string.read_dev_error, Toast.LENGTH_SHORT).show();
            return;
        }
        ret = mSys.getFirmwareVer(fVerion);
        if (ret != SdkResult.SDK_OK) {
            Toast.makeText(Config.getApp(), R.string.read_dev_error, Toast.LENGTH_SHORT).show();
            return;
        }
        ret = mSys.getPid(pid);
        if (ret != SdkResult.SDK_OK) {
            Toast.makeText(Config.getApp(), R.string.read_dev_error, Toast.LENGTH_SHORT).show();
            return;
        }

        final HashMap<String, String> params = new HashMap<>();
        params.put(Config.FIRMWARE_NAME, fName[0]);
        params.put(Config.FIRMV_ERSION, fVerion[0]);
        params.put(Config.PID, pid[0]);

        UpdateConfig updateConfig = UpdateConfig.createConfig()
                .setCheckEntity(new CheckEntity().setUrl(Config.CHECK_FIRMWARE_UPDATE).setMethod("POST").setParams(params))
                .setUpdateParser(new UpdateParser() {
                    @Override
                    public Update parse(String response) throws Exception {
                        Log.e(TAG, "parse: " + Thread.currentThread().getName());
                        Log.d("CheckUpdate params", params.toString());
                        Log.d("CheckUpdate res", response);
                        Update update = null;
                        JSONObject jsonObject = new JSONObject(response);
                        String state = jsonObject.getString(Config.CHECK_STATE);
                        if (!"2".equals(state)) {
                            update = new Update();
                            String url = jsonObject.getString(Config.FILE_URL);
                            String desc = jsonObject.getString(Config.FILE_DESC);
                            update.setUpdateUrl(url);
                            update.setUpdateContent(desc);
                            update.setForced(false);
                            update.setVersionName(fVerion[0]);
                        } else {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mActivity, "No update", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        return update;
                    }
                })
                .setUpdateChecker(new UpdateChecker() {
                    @Override
                    public boolean check(Update update) throws Exception {
                        return true;
                    }
                })
                .setFileCreator(new FileCreator() {
                    @Override
                    protected File create(Update update) {
                        File cacheDir = Config.getApp().getExternalCacheDir();
                        if (cacheDir == null) {
                            cacheDir = Config.getApp().getCacheDir();
                        }
                        File dir = new File(cacheDir, "update");
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        return new File(dir, "update.bin");
                    }

                    @Override
                    protected File createForDaemon(Update update) {
                        File cacheDir = Config.getApp().getExternalCacheDir();
                        if (cacheDir == null) {
                            cacheDir = Config.getApp().getCacheDir();
                        }
                        File dir = new File(cacheDir, "update");
                        if (dir.mkdirs()) {
                            return new File(dir, "update_daemon.bin");
                        }
                        return null;
                    }
                })
                .setFileChecker(new FileChecker() {
                    @Override
                    protected boolean onCheckBeforeDownload() throws Exception {
                        return true;
                    }

                    @Override
                    protected void onCheckBeforeInstall() throws Exception {

                    }
                })
                .setInstallStrategy(new InstallStrategy() {
                    @Override
                    public void install(Context context, String filename, Update update) {
                        updateFirmwareBase(filename);

                    }
                });
        UpdateBuilder.create(updateConfig).check();
    }

    void updateFirmwareLocal() {
        FilePickerUtils.goFile(this);
    }

    void updateFirmwareBase(String filename) {
        final File file = new File(filename);
        if (!file.exists()) {
            Toast.makeText(mActivity, "No file exist", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(mActivity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Updating...");
        progressDialog.setMax(100);
        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.btn_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.dismiss();
            }
        });
        progressDialog.show();
        progressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.GONE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mSys.updateFirmware(file, new Sys.UpdateListener() {
                    @Override
                    public void onSuccess() {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                if (mDialogInit != null) {
                                    mDialogInit.show();
                                }
                            }
                        });
                        SystemClock.sleep(3000);
                        initSdk();
                    }

                    @Override
                    public void onProgressChange(long cur, long max) {
                        progressDialog.setProgress((int) ((float) cur / max * 100));
                    }

                    @Override
                    public void onError(final int i, final String s) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.setTitle("Update fail: " + s + "  " + i);
                                progressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.VISIBLE);
                            }
                        });
                    }
                });
            }
        }).start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FilePickerUtils.REQ_FILE_PICK) {
            if (resultCode == Activity.RESULT_OK) {
                String binPath;
                Uri uri = data.getData();
                if (uri != null) {
                    binPath = FilePickerUtils.getPath(mActivity, uri);
                    if (binPath != null && binPath.endsWith(".bin")) {
                        updateFirmwareBase(binPath);
                        return;
                    }
                }
            }
        }
        Toast.makeText(mActivity, "Error bin file", Toast.LENGTH_SHORT).show();
    }

    int speed = 460800;

    //int speed = 115200;
    private void initSdk() {
        // Config the SDK base info
        mSys.showLog(getPreferenceManager().getSharedPreferences().getBoolean(getString(R.string.key_show_log), true));
        if (mDialogInit == null) {
            mDialogInit = (ProgressDialog) DialogUtils.showProgress(mActivity, getString(R.string.title_waiting), getString(R.string.msg_init));
        } else if (!mDialogInit.isShowing()) {
            mDialogInit.show();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                int statue = mSys.getFirmwareVer(new String[1]);
                if (statue != SdkResult.SDK_OK) {
                    int sysPowerOn = mSys.sysPowerOn();
                    Log.i(TAG, "sysPowerOn: " + sysPowerOn);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                mSys.setUartSpeed(speed);
                final int i = mSys.sdkInit();
                if (i == SdkResult.SDK_OK) {
                    setDeviceInfo();
                }
                if (mActivity != null) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mDialogInit != null)
                                mDialogInit.dismiss();
                            Log.e(TAG, "Cur speed: " + mSys.getCurSpeed());
                            String initRes = (i == SdkResult.SDK_OK) ? getString(R.string.init_success) : SDK_Result.obtainMsg(mActivity, i);

                            Toast.makeText(getActivity(), initRes, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void setDeviceInfo() {
        // 读取并判断, 不存在则存入
        byte[] info = new byte[1000];
        byte[] infoLen = new byte[2];
        int getInfo = mSys.getDeviceInfo(info, infoLen);
        if (getInfo == SdkResult.SDK_OK) {
            int len = infoLen[0] * 256 + infoLen[1];
            byte[] newInfo = new byte[len];
            System.arraycopy(info, 0, newInfo, 0, len);
            String infoStr = new String(newInfo);
            Log.i(TAG, "getDeviceInfo: " + getInfo + "\t" + len + "\t" + infoStr);
            if (!TextUtils.isEmpty(infoStr)) {
                String[] split = infoStr.split("\t");
                // 已存则返回
                try {
                    // 确保imei1和mac 存值正确
                    if (split.length >= 4) {
                        String val1 = split[0].split(":")[1];
                        String val4 = split[3].split(":")[1];
                        if (!TextUtils.isEmpty(val1) && !val1.equals("null") && val1.length() >= 15
                                && !TextUtils.isEmpty(val4) && !val4.equals("null") && val4.length() >= 12 && val4.contains(":")) {
                            Log.i(TAG, "Have saved, return");
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        Map<String, String> map = SystemInfoUtils.getImeiAndMeid(mActivity.getApplicationContext());
        String imei1 = map.get("imei1");
        String imei2 = map.get("imei2");
        String meid = map.get("meid");
        String mac = SystemInfoUtils.getMac();
        WifiManager wifi = (WifiManager) mActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        long start = System.currentTimeMillis();
        while (TextUtils.isEmpty(mac) && System.currentTimeMillis() - start < 5000) {
            if (!wifi.isWifiEnabled()) {
                wifi.setWifiEnabled(true);
            }
            mac = SystemInfoUtils.getMac();
        }
        Log.i(TAG, "mac = " + mac);
        if (TextUtils.isEmpty(mac)) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DialogUtils.show(mActivity, "Warning! No mac!!!");
                }
            });
            return;
        }
        String msg = "IMEI1:" + (imei1 == null ? "" : imei1) + "\t" + "IMEI2:" + (imei2 == null ? "" : imei2) + "\t" + "MEID:" + (meid == null ? "" : meid) + "\t" + "MAC:" + mac;
        Log.i(TAG, "readDeviceInfo: " + msg);
        byte[] bytes = msg.getBytes();
        int setInfo;
        int count = 0;
        do {
            setInfo = mSys.setDeviceInfo(bytes, bytes.length);
            Log.i(TAG, "setDeviceInfo: " + setInfo);
            if (setInfo == SdkResult.SDK_OK) {
                break;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (count++ < 5);
    }

    private void switchFragment(Fragment from, Fragment to) {
        MainActivity mainactivity = (MainActivity) mActivity;
        ActionBar actionBar = mainactivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (!to.isAdded()) {
            getFragmentManager().beginTransaction().addToBackStack(null).hide(from).add(R.id.frame_container, to).commit();
        } else {
            getFragmentManager().beginTransaction().addToBackStack(null).hide(from).show(to).commit();
        }
    }

    @Override
    public void onStop() {
        // restore led
        if (!mIsBlue) {
            if (mLed == null)
                mLed = mDriverManager.getLedDriver();
            mLed.setLed(LedLightModeEnum.ALL, false);
            mLed.setLed(LedLightModeEnum.BLUE, true);
        }
        super.onStop();
    }
}
