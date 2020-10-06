package teacherPackage;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.mainPages.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import databaseClasses.Constants;
import databaseClasses.Pupil;
import databaseClasses.Schedule;
import databaseClasses.Teacher;
import databaseClasses.TimeScheduling;
import pupilPackage.pupilLesson;
import pupilPackage.pupilPayment;
import pupilPackage.pupilTest;

public class teacherHomePage extends Fragment {
    public static TextView nearestLessonText;
    public static RecyclerView paymentRecycler;
    public static TextView totalPaysNeeded,totalPays,percentPayment;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private ImageButton rightArrow,leftArrow,call,whatup,sms,sendMessage,showLessons,showTests,showPayments,offLessons,offTests,offPayments;
    private TextView lessonOrTest,lessonNum,pupilName,date,time,teacherName ;
    public static TextView nearestPupilLessonDate,nearestPupilLesssonTime,lessonNumDown,testNumDown,nearestTestTime,nearestTestDate;
    public static RecyclerView pupilLessonsRecycler,pupilTestsRecycler;
    private ImageView pupilPhoto;
    public static ImageButton pupilAddPayment;
    public static ImageButton pupilPaymentPic;
    private LinkedHashMap<Date,TimeScheduling> nearestLesson;
    private ListView searchListView;
    private SearchView searchPupil;
    private String currentPhoneNum="";
    private int CurrentPupilOnScreen;
    private View rootView;
    private String currentPupilId;
    private AutoCompleteTextView  editTextFilledExposedDropdown;
    private HashMap<String, String> schoolNameMapping;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.teacher_home_page, container, false);
        CurrentPupilOnScreen=0;
        rightArrow=(ImageButton)rootView.findViewById(R.id.rightArrow);
        leftArrow=(ImageButton)rootView.findViewById(R.id.leftArrow);
        call=(ImageButton)rootView.findViewById(R.id.call);
        whatup=(ImageButton)rootView.findViewById(R.id.whatUp);
        sms=(ImageButton)rootView.findViewById(R.id.sms);
        sendMessage=(ImageButton)rootView.findViewById(R.id.sendMessage);

        pupilAddPayment=(ImageButton)rootView.findViewById(R.id.addPayment);
        pupilPaymentPic=(ImageButton)rootView.findViewById(R.id.paymentPic);
        showLessons=(ImageButton)rootView.findViewById(R.id.showLessons);
        showTests=(ImageButton)rootView.findViewById(R.id.showTests);
        showPayments=(ImageButton)rootView.findViewById(R.id.showPayments);
        offLessons=(ImageButton)rootView.findViewById(R.id.offLessons);
        offPayments=(ImageButton)rootView.findViewById(R.id.offPayments);
        offTests=(ImageButton)rootView.findViewById(R.id.offTests);

        totalPays=(TextView)rootView.findViewById(R.id.totalPay);
        totalPaysNeeded=(TextView)rootView.findViewById(R.id.totalPayNeede);
        percentPayment=(TextView)rootView.findViewById(R.id.percentPayment);

        pupilLessonsRecycler=(RecyclerView)rootView.findViewById(R.id.lessonRecycler);
        pupilTestsRecycler=(RecyclerView)rootView.findViewById(R.id.testRecycler);
        paymentRecycler=(RecyclerView)rootView.findViewById(R.id.paymentRecycler);

        lessonOrTest=(TextView)rootView.findViewById(R.id.lessonOrTest);
        lessonNum=(TextView)rootView.findViewById(R.id.lessonNumHead);
        pupilName=(TextView)rootView.findViewById(R.id.pupilName);
        date=(TextView)rootView.findViewById(R.id.date);
        time=(TextView)rootView.findViewById(R.id.time);
        teacherName=(TextView)rootView.findViewById(R.id.teacherName);
        testNumDown=(TextView) rootView.findViewById(R.id.testNum);
        nearestLessonText=(TextView)rootView.findViewById(R.id.t);
        lessonNumDown=(TextView)rootView.findViewById(R.id.lessonNum);
        nearestTestTime=(TextView)rootView.findViewById(R.id.timeHourTest);
        nearestTestDate=(TextView)rootView.findViewById(R.id.dateTest);

        nearestPupilLessonDate=(TextView)rootView.findViewById(R.id.dateLesson);
        nearestPupilLesssonTime=(TextView)rootView.findViewById(R.id.timeHourLesson);
        searchPupil=(SearchView)rootView.findViewById(R.id.searchPupil);
        pupilPhoto=(ImageView)rootView.findViewById(R.id.pupilIPhoto);

        searchListView=(ListView)rootView.findViewById(R.id.searchList);

        db=FirebaseFirestore.getInstance();
        storage=FirebaseStorage.getInstance();

        nearestLesson=new LinkedHashMap<>();

        setSchoolDropDown();
        getNearestLessons();
        setArrowsListener();
        setCallingListener();
        onClickWhatsApp();
        onClickSms();
        setSearchViewList();
        setImageFunctions();
        setSendMessageListener();
        teacherName.setText(teacherPagesNavigator.fullName);
        return rootView;

    }

    private void setSchoolDropDown() {
        editTextFilledExposedDropdown =
                rootView.findViewById(R.id.filled_exposed_dropdown);
        editTextFilledExposedDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editTextFilledExposedDropdown.setText(editTextFilledExposedDropdown.getAdapter().getItem(position).toString());
                teacherPagesNavigator.schoolId = schoolNameMapping.get(editTextFilledExposedDropdown.getAdapter().getItem(position).toString());
                searchPupil.setQuery("",false);
                getNearestLessons();
                Fragment currentFragment = getFragmentManager().findFragmentByTag(Constants.tagTeacherHome);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.detach(currentFragment);
                fragmentTransaction.attach(currentFragment);
                fragmentTransaction.commit();

            }
        });
        db.collection(Constants.UIDS).document(teacherPagesNavigator.teacherId).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    private int counter;

                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult().exists()) {
                            final ArrayList<String> schoolNames=new ArrayList<>();
                            final ArrayList<String> schoolUIds=(ArrayList<String>) task.getResult().get(Constants.schoolsUIDs);
                            counter=schoolUIds.size();
                            schoolNameMapping=new HashMap<>();
                            for(final String schoolId:schoolUIds) {
                                db.collection(Constants.drivingSchool).document(schoolId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful() && task.getResult().exists()){
                                            schoolNames.add(task.getResult().get(Constants.schoolName).toString());
                                            schoolNameMapping.put(task.getResult().get(Constants.schoolName).toString(),schoolId);
                                            counter--;
                                            if(counter==0){
                                                ArrayAdapter<String> adapter =
                                                        new ArrayAdapter<String>(
                                                                getContext(),
                                                                R.layout.drop_down_layout,
                                                                 schoolNames);
                                                editTextFilledExposedDropdown.setAdapter(adapter);
                                            }
                                        }
                                    }
                                });
                            }

                        }
                    }

                });




    }

    private void setSendMessageListener() {
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teacherMessage myFragment =new teacherMessage();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        ,myFragment,Constants.tagTeacherMessage).commit();
            }
        });
    }


    private void setImageFunctions() {
        offLessons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTests.setVisibility(View.GONE);
                offTests.setVisibility(View.VISIBLE);
                showPayments.setVisibility(View.GONE);
                offPayments.setVisibility(View.VISIBLE);
                offLessons.setVisibility(View.GONE);
                showLessons.setVisibility(View.VISIBLE);
                showPupilLessons();
            }
        });
        offTests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLessons.setVisibility(View.GONE);
                offLessons.setVisibility(View.VISIBLE);
                showPayments.setVisibility(View.GONE);
                offPayments.setVisibility(View.VISIBLE);
                showTests.setVisibility(View.VISIBLE);
                offTests.setVisibility(View.GONE);
                showPupilTests();
            }
        });
        offPayments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                showLessons.setVisibility(View.GONE);
                offLessons.setVisibility(View.VISIBLE);
                showTests.setVisibility(View.GONE);
                offTests.setVisibility(View.VISIBLE);
                showPayments.setVisibility(View.VISIBLE);
                offPayments.setVisibility(View.GONE);
                showPupilPayment();
            }
        });
    }

    private void showPupilPayment(){
        rootView.findViewById(R.id.myPupilLessons).setVisibility(View.GONE);
        rootView.findViewById(R.id.myPupilPayments).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.myPupilTests).setVisibility(View.GONE);
        pupilPayment myPupil=new pupilPayment();
        myPupil.onCreate(getContext(),false,currentPupilId);

    }
    private void showPupilLessons() {
        rootView.findViewById(R.id.myPupilLessons).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.myPupilPayments).setVisibility(View.GONE);
        rootView.findViewById(R.id.myPupilTests).setVisibility(View.GONE);
        pupilLesson myPupil=new pupilLesson();
        myPupil.onCreate(getContext(),teacherPagesNavigator.schoolId,teacherPagesNavigator.teacherId,currentPupilId);
    }
    private void showPupilTests(){
        rootView.findViewById(R.id.myPupilLessons).setVisibility(View.GONE);
        rootView.findViewById(R.id.myPupilPayments).setVisibility(View.GONE);
        rootView.findViewById(R.id.myPupilTests).setVisibility(View.VISIBLE);
        pupilTest myPupil=new pupilTest();
        myPupil.onCreateclass(getContext(),currentPupilId,false);
    }

    public void setSearchViewList() {
        db.collection(Constants.drivingSchool).document(teacherPagesNavigator.schoolId).collection(Constants.teachers).document(teacherPagesNavigator.teacherId)
                .collection(Constants.pupils).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                final HashMap<String,String> pupilsUId=new HashMap<>();
                if(task.isSuccessful() && !task.getResult().isEmpty()){
                    final ArrayList<String> pupilsList=new ArrayList<>();
                    for(QueryDocumentSnapshot doc:task.getResult()){
                        Pupil pupil=doc.toObject(Pupil.class);
                        pupilsList.add(pupil.getPupilName()+"  "+(pupil.getPupilPhone()==null?"":pupil.getPupilPhone()));
                        pupilsUId.put(pupil.getPupilName()+"  "+(pupil.getPupilPhone()==null?"":pupil.getPupilPhone()),doc.getId());
                    }
                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, pupilsList);
                    searchListView.setAdapter(adapter);
                    searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String pupil=searchListView.getItemAtPosition(position).toString();
                            searchPupil.setQuery(pupil, true);
                        }
                    });
                    searchPupil.setOnQueryTextListener(
                            new SearchView.OnQueryTextListener(){

                                @Override
                                public boolean onQueryTextSubmit(String query) {
                                    if (pupilsList.contains(query)) {
                                        adapter.getFilter().filter(query);
                                        setDataFromSearchView(pupilsUId.get(query.toString()));
                                        currentPupilId=pupilsUId.get(query.toString());
                                            if(offLessons.getVisibility()==View.VISIBLE){
                                                offLessons.performClick();
                                            }else{
                                                showPupilLessons();
                                            }
                                        }
                                    else {Toast.makeText(getContext(), "Not found", Toast.LENGTH_LONG).show();
                                    }
                                    searchListView.setVisibility(View.GONE);
                                    ((LinearLayout)rootView.findViewById(R.id.disableWhenSearching)).setVisibility(View.VISIBLE);
                                    ((RelativeLayout)rootView.findViewById(R.id.upperPage)).setVisibility(View.VISIBLE);
                                    return false;
                                }

                                @Override
                                public boolean onQueryTextChange(String newText) {
                                    if(searchPupil.getQuery().toString().isEmpty()){
                                        ((RelativeLayout)rootView.findViewById(R.id.upperPage)).setVisibility(View.VISIBLE);
                                        ((LinearLayout)rootView.findViewById(R.id.disableWhenSearching)).setVisibility(View.VISIBLE);
                                        searchListView.setVisibility(View.GONE);
                                    }else{
                                        ((RelativeLayout)rootView.findViewById(R.id.upperPage)).setVisibility(View.GONE);
                                        rootView.findViewById(R.id.disableWhenSearching).setVisibility(View.GONE);
                                        searchListView.setVisibility(View.VISIBLE);
                                    }
                                    adapter.getFilter().filter(newText);
                                    return false;
                                }
                            });
                }
            }
        });
    }

    private void setDataFromSearchView(String s) {
        for(Map.Entry<Date,TimeScheduling> entry:nearestLesson.entrySet()){
            if(entry.getValue().getPupilUId()!=null && entry.getValue().getPupilUId().equals(s) || (entry.getValue().getSecondPupilUId()!=null && entry.getValue().getSecondPupilUId().equals(s))){
                setDataOnScreen(entry,null);
                return;
            }
        }
        setDataOnScreen(null,s);
    }

    private void onClickSms() {
        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"
                        + currentPhoneNum)));
            }
        });
    }

    private void setCallingListener() {
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentPhoneNum!="") {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+currentPhoneNum));
                    startActivity(intent);
                }
            }
        });
    }
    public void onClickWhatsApp() {
        whatup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm=getActivity().getPackageManager();
                try {
                    String text = "";// Replace with your message.

                    String toNumber = "972"+currentPhoneNum; // Replace with mobile phone number without +Sign or leading zeros, but with country code
                    //Suppose your country is India and your phone number is “xxxxxxxxxx”, then you need to send “91xxxxxxxxxx”.


                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+toNumber +"&text="+text));
                    startActivity(intent);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }
    private void setArrowsListener() {
        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nearestLesson.size() > 0) {
                    int countNull = 0;
                    boolean noLesson = false;
                    CurrentPupilOnScreen++;
                    if (CurrentPupilOnScreen >= nearestLesson.entrySet().toArray().length) {
                        CurrentPupilOnScreen = 0;
                    }
//                    while (((Map.Entry<Date, TimeScheduling>) nearestLesson.entrySet().toArray()[CurrentPupilOnScreen]).getValue().getPupilUId() == null) {
//                        countNull++;
//                        CurrentPupilOnScreen++;
//                        if (CurrentPupilOnScreen >= nearestLesson.entrySet().toArray().length) {
//                            CurrentPupilOnScreen = 0;
//                        }
//                        if (countNull >= nearestLesson.entrySet().toArray().length - 1) {
//                            noLesson = true;
//                            break;
//                        }
                  //  }
                    if (nearestLesson.isEmpty()) {
                        setDataOnScreen(null, null);
                    } else {
                        setDataOnScreen((Map.Entry<Date, TimeScheduling>) nearestLesson.entrySet().toArray()[CurrentPupilOnScreen], null);
                    }
                }
            }
        });
        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nearestLesson.size() > 0) {
                    int countNull = 0;
                    boolean noLesson = false;
                    CurrentPupilOnScreen--;
                    if (CurrentPupilOnScreen < 0) {
                        CurrentPupilOnScreen = nearestLesson.entrySet().toArray().length - 1;
                    }
//                    while (((Map.Entry<Date, TimeScheduling>) nearestLesson.entrySet().toArray()[CurrentPupilOnScreen]).getValue().getPupilUId() == null) {
//                        countNull++;
//                        CurrentPupilOnScreen--;
//                        if (CurrentPupilOnScreen < 0) {
//                            CurrentPupilOnScreen = nearestLesson.entrySet().toArray().length - 1;
//                        }
//                        if (countNull >= nearestLesson.entrySet().toArray().length - 1) {
//                            noLesson = true;
//                            break;
//                        }
//                    }
                    if (nearestLesson.isEmpty()) {
                        setDataOnScreen(null, null);
                    } else {
                        setDataOnScreen((Map.Entry<Date, TimeScheduling>) nearestLesson.entrySet().toArray()[CurrentPupilOnScreen], null);
                    }
                }
            }
        });
    }

    private void setDataOnScreen(Map.Entry<Date,TimeScheduling> data, final String pupilIdNoNearestLesson) {
        if(pupilIdNoNearestLesson!=null){
            db.collection(Constants.drivingSchool).document(data==null?teacherPagesNavigator.schoolId:data.getValue().getSchoolUId().toString())
                    .collection(Constants.teachers).document(teacherPagesNavigator.teacherId)
                    .collection(Constants.pupils).document(pupilIdNoNearestLesson).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful() && task.getResult().exists()){
                        Pupil pupil=task.getResult().toObject(Pupil.class);
                        pupilName.setText(pupil.getPupilName());
                        currentPhoneNum=pupil.getPupilPhone();
                        currentPupilId=pupilIdNoNearestLesson;
                        lessonNum.setText("");
                        lessonOrTest.setText("אין תרחישים קרובים");
                        date.setText("----");
                        time.setText("----");
                    }
                }
            });
        }
        if (data != null ) {
            String testOrLesson = "";
            String dateText = "";
            time.setText(String.format(Constants.timePrintingFormat, data.getKey().getHours(), data.getKey().getMinutes()));
            final String pupilUId = data.getValue().getPupilUId();
            Date scheduleDate = data.getKey();
            if (Calendar.getInstance().get(Calendar.DATE) == scheduleDate.getDate()) {
                dateText = "היום";
            } else {
                if (scheduleDate.getDate() - Calendar.getInstance().get(Calendar.DATE) == 1) {
                    dateText = "מחר";
                } else {
                    SimpleDateFormat simpleDate = new SimpleDateFormat("dd-MM-yyyy");
                    dateText = simpleDate.format(scheduleDate);
                }
            }
            date.setText(dateText);
            if (data.getValue().isTest()) {
                if (data.getValue().isEnternalTest()) {
                    testOrLesson = "טסט פנימי";
                } else {
                    testOrLesson = "טסט חיצוני";
                }
                lessonOrTest.setText(testOrLesson);
            } else {
                testOrLesson = "שיעור מס' ";
                lessonOrTest.setText(testOrLesson);
                db.collection(Constants.drivingSchool).document(data==null?teacherPagesNavigator.schoolId:data.getValue().getSchoolUId().toString()).collection(Constants.teachers).document(teacherPagesNavigator.teacherId)
                        .collection(Constants.pupils).document(pupilUId).collection(Constants.lessons).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            lessonNum.setText(((Integer) (task.getResult().size() + 1)).toString());
                        }
                    }
                });
            }

            db.collection(Constants.drivingSchool).document(teacherPagesNavigator.schoolId).collection(Constants.teachers).document(teacherPagesNavigator.teacherId)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    teacherName.setText(task.getResult().toObject(Teacher.class).getTeacherName());
                }
            });

            db.collection(Constants.drivingSchool).document(data==null?teacherPagesNavigator.schoolId:data.getValue().getSchoolUId().toString()).collection(Constants.teachers).document(teacherPagesNavigator.teacherId)
                    .collection(Constants.pupils).document(pupilUId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        pupilName.setText(task.getResult().toObject(Pupil.class).getPupilName());
                        currentPhoneNum = task.getResult().toObject(Pupil.class).getPupilPhone();
                    }
                }
            });
            FirebaseStorage.getInstance().getReferenceFromUrl(Constants.pupilsStoragePath + pupilUId + "/" + Constants.faceImage + "/")
                    .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    StorageReference mImageRef =
                            FirebaseStorage.getInstance().getReferenceFromUrl(Constants.pupilsStoragePath + pupilUId + "/" + Constants.faceImage + "/");
                    final long ONE_MEGABYTE = 1024 * 1024;
                    mImageRef.getBytes(ONE_MEGABYTE)
                            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    Bitmap circleBitmap = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);

                                    BitmapShader shader = new BitmapShader(bm, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                                    Paint paint = new Paint();
                                    paint.setShader(shader);
                                    paint.setAntiAlias(true);
                                    Canvas c = new Canvas(circleBitmap);
                                    c.drawCircle(bm.getWidth() / 2, bm.getHeight() / 2, bm.getWidth() / 2, paint);

                                    pupilPhoto.setImageBitmap(circleBitmap);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        }else{
            pupilName.setText("----");
            lessonOrTest.setText("אין תרחישים קרובים");
            lessonNum.setText("");
            date.setText("----");
            time.setText("----");
        }
    }
    private void getNearestLessons() {
        Calendar calendar = Calendar.getInstance();
        final Date timeNow=calendar.getTime();
        Calendar calendar1=Calendar.getInstance();
        calendar1.add(Calendar.DAY_OF_MONTH,-1);
        calendar1.set(Calendar.HOUR,0);
        calendar1.set(Calendar.MINUTE,0);
        calendar1.set(Calendar.SECOND,0);
        final Date datNow=calendar1.getTime();
        Calendar lastDayAtWeakn=Calendar.getInstance();
        lastDayAtWeakn.set(Calendar.DAY_OF_WEEK,Calendar.SATURDAY);
        lastDayAtWeakn.set(Calendar.HOUR,23);
        lastDayAtWeakn.set(Calendar.MINUTE,59);
        lastDayAtWeakn.set(Calendar.SECOND,59);
        final Date lastDayAtWeak=lastDayAtWeakn.getTime();
        final ArrayList<Date> nearestSchedulesDate=new ArrayList<>();
        if (!editTextFilledExposedDropdown.getText().toString().isEmpty())
        db.collection(Constants.drivingSchool).document(teacherPagesNavigator.schoolId).collection(Constants.teachers).document(teacherPagesNavigator.teacherId)
                .collection(Constants.schedules).orderBy(Constants.scheduleDate, Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful() && !task.getResult().isEmpty()){
                    for(int i=0;i<task.getResult().size();i++) {
                        Date dbScheduleDate = task.getResult().getDocuments().get(i).toObject(Schedule.class).getScheduleDate();
                        if(dbScheduleDate.after(lastDayAtWeak)){
                            break;
                        }
                        if ((datNow.before(dbScheduleDate) || datNow.equals(dbScheduleDate))) {
                            nearestSchedulesDate.add(dbScheduleDate);
                    }
                    }
                            for (Date Ndate : nearestSchedulesDate){
                                db.collection(Constants.drivingSchool).document(teacherPagesNavigator.schoolId).collection(Constants.teachers).document(teacherPagesNavigator.teacherId)
                                        .collection(Constants.schedules).document(Ndate.toString()).collection(Constants.oneDayeSchedules).orderBy(Constants.timeSchedulingStartTime, Query.Direction.ASCENDING)
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        nearestSchedulesDate.remove(0);
                                        if(task.isSuccessful() && !task.getResult().isEmpty()){
                                            for(QueryDocumentSnapshot doc : task.getResult()){
                                                TimeScheduling time=doc.toObject(TimeScheduling.class);
                                                if(timeNow.before(time.getStartTime())){
                                                    nearestLesson.put(time.getStartTime(),time);
                                                }
                                            }

                                        }
                                        if(!nearestLesson.isEmpty() && nearestSchedulesDate.isEmpty()){
                                            int countNull=0;
                                            boolean noLesson=false;
//                                            while(((Map.Entry<Date, TimeScheduling>) nearestLesson.entrySet().toArray()[CurrentPupilOnScreen]).getValue().getPupilUId()==null)
//                                            {
//                                                countNull++;
//                                                CurrentPupilOnScreen++;
//                                                if(CurrentPupilOnScreen>=nearestLesson.entrySet().toArray().length){
//                                                    CurrentPupilOnScreen=0;
//                                                }
//                                                if(countNull>=nearestLesson.entrySet().toArray().length-1){
//                                                    {
//                                                        noLesson=true;
//                                                        break;
//                                                    }
//                                                }
//                                            }
                                            for(int i=0;i<nearestLesson.size();i++){
                                             if(((Map.Entry<Date,TimeScheduling>)nearestLesson.entrySet().toArray()[i]).getValue().getPupilUId()==null){
                                                 nearestLesson.remove(((Map.Entry<Date,TimeScheduling>)nearestLesson.entrySet().toArray()[i]).getKey());
                                                 i--;
                                             }
                                            }
                                            if(nearestLesson.isEmpty() || nearestLesson==null){
                                                setDataOnScreen(null,null);
                                            }else{
                                                setDataOnScreen((Map.Entry<Date, TimeScheduling>) nearestLesson.entrySet().toArray()[0],null);
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
