package adapters;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;

import Service.LessonNotificationReceiver;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.RingtoneManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.mainPages.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import databaseClasses.Constants;
import databaseClasses.Pupil;
import databaseClasses.TimeScheduling;
import pupilPackage.pupilCalendar;
import pupilPackage.pupilNotifications;

import static android.view.View.VISIBLE;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class pupilCalendarAdapter  extends RecyclerView.Adapter<pupilCalendarAdapter.AppViewHolder>{
    public List<TimeScheduling> timesListToRecycle;
    String schoolId;
    String teacherId;
    String pupilId;
    String pupilFullName;
    private FirebaseFirestore db;
    private Context context;
    String day;
    public pupilCalendarAdapter(Context context, ArrayList<TimeScheduling> timesToRecycle, String schoolId, String teacherId,String pupilId,
                                String pupilFullName,String day) {
        this.day=day;
        this.timesListToRecycle=timesToRecycle;
        this.schoolId=schoolId;
        this.teacherId=teacherId;
        this.pupilId=pupilId;
        this.pupilFullName=pupilFullName;
        this.context=context;
        db=FirebaseFirestore.getInstance();
    }

    public static class AppViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        CardView mCardView;
        TextView startTime;
        TextView endTime;
        TextView pupilName;
        TextView testOrLesson;
        ImageView cancelSchedule;
        ImageView makeSchedule;
        ImageView lockSchedule;
        CheckBox doubleLesson;

        public AppViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.scheduleCard);
            startTime = (TextView) itemView.findViewById(R.id.startTime);
            endTime = (TextView) itemView.findViewById(R.id.endTime);
            pupilName = (TextView) itemView.findViewById(R.id.pupilName);
            testOrLesson = (TextView) itemView.findViewById(R.id.lessonOrTest);
            cancelSchedule = (ImageView) itemView.findViewById(R.id.cancelSchedule);
            makeSchedule = (ImageView) itemView.findViewById(R.id.makeSchedule);
            lockSchedule = (ImageView) itemView.findViewById(R.id.locked);
            doubleLesson = (CheckBox) itemView.findViewById(R.id.doubleLessonCheck);
        }
    }
    @NonNull
    @Override
    public pupilCalendarAdapter.AppViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pupil_calendar_adapter_row, viewGroup, false);
        // set the view's size, margins, paddings and layout parameters
        pupilCalendarAdapter.AppViewHolder vh = new pupilCalendarAdapter.AppViewHolder(v);
        return vh;
    }
    @Override
    public void onBindViewHolder(@NonNull final pupilCalendarAdapter.AppViewHolder appViewHolder,int position1) {
        final int position=position1;
        final TimeScheduling currentTimeRow=timesListToRecycle.get(position);
        Calendar startCal=Calendar.getInstance();
        startCal.setTime(currentTimeRow.getStartTime());
        Integer startHour=startCal.get(Calendar.HOUR_OF_DAY);
        Integer startMin=startCal.get(Calendar.MINUTE);

        Calendar endCal=Calendar.getInstance();
        endCal.setTime(currentTimeRow.getEndTime());
        Integer lessonInterval=Integer.parseInt(((Long)(currentTimeRow.getEndTime().getTime()/60000-currentTimeRow.getStartTime().getTime()/60000)).toString());

        appViewHolder.startTime.setText(String.format(Constants.timePrintingFormat,startHour,startMin));
        appViewHolder.endTime.setText(lessonInterval+"דק'");

        db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(teacherId)
                .collection(Constants.schedules).document(currentTimeRow.getDate().toString()).collection(Constants.oneDayeSchedules).
                document(currentTimeRow.getStartTime().toString()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
//                String source = documentSnapshot != null && documentSnapshot.getMetadata().hasPendingWrites()
//                        ? "Local" : "Server";

                if (documentSnapshot != null && documentSnapshot.exists()) {
                        TimeScheduling timeUpdated = documentSnapshot.toObject(TimeScheduling.class);
                        if ((timeUpdated.getPupilUId() != null && timeUpdated.getPupilUId() != ""
                                ||timeUpdated.getSecondPupilUId() != null && timeUpdated.getSecondPupilUId()!="") && !timeUpdated.isTest()) {
                            if (timeUpdated.getPupilUId().equals(pupilId)) {
                                MelockVisibilityToSchedule(appViewHolder, position);
                            } else {
                                lockVisibilityToSchedule(appViewHolder, ""/*timeUpdated.getPupilFullName().toString()*/);
                            }
                        } else {
                            if (!timeUpdated.isTest()) {
                                setVisibilityToSchedule(appViewHolder, position, timeUpdated);
                            } else {
                                testlockVisibility(appViewHolder, currentTimeRow.getPupilUId().toString(), currentTimeRow.getPupilFullName().toString(), position);
                            }
                        }
                } else {
                    int i=0;
                    int rawPosition=0;
                    for(TimeScheduling time : pupilCalendar.timesToDaysScheduling.get(day)){
                        if(time.equals(currentTimeRow)){
                            rawPosition=i;
                        }
                        i++;
                    }
                    pupilCalendar.timesToDaysScheduling.get(day).remove(currentTimeRow);
                    notifyItemRemoved(rawPosition);
                    notifyItemRangeChanged(rawPosition, pupilCalendar.timesToDaysScheduling.get(day).size());

                }
            }
        });

        appViewHolder.cancelSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(teacherId).collection(Constants.schedules)
                        .document(currentTimeRow.getDate().toString()).collection(Constants.oneDayeSchedules).document(currentTimeRow.getStartTime().toString())
                        .update(Constants.timeSchedulingSchoolUId,null,Constants.timeSchedulingPupilUId,null,Constants.timeSchedulingPupilFullName,null);


                //cancel from other schools if exist
                db.collection(Constants.UIDS).document(teacherId).get().addOnCompleteListener(
                        new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful() && task.getResult().exists()){
                                    ArrayList<String> schoolsIdOfTeacher=(ArrayList<String>) task.getResult().get(Constants.schoolsUIDs);
                                    for(String anotherSchoolId : schoolsIdOfTeacher){
                                        db.collection(Constants.drivingSchool).document(anotherSchoolId).collection(Constants.teachers)
                                                .document(teacherId).collection(Constants.schedules).document(currentTimeRow.getDate().toString())
                                                .collection(Constants.oneDayeSchedules).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful() && !task.getResult().isEmpty()){
                                                    for(QueryDocumentSnapshot doc: task.getResult()){
                                                        TimeScheduling time=doc.toObject(TimeScheduling.class);
                                                        if(position+1<timesListToRecycle.size() && !time.getStartTime().after(timesListToRecycle.get(position+1).getEndTime()) && !time.getEndTime()
                                                                .before(timesListToRecycle.get(position+1).getStartTime()) && time.getPupilUId()!=null && time.getPupilUId().equals(
                                                                        pupilId)){
                                                            doc.getReference().update(Constants.timeSchedulingSchoolUId,null
                                                                    ,Constants.timeSchedulingPupilFullName,null,
                                                                    Constants.timeSchedulingPupilUId,null);
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        }
                );

                //cancel dynamically if not using alarm canceling
                db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(teacherId).collection(Constants.pupils)
                        .document(pupilId).collection(Constants.lessons).document(currentTimeRow.getStartTime().toString()).delete();
                cancelAlarm(currentTimeRow.getStartTime());
                cancelCreatingLesson(currentTimeRow.getEndTime());

            }
        });
        appViewHolder.makeSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    db.collection(Constants.UIDS).document(teacherId).get().addOnCompleteListener(
                            new OnCompleteListener<DocumentSnapshot>() {
                                private int counter;
                                private ArrayList<DocumentReference> docs=new ArrayList<>();
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful() && task.getResult().exists()){
                                        ArrayList<String> schoolsIdOfTeacher=(ArrayList<String>) task.getResult().get(Constants.schoolsUIDs);
                                        counter=schoolsIdOfTeacher.size();
                                        for(String anotherSchoolId : schoolsIdOfTeacher){
                                            db.collection(Constants.drivingSchool).document(anotherSchoolId).collection(Constants.teachers)
                                                    .document(teacherId).collection(Constants.schedules).document(currentTimeRow.getDate().toString())
                                                    .collection(Constants.oneDayeSchedules).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    counter--;
                                                    if(task.isSuccessful() && !task.getResult().isEmpty()){
                                                        for(QueryDocumentSnapshot doc: task.getResult()){
                                                            TimeScheduling time=doc.toObject(TimeScheduling.class);
                                                            //if double lesson check the next lesson time
                                                            if(appViewHolder.doubleLesson.getVisibility()==VISIBLE && appViewHolder.doubleLesson.isChecked()) {
                                                                if (position + 1 < timesListToRecycle.size() && time.getStartTime().before(timesListToRecycle.get(position + 1).getEndTime()) && time.getEndTime()
                                                                        .after(timesListToRecycle.get(position + 1).getStartTime()) ||
                                                                        time.getStartTime().before(timesListToRecycle.get(position).getEndTime()) && time.getEndTime()
                                                                                .after(timesListToRecycle.get(position).getStartTime())) {
                                                                    docs.add(doc.getReference());
                                                                }
                                                            }
                                                            //if is not double lesson check only the current lesson time
                                                            else{
                                                                if (time.getStartTime().before(timesListToRecycle.get(position).getEndTime()) && time.getEndTime()
                                                                                .after(timesListToRecycle.get(position).getStartTime())) {
                                                                    docs.add(doc.getReference());
                                                                }
                                                            }
                                                        }
                                                    }
                                                    if(counter==0){
                                                        DocumentReference secondcurrentRowTimeDoc=null;
                                                        DocumentReference currentRowTimeDoc=db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers)
                                                                .document(teacherId).collection(Constants.schedules).document(currentTimeRow.getDate().toString()).collection(
                                                                        Constants.oneDayeSchedules).document(currentTimeRow.getStartTime().toString());
                                                        //if double lesson time choosed
                                                        if(appViewHolder.doubleLesson.getVisibility()==VISIBLE && appViewHolder.doubleLesson.isChecked()) {
                                                            secondcurrentRowTimeDoc = db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers)
                                                                    .document(teacherId).collection(Constants.schedules).document(currentTimeRow.getDate().toString()).collection(
                                                                            Constants.oneDayeSchedules).document(timesListToRecycle.get(position + 1).getStartTime().toString());
                                                        }
                                                    docs.add(currentRowTimeDoc);
                                                        if(secondcurrentRowTimeDoc!=null)
                                                        {
                                                            docs.add(secondcurrentRowTimeDoc);
                                                        }
                                                        db.runTransaction(new Transaction.Function<Void>() {
                                                            @Override
                                                            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                                                                ArrayList<DocumentSnapshot> snaps=new ArrayList<>();
                                                                for(DocumentReference doc:docs){
                                                                    DocumentSnapshot snapshot = transaction.get(doc);
                                                                    snaps.add(snapshot);
                                                                }
                                                                for(DocumentSnapshot docSnap:snaps){
                                                                    if(docSnap.get(Constants.timeSchedulingPupilUId)!=null ||
                                                                            docSnap.get(Constants.timeSchedulingSecondPupilUId)!=null)
                                                                    {
                                                                        return null;
                                                                    }
                                                                }
                                                                for(DocumentReference doc: docs){
                                                                    transaction.update(doc, Constants.timeSchedulingPupilUId, pupilId,
                                                                            Constants.timeSchedulingPupilFullName,pupilFullName,Constants.timeSchedulingSchoolUId
                                                                    ,schoolId);
                                                                }

                                                                // Success
                                                                return null;
                                                            }
                                                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d(TAG, "Transaction success!");
                                                                createAlarm(currentTimeRow);
                                                            }
                                                        })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Log.w(TAG, "Transaction failure.", e);
                                                                    }
                                                                });

                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                    );

            }
        });
    }
    public void testlockVisibility(@NonNull final pupilCalendarAdapter.AppViewHolder appViewHolder,String pupilUId,String pupilFullName,int position) {
        appViewHolder.testOrLesson.setText("TEST");
        if(pupilUId.equals(pupilId))
        {
            appViewHolder.pupilName.setText("אני");
        }else{
            appViewHolder.pupilName.setText(pupilFullName);
        }
        appViewHolder.cancelSchedule.setVisibility(View.GONE);
        appViewHolder.doubleLesson.setVisibility(View.INVISIBLE);
        appViewHolder.makeSchedule.setVisibility(View.GONE);
        appViewHolder.lockSchedule.setVisibility(View.VISIBLE);
    }

    public void MelockVisibilityToSchedule(@NonNull final pupilCalendarAdapter.AppViewHolder appViewHolder,final int position) {
        appViewHolder.pupilName.setText("אני");
        appViewHolder.cancelSchedule.setVisibility(View.VISIBLE);
        appViewHolder.doubleLesson.setVisibility(View.INVISIBLE);
        appViewHolder.makeSchedule.setVisibility(View.GONE);
        appViewHolder.lockSchedule.setVisibility(View.GONE);
    }
    public void lockVisibilityToSchedule(@NonNull final pupilCalendarAdapter.AppViewHolder appViewHolder,String pupilFullName) {
        appViewHolder.pupilName.setText(pupilFullName);
        appViewHolder.cancelSchedule.setVisibility(View.GONE);
        appViewHolder.doubleLesson.setVisibility(View.INVISIBLE);
        appViewHolder.makeSchedule.setVisibility(View.GONE);
        appViewHolder.lockSchedule.setVisibility(View.VISIBLE);
    }

    public void setVisibilityToSchedule(@NonNull final pupilCalendarAdapter.AppViewHolder appViewHolder,final int position,TimeScheduling currentTime){
        Calendar cal=Calendar.getInstance();
        Date currentDateTime=cal.getTime();
    /*    if(currentTime.getStartTime().getTime()/60000-currentDateTime.getTime()/60000<=Constants.hoursBeforLockScheduling*60){
            appViewHolder.pupilName.setText("זמן הרשמה עבר");
            appViewHolder.cancelSchedule.setVisibility(View.GONE);
            appViewHolder.doubleLesson.setVisibility(View.INVISIBLE);
            appViewHolder.makeSchedule.setVisibility(View.GONE);
            appViewHolder.lockSchedule.setVisibility(View.VISIBLE);
        }
        else
        {*/
            appViewHolder.pupilName.setText("לא משובץ");
            appViewHolder.cancelSchedule.setVisibility(View.GONE);
            appViewHolder.doubleLesson.setVisibility(View.INVISIBLE);
            appViewHolder.makeSchedule.setVisibility(View.VISIBLE);
            appViewHolder.lockSchedule.setVisibility(View.GONE);

            if(timesListToRecycle.size()-1>=position+1){
                TimeScheduling secondTime=timesListToRecycle.get(position+1);
                if(currentTime.getEndTime().getTime()/60000-secondTime.getStartTime().getTime()/60000==0){
                    if(secondTime.getPupilUId()!=null && secondTime.getPupilUId()!=""){
                        appViewHolder.doubleLesson.setVisibility(View.INVISIBLE);
                    }else{
                        if((timesListToRecycle.get(position).getPupilUId()==null || timesListToRecycle.get(position).getPupilUId()=="")
                                && (timesListToRecycle.get(position).getPupilFullName()==null || timesListToRecycle.get(position).getPupilFullName()==""))
                        appViewHolder.doubleLesson.setVisibility(View.VISIBLE);
                    }
                    db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(teacherId)
                            .collection(Constants.schedules).document(secondTime.getDate().toString()).collection(Constants.oneDayeSchedules).
                            document(secondTime.getStartTime().toString()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                return;
                            }
                            String source = documentSnapshot != null && documentSnapshot.getMetadata().hasPendingWrites()
                                    ? "Local" : "Server";

                            if (documentSnapshot != null && documentSnapshot.exists()) {
                                TimeScheduling timeUpdated=documentSnapshot.toObject(TimeScheduling.class);
                                if(timeUpdated.getPupilFullName()!=null && timeUpdated.getPupilFullName()!=""
                                        && timeUpdated.getPupilUId()!=null && timeUpdated.getPupilUId()!="") {
                                        appViewHolder.doubleLesson.setVisibility(View.INVISIBLE);

                                }else{
                                    appViewHolder.doubleLesson.setVisibility(View.VISIBLE);
                                }
                            } else {
                            }
                        }
                    });


                }else
                {
                    appViewHolder.doubleLesson.setVisibility(View.INVISIBLE);
                }
            }else{
                appViewHolder.doubleLesson.setVisibility(View.INVISIBLE);
            }
        }
   // }

    @Override
    public int getItemCount() {
        if(timesListToRecycle==null){
            return 0;
        }else
        {
            return timesListToRecycle.size();
        }
    }



    private void cancelCreatingLesson(Date endTime){
        Intent updatingIntent = new Intent(this.context, LessonNotificationReceiver.class);
        updatingIntent.setAction("lesson"+endTime.toString());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 0, updatingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) this.context.getSystemService(this.context.ALARM_SERVICE);

        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();

    }
    private final void createAlarm(@NonNull TimeScheduling timeStart) {
        String channelId = "Default";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.context,channelId)
                .setContentTitle("התראת שיעור קרוב")
                .setContentText("שיעור קרוב מתקיים בעוד"+ Constants.minutsRemindBeforLesson)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.logoapp)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        Intent intent = new Intent(this.context, pupilNotifications.class);
        PendingIntent activity = PendingIntent.getActivity(this.context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(activity);

        Notification notification = builder.build();


        Intent notificationIntent = new Intent(this.context, LessonNotificationReceiver.class);
        notificationIntent.setAction("noty"+timeStart.getStartTime().toString());
        notificationIntent.putExtra(LessonNotificationReceiver.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(LessonNotificationReceiver.NOTIFICATION, notification);
        notificationIntent.putExtra(LessonNotificationReceiver.schoolId,schoolId);
        notificationIntent.putExtra(LessonNotificationReceiver.teacherId,teacherId);
        notificationIntent.putExtra(LessonNotificationReceiver.pupilId,pupilId);
        notificationIntent.putExtra(LessonNotificationReceiver.timeSchedulingDate,timeStart.getDate().getTime());
        notificationIntent.putExtra(LessonNotificationReceiver.timeSchedulingStartTime,timeStart.getStartTime().getTime());
        notificationIntent.putExtra(LessonNotificationReceiver.notyTitle,"התראת שיעור קרוב");
        notificationIntent.putExtra(LessonNotificationReceiver.notyContent,"שיעור קרוב מתקיים בעוד"+ Constants.minutsRemindBeforLesson);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        final Calendar calScheduled=Calendar.getInstance();
        calScheduled.setTime(timeStart.getStartTime());
        db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(teacherId).collection(Constants.pupils)
                .document(pupilId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Pupil pupil=documentSnapshot.toObject(Pupil.class);
                if(pupil.isNotfyBeforeLesson()) {
                    if (pupil.getTimeAlertBeforeLesson()!=null) {
                        calScheduled.add(Calendar.MINUTE, pupil.getTimeAlertBeforeLesson() * (-1));
                        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calScheduled.getTimeInMillis(), pendingIntent);
                    } else {
                        calScheduled.add(Calendar.MINUTE, Constants.minutsRemindBeforLesson);
                        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calScheduled.getTimeInMillis(), pendingIntent);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "failed to get pupil document: ",e);
            }
        });

    }

    public void cancelAlarm(@NonNull Date startingtime){
        Intent myIntent = new Intent(this.context, LessonNotificationReceiver.class);
        myIntent.setAction("noty"+startingtime.toString());
        AlarmManager alarmManager = (AlarmManager) this.context.getSystemService(this.context.ALARM_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getService(this.context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }
}
