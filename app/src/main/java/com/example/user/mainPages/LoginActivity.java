package com.example.user.mainPages;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;

import com.chaos.view.PinView;
import com.google.android.material.textfield.TextInputLayout;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import databaseClasses.Constants;
import databaseClasses.userToken;
import pupilPackage.pupilPagesNavigator;
import schoolPackage.schoolPagesNavigator;
import teacherPackage.teacherPagesNavigator;

public class LoginActivity extends Activity {

    private FirebaseAuth mAuth;
    protected EditText inputEmail,inputPhone, inputPassword;
    private TextInputLayout inputLayoutEmail,inputLayoutPhone, inputLayoutPassword;
    private ImageView viaPhone;
    protected Button sendPhoneCode,returnToMail,continueTSignPhone,resendCode,btnSignInviaMail,singUp;
    private PinView pinView;
    private TextView txtForgotPassword;
    private FirebaseFirestore db;
    protected String currentTokenId;
    SweetAlertDialog pDialog ;
    private String phoneNumber,mail,Password,Pin;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private String verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pDialog= new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#00a5ff"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        setContentView(R.layout.login);
        db=FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.textInputEmail);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.textInputPassword);
        inputLayoutPhone = (TextInputLayout) findViewById(R.id.textInputPhone);

        inputEmail = (EditText) findViewById(R.id.editTextEmail);
        inputPassword = (EditText) findViewById(R.id.editTextPassword);
        inputPhone =(EditText) findViewById(R.id.editTextPhone);

        btnSignInviaMail = (Button) findViewById(R.id.cirLoginButton);
        singUp = (Button) findViewById(R.id.signUpSchool);
        txtForgotPassword = (TextView) findViewById(R.id.forgotPassTxt);

        sendPhoneCode=(Button)findViewById(R.id.sendCode);
        returnToMail=(Button)findViewById(R.id.returnToMail);
        continueTSignPhone=(Button)findViewById(R.id.loginViaPhone);
        resendCode=(Button)findViewById(R.id.resendCode);

        pinView=(PinView)findViewById(R.id.pinView);
        viaPhone=(ImageView)findViewById(R.id.vvPhone);

        inputPhone.addTextChangedListener(new MyTextWatcher(inputPhone));
        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));

        setViaPhoneListener();
        setSendPhoneCodeListener();
        setContinueSignViaPhoneListener();
        setResendCOdeListener();
        setReturnToMailListener();
        setTokenId();
        singUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterSchoolActivity.class);
                startActivity(intent);
            }
        });

        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,ResetPasswordActivity.class);
                startActivity(intent);
            }
        });

        this.currentTokenId=OpeningPage.token;

    }

    private void setTokenId() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(task.isSuccessful()){
                    currentTokenId=task.getResult().getToken();
                }
            }
        });
    }

    private void setResendCOdeListener() {
        resendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPhoneCode.performClick();
            }
        });
    }

    private void setReturnToMailListener() {
        returnToMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.loginByMail).setVisibility(View.VISIBLE);
                findViewById(R.id.loginByPhone).setVisibility(View.GONE);
            }
        });
    }

    private void setContinueSignViaPhoneListener() {
        continueTSignPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pin=pinView.getText().toString().trim();
                if (verificationCode != null) {
                    pDialog.show();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, Pin);
                    mAuth.signInWithCredential(credential)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        db.collection(Constants.UIDS).document(mAuth.getCurrentUser().getUid().toString()).
                                                get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            private int counterSchoolsOfTeacher;
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful() && task.getResult().exists()) {
                                                    String typeOfUser = task.getResult().get(Constants.typeOfUser).toString();
                                                    if (typeOfUser.equals(Constants.schoolType)) {
                                                        FirebaseInstanceId.getInstance().getInstanceId()
                                                                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                                        if (task.isSuccessful()) {
                                                                            String tokenId = task.getResult().getToken();
                                                                            userToken myNewToken = new userToken();
                                                                            myNewToken.setTokenId(tokenId);
                                                                            db.collection(Constants.drivingSchool).document(mAuth.getCurrentUser().
                                                                                    getUid().toString()).collection(Constants.myTokens).document(tokenId).
                                                                                    set(myNewToken);
                                                                            pDialog.dismiss();
                                                                            schoolPagesNavigator.schoolUId = mAuth.getCurrentUser().getUid();
                                                                            startActivity(new Intent(LoginActivity.this,
                                                                                    schoolPagesNavigator.class));
                                                                            finish();
                                                                        }
                                                                    }
                                                                });
                                                    }else{
                                                        if(typeOfUser.equals(Constants.teacherType)){
                                                            ArrayList<String> schoolsUIds=(ArrayList<String>) task.getResult().get(Constants.schoolsUIDs);
                                                            teacherPagesNavigator.schoolUIDs=schoolsUIds;

                                                            userToken myNewToken = new userToken();
                                                            myNewToken.setTokenId(currentTokenId);
                                                            int counter=schoolsUIds.size();
                                                            counterSchoolsOfTeacher=counter;
                                                            for(String schoolId:schoolsUIds) {
                                                                db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(mAuth.getCurrentUser().getUid()
                                                                        .toString()).collection(Constants.myTokens).document(currentTokenId).set(myNewToken).
                                                                        addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                counterSchoolsOfTeacher--;
                                                                                if(counterSchoolsOfTeacher==0) {
                                                                                    pDialog.dismiss();
                                                                                    startActivity(new Intent(LoginActivity.this, teacherPagesNavigator.class));
                                                                                    finish();
                                                                                }
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                        else{
                                                            userToken myNewToken = new userToken();
                                                            String schoolId=task.getResult().get(Constants.schoolId).toString();
                                                            String teacherId=task.getResult().get(Constants.teacherId).toString();
                                                            pupilPagesNavigator.schoolId=schoolId;
                                                            pupilPagesNavigator.teacherId=teacherId;
                                                            pupilPagesNavigator.pupilId=mAuth.getCurrentUser().getUid().toString();
                                                            myNewToken.setTokenId(currentTokenId);
                                                            db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(teacherId).collection(
                                                                    Constants.pupils).document(mAuth.getCurrentUser().getUid().toString()).collection(Constants.myTokens)
                                                                    .document(currentTokenId).set(myNewToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    pDialog.dismiss();
                                                                    startActivity(new Intent(LoginActivity.this, pupilPagesNavigator.class));
                                                                    finish();
                                                                }
                                                            });
                                                        }
                                                    }
                                                } else {
                                                    pDialog.dismiss();
                                                }
                                            }
                                        });

                                    } else {
                                        pDialog.dismiss();
                                        //verification unsuccessful.. display an error message

                                        String message = "Somthing is wrong, we will fix it soon...";

                                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                            message = "Invalid code entered...";
                                        }
                                        new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Oops...")
                                                .setContentText(message)
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                        sweetAlertDialog.dismiss();
                                                    }
                                                })
                                                .show();
                                    }

                                }
                            });
                } else {
                    pDialog.dismiss();
                }
            }
        });
    }

    private void setSendPhoneCodeListener() {
        sendPhoneCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validatePhone()){
                    pDialog.show();
                    db.collection(Constants.registeredPhones).document(phoneNumber).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful() && task.getResult().exists()){
                                mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                                    @Override
                                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                                        String codeT = phoneAuthCredential.getSmsCode();

                                        //sometime the code is not detected automatically
                                        //in this case the code will be null
                                        //so user has to manually enter the code
                                        if (codeT != null && codeT!="") {
                                            pDialog.dismiss();
                                            findViewById(R.id.loginByPhone).setVisibility(View.GONE);
                                            findViewById(R.id.verifyCode).setVisibility(View.VISIBLE);
                                            pinView.setText(codeT);
                                            //verifying the code
                                            continueTSignPhone.performClick();
                                        }else{
                                            pDialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onVerificationFailed(FirebaseException e) {
                                        pDialog.dismiss();
                                        new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                .setTitleText("verification fialed")
                                                .setContentText(e.toString())
                                                .show();
                                    }

                                    @Override
                                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                        super.onCodeSent(s, forceResendingToken);
                                        pDialog.dismiss();
                                        verificationCode = s;
                                        findViewById(R.id.loginByPhone).setVisibility(View.GONE);
                                        findViewById(R.id.verifyCode).setVisibility(View.VISIBLE);
                                    }
                                };
                                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                        "+972"+phoneNumber,                     // Phone number to verify
                                        60,                           // Timeout duration
                                        TimeUnit.SECONDS,                // Unit of timeout
                                        LoginActivity.this,        // Activity (for callback binding)
                                        mCallback);
                            }else{
                                pDialog.dismiss();
                                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("משתמש לא נמצא במערכת")
                                    .setConfirmText("ok")
                                    .show();
                        }
                        }
                    });
                }
            }
        });
    }

    private void setViaPhoneListener() {
        viaPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.loginByMail).setVisibility(View.GONE);
                findViewById(R.id.loginByPhone).setVisibility(View.VISIBLE);
            }
        });
    }


    public void checkInsertedData(View view) {
        final String userName=inputEmail.getText().toString();
        final String password=inputPassword.getText().toString();
        pDialog.show();
        if(submitForm()) {
            LogInIfExist(userName,password);
        }
        else
        {
            pDialog.dismiss();
        }
    }
    public void LogInIfExist(String userName,String password){
        mAuth.signInWithEmailAndPassword(userName, password).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pDialog.dismiss();
                        if (task.isSuccessful()) {
                            if (mAuth.getCurrentUser().isEmailVerified()) {
                                db.collection(Constants.UIDS).document(mAuth.getCurrentUser().getUid().toString()).get().addOnCompleteListener(
                                        new OnCompleteListener<DocumentSnapshot>() {
                                            private int counterSchoolsOfTeacher;

                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.isSuccessful() && task.getResult().exists()){
                                                    String userType=task.getResult().get(Constants.typeOfUser).toString();
                                                    if(userType.equals(Constants.teacherType)){
                                                        ArrayList<String> schoolsUIds=(ArrayList<String>) task.getResult().get(Constants.schoolsUIDs);
                                                        teacherPagesNavigator.schoolUIDs=schoolsUIds;

                                                        userToken myNewToken = new userToken();
                                                        myNewToken.setTokenId(currentTokenId);
                                                        int counter=schoolsUIds.size();
                                                        counterSchoolsOfTeacher=counter;
                                                        for(String schoolId:schoolsUIds) {
                                                            db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(mAuth.getCurrentUser().getUid()
                                                            .toString()).collection(Constants.myTokens).document(currentTokenId).set(myNewToken).
                                                                    addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    counterSchoolsOfTeacher--;
                                                                    if(counterSchoolsOfTeacher==0) {
                                                                        pDialog.dismiss();
                                                                        finish();
                                                                        startActivity(new Intent(LoginActivity.this, teacherPagesNavigator.class));
                                                                    }
                                                                    }
                                                            });
                                                        }
                                                    }else {
                                                        if (userType.equals(Constants.schoolType)) {
                                                            userToken myNewToken = new userToken();
                                                            myNewToken.setTokenId(currentTokenId);
                                                            db.collection(Constants.drivingSchool).document(mAuth.getCurrentUser().getUid().toString()).collection(Constants.myTokens).document(currentTokenId).
                                                                    set(myNewToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    pDialog.dismiss();
                                                                    startActivity(new Intent(LoginActivity.this, schoolPagesNavigator.class));
                                                                    finish();
                                                                }
                                                            });
                                                        }else{
                                                            userToken myNewToken = new userToken();
                                                            String schoolId=task.getResult().get(Constants.schoolId).toString();
                                                            String teacherId=task.getResult().get(Constants.teacherId).toString();
                                                            pupilPagesNavigator.schoolId=schoolId;
                                                            pupilPagesNavigator.teacherId=teacherId;
                                                            pupilPagesNavigator.pupilId=mAuth.getCurrentUser().getUid().toString();
                                                            myNewToken.setTokenId(currentTokenId);
                                                            db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(teacherId).collection(
                                                                    Constants.pupils).document(mAuth.getCurrentUser().getUid().toString()).collection(Constants.myTokens)
                                                                    .document(currentTokenId).set(myNewToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    pDialog.dismiss();
                                                                    startActivity(new Intent(LoginActivity.this, pupilPagesNavigator.class));
                                                                    finish();
                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                );

                            } else {
                                pDialog.dismiss();
                                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("אמת אימייל שלך כדי שתוכל לכנס!")
                                        .show();
                            }
                        } else {
                            pDialog.dismiss();
                            new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText(task.getException().getMessage())
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismiss();
                                        }
                                    })
                                    .show();
                        }
                    }
                });
    }


    /**
     * Validating form
     */
    private boolean submitForm() {
        if (!validateEmail()) {
            return false;
        }

        if (!validatePassword()) {
            return false;
        }
    return true;
    }


    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty() || (!isValidMail(email) && !isValidMobile(email) ) ) {
            inputLayoutEmail.setError("מלא אימייל או טלפון שלך");
            requestFocus(inputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean isValidMobile(String phone) {
        return Patterns.PHONE.matcher(phone).matches();
    }
    private boolean isValidMail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean validatePassword() {
        if (inputPassword.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError("מלא סיסמה");
            requestFocus(inputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }
    private boolean validatePhone() {
        phoneNumber =inputPhone.getText().toString().trim();
        if(phoneNumber.isEmpty() || !isValidMobile(phoneNumber)){
            inputLayoutPhone.setError("מלא טלפון");
            requestFocus(inputPhone);
            return false;
        }else{
            inputLayoutPhone.setErrorEnabled(false);
        }
        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.editTextEmail:
                    validateEmail();
                    break;
                case R.id.editTextPassword:
                    validatePassword();
                    break;
                case R.id.editTextPhone:
                    validatePhone();
                    break;
            }
        }
    }


}

