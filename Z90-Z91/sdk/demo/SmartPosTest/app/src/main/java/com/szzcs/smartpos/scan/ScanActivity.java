package com.szzcs.smartpos.scan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.synodata.scanview.view.IDecodeListener;
import com.synodata.scanview.view.Preview;
import com.szzcs.smartpos.R;

public class ScanActivity extends Activity implements OnSharedPreferenceChangeListener, IDecodeListener {

    private static final int MSG_RESULT = 1;
    private static final int MSG_SCENE = 4;

    private ImageView mCameraFlash;
    private Preview mCameraView;
    private TextView mResult;
    private int mSceneMode = 1;

    private boolean bBeep = false;
    private boolean bVibrate = false;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_RESULT:
                    Bundle mBundle = msg.getData();
                    String result = mBundle.getString("result");
                    String type = mBundle.getString("type");
                    mResult.setText(type + ": " + result);
                    break;
                case MSG_SCENE:
                    mSceneMode = msg.arg1;
                    break;
            }
        }

    };
    private Camera mCamera;
    private Camera.Parameters mParameters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_scan);

        mCameraView = (Preview) findViewById(R.id.preview);
        mCameraFlash = (ImageView) findViewById(R.id.camera_flash);

        mCameraView.setVisibility(SurfaceView.VISIBLE);
        mCameraView.setDecodeListener(this);
        mCameraView.showCode11Pre(false);
        mCameraView.showCode39Pre(false);
        mCameraView.setEnhancedMode(false);
        mCameraView.setRotateDecodeMode(true);

        mCameraFlash.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mCamera = Camera.open();
                    int textureId = 0;
                    mCamera.setPreviewTexture(new SurfaceTexture(textureId));
                    mCamera.startPreview();

                    mParameters = mCamera.getParameters();

                    mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    mCamera.setParameters(mParameters);

                } catch (Exception e) {
                }
            }
        });
        final Button mToggle = (Button) findViewById(R.id.frame);

        //flash on off toggle
        mToggle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mCameraView.isFlashOn()) {
                    mCameraView.setFlashMode(Preview.FLASH_MODE_OFF);
                } else {
                    mCameraView.setFlashMode(Preview.FLASH_MODE_ON);
                }
            }
        });
        //decode result display
        mResult = (TextView) findViewById(R.id.result);
        //front and back camera toggle
        ImageView mCamera = (ImageView) findViewById(R.id.camera);
        mCamera.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mCameraView.getOpenCameraID() == mCameraView.CAMERA_ID_FRONT) {
                    mCameraView.setBackCamera();
                    Toast.makeText(getApplicationContext(), R.string.back, Toast.LENGTH_SHORT).show();
                    mToggle.setText(R.string.toggle_on);
                } else {
                    mCameraView.setFrontCamera();
                    Toast.makeText(getApplicationContext(), R.string.front, Toast.LENGTH_SHORT).show();
                    mToggle.setText("");
                }
            }
        });
    }


    private void updateSettings() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (sp == null)
            Log.e("CodeTest", "SP is NULL-----");
        if (sp != null) {
            mSceneMode = Integer.valueOf(sp.getString("scenemode", "1"));
            bBeep = Boolean.valueOf(sp.getBoolean("beep", true));
            bVibrate = Boolean.valueOf(sp.getBoolean("vibrate", false));

        }
    }

    @Override
    protected void onResume() {
        //update preference settings
        updateSettings();
        //set the mode before connected
        mCameraView.setSceneMode(mSceneMode);
        mCameraView.setBeep(bBeep);
        mCameraView.setVibrate(bVibrate);
        //		mCameraView.setAutoExposureLock(true);

        //connect the camera
        mCameraView.connect(getApplicationContext());
        mCameraView.startScanning();
        super.onResume();

    }

    @Override
    protected void onPause() {
        //stop camera when pause the activity
        if (mCameraView.isScanning() == true) {
            mCameraView.stopScanning();

        }
        mCameraView.disconnect();

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // remove the decode result listener
        mCameraView.clearDecodeListener();
        super.onDestroy();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    //Function to get the decode result
    //bDecoded: true decode success
    //			false decode fail
    @Override
    public void onDecodeResult(boolean bDecoded, String result, String type) {
        if (bDecoded) {
            Bundle mBundle = new Bundle();
            Message msg = new Message();
            mBundle.putString("result", result);
            mBundle.putString("type", type);
            msg.setData(mBundle);
            msg.what = MSG_RESULT;
            mHandler.sendMessage(msg);
        } else {
            Bundle mBundle = new Bundle();
            Message msg = new Message();
            mBundle.putString("result", "");
            mBundle.putString("type", "");
            msg.setData(mBundle);
            msg.what = MSG_RESULT;
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        int value = sharedPreferences.getInt(key, 1);

        if (key.equals("scenemode")) {
            mHandler.obtainMessage(MSG_SCENE, value).sendToTarget();
        }
    }

    @Override
    public void onSettingUpdateNotify() {

    }

    @Override
    public void onDecodeResult(boolean bDecoded, String result, byte[] bytesResult, String type) {
        if (bDecoded) {
            //打印bytes
            Log.d("DecodeResult", "成功!");
        } else {
            Log.d("DecodeResult", "ERROR!");
        }
    }
}
