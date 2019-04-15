package br.com.escoltapp.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import java.util.List;

public class NotificationUtil {

    public static Builder Builder(Context context) {
        return new Builder(context);
    }

    public static void notify(Context context, Notification notification) {
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(2, notification);

    }

    public static class Builder {
        private Context context;
        private Integer icon;
        private String title;
        private String content;
        private String sound;
        private Integer visibility;
        private Integer maxProgress;
        private Integer progress;
        private Boolean undefinedProgress;
        private Class resultActivity;
        private Integer insistent;
        private Boolean headsUpNotification;
        private Boolean ongoing;
        private List<NotificationCompat.Action> actions;

        Builder(@NonNull Context context) {
            this.context = context;

            icon = null;
            title = null;
            content = null;
            sound = null;
            visibility = null;
            maxProgress = null;
            progress = null;
            undefinedProgress = null;
            resultActivity = null;
            insistent = null;
            headsUpNotification = null;
            actions = null;
        }

        public Builder setIcon(@NonNull Integer icon) {
            this.icon = icon;
            return this;
        }

        public Builder setTitle(@NonNull String title) {
            this.title = title;
            return this;
        }

        public Builder setContent(@NonNull String content) {
            this.content = content;
            return this;
        }

        public Builder setSound(@NonNull String sound) {
            this.sound = sound;
            return this;
        }

        public Builder setVisibility(@NonNull Integer visibility) {
            this.visibility = visibility;
            return this;
        }

        public Builder setProgress(@NonNull Integer maxProgress, @NonNull Integer progress, @NonNull Boolean undefinedProgress) {
            this.maxProgress = maxProgress;
            this.progress = progress;
            this.undefinedProgress = undefinedProgress;
            return this;
        }

        public Builder setResultActivity(@NonNull Class resultActivity) {
            this.resultActivity = resultActivity;
            return this;
        }

        public Builder setInsistent(@NonNull Integer insistent) {
            this.insistent = insistent;
            return this;
        }

        public Builder setHeadsUpNotification(Boolean headsUpNotification) {
            this.headsUpNotification = headsUpNotification;
            return this;
        }

        public Builder setOngoing(Boolean ongoing) {
            this.ongoing = ongoing;
            return this;
        }

        public Builder setActions(List<NotificationCompat.Action> actions) {
            this.actions = actions;
            return this;
        }

        public Notification build() {
            NotificationCompat.Builder mBuilder;

            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O ) {
                mBuilder = new NotificationCompat.Builder(context);
            } else {
                mBuilder = new NotificationCompat.Builder(context, "0");
            }

            if (icon != null) {
                mBuilder.setSmallIcon(icon);
            }
            if (title != null) {
                mBuilder.setContentTitle(title);
            }
            if (content != null) {
                mBuilder.setContentText(content);
                mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(content));
            }
            if (sound != null) {
                mBuilder.setSound(Uri.parse("android.resource://"+ context.getPackageName()+"/raw/" + sound));
            }
            if (visibility != null) {
                mBuilder.setVisibility(visibility);
            }
            if (maxProgress != null && progress != null && undefinedProgress != null) {
                mBuilder.setProgress(maxProgress,progress,undefinedProgress);
            }
            if (resultActivity != null) {
                Intent resultIntent = new Intent(context, resultActivity);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addParentStack(resultActivity);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                mBuilder.setContentIntent(resultPendingIntent);
            }

            if (headsUpNotification != null && headsUpNotification) {
                PendingIntent p = PendingIntent.getService(context, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setFullScreenIntent(p, false);
            }

            if (ongoing != null) {
                mBuilder.setOngoing(ongoing);
            }

            if (actions != null) {
                for(NotificationCompat.Action action: actions) {
                    mBuilder.addAction(action);
                }
            }

            Notification notification = mBuilder.build();

            if (insistent != null){
                notification.flags |= insistent;
            }

            return notification;
        }


    }
}
