package com.szzcs.smartpos;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.szzcs.smartpos.base.BaseActivity;
import com.szzcs.smartpos.utils.FileUtils;
import com.zcs.sdk.DriverManager;
import com.zcs.sdk.SdkResult;
import com.zcs.sdk.card.CardInfoEntity;
import com.zcs.sdk.card.CardReaderManager;
import com.zcs.sdk.card.CardReaderTypeEnum;
import com.zcs.sdk.card.CardSlotNoEnum;
import com.zcs.sdk.card.ICCard;
import com.zcs.sdk.card.MagCard;
import com.zcs.sdk.card.RfCard;
import com.zcs.sdk.emv.EmvApp;
import com.zcs.sdk.emv.EmvCapk;
import com.zcs.sdk.emv.EmvData;
import com.zcs.sdk.emv.EmvHandler;
import com.zcs.sdk.emv.EmvResult;
import com.zcs.sdk.emv.EmvTermParam;
import com.zcs.sdk.emv.EmvTransParam;
import com.zcs.sdk.emv.OnEmvListener;
import com.zcs.sdk.listener.OnSearchCardListener;
import com.zcs.sdk.pin.PinAlgorithmMode;
import com.zcs.sdk.pin.pinpad.PinPadManager;
import com.zcs.sdk.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * Run emv in android
 */
public class EmvActivity extends BaseActivity {
    private static final String TAG = "EmvActivity";
    private DriverManager mDriverManager = MyApp.sDriverManager;
    private CardReaderManager mCardReadManager;
    private EmvHandler emvHandler;
    private ICCard mICCard;
    private MagCard mMAGCard;
    private RfCard mRFCard;
    private PinPadManager mPinPadManager;

    protected TextView mTvLog;
    protected ScrollView mScrollView;
    protected ListView mList;
    protected Dialog mSearchDialog;

    private ArrayAdapter<String> mAdapter;
    private String[] listItems;
    private StringBuffer sbLog = new StringBuffer();
    private static DateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");

