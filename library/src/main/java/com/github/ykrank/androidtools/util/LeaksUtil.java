package com.github.ykrank.androidtools.util;

import android.app.Application;
import android.app.Dialog;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

public class LeaksUtil {

    public static void install(Application application) {

    }

    /**
     * Dialog memory leaks because of Message bug
     * Must call in onDestroy() in DialogFragment because DismissListener call in onDestroyView()
     *
     * @param dialog
     */
    public static void clearDialogLeaks(@NonNull Dialog dialog) {
        dialog.setOnCancelListener(null);
        dialog.setOnDismissListener(null);
        dialog.setOnShowListener(null);
        if (dialog instanceof AlertDialog) {
            AlertDialog alertDialog = (AlertDialog) dialog;
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, null, null, null);
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, null, null, null);
            alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, null, null, null);
        }
    }
}
