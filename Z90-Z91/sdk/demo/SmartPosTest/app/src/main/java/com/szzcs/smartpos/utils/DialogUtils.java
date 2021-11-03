package com.szzcs.smartpos.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by yyzz on 2018/5/18.
 */

public class DialogUtils {

    private DialogUtils() {
    }

    private static class Holder {
        private static DialogUtils INSTANCE = new DialogUtils();
    }

    public static DialogUtils getInstace() {
        return Holder.INSTANCE;
    }

    public static void destoryDialog() {
    }

    public static Dialog showProgress(Context context, String title, String message) {
        return ProgressDialog.show(context, title, message);
    }

    public static Dialog showProgress(Context context, String title, String message, DialogInterface.OnCancelListener cancelListener) {
        return ProgressDialog.show(context, title, message, false, true, cancelListener);
    }

    public static Dialog showViewDialog(Context context, View view, String title, String message, String confirmButton, String cancelButton, DialogInterface.OnClickListener confirmListener, DialogInterface.OnClickListener cancelListener) {
        return show(context, title, message, view, true, confirmButton, confirmListener, null, null, cancelButton, cancelListener, null, null, null);
    }

    public static Dialog show(Context context, String message) {
        return show(context, null, message, null, true, "OK", null, null, null, null, null, null, null, null);
    }

    public static Dialog show(Context context, String title, String message) {
        return show(context, title, message, null, true, "OK", null, null, null, null, null, null, null, null);
    }

    public static Dialog show(Context context, String title, String message, String confirmButton) {
        return show(context, title, message, null, true, confirmButton, null, null, null, null, null, null, null, null);
    }

    public static Dialog show(Context context, String title, String message, String confirmButton, String cancelButton, DialogInterface.OnClickListener confirmListener, DialogInterface.OnClickListener cancelListener) {
        return show(context, title, message, null, true, confirmButton, confirmListener, null, null, cancelButton, cancelListener, null, null, null);
    }

    public static Dialog show(Context context, String title, String message, String confirmButton, DialogInterface.OnClickListener confirmListener, DialogInterface.OnCancelListener onCancelListener) {
        return show(context, title, message, null, true, confirmButton, confirmListener, null, null, null, null, null, onCancelListener, null);
    }

    public static Dialog show(Context context, String title, String message, View view, boolean cancelable,
                              String confirmButton, DialogInterface.OnClickListener confirmListener,
                              String centerButton, DialogInterface.OnClickListener centerListener,
                              String cancelButton, DialogInterface.OnClickListener cancelListener,
                              DialogInterface.OnShowListener onShowListener,
                              DialogInterface.OnCancelListener onCancelListener,
                              DialogInterface.OnDismissListener onDismissListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context).setCancelable(cancelable);

        if (title != null) {
            builder.setTitle(title);
        }
        if (message != null) {
            builder.setMessage(message);
        }
        if (confirmButton != null) {
            builder.setPositiveButton(confirmButton, confirmListener);
        }
        if (centerButton != null) {
            builder.setNeutralButton(centerButton, centerListener);
        }
        if (cancelButton != null) {
            builder.setNegativeButton(cancelButton, cancelListener);
        }
        if (cancelable) {
            builder.setOnCancelListener(onCancelListener);
        }
        if (view != null) {
            builder.setView(view);
        }
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(onShowListener);
        alertDialog.setOnDismissListener(onDismissListener);
        if (!(context instanceof Activity)) {
            if (alertDialog.getWindow() != null) {
                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            }
        }
        alertDialog.show();
        return alertDialog;
    }

    public static void hintKeyBoard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && view != null) {
            if (view.getWindowToken() != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    public static void showKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