    private int iRet;
    CountDownLatch mLatch;
    private int inputPINResult = 0x00;
    CardReaderTypeEnum realCardType;
    private byte[] mPinBlock = new byte[12 + 1];
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_emv);
        mContext = this;
        initView();
        initSdk();
    }

    private void initView() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.pref_emv);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mTvLog = (TextView) findViewById(R.id.tv_log);
        mScrollView = (ScrollView) findViewById(R.id.scrollView);
        mList = (ListView) findViewById(R.id.list);

        listItems = new String[4];
        listItems[0] = getString(R.string.pref_magnetic);
        listItems[1] = getString(R.string.pref_ic);
        listItems[2] = getString(R.string.pref_rf);
        listItems[3] = getString(R.string.pref_read_all);
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItems);
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mTvLog.setText("");
                CardReaderTypeEnum cardType = CardReaderTypeEnum.IC_CARD;
                switch (position) {
                    case 0:
                        cardType = CardReaderTypeEnum.MAG_CARD;
                        break;
                    case 1:
                        cardType = CardReaderTypeEnum.IC_CARD;
                        break;
                    case 2:
                        cardType = CardReaderTypeEnum.RF_CARD;
                        break;
                    case 3:
                        cardType = CardReaderTypeEnum.MAG_IC_RF_CARD;
                        break;
                }
                searchCard(cardType);
            }
        });
    }

    private void initSdk() {
        // Config the SDK base info
        mCardReadManager = mDriverManager.getCardReadManager();
        mICCard = mCardReadManager.getICCard();
        mMAGCard = mCardReadManager.getMAGCard();
        mRFCard = mCardReadManager.getRFCard();
        mPinPadManager = mDriverManager.getPadManager();
        emvHandler = EmvHandler.getInstance();
    }

    OnSearchCardListener mListener = new OnSearchCardListener() {
        @Override
        public void onError(int resultCode) {
            mCardReadManager.closeCard();
            closeDialog();
        }

        @Override
        public void onCardInfo(CardInfoEntity cardInfoEntity) {
            realCardType = cardInfoEntity.getCardExistslot();
            showLog("Deleted card " + realCardType.name());
            switch (realCardType) {
                case RF_CARD:
                    readRf();
                    break;
                case MAG_CARD:
                    getMagData();
                    break;
                case IC_CARD:
                    readIc();
                    break;
                default:
                    break;
            }
            closeDialog();
        }

        @Override
        public void onNoCard(CardReaderTypeEnum arg0, boolean arg1) {
        }
    };

    private void searchCard(final CardReaderTypeEnum cardType) {
        if (!isFinishing()) {
            mSearchDialog = ProgressDialog.show(EmvActivity.this, null, "SearchCard...", true, true, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    // 发送取消寻卡指令
                    cancelSearchCard();
                }
            });
        }

        mCardReadManager.cancelSearchCard();
        mCardReadManager.searchCard(cardType, 0, mListener);

        showLog("Search " + cardType.name() + "....");
    }

    private void cancelSearchCard() {
        mCardReadManager.cancelSearchCard();
        showLog("cancelSearchCard:  ");
    }

    private void closeDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mSearchDialog != null && mSearchDialog.isShowing()) {
                    mSearchDialog.dismiss();
                }
            }
        });
    }

    void readRf() {
        byte resetData[] = new byte[EmvData.BUF_LEN];
        int datalength[] = new int[1];
        iRet = mRFCard.rfReset(resetData, datalength);
        if (iRet != 0) {
            showLog("rf reset error");
            closeDialog();
            return;
        }
        emv(realCardType);
    }

    void readIc() {
        iRet = mICCard.icCardReset(CardSlotNoEnum.SDK_ICC_USERCARD);
        if (iRet != 0) {
            showLog("ic reset error");
            closeDialog();
            return;
        }
        emv(realCardType);
    }

    private void loadMasterCardCapks(EmvHandler emvHandle) {
        EmvCapk capk = new EmvCapk();
        capk.setKeyID((byte) 0x05);
        capk.setRID("A000000004");
        capk.setModul("B8048ABC30C90D976336543E3FD7091C8FE4800"
                + "DF820ED55E7E94813ED00555B573FECA3D84AF6"
                + "131A651D66CFF4284FB13B635EDD0EE40176D8B"
                + "F04B7FD1C7BACF9AC7327DFAA8AA72D10DB3B"
                + "8E70B2DDD811CB4196525EA386ACC33C0D9D45"
                + "75916469C4E4F53E8E1C912CC618CB22DDE7C3"
                + "568E90022E6BBA770202E4522A2DD623D180E21"
                + "5BD1D1507FE3DC90CA310D27B3EFCCD8F83DE"
                + "3052CAD1E48938C68D095AAC91B5F37E28BB49EC7ED597");
        capk.setCheckSum("EBFA0D5D06D8CE702DA3EAE890701D45E274C845");
        capk.setExpDate("20211231"); // YYYYMMDD

        emvHandle.addCapk(capk);

    }

    private void loadVisaAIDs(EmvHandler emvHandle) {
        // Visa Credit/Debit
        EmvApp ea = new EmvApp();

        ea.setAid("A0000000031010");
        ea.setSelFlag((byte) 0);
        ea.setTargetPer((byte) 0x00);
        ea.setMaxTargetPer((byte) 0);
        ea.setFloorLimit(1000);
        ea.setOnLinePINFlag((byte) 1);
        ea.setThreshold(0);
        ea.setTacDefault("0000000000");
        ea.setTacDenial("0000000000");
        ea.setTacOnline("0000000000");
        ea.settDOL("0F9F02065F2A029A039C0195059F3704");
        ea.setdDOL("039F3704");
        ea.setVersion("008C");
        ea.setClTransLimit("000000015000");
        ea.setClOfflineLimit("000000008000");
        ea.setClCVMLimit("000000005000");
        ea.setEcTTLVal("000000100000");

        emvHandle.addApp(ea);


        // Visa Electron
        ea = new EmvApp();

        ea.setAid("A0000000032010");
        ea.setSelFlag((byte) 0);
        ea.setTargetPer((byte) 0x00);
        ea.setMaxTargetPer((byte) 0);
        ea.setFloorLimit(1000);
        ea.setOnLinePINFlag((byte) 1);
        ea.setThreshold(0);
        ea.setTacDefault("0000000000");
        ea.setTacDenial("0000000000");
        ea.setTacOnline("0000000000");
        ea.settDOL("0F9F02065F2A029A039C0195059F3704");
        ea.setdDOL("039F3704");
        ea.setVersion("008C");
        ea.setClTransLimit("000000015000");
        ea.setClOfflineLimit("000000008000");
        ea.setClCVMLimit("000000005000");
        ea.setEcTTLVal("000000100000");

        emvHandle.addApp(ea);

        // Visa Plus
        ea = new EmvApp();

        ea.setAid("A0000000038010");
        ea.setSelFlag((byte) 0);
        ea.setTargetPer((byte) 0x00);
        ea.setMaxTargetPer((byte) 0);
        ea.setFloorLimit(1000);
        ea.setOnLinePINFlag((byte) 1);
        ea.setThreshold(0);
        ea.setTacDefault("0000000000");
        ea.setTacDenial("0000000000");
        ea.setTacOnline("0000000000");
        ea.settDOL("0F9F02065F2A029A039C0195059F3704");
        ea.setdDOL("039F3704");
        ea.setVersion("008C");
        ea.setClTransLimit("000000015000");
        ea.setClOfflineLimit("000000008000");
        ea.setClCVMLimit("000000005000");
        ea.setEcTTLVal("000000100000");

        emvHandle.addApp(ea);
    }

    String[] aids = new String[]{
            "A000000333010101",
            "A000000333010102",
            "A000000333010103",
            "A000000333010106",
            "A000000333010108",
            "A0000003330101",
            "A00000000305076010",
            "A0000000031010",
            "A000000003101001",
            "A000000003101002",
            "A0000000032010",
            "A0000000032020",
            "A0000000033010",
            "A0000000034010",
            "A0000000035010",
            "A0000000036010",
            "A0000000036020",
            "A0000000038002",
            "A0000000038010",
            "A0000000039010",
            "A000000003999910",
            "A00000000401",
            "A0000000041010",
            "A00000000410101213",
            "A00000000410101215",
            "A0000000042010",
            "A0000000043010",
            "A0000000043060",
            "A000000004306001",
            "A0000000044010",
            "A0000000045010",
            "A0000000046000",
            "A0000000048002",
            "A0000000049999",
            "A0000000050001",
            "A0000000050002",
            "A00000002401",
            "A000000025",
            "A0000000250000",
            "A00000002501",
            "A000000025010402",
            "A000000025010701",
            "A000000025010801",
            "A0000000291010",
            "A0000000421010",
            "A0000000422010",
            "A0000000423010",
            "A0000000424010",
            "A0000000425010",
            "A0000000426010",
            "A00000006510",
            "A0000000651010",
            "A00000006900",
            "A000000077010000021000000000003B",
            "A000000098",
            "A0000000980848",
            "A0000001211010",
            "A0000001410001",
            "A0000001523010",
            "A0000001524010",
            "A0000001544442",
            "A000000172950001",
            "A0000001850002",
            "A0000002281010",
            "A0000002282010",
            "A0000002771010",
            "A00000031510100528",
            "A0000003156020",
            "A0000003591010028001",
            "A0000003710001",
            "A0000004540010",
            "A0000004540011",
            "A0000004766C",
            "A0000005241010",
            "A0000006723010",
            "A0000006723020",
            "A0000007705850",
            "B012345678",
            "D27600002545500100",
            "D5280050218002",
            "D5780000021010",
            "F0000000030001"
    };

    void loadAids(EmvHandler emvHandler) {
        for (String aid : aids) {
            EmvApp ea = new EmvApp();
            //ea.setAid("A00000000305076010");
            ea.setAid(aid);
            ea.setSelFlag((byte) 0);
            ea.setTargetPer((byte) 0x00);
            ea.setMaxTargetPer((byte) 0);
            ea.setFloorLimit(1000);
            ea.setOnLinePINFlag((byte) 1);
            ea.setThreshold(0);
            ea.setTacDefault("0000000000");
            ea.setTacDenial("0000000000");
            ea.setTacOnline("0000000000");
            ea.settDOL("0F9F02065F2A029A039C0195059F3704");
            ea.setdDOL("039F3704");
            ea.setVersion("008C");
            ea.setClTransLimit("000000015000");
            ea.setClOfflineLimit("000000008000");
            ea.setClCVMLimit("000000005000");
            ea.setEcTTLVal("000000100000");
            // paypass
            ea.setPayPassTermType((byte) 0x22);
            ea.setTermCapNoCVMReq("E0E9C8");
            ea.setTermCapCVMReq("E0E9C8");
            ea.setUdol("9F6A04");

            emvHandler.addApp(ea);
        }
    }

    private void emv(CardReaderTypeEnum cardType) {
        // 1. copy aid and capk to '/sdcard/emv/' as the default aid and capk
        try {
            if (!new File(EmvTermParam.emvParamFilePath).exists()) {
                FileUtils.doCopy(EmvActivity.this, "emv", EmvTermParam.emvParamFilePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 2. set params
        final EmvTransParam emvTransParam = new EmvTransParam();
        if (cardType == CardReaderTypeEnum.IC_CARD) {
            emvTransParam.setTransKernalType(EmvData.KERNAL_EMV_PBOC);
        } else if (cardType == CardReaderTypeEnum.RF_CARD) {
            emvTransParam.setTransKernalType(EmvData.KERNAL_CONTACTLESS_ENTRY_POINT);
        }
        emvHandler.transParamInit(emvTransParam);
        final EmvTermParam emvTermParam = new EmvTermParam();
        emvHandler.kernelInit(emvTermParam);

        // 3. add aid or capk
        //emvHandler.delAllApp();
        //emvHandler.delAllCapk();
        loadAids(emvHandler);
        //loadVisaAIDs(emvHandler);
        //loadMasterCardCapks(emvHandler);

        // 4. transaction
        byte[] pucIsEcTrans = new byte[1];
        byte[] pucBalance = new byte[6];
        byte[] pucTransResult = new byte[1];

        OnEmvListener onEmvListener = new OnEmvListener() {
            @Override
            public int onSelApp(String[] appLabelList) {
                Log.d("Debug", "onSelApp");
                return iRet;
            }

            @Override
            public int onConfirmCardNo(String cardNo) {
                Log.d("Debug", "onConfirmCardNo");
                String[] track2 = new String[1];
                final String[] pan = new String[1];
                emvHandler.getTrack2AndPAN(track2, pan);
                int index = 0;
                if (track2[0].contains("D")) {
                    index = track2[0].indexOf("D") + 1;
                } else if (track2[0].contains("=")) {
                    index = track2[0].indexOf("=") + 1;
                }
                final String exp = track2[0].substring(index, index + 4);
                showLog("cardNum:" + pan[0]);
                showLog("exp:" + exp);
                return 0;
            }

            @Override
            public int onInputPIN(byte pinType) {
                // 1. open the secret pin pad to get pin block
                // 2. send the pinBlock to emv kernel
                if (emvTransParam.getTransKernalType() == EmvData.KERNAL_CONTACTLESS_ENTRY_POINT) {
                    String[] track2 = new String[1];
                    final String[] pan = new String[1];
                    emvHandler.getTrack2AndPAN(track2, pan);
                    int index = 0;
                    if (track2[0].contains("D")) {
                        index = track2[0].indexOf("D") + 1;
                    } else if (track2[0].contains("=")) {
                        index = track2[0].indexOf("=") + 1;
                    }
                    final String exp = track2[0].substring(index, index + 4);
                    showLog("card:" + pan[0]);
                    showLog("exp:" + exp);
                }
                Log.d("Debug", "onInputPIN");
                int iRet = 0;
                iRet = inputPIN(pinType);
                Log.d("Debug", "iRet=" + iRet);
                if (iRet == EmvResult.EMV_OK) {
                    emvHandler.setPinBlock(mPinBlock);
                }
                return iRet;
            }

            @Override
            public int onCertVerify(int certType, String certNo) {
                Log.d("Debug", "onCertVerify");
                return 0;
            }

            @Override
            public byte[] onExchangeApdu(byte[] send) {
                Log.d("Debug", "onExchangeApdu");
                if (realCardType == CardReaderTypeEnum.IC_CARD) {
                    return mICCard.icExchangeAPDU(CardSlotNoEnum.SDK_ICC_USERCARD, send);
                } else if (realCardType == CardReaderTypeEnum.RF_CARD) {
                    return mRFCard.rfExchangeAPDU(send);
                }
                return null;
            }

            @Override
            public int onlineProc() {
                // 1. assemble the authorisation request data and send to bank by using get 'emvHandler.getTlvData()'
                // 2. separateOnlineResp to emv kernel
                // 3. return the callback ret
                Log.d("Debug", "onOnlineProc");
                byte[] authRespCode = new byte[3];
                byte[] issuerResp = new byte[512];
                int[] issuerRespLen = new int[1];
                int iSendRet = emvHandler.separateOnlineResp(authRespCode, issuerResp, issuerRespLen[0]);
                Log.d("Debug", "separateOnlineResp iSendRet=" + iSendRet);
                return 0;
            }

        };
        showLog("Emv Trans start...");
        // for the emv result, plz refer to emv doc.
        int ret = emvHandler.emvTrans(emvTransParam, onEmvListener, pucIsEcTrans, pucBalance, pucTransResult);
        showLog("Emv trans end, ret = " + ret);
        String str = "Decline";
        if (pucTransResult[0] == EmvData.APPROVE_M) {
            str = "Approve";
        } else if (pucTransResult[0] == EmvData.ONLINE_M) {
            str = "Online";
        } else if (pucTransResult[0] == EmvData.DECLINE_M) {
            str = "Decline";
        }
        showLog("Emv trans result = " + pucTransResult[0] + ", " + str);
        if (ret == 0) {
            getEmvData();
        }
        mCardReadManager.closeCard();
    }

    private void getMagData() {
        Log.d("Debug", "MAG_CARD");
        showLog("Mag card swipe");
        CardInfoEntity magReadData = mCardReadManager.getMAGCard().getMagReadData();
        MyApp.cardInfoEntity = magReadData;
        if (magReadData.getResultcode() == SdkResult.SDK_OK) {
            String tk1 = magReadData.getTk1();
            String tk2 = magReadData.getTk2();
            String tk3 = magReadData.getTk3();
            String expiredDate = magReadData.getExpiredDate();
            String cardNo = magReadData.getCardNo();
            showLog("tk1:  " + tk1);
            showLog("tk2:  " + tk2);
            showLog("tk3:  " + tk3);
            showLog("expiredDate:  " + expiredDate);
            showLog("cardNo:  " + cardNo);
        } else {
            showLog("Mag card read error:  " + magReadData.getResultcode());
        }

    }

    int[] tags = {
            0x9F26,
            0x9F27,
            0x9F10,
            0x9F37,
            0x9F36,
            0x95,
            0x9A,
            0x9C,
            0x9F02,
            0x5F2A,
            0x82,
            0x9F1A,
            0x9F03,
            0x9F33,
            0x9F34,
            0x9F35,
            0x9F1E,
            0x84,
            0x9F09,
            0x9F41,
            0x9F63,
            0x5F24
    };

    private void getEmvData() {
        byte[] field55 = emvHandler.packageTlvList(tags);
        showLog("Filed55: " + StringUtils.convertBytesToHex(field55));
    }

    /**
     * To use the pin pad, you must register the activity for pin. Plz refer to the lib doc.
     *
     * @param pinType
     * @return
     */
    public int inputPIN(byte pinType) {
        final byte InputPinType = pinType;
        mLatch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PinPadManager.OnPinPadInputListener onPinPadInputListener = new PinPadManager.OnPinPadInputListener() {

                        @Override
                        public void onSuccess(byte[] pinBlock) {
                            System.arraycopy(pinBlock, 0, mPinBlock, 0, pinBlock.length);
                            mPinBlock[pinBlock.length] = 0x00;

                            String encryptedPin = emvHandler.bytesToHexString(mPinBlock);
                            Log.d("Debug", "encryptedPin=" + encryptedPin);

                            if (encryptedPin.length() == 0) {
                                inputPINResult = EmvResult.EMV_NO_PASSWORD;
                            } else {// pin length =0
                                inputPINResult = EmvResult.EMV_OK;
                            }
                            mLatch.countDown();
                        }

                        @Override
                        public void onError(int backCode) {
                            Log.d("Debug", "backCode=" + backCode);
                            if (backCode == SdkResult.SDK_PAD_ERR_NOPIN) {
                                inputPINResult = EmvResult.EMV_NO_PASSWORD;
                            } else if (backCode == SdkResult.SDK_PAD_ERR_TIMEOUT) {
                                inputPINResult = EmvResult.EMV_TIME_OUT;
                            } else if (backCode == SdkResult.SDK_PAD_ERR_CANCEL) {
                                inputPINResult = EmvResult.EMV_USER_CANCEL;
                            } else {
                                inputPINResult = EmvResult.EMV_NO_PINPAD_OR_ERR;
                            }

                            mLatch.countDown();
                        }
                    };

                    Log.d("Debug", "InputPinType=" + InputPinType);
                    if (InputPinType == EmvData.ONLINE_ENCIPHERED_PIN) {
                        String track2[] = new String[1];
                        String pan[] = new String[1];
                        iRet = emvHandler.getTrack2AndPAN(track2, pan);
                        mPinPadManager.inputOnlinePin(mContext, (byte) 4, (byte) 12, 60, true, pan[0], (byte) 0, PinAlgorithmMode.ANSI_X_9_8, onPinPadInputListener);
                    } else {
                        mPinPadManager.inputOfflinePin(mContext, (byte) 4, (byte) 12, 60, true, onPinPadInputListener);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        try {
            mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return inputPINResult;
    }

    public void showLog(final String log) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, log);
                Date date = new Date();
                sbLog.append(dateFormat.format(date)).append(":");
                sbLog.append(log);
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
}
