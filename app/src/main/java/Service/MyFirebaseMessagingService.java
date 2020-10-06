package Service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.user.mainPages.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import databaseClasses.Constants;
import databaseClasses.tokenLoginDetails;
import pupilPackage.pupilCalendar;
import pupilPackage.pupilNotifications;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessagingServ";
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    private void sendRegistrationToServer(String refreshedToken) {
        Log.d("TOKEN ", refreshedToken.toString());
        tokenLoginDetails newToken=new tokenLoginDetails();
        newToken.setTokenId(refreshedToken);
        newToken.setAutoLogin(false);
        db.collection(Constants.tokens).document(refreshedToken).set(newToken).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        });
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getNotification()!=null) {
            Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Log.d("msg", "onMessageReceived: " + remoteMessage.getData().get("message"));
            String channelId = "Default";
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.logoapp)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
                manager.createNotificationChannel(channel);
            }
            Intent intent = new Intent(this, pupilNotifications.class);
            PendingIntent activity = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(activity);
            manager.notify(0, builder.build());
        }
        else{
            if(remoteMessage.getData().size()>0){
                if(remoteMessage.getData().get("isScheduled")!=null){
                    Boolean isScheduled=Boolean.parseBoolean(remoteMessage.getData().get("isScheduled"));
                    if(isScheduled){
                        String channelId = "Default";
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,channelId)
                                .setContentTitle("התראת שיעור קרוב")
                                .setContentText("שיעור קרוב מתקיים בעוד"+ Constants.minutsRemindBeforLesson)
                                .setAutoCancel(true)
                                .setSmallIcon(R.drawable.logoapp)
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                        Integer notyCode=Integer.parseInt(remoteMessage.getData().get("notyCode"));
                        Intent intent = new Intent(this, pupilCalendar.class);
                        PendingIntent activity = PendingIntent.getActivity(this, notyCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                        builder.setContentIntent(activity);

                        Notification notification = builder.build();

                        Intent notificationIntent = new Intent(this, LessonNotificationReceiver.class);
                        notificationIntent.putExtra(LessonNotificationReceiver.NOTIFICATION_ID, notyCode);
                        notificationIntent.putExtra(LessonNotificationReceiver.NOTIFICATION, notification);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, notyCode, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                        DateFormat scheduledTime = new SimpleDateFormat("dd-MM-yyyy H:mm");
                        Date scheduleDate= null ;
                        try {
                            scheduleDate = scheduledTime.parse(remoteMessage.getData().get("lessonTime"));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Calendar calScheduled=Calendar.getInstance();
                        calScheduled.setTime(scheduleDate);
                        calScheduled.add(Calendar.MINUTE, Constants.minutsRemindBeforLesson);
                        Date date=calScheduled.getTime();
                        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calScheduled.getTimeInMillis(), pendingIntent);

                    }
                }
            }
        }
    }

}
