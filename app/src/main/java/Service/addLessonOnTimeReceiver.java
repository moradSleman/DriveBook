package Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

import androidx.annotation.NonNull;
import databaseClasses.Constants;
import databaseClasses.Teacher;
import databaseClasses.lessonPayment;

public class addLessonOnTimeReceiver extends BroadcastReceiver {
    public static String timeSchedulingEndTime="timeSchedulingEndTime";
    public static String schoolId= "schoolId";
    public static String teacherId="teacherId";
    public static String pupilId="pupilId";
    public static String lessonDate="lessonDate";
    public static String lessonStartingDate="lessonStartingDate";
    @Override
    public void onReceive(@NonNull final Context context, @NonNull Intent intent) {
        final Date date=new Date(intent.getLongExtra(lessonDate,0));
        final Date startingTime=new Date(intent.getLongExtra(lessonStartingDate,0));
        final Date endingTime=new Date(intent.getLongExtra(timeSchedulingEndTime,0));
        final String schoolUId=intent.getStringExtra(schoolId);
        final String teacherUId=intent.getStringExtra(teacherId);
        final String pupilUId=intent.getStringExtra(pupilId);


        final lessonPayment lessonPayment=new lessonPayment();
        FirebaseFirestore.getInstance().collection(Constants.drivingSchool).document(schoolUId).collection(Constants.teachers).document(teacherUId).collection(
                Constants.schedules).document(date.toString()).collection(Constants.oneDayeSchedules).document(startingTime.toString()).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful() && task.getResult().exists()){
                            if(task.getResult().get(Constants.timeSchedulingPupilUId).toString().equals(pupilUId)){
                                FirebaseFirestore.getInstance().collection(Constants.drivingSchool).document(schoolUId).collection(Constants.teachers).document(teacherUId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful() && task.getResult().exists()){
                                            Teacher teacher =task.getResult().toObject(Teacher.class);

                                            lessonPayment.setLessonCost(teacher.getLessonCost()==null?80:teacher.getLessonCost());
                                            lessonPayment.setLessonDate(date);
                                            lessonPayment.setLessonStartingDate(startingTime);
                                            lessonPayment.setLessonEndingDate(endingTime);
                                            lessonPayment.setLessonPaidSituation(false);
                                            FirebaseFirestore.getInstance().collection(Constants.drivingSchool).document(schoolUId).collection(Constants.teachers).document(teacherUId).collection(Constants.pupils)
                                                    .document(pupilUId).collection(Constants.lessons).document(lessonPayment.getLessonStartingDate().toString()).set(lessonPayment);

                                        }
                                    }
                                });
                            }
                        }
                    }
                }
        );

    }
}
