package teacherPackage;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.mainPages.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import adapters.teacherCalendarAdapter;
import databaseClasses.Constants;
import databaseClasses.TimeScheduling;

public class teacherCalendar extends Fragment {

    private TextView day1;
    private TextView day2;
    private TextView day3;
    private TextView day4;
    private TextView day5;
    private TextView day6;
    private TextView day7;
    private Integer todayOfWeek;
    private ImageView continueCal;
    private ImageView returnCal;
    private ImageView addSchedule;
    private TextView currentTime;
    private Date currentDateScheduling;
    private FirebaseFirestore db;
    private String teacherId,schoolId;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private int counter=0;
    private HashMap<String,ArrayList<TimeScheduling>> timesToDaysScheduling;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.teacher_calendar, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);

        timesToDaysScheduling=new HashMap<>();
        db = FirebaseFirestore.getInstance();
        teacherId=teacherPagesNavigator.teacherId;
        schoolId=teacherPagesNavigator.schoolId;

        currentDateScheduling = Calendar.getInstance().getTime();
        day1 = (TextView) rootView.findViewById(R.id.day1);
        day2 = (TextView) rootView.findViewById(R.id.day2);
        day3 = (TextView) rootView.findViewById(R.id.day3);
        day4 = (TextView) rootView.findViewById(R.id.day4);
        day5 = (TextView) rootView.findViewById(R.id.day5);
        day6 = (TextView) rootView.findViewById(R.id.day6);
        day7 = (TextView) rootView.findViewById(R.id.day7);
        addSchedule=(ImageView)rootView.findViewById(R.id.addSchedule);
        continueCal = (ImageView) rootView.findViewById(R.id.continueCal);
        returnCal = (ImageView) rootView.findViewById(R.id.returnCal);
        currentTime = (TextView) rootView.findViewById(R.id.currentTime);

        setWeakDays(currentDateScheduling);

        continueCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDateScheduling = new Date(currentDateScheduling.getTime() - 7 * 24 * 3600 * 1000);
                setListFromFirebase();
                setWeakDays(currentDateScheduling);

            }
        });
        returnCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDateScheduling = new Date(currentDateScheduling.getTime() + 7 * 24 * 3600 * 1000);
                setListFromFirebase();
                setWeakDays(currentDateScheduling);
            }
        });
        addSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                teacherScheduling scheduleFragment=new teacherScheduling();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        scheduleFragment,Constants.tagTeacherScheduling).commit();
            }
        });
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        setListFromFirebase();


        setClickListenerForTexts();
        return rootView;

    }

    public void setListFromFirebase() {
        SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd yyyy");
        try {
            currentDateScheduling = sdf.parse(sdf.format(currentDateScheduling));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final Calendar runnigDay=Calendar.getInstance();
        runnigDay.setTime(currentDateScheduling);
        runnigDay.set(Calendar.HOUR_OF_DAY,0);
        runnigDay.set(Calendar.MINUTE,0);
        runnigDay.set(Calendar.SECOND,0);
        while(runnigDay.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY){
            runnigDay.add(Calendar.DAY_OF_WEEK,-1);
        }

        timesToDaysScheduling=new HashMap<>();
        for(int i=0; i<7; i++){
                db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(teacherId).
                    collection(Constants.schedules).document(runnigDay.getTime().toString()).collection(Constants.oneDayeSchedules)
                    .orderBy(Constants.TimeScheduling_startTime, Query.Direction.ASCENDING)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    counter++;
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            ArrayList<TimeScheduling> dayScheduleList=new ArrayList<>();
                            TimeScheduling time=null;
                            int sizeOfSchedules=task.getResult().size();
                            for (QueryDocumentSnapshot doc : task.getResult()) {

                                time = doc.toObject(TimeScheduling.class);
                                if(time.isTest()){
                                    //check if only one pupil scheduled
                                    if(time.getSecondPupilUId()!=null && time.getPupilUId()!=null) {
                                        long differenceInMin = time.getEndTime().getTime() / 60000 - time.getStartTime().getTime() / 60000;
                                        int halfTimeHourAdd = ((int) differenceInMin / 2) / 24;
                                        int halfTimeMinAdd = (int) differenceInMin / 2 - 60 * halfTimeHourAdd;
                                        Calendar halftime = Calendar.getInstance();
                                        halftime.setTime(time.getStartTime());
                                        halftime.add(Calendar.HOUR_OF_DAY, halfTimeHourAdd);
                                        halftime.add(Calendar.MINUTE, halfTimeMinAdd);
                                        Date halfTimeDate = halftime.getTime();

                                        TimeScheduling firstHalfTest = new TimeScheduling();
                                        firstHalfTest.setTest(true);
                                        firstHalfTest.setTeacherUId(teacherId);
                                        firstHalfTest.setDate(time.getDate());
                                        firstHalfTest.setStartTime(time.getStartTime());
                                        firstHalfTest.setEndTime(halfTimeDate);
                                        firstHalfTest.setPupilUId(time.getPupilUId());
                                        firstHalfTest.setPupilFullName(time.getPupilFullName());
                                        firstHalfTest.setEnternalTest(time.isEnternalTest());

                                        TimeScheduling secondHalfTest = new TimeScheduling();
                                        secondHalfTest.setTest(true);
                                        secondHalfTest.setTeacherUId(teacherId);
                                        secondHalfTest.setDate(time.getDate());
                                        secondHalfTest.setStartTime(halfTimeDate);
                                        secondHalfTest.setEndTime(time.getEndTime());
                                        secondHalfTest.setPupilUId(time.getSecondPupilUId());
                                        secondHalfTest.setPupilFullName(time.getSecondpupilFullName());
                                        secondHalfTest.setEnternalTest(time.isEnternalTest());

                                        dayScheduleList.add(firstHalfTest);
                                        dayScheduleList.add(secondHalfTest);
                                    }else{
                                        dayScheduleList.add(time);
                                    }

                                }
                                else
                                {
                                    dayScheduleList.add(time);
                                }
                            }
                            Calendar cal=Calendar.getInstance();
                            cal.setTime(time.getDate());
                            switch (cal.get(Calendar.DAY_OF_WEEK)){
                                case Calendar.SUNDAY :
                                    timesToDaysScheduling.put(Constants.sunday,dayScheduleList);
                                    break;
                                case Calendar.MONDAY :
                                    timesToDaysScheduling.put(Constants.monday,dayScheduleList);
                                    break;
                                case Calendar.TUESDAY :
                                    timesToDaysScheduling.put(Constants.tuesday,dayScheduleList);
                                    break;
                                case Calendar.WEDNESDAY :
                                    timesToDaysScheduling.put(Constants.wensday,dayScheduleList);
                                    break;
                                case Calendar.THURSDAY :
                                    timesToDaysScheduling.put(Constants.thursday,dayScheduleList);
                                    break;
                                case Calendar.FRIDAY :
                                    timesToDaysScheduling.put(Constants.friday,dayScheduleList);
                                    break;
                                case Calendar.SATURDAY :
                                    timesToDaysScheduling.put(Constants.saturday,dayScheduleList);
                                    break;
                            }
                                if(counter==7){
                                counter=0;
                                    if (todayOfWeek == Calendar.SUNDAY) {
                                        day1.performClick();
                                    } else if (todayOfWeek == Calendar.MONDAY) {
                                        day2.performClick();
                                    } else if (todayOfWeek == Calendar.TUESDAY) {
                                        day3.performClick();
                                    } else if (todayOfWeek == Calendar.WEDNESDAY) {
                                        day4.performClick();
                                    } else if (todayOfWeek == Calendar.THURSDAY) {
                                        day5.performClick();
                                    } else if (todayOfWeek == Calendar.FRIDAY) {
                                        day6.performClick();
                                    } else if (todayOfWeek == Calendar.SATURDAY) {
                                        day7.performClick();
                                    }
                                }

                        }else {
                        if (counter == 7) {
                            counter = 0;
                            if (todayOfWeek == Calendar.SUNDAY) {
                                day1.performClick();
                            } else if (todayOfWeek == Calendar.MONDAY) {
                                day2.performClick();
                            } else if (todayOfWeek == Calendar.TUESDAY) {
                                day3.performClick();
                            } else if (todayOfWeek == Calendar.WEDNESDAY) {
                                day4.performClick();
                            } else if (todayOfWeek == Calendar.THURSDAY) {
                                day5.performClick();
                            } else if (todayOfWeek == Calendar.FRIDAY) {
                                day6.performClick();
                            } else if (todayOfWeek == Calendar.SATURDAY) {
                                day7.performClick();
                            }
                        }
                    }
                }
            });
            runnigDay.add(Calendar.DAY_OF_WEEK,1);
        }

    }


    public void setWeakDays(Date date) {
        // Date c = Calendar.getInstance().getTime();
        Date c = date;
        Calendar cal = Calendar.getInstance();
        cal.setTime(c);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int year = cal.get(Calendar.YEAR);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        Integer dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        String monthName = new SimpleDateFormat("MMMM").format(cal.getTime());
        currentTime.setText(monthName + " " + year);

        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

        String[] days = new String[7];
        int delta = -cal.get(GregorianCalendar.DAY_OF_WEEK) + 1; //add 2 if your week start on monday
        cal.add(Calendar.DAY_OF_MONTH, delta);
        for (int i = 0; i < 7; i++) {
            days[i] = ((Integer) cal.get(Calendar.DAY_OF_MONTH)).toString();
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        day1.setText(days[0]);
        day2.setText(days[1]);
        day3.setText(days[2]);
        day4.setText(days[3]);
        day5.setText(days[4]);
        day6.setText(days[5]);
        day7.setText(days[6]);
        todayOfWeek=dayOfWeek;

    }

    public void setClickListenerForTexts() {
        day1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                coloredTextCircular(day1);
                deleteCircleAroundTxts(day2, day3, day4, day5, day6, day7);
                int day = Integer.parseInt(day1.getText().toString());
                currentDateScheduling.setDate(day);
                teacherCalendarAdapter recyclerAdapter = new teacherCalendarAdapter(getContext(),timesToDaysScheduling.get(Constants.sunday),schoolId,teacherId);
                mRecyclerView.setAdapter(recyclerAdapter);
            }
        });
        day2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                coloredTextCircular(day2);
                deleteCircleAroundTxts(day1, day3, day4, day5, day6, day7);
                int day = Integer.parseInt(day2.getText().toString());
                currentDateScheduling.setDate(day);
                teacherCalendarAdapter recyclerAdapter = new teacherCalendarAdapter(getContext(),timesToDaysScheduling.get(Constants.monday),schoolId,teacherId);
                mRecyclerView.setAdapter(recyclerAdapter);
            }
        });
        day3.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                coloredTextCircular(day3);
                deleteCircleAroundTxts(day2, day1, day4, day5, day6, day7);
                int day = Integer.parseInt(day3.getText().toString());
                currentDateScheduling.setDate(day);
                teacherCalendarAdapter recyclerAdapter = new teacherCalendarAdapter(getContext(),timesToDaysScheduling.get(Constants.tuesday),schoolId,teacherId);
                mRecyclerView.setAdapter(recyclerAdapter);
            }
        });
        day4.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                coloredTextCircular(day4);
                deleteCircleAroundTxts(day2, day3, day1, day5, day6, day7);
                int day = Integer.parseInt(day4.getText().toString());
                currentDateScheduling.setDate(day);
                teacherCalendarAdapter recyclerAdapter = new teacherCalendarAdapter(getContext(),timesToDaysScheduling.get(Constants.wensday),schoolId,teacherId);
                mRecyclerView.setAdapter(recyclerAdapter);
            }
        });
        day5.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                coloredTextCircular(day5);
                deleteCircleAroundTxts(day2, day3, day4, day1, day6, day7);
                int day = Integer.parseInt(day5.getText().toString());
                currentDateScheduling.setDate(day);
                teacherCalendarAdapter recyclerAdapter = new teacherCalendarAdapter(getContext(),timesToDaysScheduling.get(Constants.thursday),schoolId,teacherId);
                mRecyclerView.setAdapter(recyclerAdapter);
            }
        });
        day6.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                coloredTextCircular(day6);
                deleteCircleAroundTxts(day2, day3, day4, day5, day1, day7);
                int day = Integer.parseInt(day6.getText().toString());
                currentDateScheduling.setDate(day);
                teacherCalendarAdapter recyclerAdapter = new teacherCalendarAdapter(getContext(),timesToDaysScheduling.get(Constants.friday),schoolId,teacherId);
                mRecyclerView.setAdapter(recyclerAdapter);
            }
        });
        day7.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                coloredTextCircular(day7);
                deleteCircleAroundTxts(day2, day3, day4, day5, day6, day1);
                int day = Integer.parseInt(day7.getText().toString());
                currentDateScheduling.setDate(day);
                teacherCalendarAdapter recyclerAdapter = new teacherCalendarAdapter(getContext(),timesToDaysScheduling.get(Constants.saturday),schoolId,teacherId);
                mRecyclerView.setAdapter(recyclerAdapter);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void coloredTextCircular(TextView txt) {
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.OVAL);
        gd.setColor(Color.parseColor("#FF00A5FF"));
        //  gd.setStroke(5, Color.BLUE);
        gd.setSize(txt.getWidth() * 2, txt.getHeight() * 2);
        Log.d("DB121", "Width " + (txt.getWidth() * 1.5) + "Height =" + (txt.getHeight() * 1.5));
        txt.setBackground(gd);
        txt.setTextColor(Color.WHITE);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void deleteCircleAroundTxts(TextView txt, TextView txt1, TextView txt2, TextView txt3, TextView txt4,
                                       TextView txt5) {
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.OVAL);
        gd.setColor(Color.TRANSPARENT);
        //  gd.setStroke(5, Color.BLUE);
        gd.setSize(txt.getHeight(), txt.getHeight());
        Log.d("DB121", "Width " + txt.getHeight() + "Height =" + txt.getHeight());
        txt.setBackground(gd);
        txt.setTextColor(Color.BLACK);
        txt1.setBackground(gd);
        txt1.setTextColor(Color.BLACK);
        txt2.setBackground(gd);
        txt2.setTextColor(Color.BLACK);
        txt3.setBackground(gd);
        txt3.setTextColor(Color.BLACK);
        txt4.setBackground(gd);
        txt4.setTextColor(Color.BLACK);
        txt5.setBackground(gd);
        txt5.setTextColor(Color.BLACK);
    }

}
