package teacherPackage;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;

import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.user.mainPages.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;

import adapters.teacherScheduledPupilAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import databaseClasses.Constants;
import databaseClasses.Schedule;
import databaseClasses.TimeScheduling;

public class teacherScheduledPupil  {

    public static  ArrayList<TimeScheduling> timesToStay;
    public static boolean isCheckAll;
    private String teacherId;
    public static ArrayList<TimeScheduling> timesScheduledByPupil;
    private RecyclerView timesPupilList;
    private Button removeAllCheckedAndUpd,updateWithoutRemoving;
    public static ArrayList<TimeScheduling> timesToRemove;
    private FirebaseFirestore db;
    private CheckBox selectAll;
    private SweetAlertDialog pDialog;
    public static TextView lessoNum;
    Context context;
    private Activity activity;

    interface addNewTimes{
        public void addTimes();
    }

    public void onCreate(final Context context, final Activity activity) {
        this.activity=activity;
        this.context = context;
        final Dialog mDialog = new Dialog(context);
        mDialog.setContentView(R.layout.teacher_scheduled_pupil);
        db = FirebaseFirestore.getInstance();
        timesToRemove = new ArrayList<>();
        timesToStay = new ArrayList<>();
        lessoNum = (TextView) mDialog.findViewById(R.id.lessonNum);
        lessoNum.setText(((Integer)teacherScheduling.timesHasBeenScheduled.size()).toString());
        selectAll = (CheckBox) mDialog.findViewById(R.id.removeAll);
        ImageButton exit=mDialog.findViewById(R.id.exit);
        removeAllCheckedAndUpd = mDialog.findViewById(R.id.removeCCheckAndUpdate);
        timesPupilList = mDialog.findViewById(R.id.timesList);
        teacherId = teacherPagesNavigator.teacherId;
        timesScheduledByPupil = teacherScheduling.timesHasBeenScheduled;
        timesPupilList.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        timesPupilList.setLayoutManager(mLayoutManager);
            final teacherScheduledPupilAdapter teacherScheduledPupilAdapter = new teacherScheduledPupilAdapter(context
                    , timesScheduledByPupil,teacherId);
            timesPupilList.setAdapter(teacherScheduledPupilAdapter);

        isCheckAll = false;
        selectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isCheckAll = true;
                    teacherScheduledPupilAdapter.notifyDataSetChanged();
                } else {
                    isCheckAll = false;
                    teacherScheduledPupilAdapter.notifyDataSetChanged();
                }
            }
        });

        removeAllCheckedAndUpd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeCheckedOnClick(new addNewTimes() {
                    int timesAdded;

                    @Override
                    public void addTimes() {
                        timesAdded = 0;
                        for (final TimeScheduling timeToAdd : teacherScheduling.totalTimesToAdd) {
                            Schedule scheduleToAdd = new Schedule();
                            scheduleToAdd.setScheduleDate(timeToAdd.getDate());
                            for(final String schoolId : teacherScheduling.schoolIdsToSchedule)
                            db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers)
                                    .document(teacherId).collection(Constants.schedules).document(timeToAdd.getDate().toString()).set(
                                    scheduleToAdd).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    timeToAdd.setSchoolUId(schoolId);
                                    db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(teacherId).collection(
                                            Constants.schedules).document(timeToAdd.getDate().toString()).collection(Constants.oneDayeSchedules).document(
                                            timeToAdd.getStartTime().toString()).set(timeToAdd).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                timesAdded++;
                                                if (timesAdded == teacherScheduling.totalTimesToAdd.size()*teacherScheduling.schoolIdsToSchedule.size()) {
                                                    pDialog.dismiss();
                                                    new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                                                            .setTitleText("Good job!")
                                                            .setContentText("הזמנים התעדכנו")
                                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                @Override
                                                                public void onClick(SweetAlertDialog sDialog) {
                                                                    sDialog.dismissWithAnimation();
                                                                    mDialog.dismiss();
                                                                    teacherScheduling.schoolIdsToSchedule=new ArrayList<>();
                                                                    teacherScheduling.totalTimesToAdd=new ArrayList<>();
                                                                    teacherScheduling.timesHasBeenScheduled=new ArrayList<>();

                                                                    teacherScheduling myFragment = new teacherScheduling();
                                                                    ((teacherPagesNavigator)(context)).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                                                                            , myFragment, Constants.tagTeacherScheduling).commit();


                                                                }
                                                            })
                                                            .show();
                                                }
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.show();
    }
    public void removeCheckedOnClick(final addNewTimes addTimeInstance){
        pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#00a5ff"));
        pDialog.setTitleText("טוען");
        pDialog.setCancelable(false);
        pDialog.show();
        if(timesToRemove.isEmpty()) {
            removeInterruptSchedules();
            pDialog.show();
            addTimeInstance.addTimes();

        }else
        {
            removeInterruptSchedules();
            for(final TimeScheduling time : timesToRemove){
                db.collection(Constants.drivingSchool).document(time.getSchoolUId()).collection(Constants.teachers).document(teacherId).collection(Constants.schedules)
                        .document(time.getDate().toString()).collection(Constants.oneDayeSchedules).document(time.getStartTime().toString()).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                db.collection(Constants.drivingSchool).document(time.getSchoolUId()).collection(Constants.teachers).document(teacherId).collection(Constants.schedules)
                                        .document(time.getDate().toString()).collection(Constants.oneDayeSchedules).get().addOnCompleteListener(
                                        new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                                    timesToRemove.remove(0);
                                                    if (timesToRemove.isEmpty() || timesToRemove == null) {
                                                        addTimeInstance.addTimes();
                                                    }
                                                } else {
                                                    db.collection(Constants.drivingSchool).document(time.getSchoolUId()).collection(Constants.teachers).document(teacherId).collection(Constants.schedules)
                                                            .document(time.getDate().toString()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            timesToRemove.remove(0);
                                                            if (timesToRemove.isEmpty() || timesToRemove == null) {
                                                                addTimeInstance.addTimes();
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                );
                            }
                        });
            }

        }
    }

    public void removeInterruptSchedules(){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        c.set(Calendar.HOUR_OF_DAY,23);
        c.set(Calendar.MINUTE,59);
        c.set(Calendar.SECOND,59);
        ArrayList<TimeScheduling> thisWeakTimes=new ArrayList<>();
        for (TimeScheduling time: teacherScheduling.totalTimesToAdd){
            if(time.getDate().before(c.getTime())){
                thisWeakTimes.add(time);
            }
        }

        for(TimeScheduling time1:timesToStay){
            for(TimeScheduling time2:thisWeakTimes){
               if((time1.getStartTime().before(time2.getEndTime())) &&
                        (time1.getEndTime().after(time2.getStartTime()))){
                            teacherScheduling.totalTimesToAdd.remove(time2);
                }
            }
        }
    }
}
