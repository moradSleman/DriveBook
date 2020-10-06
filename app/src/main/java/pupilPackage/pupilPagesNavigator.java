package pupilPackage;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.user.mainPages.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.Date;

import databaseClasses.Constants;
import databaseClasses.Pupil;
import databaseClasses.lessonPayment;
import databaseClasses.pupilSqlLite;
import databaseClasses.sqlLitDbHelper;

public class pupilPagesNavigator extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    FirebaseAuth mAuth;
    private NavigationView navView;
    private FirebaseFirestore db;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReferenceFromUrl(Constants.pupilsStoragePath);
    public static String schoolId;
    public static String teacherId;
    public static String pupilId;
    protected static String fullName;
    protected static  String Address;
    protected static  String Email;
    protected static  String birthDate;
    protected static  String theory;
    protected static String pupilPhone;
    protected static int alertLesson;
    protected static String tokenId;
    protected static Bitmap bmp;
    private sqlLitDbHelper myLocalDb;
    protected static ArrayList<lessonPayment> lessonsPaymentData;
    private ImageView photo;
    private TextView navUserName;
    public static Context context;
    public static Date theoryDate;
    public static Date birthDateDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context=getBaseContext();
        mAuth = FirebaseAuth.getInstance();
        lessonsPaymentData=new ArrayList<>();
        setContentView(R.layout.pupil_pages_navigator);
        navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setBackgroundColor(Color.parseColor("#00a5ff"));
        }
        toolbar.setTitle("");
        toolbar.setBackgroundColor(Color.parseColor("#eeeeee"));
        drawer = findViewById(R.id.drawer_layout);
        db = FirebaseFirestore.getInstance();
        myLocalDb=new sqlLitDbHelper(pupilPagesNavigator.this);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(pupilPagesNavigator.this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        View headerView = navView.getHeaderView(0);
        navUserName = (TextView) headerView.findViewById(R.id.userNameTxt);
        photo = (ImageView) headerView.findViewById(R.id.photo);


        setProfileImage();
        setData();



    }

    private void setData() {
        FirebaseFirestore.getInstance().collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(teacherId).collection(
                Constants.pupils).document(pupilId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful() && task.getResult().exists()){
                    Pupil pupil=task.getResult().toObject(Pupil.class);
                    pupilPhone=pupil.getPupilPhone()==null?"":pupil.getPupilPhone();
                    birthDate=pupil.getPupilBirthDate()!=null?pupil.getPupilBirthDate().toString():"";
                    Address=pupil.getPupilLocation()!=null?pupil.getPupilLocation().toString():"";
                    fullName=pupil.getPupilName();
                    Email=pupil.getPupilMail()!=null?pupil.getPupilMail().toString():"";
                    theory=pupil.getPupilTheoryEnd()!=null?pupil.getPupilTheoryEnd().toString():"";
                    if (birthDate!=""){
                        birthDateDate=pupil.getPupilBirthDate();
                    }
                    if (theory != "") {
                        theoryDate=pupil.getPupilTheoryEnd();
                    }
                    alertLesson=pupil.getTimeAlertBeforeLesson()!=null?((Integer)pupil.getTimeAlertBeforeLesson()):45;
                    navUserName.setText(fullName);
                    FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if(task.isSuccessful()){
                                tokenId=task.getResult().getToken().toString();
                            }
                        }
                    });
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new pupilHomePage(),Constants.tagPupilHomePage).commit();
                }
            }
        });
    }

    private void setProfileImage() {
        if(sqlLitDbHelper.isConnected(pupilPagesNavigator.this)) {
            new pupilLesson().saveLessonsToSqlLite(pupilPagesNavigator.this);
            new pupilTest().saveTestToSqlLite(pupilPagesNavigator.this);
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

                                    photo.setImageBitmap(circleBitmap);

                                    setUserProfileToSqlLite(circleBitmap);
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

        }

    }

    private void setUserProfileToSqlLite(Bitmap circleBitmap) {
        if (sqlLitDbHelper.isConnected(pupilPagesNavigator.this)) {
            pupilSqlLite pupilSqlLite = new pupilSqlLite();
            pupilSqlLite.setPupilImage(circleBitmap);
            pupilSqlLite.setPupilBirthDate(this.birthDate);
            pupilSqlLite.setPupilTheoryEnd(this.theory);
            pupilSqlLite.setPupilLocation(this.Address);
            pupilSqlLite.setPupilId("");
            pupilSqlLite.setPupilMail(this.Email);
            pupilSqlLite.setPupilName(this.fullName);
            pupilSqlLite.setPupilPhone(this.pupilPhone);
            pupilSqlLite.setTimeAlertBeforeLesson(this.alertLesson);
            pupilSqlLite.setTokenId(this.tokenId);
            pupilSqlLite.setTeacherUId(this.teacherId);
            pupilSqlLite.setSchoolUId(this.schoolId);
            pupilSqlLite.setPupilUId(this.pupilId);

            myLocalDb.savePupilProfileToSqlLite(pupilSqlLite);

            myLocalDb.saveTypeOfUser(Constants.pupilType);
        }
    }

    @Override
    public void onBackPressed() {
        pupilCalendar pupilCalendar = (pupilCalendar) getSupportFragmentManager().findFragmentByTag(Constants.tagPupilCalendar);
        pupilSettings pupilSettings = (pupilSettings) getSupportFragmentManager().findFragmentByTag(Constants.tagPupilSettings);
        pupilHomePage pupilHomePage = (pupilHomePage) getSupportFragmentManager().findFragmentByTag(Constants.tagPupilHomePage);
        pupilNotifications pupilNotifications=(pupilNotifications) getSupportFragmentManager().findFragmentByTag(Constants.tagPupilNotification);

        if (pupilNotifications != null && pupilNotifications.isVisible()) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new pupilHomePage(), Constants.tagPupilHomePage).commit();
        } else
        if (pupilCalendar != null && pupilCalendar.isVisible()) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new pupilHomePage(), Constants.tagPupilHomePage).commit();
        } else if (pupilSettings != null && pupilSettings.isVisible()) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new pupilHomePage(), Constants.tagPupilHomePage).commit();
        } else if (pupilHomePage != null && pupilHomePage.isVisible()) {

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
        db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(teacherId)
                .collection(Constants.pupils).document(pupilId).collection(Constants.myTokens).document(tokenId).delete().addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mAuth.signOut();
                        finish();
                        startActivity(new Intent(getBaseContext(), LoginActivity.class));
                    }
                }
        );
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.pupilSettings:
                pupilSettings myFragment =new pupilSettings();

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                    ,myFragment,Constants.tagPupilSettings).commit();
            break;

            case R.id.calendar :
                 pupilCalendar myFragment1 =new pupilCalendar();
                 getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        ,myFragment1,Constants.tagPupilCalendar).commit();
                 break;
            case R.id.notifications :
                pupilNotifications fragment=new pupilNotifications();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        ,fragment,Constants.tagPupilNotification).commit();
                break;
            case R.id.exit:
                removeRegisteredTokens();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
