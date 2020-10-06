package schoolPackage;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.user.mainPages.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import databaseClasses.Constants;
import databaseClasses.Test;

public class schoolStatisticsPage extends Fragment {
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String schoolUId;
    private BarChart chart;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.school_statistics, container, false);
        db=FirebaseFirestore.getInstance();
        schoolUId=schoolPagesNavigator.schoolUId;
        chart = (BarChart) rootView.findViewById(R.id.PathTestStatistic);
        setColumnChart();
        return rootView;
    }

    private void setColumnChart() {
        final Calendar day1=Calendar.getInstance();
        final Calendar day2=Calendar.getInstance();
        final Calendar day3=Calendar.getInstance();
        final Calendar day4=Calendar.getInstance();
        final Calendar day5=Calendar.getInstance();

        day1.set(Calendar.HOUR_OF_DAY,23);
        day1.set(Calendar.MINUTE,59);
        day1.set(Calendar.SECOND,0);
        day1.set(Calendar.MILLISECOND,0);
        day1.set(Calendar.DAY_OF_MONTH,day1.get(Calendar.DAY_OF_MONTH)-1);

        day2.set(Calendar.HOUR_OF_DAY,23);
        day2.set(Calendar.MINUTE,59);
        day2.set(Calendar.SECOND,0);
        day2.set(Calendar.DAY_OF_YEAR,day2.get(Calendar.DAY_OF_YEAR)-2);

        day3.set(Calendar.HOUR_OF_DAY,23);
        day3.set(Calendar.MINUTE,59);
        day3.set(Calendar.SECOND,0);
        day3.set(Calendar.DAY_OF_YEAR,day3.get(Calendar.DAY_OF_YEAR)-3);

        day4.set(Calendar.HOUR_OF_DAY,23);
        day4.set(Calendar.MINUTE,59);
        day4.set(Calendar.SECOND,0);
        day4.set(Calendar.DAY_OF_YEAR,day4.get(Calendar.DAY_OF_YEAR)-4);

        day5.set(Calendar.HOUR_OF_DAY,23);
        day5.set(Calendar.MINUTE,59);
        day5.set(Calendar.SECOND,0);
        day5.set(Calendar.DAY_OF_YEAR,day5.get(Calendar.DAY_OF_YEAR)-5);





        db.collection(Constants.drivingSchool).document(schoolUId).collection(Constants.teachers).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            private int counter1=0;
            private int yet1=0;
            private int counter2=0;
            private int yet2=0;
            private int counter3=0;
            private int yet3=0;
            private int counter4=0;
            private int yet4=0;
            private int sumCount1=0;
            private int sumCount2=0;
            private int sumCount3=0;
            private int sumCount4=0;
            private int sumTeachers;
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful() && !task.getResult().isEmpty()){
                    sumTeachers=task.getResult().size();
                    for(QueryDocumentSnapshot doc:task.getResult()){
                        doc.getReference().collection(Constants.pupils).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            private int sumPupils;
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                sumTeachers--;
                                if(task.isSuccessful() && !task.getResult().isEmpty()){
                                    sumPupils=task.getResult().size()*4;
                                    for(QueryDocumentSnapshot doc : task.getResult()){
                                        doc.getReference().collection(Constants.tests).whereGreaterThanOrEqualTo(Constants.testDate,day2.getTime())
                                                .whereLessThanOrEqualTo(Constants.testDate,day1.getTime()).get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        sumPupils--;
                                                        if(task.isSuccessful() && !task.getResult().isEmpty()){
                                                            ArrayList<Test> tests=(ArrayList<Test>)task.getResult().toObjects(Test.class);
                                                            for(Test test : tests) {
                                                                if (!test.isInternalTest()) {
                                                                    sumCount1++;
                                                                    if (test.getResult().equals(Constants.testResultSucces)) {
                                                                        counter1++;
                                                                    }else
                                                                    if(test.getResult().equals(Constants.tesutResultYer)){
                                                                        yet1++;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        setChart();
                                                    }
                                                });
                                        doc.getReference().collection(Constants.tests).whereGreaterThanOrEqualTo(Constants.testDate,day3.getTime())
                                                .whereLessThanOrEqualTo(Constants.testDate,day2.getTime()).get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        sumPupils--;
                                                        if(task.isSuccessful() && !task.getResult().isEmpty()){
                                                            ArrayList<Test> tests=(ArrayList<Test>)task.getResult().toObjects(Test.class);
                                                            for(Test test : tests) {
                                                                if (!test.isInternalTest()) {
                                                                    sumCount2++;
                                                                    if (test.getResult().equals(Constants.testResultSucces)) {
                                                                        counter2++;
                                                                    }else
                                                                    if(test.getResult().equals(Constants.tesutResultYer)){
                                                                        yet2++;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        setChart();
                                                    }
                                                });
                                        doc.getReference().collection(Constants.tests).whereGreaterThanOrEqualTo(Constants.testDate,day4.getTime())
                                                .whereLessThanOrEqualTo(Constants.testDate,day4.getTime()).get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        sumPupils--;
                                                        if(task.isSuccessful() && !task.getResult().isEmpty()){
                                                            ArrayList<Test> tests=(ArrayList<Test>)task.getResult().toObjects(Test.class);
                                                            for(Test test : tests) {
                                                                if (!test.isInternalTest()) {
                                                                    sumCount3++;
                                                                    if (test.getResult().equals(Constants.testResultSucces)) {
                                                                        counter3++;
                                                                    }else
                                                                    if(test.getResult().equals(Constants.tesutResultYer)){
                                                                        yet3++;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        setChart();
                                                    }
                                                });
                                        doc.getReference().collection(Constants.tests).whereGreaterThanOrEqualTo(Constants.testDate,day5.getTime())
                                                .whereLessThanOrEqualTo(Constants.testDate,day4.getTime()).get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        sumPupils--;
                                                        if(task.isSuccessful() && !task.getResult().isEmpty()){
                                                            ArrayList<Test> tests=(ArrayList<Test>)task.getResult().toObjects(Test.class);
                                                            for(Test test : tests) {

                                                                if (!test.isInternalTest()) {
                                                                    sumCount4++;
                                                                    if (test.getResult().equals(Constants.testResultSucces)) {
                                                                        counter4++;
                                                                    }else
                                                                    if(test.getResult().equals(Constants.tesutResultYer)){
                                                                        yet4++;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        setChart();

                                                    }
                                                });
                                    }
                                }
                            }

                            private void setChart() {
                                    if(sumTeachers==0 && sumPupils==0){
                                        BarData data = new BarData(getXAxisValues(),getDataSet());
                                        chart.setData(data);
                                        chart.animateXY(2000, 2000);
                                        chart.invalidate();
                                }
                            }
                            public ArrayList getDataSet() {
                                ArrayList dataSets = null;

                                ArrayList valueSet1 = new ArrayList();
                                BarEntry v1e1 = new BarEntry(counter1, 0); // day1
                                valueSet1.add(v1e1);
                                BarEntry v1e2 = new BarEntry(counter2, 1); // day2
                                valueSet1.add(v1e2);
                                BarEntry v1e3 = new BarEntry(counter3, 2); // day3
                                valueSet1.add(v1e3);
                                BarEntry v1e4 = new BarEntry(counter4, 3); // day4
                                valueSet1.add(v1e4);

                                ArrayList valueSet2 = new ArrayList();
                                BarEntry v2e1 = new BarEntry(yet1, 0); // day1
                                valueSet2.add(v2e1);
                                BarEntry v2e2 = new BarEntry(yet2, 1); // day2
                                valueSet2.add(v2e2);
                                BarEntry v2e3 = new BarEntry(yet3, 2); // day3
                                valueSet2.add(v2e3);
                                BarEntry v2e4 = new BarEntry(yet4, 3); // day4
                                valueSet2.add(v2e4);

                                ArrayList valueSet3 = new ArrayList();
                                BarEntry v3e1 = new BarEntry(sumCount1-counter1-yet1, 0); // day1
                                valueSet3.add(v3e1);
                                BarEntry v3e2 = new BarEntry(sumCount2-counter2-yet2, 1); // day2
                                valueSet3.add(v3e2);
                                BarEntry v3e3 = new BarEntry(sumCount3-counter3-yet3, 2); // day3
                                valueSet3.add(v3e3);
                                BarEntry v3e4 = new BarEntry(sumCount4-counter4-yet4, 3); // day4
                                valueSet3.add(v3e4);

                                BarDataSet barDataSet1 = new BarDataSet(valueSet1, "עבר");
                                barDataSet1.setColor(Color.GREEN);
                                BarDataSet barDataSet2 = new BarDataSet(valueSet2, "המתנה");
                                barDataSet2.setColor(Color.BLUE);
                                BarDataSet barDataSet3 = new BarDataSet(valueSet3, "נכשל");
                                barDataSet3.setColor(Color.RED);

                                dataSets = new ArrayList();
                                dataSets.add(barDataSet1);
                                dataSets.add(barDataSet2);
                                dataSets.add(barDataSet3);
                                return dataSets;
                            }

                            public ArrayList getXAxisValues() {
                                ArrayList xAxis = new ArrayList();
                                xAxis.add(day1.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US));
                                xAxis.add(day2.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US));
                                xAxis.add(day3.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US));
                                xAxis.add(day4.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US));
                                return xAxis;
                            }
                        });
                    }
                }
            }
        });

    }

}
