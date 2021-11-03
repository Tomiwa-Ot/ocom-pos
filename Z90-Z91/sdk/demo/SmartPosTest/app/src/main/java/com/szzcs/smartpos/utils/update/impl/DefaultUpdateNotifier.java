/*
 * Copyright (C) 2017 Haoge
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.szzcs.smartpos.utils.update.impl;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

import com.szzcs.smartpos.utils.update.base.CheckNotifier;
import com.szzcs.smartpos.utils.update.util.SafeDialogHandle;

import java.util.Locale;

/**
 * 默认使用的在检查到有更新时的通知创建器：创建一个弹窗提示用户当前有新版本需要更新。
 *
 * @author haoge
 * @see CheckNotifier
 */
public class DefaultUpdateNotifier extends CheckNotifier {

    private static String mVersion = "Version: ";
    private static String mTitle = "Find new version";
    private static String mUpdate = "Update";
    private static String mIgnore = "Ignore";
    private static String mCancel = "Cancel";

    static {
        if (Locale.getDefault().toString().contains(Locale.CHINA.toString())) {
            mVersion = "版本号: ";
            mTitle = "你有新版本需要更新";
            mUpdate = "立即更新";
            mIgnore = "忽略此版本";
            mCancel = "取消";
        }
    }

    @Override
    public Dialog create(Activity activity) {
        StringBuilder content = new StringBuilder();
        if (update.getVersionName() != null) {
            content.append(mVersion + update.getVersionName() + "\n\n");
        }
        if (update.getUpdateContent() != null) {
            content.append(update.getUpdateContent());
        }
        String updateContent = content.toString();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setMessage(updateContent)
                .setTitle(mTitle)
                .setPositiveButton(mUpdate, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendDownloadRequest();
                        SafeDialogHandle.safeDismissDialog((Dialog) dialog);
                    }
                });
        if (update.isIgnore() && !update.isForced()) {
            builder.setNeutralButton(mIgnore, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sendUserIgnore();
                    SafeDialogHandle.safeDismissDialog((Dialog) dialog);
                }
            });
        }

        if (!update.isForced()) {
            builder.setNegativeButton(mCancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sendUserCancel();
                    SafeDialogHandle.safeDismissDialog((Dialog) dialog);
                }
            });
        }
        builder.setCancelable(false);
        return builder.create();
    }
}
