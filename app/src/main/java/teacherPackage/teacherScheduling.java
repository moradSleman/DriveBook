package teacherPackage;
import adapters.timesEditAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.switchmaterial.SwitchMaterial;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.user.mainPages.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import databaseClasses.Constants;
import databaseClasses.Pupil;
import databaseClasses.Schedule;
import databaseClasses.Test;
import databaseClasses.TimeScheduling;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class teacherScheduling extends Fragment {
    private int countSchools;
    private HashMap<String, Integer> repetionMap;
    public static Integer chosenDayFromLookUpDialog;


    interface progressAddingSchedule {
        public void setSumDays(int sumDays);
        public int getSumDays();
        public int getSumTimesAtDay();
        public void setSumTimesAtDay(int timesAtDay);
        public void removeOneDayIfEmpty(DocumentReference scheduleDayRef);
        public void addNewTimes();
        public void showTimesScheduledByPupil();
        public void removeNotScheduledDays();
    }
    private View rootView;

    public static boolean isSameHoursDays;

    public static HashMap<Integer, LinkedHashMap<Integer, TimeScheduling>>timesToDays;

    public static ArrayList<TimeScheduling>timesHasBeenScheduled;
    public static ArrayList<TimeScheduling> totalTimesToAdd;
    private int lessonIntervalTime;

    private SweetAlertDialog pDialog;

    private Integer choosenCalendarDay;
    private ArrayList<Integer> sameChoosenCalendarDays;
    private FirebaseFirestore db;
    private String teacherId;
    public static ArrayList<String> schoolIdsToSchedule;
    private Integer timesRepition;

    private AutoCompleteTextView drivingScoolSpinner, autoCompleteRepetition;
    private SwitchMaterial sameDaysSwitch;
    private LinearLayout sameHours, differentHours;
    private CheckBox day1Check, day2Check, day3Check, day4Check, day5Check, day6Check, day7Check;
    private TextView day1, day1Filled, day2, day2Filled, day3, day3Filled, day4, day4Filled, day5, day5Filled, day6, day6Filled, day7, day7Filled;
    private TextInputEditText strtTimeEditText,endtimeEditText;
    private TextInputLayout layoutStartTime,layoutEndTime;
    private TextView addMoreTimes,timesFeedback;
    private Button save, lookUp, cancel;
    private HashMap<Object, Object> schoolNameMappingUId;
    private Date newStartTime;
    private Date newEndtime;


    //test views

    private EditText dateEditText,timeStartEditTextTest,timeEndEditTextTest;
    private AutoCompleteTextView autoCompletetester1,autoCompletetester2,drivingScoolSpinnerTest;
    private SwitchMaterial switchInternalTest;
    private Button saveTest,cancelTest;
    private HashMap<String, String> schoolNameMappingUIdTest;
    private Date testDate;
    private boolean isEnternal;
    private Date newEndtimeTest;
    private Date newStartTimeTest;
    private String pupilTest1,pupilTest2;
    private HashMap<String, String> pupilIdsMapping;
    private ArrayList<String> schoolNamesTestLayout;

    private TabLayout tabs;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.teacher_schedule, null);

        db= FirebaseFirestore.getInstance();
        sameChoosenCalendarDays=new ArrayList<>();

        timesToDays=new HashMap<>();
        timesHasBeenScheduled=new ArrayList<>();
        totalTimesToAdd=new ArrayList<>();

        teacherId=teacherPagesNavigator.teacherId;
        schoolIdsToSchedule =new ArrayList<>();

        drivingScoolSpinner=rootView.findViewById(R.id.drivingScoolSpinner);
        autoCompleteRepetition=rootView.findViewById(R.id.autoCompleteRepetition);
        sameDaysSwitch=rootView.findViewById(R.id.sameDaysSwitch);
        sameHours=rootView.findViewById(R.id.sameHours);
        differentHours=rootView.findViewById(R.id.differentHours);

        day1Check=rootView.findViewById(R.id.day1Check);
        day2Check=rootView.findViewById(R.id.day2Check);
        day3Check=rootView.findViewById(R.id.day3Check);
        day4Check=rootView.findViewById(R.id.day4Check);
        day5Check=rootView.findViewById(R.id.day5Check);
        day6Check=rootView.findViewById(R.id.day6Check);
        day7Check=rootView.findViewById(R.id.day7Check);

        day1=rootView.findViewById(R.id.day1);
        day1Filled=rootView.findViewById(R.id.day1Filled);
        day2=rootView.findViewById(R.id.day2);
        day2Filled=rootView.findViewById(R.id.day2Filled);
        day3=rootView.findViewById(R.id.day3);
        day3Filled=rootView.findViewById(R.id.day3Filled);
        day4=rootView.findViewById(R.id.day4);
        day4Filled=rootView.findViewById(R.id.day4Filled);
        day5=rootView.findViewById(R.id.day5);
        day5Filled=rootView.findViewById(R.id.day5Filled);
        day6=rootView.findViewById(R.id.day6);
        day6Filled=rootView.findViewById(R.id.day6Filled);
        day7=rootView.findViewById(R.id.day7);
        day7Filled=rootView.findViewById(R.id.day7Filled);

        strtTimeEditText=rootView.findViewById(R.id.startTimeEditText);
        endtimeEditText=rootView.findViewById(R.id.endTimeEditText);

        layoutStartTime=rootView.findViewById(R.id.startTimeLayout);
        layoutEndTime=rootView.findViewById(R.id.endTimeLayout);

        addMoreTimes=rootView.findViewById(R.id.addMoreTimes);
        timesFeedback=rootView.findViewById(R.id.timesFeedback);

        save=rootView.findViewById(R.id.save);
        lookUp=rootView.findViewById(R.id.lookUp);
        cancel=rootView.findViewById(R.id.cancel);

        pDialog= new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#00a5ff"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);

        isSameHoursDays=true;

        setChooseDayListener();
        setSaveListener();
        setSchoolDropDown();
        setRepitionDropDown();
        setSwitchSameDayeHours();
        setAddMoreTimesListner();
        setTimeListeners();
        setLookUpListener();


    ///////////test data Set//////////////
        dateEditText=rootView.findViewById(R.id.dateEditText);
        timeStartEditTextTest=rootView.findViewById(R.id.timeStartEditTextTest);
        timeEndEditTextTest=rootView.findViewById(R.id.timeEndEditTextTest);
        autoCompletetester1=rootView.findViewById(R.id.autoCompletetester1);
        autoCompletetester2=rootView.findViewById(R.id.autoCompletetester2);
        switchInternalTest=rootView.findViewById(R.id.switchInternalTest);
        saveTest=rootView.findViewById(R.id.saveTest);
        cancelTest=rootView.findViewById(R.id.cancelTest);
        drivingScoolSpinnerTest=rootView.findViewById(R.id.drivingScoolSpinnerTest);

        isEnternal=false;
        setTabs();
        setSchoolSearch();
        setDatePicker();
        setSwitchInternalTest();
        setTimesPicker();
        setSaveTestListener();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new teacherHomePage(),Constants.tagTeacherHome).commit();
            }
        });
        return rootView;
    }


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void setTabs() {
        tabs =rootView.findViewById(R.id.tabs);
        tabs.setTabMode(TabLayout.MODE_FIXED);
        tabs.setTabGravity(TabLayout.GRAVITY_FILL);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    rootView.findViewById(R.id.lessonScheduling).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.testsScheduling).setVisibility(View.GONE);
                }else {
                    rootView.findViewById(R.id.lessonScheduling).setVisibility(View.GONE);
                    rootView.findViewById(R.id.testsScheduling).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setSaveTestListener() {
        saveTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drivingScoolSpinnerTest.getText().toString().isEmpty()){
                    new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("בחר בית סופר")
                            .setConfirmText("בסדר")
                            .show();
                    return;
                }
                if((pupilTest1==null || pupilTest1.isEmpty()) &&(pupilTest2==null || pupilTest2.isEmpty())){
                    new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("בחר תלמיד")
                            .setConfirmText("בסדר")
                            .show();
                    return;
                }
                final Calendar testDate1=Calendar.getInstance();
                testDate1.setTime(testDate);
                testDate1.set(Calendar.HOUR_OF_DAY,0);
                testDate1.set(Calendar.MINUTE,0);
                testDate1.set(Calendar.SECOND,0);
                final Calendar testSttartingDateC= Calendar.getInstance();
                testSttartingDateC.setTime(testDate);
                Calendar newStartTimeC=Calendar.getInstance();
                newStartTimeC.setTime(newStartTimeTest);
                testSttartingDateC.set(Calendar.HOUR_OF_DAY,newStartTimeC.get(Calendar.HOUR_OF_DAY));
                testSttartingDateC.set(Calendar.MINUTE,newStartTimeC.get(Calendar.MINUTE));
                testSttartingDateC.set(Calendar.SECOND,newStartTimeC.get(Calendar.SECOND));
                Calendar newEndTimeC=Calendar.getInstance();
                newEndTimeC.setTime(newEndtimeTest);

                final Calendar testEndingTimeC=Calendar.getInstance();
                testEndingTimeC.setTime(testDate);
                testEndingTimeC.set(Calendar.HOUR_OF_DAY,newEndTimeC.get(Calendar.HOUR_OF_DAY));
                testEndingTimeC.set(Calendar.MINUTE,newEndTimeC.get(Calendar.MINUTE));
                testEndingTimeC.set(Calendar.SECOND,newEndTimeC.get(Calendar.SECOND));

                final TimeScheduling timeTestToAdd=new TimeScheduling();
                timeTestToAdd.setSchoolUId(schoolIdsToSchedule.get(0));
                timeTestToAdd.setTest(true);
                timeTestToAdd.setEnternalTest(isEnternal);
                timeTestToAdd.setDate(testDate1.getTime());
                timeTestToAdd.setEndTime(testEndingTimeC.getTime());
                timeTestToAdd.setStartTime(testSttartingDateC.getTime());
                timeTestToAdd.setPupilUId(pupilTest1!=null?pupilTest1:pupilTest2);
                timeTestToAdd.setSecondPupilUId(pupilTest1!=null?pupilTest2:pupilTest2!=null?pupilTest2:null);
                if(pupilTest1!=null)
                    timeTestToAdd.setPupilFullName(autoCompletetester1.getText().toString());
                else
                    timeTestToAdd.setPupilFullName(autoCompletetester2.getText().toString());
                if(pupilTest1!=null)
                    timeTestToAdd.setSecondpupilFullName(autoCompletetester2.getText().toString());

                final Schedule schedule=new Schedule();
                schedule.setScheduleDate(testDate1.getTime());

                db.collection(Constants.drivingSchool).document(schoolIdsToSchedule.get(0)).collection(Constants.teachers).document(teacherPagesNavigator.teacherId)
                        .collection(Constants.schedules).document(testDate1.getTime().toString()).collection(Constants.oneDayeSchedules).get().addOnCompleteListener(
                        new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful() && !task.getResult().isEmpty()){
                                    for(QueryDocumentSnapshot doc:task.getResult()){
                                        TimeScheduling time=doc.toObject(TimeScheduling.class);
                                        if(!time.getStartTime().after(timeTestToAdd.getEndTime()) && !time.getEndTime().before(timeTestToAdd.getStartTime()))
                                             doc.getReference().delete();
                                    }
                                    db.collection(Constants.drivingSchool).document(schoolIdsToSchedule.get(0)).collection(Constants.teachers).document(teacherPagesNavigator.teacherId)
                                            .collection(Constants.schedules).document(testDate1.getTime().toString()).collection(Constants.oneDayeSchedules).document(testSttartingDateC.getTime()
                                            .toString()).set(timeTestToAdd);
                                }else{
                                    db.collection(Constants.drivingSchool).document(schoolIdsToSchedule.get(0)).collection(Constants.teachers).document(teacherPagesNavigator.teacherId)
                                            .collection(Constants.schedules).document(testDate1.getTime().toString()).set(schedule).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            db.collection(Constants.drivingSchool).document(schoolIdsToSchedule.get(0)).collection(Constants.teachers).document(teacherPagesNavigator.teacherId)
                                                    .collection(Constants.schedules).document(testDate1.getTime().toString()).collection(Constants.oneDayeSchedules).document(testSttartingDateC.getTime()
                                                    .toString()).set(timeTestToAdd);
                                        }
                                    });

                                }
                            }
                        }
                );

                final Test testToAdd=new Test();
                testToAdd.setInternalTest(isEnternal);
                testToAdd.setResult(Constants.tesutResultYer);
                testToAdd.setTestDate(timeTestToAdd.getDate());
                testToAdd.setTestTimeStart(timeTestToAdd.getStartTime());
                testToAdd.setPaidSituation(false);
                db.collection(Constants.drivingSchool).document(schoolIdsToSchedule.get(0)).collection(Constants.teachers).document(teacherPagesNavigator.teacherId).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful() && task.getResult().exists()){
                                    testToAdd.setTestCost(task.getResult().get(Constants.teacherLessonCost)==null?
                                            (Integer)80:Integer.parseInt(task.getResult().get(Constants.teacherLessonCost).toString()));
                                }
                                if(pupilTest1!=null){
                                    db.collection(Constants.drivingSchool).document(schoolIdsToSchedule.get(0)).collection(Constants.teachers).document(teacherPagesNavigator.teacherId)
                                            .collection(Constants.pupils).document(pupilTest1).collection(Constants.tests).document(testToAdd.getTestTimeStart().toString())
                                            .set(testToAdd);
                                }
                                if(pupilTest2!=null){
                                    db.collection(Constants.drivingSchool).document(schoolIdsToSchedule.get(0)).collection(Constants.teachers).document(teacherPagesNavigator.teacherId)
                                            .collection(Constants.pupils).document(pupilTest2).collection(Constants.tests).document(testToAdd.getTestTimeStart().toString())
                                            .set(testToAdd);
                                }
                                new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Good job!")
                                        .show();
                            }
                        });

            }
        });
    }

    private void setpupilNamesSearch() {

        String schoolId=schoolIdsToSchedule.get(0);
        final ArrayList<String>pupilSearchList=new ArrayList<>();
        db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(teacherId).collection(
                Constants.pupils).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful() && !task.getResult().isEmpty()){
                    pupilIdsMapping=new HashMap<>();
                    for(QueryDocumentSnapshot doc:task.getResult()){
                        Pupil pupil=doc.toObject(Pupil.class);
                        String searchS=pupil.getPupilName()+" "+pupil.getPupilPhone();
                        pupilIdsMapping.put(searchS,doc.getId());
                        pupilSearchList.add(searchS);
                    }

                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<String>(
                                    teacherPagesNavigator.context,
                                    R.layout.drop_down_layout,
                                    pupilSearchList);
                    autoCompletetester1.setAdapter(adapter);
                    autoCompletetester2.setAdapter(adapter);

                    autoCompletetester1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            autoCompletetester1.setText(autoCompletetester1.getAdapter().getItem(position).toString());
                            pupilTest1=pupilIdsMapping.get(autoCompletetester1.getAdapter().getItem(position).toString()).toString();
                        }
                    });

                    autoCompletetester2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            autoCompletetester2.setText(autoCompletetester2.getAdapter().getItem(position).toString());
                            pupilTest1=pupilIdsMapping.get(autoCompletetester2.getAdapter().getItem(position).toString()).toString();
                        }
                    });
                }
            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setTimesPicker() {
        final Calendar cStart = Calendar.getInstance();
        int mHour = cStart.get(Calendar.HOUR_OF_DAY);
        int mMinute = cStart.get(Calendar.MINUTE);
        timeStartEditTextTest.setText(String.format(Constants.timePrintingFormat,  mHour ,mMinute));
        Calendar cc=Calendar.getInstance();
        cc.set(Calendar.HOUR_OF_DAY,mHour);
        cc.set(Calendar.MINUTE,mMinute);
        cc.set(Calendar.SECOND,0);
        newStartTimeTest=cc.getTime();
        timeStartEditTextTest.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                // Process to get Current Time
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog tpd = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                // Display Selected time in textbox
                                timeStartEditTextTest.setText(String.format(Constants.timePrintingFormat,  hourOfDay ,minute));
                                Calendar cc=Calendar.getInstance();
                                cc.set(Calendar.HOUR_OF_DAY,hourOfDay);
                                cc.set(Calendar.MINUTE,minute);
                                cc.set(Calendar.SECOND,0);
                                newStartTimeTest=cc.getTime();
                            }
                        }, mHour, mMinute, true);
                tpd.show();

            }
        });

        final Calendar cEnd = Calendar.getInstance();
        int mHoureEnd = cEnd.get(Calendar.HOUR_OF_DAY);
        int mMinuteEnd = cEnd.get(Calendar.MINUTE);
        timeEndEditTextTest.setText(String.format(Constants.timePrintingFormat,  mHoureEnd ,mMinuteEnd));
        Calendar ee=Calendar.getInstance();
        ee.set(Calendar.HOUR_OF_DAY,mHoureEnd);
        ee.set(Calendar.MINUTE,mMinuteEnd);
        ee.set(Calendar.SECOND,0);
        newEndtimeTest=ee.getTime();
        timeEndEditTextTest.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                // Process to get Current Time
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);
                // Launch Time Picker Dialog
                TimePickerDialog tpd = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                // Display Selected time in textbox
                                timeEndEditTextTest.setText(String.format(Constants.timePrintingFormat,  hourOfDay ,minute));
                                Calendar cc=Calendar.getInstance();
                                cc.set(Calendar.HOUR_OF_DAY,hourOfDay);
                                cc.set(Calendar.MINUTE,minute);
                                cc.set(Calendar.SECOND,0);
                                newEndtimeTest=cc.getTime();
                            }
                        }, mHour, mMinute, true);
                tpd.show();

            }
        });
    }

    private void setSwitchInternalTest() {
        switchInternalTest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    isEnternal=true;
                }else{
                    isEnternal=false;
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setDatePicker() {
        final Calendar myCalendar = Calendar.getInstance();
        myCalendar.set(Calendar.HOUR_OF_DAY,0);
        myCalendar.set(Calendar.MINUTE,0);
        myCalendar.set(Calendar.SECOND,0);
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        testDate=myCalendar.getTime();
        dateEditText.setText(sdf.format(myCalendar.getTime()).toString());
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "MM/dd/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
                testDate=myCalendar.getTime();
                dateEditText.setText(sdf.format(myCalendar.getTime()));
            }

        };

        dateEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void setSchoolSearch() {
        drivingScoolSpinnerTest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                drivingScoolSpinnerTest.setText(drivingScoolSpinnerTest.getAdapter().getItem(position).toString());
                    schoolIdsToSchedule=new ArrayList<>();
                    schoolIdsToSchedule.add(schoolNameMappingUIdTest.get(drivingScoolSpinnerTest.getAdapter().getItem(position).toString()).toString());
                setpupilNamesSearch();
            }
        });
        db.collection(Constants.UIDS).document(teacherId).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    private int counter;

                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult().exists()) {
                             schoolNamesTestLayout=new ArrayList<>();
                             ArrayList<String> schoolUIds=(ArrayList<String>) task.getResult().get(Constants.schoolsUIDs);
                            counter=schoolUIds.size();
                            schoolNameMappingUIdTest = new HashMap<>();
                            for(final String schoolId:schoolUIds) {
                                db.collection(Constants.drivingSchool).document(schoolId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful() && task.getResult().exists()){
                                            schoolNamesTestLayout.add(task.getResult().get(Constants.schoolName).toString());
                                            schoolNameMappingUIdTest.put(task.getResult().get(Constants.schoolName).toString(),schoolId);
                                            counter--;
                                            if(counter==0){
                                                ArrayAdapter<String> adapter =
                                                        new ArrayAdapter<String>(
                                                                teacherPagesNavigator.context,
                                                                R.layout.drop_down_layout,
                                                                schoolNamesTestLayout);
                                                drivingScoolSpinnerTest.setAdapter(adapter);
                                                drivingScoolSpinnerTest.setText(drivingScoolSpinnerTest.getAdapter().getItem(0).toString());
                                                schoolIdsToSchedule=new ArrayList<>();
                                                schoolIdsToSchedule.add(schoolNameMappingUIdTest.get(drivingScoolSpinnerTest.getAdapter().getItem(0).toString()).toString());
                                                setpupilNamesSearch();
                                            }
                                        }
                                    }
                                });
                            }

                        }
                    }
                });
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////



    private void setLookUpListener() {
        final Dialog lookUpDialog=new Dialog(getContext());
        lookUpDialog.setContentView(R.layout.teacher_schduing_hours_pop_up);
        final TextView day1, day1Filled, day2, day2Filled, day3, day3Filled, day4, day4Filled, day5, day5Filled, day6, day6Filled, day7, day7Filled;
        final RecyclerView timesRecycler;
        Button saveAfterLookUp,cancelLookUp;

        timesRecycler=lookUpDialog.findViewById(R.id.timesRecycler);
        saveAfterLookUp=lookUpDialog.findViewById(R.id.saveAfterLookUp);
        cancelLookUp=lookUpDialog.findViewById(R.id.cancelLookUp);
        day1=lookUpDialog.findViewById(R.id.day1);
        day1Filled=lookUpDialog.findViewById(R.id.day1Filled);
        day2=lookUpDialog.findViewById(R.id.day2);
        day2Filled=lookUpDialog.findViewById(R.id.day2Filled);
        day3=lookUpDialog.findViewById(R.id.day3);
        day3Filled=lookUpDialog.findViewById(R.id.day3Filled);
        day4=lookUpDialog.findViewById(R.id.day4);
        day4Filled=lookUpDialog.findViewById(R.id.day4Filled);
        day5=lookUpDialog.findViewById(R.id.day5);
        day5Filled=lookUpDialog.findViewById(R.id.day5Filled);
        day6=lookUpDialog.findViewById(R.id.day6);
        day6Filled=lookUpDialog.findViewById(R.id.day6Filled);
        day7=lookUpDialog.findViewById(R.id.day7);
        day7Filled=lookUpDialog.findViewById(R.id.day7Filled);
        LinearLayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(getContext());
        timesRecycler.setHasFixedSize(true);
        timesRecycler.setLayoutManager(mLayoutManager);

        cancelLookUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lookUpDialog.dismiss();
            }
        });
        saveAfterLookUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save.performClick();
            }
        });

        day1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                chosenDayFromLookUpDialog=Calendar.SUNDAY;
                choosenCalendarDay=Calendar.SUNDAY;
                day1.setVisibility(View.GONE);
                day1Filled.setVisibility(View.VISIBLE);
                day2.setVisibility(View.VISIBLE);
                day2Filled.setVisibility(View.GONE);
                day3.setVisibility(View.VISIBLE);
                day3Filled.setVisibility(View.GONE);
                day4.setVisibility(View.VISIBLE);
                day4Filled.setVisibility(View.GONE);
                day5.setVisibility(View.VISIBLE);
                day5Filled.setVisibility(View.GONE);
                day6.setVisibility(View.VISIBLE);
                day6Filled.setVisibility(View.GONE);
                day7.setVisibility(View.VISIBLE);
                day7Filled.setVisibility(View.GONE);
                if(timesToDays.get(Calendar.SUNDAY)!=null) {
                    ArrayList<TimeScheduling> times = new ArrayList<>();
                    for (TimeScheduling time : timesToDays.get(Calendar.SUNDAY).values()) {
                        times.add(time);
                    }
                    timesEditAdapter timeEditAdapter = new timesEditAdapter(getContext(), times);
                    timesRecycler.setAdapter(timeEditAdapter);
                    timesRecycler.setVisibility(View.VISIBLE);
                }else{
                    timesRecycler.setVisibility(View.INVISIBLE);
                }
            }
        });
        day2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                chosenDayFromLookUpDialog=Calendar.MONDAY;
                setTimeListeners();
                if(timesToDays.get(Calendar.MONDAY)!=null){
                    addMoreTimes.setVisibility(View.GONE);
                }else{
                    addMoreTimes.setVisibility(View.VISIBLE);
                }
                choosenCalendarDay=Calendar.MONDAY;
                day1.setVisibility(View.VISIBLE);
                day1Filled.setVisibility(View.GONE);
                day2.setVisibility(View.GONE);
                day2Filled.setVisibility(View.VISIBLE);
                day3.setVisibility(View.VISIBLE);
                day3Filled.setVisibility(View.GONE);
                day4.setVisibility(View.VISIBLE);
                day4Filled.setVisibility(View.GONE);
                day5.setVisibility(View.VISIBLE);
                day5Filled.setVisibility(View.GONE);
                day6.setVisibility(View.VISIBLE);
                day6Filled.setVisibility(View.GONE);
                day7.setVisibility(View.VISIBLE);
                day7Filled.setVisibility(View.GONE);
                if(timesToDays.get(Calendar.MONDAY)!=null) {
                    ArrayList<TimeScheduling> times = new ArrayList<>();
                    for (TimeScheduling time : timesToDays.get(Calendar.MONDAY).values()) {
                        times.add(time);
                    }
                    timesEditAdapter timeEditAdapter = new timesEditAdapter(getContext(), times);
                    timesRecycler.setAdapter(timeEditAdapter);
                    timesRecycler.setVisibility(View.VISIBLE);
                }else{
                    timesRecycler.setVisibility(View.INVISIBLE);
                }
            }
        });
        day3.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                chosenDayFromLookUpDialog=Calendar.TUESDAY;
                setTimeListeners();
                if(timesToDays.get(Calendar.TUESDAY)!=null){
                    addMoreTimes.setVisibility(View.GONE);
                }else{
                    addMoreTimes.setVisibility(View.VISIBLE);
                }
                choosenCalendarDay=Calendar.TUESDAY;
                day1.setVisibility(View.VISIBLE);
                day1Filled.setVisibility(View.GONE);
                day2.setVisibility(View.VISIBLE);
                day2Filled.setVisibility(View.GONE);
                day3.setVisibility(View.GONE);
                day3Filled.setVisibility(View.VISIBLE);
                day4.setVisibility(View.VISIBLE);
                day4Filled.setVisibility(View.GONE);
                day5.setVisibility(View.VISIBLE);
                day5Filled.setVisibility(View.GONE);
                day6.setVisibility(View.VISIBLE);
                day6Filled.setVisibility(View.GONE);
                day7.setVisibility(View.VISIBLE);
                day7Filled.setVisibility(View.GONE);
                if(timesToDays.get(Calendar.TUESDAY)!=null) {
                    ArrayList<TimeScheduling> times = new ArrayList<>();
                    for (TimeScheduling time : timesToDays.get(Calendar.TUESDAY).values()) {
                        times.add(time);
                    }
                    timesEditAdapter timeEditAdapter = new timesEditAdapter(getContext(), times);
                    timesRecycler.setAdapter(timeEditAdapter);
                    timesRecycler.setVisibility(View.VISIBLE);
                }else{
                    timesRecycler.setVisibility(View.INVISIBLE);
                }
            }
        });
        day4.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                chosenDayFromLookUpDialog=Calendar.WEDNESDAY;
                setTimeListeners();
                if(timesToDays.get(Calendar.WEDNESDAY)!=null){
                    addMoreTimes.setVisibility(View.GONE);
                }else{
                    addMoreTimes.setVisibility(View.VISIBLE);
                }
                choosenCalendarDay=Calendar.WEDNESDAY;
                day1.setVisibility(View.VISIBLE);
                day1Filled.setVisibility(View.GONE);
                day2.setVisibility(View.VISIBLE);
                day2Filled.setVisibility(View.GONE);
                day3.setVisibility(View.VISIBLE);
                day3Filled.setVisibility(View.GONE);
                day4.setVisibility(View.GONE);
                day4Filled.setVisibility(View.VISIBLE);
                day5.setVisibility(View.VISIBLE);
                day5Filled.setVisibility(View.GONE);
                day6.setVisibility(View.VISIBLE);
                day6Filled.setVisibility(View.GONE);
                day7.setVisibility(View.VISIBLE);
                day7Filled.setVisibility(View.GONE);
                if(timesToDays.get(Calendar.WEDNESDAY)!=null) {
                    ArrayList<TimeScheduling> times = new ArrayList<>();
                    for (TimeScheduling time : timesToDays.get(Calendar.WEDNESDAY).values()) {
                        times.add(time);
                    }
                    timesEditAdapter timeEditAdapter = new timesEditAdapter(getContext(), times);
                    timesRecycler.setAdapter(timeEditAdapter);
                    timesRecycler.setVisibility(View.VISIBLE);
                }else{
                    timesRecycler.setVisibility(View.INVISIBLE);
                }
            }
        });
        day5.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                chosenDayFromLookUpDialog=Calendar.THURSDAY;
                setTimeListeners();
                if(timesToDays.get(Calendar.THURSDAY)!=null){
                    addMoreTimes.setVisibility(View.GONE);
                }else{
                    addMoreTimes.setVisibility(View.VISIBLE);
                }
                choosenCalendarDay=Calendar.THURSDAY;
                day1.setVisibility(View.VISIBLE);
                day1Filled.setVisibility(View.GONE);
                day2.setVisibility(View.VISIBLE);
                day2Filled.setVisibility(View.GONE);
                day3.setVisibility(View.VISIBLE);
                day3Filled.setVisibility(View.GONE);
                day4.setVisibility(View.VISIBLE);
                day4Filled.setVisibility(View.GONE);
                day5.setVisibility(View.GONE);
                day5Filled.setVisibility(View.VISIBLE);
                day6.setVisibility(View.VISIBLE);
                day6Filled.setVisibility(View.GONE);
                day7.setVisibility(View.VISIBLE);
                day7Filled.setVisibility(View.GONE);
                if(timesToDays.get(Calendar.THURSDAY)!=null) {
                    ArrayList<TimeScheduling> times = new ArrayList<>();
                    for (TimeScheduling time : timesToDays.get(Calendar.THURSDAY).values()) {
                        times.add(time);
                    }
                    timesEditAdapter timeEditAdapter = new timesEditAdapter(getContext(), times);
                    timesRecycler.setAdapter(timeEditAdapter);
                    timesRecycler.setVisibility(View.VISIBLE);
                }else{
                    timesRecycler.setVisibility(View.INVISIBLE);
                }
            }
        });
        day6.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                chosenDayFromLookUpDialog=Calendar.FRIDAY;
                setTimeListeners();
                if(timesToDays.get(Calendar.FRIDAY)!=null){
                    addMoreTimes.setVisibility(View.GONE);
                }else{
                    addMoreTimes.setVisibility(View.VISIBLE);
                }
                choosenCalendarDay=Calendar.FRIDAY;
                day1.setVisibility(View.VISIBLE);
                day1Filled.setVisibility(View.GONE);
                day2.setVisibility(View.VISIBLE);
                day2Filled.setVisibility(View.GONE);
                day3.setVisibility(View.VISIBLE);
                day3Filled.setVisibility(View.GONE);
                day4.setVisibility(View.VISIBLE);
                day4Filled.setVisibility(View.GONE);
                day5.setVisibility(View.VISIBLE);
                day5Filled.setVisibility(View.GONE);
                day6.setVisibility(View.GONE);
                day6Filled.setVisibility(View.VISIBLE);
                day7.setVisibility(View.VISIBLE);
                day7Filled.setVisibility(View.GONE);
                ArrayList<TimeScheduling> times=new ArrayList<>();
                if(timesToDays.get(Calendar.FRIDAY)!=null) {
                    for (TimeScheduling time : timesToDays.get(Calendar.FRIDAY).values()) {
                        times.add(time);
                    }
                    timesEditAdapter timeEditAdapter = new timesEditAdapter(getContext(), times);
                    timesRecycler.setAdapter(timeEditAdapter);
                    timesRecycler.setVisibility(View.VISIBLE);
                }else{
                    timesRecycler.setVisibility(View.INVISIBLE);
                }
            }
        });
        day7.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                chosenDayFromLookUpDialog=Calendar.SATURDAY;
                setTimeListeners();
                if(timesToDays.get(Calendar.SATURDAY)!=null){
                    addMoreTimes.setVisibility(View.GONE);
                }else{
                    addMoreTimes.setVisibility(View.VISIBLE);
                }
                choosenCalendarDay=Calendar.SATURDAY;
                day1.setVisibility(View.VISIBLE);
                day1Filled.setVisibility(View.GONE);
                day2.setVisibility(View.VISIBLE);
                day2Filled.setVisibility(View.GONE);
                day3.setVisibility(View.VISIBLE);
                day3Filled.setVisibility(View.GONE);
                day4.setVisibility(View.VISIBLE);
                day4Filled.setVisibility(View.GONE);
                day5.setVisibility(View.VISIBLE);
                day5Filled.setVisibility(View.GONE);
                day6.setVisibility(View.VISIBLE);
                day6Filled.setVisibility(View.GONE);
                day7.setVisibility(View.GONE);
                day7Filled.setVisibility(View.VISIBLE);

                ArrayList<TimeScheduling> times=new ArrayList<>();
                if(timesToDays.get(Calendar.SATURDAY)!=null) {
                    for (TimeScheduling time : timesToDays.get(Calendar.SATURDAY).values()) {
                        times.add(time);
                    }
                    timesEditAdapter timeEditAdapter = new timesEditAdapter(getContext(), times);
                    timesRecycler.setAdapter(timeEditAdapter);
                    timesRecycler.setVisibility(View.VISIBLE);
                }else{
                    timesRecycler.setVisibility(View.INVISIBLE);
                }
            }
        });

        lookUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lookUpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                lookUpDialog.show();
            }
        });
    }



    private void setChooseDayListener() {
        if(isSameHoursDays){
            day1Check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        sameChoosenCalendarDays.add(Calendar.SUNDAY);
                    }else{
                        if(sameChoosenCalendarDays.contains(Calendar.SUNDAY)){
                            sameChoosenCalendarDays.remove(new Integer(Calendar.SUNDAY));
                        }
                    }
                }
            });
            day2Check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        sameChoosenCalendarDays.add(Calendar.MONDAY);
                    }else{
                        if(sameChoosenCalendarDays.contains(Calendar.MONDAY)){
                            sameChoosenCalendarDays.remove(new Integer(Calendar.MONDAY));
                        }
                    }
                }
            });
            day3Check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        sameChoosenCalendarDays.add(Calendar.TUESDAY);
                    }else{
                        if(sameChoosenCalendarDays.contains(Calendar.TUESDAY)){
                            sameChoosenCalendarDays.remove(new Integer(Calendar.TUESDAY));
                        }
                    }
                }
            });
            day4Check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        sameChoosenCalendarDays.add(Calendar.WEDNESDAY);
                    }else{
                        if(sameChoosenCalendarDays.contains(Calendar.WEDNESDAY)){
                            sameChoosenCalendarDays.remove(new Integer(Calendar.WEDNESDAY));
                        }
                    }
                }
            });
            day5Check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        sameChoosenCalendarDays.add(Calendar.THURSDAY);
                    }else{
                        if(sameChoosenCalendarDays.contains(Calendar.THURSDAY)){
                            sameChoosenCalendarDays.remove(new Integer(Calendar.THURSDAY));
                        }
                    }
                }
            });
            day6Check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        sameChoosenCalendarDays.add(Calendar.FRIDAY);
                    }else{
                        if(sameChoosenCalendarDays.contains(Calendar.FRIDAY)){
                            sameChoosenCalendarDays.remove(new Integer(Calendar.FRIDAY));
                        }
                    }
                }
            });
            day7Check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        sameChoosenCalendarDays.add(Calendar.SATURDAY);
                    }else{
                        if(sameChoosenCalendarDays.contains(Calendar.SATURDAY)){
                            sameChoosenCalendarDays.remove(new Integer(Calendar.SATURDAY));
                        }
                    }
                }
            });
        }else {
            day1.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {
                    setTimeListeners();
                    if(timesToDays.get(Calendar.SUNDAY)!=null){
                        addMoreTimes.setVisibility(View.GONE);
                    }else{
                        addMoreTimes.setVisibility(View.VISIBLE);
                    }
                    choosenCalendarDay=Calendar.SUNDAY;
                    day1.setVisibility(View.GONE);
                    day1Filled.setVisibility(View.VISIBLE);
                    day2.setVisibility(View.VISIBLE);
                    day2Filled.setVisibility(View.GONE);
                    day3.setVisibility(View.VISIBLE);
                    day3Filled.setVisibility(View.GONE);
                    day4.setVisibility(View.VISIBLE);
                    day4Filled.setVisibility(View.GONE);
                    day5.setVisibility(View.VISIBLE);
                    day5Filled.setVisibility(View.GONE);
                    day6.setVisibility(View.VISIBLE);
                    day6Filled.setVisibility(View.GONE);
                    day7.setVisibility(View.VISIBLE);
                    day7Filled.setVisibility(View.GONE);
                }
            });
            day2.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {
                    setTimeListeners();
                    if(timesToDays.get(Calendar.MONDAY)!=null){
                        addMoreTimes.setVisibility(View.GONE);
                    }else{
                        addMoreTimes.setVisibility(View.VISIBLE);
                    }
                    choosenCalendarDay=Calendar.MONDAY;
                    day1.setVisibility(View.VISIBLE);
                    day1Filled.setVisibility(View.GONE);
                    day2.setVisibility(View.GONE);
                    day2Filled.setVisibility(View.VISIBLE);
                    day3.setVisibility(View.VISIBLE);
                    day3Filled.setVisibility(View.GONE);
                    day4.setVisibility(View.VISIBLE);
                    day4Filled.setVisibility(View.GONE);
                    day5.setVisibility(View.VISIBLE);
                    day5Filled.setVisibility(View.GONE);
                    day6.setVisibility(View.VISIBLE);
                    day6Filled.setVisibility(View.GONE);
                    day7.setVisibility(View.VISIBLE);
                    day7Filled.setVisibility(View.GONE);
                }
            });
            day3.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {
                    setTimeListeners();
                    if(timesToDays.get(Calendar.TUESDAY)!=null){
                        addMoreTimes.setVisibility(View.GONE);
                    }else{
                        addMoreTimes.setVisibility(View.VISIBLE);
                    }
                    choosenCalendarDay=Calendar.TUESDAY;
                    day1.setVisibility(View.VISIBLE);
                    day1Filled.setVisibility(View.GONE);
                    day2.setVisibility(View.VISIBLE);
                    day2Filled.setVisibility(View.GONE);
                    day3.setVisibility(View.GONE);
                    day3Filled.setVisibility(View.VISIBLE);
                    day4.setVisibility(View.VISIBLE);
                    day4Filled.setVisibility(View.GONE);
                    day5.setVisibility(View.VISIBLE);
                    day5Filled.setVisibility(View.GONE);
                    day6.setVisibility(View.VISIBLE);
                    day6Filled.setVisibility(View.GONE);
                    day7.setVisibility(View.VISIBLE);
                    day7Filled.setVisibility(View.GONE);
                }
            });
            day4.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {
                    setTimeListeners();
                    if(timesToDays.get(Calendar.WEDNESDAY)!=null){
                        addMoreTimes.setVisibility(View.GONE);
                    }else{
                        addMoreTimes.setVisibility(View.VISIBLE);
                    }
                    choosenCalendarDay=Calendar.WEDNESDAY;
                    day1.setVisibility(View.VISIBLE);
                    day1Filled.setVisibility(View.GONE);
                    day2.setVisibility(View.VISIBLE);
                    day2Filled.setVisibility(View.GONE);
                    day3.setVisibility(View.VISIBLE);
                    day3Filled.setVisibility(View.GONE);
                    day4.setVisibility(View.GONE);
                    day4Filled.setVisibility(View.VISIBLE);
                    day5.setVisibility(View.VISIBLE);
                    day5Filled.setVisibility(View.GONE);
                    day6.setVisibility(View.VISIBLE);
                    day6Filled.setVisibility(View.GONE);
                    day7.setVisibility(View.VISIBLE);
                    day7Filled.setVisibility(View.GONE);
                }
            });
            day5.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {
                    setTimeListeners();
                    if(timesToDays.get(Calendar.THURSDAY)!=null){
                        addMoreTimes.setVisibility(View.GONE);
                    }else{
                        addMoreTimes.setVisibility(View.VISIBLE);
                    }
                    choosenCalendarDay=Calendar.THURSDAY;
                    day1.setVisibility(View.VISIBLE);
                    day1Filled.setVisibility(View.GONE);
                    day2.setVisibility(View.VISIBLE);
                    day2Filled.setVisibility(View.GONE);
                    day3.setVisibility(View.VISIBLE);
                    day3Filled.setVisibility(View.GONE);
                    day4.setVisibility(View.VISIBLE);
                    day4Filled.setVisibility(View.GONE);
                    day5.setVisibility(View.GONE);
                    day5Filled.setVisibility(View.VISIBLE);
                    day6.setVisibility(View.VISIBLE);
                    day6Filled.setVisibility(View.GONE);
                    day7.setVisibility(View.VISIBLE);
                    day7Filled.setVisibility(View.GONE);
                }
            });
            day6.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {
                    setTimeListeners();
                    if(timesToDays.get(Calendar.FRIDAY)!=null){
                        addMoreTimes.setVisibility(View.GONE);
                    }else{
                        addMoreTimes.setVisibility(View.VISIBLE);
                    }
                    choosenCalendarDay=Calendar.FRIDAY;
                    day1.setVisibility(View.VISIBLE);
                    day1Filled.setVisibility(View.GONE);
                    day2.setVisibility(View.VISIBLE);
                    day2Filled.setVisibility(View.GONE);
                    day3.setVisibility(View.VISIBLE);
                    day3Filled.setVisibility(View.GONE);
                    day4.setVisibility(View.VISIBLE);
                    day4Filled.setVisibility(View.GONE);
                    day5.setVisibility(View.VISIBLE);
                    day5Filled.setVisibility(View.GONE);
                    day6.setVisibility(View.GONE);
                    day6Filled.setVisibility(View.VISIBLE);
                    day7.setVisibility(View.VISIBLE);
                    day7Filled.setVisibility(View.GONE);
                }
            });
            day7.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {
                    setTimeListeners();
                    if(timesToDays.get(Calendar.SATURDAY)!=null){
                        addMoreTimes.setVisibility(View.GONE);
                    }else{
                        addMoreTimes.setVisibility(View.VISIBLE);
                    }
                    choosenCalendarDay=Calendar.SATURDAY;
                    day1.setVisibility(View.VISIBLE);
                    day1Filled.setVisibility(View.GONE);
                    day2.setVisibility(View.VISIBLE);
                    day2Filled.setVisibility(View.GONE);
                    day3.setVisibility(View.VISIBLE);
                    day3Filled.setVisibility(View.GONE);
                    day4.setVisibility(View.VISIBLE);
                    day4Filled.setVisibility(View.GONE);
                    day5.setVisibility(View.VISIBLE);
                    day5Filled.setVisibility(View.GONE);
                    day6.setVisibility(View.VISIBLE);
                    day6Filled.setVisibility(View.GONE);
                    day7.setVisibility(View.GONE);
                    day7Filled.setVisibility(View.VISIBLE);
                }
            });
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setTimeListeners() {
        final Calendar cStart = Calendar.getInstance();
        int mHour = cStart.get(Calendar.HOUR_OF_DAY);
        int mMinute = cStart.get(Calendar.MINUTE);
        strtTimeEditText.setText(String.format(Constants.timePrintingFormat,  mHour ,mMinute));
        Calendar cc=Calendar.getInstance();
        cc.set(Calendar.HOUR_OF_DAY,mHour);
        cc.set(Calendar.MINUTE,mMinute);
        cc.set(Calendar.SECOND,0);
        newStartTime=cc.getTime();
        strtTimeEditText.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                // Process to get Current Time
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog tpd = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                // Display Selected time in textbox
                                strtTimeEditText.setText(String.format(Constants.timePrintingFormat,  hourOfDay ,minute));
                                Calendar cc=Calendar.getInstance();
                                cc.set(Calendar.HOUR_OF_DAY,hourOfDay);
                                cc.set(Calendar.MINUTE,minute);
                                cc.set(Calendar.SECOND,0);
                                newStartTime=cc.getTime();
                            }
                        }, mHour, mMinute, true);
                tpd.show();

            }
        });

        final Calendar cEnd = Calendar.getInstance();
        int mHoureEnd = cEnd.get(Calendar.HOUR_OF_DAY);
        int mMinuteEnd = cEnd.get(Calendar.MINUTE);
        endtimeEditText.setText(String.format(Constants.timePrintingFormat,  mHoureEnd ,mMinuteEnd));
        Calendar ee=Calendar.getInstance();
        ee.set(Calendar.HOUR_OF_DAY,mHoureEnd);
        ee.set(Calendar.MINUTE,mMinuteEnd);
        ee.set(Calendar.SECOND,0);
        newEndtime=ee.getTime();
        endtimeEditText.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                // Process to get Current Time
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);
                // Launch Time Picker Dialog
                TimePickerDialog tpd = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                // Display Selected time in textbox
                                endtimeEditText.setText(String.format(Constants.timePrintingFormat,  hourOfDay ,minute));
                                Calendar cc=Calendar.getInstance();
                                cc.set(Calendar.HOUR_OF_DAY,hourOfDay);
                                cc.set(Calendar.MINUTE,minute);
                                cc.set(Calendar.SECOND,0);
                                newEndtime=cc.getTime();
                            }
                        }, mHour, mMinute, true);
                tpd.show();

            }
        });
    }

    private void setAddMoreTimesListner() {
        addMoreTimes.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                LinkedHashMap<Integer, TimeScheduling> timesToCustomSchedule=new LinkedHashMap<>();
                if(!isSameHoursDays){
                    if(choosenCalendarDay==null){
                        new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("בחר יום קודם")
                                .setConfirmText("בסדר")
                                .show();
                        return;
                    }
                }else{
                    if (sameChoosenCalendarDays.isEmpty())
                    {
                        new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("בחר ימים קודם")
                                .setConfirmText("בסדר")
                                .show();
                        return;
                    }
                }

                if(newEndtime.getTime()/60000-newStartTime.getTime()/60000<lessonIntervalTime){
                    new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("אורך הזמנים שבחרתה פחות מזמן שיעור אחד לפחות")
                            .setConfirmText("בסדר")
                            .show();
                }else {
                    int countTimesAdded=0;
                    while (newEndtime.getTime() / 60000 - newStartTime.getTime() / 60000 >= lessonIntervalTime) {
                        TimeScheduling timeToAdd = new TimeScheduling();
                        timeToAdd.setStartTime(newStartTime);
                        if (newStartTime.getMinutes() + lessonIntervalTime >= 60) {
                            Calendar cal2 = Calendar.getInstance();
                            cal2.setTime(newStartTime);
                            cal2.add(Calendar.MINUTE, lessonIntervalTime - 60);
                            cal2.add(Calendar.HOUR_OF_DAY, 1);
                            newStartTime = cal2.getTime();
                            timeToAdd.setEndTime(newStartTime);
                        } else {
                            Calendar cal2 = Calendar.getInstance();
                            cal2.setTime(newStartTime);
                            cal2.add(Calendar.MINUTE, lessonIntervalTime);
                            newStartTime = cal2.getTime();
                            timeToAdd.setEndTime(newStartTime);
                        }
                        if(!isSameHoursDays){

                            if(timesToDays.get(choosenCalendarDay)!=null && !timesToDays.get(choosenCalendarDay).isEmpty()) {

                            }
                                timesToCustomSchedule.put(countTimesAdded, timeToAdd);
                                countTimesAdded++;

                        }else{
                            timesToCustomSchedule.put(countTimesAdded, timeToAdd);
                            countTimesAdded++;
                        }

                    }
                    if (!isSameHoursDays) {
                        timesToDays.put(choosenCalendarDay, timesToCustomSchedule);
                        addMoreTimes.setVisibility(View.GONE);
                    }
                    else{
                        for(Integer day: sameChoosenCalendarDays){
                            timesToDays.put(day,timesToCustomSchedule);
                        }
                        addMoreTimes.setVisibility(View.GONE);
                    }
                    timesFeedback.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setSaveListener() {
        save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if(drivingScoolSpinner.getText().toString().isEmpty()){
                    new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("בחר בית ספר קודם")
                            .show();
                    return;
                }else {
                    if (autoCompleteRepetition.getText().toString().isEmpty()) {
                        new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("בחר חזרות קודם")
                                .show();
                        return;
                    }
                }
                setTimeSchedulingsCompletly();
                if(!totalTimesToAdd.isEmpty())
                new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("שמירת זמני שיעורים")
                        .setContentText("בלחיצה על כפתור 'כן' את משנה את כל הזמנים הישנים לחדשים חוץ ממה שנעשה עליהם שיבוץ מתלמיד לשבוע הבא רוצה להמשיך?")
                        .setConfirmText("כן המשך").setCancelText("ביטל")
                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                                pDialog.show();
                                saveDateTimes(new progressAddingSchedule() {
                                    private int couterSchoolToAdd;
                                    private int counterToRemove;
                                    int sumDays;
                                    int sumTimesAtDay;
                                    int sumOtherDaysRemoved;
                                    int timesAdded;
                                    @Override
                                    public void setSumDays(int sumDays) {
                                        this.sumDays=sumDays;
                                    }
                                    public int getSumDays(){
                                        return this.sumDays;
                                    }

                                    public int getSumTimesAtDay() {
                                        return sumTimesAtDay;
                                    }

                                    public void setSumTimesAtDay(int sumTimesAtDay) {
                                        this.sumTimesAtDay = sumTimesAtDay;
                                    }

                                    public int getSumOtherDaysRemoved() {
                                        return sumOtherDaysRemoved;
                                    }

                                    public void setSumOtherDaysRemoved(int sumOtherDaysRemoved) {
                                        this.sumOtherDaysRemoved = sumOtherDaysRemoved;
                                    }

                                    public void removeOneDayIfEmpty(final DocumentReference scheduleDayRef){
                                        scheduleDayRef.collection(Constants.oneDayeSchedules).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(!task.isSuccessful() || task.getResult().isEmpty()) {
                                                    scheduleDayRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                    public void showTimesScheduledByPupil(){
                                        teacherScheduledPupil mDialog=new teacherScheduledPupil();
                                        mDialog.onCreate(getContext(),getActivity());
                                    }
                                    public void addNewTimes(){
                                        timesAdded=0;
                                        for (final TimeScheduling timeToAdd : totalTimesToAdd) {
                                            Schedule scheduleToAdd = new Schedule();
                                            scheduleToAdd.setScheduleDate(timeToAdd.getDate());
                                                for (final String schoolId : schoolIdsToSchedule) {
                                                    db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers)
                                                            .document(teacherId).collection(Constants.schedules).document(timeToAdd.getDate().toString()).set(
                                                            scheduleToAdd).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            timeToAdd.setSchoolUId(schoolId);
                                                            db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(teacherId).collection(
                                                                    Constants.schedules).document(timeToAdd.getDate().toString()).collection(Constants.oneDayeSchedules).document(
                                                                    timeToAdd.getStartTime().toString()).set(timeToAdd).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        timesAdded++;
                                                                        if (timesAdded == totalTimesToAdd.size() * schoolIdsToSchedule.size() ) {
                                                                            timesToDays = new HashMap<>();
                                                                            pDialog.dismiss();

                                                                            schoolIdsToSchedule=new ArrayList<>();
                                                                            totalTimesToAdd=new ArrayList<>();
                                                                            timesHasBeenScheduled=new ArrayList<>();
                                                                            timesToDays=new HashMap<>();
                                                                            new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                                                                                    .setTitleText("Good job!")
                                                                                    .setContentText("הזמנים התעדכנו")
                                                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                                        @Override
                                                                                        public void onClick(SweetAlertDialog sDialog) {
                                                                                            sDialog.dismissWithAnimation();

                                                                                            teacherScheduling myFragment = new teacherScheduling();
                                                                                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
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
                                    }
                                    public void removeNotScheduledDays(){
                                        Calendar c = Calendar.getInstance();
                                        c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                                        c.set(Calendar.HOUR_OF_DAY,23);
                                        c.set(Calendar.MINUTE,59);
                                        c.set(Calendar.SECOND,59);
                                        counterToRemove=schoolIdsToSchedule.size();
                                            for (String schoolId : schoolIdsToSchedule) {
                                                db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(teacherId)
                                                        .collection(Constants.schedules).whereGreaterThan(Constants.scheduleDate, c.getTime()).get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            private int sumOtherDays;
                                                            @Override
                                                            public void onComplete(@NonNull final Task<QuerySnapshot> task0) {
                                                                if (task0.isSuccessful() && !task0.getResult().isEmpty()) {
                                                                    sumOtherDays=task0.getResult().size();
                                                                    for (final QueryDocumentSnapshot doc : task0.getResult()) {
                                                                        doc.getReference().collection(Constants.oneDayeSchedules).get().addOnCompleteListener(
                                                                                new OnCompleteListener<QuerySnapshot>() {
                                                                                    private int counterOneDayTimes;

                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                        if(task.isSuccessful() && !task.getResult().isEmpty()){
                                                                                            counterOneDayTimes=task.getResult().size();
                                                                                            for(QueryDocumentSnapshot doc2:task.getResult()){
                                                                                                doc2.getReference().delete().addOnSuccessListener(
                                                                                                        new OnSuccessListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onSuccess(Void aVoid) {
                                                                                                                counterOneDayTimes--;
                                                                                                                if(counterOneDayTimes==0){
                                                                                                                    doc.getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {

                                                                                                                        @Override
                                                                                                                        public void onSuccess(Void aVoid) {
                                                                                                                            sumOtherDays--;
                                                                                                                            if (sumOtherDays == 0) {
                                                                                                                                counterToRemove--;
                                                                                                                            }
                                                                                                                            if (counterToRemove == 0 && sumOtherDays == 0) {
                                                                                                                                if (timesHasBeenScheduled != null && !timesHasBeenScheduled.isEmpty()) {
                                                                                                                                    pDialog.dismiss();
                                                                                                                                    showTimesScheduledByPupil();
                                                                                                                                } else {
                                                                                                                                    addNewTimes();
                                                                                                                                }
                                                                                                                            }
                                                                                                                        }
                                                                                                                    });
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                );
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                        );

                                                                    }
                                                                } else {
                                                                    counterToRemove--;
                                                                    if (counterToRemove == 0 && timesHasBeenScheduled != null && !timesHasBeenScheduled.isEmpty()) {
                                                                        pDialog.dismiss();
                                                                        showTimesScheduledByPupil();
                                                                    } else {
                                                                        if(counterToRemove == 0 )
                                                                        addNewTimes();
                                                                    }
                                                                }
                                                            }
                                                        });
                                            }
                                    }
                                });
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                                pDialog.dismiss();
                            }
                        }).show();
                else
                    new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("לא התווסף זמנים")
                            .show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void saveDateTimes(final progressAddingSchedule progessInterface) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        c.set(Calendar.HOUR_OF_DAY,23);
        c.set(Calendar.MINUTE,59);
        c.set(Calendar.SECOND,59);

        Calendar today=Calendar.getInstance();
        today.set(Calendar.DAY_OF_WEEK,today.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY?Calendar.SATURDAY:today.get(Calendar.DAY_OF_WEEK)-1);
        today.set(Calendar.HOUR_OF_DAY,23);
        today.set(Calendar.MINUTE,59);
        today.set(Calendar.SECOND,59);
        countSchools=schoolIdsToSchedule.size();
            for(String schoolId : schoolIdsToSchedule)
            db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(teacherId)
                    .collection(Constants.schedules).whereLessThanOrEqualTo(Constants.scheduleDate, c.getTime()).whereGreaterThanOrEqualTo
                    (Constants.scheduleDate,today.getTime()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        private int sumDays;
                        @Override
                        public void onComplete(@NonNull final Task<QuerySnapshot> task0) {
                            if (task0.isSuccessful() && !task0.getResult().isEmpty()) {
                                sumDays=task0.getResult().size();
                                for (final QueryDocumentSnapshot doc : task0.getResult()) {

                                    doc.getReference().collection(Constants.oneDayeSchedules).get().addOnCompleteListener(
                                            new OnCompleteListener<QuerySnapshot>() {
                                                private int sumtimesAtDay;
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                                        sumtimesAtDay=task.getResult().size();
                                                        for (QueryDocumentSnapshot doc1 : task.getResult()) {
                                                            TimeScheduling time = doc1.toObject(TimeScheduling.class);
                                                            if (time.getPupilUId() != null) {
                                                                timesHasBeenScheduled.add(time);
                                                               sumtimesAtDay--;
                                                               if(sumtimesAtDay==0){
                                                                   sumDays--;
                                                               }
                                                               if( sumDays == 0 && sumtimesAtDay == 0)
                                                                {
                                                                    countSchools--;
                                                                }
                                                                if (countSchools==0 &&  sumDays == 0 && sumtimesAtDay == 0) {
                                                                    progessInterface.removeNotScheduledDays();

                                                                }
                                                            } else {
                                                                doc1.getReference().delete().addOnSuccessListener(
                                                                        new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                progessInterface.removeOneDayIfEmpty(doc.getReference());
                                                                                sumtimesAtDay--;
                                                                                if(sumtimesAtDay==0){
                                                                                    sumDays--;
                                                                                }
                                                                                if( sumDays == 0 && progessInterface.getSumTimesAtDay() == 0) {
                                                                                    countSchools--;
                                                                                }
                                                                                if (countSchools==0 &&  sumDays == 0 && sumtimesAtDay==0) {
                                                                                    progessInterface.removeNotScheduledDays();

                                                                                }
                                                                            }

                                                                        });
                                                            }

                                                        }
                                                    }
                                                }
                                            }
                                    );
                                }

                            } else {
                                countSchools--;
                                if(countSchools==0)
                                progessInterface.removeNotScheduledDays();
                            }
                        }
                    });
        }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setTimeSchedulingsCompletly() {
        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        for (int i = 0; i < timesRepition.intValue() * 7; i++) {
            Integer day=cal.get(Calendar.DAY_OF_WEEK);
            if (timesToDays.get(day) != null && !timesToDays.get(day).isEmpty()) {
                for (TimeScheduling time : timesToDays.get(day).values()) {
                    Calendar startCal = Calendar.getInstance();
                    Calendar endCal = Calendar.getInstance();
                    Calendar calDate = Calendar.getInstance();

                    startCal.setTime(time.getStartTime());
                    endCal.setTime(time.getEndTime());
                    calDate = cal;

                    startCal.set(Calendar.YEAR, cal.get(Calendar.YEAR));
                    startCal.set(Calendar.MONTH, cal.get(Calendar.MONTH));
                    startCal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR));

                    endCal.set(Calendar.YEAR, cal.get(Calendar.YEAR));
                    endCal.set(Calendar.MONTH, cal.get(Calendar.MONTH));
                    endCal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR));

                    calDate.set(Calendar.HOUR_OF_DAY, 0);
                    calDate.set(Calendar.MINUTE, 0);
                    calDate.set(Calendar.SECOND, 0);

                    TimeScheduling finalTimeToAdd = new TimeScheduling();
                    finalTimeToAdd.setTeacherUId(teacherId);
                    Date startTime = startCal.getTime();
                    Date endTime = endCal.getTime();
                    Date date = calDate.getTime();
                    finalTimeToAdd.setStartTime(startTime);
                    finalTimeToAdd.setEndTime(endTime);
                    finalTimeToAdd.setDate(date);
                    Calendar now=Calendar.getInstance();
                    if(!finalTimeToAdd.getStartTime().before(now.getTime()))
                    totalTimesToAdd.add(finalTimeToAdd);
                }
            }

            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    private void setSwitchSameDayeHours() {
        sameDaysSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sameHours.setVisibility(View.VISIBLE);
                    differentHours.setVisibility(View.GONE);
                    timesToDays=new HashMap<>();
                    isSameHoursDays=true;
                    if(!timesToDays.isEmpty() && timesToDays.get(sameChoosenCalendarDays.get(0))!=null){
                        addMoreTimes.setVisibility(View.GONE);
                    }
                    setTimeListeners();
                    setChooseDayListener();
                }else{
                    sameHours.setVisibility(View.GONE);
                    timesToDays=new HashMap<>();
                    differentHours.setVisibility(View.VISIBLE);
                    isSameHoursDays=false;
                    setChooseDayListener();
                    setTimeListeners();
                }
            }
        });
        sameDaysSwitch.setChecked(true);
    }

    private void setRepitionDropDown() {
        repetionMap=new HashMap<>();
        repetionMap.put("רק שבוע",1);
        repetionMap.put("2 שבועות",2);
        repetionMap.put("3 שבועות",3);
        repetionMap.put("4 שבועות",4);
        String[] rep={"4 שבועות","3 שבועות","2 שבועות","רק שבוע"};
        autoCompleteRepetition.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               timesRepition=repetionMap.get(autoCompleteRepetition.getAdapter().getItem(position).toString());
            }
        });
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(
                        teacherPagesNavigator.context,
                        R.layout.drop_down_layout,
                        rep);
        autoCompleteRepetition.setAdapter(adapter);
        autoCompleteRepetition.setSelection(0);
    }

    private void setSchoolDropDown() {
        drivingScoolSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                drivingScoolSpinner.setText(drivingScoolSpinner.getAdapter().getItem(position).toString());
                if(drivingScoolSpinner.getAdapter().getItem(position).toString().equals(Constants.allSchools)){
                    schoolIdsToSchedule=new ArrayList<>();
                  for(int i=1;i<drivingScoolSpinner.getAdapter().getCount();i++){
                      schoolIdsToSchedule.add(schoolNameMappingUId.get(drivingScoolSpinner.getAdapter().getItem(i).toString()).toString());
                  }
                }
                else{
                   schoolIdsToSchedule=new ArrayList<>();
                   schoolIdsToSchedule.add(schoolNameMappingUId.get(drivingScoolSpinner.getAdapter().getItem(position).toString()).toString());
                }
        }
        });
        db.collection(Constants.UIDS).document(teacherId).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    private int counter;

                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult().exists()) {
                            final ArrayList<String> schoolNames=new ArrayList<>();
                            schoolNames.add(Constants.allSchools);
                            final ArrayList<String> schoolUIds=(ArrayList<String>) task.getResult().get(Constants.schoolsUIDs);
                            counter=schoolUIds.size();
                            schoolNameMappingUId = new HashMap<>();
                            for(final String schoolId:schoolUIds) {
                                db.collection(Constants.drivingSchool).document(schoolId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful() && task.getResult().exists()){
                                            schoolNames.add(task.getResult().get(Constants.schoolName).toString());
                                            schoolNameMappingUId.put(task.getResult().get(Constants.schoolName).toString(),schoolId);
                                            counter--;
                                            if(counter==0){
                                                ArrayAdapter<String> adapter =
                                                        new ArrayAdapter<String>(
                                                                teacherPagesNavigator.context,
                                                                R.layout.drop_down_layout,
                                                                schoolNames);
                                                drivingScoolSpinner.setAdapter(adapter);
                                                //get first school in adapter
                                                db.collection(Constants.drivingSchool).document(schoolNameMappingUId.get(drivingScoolSpinner.getAdapter().getItem(1).toString()).toString()).collection(Constants.teachers)
                                                        .document(teacherId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if(task.isSuccessful() && task.getResult().exists()){
                                                            lessonIntervalTime=Integer.parseInt(task.getResult().get(Constants.teacherLessonInterval)==null?"45":task.getResult().get(Constants.teacherLessonInterval).toString());
                                                        }else{
                                                            Log.d(TAG, "get teacher document doesnot success ");
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }
                                });
                            }

                        }
                    }

                });
    }
    }
