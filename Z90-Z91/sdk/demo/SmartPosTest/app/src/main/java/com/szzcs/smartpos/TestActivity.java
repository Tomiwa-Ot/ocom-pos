package com.szzcs.smartpos;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.szzcs.smartpos.base.BaseActivity;
import com.szzcs.smartpos.qr.QRTestActivity;
import com.szzcs.smartpos.utils.DialogUtils;
import com.szzcs.smartpos.utils.SDK_Result;
import com.zcs.sdk.Beeper;
import com.zcs.sdk.DriverManager;
import com.zcs.sdk.Led;
import com.zcs.sdk.LedLightModeEnum;
import com.zcs.sdk.Printer;
import com.zcs.sdk.SdkData;
import com.zcs.sdk.SdkResult;
import com.zcs.sdk.Sys;
import com.zcs.sdk.card.CardInfoEntity;
import com.zcs.sdk.card.CardReaderManager;
import com.zcs.sdk.card.CardReaderTypeEnum;
import com.zcs.sdk.card.CardSlotNoEnum;
import com.zcs.sdk.card.ICCard;
import com.zcs.sdk.card.MagCard;
import com.zcs.sdk.card.RfCard;
import com.zcs.sdk.listener.OnSearchCardListener;
import com.zcs.sdk.pin.pinpad.PinKeyboardViewModeEnum;
import com.zcs.sdk.pin.pinpad.PinPadManager;
import com.zcs.sdk.print.PrnStrFormat;
import com.zcs.sdk.print.PrnTextFont;
import com.zcs.sdk.print.PrnTextStyle;
import com.zcs.sdk.util.LogUtils;
import com.zcs.sdk.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;

public class TestActivity extends BaseActivity implements DialogInterface.OnCancelListener {


