package com.szzcs.smartpos;

import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.szzcs.scan.OnActivateListener;
import com.szzcs.scan.SDKUtils;
import com.szzcs.smartpos.base.BaseActivity;
import com.szzcs.smartpos.scan.ScanActivity;
import com.szzcs.smartpos.utils.DialogUtils;
import com.szzcs.smartpos.utils.update.UpdateBuilder;
import com.zcs.sdk.SdkResult;
import com.zcs.sdk.Sys;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.pref_settings));
        }
        Fragment fragment = new SettingsFragment();
        if (savedInstanceState == null)
            getFragmentManager().beginTransaction().add(R.id.frame_container, fragment).commit();
        UpdateBuilder.create().check();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_scan:
                showVerifyDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showVerifyDialog() {
        final View view = LayoutInflater.from(this).inflate(R.layout.view_dialog, null);
        final EditText etID = (EditText) view.findViewById(R.id.et_id);
        final EditText etPwd = (EditText) view.findViewById(R.id.et_pwd);
        Dialog dialog = DialogUtils.showViewDialog(this, view, getString(R.string.input_pwd), null,
                "OK", "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String id = etID.getText().toString();
                        String pwd = etPwd.getText().toString();
                        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(pwd)) {
                            return;
                        }
                        dialog.dismiss();
                        if (etPwd.getText().toString().equals(getString(R.string.pwd_str))) {
                            final Dialog loading = DialogUtils.showProgress(MainActivity.this, "Waiting", "Activate Barcode...");
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    String sn = getSn();
                                    if (sn == null) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                                                loading.dismiss();
                                            }
                                        });
                                        return;
                                    }
                                    activateBarcode(id, loading, sn);
                                }
                            }).start();
                        } else {
                            Toast.makeText(MainActivity.this, getString(R.string.pwd_wrong), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DialogUtils.hintKeyBoard(view);
                    }
                });
    }

    private void activateBarcode(String id, final Dialog loading, String sn) {
        SDKUtils.getInstance(MainActivity.this, id)
                .setDeviceId(sn)
                .activeBarcode(new OnActivateListener() {
                    @Override
                    public void onActivateResult(int code, String error) {
                        Log.e(TAG, "code = " + code + "   error = " + error);
                    }

                    @Override
                    public void onActivateProcess(String msg) {
                        Log.e(TAG, msg);
                    }

                    @Override
                    public void onActivateState(final boolean isActivated) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loading.dismiss();
                                if (isActivated) {
                                    startActivity(new Intent(MainActivity.this, ScanActivity.class));
                                } else {
                                    Toast.makeText(MainActivity.this, "Activated failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
    }

    private String getSn() {
        // Config the SDK base info
        Sys sys = MyApp.sDriverManager.getBaseSysDevice();
        String[] pid = new String[1];
        int status = sys.getPid(pid);
        int count = 0;
        while (status != SdkResult.SDK_OK && count < 3) {
            count++;
            int sysPowerOn = sys.sysPowerOn();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            final int i = sys.sdkInit();
        }
        return pid[0];
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getFragmentManager().getBackStackEntryCount() <= 1) {
            if (actionBar != null) {
                actionBar.setTitle(getString(R.string.pref_settings));
                actionBar.setDisplayHomeAsUpEnabled(false);
            }
        }
    }
}
