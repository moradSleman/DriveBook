package pupilPackage;

import android.content.Context;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import adapters.pupilLessonAdapter;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import databaseClasses.Constants;
import databaseClasses.Schedule;
import databaseClasses.TimeScheduling;
import databaseClasses.lessonPayment;
import databaseClasses.sqlLitDbHelper;
import teacherPackage.teacherHomePage;

public class pupilLesson extends AppCompatActivity {

    private FirebaseFirestore db;
    private String schoolId;
    private String teacherId;
    private String pupilId;
    private ArrayList<lessonPayment> lessonsPaymentData;
    private sqlLitDbHelper myLocalDb;
    private Context context;
    private RecyclerView mRecyclerView;
    private TextView nearestLessonDate;
    private TextView nearestLessonTime;
    private TextView lessonNum;
    private boolean isPupilPage;
    private boolean found=false;
    private TextView nearestLessonText;

    public void onCreate(Context context,String schoolId,String teacherId,String pupilId){
        this.context=context;
        db=FirebaseFirestore.getInstance();
        if(schoolId==null && teacherId==null && pupilId==null) {
            this.schoolId = pupilPagesNavigator.schoolId;
            this.teacherId = pupilPagesNavigator.teacherId;
            this.pupilId = pupilPagesNavigator.pupilId;
            mRecyclerView=pupilHomePage.lessonRecyclerView;
            lessonNum=pupilHomePage.lessonNum;
            nearestLessonDate=pupilHomePage.nearLessonDate;
            nearestLessonTime=pupilHomePage.nearLessonTime;
            isPupilPage=true;
        }else{
            this.schoolId=schoolId;
            this.teacherId=teacherId;
            this.pupilId=pupilId;
            mRecyclerView= teacherHomePage.pupilLessonsRecycler;
            lessonNum=teacherHomePage.lessonNumDown;
            nearestLessonTime=teacherHomePage.nearestPupilLesssonTime;
            nearestLessonDate=teacherHomePage.nearestPupilLessonDate;
            nearestLessonText=teacherHomePage.nearestLessonText;
            isPupilPage=false;
        }
        lessonsPaymentData=new ArrayList<>();
        myLocalDb=new sqlLitDbHelper(context);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        setData();
    }


