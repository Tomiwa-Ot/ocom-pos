package com.szzcs.smartpos;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.szzcs.smartpos.base.BaseActivity;
import com.zcs.sdk.SdkResult;
import com.zcs.sdk.fingerprint.FingerprintListener;
import com.zcs.sdk.fingerprint.FingerprintManager;
import com.zcs.sdk.fingerprint.Result;
import com.zcs.sdk.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FingerprintActivity extends BaseActivity implements FingerprintListener, OnClickListener {
    private static final String TAG = "FingerprintActivity";
    protected Button mBtAdd;
    protected Button mBtVerify;
    protected ImageView mIvResult;
    protected TextView mTextStatus;
    protected Button mBtGetFeature;
    protected Button mBtEnroll;
    protected Button mBtGetCount;
    protected Button mBtGetEnrolledList;
    protected Button mBtDelete;
    protected Button mBtDeleteAll;
    protected Button mBtIso;
    protected EditText mEtFingerId;
    protected Button mBtVerifyFeature;
    protected Button mBtVerifyISOFeature;
    protected Button mBtSearchFeature;
    protected Button mBtSearchISOFeature;
    protected Button mBtIdentify;
    private String files = "/sdcard/";
    private long lastClick = 0;
    private Handler mHandler;
    private FingerprintManager mFingerprintManager;
    private int mFingerId = 0;
    private int mTimeout = 3;
    private byte[] featureTmp;
    private byte[] isoFeatureTmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_finger);
        setTitle(getResources().getString(R.string.pref_fingerprint));
        initView();
        initFinger();
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void onDestroy() {
        mFingerprintManager.close();
        super.onDestroy();
    }

    private void initFinger() {
        mFingerprintManager = MyApp.sDriverManager.getFingerprintManager();
        mFingerprintManager.addFignerprintListener(this);
        mFingerprintManager.init();
    }

    @Override
    public void onClick(View view) {
        if (!clickCheck()) {
            showLog(getResources().getString(R.string.button_tip));
            return;
        }
        mIvResult.setVisibility(View.GONE);
        mTextStatus.setText("");
        String fingerText = mEtFingerId.getText().toString().trim();
        mFingerId = Integer.parseInt(TextUtils.isEmpty(fingerText) ? "0" : fingerText);
        Log.e(TAG, "FingerId: " + Integer.toHexString(mFingerId));
        if (view.getId() == R.id.bt_add) {
            mFingerprintManager.capture();
        } else if (view.getId() == R.id.bt_get_feature) {
            mFingerprintManager.captureAndGetFeature();
        } else if (view.getId() == R.id.bt_iso) {
            mFingerprintManager.captureAndGetISOFeature();
        } else if (view.getId() == R.id.bt_enroll) {
            mFingerprintManager.enrollment(mFingerId);
        } else if (view.getId() == R.id.bt_verify) {
            mFingerprintManager.authenticate(mFingerId, 3);
        } else if (view.getId() == R.id.bt_identify) {
            mFingerprintManager.authenticate(3);
        } else if (view.getId() == R.id.bt_verify_feature) {
            if (featureTmp != null) {
                mFingerprintManager.verifyWithFeature(featureTmp);
            } else {
                showLog("Feature data is null, click `Get feature` button first");
            }
        } else if (view.getId() == R.id.bt_verify_ISO_feature) {
            if (isoFeatureTmp != null) {
                mFingerprintManager.verifyWithISOFeature(isoFeatureTmp);
            } else {
                showLog("ISO feature data is null, click `Get ISO feature` button first");
            }
        } else if (view.getId() == R.id.bt_search_feature) {
            if (featureTmp != null) {
                mFingerprintManager.identifyWithFeature(featureTmp);
            } else {
                showLog("Feature data is null, click `Get feature` button first");
            }
        } else if (view.getId() == R.id.bt_search_ISO_feature) {
            if (isoFeatureTmp != null) {
                mFingerprintManager.identifyWithISOFeature(isoFeatureTmp);
            } else {
                showLog("ISO feature data is null, click `Get ISO feature` button first");
            }
        } else if (view.getId() == R.id.bt_get_count) {
            Result result = mFingerprintManager.getEnrolledCount();
            showLog("getEnrolledCount:  " + result.error + "  count: " + result.arg1);
        } else if (view.getId() == R.id.bt_get_enrolled_list) {
            Result result = mFingerprintManager.getEnrolledFingerprints();
            showLog("getEnrolledFingerprints: " + result.error + "\t" + result.data.toString());
        } else if (view.getId() == R.id.bt_delete) {
            int ret = mFingerprintManager.remove(mFingerId);
            showLog("remove: " + ret);
        } else if (view.getId() == R.id.bt_delete_all) {
            int ret = mFingerprintManager.removeAll();
            showLog("removeAll: " + ret);
        }
    }

    @Override
    public void onAuthenticationFailed(int reason) {
        showLog("Fingerprint auth failed: " + reason);
    }

    @Override
    public void onAuthenticationSucceeded(int fingerId, Object obj) {
        showLog("Fingerprint auth successfully:  fingerId = " + fingerId + "  score = " + obj);
    }

    @Override
    public void onEnrollmentProgress(int fingerId, int remaining, int reason) {
        if (reason == 0 && remaining == 0) {
            showLog("Fingerprint ID:" + fingerId + "  Enrollment success!");
        } else {
            showLog("Fingerprint ID:" + fingerId);
            showLog("remaining times:" + remaining);
            showLog("reason:" + reason);
        }
    }

    private void save2File(String path, byte[] data) {
        FileOutputStream fos = null;
        try {
            File f = new File(path);
            if (!f.exists()) {
                if (!f.getParentFile().exists()) {
                    f.getParentFile().mkdirs();
                }
                f.createNewFile();
            }
            fos = new FileOutputStream(f);
            fos.write(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onGetImageComplete(int result, byte[] imgBuff) {
        if (result == 0) {
            try {
                save2File("/sdcard/raw.data", imgBuff);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                String name = sdf.format(new Date()) + ".bmp";
                // convert raw format image to bmp
                final Bitmap bitmap = mFingerprintManager.generateBmp(imgBuff, files + name);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mIvResult.setVisibility(View.VISIBLE);
                        mIvResult.setImageBitmap(bitmap);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String msg = "";
            if (result == 0x01) {
                msg = getResources().getString(R.string.finger_tip01);
            } else if (result == 0x02) {
                msg = getResources().getString(R.string.finger_tip02);
            } else if (result == 0x03) {
                msg = getResources().getString(R.string.finger_tip03);
            } else if (result == 0xFF) {
                msg = getResources().getString(R.string.finger_tiperr);
            }
            showLog(msg);
        }
    }

    @Override
    public void onGetImageFeature(int result, byte[] feature) {
        showLog("onGetImageFeature: ret = " + result + (result == SdkResult.SDK_OK ? "\tfeature = " + StringUtils.convertBytesToHex(feature) : null));
        if (result == SdkResult.SDK_OK) {
            featureTmp = feature;
        }
    }

    @Override
    public void onGetImageISOFeature(int result, byte[] feature) {
        showLog("onGetImageISOFeature: ret =  " + result + (result == SdkResult.SDK_OK ? "\tISO feature = " + StringUtils.convertBytesToHex(feature) : null));
        if (result == SdkResult.SDK_OK) {
            isoFeatureTmp = feature;
        }
    }


    StringBuffer _msg = new StringBuffer();
    private int _lines = 20;

    private void showLog(final String msg) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, msg);
                Date date = new Date();
                DateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm:ss");
                _msg.append(dateFormat.format(date)).append(":");
                _msg.append(msg);
                String text = mTextStatus.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    String str[] = text.split("\r\n");
                    for (int i = 0; i < _lines && i < str.length; i++) {
                        _msg.append("\r\n");
                        _msg.append(str[i]);
                    }
                }
                mTextStatus.setText(_msg.toString());
                _msg.setLength(0);
            }

        });
    }

    private boolean clickCheck() {
        if (System.currentTimeMillis() - lastClick <= 3000) {
            return false;
        }
        lastClick = System.currentTimeMillis();
        return true;
    }

    private void initView() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.pref_fingerprint);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mBtAdd = (Button) findViewById(R.id.bt_add);
        mBtAdd.setOnClickListener(FingerprintActivity.this);
        mBtVerify = (Button) findViewById(R.id.bt_verify);
        mBtVerify.setOnClickListener(FingerprintActivity.this);
        mIvResult = (ImageView) findViewById(R.id.iv_result);
        mTextStatus = (TextView) findViewById(R.id.text_status);
        mBtGetFeature = (Button) findViewById(R.id.bt_get_feature);
        mBtGetFeature.setOnClickListener(FingerprintActivity.this);
        mBtEnroll = (Button) findViewById(R.id.bt_enroll);
        mBtEnroll.setOnClickListener(FingerprintActivity.this);
        mBtGetCount = (Button) findViewById(R.id.bt_get_count);
        mBtGetCount.setOnClickListener(FingerprintActivity.this);
        mBtGetEnrolledList = (Button) findViewById(R.id.bt_get_enrolled_list);
        mBtGetEnrolledList.setOnClickListener(FingerprintActivity.this);
        mBtDelete = (Button) findViewById(R.id.bt_delete);
        mBtDelete.setOnClickListener(FingerprintActivity.this);
        mBtDeleteAll = (Button) findViewById(R.id.bt_delete_all);
        mBtDeleteAll.setOnClickListener(FingerprintActivity.this);
        mBtIso = (Button) findViewById(R.id.bt_iso);
        mBtIso.setOnClickListener(FingerprintActivity.this);
        mEtFingerId = (EditText) findViewById(R.id.et_finger_id);
        mEtFingerId.setSelection(mEtFingerId.getText().toString().length());
        mBtVerifyFeature = (Button) findViewById(R.id.bt_verify_feature);
        mBtVerifyFeature.setOnClickListener(FingerprintActivity.this);
        mBtVerifyISOFeature = (Button) findViewById(R.id.bt_verify_ISO_feature);
        mBtVerifyISOFeature.setOnClickListener(FingerprintActivity.this);
        mBtSearchFeature = (Button) findViewById(R.id.bt_search_feature);
        mBtSearchFeature.setOnClickListener(FingerprintActivity.this);
        mBtSearchISOFeature = (Button) findViewById(R.id.bt_search_ISO_feature);
        mBtSearchISOFeature.setOnClickListener(FingerprintActivity.this);
        mBtIdentify = (Button) findViewById(R.id.bt_identify);
        mBtIdentify.setOnClickListener(FingerprintActivity.this);
    }
}
