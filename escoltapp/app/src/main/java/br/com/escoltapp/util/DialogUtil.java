package br.com.escoltapp.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import br.com.scoltapp.R;

public class DialogUtil {
    public static void showOkDialog(Activity activity, int sourceTitle, int sourceMessage, final OnDismissDialogListener onDismissDialogListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(sourceTitle));
        builder.setMessage(activity.getString(sourceMessage));
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(onDismissDialogListener != null) {
                    onDismissDialogListener.onDismissDialog();
                }
            }
        });
        builder.show();
    }

    public static void showYesNoDialog(Activity activity, String sourceTitle, String sourceMessage, final OnClickYesDialogListener onClickYesDialogListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(sourceTitle);
        builder.setMessage(sourceMessage);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(onClickYesDialogListener != null) {
                    onClickYesDialogListener.onClickYes();
                }
            }
        });

        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }



    public interface OnDismissDialogListener {
        void onDismissDialog();
    }

    public interface OnClickYesDialogListener {
        void onClickYes();
    }

}