    private static final String TAG = "TestActivity";
    ActionBar actionBar;
    TextView ledTestResult;
    TextView beeperTestResult;
    TextView rfTestResult;
    TextView icTestResult;
    TextView magTestResult;
    TextView psam1TestResult;
    TextView psam2TestResult;
    TextView scanTestResult;
    TextView printTestResult;
    Button startTest;
    TextView mM1TestResult;
    EditText mPinKeyboard;
    TextView mSecurityTestResult;
    TextView mPukTestResult;
    public String security;
    private String mGetPuk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_test);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.pref_whole_engine_test));
        }
        initView();
        Sys mBaseSysDevice = manager.getBaseSysDevice();
        byte[] mPosSecurity = new byte[8];
        int resState = mBaseSysDevice.getSpStatus(mPosSecurity);
        Log.e(TAG, "Read sn res: " + resState);
        Log.e(TAG, "getCustomSn: " + mPosSecurity[5]);
        if (resState == SdkResult.SDK_OK) {

            security = getString(R.string.contact_is_installed);
            mSecurityTestResult.setTextColor(0xff54acea);
        } else {
            if (mPosSecurity[5] == 0x00) {
                mSecurityTestResult.setTextColor(0xff54acea);
                security = getString(R.string.contact_is_installed);
            } else {
                security = getString(R.string.contact_not_installed);
                mSecurityTestResult.setTextColor(0xffcc0000);
            }
        }
        mSecurityTestResult.setText(security);

        // 密钥和pid
        String[] pid = new String[1];
        int getPid = mBaseSysDevice.getPid(pid);
        if (getPid == SdkResult.SDK_OK) {
            int[] len = new int[10];
            byte[] pubKey = new byte[2048];
            int readPubKey = mBaseSysDevice.readPubKey(len, pubKey);
            if (readPubKey == SdkResult.SDK_OK) {
                mGetPuk = getString(R.string.success_test);
                mPukTestResult.setTextColor(0xff54acea);
            } else {
                mGetPuk = getString(R.string.no_puk);
                mPukTestResult.setTextColor(0xffcc0000);
            }
        } else {
            mGetPuk = getString(R.string.no_pid);
            mPukTestResult.setTextColor(0xffcc0000);
        }
        mPukTestResult.setText(mGetPuk);
    }

    void startTest() {
        dialog = DialogUtils.showProgress(this, "Tip", getString(R.string.led_testing));
        //测试led灯
        TestLed();
    }

    void keyBoardOnclick() {
        PinPadManager pinPadManager = manager.getPadManager();
        pinPadManager.setKeyBoardViewMode(PinKeyboardViewModeEnum.NO_INPUTVIEW);
        pinPadManager.setEditTextView(mPinKeyboard);
        /**
         * 获取pinblock方法
         */
        pinPadManager.inputOfflinePin(TestActivity.this, (byte) 6, (byte) 12, 60/* * 1000*/, true, new PinPadManager.OnPinPadInputListener() {

            @Override
            public void onError(final int code) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DialogUtils.show(TestActivity.this, SDK_Result.obtainMsg(TestActivity.this, code));
                    }
                });

            }

            @Override
            public void onSuccess(final byte[] bytes) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DialogUtils.show(TestActivity.this, "get pinblock data:" + StringUtils.convertBytesToHex(bytes));
                    }
                });

            }
        });
    }

    Dialog dialog;
    DriverManager manager = MyApp.sDriverManager;

    private void TestLed() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Led led = manager.getLedDriver();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final int status = led.setLed(LedLightModeEnum.ALL, true);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
                if (status == SdkResult.SDK_OK) {

                    checkTrue("beeper", getString(R.string.led_light_full));

                } else {
                    checkFalse("beeper", status);
                }
            }
        }).start();

    }

    //设置蜂鸣测试
    private void beeperTest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Beeper beeper = manager.getBeeper();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final int status = beeper.beep(500);

                dialog.dismiss();
                if (status == SdkResult.SDK_OK) {
                    checkTrue("rfcard", getString(R.string.buzzing_sound));

                } else {
                    checkFalse("rfcard", status);
                }
            }
        }).start();

    }

    private void rfCardTest() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        readCard(CardReaderTypeEnum.RF_CARD, READ_TIMEOUT, (byte) (SdkData.RF_TYPE_A | SdkData.RF_TYPE_B | SdkData.RF_TYPE_FELICA | SdkData.RF_TYPE_N24G));
    }

    private void m1CardTest() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        readCard(CardReaderTypeEnum.RF_CARD, READ_TIMEOUT, SdkData.RF_TYPE_A);
    }

    private void icCardTest() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        readCard(CardReaderTypeEnum.IC_CARD, READ_TIMEOUT, (byte) 0);
    }

    private void magCardTest() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        readCard(CardReaderTypeEnum.MAG_CARD, READ_TIMEOUT, (byte) 0);
    }

    private void psam1CardTest() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        readCard(CardReaderTypeEnum.PSIM1, READ_TIMEOUT, (byte) 0);
    }

    private void psam2CardTest() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        readCard(CardReaderTypeEnum.PSIM2, READ_TIMEOUT, (byte) 0);
    }

    private void scanTest() {
        number = 1;
        startActivity(new Intent(this, QRTestActivity.class));
    }

    private void testPrint() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Printer mPrinter = manager.getPrinter();
                AssetManager asm = getAssets();
                InputStream inputStream = null;
                try {
                    inputStream = asm.open("china_unin.bmp");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Drawable d = Drawable.createFromStream(inputStream, null);
                Bitmap bitmap = ((BitmapDrawable) d).getBitmap();

                int printStatus = mPrinter.getPrinterStatus();
                if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {
                    checkFalse("print1", printStatus);
                } else {
                    PrnStrFormat format = new PrnStrFormat();
                    format.setTextSize(30);
                    format.setAli(Layout.Alignment.ALIGN_CENTER);
                    format.setStyle(PrnTextStyle.BOLD);
                    mPrinter.setPrintAppendString(getResources().getString(R.string.all_test), format);
                    format.setFont(PrnTextFont.DEFAULT);

                    format.setTextSize(22);
                    format.setStyle(PrnTextStyle.NORMAL);
                    format.setAli(Layout.Alignment.ALIGN_NORMAL);

                    mPrinter.setPrintAppendString(" ", format);
                    mPrinter.setPrintAppendString(getResources().getString(R.string.led_test) + " : " +
                                    (checkresult[0] == 0 ? getResources().getString(R.string.failure_test) : checkresult[0] == 1 ?
                                            getResources().getString(R.string.success_test) : getResources().getString(R.string.no_test)),
                            format);
                    mPrinter.setPrintAppendString(getResources().getString(R.string.beeper_test) + " : " +
                                    (checkresult[1] == 0 ? getResources().getString(R.string.failure_test) : checkresult[1] == 1 ?
                                            getResources().getString(R.string.success_test) : getResources().getString(R.string.no_test)),
                            format);
                    mPrinter.setPrintAppendString(getResources().getString(R.string.rf_test) + " : " +
                                    (checkresult[2] == 0 ? getResources().getString(R.string.failure_test) : checkresult[2] == 1 ?
                                            getResources().getString(R.string.success_test) : getResources().getString(R.string.no_test)),
                            format);
                    mPrinter.setPrintAppendString(getResources().getString(R.string.ic_test) + " : " +
                                    (checkresult[3] == 0 ? getResources().getString(R.string.failure_test) : checkresult[3] == 1 ?
                                            getResources().getString(R.string.success_test) : getResources().getString(R.string.no_test)),
                            format);
                    mPrinter.setPrintAppendString(getResources().getString(R.string.m1_test) + " : " +
                                    (checkresult[4] == 0 ? getResources().getString(R.string.failure_test) : checkresult[4] == 1 ?
                                            getResources().getString(R.string.success_test) : getResources().getString(R.string.no_test)),
                            format);

                    mPrinter.setPrintAppendString(getResources().getString(R.string.mag_test) + " : " +
                                    (checkresult[5] == 0 ? getResources().getString(R.string.failure_test) : checkresult[5] == 1 ?
                                            getResources().getString(R.string.success_test) : getResources().getString(R.string.no_test)),
                            format);
                    mPrinter.setPrintAppendString(getResources().getString(R.string.psam1_test) + " : " +
                                    (checkresult[6] == 0 ? getResources().getString(R.string.failure_test) : checkresult[6] == 1 ?
                                            getResources().getString(R.string.success_test) : getResources().getString(R.string.no_test)),
                            format);
                    mPrinter.setPrintAppendString(getResources().getString(R.string.psam2_test) + " : " +
                                    (checkresult[7] == 0 ? getResources().getString(R.string.failure_test) : checkresult[7] == 1 ?
                                            getResources().getString(R.string.success_test) : getResources().getString(R.string.no_test)),
                            format);
                    mPrinter.setPrintAppendString(getResources().getString(R.string.scan_test) + " : " +
                                    (checkresult[8] == 0 ? getResources().getString(R.string.failure_test) : checkresult[8] == 1 ?
                                            getResources().getString(R.string.success_test) : getResources().getString(R.string.no_test)),
                            format);
                    mPrinter.setPrintAppendString(" ", format);
                    mPrinter.setPrintAppendString(" ", format);
                    mPrinter.setPrintAppendString(" ", format);
                    mPrinter.setPrintAppendString(" ", format);
                    printStatus = mPrinter.setPrintStart();
                    if (printStatus == SdkResult.SDK_OK) {
                        checkTrue("print1", getString(R.string.print_successful));
                    } else {
                        checkFalse("print1", printStatus);
                    }
                }

            }
        }).start();
    }

    public static final int READ_TIMEOUT = 0;
    public static final byte[] APDU_SEND_IC = {0x00, (byte) 0xA4, 0x04, 0x00, 0x0E, 0x31, 0x50, 0x41, 0x59, 0x2E, 0x53, 0x59, 0x53, 0x2E, 0x44, 0x44, 0x46, 0x30, 0x31, 0X00};
    public static final byte[] APDU_SEND_RF = {0x00, (byte) 0xA4, 0x04, 0x00, 0x0E, 0x32, 0x50, 0x41, 0x59, 0x2E, 0x53, 0x59, 0x53, 0x2E, 0x44, 0x44, 0x46, 0x30, 0x31, 0x00};
    public static final byte[] APDU_SEND_RANDOM = {0x00, (byte) 0x84, 0x00, 0x00, 0x08};
    private static final String KEY_APDU = "APDU";
    private byte[] mReceivedData = new byte[300];
    private int[] mReceivedDataLength = new int[1];
    private static final byte SLOT_USERCARD = 0x00;
    private static final byte SLOT_PSAM1 = 0x01;
    private static final byte SLOT_PSAM2 = 0x02;

    private void readCard(final CardReaderTypeEnum cardType, final int timeout, final byte bCardType) {
        switch (cardType) {
            case MAG_IC_RF_CARD:
                showSearchCardDialog(R.string.title_waiting, R.string.msg_bank_card);
                break;
            case MAG_CARD:
                showSearchCardDialog(R.string.title_waiting, R.string.msg_mag_card);
                break;
            case RF_CARD:
                if (bCardType == SdkData.RF_TYPE_A) {
                    showSearchCardDialog(R.string.title_waiting, R.string.msg_m1_card);
                } else {
                    showSearchCardDialog(R.string.title_waiting, R.string.msg_rf_card);
                }
                break;
            case IC_CARD:
                showSearchCardDialog(R.string.title_waiting, R.string.msg_ic_card);
                break;
            case PSIM1:
                showSearchCardDialog(R.string.title_waiting, R.string.msg_psam1_reading);
                break;
            case PSIM2:
                showSearchCardDialog(R.string.title_waiting, R.string.msg_psam2_reading);
                break;
        }

        mCardReadManager.searchCard(cardType, timeout, bCardType, new OnSearchCardListener() {
            @Override
            public void onCardInfo(CardInfoEntity cardInfoEntity) {
                Log.e(TAG, "searchCard thread: " + Thread.currentThread().getName());
                Message msg = Message.obtain();
                CardReaderTypeEnum cardTypeNew = cardInfoEntity.getCardExistslot();
                switch (cardTypeNew) {
                    case RF_CARD:
                        if (bCardType == SdkData.RF_TYPE_A) {
                            readM1Card();

                        } else {
                            readRfCard();
                        }
                        break;
                    case MAG_CARD:
                        readMagCard();
                        break;
                    case IC_CARD:
                        readICCard(CardSlotNoEnum.SDK_ICC_USERCARD);
                        break;
                    case PSIM1:
                        readICCard(CardSlotNoEnum.SDK_ICC_SAM1);
                        break;
                    case PSIM2:
                        readICCard(CardSlotNoEnum.SDK_ICC_SAM2);
                        break;
                }

            }

            @Override
            public void onError(int i) {
                Log.e(TAG, "search card onError: " + i);
                //  mHandler.sendEmptyMessage(i);
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onNoCard(CardReaderTypeEnum cardReaderTypeEnum, boolean b) {

            }
        });


    }

    private void readICCard(CardSlotNoEnum slotNo) {
        mProgressDialog.dismiss();
        ICCard iccCard = mCardReadManager.getICCard();
        int icCardReset = iccCard.icCardReset(slotNo);
        Log.e(TAG, "icCardReset: " + icCardReset);

        if (icCardReset == SdkResult.SDK_OK) {
            int icRes;
            if (slotNo.getType() == SLOT_PSAM1) {
                // test random
                icRes = iccCard.icExchangeAPDU(slotNo, APDU_SEND_RANDOM, mReceivedData, mReceivedDataLength);
            } else {
                icRes = iccCard.icExchangeAPDU(slotNo, APDU_SEND_IC, mReceivedData, mReceivedDataLength);
            }
            Log.e(TAG, "icRes: " + icRes);
            if (icRes == SdkResult.SDK_OK) {


                if (slotNo.getType() == SLOT_PSAM1) {

                    Log.e(TAG, "icard res: " + StringUtils.convertBytesToHex(mReceivedData));
                    checkTrue1("psam2", "");

                } else if (slotNo.getType() == SLOT_PSAM2) {
                    Log.e(TAG, "iccCard res: " + StringUtils.convertBytesToHex(mReceivedData));
                    checkTrue1("scan", "");

                } else if (slotNo.getType() == SLOT_USERCARD) {
                    Log.e(TAG, "iccCard res: " + StringUtils.convertBytesToHex(mReceivedData));
                    checkTrue1("m1card", "");

                }
                int icCardPowerDown = iccCard.icCardPowerDown(CardSlotNoEnum.SDK_ICC_USERCARD);
            } else {
                if (slotNo.getType() == SLOT_PSAM1) {
                    checkFalse("psam2", icRes);
                } else if (slotNo.getType() == SLOT_PSAM2) {
                    checkFalse("scan", icRes);
                } else if (slotNo.getType() == SLOT_USERCARD) {
                    checkFalse("m1card", icRes);
                }
            }

        } else {
            if (slotNo.getType() == SLOT_PSAM1) {
                checkFalse("psam2", icCardReset);
            } else if (slotNo.getType() == SLOT_PSAM2) {
                checkFalse("scan", icCardReset);
            } else if (slotNo.getType() == SLOT_USERCARD) {
                checkFalse("m1card", icCardReset);
            }
        }

    }

    private void readMagCard() {
        mProgressDialog.dismiss();
        CardInfoEntity cardInfo;
        MagCard magCard = mCardReadManager.getMAGCard();
        cardInfo = magCard.getMagReadData();
        String tk1 = cardInfo.getTk1();
        String tk2 = cardInfo.getTk2();
        String tk3 = cardInfo.getTk3();
        if (cardInfo.getResultcode() == SdkResult.SDK_OK
                && !TextUtils.isEmpty(tk1)
                && !TextUtils.isEmpty(tk2)
                && !TextUtils.isEmpty(tk3)) {
            checkTrue1("psam1", "磁卡返回数据为" + "是否成功");
            //  mHandler.sendMessage(msg);
        } else {
            checkFalse("psam1", cardInfo.getResultcode());
        }
        magCard.magCardClose();
    }


    private void readM1Card() {
        mProgressDialog.dismiss();
        RfCard rfCard = mCardReadManager.getRFCard();
        String keyS = "FFFFFFFFFFFF";
        byte[] key = StringUtils.convertHexToBytes(keyS);

        int status = rfCard.m1VerifyKey((byte) 4, (byte) 0x01, key);
        if (status == SdkResult.SDK_OK) {
            for (int i = 0; i < 4; i++) {
                byte[] out = new byte[16];
                rfCard.m1ReadBlock((byte) (4 * 1 + i), out);

            }

        }

        int status1 = rfCard.m1VerifyKey((byte) (2 * 4), (byte) 0x01, key);
        if (status1 == SdkResult.SDK_OK && status == SdkResult.SDK_OK) {
            for (int i = 0; i < 4; i++) {
                byte[] out = new byte[16];
                rfCard.m1ReadBlock((byte) (2 * 4 + i), out);
            }
            checkTrue1("magcard", "是否成功");
        } else {
            checkFalse("magcard", status1);
        }


    }

    private void readRfCard() {
        mProgressDialog.dismiss();
        RfCard rfCard = mCardReadManager.getRFCard();
        int resetStatus = rfCard.rfReset(new byte[300], new int[10]);
        if (resetStatus != SdkResult.SDK_OK) {
            checkFalse("iccard", resetStatus);
        } else {
            int rfRes = rfCard.rfExchangeAPDU(APDU_SEND_RF, mReceivedData, mReceivedDataLength);
            Log.e(TAG, "rfAPUDRes: " + rfRes);
            Log.e(TAG, "mReceivedData: " + StringUtils.convertBytesToHex(mReceivedData));
            if (rfRes == SdkResult.SDK_OK) {

                LogUtils.error("非接交互code:" + rfRes);
                int powerDownRes = rfCard.rfCardPowerDown();
                if (powerDownRes == SdkResult.SDK_OK) {

                    LogUtils.error("下电失败code:" + powerDownRes);
                    checkTrue1("iccard", "是否成功");
                    Log.e(TAG, "rfPowerDownRes: " + powerDownRes);
                } else {
                    checkFalse("iccard", powerDownRes);
                }
            } else {
                checkFalse("iccard", rfRes);
            }
        }

    }

    private Dialog mProgressDialog;
    private static CardReaderManager mCardReadManager = MyApp.sDriverManager.getCardReadManager();

    private int dia_msg = 0;

    private void showSearchCardDialog(@StringRes int title, @StringRes final int msg) {
        dia_msg = msg;
        mProgressDialog = DialogUtils.show(this, getString(title), getString(msg), getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mCardReadManager.cancelSearchCard();
                switch (dia_msg) {
                    case R.string.msg_rf_card:
                        checkresult[2] = 2;
                        rfTestResult.setText(getString(R.string.no_test));
                        rfTestResult.setTextColor(0xff1B2227);
                        icCardTest();
                        break;
                    case R.string.msg_ic_card:
                        icTestResult.setText(getString(R.string.no_test));
                        icTestResult.setTextColor(0xff1B2227);
                        checkresult[3] = 2;
                        m1CardTest();
                        break;
                    case R.string.msg_mag_card:
                        magTestResult.setText(getString(R.string.no_test));
                        magTestResult.setTextColor(0xff1B2227);
                        checkresult[5] = 2;
                        psam1CardTest();
                        break;
                    case R.string.msg_psam1_reading:
                        psam1TestResult.setText(getString(R.string.no_test));
                        psam1TestResult.setTextColor(0xff1B2227);
                        checkresult[6] = 2;
                        psam2CardTest();
                        break;
                    case R.string.msg_psam2_reading:
                        psam2TestResult.setText(getString(R.string.no_test));
                        psam2TestResult.setTextColor(0xff1B2227);
                        checkresult[7] = 2;
                        scanTest();
                        break;
                    case R.string.msg_m1_card:
                        mM1TestResult.setText(getString(R.string.no_test));
                        mM1TestResult.setTextColor(0xff1B2227);
                        checkresult[4] = 2;
                        magCardTest();
                        break;
                    default:
                        break;
                }
            }
        }, this);
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        mCardReadManager.cancelSearchCard();
        switch (dia_msg) {
            case R.string.msg_rf_card:
                checkresult[2] = 2;
                rfTestResult.setText(getString(R.string.no_test));
                rfTestResult.setTextColor(0xff1B2227);
                icCardTest();
                break;
            case R.string.msg_ic_card:
                icTestResult.setText(getString(R.string.no_test));
                icTestResult.setTextColor(0xff1B2227);
                checkresult[3] = 2;
                m1CardTest();
                break;
            case R.string.msg_mag_card:
                magTestResult.setText(getString(R.string.no_test));
                magTestResult.setTextColor(0xff1B2227);
                checkresult[5] = 2;
                psam1CardTest();
                break;
            case R.string.msg_psam1_reading:
                psam1TestResult.setText(getString(R.string.no_test));
                psam1TestResult.setTextColor(0xff1B2227);
                checkresult[6] = 2;
                psam2CardTest();
                break;
            case R.string.msg_psam2_reading:
                psam2TestResult.setText(getString(R.string.no_test));
                psam2TestResult.setTextColor(0xff1B2227);
                checkresult[7] = 2;
                scanTest();
                break;
            case R.string.msg_m1_card:
                mM1TestResult.setText(getString(R.string.no_test));
                mM1TestResult.setTextColor(0xff1B2227);
                checkresult[4] = 2;
                magCardTest();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    int[] checkresult = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    public void checkTrue(final String title, final String massage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog = DialogUtils.show(TestActivity.this, "Tip", massage,
                        getString(R.string.test_true), getString(R.string.test_false), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialog.dismiss();
                                mCardReadManager.cancelSearchCard();
                                if (title.equals("beeper")) {
                                    int status1 = manager.getLedDriver().setLed(LedLightModeEnum.ALL, false);
                                    checkresult[0] = 1;
                                    ledTestResult.setText(getString(R.string.success_test));
                                    ledTestResult.setTextColor(0xff54acea);
                                    beeperTest();
                                } else if (title.equals("rfcard")) {
                                    checkresult[1] = 1;
                                    beeperTestResult.setText(getString(R.string.success_test));
                                    beeperTestResult.setTextColor(0xff54acea);
                                    rfCardTest();
                                }
                                if (title.equals("iccard")) {
                                    checkresult[2] = 1;
                                    rfTestResult.setText(getString(R.string.success_test));
                                    rfTestResult.setTextColor(0xff54acea);
                                    icCardTest();
                                } else if (title.equals("magcard")) {
                                    checkresult[4] = 1;
                                    mM1TestResult.setText(getString(R.string.success_test));
                                    mM1TestResult.setTextColor(0xff54acea);
                                    magCardTest();
                                } else if (title.equals("psam1")) {
                                    checkresult[5] = 1;
                                    magTestResult.setText(getString(R.string.success_test));
                                    magTestResult.setTextColor(0xff54acea);
                                    psam1CardTest();
                                } else if (title.equals("psam2")) {
                                    checkresult[6] = 1;
                                    psam1TestResult.setText(getString(R.string.success_test));
                                    psam1TestResult.setTextColor(0xff54acea);
                                    psam2CardTest();
                                } else if (title.equals("scan")) {
                                    checkresult[7] = 1;
                                    psam2TestResult.setText(getString(R.string.success_test));
                                    psam2TestResult.setTextColor(0xff54acea);
                                    scanTest();
                                } else if (title.equals("print")) {
                                    checkresult[8] = 1;
                                    scanTestResult.setText(getString(R.string.success_test));
                                    scanTestResult.setTextColor(0xff54acea);
                                    testPrint();
                                } else if (title.equals("print1")) {
                                    checkresult[9] = 1;
                                    printTestResult.setText(getString(R.string.success_test));
                                    printTestResult.setTextColor(0xff54acea);

                                } else if (title.equals("m1card")) {
                                    checkresult[3] = 1;
                                    icTestResult.setText(getString(R.string.failure_test));
                                    icTestResult.setTextColor(0xffcc0000);
                                    m1CardTest();
                                }

                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialog.dismiss();
                                mCardReadManager.cancelSearchCard();
                                if (title.equals("beeper")) {
                                    checkresult[0] = 0;
                                    ledTestResult.setText(getString(R.string.failure_test));
                                    ledTestResult.setTextColor(0xffcc0000);
                                    beeperTest();
                                } else if (title.equals("rfcard")) {
                                    checkresult[1] = 0;
                                    beeperTestResult.setText(getString(R.string.failure_test));
                                    beeperTestResult.setTextColor(0xffcc0000);
                                    rfCardTest();
                                } else if (title.equals("iccard")) {
                                    checkresult[2] = 0;
                                    rfTestResult.setText(getString(R.string.failure_test));
                                    rfTestResult.setTextColor(0xffcc0000);
                                    icCardTest();
                                } else if (title.equals("magcard")) {
                                    checkresult[4] = 0;
                                    mM1TestResult.setText(getString(R.string.failure_test));
                                    mM1TestResult.setTextColor(0xffcc0000);
                                    magCardTest();
                                } else if (title.equals("psam1")) {
                                    checkresult[5] = 0;
                                    magTestResult.setText(getString(R.string.failure_test));
                                    magTestResult.setTextColor(0xffcc0000);
                                    psam1CardTest();
                                } else if (title.equals("psam2")) {
                                    checkresult[6] = 0;
                                    psam1TestResult.setText(getString(R.string.failure_test));
                                    psam1TestResult.setTextColor(0xffcc0000);
                                    psam2CardTest();
                                } else if (title.equals("scan")) {
                                    checkresult[7] = 0;
                                    psam2TestResult.setText(getString(R.string.failure_test));
                                    psam2TestResult.setTextColor(0xffcc0000);
                                    scanTest();
                                } else if (title.equals("print")) {
                                    checkresult[8] = 0;
                                    scanTestResult.setText(getString(R.string.failure_test));
                                    scanTestResult.setTextColor(0xffcc0000);
                                    testPrint();
                                } else if (title.equals("print1")) {
                                    checkresult[9] = 0;
                                    printTestResult.setText(getString(R.string.failure_test));
                                    printTestResult.setTextColor(0xffcc0000);

                                } else if (title.equals("m1card")) {
                                    checkresult[3] = 0;
                                    icTestResult.setText(getString(R.string.failure_test));
                                    icTestResult.setTextColor(0xffcc0000);
                                    m1CardTest();
                                }
                            }
                        });

            }
        });
    }

    public void checkTrue1(final String title, final String massage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCardReadManager.cancelSearchCard();

                //  dialog = DialogUtils.show(TestActivity.this,"Tip",massage,getString(R.string.))
                if (title.equals("iccard")) {
                    checkresult[2] = 1;
                    rfTestResult.setText(getString(R.string.success_test));
                    rfTestResult.setTextColor(0xff54acea);
                    icCardTest();
                } else if (title.equals("magcard")) {
                    checkresult[4] = 1;
                    mM1TestResult.setText(getString(R.string.success_test));
                    mM1TestResult.setTextColor(0xff54acea);
                    magCardTest();
                } else if (title.equals("psam1")) {
                    checkresult[5] = 1;
                    magTestResult.setText(getString(R.string.success_test));
                    magTestResult.setTextColor(0xff54acea);
                    psam1CardTest();
                } else if (title.equals("psam2")) {
                    checkresult[6] = 1;
                    psam1TestResult.setText(getString(R.string.success_test));
                    psam1TestResult.setTextColor(0xff54acea);
                    psam2CardTest();
                } else if (title.equals("scan")) {
                    checkresult[7] = 1;
                    psam2TestResult.setText(getString(R.string.success_test));
                    psam2TestResult.setTextColor(0xff54acea);
                    scanTest();
                } else if (title.equals("m1card")) {
                    checkresult[3] = 1;
                    icTestResult.setText(getString(R.string.success_test));
                    icTestResult.setTextColor(0xff54acea);
                    m1CardTest();
                }

            }
        });

    }

    int number = 0;

    @Override
    protected void onResume() {
        super.onResume();
        if (number == 1) {
            checkTrue("print", getString(R.string.scan_test_passiing));
        }
    }

    public void checkFalse(final String title, final int status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (title.equals("beeper")) {
                    checkresult[0] = 0;
                    ledTestResult.setText(getString(R.string.failure_test));
                    ledTestResult.setTextColor(0xffcc0000);
                    beeperTest();
                } else if (title.equals("rfcard")) {

                    checkresult[1] = 0;
                    beeperTestResult.setText(getString(R.string.failure_test));
                    beeperTestResult.setTextColor(0xffcc0000);
                    rfCardTest();
                } else if (title.equals("iccard")) {
                    checkresult[2] = 0;
                    rfTestResult.setText(getString(R.string.failure_test));
                    rfTestResult.setTextColor(0xffcc0000);
                    icCardTest();
                } else if (title.equals("magcard")) {
                    checkresult[4] = 0;
                    mM1TestResult.setText(getString(R.string.failure_test));
                    mM1TestResult.setTextColor(0xffcc0000);
                    magCardTest();
                } else if (title.equals("psam1")) {
                    checkresult[5] = 0;
                    magTestResult.setText(getString(R.string.failure_test));
                    magTestResult.setTextColor(0xffcc0000);

                    psam1CardTest();
                } else if (title.equals("psam2")) {
                    checkresult[6] = 0;
                    psam1TestResult.setText(getString(R.string.failure_test));
                    psam1TestResult.setTextColor(0xffcc0000);

                    psam2CardTest();
                } else if (title.equals("scan")) {
                    checkresult[7] = 0;
                    psam2TestResult.setText(getString(R.string.failure_test));
                    psam2TestResult.setTextColor(0xffcc0000);

                    scanTest();
                } else if (title.equals("print")) {
                    checkresult[8] = 0;
                    scanTestResult.setText(getString(R.string.failure_test));
                    scanTestResult.setTextColor(0xffcc0000);
                    testPrint();
                } else if (title.equals("print1")) {
                    checkresult[9] = 0;
                    printTestResult.setText(getString(R.string.failure_test));
                    printTestResult.setTextColor(0xffcc0000);

                } else if (title.equals("m1card")) {
                    checkresult[3] = 0;
                    icTestResult.setText(getString(R.string.failure_test));
                    icTestResult.setTextColor(0xffcc0000);
                    m1CardTest();
                }
            }
        });
    }

    private void initView() {
        ledTestResult = findViewById(R.id.led_test_result);
        beeperTestResult = findViewById(R.id.beeper_test_result);
        rfTestResult = findViewById(R.id.rf_test_result);
        icTestResult = findViewById(R.id.ic_test_result);
        magTestResult = findViewById(R.id.mag_test_result);
        psam1TestResult = findViewById(R.id.psam1_test_result);
        psam2TestResult = findViewById(R.id.psam2_test_result);
        scanTestResult = findViewById(R.id.scan_test_result);
        printTestResult = findViewById(R.id.print_test_result);
        startTest = findViewById(R.id.start_test);
        mM1TestResult = findViewById(R.id.m1_test_result);
        mPinKeyboard = findViewById(R.id.pin_keyboard);
        mSecurityTestResult = findViewById(R.id.security_test_result);
        mPukTestResult = findViewById(R.id.puk_test_result);
        startTest = findViewById(R.id.start_test);

        startTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTest();
            }
        });

        mPinKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyBoardOnclick();
            }
        });
    }
}
