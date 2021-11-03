package com.szzcs.smartpos;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.szzcs.smartpos.base.BaseActivity;
import com.szzcs.smartpos.utils.DialogUtils;
import com.zcs.sdk.SdkResult;
import com.zcs.sdk.card.SLE4442Card;
import com.zcs.sdk.util.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SLE4442Activity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "SLE4442Activity";
    protected TextView mTvLog;
    protected ScrollView mScrollView;
    protected Button mBtnRead;
    protected Button mBtnWrite;
    protected Button mBtnErrorCount;
    protected Button mBtnChangeKey;
    protected Button mBtnVerify;
    protected Button mBtnInit;

    private SLE4442Card mSLE4442Card = MyApp.sDriverManager.getCardReadManager().getSLE4442Card();
    private ExecutorService mExecutor;
    private StringBuffer sbLog = new StringBuffer();
    private static DateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
    private static Handler mHandler = new Handler(Looper.getMainLooper());
    private String key = "FFFFFF";
    private byte startAddr = 0;
    private byte len = 127;
    private byte[] data;
    private byte[] protectedData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_sle4442);
        initView();
        mExecutor = Executors.newSingleThreadExecutor();
    }

    private void readCombi() {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (init() == SdkResult.SDK_OK) {
                    read();
                    readProtected();
                }
            }
        });
    }

    private void verify() {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                verifyKey();
            }
        });
    }

    private void writeCombi() {
        final EditText view = new EditText(this);
        DialogUtils.showViewDialog(this, view, getString(R.string.input_byte), null,
                getString(R.string.button_ok), getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input = view.getText().toString().trim();
                        if (input.length() > 2) {
                            input = input.substring(0, 2);
                        }
                        if (input.length() <= 0) {
                            input = "0";
                        }
                        final int inputHex = Integer.parseInt(input, 16);
                        mExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                //if (init() != SdkResult.SDK_OK) {
                                //    return;
                                //}
                                if (verifyKey() != SdkResult.SDK_OK) {
                                    return;
                                }
                                if (data != null && data.length != 0) {
                                    data[32] = (byte) inputHex;
                                }
                                write();
                                //writeProtected();
                            }
                        });
                    }
                }, null);
    }

    private int init() {
        int ret = mSLE4442Card.init();
        Log.e(TAG, "init: " + ret);
        showLog("init: " + ret);
        if (ret != SdkResult.SDK_OK) {
            showLog(getString(R.string.init_failed), Color.RED);
        }
        return ret;
    }

    private int verifyKey() {
        int ret = mSLE4442Card.verifyKey(StringUtils.convertHexToBytes(key));
        Log.e(TAG, "verify: " + key + "\t" + ret);
        showLog("verify: " + key + "  \t" + ret);
        if (ret != SdkResult.SDK_OK) {
            showLog(getString(R.string.verify_failed), Color.RED);
        }
        return ret;
    }

    private void read() {
        data = new byte[len];
        int ret = mSLE4442Card.readData(startAddr, len, data);
        Log.e(TAG, "readData: " + ret + "\t" + StringUtils.convertBytesToHex(data));
        String prefix = "readData: " + ret + "\t";
        showLog(prefix + StringUtils.convertBytesToHex(data), Color.RED, prefix.length() + 32 * 2, 2);
    }

    private void readProtected() {
        protectedData = new byte[32];
        int ret = mSLE4442Card.readProtectedData(protectedData);
        Log.e(TAG, "readProtectedData: " + ret + "  \t" + StringUtils.convertBytesToHex(protectedData));
        showLog("readProtectedData: " + ret + "\t" + StringUtils.convertBytesToHex(protectedData));
    }


    private void changeKey() {
        int ret = mSLE4442Card.changeKey(StringUtils.convertHexToBytes(key));
        Log.e(TAG, "changeKey: " + ret);
        showLog("changeKey: " + ret);
    }

    private void write() {
        if (data == null || data.length == 0) {
            showLog(getString(R.string.read_first), Color.RED);
            return;
        }
        int ret = mSLE4442Card.writeData(startAddr, len, data);
        Log.e(TAG, "writeData: " + StringUtils.convertBytesToHex(data) + "\t" + ret);
        showLog("writeData: " + StringUtils.convertBytesToHex(data) + "\t" + ret);
    }

    private void writeProtected() {
        if (protectedData == null || protectedData.length == 0) {
            showLog(getString(R.string.read_first), Color.RED);
            return;
        }
        int ret = mSLE4442Card.writeProtectedData((byte) 0, (byte) 32, protectedData);
        Log.e(TAG, "writeProtected: " + StringUtils.convertBytesToHex(protectedData) + "\t" + ret);
        showLog("writeProtected: " + StringUtils.convertBytesToHex(protectedData) + "\t" + ret);
    }

    private void readErrCount() {
        byte[] key = new byte[4];
        int ret = mSLE4442Card.readErrCountAndKey(key);
        Log.e(TAG, "readErrCountAndKey: " + ret + StringUtils.convertBytesToHex(key));
        if (ret == SdkResult.SDK_OK) {
            int count = 0;
            if (key[0] == 7) {
                count = 3;
            } else if (key[0] == 6) {
                count = 2;
            } else if (key[0] == 4) {
                count = 1;
            }
            showLog("readErrCountAndKey: " + ret + StringUtils.convertBytesToHex(key));
            showLog("remain count: " + count);
            showLog("key: " + StringUtils.convertBytesToHex(key).substring(2));
        }
    }

    public void showLog(final String log) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                sbLog.setLength(0);
                Date date = new Date();
                sbLog.append(dateFormat.format(date)).append(":");
                sbLog.append(log);
                sbLog.append("\r\n");
                mTvLog.append(sbLog);
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    public void showLog(final String log, final int color) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                sbLog.setLength(0);
                Date date = new Date();
                sbLog.append(dateFormat.format(date)).append(":");
                sbLog.append(log);
                sbLog.append("\r\n");
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
                SpannableString spannableString = new SpannableString(sbLog);
                spannableString.setSpan(colorSpan, 0, sbLog.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                mTvLog.append(spannableString);
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    public void showLog(final String log, final int color, final int start, final int len) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                sbLog.setLength(0);
                Date date = new Date();
                sbLog.append(dateFormat.format(date)).append(":");
                int startIndex = sbLog.length() + start;
                int end = startIndex + len;
                sbLog.append(log);
                sbLog.append("\r\n");
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
                SpannableString spannableString = new SpannableString(sbLog);
                spannableString.setSpan(colorSpan, startIndex, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                mTvLog.append(spannableString);
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    private void initView() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.pref_sle4442);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mTvLog = (TextView) findViewById(R.id.tv_log);
        mScrollView = (ScrollView) findViewById(R.id.scrollView);
        mBtnRead = (Button) findViewById(R.id.btn_read);
        mBtnRead.setOnClickListener(SLE4442Activity.this);
        mBtnWrite = (Button) findViewById(R.id.btn_write);
        mBtnWrite.setOnClickListener(SLE4442Activity.this);
        mBtnErrorCount = (Button) findViewById(R.id.btn_error_count);
        mBtnErrorCount.setOnClickListener(SLE4442Activity.this);
        mBtnChangeKey = (Button) findViewById(R.id.btn_change_key);
        mBtnChangeKey.setOnClickListener(SLE4442Activity.this);
        mBtnVerify = (Button) findViewById(R.id.btn_verify);
        mBtnVerify.setOnClickListener(SLE4442Activity.this);
        mBtnInit = (Button) findViewById(R.id.btn_init);
        mBtnInit.setOnClickListener(SLE4442Activity.this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_read) {
            readCombi();
        } else if (view.getId() == R.id.btn_write) {
            writeCombi();
        } else if (view.getId() == R.id.btn_error_count) {
            readErrCount();
        } else if (view.getId() == R.id.btn_change_key) {
            changeKey();
        } else if (view.getId() == R.id.btn_verify) {
            verify();
        } else if (view.getId() == R.id.btn_init) {
            init();
        }
    }
}
