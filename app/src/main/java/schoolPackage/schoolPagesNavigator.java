package schoolPackage;

import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.user.mainPages.LoginActivity;
import com.example.user.mainPages.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import databaseClasses.Constants;
import databaseClasses.drivingSchool;

public class schoolPagesNavigator extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private NavigationView navView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    public static String schoolUId;
    protected static String schoolName;
    protected static String schoolAddress;
    protected static String schoolPhone;
    protected static String schoolMail;
    private String tokenId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.school_pages_navigator);
        db = FirebaseFirestore.getInstance();
        navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setBackgroundColor(Color.parseColor("#00a5ff"));
        }
        toolbar.setTitle("");
        toolbar.setBackgroundColor(Color.parseColor("#eeeeee"));
        drawer = findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(schoolPagesNavigator.this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        schoolUId = mAuth.getCurrentUser().getUid().toString();
        View headerView = navView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.userNameTxt);
        if(mAuth.getCurrentUser().getEmail()==null || mAuth.getCurrentUser().getEmail()==""){
            navUsername.setText(mAuth.getCurrentUser().getPhoneNumber().toString());
        }else {
            navUsername.setText(mAuth.getCurrentUser().getEmail().toString());
        }
        setData();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new schoolHomePage(),Constants.tagSchoolHomePage).commit();
    }

    private void setData() {
        db.collection(Constants.drivingSchool).document(schoolUId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                drivingSchool school=task.getResult().toObject(drivingSchool.class);
                schoolName=school.getSchoolName();
                schoolAddress=school.getSchoolLocation();
                schoolMail=school.getSchoolEmail()!=null?school.getSchoolEmail():"";
                schoolPhone=school.getSchoolPhone()!=null?school.getSchoolPhone():"";
            }
        });
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(task.isSuccessful()){
                    tokenId=task.getResult().getToken().toString();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        schoolHomePage schoolHomePage = (schoolHomePage) getSupportFragmentManager().findFragmentByTag(Constants.tagSchoolHomePage);
       schoolSettings schoolSettings = (schoolSettings) getSupportFragmentManager().findFragmentByTag(Constants.tagSchoolSetting);
        schoolRegisterTeacher schoolRegisterTeacher = (schoolRegisterTeacher) getSupportFragmentManager().findFragmentByTag(Constants.tagSchoolRegisterTeacher);
        schoolTeachersPage schoolTeachersPage=(schoolTeachersPage)getSupportFragmentManager().findFragmentByTag(Constants.tagSchoolTeacherPage);
        schoolPupilsPage schoolPupilsPage=(schoolPupilsPage) getSupportFragmentManager().findFragmentByTag(Constants.tagSchoolPupilsPage);
        schoolStatisticsPage schoolStatisticsPage=(schoolStatisticsPage) getSupportFragmentManager().findFragmentByTag(Constants.tagSchoolStatisticsPage);

        if(schoolStatisticsPage!=null && schoolStatisticsPage.isVisible()){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new schoolHomePage(),Constants.tagSchoolHomePage).commit();
        }else
        if(schoolPupilsPage!=null && schoolPupilsPage.isVisible()){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new schoolHomePage(),Constants.tagSchoolHomePage).commit();
        }else
        if(schoolTeachersPage!=null && schoolTeachersPage.isVisible()){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new schoolHomePage(),Constants.tagSchoolHomePage).commit();
        }else
        if(schoolRegisterTeacher!=null && schoolRegisterTeacher.isVisible()){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new schoolHomePage(),Constants.tagSchoolHomePage).commit();
        }else
        if(schoolSettings!=null && schoolSettings.isVisible()){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new schoolHomePage(),Constants.tagSchoolHomePage).commit();
        }else
        if (schoolHomePage != null && schoolHomePage.isVisible()) {
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
          db.collection(Constants.drivingSchool).document(schoolUId.toString()).collection(Constants.myTokens).document(tokenId).delete().addOnSuccessListener(
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
           case R.id.addTeacher:
               android.app.FragmentManager fm =getFragmentManager();
               schoolRegisterTeacher myFragment =new schoolRegisterTeacher();
               getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        ,myFragment,Constants.tagSchoolRegisterTeacher).commit();
               break;
           case R.id.schoolSettings:
               android.app.FragmentManager fm1 =getFragmentManager();
               schoolSettings myFragment1 =new schoolSettings();
               getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                       ,myFragment1,Constants.tagSchoolSetting).commit();
               break;
           case R.id.exit :
               removeRegisteredTokens();
       }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
