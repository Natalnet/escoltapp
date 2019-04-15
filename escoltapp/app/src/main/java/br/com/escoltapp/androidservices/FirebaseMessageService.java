package br.com.escoltapp.androidservices;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Action;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;

import br.com.escoltapp.util.NotificationUtil;
import br.com.scoltapp.R;

public class FirebaseMessageService extends FirebaseMessagingService {
    public FirebaseMessageService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {

            String firstname = remoteMessage.getData().get("firstname");
            String lastname = remoteMessage.getData().get("lastname");
            String phone = remoteMessage.getData().get("phone");

            Intent callUserIntent = new Intent(Intent.ACTION_CALL);
            callUserIntent.setData(Uri.parse("tel: " + phone));
            PendingIntent callUserPendingIntent = PendingIntent.getActivity(this, 0, callUserIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent callPoliceIntent = new Intent(Intent.ACTION_CALL);
            callPoliceIntent.setData(Uri.parse("tel: 190"));
            PendingIntent callPolicePendingIntent = PendingIntent.getActivity(this, 0, callPoliceIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            List<Action> actions = new ArrayList<>();
            actions.add(new Action(R.drawable.ic_call_black_24dp,String.format("%s", firstname),callUserPendingIntent));
            actions.add(new Action(R.drawable.ic_call_black_24dp,getString(R.string.police),callPolicePendingIntent));

            NotificationUtil.Builder builder = NotificationUtil.Builder(this)
                    .setIcon(R.drawable.ic_warning_black_24dp)
                    .setTitle(getString(R.string.app_name))
                    .setContent(String.format(getString(R.string.warning_message_content),String.format("%s %s", firstname, lastname)))
                    .setSound("warning")
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setInsistent(NotificationCompat.FLAG_INSISTENT)
                    .setHeadsUpNotification(true)
                    .setActions(actions);
            NotificationUtil.notify(this,builder.build());
        }
    }

}