    private void setData() {
        Calendar cal=Calendar.getInstance();
        final Date nowTimeDate=cal.getTime();
        if(sqlLitDbHelper.isConnected(context)) {
            if(pupilId!=null)
            db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(teacherId).collection(Constants.pupils)
                    .document(pupilId).collection(Constants.lessons).orderBy(Constants.lessonDate, Query.Direction.DESCENDING).get().addOnCompleteListener(
                    new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                lessonNum.setText(((Integer)task.getResult().size()).toString());
                                Date lastDateTime=new Date();
                                for (int i=0;i<task.getResult().size();i++) {
                                    QueryDocumentSnapshot doc=(QueryDocumentSnapshot) task.getResult().getDocuments().get(i);
                                    lessonPayment lesPayToAdd = doc.toObject(lessonPayment.class);
                                    lessonsPaymentData.add(lesPayToAdd);
                                }
                                pupilLessonAdapter recyclerAdapter = new pupilLessonAdapter(context,lessonsPaymentData);
                                mRecyclerView.setAdapter(recyclerAdapter);
                            }
                        }
                    });
            if(isPupilPage) {
                Calendar toDayn = Calendar.getInstance();
                toDayn.add(Calendar.DAY_OF_MONTH,-2);
                toDayn.set(Calendar.HOUR, 23);
                toDayn.set(Calendar.MINUTE, 59);
                toDayn.set(Calendar.SECOND, 59);

                final Date toDay=toDayn.getTime();
                db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(teacherId).collection(Constants.schedules)
                        .whereGreaterThanOrEqualTo(Constants.scheduleDate, toDay).orderBy(Constants.scheduleDate, Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            Calendar endOfWeek = Calendar.getInstance();
                            endOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                Schedule sch = doc.toObject(Schedule.class);
                                if (sch.getScheduleDate().after(endOfWeek.getTime())) {
                                    break;
                                } else {
                                    db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(teacherId).collection(Constants.schedules).
                                            document(sch.getScheduleDate().toString()).collection(Constants.oneDayeSchedules).orderBy(Constants.timeSchedulingStartTime, Query.Direction.ASCENDING)
                                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                                    TimeScheduling time = doc.toObject(TimeScheduling.class);
                                                    if (time.getStartTime().after(Calendar.getInstance().getTime()) && time.getPupilUId() != null && time.getPupilUId().equals(pupilId) && !time.isTest() && !found) {
                                                        setnearLessonDateTime(time.getStartTime());
                                                        found = true;
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                });
            }else{
                if(pupilId!=null)
                db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(teacherId).collection(Constants.pupils)
                        .document(pupilId).collection(Constants.lessons).orderBy(Constants.lessonStartingDate, Query.Direction.DESCENDING).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful() && !task.getResult().isEmpty()){
                                    lessonPayment lesson=new lessonPayment();
                                    for(QueryDocumentSnapshot doc : task.getResult()){
                                        lesson=doc.toObject(lessonPayment.class);
                                        if (lesson.getLessonStartingDate().after(Calendar.getInstance().getTime())){
                                            continue;
                                        }
                                        break;
                                    }
                                    setLastLesson(lesson.getLessonStartingDate());
                                }
                            }
                        });
            }
        }
        else {
            if (isPupilPage) {
                lessonsPaymentData = myLocalDb.readLessonPayFromSqlLite();
                for (int i = 0; i < lessonsPaymentData.size(); i++) {
                    lessonPayment lesPayToAdd = lessonsPaymentData.get(i);
                    if (i > 0) {
                        lessonPayment lastlesPayToAdd = lessonsPaymentData.get(i - 1);
                        Date lastDateTime = lastlesPayToAdd.getLessonStartingDate();
                        if (nowTimeDate.before(lastDateTime) && nowTimeDate.after(lesPayToAdd.getLessonStartingDate())) {
                            setnearLessonDateTime(lastlesPayToAdd.getLessonStartingDate());
                        } else {
                            setnearLessonDateTime(null);
                        }
                    } else {
                        if (lessonsPaymentData.size() == 1) {
                            if (nowTimeDate.before(lesPayToAdd.getLessonStartingDate())) {
                                setnearLessonDateTime(lesPayToAdd.getLessonStartingDate());
                            } else {
                                setnearLessonDateTime(null);
                            }
                        }
                    }
                }
                lessonNum.setText(((Integer) lessonsPaymentData.size()).toString());
                pupilLessonAdapter recyclerAdapter = new pupilLessonAdapter(context, lessonsPaymentData);
                mRecyclerView.setAdapter(recyclerAdapter);
            }
        }

    }

    private void setLastLesson(Date lessonStartingDate) {
        if(lessonStartingDate==null){
            nearestLessonDate.setText("אין");
            nearestLessonTime.setText("");
            return;
        }
        Calendar startCal=Calendar.getInstance();
        startCal.setTime(lessonStartingDate);
        Integer startHour=startCal.get(Calendar.HOUR_OF_DAY);
        Integer startMin=startCal.get(Calendar.MINUTE);
        Integer startDay=startCal.get(Calendar.DATE);
        Integer startMonth=startCal.get(Calendar.MONTH)+1;
        Integer startYear=startCal.get(Calendar.YEAR);

        nearestLessonTime.setText(String.format(Constants.timePrintingFormat,startHour,startMin));
        nearestLessonDate.setText(String.format(Constants.datePrintingFormat,startDay,startMonth,startYear));
        nearestLessonText.setText("שיעור אחרון התקיים ב");
    }

    private void setnearLessonDateTime(Date startingTime) {
        if(startingTime==null){
            nearestLessonDate.setText("אין");
            nearestLessonTime.setText("");
            return;
        }
        Calendar startCal=Calendar.getInstance();
        startCal.setTime(startingTime);
        Integer startHour=startCal.get(Calendar.HOUR_OF_DAY);
        Integer startMin=startCal.get(Calendar.MINUTE);
        Integer startDay=startCal.get(Calendar.DATE);
        Integer startMonth=startCal.get(Calendar.MONTH)+1;
        Integer startYear=startCal.get(Calendar.YEAR);


        nearestLessonTime.setText(String.format(Constants.timePrintingFormat,startHour,startMin));
        nearestLessonDate.setText(String.format(Constants.datePrintingFormat,startDay,startMonth,startYear));

    }

    protected void saveLessonsToSqlLite(Context context) {
        if(sqlLitDbHelper.isConnected(context)) {
            final sqlLitDbHelper myLocalDb=new sqlLitDbHelper(context);
            final ArrayList<lessonPayment> lessonsPaymentData=new ArrayList<>();
            db=FirebaseFirestore.getInstance();
            db.collection(Constants.drivingSchool).document(pupilPagesNavigator.schoolId).collection(Constants.teachers).document(pupilPagesNavigator.teacherId).collection(Constants.pupils)
                    .document(pupilPagesNavigator.pupilId).collection(Constants.lessons).orderBy(Constants.lessonDate, Query.Direction.DESCENDING).get().addOnCompleteListener(
                    new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                    lessonPayment lesPayToAdd = doc.toObject(lessonPayment.class);
                                    lessonsPaymentData.add(lesPayToAdd);
                                }
                                ArrayList<lessonPayment> currentSqlLiteData=myLocalDb.readLessonPayFromSqlLite();
                                for(lessonPayment lesPay:lessonsPaymentData){
                                    if(!currentSqlLiteData.contains(lesPay)){
                                        myLocalDb.savelessonPayToSqlLite(lesPay);
                                    }
                                }
                            }
                        }
                    });
        }
    }
}