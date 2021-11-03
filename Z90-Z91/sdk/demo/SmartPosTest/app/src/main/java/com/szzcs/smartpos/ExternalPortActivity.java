package com.szzcs.smartpos;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.szzcs.smartpos.base.BaseActivity;
import com.szzcs.smartpos.utils.DialogUtils;
import com.zcs.sdk.DriverManager;
import com.zcs.sdk.SdkResult;
import com.zcs.sdk.exteranl.ExternalCardManager;
import com.zcs.sdk.exteranl.ICCard;
import com.zcs.sdk.util.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExternalPortActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "ExternalPortActivity";
    protected Button mBtnTest;
    private DriverManager mDriverManager;
    private ExternalCardManager mCardManager;
    private ICCard mICCard;

    protected Button mBtnDetect;
    protected Button mBtnSpecificSlot;
    protected TextView mTvLog;

    public static final byte[] APDU_SEND_IC = CardFragment.APDU_SEND_RANDOM;
    private static final int MSG_SLOT = 0x101;
    private static final int MSG_FINISH = 0x102;
    public byte slot = 0x00;
    private List<Byte> slots = new ArrayList<>();
    private int index = 0;
    private int size = 0;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SLOT:
                    if (mProgressDialog != null)
                        mProgressDialog.setMessage("Slot  " + (msg.arg1 + 1));
                    break;
                case MSG_FINISH:
                    if (mProgressDialog != null)
                        mProgressDialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    };
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_external_port);
        initView();
        initSDK();
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_one_click_test) {

            onClickTest();

        } else if (view.getId() == R.id.btn_specific_slot) {
            final EditText editText = new EditText(this);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            //editText.setTransformationMethod();
            Dialog dialog = DialogUtils.showViewDialog(this,
                    editText, getString(R.string.input_slot), null,
                    getString(R.string.button_ok), getString(R.string.btn_cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            dialog.dismiss();
                            final String s = editText.getText().toString();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        icDetect(Integer.parseInt(s) - 1);
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                    }, null);
        } else if (view.getId() == R.id.btn_test) {
            byte[] icRecv = mICCard.icExchangeAPDU(slot, APDU_SEND_IC);
        }
    }

    private void onClickTest() {
        index = 0;
        size = 0;
        slots.clear();
        mTvLog.setText("");
        mProgressDialog = (ProgressDialog) DialogUtils.showProgress(this, null, getString(R.string.detecting));
        new Thread(new Runnable() {
            @Override
            public void run() {
                mICCard.setCardType(true);
                for (int i = 0; i < 16; i++) {
                    if (icDetect(i))
                        continue;
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                size = slots.size();
                Message.obtain(mHandler, MSG_FINISH).sendToTarget();
            }
        }).start();
    }

    private boolean icDetect(int i) {
        Message msg = Message.obtain(mHandler, MSG_SLOT, i, i);
        msg.sendToTarget();
        int icCardStatus = mICCard.getIcCardStatus((byte) i);
        if (icCardStatus == SdkResult.SDK_OK) {
            slots.add((byte) i);
            //showLog("Detect card slot " + (byte) i);
            int icReset = icReset((byte) i);
            if (icReset == SdkResult.SDK_OK) {
                String recv = exchangeApdu((byte) i);
                if (recv != null) {
                    int powerDown = icPowerDown((byte) i);
                    if (powerDown == SdkResult.SDK_OK) {
                        showLog("Slot " + (i + 1) + "  success  apdu : " + recv);
                        return true;
                    }
                }
            }
        } else {
            showLog("Slot " + (i + 1) + " no card");
            return true;
        }
        showLog("Slot " + (i + 1) + " error");
        return true;
    }

    private int icReset(byte slot) {
        //showLog("------------ 读卡槽  " + slot + "开始 ------------");
        Log.e(TAG, "------------ 读卡槽  " + slot + "开始 ------------");
        int icCardReset = mICCard.icCardReset(slot);
        Log.e(TAG, "icReset: slot " + slot);
        //showLog(icCardReset == SdkResult.SDK_OK ? "Reset success" : "Fail " + icCardReset);
        return icCardReset;
    }

    private String exchangeApdu(byte slot) {
        byte[] icRecv = mICCard.icExchangeAPDU(slot, APDU_SEND_IC);
        String recvHex = StringUtils.convertBytesToHex(icRecv);
        Log.e(TAG, "recvApdu: " + recvHex);
        //showLog("recvApdu：  " + recvHex);
        return recvHex;
    }

    private int icPowerDown(byte slot) {
        int cardPowerDown = mICCard.icCardPowerDown(slot);
        //showLog(cardPowerDown == SdkResult.SDK_OK ? "Power down success" : "Fail " + cardPowerDown);
        //showLog("------------ 读卡槽 " + slot + "结束 ------------");
        Log.e(TAG, "icPowerDown: slot " + cardPowerDown);
        Log.e(TAG, "------------ 读卡槽 " + slot + "结束 ------------");
        return cardPowerDown;
    }

    private StringBuffer sbLog = new StringBuffer();
    private static DateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");

    public void showLog(String log) {
        Log.e(TAG, log);
        Date date = new Date();
        sbLog.append(dateFormat.format(date)).append(":");
        sbLog.append(log);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String text = mTvLog.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    String[] str = text.split("\r\n");
                    for (int i = 0; i < str.length; i++) {
                        sbLog.append("\r\n");
                        sbLog.append(str[i]);
                    }
                }
                mTvLog.setText(sbLog.toString());
                sbLog.setLength(0);
            }
        });
    }

    private void initSDK() {
        mDriverManager = MyApp.sDriverManager;
        mCardManager = mDriverManager.getExternalCardManager();
        mICCard = mCardManager.getICCCard();
    }

    private void initView() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.pref_external_ic);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mBtnDetect = (Button) findViewById(R.id.btn_one_click_test);
        mBtnDetect.setOnClickListener(ExternalPortActivity.this);
        mBtnSpecificSlot = (Button) findViewById(R.id.btn_specific_slot);
        mBtnSpecificSlot.setOnClickListener(ExternalPortActivity.this);
        mTvLog = findViewById(R.id.tv_log);
        mBtnTest = (Button) findViewById(R.id.btn_test);
        mBtnTest.setOnClickListener(ExternalPortActivity.this);
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
