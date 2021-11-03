package com.szzcs.smartpos.qr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.szzcs.smartpos.R;
import com.szzcs.smartpos.base.BaseActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

public class QRTestActivity extends BaseActivity implements View.OnClickListener {
    private Button btn_create, btn_scanner;
    private ImageView imageView;
    private EditText et;
    private String time;
    private File file = null;
    private TextView tv;
    android.support.v7.app.ActionBar actionBar;
    private static final int MSG_ACTIVE = 5;
    //    private int[] mBarcodeList = {CodeID.CODEEAN13, CodeID.CODEEAN8, CodeID.CODE128,
    //            CodeID.CODE39, CodeID.QR, CodeID.PDF417, CodeID.DM};
    ////    private TextView mToast;
    ////    private Button mEnter;
    //    private  PermissionsManager mPermissionsManager;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_ACTIVE:
                    // mToast.setText((String) msg.obj);
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrtest);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.pref_scan));
        }
        btn_create = (Button) findViewById(R.id.btn_create);
        btn_scanner = (Button) findViewById(R.id.btn_scanner);
        imageView = (ImageView) findViewById(R.id.image);
        et = (EditText) findViewById(R.id.editText);
        String res = getIntent().getStringExtra("QRCODE");
        et.setText(res == null ? "" : res);
        tv = (TextView) findViewById(R.id.tv_tips);
        btn_create.setOnClickListener(this);
        btn_scanner.setOnClickListener(this);
        imageView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                saveCurrentImage();
                return true;
            }
        });


    /*    // 动态权限检查器
        mPermissionsManager = new PermissionsManager(this) {
            @Override
            public void authorized(int requestCode) {
                activeBarcode();
            }

            @Override
            public void noAuthorization(int requestCode, String[] lacksPermissions) {
                AlertDialog.Builder builder = new AlertDialog.Builder(QRTestActivity.this);
                builder.setTitle("提示");
                builder.setMessage("缺少相关权限！");
                builder.setPositiveButton("设置权限", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PermissionsManager.startAppSettings(getApplicationContext());
                    }
                });
                builder.create().show();
            }

            @Override
            public void ignore() {
                activeBarcode();
            }
        };*/
    }

    @Override
    protected void onResume() {
        super.onResume();

       /* // 要校验的权限
        String[] PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.READ_PHONE_STATE};
        // 检查权限
        mPermissionsManager.checkPermissions(0, PERMISSIONS);*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_create:
                String msg = et.getText().toString();
                if (TextUtils.isEmpty(msg)) {
                    Toast.makeText(QRTestActivity.this, "please input", Toast.LENGTH_LONG).show();
                    return;
                }
                //生成二维码图片，第一个参数是二维码的内容，第二个参数是正方形图片的边长，单位是像素
                // Generate a two-dimensional code picture, the first parameter is the content of two-dimensional code, the second parameter is the square picture side length, the unit is pixels
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapUtil.create2DCoderBitmap(msg, 400, 400);
                    //bitmap = BitmapUtil.CreateOneDCode("0123456789012");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(bitmap);
                tv.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_scanner:
                Intent mIntent = new Intent(QRTestActivity.this, CaptureActivity.class);
                // mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //  mIntent.setClass(getApplicationContext(), CaptureActivity.class);
                startActivity(mIntent);
                break;
            default:
                break;
        }
    }

    //这种方法状态栏是空白，显示不了状态栏的信息
    //This method status bar is blank, can not display the status bar information
    private void saveCurrentImage() {
        //获取当前屏幕的大小
        //Gets the size of the current screen
        int width = getWindow().getDecorView().getRootView().getWidth();
        int height = getWindow().getDecorView().getRootView().getHeight();
        //生成相同大小的图片
        //Generate the same size of the picture
        Bitmap temBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        View view = getWindow().getDecorView().getRootView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        //从缓存中获取当前屏幕的图片,创建一个DrawingCache的拷贝，因为DrawingCache得到的位图在禁用后会被回收
        //Get the current screen image from the cache and create a copy of the DrawingCache because the bitmap that DrawingCache gets is disabled after being disabled
        temBitmap = view.getDrawingCache();
        SimpleDateFormat df = new SimpleDateFormat("yyyymmddhhmmss");
        time = df.format(new Date());
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/screen", time + ".png");
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                temBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/screen/" + time + ".png";
                    final Result result = parseQRcodeBitmap(path);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(QRTestActivity.this, result.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).start();
            //禁用DrawingCahce否则会影响性能 ,而且不禁止会导致每次截图到保存的是第一次截图缓存的位图
            //Disable DrawingCahce otherwise it will affect the performance, and does not prohibit each capture will lead to the first screenshot to save the cache bitmap
            view.setDrawingCacheEnabled(false);
        }
    }

    //解析二维码图片,返回结果封装在Result对象中
    //The QRcode is parsed and the result is wrapped in a Result object
    private Result parseQRcodeBitmap(String bitmapPath) {
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(bitmapPath, options);
        options.inSampleSize = options.outHeight / 400;
        if (options.inSampleSize <= 0) {
            options.inSampleSize = 1;
        }
        /**
         * 辅助节约内存设置 Auxiliary saves memory settings
         *
         * options.inPreferredConfig = Bitmap.Config.ARGB_4444;    
         * options.inPurgeable = true;
         * options.inInputShareable = true;
         */
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(bitmapPath, options);
        RGBLuminanceSource rgbLuminanceSource = new RGBLuminanceSource(bitmap);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(rgbLuminanceSource));
        QRCodeReader reader = new QRCodeReader();
        Result result = null;
        try {
            result = reader.decode(binaryBitmap, hints);
        } catch (Exception e) {
        }
        return result;
    }


    //    //Active the Barcode before use it to scan and decode.
    //    //Decode can only return correct result when it's activated.
    //
    //    private void activeBarcode() {
    //        //set a readable and writeable path to save the download license file.
    //        //path need to be end with a "/"
    //        CodeUtils.setLicPathName("/mnt/sdcard/apklic/", "test");
    //        CodeUtils.enableDebug(true);
    //        CodeUtils mUtils = new CodeUtils(getApplicationContext());
    //
    //        if (mUtils.isBarcodeActivated() == false) {
    //            mUtils.tryActivateBarcode(new IActivateListener() {
    //                //this function will be called during active process, and return the process messages
    //                @Override
    //                public void onActivateProcess(String msg) {
    //                    // post the processing message
    //                    mHandler.obtainMessage(MSG_ACTIVE,
    //                            msg
    //                    ).sendToTarget();
    //                }
    //
    //                // this function will be called after the active process.
    //                //result_code: CodeUtils.RESULT_SUCCESS means active success, others means fail
    //                // error: return the fail cause message.
    //                @Override
    //                public void onActivateResult(int result_code, String error) {
    //                    // post the result message
    //                    mHandler.obtainMessage(MSG_ACTIVE,
    ////							result_code+""
    //                            error
    //                    ).sendToTarget();
    //                }
    //
    //                //Current Active state when calling active function.
    //                //if it's unactive state, this function will be returned when active process is done.
    //                @Override
    //                public void onActivateState(boolean bActivated) {
    //                    if (bActivated) // barcode is in activated state
    //                    {
    //                        //config the barcode
    //                        configBarcode();
    //                        // show the jump button
    //                       // mEnter.setVisibility(View.VISIBLE);
    //                    }
    //                }
    //            });
    //        } else {
    //            configBarcode();
    //            // show the jump button
    //          //  mEnter.setVisibility(View.VISIBLE);
    //        }
    //
    //    }
    //
    //    // config the supporting barcode types.
    //    //all the supported types are listed in CodeID.
    //    private void configBarcode() {
    //        CodeUtils mUtils = new CodeUtils(getApplicationContext());
    //
    //        //clear old configs
    //        mUtils.enableAllFormats(false);
    //        //set new config, just enable the type in the list
    //        for (int i = 0; i < mBarcodeList.length; i++)
    //            mUtils.enableCodeFormat(mBarcodeList[i]);
    //
    //
    //    }
    //
    //    @Override
    //    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    //        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    //        mPermissionsManager.recheckPermissions(requestCode, permissions, grantResults);
    //    }

}
