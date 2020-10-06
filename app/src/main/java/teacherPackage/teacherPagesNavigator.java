package teacherPackage;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;

import adapters.pupilTestAdapter;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.mainPages.LoginActivity;
import com.example.user.mainPages.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import databaseClasses.Constants;
import databaseClasses.Teacher;
import databaseClasses.rotateImage;

import static databaseClasses.Constants.tagTeacherHome;

public class teacherPagesNavigator extends  AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static Date birthDateDate;
    private DrawerLayout drawer;
    private NavigationView navView;
    private FirebaseAuth mAuth;
    protected static String fullName;
    protected static String tokenId;
    public static String teacherId;
    public static Context context;
    //after choosing school set this field
    public static String schoolId;
    public static ArrayList<String> schoolUIDs;
    protected static String Phone;
    protected static String Address;
    protected static String birthDate;
    protected static String Email;
    private FirebaseFirestore db;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReferenceFromUrl(Constants.teacherStoragePath);
    public Bitmap bm;
    TextView navUsername;


    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (reqCode == 1 && resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final Bitmap orientedBitMap = rotateImage.scaleImage(teacherPagesNavigator.this, imageUri);
                pupilTestAdapter.testResultImage.setImageBitmap(orientedBitMap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(teacherPagesNavigator.this, "Something went wrong", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this.getBaseContext();
        setContentView(R.layout.teacher_pages_navigator);
        db=FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        teacherId=mAuth.getCurrentUser().getUid();
        schoolId=schoolUIDs.get(0);
        navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setBackgroundColor(Color.parseColor("#00a5ff"));
        }
        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(teacherPagesNavigator.this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toolbar.setBackgroundColor(Color.parseColor("#eeeeee"));
        toggle.syncState();
        View headerView = navView.getHeaderView(0);
        navUsername = (TextView) headerView.findViewById(R.id.userNameTxt);
        final ImageView photo = (ImageView) headerView.findViewById(R.id.photo);
        FirebaseStorage.getInstance().getReferenceFromUrl(Constants.teacherStoragePath + teacherId + "/"+Constants.faceImage+"/")
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                StorageReference mImageRef =
                        FirebaseStorage.getInstance().getReferenceFromUrl(Constants.teacherStoragePath + teacherId + "/"+Constants.faceImage+"/");
                final long ONE_MEGABYTE = 1024 * 1024;
                mImageRef.getBytes(ONE_MEGABYTE)
                        .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                 bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                photo.setImageBitmap(bm);
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
                Log.e(tagTeacherHome ,"onFailure: "+e);
            }
        });

        setData();

    }

    private void setData() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(task.isSuccessful()){
                    tokenId=task.getResult().getToken();
                }
            }
        });
        db.collection(Constants.drivingSchool).document(schoolUIDs.get(0)).collection(Constants.teachers).document(teacherId).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful() && task.getResult().exists()){
                            Teacher teacher=task.getResult().toObject(Teacher.class);
                            fullName=teacher.getTeacherName();
                            Phone=teacher.getTeacherPhone();
                            Address=teacher.getTeacherLocation();
                            birthDate=teacher.getTeacherBirthdate()!=null?teacher.getTeacherBirthdate().toString():null;
                            if(birthDate!=""){
                                birthDateDate=teacher.getTeacherBirthdate();
                            }
                            Email=teacher.getTeacherMail();
                            navUsername.setText(fullName);
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new teacherHomePage(), tagTeacherHome).commit();

                        }
                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
        teacherHomePage teacherHome = (teacherHomePage) getSupportFragmentManager().findFragmentByTag(Constants.tagTeacherHome);
        teacherRegisterPupil teacherRegisterPupil = (teacherRegisterPupil) getSupportFragmentManager().findFragmentByTag(Constants.tagTeacherRegisterPupil);
        teacherCalendar teacherCalendar = (teacherCalendar) getSupportFragmentManager().findFragmentByTag(Constants.tagTeacherCalendar);
        teacherSetting teacherSetting = (teacherSetting) getSupportFragmentManager().findFragmentByTag(Constants.tagTeacherSetting);
        teacherScheduling teacherScheduling=(teacherScheduling) getSupportFragmentManager().findFragmentByTag(Constants.tagTeacherScheduling);
        teacherMessage teacherMessage=(teacherMessage)getSupportFragmentManager().findFragmentByTag(Constants.tagTeacherMessage);
        teacherNotifications teacherNotifications=(teacherNotifications) getSupportFragmentManager().findFragmentByTag(Constants.tagTeacherNotification);

        if(teacherNotifications!=null && teacherNotifications.isVisible()){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new teacherHomePage(),Constants.tagTeacherHome).commit();
        }
        if(teacherScheduling!=null && teacherScheduling.isVisible()){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new teacherCalendar(),Constants.tagTeacherCalendar).commit();
        }else
        if(teacherCalendar!=null && teacherCalendar.isVisible()){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new teacherHomePage(),Constants.tagTeacherHome).commit();
        }else
        if(teacherRegisterPupil!=null && teacherRegisterPupil.isVisible()){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new teacherHomePage(),Constants.tagTeacherHome).commit();
        }else
        if(teacherSetting!=null && teacherSetting.isVisible()){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new teacherHomePage(),Constants.tagTeacherHome).commit();
        }else
        if(teacherMessage!=null && teacherMessage.isVisible()){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new teacherHomePage(),Constants.tagTeacherHome).commit();
        } else
        if (teacherHome != null && teacherHome.isVisible()) {
            new AlertDialog.Builder(this)
                    .setTitle("Exit")
                    .setMessage("Are you sure you want to exit?")
                    .setIcon(R.drawable.ic_exit)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            removeRegisteredTokens();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        }
    }

    public void removeRegisteredTokens(){
        for(String schoolId:schoolUIDs) {
            db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(teacherId)
                    .collection(Constants.myTokens).document(tokenId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mAuth.signOut();
                    finish();
                    startActivity(new Intent(getBaseContext(),LoginActivity.class));
                }
            });
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.addPupil:
                android.app.FragmentManager fm =getFragmentManager();
                teacherRegisterPupil myFragment =new teacherRegisterPupil();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        ,myFragment,Constants.tagTeacherRegisterPupil).commit();
                break;
            case R.id.teacherSettings:
                android.app.FragmentManager fm1 =getFragmentManager();
                Intent intent2=getIntent();
                teacherSetting settingFragment=new teacherSetting();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        ,settingFragment,Constants.tagTeacherSetting).commit();
                break;
            case R.id.exit :
                removeRegisteredTokens();
                break;
            case R.id.notifications :
                teacherNotifications scheduleFragment1=new teacherNotifications();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        scheduleFragment1,Constants.tagTeacherNotification).commit();
                break;
            case R.id.calendar :
                Bundle arguments3 = new Bundle();
                Intent intent3=getIntent();
                teacherCalendar scheduleFragment=new teacherCalendar();
                scheduleFragment.setArguments(arguments3);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        scheduleFragment,Constants.tagTeacherCalendar).commit();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
