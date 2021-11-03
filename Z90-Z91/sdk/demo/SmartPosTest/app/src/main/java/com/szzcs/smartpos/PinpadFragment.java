package com.szzcs.smartpos;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.szzcs.smartpos.utils.DialogUtils;
import com.szzcs.smartpos.utils.SDK_Result;
import com.zcs.sdk.DriverManager;
import com.zcs.sdk.SdkResult;
import com.zcs.sdk.pin.MagEncryptTypeEnum;
import com.zcs.sdk.pin.PinAlgorithmMode;
import com.zcs.sdk.pin.PinMacTypeEnum;
import com.zcs.sdk.pin.PinWorkKeyTypeEnum;
import com.zcs.sdk.pin.pinpad.PinPadManager;
import com.zcs.sdk.util.LogUtils;
import com.zcs.sdk.util.StringUtils;

/**
 * Created by yyzz on 2018/5/18.
 */

public class PinpadFragment extends PreferenceFragment {
    private static final String TAG = "InfoFragment";


    private DriverManager mDriverManager = MyApp.sDriverManager;
    private PinPadManager pinPadManager;


    public static int master_key = 0;
    public static int work_key = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pinpad_info);

        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        pinPadManager = mDriverManager.getPadManager();
        // 设置主密钥
        findPreference(getString(R.string.key_setmaster_key)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                master_key = 0;
                setMasterKey();
                return true;
            }
        });
        // 磁道数据加密
        findPreference(getString(R.string.key_track_encrypt)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                try {
                    if (MyApp.cardInfoEntity.getTk2() != null || MyApp.cardInfoEntity.getTk3() != null) {
                        byte[] track2data = (MyApp.cardInfoEntity.getTk2()).getBytes();
                        byte[] track3data = (MyApp.cardInfoEntity.getTk3()).getBytes();
                        byte[] out2data = new byte[track2data.length];
                        byte[] out3data = new byte[track3data.length];
                        int status2 = pinPadManager.pinPadEncryptTrackData(index_all, MagEncryptTypeEnum.UNION_ENCRYPT, track2data, (byte) track2data.length, out2data);
                        int status3 = pinPadManager.pinPadEncryptTrackData(index_all, MagEncryptTypeEnum.UNION_ENCRYPT, track3data, (byte) track3data.length, out3data);
                        LogUtils.error("磁道二加密状态码:" + status2 + "磁道三加密状态码:" + status3);
                        // LogUtils.error(ByteToString.byteToString(""));

                        DialogUtils.show(getActivity(), getString(R.string.show_second_track) + new String(out2data) + " \n " + getString(R.string.show_second_track) + new String(out3data));

                    } else {
                        DialogUtils.show(getActivity(), getString(R.string.no_mag_card_data));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
        // 设置工作密钥
        findPreference(getString(R.string.key_setworkKey)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                work_key = 0;
                setWorkKey();
                return true;
            }
        });
        // 获取pinBlock信息
        findPreference(getString(R.string.key_getpinblock)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                /**
                 * 获取pinblock方法
                 */
                pinPadManager.inputOnlinePin(getActivity(), (byte) 6, (byte) 12, 60, true, "5187108106590784",
                        (byte) 0, PinAlgorithmMode.ANSI_X_9_8, new PinPadManager.OnPinPadInputListener() {

                            @Override
                            public void onError(final int code) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        DialogUtils.show(getActivity(), SDK_Result.obtainMsg(getActivity(), code));
                                    }
                                });

                            }

                            @Override
                            public void onSuccess(final byte[] bytes) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.e(TAG, "PinBlock: " + StringUtils.convertBytesToHex(bytes));
                                        DialogUtils.show(getActivity(), "get pinblock data:" + StringUtils.convertBytesToHex(bytes));
                                    }
                                });

                            }
                        });
                return true;
            }
        });
        // 计算mac
        findPreference(getString(R.string.key_getmac)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                getMac();
                return true;
            }
        });
    }

    private void setDukptKey() {
        String key = "6AC292FAA1315B4D858AB3A3D7D5933A";
        String ksn = "FFFF9876543210E00000";
        int upDukpt = pinPadManager.pinPadUpDukpt(0, StringUtils.convertHexToBytes(key), (byte) (key.length() / 2), StringUtils.convertHexToBytes(ksn));

    }

    private void getMacByDukpt() {
        String input = "0200302004C030C09811000000000000000001000008021000123251871081065907699B0E751ADD38E0680104995187108106590784D15615619999999930019990000000343434130310DD068423601059800005219298D060D745153979CC003132333435363738313233343536373839303132333435313536117A7E3A0DFD41792610000000000000001422000335000601";
        byte[] outData = new byte[8];
        byte[] ksn = new byte[10];
        int ret = pinPadManager.pinPadMacByDukpt(0, PinMacTypeEnum.ECB, StringUtils.convertHexToBytes(input), input.length() / 2, outData, ksn);
        Log.d(TAG, "pinPadMacByDukpt:" + ret);
        if (ret == SdkResult.SDK_OK) {
            Log.d(TAG, "outData:" + StringUtils.convertBytesToHex(outData));
            Log.d(TAG, "ksn:" + StringUtils.convertBytesToHex(ksn));
        }
    }


    private void encryptByDukpt() {
        String input = "0200302004C030C09811000000000000000001000008021000123251871081065907699B0E751ADD38E0680104995187108106590784D15615619999999930019990000000343434130310DD068423601059800005219298D060D745153979CC003132333435363738313233343536373839303132333435313536117A7E3A0DFD417926100000000000000014220003";
        byte[] outData = new byte[input.length() / 2];
        byte[] ksn = new byte[10];
        int ret = pinPadManager.pinPadEncryptDataByDukpt(0, PinWorkKeyTypeEnum.PIN_KEY, StringUtils.convertHexToBytes(input), input.length() / 2, outData, ksn);
        Log.d(TAG, "sdkPadEncryptDataByDukpt:" + ret);
        if (ret == SdkResult.SDK_OK) {
            Log.d(TAG, "outData:" + StringUtils.convertBytesToHex(outData));
            Log.d(TAG, "ksn:" + StringUtils.convertBytesToHex(ksn));
        }
    }

    private void getPinBlockByDukpt() {
        final byte[] ksn = new byte[10];
        pinPadManager.inputOnlinePinByDukpt(getActivity(), (byte) 6, (byte) 12, 60, true, "5187108106590784",
                (byte) 0, PinAlgorithmMode.ANSI_X_9_8, new PinPadManager.OnPinPadInputListener() {

                    @Override
                    public void onError(final int code) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DialogUtils.show(getActivity(), SDK_Result.obtainMsg(getActivity(), code));
                            }
                        });

                    }

                    @Override
                    public void onSuccess(final byte[] bytes) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e(TAG, "PinBlock: " + StringUtils.convertBytesToHex(bytes));
                                Log.e(TAG, "ksn: " + StringUtils.convertBytesToHex(ksn));
                                DialogUtils.show(getActivity(), "get pinblock data:" + StringUtils.convertBytesToHex(bytes));
                            }
                        });

                    }
                }, ksn);
    }

    //计算mac
    private void getMac() {
        showPopupWindow2();
    }

    //更新工作密钥方法
    private void setWorkKey() {

        //处理更新工作密钥的操作
        String pin_key = "BF1CA957FE63B286E2134E08A8F3DDA903E0686F";
        String mac_key = "8670685795c8d2ea0000000000000000d2db51f1";
        String tdk_key = "00A0ABA733F2CBB1E61535EDCFDC34A93AA3EA2D";

        //popwindow 弹出关闭
        byte[] pin_key_byte = StringUtils.convertHexToBytes(pin_key);
        byte[] mac_key_byte = StringUtils.convertHexToBytes(mac_key);
        byte[] tdk_key_byte = StringUtils.convertHexToBytes(tdk_key);

        int status = pinPadManager.pinPadUpWorkKey(index_all, pin_key_byte, (byte) pin_key_byte.length,
                mac_key_byte, (byte) mac_key_byte.length, tdk_key_byte, (byte) tdk_key_byte.length);
        LogUtils.debug("更新加密主密钥的返回状态 status:" + status);
        if (status == SdkResult.SDK_OK) {
            DialogUtils.show(getActivity(), getString(R.string.update_work_key_success));
        } else {
            DialogUtils.show(getActivity(), getString(R.string.update_work_key_failure) + ":" + SDK_Result.obtainMsg(getActivity(), status));
        }

    }

    //设置主密钥方法
    private void setMasterKey() {
        showPopupWindow();

    }

    int index_all = 0;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private PopupWindow mPopWindow;

    /**
     * 更新主密钥的popwindows弹出
     */
    private void showPopupWindow() {
        final EditText masterKeyIndex;
        final EditText masterKey1;
        final EditText masterKey2;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View contentView = getActivity().getLayoutInflater().inflate(
                R.layout.pinpad_master_layout, null);
        masterKeyIndex = contentView.findViewById(R.id.masterKeyIndex);
        masterKey1 = contentView.findViewById(R.id.masterKey1);
        masterKey2 = contentView.findViewById(R.id.masterKey2);
        builder.setTitle(getString(R.string.input_master_key_meg));
        builder.setView(contentView);
        builder.setPositiveButton(getString(R.string.set_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String index = masterKeyIndex.getText().toString().trim();
                String key1 = masterKey1.getText().toString().trim();
                String key2 = masterKey2.getText().toString().trim();
                int int_index = 0;
                if ("".equals(index) || "".equals(key1) || "".equals(key2)) {

                } else {
                    index = index.toUpperCase();
                    switch (index) {
                        case "F":
                            int_index = 15;
                            break;
                        case "E":
                            int_index = 14;
                            break;
                        case "D":
                            int_index = 13;
                            break;
                        case "C":
                            int_index = 12;
                            break;
                        case "B":
                            int_index = 11;
                            break;
                        case "A":
                            int_index = 10;
                            break;
                        default:
                            int_index = Integer.valueOf(index);
                            break;
                    }
                    byte[] key_byte = StringUtils.convertHexToBytes(key1 + key2);
                    LogUtils.error("显示的是信息 key:" + key1 + key2);
                    LogUtils.debugHexMsg("显示信息", key_byte);
                    index_all = int_index;
                    int status = pinPadManager.pinPadUpMastKey(int_index, key_byte, (byte) key_byte.length);
                    LogUtils.debug("更新主密钥的返回状态 status:" + status);
                    if (status == SdkResult.SDK_OK) {
                        DialogUtils.show(getActivity(), getString(R.string.update_master_key_success));

                    } else {
                        DialogUtils.show(getActivity(), getString(R.string.update_master_key_failure) + ":" + SDK_Result.obtainMsg(getActivity(), status));
                    }

                }
            }
        }).setNegativeButton(getString(R.string.set_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).create().show();
       /* //设置contentView
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.pinpad_master_layout, null);
        mPopWindow = new PopupWindow(contentView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setContentView(contentView);
        //设置各个控件的点击响应
        Button button = contentView.findViewById(R.id.btn_cancel);
        Button button1 = contentView.findViewById(R.id.btn_checkmsg);
        masterKeyIndex = contentView.findViewById(R.id.masterKeyIndex);
        masterKey1 = contentView.findViewById(R.id.masterKey1);
        masterKey2 = contentView.findViewById(R.id.masterKey2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopWindow.dismiss();
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String index = masterKeyIndex.getText().toString().trim();
                String key1 = masterKey1.getText().toString().trim();
                String key2 = masterKey2.getText().toString().trim();
                int int_index = 0 ;
                if ("".equals(index)||"".equals(key1)||"".equals(key2)) {

                }else {
                    mPopWindow.dismiss();
                    index = index.toUpperCase();
                    switch (index){
                        case "F":
                            int_index = 15;
                            break;
                        case "E":
                            int_index = 14;
                            break;
                        case "D":
                            int_index = 13;
                            break;
                        case "C":
                            int_index = 12;
                            break;
                        case "B":
                            int_index = 11;
                            break;
                        case "A":
                            int_index = 10;
                            break;
                        default:
                            int_index = Integer.valueOf(index);
                            break;
                    }
                    byte[] key_byte = StringUtils.convertHexToBytes(key1 + key2);
                    LogUtils.error("显示的是信息 key:"+key1+key2);
                    LogUtils.debugHexMsg("显示信息",key_byte);
                    index_all = int_index;
                    int status = pinPadManager.pinPadUpMastKey(int_index, key_byte, (byte) key_byte.length);
                    LogUtils.debug("更新主密钥的返回状态 status:"+ status);
                    if (status == SdkResult.SDK_OK){
                        DialogUtils.show(getActivity(),getString(R.string.update_master_key_success));

                    }else {
                        DialogUtils.show(getActivity(),getString(R.string.update_master_key_failure)+":"+SDK_Result.obtainMsg(getActivity(),status));
                    }

                }
            }
        });
        backgroundAlpha(0.5f);
        mPopWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopWindow.setFocusable(false);// 点击空白处时，隐藏掉pop窗口
        //添加pop窗口关闭事件
        mPopWindow.setOnDismissListener(new poponDismissListener());
        //显示PopupWindow
        View rootview = LayoutInflater.from(getActivity()).inflate(R.layout.activity_main, null);
        mPopWindow.showAtLocation(rootview, Gravity.CENTER ,0, 0);*/
    }

    /**
     * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
     *
     * @author cg
     */
    class poponDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            //Log.v("List_noteTypeActivity:", "我是关闭事件");
            backgroundAlpha(1f);
        }

    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getActivity().getWindow().setAttributes(lp);
    }

    /**
     * 计算MAC popwindow 弹出
     */
    private void showPopupWindow2() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View contentView = getActivity().getLayoutInflater().inflate(
                R.layout.pinpad_getmac_layout, null);
        builder.setTitle(getString(R.string.input_mac_key_datainfo));
        builder.setView(contentView);
        final EditText mac_key_datainfo = contentView.findViewById(R.id.mac_key_datainfo);
        builder.setPositiveButton(getString(R.string.set_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //处理更新工作密钥的操作
                String mac_key = mac_key_datainfo.getText().toString().trim();
                if ("".equals(mac_key)) {

                } else {
                    byte[] mac_datainfo_byte = StringUtils.convertHexToBytes(mac_key);

                    LogUtils.error("显示的是信息 key:" + mac_datainfo_byte);
                    byte[] outdata = new byte[8];
                    int status = pinPadManager.pinPadMac(index_all, PinMacTypeEnum.ECB, mac_datainfo_byte, mac_datainfo_byte.length,
                            outdata);
                    LogUtils.debug("计算mac加密的返回状态 status:" + status);
                    if (status == SdkResult.SDK_OK) {
                        DialogUtils.show(getActivity(), getString(R.string.calc_mac_encrypt_success) + com.zcs.sdk.util.StringUtils.convertBytesToHex(outdata));
                    } else {
                        DialogUtils.show(getActivity(), getString(R.string.calc_mac_encrypt_failure) + ":" + SDK_Result.obtainMsg(getActivity(), status));
                    }

                }
            }
        }).setNegativeButton(getString(R.string.set_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).create().show();
       /* //设置contentView
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.pinpad_getmac_layout, null);
        mPopWindow = new PopupWindow(contentView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setContentView(contentView);

        final EditText mac_key_datainfo = contentView.findViewById(R.id.mac_key_datainfo);

        Button button = contentView.findViewById(R.id.btn_cancel);
        Button button1 = contentView.findViewById(R.id.btn_checkmsg);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //popwindow 弹出关闭
                mPopWindow.dismiss();
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //处理更新工作密钥的操作
                String mac_key = mac_key_datainfo.getText().toString().trim();
                if ("".equals(mac_key)) {

                }else {
                    //popwindow 弹出关闭
                    mPopWindow.dismiss();

                    byte[] mac_datainfo_byte = StringUtils.convertHexToBytes(mac_key);

                    LogUtils.error("显示的是信息 key:"+mac_datainfo_byte);
                    byte[] outdata = new byte[8];
                    int status = pinPadManager.pinPadMac(index_all, PinMacTypeEnum.ECB, mac_datainfo_byte, mac_datainfo_byte.length,
                            outdata);
                    LogUtils.debug("计算mac加密的返回状态 status:"+ status);
                    if (status == SdkResult.SDK_OK){
                        DialogUtils.show(getActivity(),getString(R.string.calc_mac_encrypt_success)+ com.zcs.sdk.util.StringUtils.convertBytesToHex(outdata));
                    }else {
                        DialogUtils.show(getActivity(),getString(R.string.calc_mac_encrypt_failure)+":"+SDK_Result.obtainMsg(getActivity(),status));
                    }

                }
            }
        });
        backgroundAlpha(0.5f);
        mPopWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopWindow.setFocusable(false);// 点击空白处时，隐藏掉pop窗口
        //添加pop窗口关闭事件
        mPopWindow.setOnDismissListener(new poponDismissListener());
        //显示PopupWindow
        View rootview = LayoutInflater.from(getActivity()).inflate(R.layout.activity_main, null);
        mPopWindow.showAtLocation(rootview, Gravity.CENTER ,0, 0);*/
    }
}
