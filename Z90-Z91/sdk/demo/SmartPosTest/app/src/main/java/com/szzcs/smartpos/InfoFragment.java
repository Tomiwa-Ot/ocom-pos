package com.szzcs.smartpos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.szzcs.smartpos.utils.DialogUtils;
import com.szzcs.smartpos.utils.SDK_Result;
import com.zcs.sdk.DriverManager;
import com.zcs.sdk.SdkResult;
import com.zcs.sdk.Sys;
import com.zcs.sdk.util.StringUtils;

import java.lang.ref.WeakReference;

/**
 * 系统功能模块
 * Created by yyzz on 2018/5/18.
 */

public class InfoFragment extends PreferenceFragment {
    private static final String TAG = "InfoFragment";
    private String mSdkVer;
    private String[] mBaseSdkVer;
    private String[] mFirmwareVer;
    private String[] mCustomerSN;
    private String[] mTerminalSN;
    private String[] mMachineModel;
    private static String mImei;
    private Activity mActivity;
    private ProgressDialog mProgressDialog;
    private static Handler mHandler;
    private DriverManager mDriverManager = MyApp.sDriverManager;
    private Sys mSys;


    private static final int MSG_SN = 1001;
    private static final int MSG_VER = 1002;
    private static final int MSG_FIRM_VER = 1003;
    private static final int MSG_CUSTOMER_SN = 1004;
    private static final int MSG_BASE_VER = 1005;
    private static final int MSG_MACHINE_MODEL = 1006;
    private static final int MSG_CUSTOMER_SECURITY = 1007;
    private static final int MSG_IMEI = 1008;


    static class InfoHandler extends Handler {
        WeakReference<InfoFragment> mFragment;

