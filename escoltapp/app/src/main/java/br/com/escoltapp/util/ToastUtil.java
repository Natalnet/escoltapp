package br.com.escoltapp.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    public static void show(Context context, String message) {
        show(context, message, Toast.LENGTH_LONG);
    }

    public static void show(Context context, String message, int duration) {
        Toast.makeText(context,message,duration).show();
    }

    public static void show(Context context, int resource) {
        show(context, resource, Toast.LENGTH_LONG);
    }

    public static void show(Context context, int resource, int duration) {
        Toast.makeText(context,context.getString(resource),duration).show();
    }
}
