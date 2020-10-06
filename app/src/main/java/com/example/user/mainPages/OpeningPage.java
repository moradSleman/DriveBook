package com.example.user.mainPages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import databaseClasses.Constants;
import databaseClasses.pupilSqlLite;
import databaseClasses.sqlLitDbHelper;
import pupilPackage.pupilPagesNavigator;
import schoolPackage.schoolPagesNavigator;
import teacherPackage.teacherPagesNavigator;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class OpeningPage extends AppCompatActivity {
    private String currentTokenId;
    public static String token;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private sqlLitDbHelper myLocalDb;
    private boolean userUIdFounded=false;
    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opening_page);
        myLocalDb = new sqlLitDbHelper(OpeningPage.this);
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            token = task.getResult().getToken();
                        }

                        //  token = task.getResult().getToken();

                        //  checkToken(token);
                    }
                });
        if (sqlLitDbHelper.isConnected(OpeningPage.this)) {
            db = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();
            if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                FirebaseFirestore.getInstance().collection(Constants.UIDS).document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(
                        new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful() && task.getResult().exists()){
                                    switch(task.getResult().get(Constants.typeOfUser).toString()){
                                        case "schoolType" :
                                            schoolPagesNavigator.schoolUId=mAuth.getCurrentUser().getUid().toString();
                                            startActivity(new Intent(OpeningPage.this,schoolPagesNavigator.class));
                                            finish();
                                            break;
                                        case "teacherType":
                                            ArrayList<String> schoolsUIDs=(ArrayList<String>) task.getResult().get(Constants.schoolsUIDs);
                                            teacherPagesNavigator.schoolUIDs=schoolsUIDs;
                                            teacherPagesNavigator.teacherId=mAuth.getCurrentUser().getUid().toString();
                                            startActivity(new Intent(OpeningPage.this,teacherPagesNavigator.class));
                                            finish();
                                            break;
                                        case "pupilType" :
                                            pupilPagesNavigator.pupilId=mAuth.getCurrentUser().getUid();
                                            pupilPagesNavigator.schoolId=task.getResult().get(Constants.schoolId).toString();
                                            pupilPagesNavigator.teacherId=task.getResult().get(Constants.teacherId).toString();
                                            startActivity(new Intent(OpeningPage.this,pupilPagesNavigator.class));
                                            finish();
                                            break;
                                    }
                                }
                            }
                        }
                );
            }else{
                startActivity(new Intent(OpeningPage.this, LoginActivity.class));
                finish();

            }

        } else {
            String typeOfUser = myLocalDb.readTypeOfUser();
            if (typeOfUser.equals(Constants.pupilType)) {
                ArrayList<pupilSqlLite> pupilSqlLites = myLocalDb.readPupilProfileFromSqlLite();
                pupilSqlLite pupilSqlLite = pupilSqlLites.get(0);
                Intent intent = new Intent(getBaseContext()
                        , pupilPagesNavigator.class);

                intent.putExtra(Constants.pupilMail, pupilSqlLite.getPupilPhone());
                intent.putExtra(Constants.pupilId, pupilSqlLite.getPupilUId());
                intent.putExtra(Constants.teacherId, pupilSqlLite.getTeacherUId());
                intent.putExtra(Constants.schoolId, pupilSqlLite.getSchoolUId());
                intent.putExtra(Constants.pupilPhone, pupilSqlLite.getPupilPhone());
                intent.putExtra(Constants.pupilName, pupilSqlLite.getPupilName());
                intent.putExtra(Constants.pupilMail, pupilSqlLite.getPupilMail());
                intent.putExtra(Constants.pupilLocation, pupilSqlLite.getPupilLocation());
                intent.putExtra(Constants.pupilBirthdate, pupilSqlLite.getPupilBirthDate().toString());
                intent.putExtra(Constants.pupilTheoryEnd, pupilSqlLite.getPupilTheoryEnd().toString());
                intent.putExtra(Constants.TokenId, pupilSqlLite.getTokenId());
                intent.putExtra(Constants.tokenautoLogin, true);
                intent.putExtra(Constants.tokenUserName, pupilSqlLite.getPupilPhone());
                intent.putExtra(Constants.tokenPassword, pupilSqlLite.getPupilPassword());

                Bitmap bmp=pupilSqlLite.getPupilImage();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                intent.putExtra(Constants.pupilPhoto,byteArray);

                this.currentTokenId=pupilSqlLite.getTokenId();
                startActivity(intent);
            } else {
                if (typeOfUser.equals(Constants.teacherType)) {
                    //TODO
                } else {
                    if (typeOfUser.equals(Constants.schoolType)) {
                        //TODO
                    }
                }
            }

        }
    }
  /*  public void checkToken(final String currentTokenId){
        db.collection(Constants.tokens).document(currentTokenId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful() && task.getResult().exists()){
                    final tokenLoginDetails tokenDetails=task.getResult().toObject(tokenLoginDetails.class);
                    if(tokenDetails.isAutoLogin()){
                        if(tokenDetails.getTokenPupilId()!=null){
                            final Intent intent = new Intent(getBaseContext()
                                    , pupilPagesNavigator.class);
                            db.collection(Constants.drivingSchool).document(tokenDetails.getTokenSchoolId()).collection(Constants.teachers).document(tokenDetails.getTokenTeacherId())
                                    .collection(Constants.pupils).document(tokenDetails.getTokenPupilId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful() && task.getResult().exists()) {
                                        Pupil pupil = task.getResult().toObject(Pupil.class);
                                        intent.putExtra(Constants.pupilMail, pupil.getPupilPhone());
                                        intent.putExtra(Constants.pupilId, tokenDetails.getTokenPupilId());
                                        intent.putExtra(Constants.teacherId, tokenDetails.getTokenTeacherId());
                                        intent.putExtra(Constants.schoolId, tokenDetails.getTokenSchoolId());
                                        intent.putExtra(Constants.pupilPhone,pupil.getPupilPhone());
                                        intent.putExtra(Constants.pupilName, pupil.getPupilName());
                                        intent.putExtra(Constants.pupilMail, pupil.getPupilMail());
                                        intent.putExtra(Constants.pupilLocation, pupil.getPupilLocation());
                                        intent.putExtra(Constants.pupilBirthdate, pupil.getPupilBirthDate().toString());
                                        intent.putExtra(Constants.pupilTheoryEnd, pupil.getPupilTheoryEnd().toString());
                                        intent.putExtra(Constants.TokenId, currentTokenId);
                                        intent.putExtra(Constants.tokenautoLogin, true);
                                        intent.putExtra(Constants.tokenUserName, pupil.getPupilPhone());
                                        intent.putExtra(Constants.tokenPassword, pupil.getPupilPassword());
                                        startActivity(intent);
                                    }
                                }
                            });

                        }else{
                            if(tokenDetails.getTokenTeacherId()!=null){
                                db.collection(Constants.drivingSchool).document(tokenDetails.getTokenSchoolId()).collection(Constants.teachers).
                                        document(tokenDetails.getTokenTeacherId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful() && task.getResult().exists()) {
                                            Teacher teacher = task.getResult().toObject(Teacher.class);
                                            Intent intent = new Intent(getBaseContext()
                                                    , teacherPagesNavigator.class);
                                            intent.putExtra(Constants.teacherPhone,teacher.getTeacherPhone());
                                            intent.putExtra(Constants.teacherId,tokenDetails.getTokenTeacherId());
                                            intent.putExtra(Constants.teacherName, teacher.getTeacherName());
                                            intent.putExtra(Constants.teacherLocation, teacher.getTeacherLocation());
                                            intent.putExtra(Constants.teacherBirthdate, teacher.getTeacherBirthdate().toString());
                                            intent.putExtra(Constants.teacherMail, teacher.getTeacherMail());
                                            intent.putExtra(Constants.schoolId, tokenDetails.getTokenSchoolId());
                                            intent.putExtra(Constants.TokenId,currentTokenId);
                                            intent.putExtra(Constants.tokenautoLogin,true);
                                            intent.putExtra(Constants.tokenUserName,teacher.getTeacherPhone());
                                            intent.putExtra(Constants.tokenPassword,teacher.getTeacherPassword());
                                            startActivity(intent);
                                        }
                                    }
                                });

                            }else{
                                drivingSchool school = task.getResult().toObject(drivingSchool.class);
                                Intent intent = new Intent(getBaseContext(), schoolPagesNavigator.class);
                                intent.putExtra(Constants.schoolName, school.getSchoolName());
                                intent.putExtra(Constants.schoolPhone, school.getSchoolPhone());
                                intent.putExtra(Constants.schoolLoaction, school.getSchoolLocation());
                                intent.putExtra(Constants.schoolId, tokenDetails.getTokenSchoolId());
                                intent.putExtra(Constants.TokenId,currentTokenId);
                                intent.putExtra(Constants.tokenautoLogin,true);
                                intent.putExtra(Constants.tokenUserName,school.getSchoolEmail());
                                intent.putExtra(Constants.tokenPassword,school.getSchoolPassword());
                                startActivity(intent);
                            }
                        }
                    }
                    else
                    {
                        startActivity(new Intent(getBaseContext(),LoginActivity.class));
                    }
                }else
                {
                    startActivity(new Intent(getBaseContext(),LoginActivity.class));
                }
            }
        });

    }*/

}
