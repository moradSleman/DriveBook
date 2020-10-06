package Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import databaseClasses.Constants;
import databaseClasses.notification;

import static android.content.Context.NOTIFICATION_SERVICE;

public class LessonNotificationReceiver extends BroadcastReceiver {
    public static String NOTIFICATION_ID = "notification_id";
    public static String NOTIFICATION = "notification";
    public static String pupilId="pupilId";
    public static String teacherId="teacherId";
    public static String schoolId="schoolId";
    public static String timeSchedulingDate="date";
    public static String timeSchedulingStartTime="startTime";
    public static String notyTitle="notyTitle";
    public static String notyContent="notycontent";

    @Override
    public void onReceive(@NonNull final Context context,@NonNull Intent intent) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int notificationId = intent.getIntExtra(NOTIFICATION_ID, 0);
        String channelId = "Default";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(notificationId, notification);
       }
}
