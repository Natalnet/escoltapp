package br.com.escoltapp.androidservices;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.escoltapp.activities.MainActivity;
import br.com.escoltapp.entities.User;
import br.com.escoltapp.entityservices.MessageService;
import br.com.escoltapp.util.NotificationUtil;
import br.com.escoltapp.util.ToastUtil;
import br.com.scoltapp.R;

public class GetInHomeService extends Service {
    //private static final double THIRTY_METERS = 30;

    private Location userHomeLocation;
    private CountDownTimer countDownTimer;
    private User user;
    private User.Place place;

    public GetInHomeService() {}

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getBooleanExtra("cancel",false)) {
            countDownTimer.cancel();
            stopSelf();
            return START_NOT_STICKY;
        }

        user = intent.getParcelableExtra("user");
        place = intent.getParcelableExtra("place");

        userHomeLocation = new Location(LocationManager.GPS_PROVIDER);
        userHomeLocation.setLatitude(place.latitude);
        userHomeLocation.setLongitude(place.longitude);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, createLocationListener());

        final int maxTime = 10000;
        final int tick = 1000;

        List<NotificationCompat.Action> actions = new ArrayList<>();

        Intent cancelNotificationIntent = new Intent(this,GetInHomeService.class);
        cancelNotificationIntent.putExtra("cancel",true);
        PendingIntent cancelNotificationPedingIntent = PendingIntent.getService(this, 0, cancelNotificationIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        actions.add(new NotificationCompat.Action(R.drawable.ic_clear_black_24dp,getString(R.string.cancel),cancelNotificationPedingIntent));

        final NotificationUtil.Builder builder = NotificationUtil.Builder(this)
                .setIcon(R.drawable.ic_alarm_black_24dp)
                .setTitle(getString(R.string.app_name))
                .setContent(String.format(getString(R.string.time_to_get_in_home),2,0))
                .setResultActivity(MainActivity.class)
                .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                .setProgress(maxTime,maxTime,false)
                .setActions(actions);

        startForeground(1, builder.build());

        countDownTimer = new CountDownTimer(maxTime, tick) {

            public void onTick(long millisUntilFinished) {
                long minutes = (millisUntilFinished/tick)/60;
                long seconds = (millisUntilFinished/tick)%60;
                builder.setContent(String.format(getString(R.string.time_to_get_in_home),minutes,seconds))
                        .setProgress(maxTime,(int)millisUntilFinished,false);
                startForeground(1, builder.build());
            }

            public void onFinish() {
                MessageService.sendDangerNotification(getBaseContext(), user, new MessageService.OnCompleteListener() {
                    @Override
                    public void onSuccess() {
                        ToastUtil.show(getBaseContext(),R.string.danger_message_sent, Toast.LENGTH_SHORT);
                        stopSelf();
                    }

                    @Override
                    public void onFailure() {
                        ToastUtil.show(getBaseContext(),R.string.could_not_send_danger_message);
                        stopSelf();
                    }
                });
            }
        };
        countDownTimer.start();

        return START_STICKY;
    }

    private LocationListener createLocationListener() {
        return new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                /*if (location.distanceTo(userHomeLocation) > THIRTY_METERS) {
                    countDownTimer.cancel();
                    stopSelf();
                }*/
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };
    }


}
