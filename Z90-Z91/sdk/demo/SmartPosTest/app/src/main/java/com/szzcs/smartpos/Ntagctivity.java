package com.szzcs.smartpos;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.zcs.sdk.SdkData;
import com.zcs.sdk.SdkResult;
import com.zcs.sdk.card.RfCard;
import com.zcs.sdk.util.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;

public class Ntagctivity extends AppCompatActivity implements View.OnClickListener {

    protected Button mBtnRead;
    protected Button mBtnReadVer;
    protected Button mBtnWrite;
    protected Button mBtnVerify;
    protected Button mBtnChangeKey;
    protected Button mBtnCount;
    protected TextView mTvLog;
    protected ScrollView mScrollView;
    protected Button mBtnProtect;


    private RfCard mRfCard = MyApp.sDriverManager.getCardReadManager().getRFCard();
    private ExecutorService mExecutor = MyApp.sDriverManager.getSingleThreadExecutor();
    private StringBuffer sbLog = new StringBuffer();
    private static DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private static Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_ntag);
        initView();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_read) {
            if (searchCard() != SdkResult.SDK_OK) {
                return;
            }
            readPage();
        } else if (view.getId() == R.id.btn_read_ver) {
            if (searchCard() != SdkResult.SDK_OK) {
                return;
            }
            readVersion();
        } else if (view.getId() == R.id.btn_write) {
            if (searchCard() != SdkResult.SDK_OK) {
                return;
            }
            writePage();
        } else if (view.getId() == R.id.btn_verify) {
            if (searchCard() != SdkResult.SDK_OK) {
                return;
            }
            verifyKey();
        } else if (view.getId() == R.id.btn_change_key) {
            if (searchCard() != SdkResult.SDK_OK) {
                return;
            }
            changeKey();
        } else if (view.getId() == R.id.btn_count) {
            if (searchCard() != SdkResult.SDK_OK) {
                return;
            }
            count();
        } else if (view.getId() == R.id.btn_protect) {
            if (searchCard() != SdkResult.SDK_OK) {
                return;
            }
            protectPageData();
        }
    }

    private int searchCard() {
        byte[] outType = new byte[1];
        byte[] uid = new byte[300];
        int ret = mRfCard.rfSearchCard(SdkData.RF_TYPE_A, outType, uid);
        if (ret != SdkResult.SDK_OK) {
            showLog("Can't detect card");
        }
        return ret;
    }


    String keyNtag = "FFFFFFFF";


    /**
     * the number of pages from 0x00 ~ 0x2c
     * Read 0x10 pages of data every time. Each page have 4 bytes data
     */
    void readPage() {
        StringBuilder sb = new StringBuilder();
        int ret;
        do {
            byte startPage = 0x00;
            byte endPage = 0x10;
            byte[] outData = new byte[(endPage - startPage + 1) * 4];
            ret = mRfCard.ntagFastRead(startPage, endPage, outData);
            if (ret != SdkResult.SDK_OK) {
                break;
            }
            sb.append(StringUtils.convertBytesToHex(outData));
            startPage = 0x11;
            endPage = 0x2c;
            outData = new byte[(endPage - startPage + 1) * 4];
            ret = mRfCard.ntagFastRead(startPage, endPage, outData);
            if (ret != SdkResult.SDK_OK) {
                break;
            }
            sb.append(StringUtils.convertBytesToHex(outData));
        } while (false);
        showLog("read return = " + ret + "  data = " + sb);
    }

    void readVersion() {
        byte[] ver = new byte[8];
        int ret = mRfCard.ntagVersion(ver);
        showLog("readVersion: " + ret + "  " + StringUtils.convertBytesToHex(ver));
    }

    /**
     * Some pages cannot be written to the data. It depends on the card
     */
    void writePage() {
        verifyKey();
        byte page = 0x20;
        byte[] inData = new byte[]{0x01, 0x01, 0x01, 0x01};
        int ret = mRfCard.ntagFastWrite(page, inData);
        showLog("write page" + page + "data " + StringUtils.convertBytesToHex(inData) + " result = " + ret);
        byte startPage = page;
        byte endPage = page;
        byte[] outData = new byte[(endPage - startPage + 1) * 4];
        ret = mRfCard.ntagFastRead(startPage, endPage, outData);
        showLog("read page" + page + "data: " + ret + "  data: " + StringUtils.convertBytesToHex(outData));

    }

    void verifyKey() {
        int ret = mRfCard.sdkNtagVerifyKey(StringUtils.convertHexToBytes(keyNtag));
        showLog("verifyKey " + keyNtag + " result = " + ret);
    }

    void changeKey() {
        int ret = mRfCard.ntagChangeKey(StringUtils.convertHexToBytes(keyNtag));
        showLog("changeKey: " + ret + "  new key is: " + keyNtag);
    }

    void count() {
        byte[] cnt = new byte[3];
        int ret = mRfCard.ntagReadCnt(cnt);
        showLog("count: " + ret + "  " + StringUtils.convertBytesToHex(cnt));
    }

    void protectPageData() {
        // level: 0x00 write-protect; 0x01 read-write protection
        // page: 0x00~0x27
        byte level = 0x00;
        byte page = 0x02;
        int ret = mRfCard.ntagKeyProtect(level, page);
        showLog("protectPageData: change page" + page + " protect level " + level + " ret = " + ret);
    }

    private void initView() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.pref_ntag));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mBtnRead = (Button) findViewById(R.id.btn_read);
        mBtnRead.setOnClickListener(Ntagctivity.this);
        mBtnReadVer = (Button) findViewById(R.id.btn_read_ver);
        mBtnReadVer.setOnClickListener(Ntagctivity.this);
        mBtnWrite = (Button) findViewById(R.id.btn_write);
        mBtnWrite.setOnClickListener(Ntagctivity.this);
        mBtnVerify = (Button) findViewById(R.id.btn_verify);
        mBtnVerify.setOnClickListener(Ntagctivity.this);
        mBtnChangeKey = (Button) findViewById(R.id.btn_change_key);
        mBtnChangeKey.setOnClickListener(Ntagctivity.this);
        mBtnCount = (Button) findViewById(R.id.btn_count);
        mBtnCount.setOnClickListener(Ntagctivity.this);
        mTvLog = (TextView) findViewById(R.id.tv_log);
        mScrollView = (ScrollView) findViewById(R.id.scrollView);
        mBtnProtect = (Button) findViewById(R.id.btn_protect);
        mBtnProtect.setOnClickListener(Ntagctivity.this);
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
}
