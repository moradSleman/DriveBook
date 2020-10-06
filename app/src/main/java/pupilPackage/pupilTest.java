package pupilPackage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import adapters.pupilTestAdapter;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import databaseClasses.Constants;
import databaseClasses.Test;
import databaseClasses.rotateImage;
import databaseClasses.sqlLitDbHelper;
import teacherPackage.teacherHomePage;
import teacherPackage.teacherPagesNavigator;

public class pupilTest{
    private FirebaseFirestore db;
    private ArrayList<Test> pupilTests;
    private String schoolId;
    private String teacherId;
    public static String pupilId;
    private Context context;
    private RecyclerView mRecyclerView;
    private sqlLitDbHelper myLocalDb;
    private boolean isPupilPage;
    public static pupilTestAdapter recyclerAdapter=null;
    public void onCreateclass(Context context,String pupilId,boolean isPupilPage){
        this.context=context;
        db=FirebaseFirestore.getInstance();
        myLocalDb=new sqlLitDbHelper(context);
        pupilTests=new ArrayList<>();
        this.isPupilPage=isPupilPage;
        if(isPupilPage) {
            schoolId = pupilPagesNavigator.schoolId;
            teacherId = pupilPagesNavigator.teacherId;
            this.pupilId = pupilPagesNavigator.pupilId;
            mRecyclerView = pupilHomePage.testRecyclerView;

        }else{
            schoolId= teacherPagesNavigator.schoolId;
            teacherId=teacherPagesNavigator.teacherId;
            this.pupilId=pupilId;
            mRecyclerView = teacherHomePage.pupilTestsRecycler;
        }
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        setData();
    }

    private void setData() {
        Calendar cal=Calendar.getInstance();
        final Date todayDate=cal.getTime();
        if(sqlLitDbHelper.isConnected(context)){
            if(pupilId!=null)
            db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(teacherId).collection(Constants.pupils)
                    .document(pupilId).collection(Constants.tests).orderBy(Constants.testDate, Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful() && !task.getResult().isEmpty()){
                        int i=0;
                        Date testDate=null;
                        for(QueryDocumentSnapshot doc : task.getResult()){
                            Test test=doc.toObject(Test.class);
                            if(isPupilPage) {
                                if (i == 0) {
                                    testDate = test.getTestTimeStart();
                                }
                            }else{
                                if(test.getTestTimeStart().before(Calendar.getInstance().getTime())){
                                    testDate=test.getTestTimeStart();
                                }
                            }
                            Test testToAdd=doc.toObject(Test.class);
                            i++;
                            pupilTests.add(testToAdd);
                        }
                        if(isPupilPage) {
                            if (testDate!=null && testDate.after(todayDate)) {
                                setNearestTestDate(testDate);
                            } else {
                                setNearestTestDate(null);
                            }
                        }else{
                            setNearestTestDate(testDate);
                        }

                        if(isPupilPage) {
                             pupilHomePage.testsNum.setText(((Integer) pupilTests.size()).toString());
                            recyclerAdapter = new pupilTestAdapter(context, pupilTests,true);
                        }else{
                            teacherHomePage.testNumDown.setText(((Integer)pupilTests.size()).toString());
                            recyclerAdapter = new pupilTestAdapter(context, pupilTests,false);
                        }

                        mRecyclerView.setAdapter(recyclerAdapter);
                    }
                }
            });
        }else{
            pupilTests=myLocalDb.readTestFromDb();
            if(pupilTests.get(0).getTestTimeStart().after(todayDate)){
                setNearestTestDate(pupilTests.get(0).getTestTimeStart());
            }else{
                setNearestTestDate(null);
            }
            pupilHomePage.testsNum.setText(((Integer)pupilTests.size()).toString());

            //it is pupil page then send isPupil=true
            pupilTestAdapter recyclerAdapter = new pupilTestAdapter(context,pupilTests,true);
            mRecyclerView.setAdapter(recyclerAdapter);
        }
    }

    private void setNearestTestDate(Date startingTestTime) {
        if(startingTestTime==null){
            pupilHomePage.nearTestTime.setText("אין");
            pupilHomePage.nearTestDate.setText("");
            return;
        }
        Calendar startCal=Calendar.getInstance();
        startCal.setTime(startingTestTime);
        Integer startHour=startCal.get(Calendar.HOUR_OF_DAY);
        Integer startMin=startCal.get(Calendar.MINUTE);
        Integer startDay=startCal.get(Calendar.DATE);
        Integer startMonth=startCal.get(Calendar.MONTH)+1;
        Integer startYear=startCal.get(Calendar.YEAR);

        if(isPupilPage) {
            pupilHomePage.nearTestTime.setText(String.format(Constants.timePrintingFormat, startHour, startMin));
            pupilHomePage.nearTestDate.setText(String.format(Constants.datePrintingFormat, startDay, startMonth, startYear));
        }else{

        }
    }

    protected void saveTestToSqlLite(Context context) {
        ArrayList<Test> lessonsPaymentData=new ArrayList<>();
        if(sqlLitDbHelper.isConnected(context)) {
            final sqlLitDbHelper myLocalDb=new sqlLitDbHelper(context);
            final ArrayList<Test> finalLessonsPaymentData = lessonsPaymentData;
            db=FirebaseFirestore.getInstance();
            db.collection(Constants.drivingSchool).document(pupilPagesNavigator.schoolId).collection(Constants.teachers).document(pupilPagesNavigator.teacherId).collection(Constants.pupils)
                    .document(pupilPagesNavigator.pupilId).collection(Constants.tests).orderBy(Constants.testTime, Query.Direction.DESCENDING).get().addOnCompleteListener(
                    new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                    Test testToAdd = doc.toObject(Test.class);
                                    finalLessonsPaymentData.add(testToAdd);
                                }
                                ArrayList<Test> currentSqlLiteTests=myLocalDb.readTestFromDb();
                                for(Test test: finalLessonsPaymentData){
                                    if(!currentSqlLiteTests.contains(test)){
                                        myLocalDb.saveTestToDb(test);
                                    }
                                }
                            }
                        }
                    });
        }
    }
}
