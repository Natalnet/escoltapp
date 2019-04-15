package br.com.escoltapp.entityservices;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.List;

import br.com.escoltapp.entities.User;
import br.com.escoltapp.util.HttpUtil;
import br.com.scoltapp.BuildConfig;

public class MessageService {
    public static void sendDangerNotification(Context context, User user, @NonNull final OnCompleteListener onCompleteListener) {
        String url = BuildConfig.API_URL + String.format("/messaging/danger/%s/%s/%s/%s",user.getId(),user.getPhone(),user.getFirstName(),user.getLastName());
        HttpUtil.GET(context, url, new HttpUtil.OnHttpResponseListener() {
            @Override
            public void onResponse(JSONObject object) {
                onCompleteListener.onSuccess();
            }

            @Override
            public void onError(String message) {
                onCompleteListener.onFailure();
            }
        });
    }

    public interface OnCompleteListener {
        void onSuccess();
        void onFailure();
    }
}
