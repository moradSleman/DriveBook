package pupilPackage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import databaseClasses.Constants;
import databaseClasses.notification;
import databaseClasses.sqlLitDbHelper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.mainPages.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class pupilHomePage extends Fragment {

    public static RecyclerView paymentRecycler;
    public static TextView totalPayNeeded,totalPays,paymentPercent;
    private ImageView image;
    private String pupilId;
    private TextView pupilName;
    protected static TextView lessonNum;
    protected static TextView nearLessonDate;
    protected static TextView nearLessonTime;
    protected static TextView nearTestDate;
    protected static TextView nearTestTime;
    protected static TextView testsNum;
    protected static ImageButton addCalendar;
    protected static ImageButton goNotification;
    protected static ImageButton settingInfo;
    private TextView newNotenum;
    private sqlLitDbHelper myLocalDb;
    protected static RecyclerView lessonRecyclerView;
    protected static RecyclerView testRecyclerView;
    private int numOfNewNotifications;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.pupil_home_page, container, false);
        pupilName=rootView.findViewById(R.id.pupilName);
        newNotenum=rootView.findViewById(R.id.numNoty);
        newNotenum.setVisibility(View.GONE);
        myLocalDb=new sqlLitDbHelper(getContext());
        setNumNewNotes();
        image=rootView.findViewById(R.id.pupilImage);
        pupilId=pupilPagesNavigator.pupilId;

        testRecyclerView=(RecyclerView)rootView.findViewById(R.id.testRecycler);
        lessonRecyclerView =(RecyclerView)rootView.findViewById(R.id.lessonRecycler);
        paymentRecycler=(RecyclerView)rootView.findViewById(R.id.paymentRecycler);

        totalPayNeeded=(TextView)rootView.findViewById(R.id.totalPayNeede);
        totalPays=(TextView)rootView.findViewById(R.id.totalPay);
        paymentPercent=(TextView)rootView.findViewById(R.id.percentPayment);

        lessonNum=rootView.findViewById(R.id.lessonNum);
        nearLessonDate=rootView.findViewById(R.id.dateLesson);
        nearLessonTime=rootView.findViewById(R.id.timeHourLesson);
        nearTestDate=rootView.findViewById(R.id.dateTest);
        nearTestTime=rootView.findViewById(R.id.timeHourTest);
        testsNum=rootView.findViewById(R.id.testNum);
        addCalendar=rootView.findViewById(R.id.addCalendar);
        goNotification=rootView.findViewById(R.id.notifications);
        settingInfo=rootView.findViewById(R.id.settingInfo);
        setWorkingButtons();
        setProfileImage();
        pupilName.setText(pupilPagesNavigator.fullName);
        TabLayout tabsChoosing = rootView.findViewById(R.id.pupilHomeTabs);
        tabsChoosing.setTabMode(TabLayout.MODE_FIXED);
        tabsChoosing.setTabGravity(TabLayout.GRAVITY_FILL);
        tabsChoosing.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    rootView.findViewById(R.id.testLayout).setVisibility(View.GONE);
                    rootView.findViewById(R.id.paymentLayout).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.lessonLayout).setVisibility(View.GONE);
                    pupilPayment pupilPayment=new pupilPayment();
                    pupilPayment.onCreate(getContext(),true,null);
                }else
                {
                    if(tab.getPosition() ==1 ){
                        rootView.findViewById(R.id.paymentLayout).setVisibility(View.GONE);
                        rootView.findViewById(R.id.lessonLayout).setVisibility(View.GONE);
                        pupilTest pupilTest=new pupilTest();
                        pupilTest.onCreateclass(getContext(),null,true);
                        rootView.findViewById(R.id.testLayout).setVisibility(View.VISIBLE);
                    }else
                    {
                        if(tab.getPosition() ==2 ){
                            rootView.findViewById(R.id.testLayout).setVisibility(View.GONE);
                            rootView.findViewById(R.id.paymentLayout).setVisibility(View.GONE);
                            pupilLesson pupilLesson=new pupilLesson();
                            pupilLesson.onCreate(getContext(),null,null,null);
                            rootView.findViewById(R.id.lessonLayout).setVisibility(View.VISIBLE);

                        }
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabsChoosing.getTabAt(2).select();
        return rootView;
    }

    private void setNumNewNotes(){
        if(sqlLitDbHelper.isConnected(getContext())){
            FirebaseFirestore.getInstance().collection(Constants.drivingSchool).document(pupilPagesNavigator.schoolId).collection(Constants.teachers)
                    .document(pupilPagesNavigator.teacherId).collection(Constants.pupils).document(pupilPagesNavigator.pupilId).collection(Constants.notifications)
                    .whereEqualTo(Constants.notyIsRead,false).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful() && !task.getResult().isEmpty()){
                        numOfNewNotifications=task.getResult().size();
                        newNotenum.setText(((Integer)numOfNewNotifications).toString());
                        newNotenum.setVisibility(View.VISIBLE);
                    }else
                    {
                        newNotenum.setText("0");
                        newNotenum.setVisibility(View.VISIBLE);
                    }
                }
            });
        }else{
            int count=0;
            for(notification note : myLocalDb.readNotyFromDb()){
                if(!note.isRead()){
                    count++;
                }

            }
            this.numOfNewNotifications=count;
            newNotenum.setText(((Integer)numOfNewNotifications).toString());
            newNotenum.setVisibility(View.VISIBLE);
        }
    }

    private void setProfileImage() {
        if(sqlLitDbHelper.isConnected(getContext())) {
            FirebaseStorage.getInstance().getReferenceFromUrl(Constants.pupilsStoragePath + pupilId + "/" + Constants.faceImage + "/")
                    .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    StorageReference mImageRef =
                            FirebaseStorage.getInstance().getReferenceFromUrl(Constants.pupilsStoragePath + pupilId + "/" + Constants.faceImage + "/");
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

                                    image.setImageBitmap(circleBitmap);
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
            image.setImageBitmap(pupilPagesNavigator.bmp);
        }
    }
    private void setWorkingButtons() {
        pupilHomePage.goNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pupilNotifications fragment=new pupilNotifications();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        ,fragment,Constants.tagPupilNotification).commit();
            }
        });
        pupilHomePage.settingInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pupilSettings myFragment =new pupilSettings();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        ,myFragment,Constants.tagPupilSettings).commit();
            }
        });

        pupilHomePage.addCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pupilCalendar myFragment1 =new pupilCalendar();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        ,myFragment1,Constants.tagPupilCalendar).commit();
            }
        });
    }

}