        InfoHandler(InfoFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            InfoFragment infoFragment = mFragment.get();
            if (infoFragment == null || !infoFragment.isAdded())
                return;
            switch (msg.what) {
                case MSG_SN:
                    infoFragment.refreshSummary(infoFragment.getString(R.string.key_pid), infoFragment.mTerminalSN[0]);
                    break;
                case MSG_FIRM_VER:
                    infoFragment.refreshSummary(infoFragment.getString(R.string.key_firmware_ver), infoFragment.mFirmwareVer[0]);
                    break;
                case MSG_CUSTOMER_SN:
                    infoFragment.refreshSummary(infoFragment.getString(R.string.key_sn), infoFragment.mCustomerSN[0]);
                    break;
                case MSG_VER:
                    infoFragment.refreshSummary(infoFragment.getString(R.string.key_sdk_ver), infoFragment.mSdkVer);
                    break;
                case MSG_BASE_VER:
                    infoFragment.refreshSummary(infoFragment.getString(R.string.key_base_sdk_ver), infoFragment.mBaseSdkVer[0]);
                    break;
                case MSG_CUSTOMER_SECURITY:
                    String security = infoFragment.getString((Integer) msg.obj);
                    infoFragment.refreshSummary(infoFragment.getString(R.string.key_pos_sq_status), security);
                    break;
                case MSG_IMEI:
                    //infoFragment.refreshSummary(infoFragment.getString(R.string.key_imei), mImei);
                    break;
                //case MSG_MACHINE_MODEL:
                //    infoFragment.refreshSummary(infoFragment.getString(R.string.key_machine_model), infoFragment.mMachineModel[0]);
                //    break;
                default:
                    DialogUtils.show(infoFragment.getActivity(), SDK_Result.obtainMsg(infoFragment.getActivity(), msg.what));
                    break;
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_info);
        mActivity = getActivity();

        mBaseSdkVer = new String[1];
        mFirmwareVer = new String[1];
        mCustomerSN = new String[1];
        mTerminalSN = new String[1];
        mMachineModel = new String[1];
        mSys = mDriverManager.getBaseSysDevice();
        mHandler = new InfoHandler(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                // pid
                int resPid = mSys.getPid(mTerminalSN);
                Log.i(TAG, "getPid res: " + resPid);
                if (resPid == SdkResult.SDK_OK) {

                    Log.i(TAG, "getPid: " + mTerminalSN[0]);
                    mHandler.sendEmptyMessage(MSG_SN);
                }

                // base sdk version
                int resBaseVer = mSys.getBaseSdkVer(mBaseSdkVer);
                Log.i(TAG, "Read base ver res:  " + resBaseVer);
                if (resBaseVer == SdkResult.SDK_OK) {

                    Log.i(TAG, "getBaseSdkVer: " + mBaseSdkVer[0]);
                    mHandler.sendEmptyMessage(MSG_BASE_VER);
                }

                // sdk ver
                mSdkVer = mSys.getSdkVersion();
                if (mSdkVer != null)
                    mHandler.sendEmptyMessage(MSG_VER);

                // firmware ver
                int resFireVer = mSys.getFirmwareVer(mFirmwareVer);
                Log.i(TAG, "Read firm ver res: " + resFireVer);
                if (resFireVer == SdkResult.SDK_OK) {

                    Log.i(TAG, "getFirmwareVer: " + mFirmwareVer[0]);
                    mHandler.sendEmptyMessage(MSG_FIRM_VER);
                }

                // sn
                //byte[] sn = new byte[12];
                //for (int i = 0; i < 12; i++) {
                //    sn[i] = (byte) 0x33;
                //}
                //   mSys.setCustomSn(new String(sn));
                int resSN = mSys.getCustomSn(mCustomerSN);
                Log.i(TAG, "Read sn res: " + resSN);
                if (resSN == SdkResult.SDK_OK) {
                    Log.i(TAG, "getCustomSn: " + mCustomerSN[0]);
                    mHandler.sendEmptyMessage(MSG_CUSTOMER_SN);
                }
                byte[] mPosSecurity = new byte[8];
                int resState = mSys.getSpStatus(mPosSecurity);
                Log.i(TAG, "Read sn res: " + resState);
                Log.i(TAG, "getSpStatus: " + mPosSecurity[5]);
                if (resState == SdkResult.SDK_OK) {
                    mHandler.obtainMessage(MSG_CUSTOMER_SECURITY, R.string.contact_is_installed).sendToTarget();
                } else {
                    if (mPosSecurity[5] == 0x00) {
                        mHandler.obtainMessage(MSG_CUSTOMER_SECURITY, R.string.contact_is_installed).sendToTarget();
                    } else {
                        mHandler.obtainMessage(MSG_CUSTOMER_SECURITY, R.string.contact_not_installed).sendToTarget();
                    }
                }
              /*  int resMachine = mSys.getDevName(mMachineModel);
                Log.i(TAG, "Read sn res: " + resSN);
                if (resSN == SdkResult.SDK_OK) {
                    Log.i(TAG, "getCustomSn: " + mMachineModel[0]);
                    mHandler.sendEmptyMessage(MSG_MACHINE_MODEL);
                }*/

                // imei
                /*byte[] info = new byte[1000];
                byte[] infoLen = new byte[2];
                int getInfo = mSys.getDeviceInfo(info, infoLen);
                if (getInfo == SdkResult.SDK_OK) {
                    int len = infoLen[0] * 256 + infoLen[1];
                    byte[] newInfo = new byte[len];
                    System.arraycopy(info, 0, newInfo, 0, len);
                    mImei = new String(newInfo);
                    Log.i(TAG, "getDeviceInfo: " + getInfo + "\t" + len + "\t" + mImei);
                    mHandler.sendEmptyMessage(MSG_IMEI);
                }*/
            }
        }).start();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findPreference(getString(R.string.key_show_log)).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mSys.showLog((Boolean) newValue);
                return true;
            }
        });
        // init sdk
        findPreference(getString(R.string.key_init)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Log.d(TAG, "key_init click: ");
                initSdk();
                return true;
            }
        });

        findPreference(getString(R.string.key_trans_mode))
                .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        boolean isOpen = ((boolean) newValue);
                        int ret = -1;
                        if (isOpen) {
                            ret = openTransMode();
                        } else {
                            ret = closeTransMode();
                        }
                        if (ret == SdkResult.SDK_OK) {
                            return true;
                        }
                        Toast.makeText(mActivity, "open/close fail：  " + ret, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });

        findPreference(getString(R.string.key_trans_mode_send)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                boolean isTransMode = isTransMode();
                if (isTransMode) {
                    sendData();
                } else {
                    Toast.makeText(mActivity, "Trans mode is not open", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        findPreference(getString(R.string.key_trans_mode_recv)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (isTransMode()) {
                    recvData();
                } else {
                    Toast.makeText(mActivity, "Trans mode is not open", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }

    private boolean isTransMode() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
        boolean isTransMode = prefs.getBoolean(getString(R.string.key_trans_mode), false);
        Log.i(TAG, "isTransMode: " + isTransMode);
        return isTransMode;
    }

    private void initSdk() {
        mProgressDialog = (ProgressDialog) DialogUtils.showProgress(mActivity, getString(R.string.title_waiting), getString(R.string.msg_init));
        new Thread(new Runnable() {
            @Override
            public void run() {
                final int i = mDriverManager.getBaseSysDevice().sdkInit();
                if (mActivity != null) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mProgressDialog != null)
                                mProgressDialog.dismiss();
                            String initRes = (i == SdkResult.SDK_OK) ? getString(R.string.init_success) : SDK_Result.obtainMsg(mActivity, i);

                            Toast.makeText(getActivity(), initRes, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 开启透传模式
     */
    private int openTransMode() {
        byte[] pubKey = new byte[5];
        int ret = mSys.setExternalPort((byte) 0x01, 0, pubKey);
        return ret;
    }

    /**
     * 开启透传模式
     */
    private int closeTransMode() {
        byte[] pubKey = new byte[5];
        int ret = mSys.setExternalPort((byte) 0x00, 0, pubKey);
        return ret;
    }

    private void sendData() {
        String cmd = " 02 00 03 01 01 00 03 ".replaceAll(" ", "");
        byte[] bytes = StringUtils.convertHexToBytes(cmd);
        //String cmd = "0102030405060708";
        //StringBuilder sb = new StringBuilder();
        //for (int i = 0; i < 256; i++) {
        //    sb.append(cmd);
        //}
        //Log.i(TAG, "sendData: " + sb);
        //Log.i(TAG, "sendData: " + sb.length());
        //byte[] bytes = StringUtils.convertHexToBytes(sb.toString());
        int sendRet = mSys.externalPortSend(bytes.length, bytes);
        Log.i(TAG, "sendData sendRet: " + sendRet);
    }

    private void recvData() {
        int[] len = new int[1];
        byte[] data = new byte[2048];
        int recvRet = mSys.externalPortRcv(len, data);
        Log.i(TAG, "recvData recvRet: " + recvRet);
        byte[] subByte = StringUtils.subByte(data, 0, len[0]);
        String dataHex = StringUtils.convertBytesToHex(subByte);
        Log.i(TAG, "recvData: len:  " + len[0] + "\tdata:  " + dataHex);
        DialogUtils.show(mActivity, "Trans mode recv:  " + dataHex);
    }

    private void refreshSummary(@NonNull String key, String summary) {
        refreshSummary(findPreference(key), summary);
    }

    private void refreshSummary(Preference preference, String summary) {
        SpannableString spannableString = new SpannableString(summary);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, summary.length(), 0);
        preference.setSummary(spannableString);
    }
}
