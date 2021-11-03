package com.szzcs.smartpos;

import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.szzcs.smartpos.utils.DialogUtils;
import com.zcs.sdk.DriverManager;
import com.zcs.sdk.Printer;
import com.zcs.sdk.SdkResult;
import com.zcs.sdk.print.PrnStrFormat;
import com.zcs.sdk.print.PrnTextFont;
import com.zcs.sdk.print.PrnTextStyle;
import com.zcs.sdk.util.LogUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by yyzz on 2018/5/25.
 */

public class PrintFragment extends PreferenceFragment {
    private static final String TAG = "PrintFragment";
    private DriverManager mDriverManager = MyApp.sDriverManager;
    private Printer mPrinter;

    public static final String QR_TEXT = "https://www.baidu.com";
    public static final String BAR_TEXT = "6922711079066";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_print);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDriverManager = MyApp.sDriverManager;
        mPrinter = mDriverManager.getPrinter();

        findPreference(getString(R.string.key_paper_out)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                printPaperOut();
                return true;
            }
        });

        findPreference(getString(R.string.key_print_text)).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(final Preference preference, final Object newValue) {

                ListPreference listPreference = (ListPreference) preference;
                final int index = listPreference.findIndexOfValue((String) newValue);
                final CharSequence[] entries = listPreference.getEntries();
                if (entries[index].equals("默认字体") || entries[index].equals("Default Typeface")) {
                    LogUtils.error("打印默认字体");
                } else if (entries[index].equals("幼圆体") || entries[index].equals("Rounded Fonts")) {
                    LogUtils.error("打印圆幼体");
                    try {
                        File file = new File(Environment.getExternalStorageDirectory() + "/fonts/fangzhengyouyuan.ttf");
                        if (!file.exists()) {
                            AssetManager mAssetManger = getActivity().getAssets();
                            InputStream in = mAssetManger.open("fonts/fangzhengyouyuan.ttf");
                            saveFile(in, "fangzhengyouyuan.ttf");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                printMatrixText(index);
                return false;
            }
        });

        findPreference(getString(R.string.key_print_pic)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                printPic("print_demo.bmp");
                return true;
            }
        });
        findPreference(getString(R.string.key_print_qr)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                printQr();
                return true;
            }
        });
        findPreference(getString(R.string.key_print_bar)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                printBar();
                return true;
            }
        });

        Preference printLoopPref = findPreference(getString(R.string.key_print_loop));
        printLoopPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (isLoop) {
                    stopPrintLoop();
                } else {
                    final View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_print_loop, null);
                    final EditText et1 = view.findViewById(R.id.editText1);
                    final EditText et2 = view.findViewById(R.id.editText2);
                    DialogUtils.showViewDialog(getActivity(),
                            view,
                            "PrintLoop", null, "ok", "cancel",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DialogUtils.hintKeyBoard(view);
                                    int count = 0;
                                    int interval = 0;
                                    try {
                                        count = Integer.parseInt(et1.getText().toString().trim());
                                        interval = Integer.parseInt(et2.getText().toString().trim());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    startPrintLoop(count, interval);
                                }
                            },
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DialogUtils.hintKeyBoard(view);
                                }
                            });
                }
                return false;
            }
        });

        findPreference(getString(R.string.key_print_label)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                singleLabel();
                return false;
            }
        });

        findPreference(getString(R.string.key_print_label_loop)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (isLoop) {
                    stopPrintLoop();
                } else {
                    final View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_print_loop, null);
                    final EditText et1 = view.findViewById(R.id.editText1);
                    final EditText et2 = view.findViewById(R.id.editText2);
                    DialogUtils.showViewDialog(getActivity(),
                            view,
                            "PrintLoop", null, "ok", "cancel",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DialogUtils.hintKeyBoard(view);
                                    int count = 0;
                                    int interval = 0;
                                    try {
                                        count = Integer.parseInt(et1.getText().toString().trim());
                                        interval = Integer.parseInt(et2.getText().toString().trim());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    loopLabel(count, interval);
                                }
                            },
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DialogUtils.hintKeyBoard(view);
                                }
                            });
                }
                return false;
            }
        });
    }


    int paperWidth = 360;
    int paperHeight = 240;

    void loopLabel(final int total, final int interval) {
        isLoop = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                int ret = mPrinter.labelPrintLocationFeed();
                ret = mPrinter.labelPrintBackFeed();
                do {
                    mPrinter.setPrintAppendBarCode(getActivity(), BAR_TEXT, 320, 220, true, Layout.Alignment.ALIGN_CENTER, BarcodeFormat.CODE_128, paperWidth);
                    int printStatus = mPrinter.setLabelPrintStart(paperWidth, paperHeight);
                    if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {
                        isLoop = false;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DialogUtils.show(getActivity(), getString(R.string.printer_out_of_paper));
                            }
                        });
                        return;
                    }
                    mPrinter.labelPrintForwardFeed();

                    if (interval != 0) {
                        SystemClock.sleep(interval * 1000);
                    }
                } while (isLoop && (total == 0 || ++count < total));

                ret = mPrinter.labelPrintLocationFeed();
                isLoop = false;
            }
        }).start();
    }

    void singleLabel() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int ret = 0;
                for (int i = 0; i < 1; i++) {
                    ret = mPrinter.labelPrintLocationFeed();
                    if (ret == SdkResult.SDK_OK) {
                        ret = mPrinter.labelPrintBackFeed();
                        if (ret == SdkResult.SDK_OK) {
                            mPrinter.setPrintAppendQRCode(QR_TEXT, 220, 220, Layout.Alignment.ALIGN_CENTER, paperWidth);
                            int printStatus = mPrinter.setLabelPrintStart(paperWidth, 240);
                            if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        DialogUtils.show(getActivity(), getString(R.string.printer_out_of_paper));
                                    }
                                });
                            }
                        }
                    } else
                        break;
                }
                ret = mPrinter.labelPrintLocationFeed();
            }
        }).start();
    }

    boolean isLoop = false;

    void startPrintLoop(final int total, final int interval) {
        if (isLoop) {
            Toast.makeText(getActivity(), "Loop printing now!!!", Toast.LENGTH_SHORT).show();
            return;
        }
        isLoop = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                do {
                    final int ret = printPic("test_print.png");
                    Log.e(TAG, "print error: " + count);
                    if (ret != SdkResult.SDK_OK) {
                        isLoop = false;
                        if (ret != SdkResult.SDK_PRN_STATUS_PAPEROUT) {
                            final int cnt = count;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogUtils.show(getActivity(), "Print error, code = " + ret + "\ncount = " + (cnt + 1))
                                            .setCanceledOnTouchOutside(false);
                                }
                            });
                        }
                    }
                    if (interval != 0) {
                        SystemClock.sleep(interval * 1000);
                    }
                } while (isLoop && (total == 0 || ++count < total));
                isLoop = false;
            }
        }).start();
    }

    void stopPrintLoop() {
        isLoop = false;
        Toast.makeText(getActivity(), "Stop print", Toast.LENGTH_SHORT).show();
    }

    /**
     * paper out
     */
    private void printPaperOut() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int printStatus = mPrinter.getPrinterStatus();
                if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogUtils.show(getActivity(), getString(R.string.printer_out_of_paper));

                        }
                    });
                } else {
                    mPrinter.setPrintLine(30);
                }
            }
        }).start();
    }

    private void printMatrixText(final int fontsStyle) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AssetManager asm = getActivity().getAssets();
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogUtils.show(getActivity(), getString(R.string.printer_out_of_paper));

                        }
                    });
                } else {
                    mPrinter.setPrintAppendBitmap(bitmap, Layout.Alignment.ALIGN_CENTER);
                    PrnStrFormat format = new PrnStrFormat();
                    format.setTextSize(30);
                    format.setAli(Layout.Alignment.ALIGN_CENTER);
                    format.setStyle(PrnTextStyle.BOLD);
                    if (fontsStyle == 0) {
                        format.setFont(PrnTextFont.DEFAULT);
                    } else {
                        format.setFont(PrnTextFont.CUSTOM);
                        format.setPath(Environment.getExternalStorageDirectory() + "/fonts/fangzhengyouyuan.ttf");
                    }
                    mPrinter.setPrintAppendString(getResources().getString(R.string.pos_sales_slip), format);
                    format.setTextSize(25);
                    format.setStyle(PrnTextStyle.NORMAL);
                    format.setAli(Layout.Alignment.ALIGN_NORMAL);
                    mPrinter.setPrintAppendString(" ", format);
                    mPrinter.setPrintAppendString(getResources().getString(R.string.merchant_name) + " Test ", format);
                    mPrinter.setPrintAppendString(getResources().getString(R.string.merchant_no) + " 123456789012345 ", format);
                    mPrinter.setPrintAppendString(getResources().getString(R.string.terminal_name) + " 12345678 ", format);
                    mPrinter.setPrintAppendString(getResources().getString(R.string.operator_no) + " 01 ", format);
                    mPrinter.setPrintAppendString(getResources().getString(R.string.card_no) + " ", format);
                    format.setAli(Layout.Alignment.ALIGN_CENTER);
                    format.setTextSize(30);
                    format.setStyle(PrnTextStyle.BOLD);
                    mPrinter.setPrintAppendString("6214 44** **** **** 7816", format);
                    format.setAli(Layout.Alignment.ALIGN_NORMAL);
                    format.setStyle(PrnTextStyle.NORMAL);
                    format.setTextSize(25);
                    mPrinter.setPrintAppendString(getResources().getString(R.string.acq_institute), format);
                    mPrinter.setPrintAppendString(getResources().getString(R.string.iss) + " ", format);
                    mPrinter.setPrintAppendString(getResources().getString(R.string.trans_type) + " ", format);
                    format.setTextSize(30);
                    format.setStyle(PrnTextStyle.BOLD);
                    mPrinter.setPrintAppendString(getResources().getString(R.string.sale) + " (C) ", format);
                    format.setAli(Layout.Alignment.ALIGN_NORMAL);
                    format.setStyle(PrnTextStyle.NORMAL);
                    format.setTextSize(25);
                    mPrinter.setPrintAppendString(getResources().getString(R.string.exe_date) + " 2030/10  ", format);
                    mPrinter.setPrintAppendString(getResources().getString(R.string.batch_no) + " 000335 ", format);
                    mPrinter.setPrintAppendString(getResources().getString(R.string.voucher_no) + " 000002 ", format);
                    mPrinter.setPrintAppendString(getResources().getString(R.string.date) + " 2018/05/28 ", format);
                    mPrinter.setPrintAppendString(getResources().getString(R.string.time) + " 00:00:01 ", format);
                    format.setTextSize(30);
                    format.setStyle(PrnTextStyle.BOLD);
                    mPrinter.setPrintAppendString(getResources().getString(R.string.amount) + "￥0.01", format);
                    format.setStyle(PrnTextStyle.NORMAL);
                    format.setTextSize(25);
                    mPrinter.setPrintAppendString(getResources().getString(R.string.reference) + " ", format);
                    mPrinter.setPrintAppendString(getResources().getString(R.string.cardholder_signature) + " ", format);
                    mPrinter.setPrintAppendString(" ", format);

                    mPrinter.setPrintAppendString(" -----------------------------", format);
                    mPrinter.setPrintAppendString(getResources().getString(R.string.print_remark) + " ", format);
                    mPrinter.setPrintAppendString(getResources().getString(R.string.cardholder_copy) + " ", format);
                    mPrinter.setPrintAppendString(" ", format);
                    mPrinter.setPrintAppendString(" ", format);
                    mPrinter.setPrintAppendString(" ", format);
                    mPrinter.setPrintAppendString(" ", format);
                    printStatus = mPrinter.setPrintStart();
                    if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DialogUtils.show(getActivity(), getString(R.string.printer_out_of_paper));
                            }
                        });
                    }
                }
            }
        }).start();
    }

    public static void saveFile(InputStream inputStream, String fileName) {
        File appDir = new File(Environment.getExternalStorageDirectory(), "fonts");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File file = new File(appDir, fileName);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            // 开始读取
            while ((len = inputStream.read(bs)) != -1) {
                fos.write(bs, 0, len);
            }

            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int printPic(String path) {
        int printStatus = mPrinter.getPrinterStatus();
        if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DialogUtils.show(getActivity(), getString(R.string.printer_out_of_paper));

                }
            });
        } else {
            try {
                InputStream inputStream = getActivity().getAssets().open(path);
                Drawable drawable = Drawable.createFromStream(inputStream, null);
                Bitmap mBitmapDef = ((BitmapDrawable) drawable).getBitmap();

                PrnStrFormat format = new PrnStrFormat();
                mPrinter.setPrintAppendBitmap(mBitmapDef, Layout.Alignment.ALIGN_CENTER);
                mPrinter.setPrintAppendString(" ", format);
                mPrinter.setPrintAppendString(" ", format);
                mPrinter.setPrintAppendString(" ", format);
                printStatus = mPrinter.setPrintStart();
                if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogUtils.show(getActivity(), getString(R.string.printer_out_of_paper));

                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return printStatus;
    }


    private void printQr() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int printStatus = mPrinter.getPrinterStatus();
                if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogUtils.show(getActivity(), getString(R.string.printer_out_of_paper));

                        }
                    });
                } else {

                    PrnStrFormat format = new PrnStrFormat();
                    mPrinter.setPrintAppendString(getString(R.string.show_qrcode_status1), format);
                    mPrinter.setPrintAppendQRCode(QR_TEXT, 200, 200, Layout.Alignment.ALIGN_NORMAL);
                    mPrinter.setPrintAppendString(" ", format);
                    mPrinter.setPrintAppendString(getString(R.string.show_qrcode_status2), format);
                    mPrinter.setPrintAppendQRCode(QR_TEXT, 200, 200, Layout.Alignment.ALIGN_OPPOSITE);
                    mPrinter.setPrintAppendString(" ", format);
                    mPrinter.setPrintAppendString(getString(R.string.show_qrcode_status3), format);
                    mPrinter.setPrintAppendQRCode(QR_TEXT, 200, 200, Layout.Alignment.ALIGN_CENTER);
                    mPrinter.setPrintAppendString(" ", format);
                    mPrinter.setPrintAppendString(" ", format);
                    mPrinter.setPrintAppendString(" ", format);
                    printStatus = mPrinter.setPrintStart();
                    if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DialogUtils.show(getActivity(), getString(R.string.printer_out_of_paper));

                            }
                        });
                    }
                }
            }
        }).start();
    }

    private void printBar() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int printStatus = mPrinter.getPrinterStatus();
                if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogUtils.show(getActivity(), getString(R.string.printer_out_of_paper));

                        }
                    });
                } else {
                    PrnStrFormat format = new PrnStrFormat();
                    mPrinter.setPrintAppendString(getString(R.string.show_barcode_status1), format);
                    mPrinter.setPrintAppendBarCode(getActivity(), BAR_TEXT, 360, 100, true, Layout.Alignment.ALIGN_NORMAL, BarcodeFormat.CODE_128);
                    mPrinter.setPrintAppendString(" ", format);
                    mPrinter.setPrintAppendString(getString(R.string.show_barcode_status2), format);
                    mPrinter.setPrintAppendBarCode(getActivity(), BAR_TEXT, 300, 80, false, Layout.Alignment.ALIGN_CENTER, BarcodeFormat.CODE_128);
                    mPrinter.setPrintAppendString(" ", format);
                    mPrinter.setPrintAppendString(getString(R.string.show_barcode_status3), format);
                    mPrinter.setPrintAppendBarCode(getActivity(), BAR_TEXT, 300, 100, false, Layout.Alignment.ALIGN_OPPOSITE, BarcodeFormat.CODE_128);
                    mPrinter.setPrintAppendString(" ", format);
                    mPrinter.setPrintAppendString(" ", format);
                    mPrinter.setPrintAppendString(" ", format);
                    printStatus = mPrinter.setPrintStart();
                    if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DialogUtils.show(getActivity(), getString(R.string.printer_out_of_paper));

                            }
                        });
                    }

                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isLoop = false;
    }
}
