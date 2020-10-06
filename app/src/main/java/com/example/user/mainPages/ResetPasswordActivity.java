package com.example.user.mainPages;

import android.content.Intent;
import android.graphics.Color;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.TimeUnit;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import cn.pedant.SweetAlert.SweetAlertDialog;
import databaseClasses.Constants;

public class ResetPasswordActivity extends AppCompatActivity {
  //  private RadioButton resetByMailBtn;
//    private RadioButton resetByPhoneBtn;
    private TextInputLayout inputLayoutPhone ,inputLayoutPassword , inputLayoutRepassword ,inputLayoutMail;
    private CircularProgressButton btnsendCode;
    private TextView backToLoginMail;
    private String mVerificationId;
    private TextView txtSignIn;
    private EditText inputPhone, inputPassword, inputRePassword, inputMail;
    private TextView answerText;
    private FirebaseFirestore db;
    private String teacherUID;
    private String pupilUID;
    private String schoolUID;
    private PinView pin;
    private TextView backImage;
    private CircularProgressButton btnVerify;
    private TextView resendCode;
    private CircularProgressButton verMailBtn;
    private CircularProgressButton backToLogin;
    private FirebaseAuth mAuth;;
    private TextView answerMail;
    private TextView returnToLogin;
    private static boolean isfound=false;

    interface oncompleterunning {
        void afterfinishRunningDb(boolean isfound);
        void setSchoolCount(int schoolCount,int iSchool);
        void setTeacherCount(int teacherCount,int iTeacher);
        void setTeacherCount2(int teacherCount2,int iTeacher2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resetpassword);
        inputLayoutPassword=(TextInputLayout) findViewById(R.id.textInputPassword);
        inputLayoutRepassword=(TextInputLayout) findViewById(R.id.textInputRePassword);
        inputLayoutMail = (TextInputLayout) findViewById(R.id.textInputEmail) ;
        inputMail = (EditText)findViewById(R.id.editTextEmail);
        returnToLogin = (TextView) findViewById(R.id.returntoLogin);
        inputMail.addTextChangedListener(new MyTextWatcher(inputMail));
        backToLogin=(CircularProgressButton)findViewById(R.id.loginBtn) ;
        resendCode=(TextView)findViewById(R.id.resendCode);
        backImage=(TextView) findViewById(R.id.backToLogin);
        inputPassword = (EditText) findViewById(R.id.editTextPassword);
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));
        verMailBtn=(CircularProgressButton) findViewById(R.id.cirmailSendV);
        answerMail=(TextView)findViewById(R.id.answerOfMail);
        inputRePassword = (EditText) findViewById(R.id.editTextRePassword);
        inputRePassword.addTextChangedListener(new MyTextWatcher(inputRePassword));

        mAuth = FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        pin=(PinView)findViewById(R.id.pinView);
        answerText = (TextView) findViewById(R.id.answer);
      //  resetByMailBtn = (RadioButton) findViewById(R.id.byMail);
     //   resetByPhoneBtn = (RadioButton) findViewById(R.id.byPhone);
        inputLayoutPhone=(TextInputLayout)findViewById(R.id.textInputPhone);

        inputPhone=(EditText) findViewById(R.id.editTextPhone);
        inputPhone.addTextChangedListener(new MyTextWatcher(inputPhone));

        txtSignIn=(TextView)findViewById(R.id.backToLogin);
        btnsendCode = (CircularProgressButton) findViewById(R.id.phoneSendV);
        btnVerify=(CircularProgressButton) findViewById(R.id.verifyBtn);
        setRaioButtonListeners();
    //    resetByPhoneBtn.setChecked(true);

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnVerify.startAnimation();
                verifyOnClick();
            }
        });

        returnToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ResetPasswordActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ResetPasswordActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        resendCode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isfound=false;

                findAndSend(new oncompleterunning() {
                    int countSchool = 0;
                    int iSchool = -1;
                    int countTeacher = 0;
                    int iTeacher = -1;
                    int iTeaacher2 = 0;
                    int countTeacher2 = 0;

                    @Override
                    public void afterfinishRunningDb(boolean isfound) {
                        if (!isfound && iSchool == countSchool && iTeacher == countTeacher && iTeaacher2 == countTeacher2) {
                            stopButtonAnimation();
                            new SweetAlertDialog(ResetPasswordActivity.this)
                                    .setTitleText("מספר טלפון לא נמצא במערכת!")
                                    .show();
                        } else {
                            if (isfound) {
                                stopButtonAnimation();
                                btnsendCode.setVisibility(View.GONE);
                                btnVerify.setVisibility(View.VISIBLE);

                                findViewById(R.id.phoneInput).setVisibility(View.GONE);
                                findViewById(R.id.verificationCode).setVisibility(View.VISIBLE);

                            }
                        }
                    }

                    @Override
                    public void setSchoolCount(int schoolCount, int iSchool) {
                        this.countSchool = schoolCount;
                        this.iSchool = iSchool;
                    }

                    @Override
                    public void setTeacherCount(int teacherCount, int iTeacher) {
                        this.iTeacher = iTeacher;
                        this.countTeacher = teacherCount;
                    }

                    @Override
                    public void setTeacherCount2(int teacherCount2, int iTeacher2) {
                        this.countTeacher2 = teacherCount2;
                        this.iTeaacher2 = iTeacher2;
                    }
                });
            }
        });
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(findViewById(R.id.verificationCode).getVisibility()==View.VISIBLE){
                    findViewById(R.id.verificationCode).setVisibility(View.GONE);
                    findViewById(R.id.phoneInput).setVisibility(View.VISIBLE);
                    inputPassword.setText("");
                    inputRePassword.setText("");
                    inputPhone.setText("");

                    btnsendCode.setVisibility(View.VISIBLE);
                    btnVerify.setVisibility(View.GONE);
                }else
                {
                    Intent intent=new Intent(ResetPasswordActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        findViewById(R.id.phoneResetLayout).setVisibility(View.GONE);
        findViewById(R.id.mailResetLayout).setVisibility(View.VISIBLE);
    }

    public void sendCondOnClick(View view){
        isfound=false;
            btnsendCode.startAnimation();
            if (submitForm()) {
                findAndSend(new oncompleterunning() {
                    int countSchool = 0;
                    int iSchool = -1;
                    int countTeacher = 0;
                    int iTeacher = -1;
                    int iTeaacher2 = 0;
                    int countTeacher2 = 0;

                    @Override
                    public void afterfinishRunningDb(boolean isfound) {
                        if (!isfound && iSchool == countSchool && iTeacher == countTeacher && iTeaacher2 == countTeacher2) {
                            stopButtonAnimation();
                            new SweetAlertDialog(ResetPasswordActivity.this)
                                    .setTitleText("מספר טלפון לא נמצא במערכת!")
                                    .show();
                        }else
                        {
                            if (isfound) {
                                stopButtonAnimation();
                                btnsendCode.setVisibility(View.GONE);
                                btnVerify.setVisibility(View.VISIBLE);
                                findViewById(R.id.phoneInput).setVisibility(View.GONE);
                                findViewById(R.id.verificationCode).setVisibility(View.VISIBLE);

                            }
                        }
                    }

                    @Override
                    public void setSchoolCount(int schoolCount, int iSchool) {
                        this.countSchool = schoolCount;
                        this.iSchool = iSchool;
                    }

                    @Override
                    public void setTeacherCount(int teacherCount, int iTeacher) {
                        this.iTeacher = iTeacher;
                        this.countTeacher = teacherCount;
                    }

                    @Override
                    public void setTeacherCount2(int teacherCount2, int iTeacher2) {
                        this.countTeacher2 = teacherCount2;
                        this.iTeaacher2 = iTeacher2;
                    }
                });
            } else {
                stopButtonAnimation();
            }

    }
    public void findAndSend(final oncompleterunning listener){
        db.collection(Constants.drivingSchool).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                    if(!task.getResult().isEmpty()){
                        int count=0;
                        for(final QueryDocumentSnapshot doc:task.getResult()){
                            int i=task.getResult().size();
                            count++;
                            listener.setSchoolCount(i,count);
                            doc.getReference().collection(Constants.teachers).whereEqualTo(Constants.teacherPhone,
                                    inputPhone.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                    if(task.isSuccessful() && !task.getResult().isEmpty()){
                                        for(QueryDocumentSnapshot doc1:task.getResult()){
                                            teacherUID=doc1.getId().toString();
                                        }
                                        isfound=true;
                                        schoolUID=doc.getId().toString();
                                        String phone = inputPhone.getText().toString();
                                        btnsendCode.startAnimation();
                                        listener.afterfinishRunningDb(isfound);
                                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                                "+972" + phone,
                                                60,
                                                TimeUnit.SECONDS,
                                                TaskExecutors.MAIN_THREAD,
                                                mCallbacks);

                                    }else
                                    {
                                        doc.getReference().collection("teachers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful() && !task.getResult().isEmpty()){
                                                    int count =0;
                                                    for(final QueryDocumentSnapshot doc1 : task.getResult()){
                                                        int i=task.getResult().size();
                                                        count++;
                                                        listener.setTeacherCount(i,count);
                                                        doc1.getReference().collection("pupils").whereEqualTo("phone",inputPhone.getText().toString())
                                                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if(task.isSuccessful() && !task.getResult().isEmpty()) {
                                                                    for (QueryDocumentSnapshot doc2 : task.getResult()) {
                                                                        pupilUID = doc2.getId();
                                                                    }
                                                                    isfound=true;
                                                                    teacherUID = doc1.getId().toString();
                                                                    schoolUID = doc.getId().toString();
                                                                    btnsendCode.startAnimation();
                                                                    String phone = inputPhone.getText().toString();
                                                                    listener.afterfinishRunningDb(isfound);
                                                                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                                                            "+972" + phone,
                                                                            60,
                                                                            TimeUnit.SECONDS,
                                                                            TaskExecutors.MAIN_THREAD,
                                                                            mCallbacks);

                                                                } else{
                                                                    listener.afterfinishRunningDb(isfound);
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
            }
        });
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            //Getting the code sent by SMS
            isfound=true;
            String codeT = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (codeT != null && codeT!="") {
                stopButtonAnimation();
                pin.setText(codeT);
                //verifying the code
                btnVerify.performClick();
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            stopButtonAnimation();
            isfound=true;
            Toast.makeText(ResetPasswordActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            isfound=true;
            stopButtonAnimation();
             mVerificationId = s;
        }
    };

    private void stopButtonAnimation(){
        btnsendCode.revertAnimation();
        btnVerify.revertAnimation();
    }
    public boolean submitForm() {
        if (!validatePhone()) {
            return false;
        }
        if(!validaPass()){
            return false;
        }
        if(!validateRepassword()){
            return false;
        }

        return true;
    }

    public boolean validaPass() {
        if (inputPassword.getText().toString().isEmpty()) {
            inputLayoutPassword.setError("מלא סיסמא");
            requestFocus(inputLayoutPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }
        return true;
    }
        public boolean validateRepassword(){
        if(inputRePassword.getText().toString().isEmpty()){
            inputLayoutRepassword.setError("מלא סיסמא");
            requestFocus(inputRePassword);
            return false;
        }
        else {
            inputLayoutRepassword.setErrorEnabled(false);
        }
        if(!inputPassword.getText().toString().equals(inputRePassword.getText().toString())){
            inputLayoutRepassword.setError("סיסמאות לא דומות");
            requestFocus(inputRePassword);
            return false;
        }
        else {
            inputLayoutRepassword.setErrorEnabled(false);
        }
        return true;
    }

    public boolean validatePin(){
        if(pin.getText().toString().isEmpty() || pin.getText().toString().length()<4){
            return false;
        }
        return true;
    }
    public void verifyOnClick() {
        if(validatePin()) {
            if (mVerificationId != null) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, pin.getText().toString().trim());
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(ResetPasswordActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                   resetOnClick();
                                } else {
                                    stopButtonAnimation();
                                    //verification unsuccessful.. display an error message

                                    String message = "Somthing is wrong, we will fix it soon...";

                                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                        message = "Invalid code entered...";
                                    }
                                    answerText.setTextColor(Color.RED);
                                    new SweetAlertDialog(ResetPasswordActivity.this, SweetAlertDialog.ERROR_TYPE)
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
            }
        }
        else
        {
            new SweetAlertDialog(this)
                    .setTitleText("מלא את קוד האימות")
                    .show();
        }
    }

    public  void resetOnClick() {
        final String pass = inputPassword.getText().toString();
            if (pupilUID != null) {
                db.collection(Constants.drivingSchool).document(schoolUID).collection(Constants.teachers).document(teacherUID).
                        collection(Constants.pupils).document(pupilUID).update(Constants.pupilPassword, pass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        stopButtonAnimation();
                        btnVerify.setVisibility(View.GONE);
                        backToLogin.setVisibility(View.VISIBLE);
                        new SweetAlertDialog(ResetPasswordActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Good job!")
                                .setContentText("הסיסמה כבר שונתה")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                    }
                                })
                                .show();
                    }
                });
            } else {
                db.collection(Constants.drivingSchool).document(schoolUID).collection(Constants.teachers).document(teacherUID).
                        update(Constants.teacherPassword, pass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        stopButtonAnimation();
                        btnVerify.setVisibility(View.GONE);
                        backToLogin.setVisibility(View.VISIBLE);
                        new SweetAlertDialog(ResetPasswordActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Good job!")
                                .setContentText("הסיסמה כבר שונתה")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                    }
                                })
                                .show();
                    }
                });

        }
    }

    private boolean validatePhone() {
        String phone = inputPhone.getText().toString().trim();

        if (phone.isEmpty()  || !isValidMobile(phone)) {
            inputLayoutPhone.setError("מלא מספר טלפון שלך");
            requestFocus(inputPhone);
            return false;
        } else {
            inputLayoutPhone.setErrorEnabled(false);
        }

        return true;
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    private boolean isValidMobile(String phone) {
        return Patterns.PHONE.matcher(phone).matches();
    }

    public void setRaioButtonListeners(){
//        resetByMailBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(findViewById(R.id.mailResetLayout).getVisibility()==View.VISIBLE){
//                    return;
//                }else {
//                    findViewById(R.id.phoneResetLayout).setVisibility(View.GONE);
//                    findViewById(R.id.mailResetLayout).setVisibility(View.VISIBLE);
//                }
//            }
//        });

//        resetByPhoneBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(findViewById(R.id.phoneResetLayout).getVisibility()==View.VISIBLE){
//                    return;
//                }else {
//                    findViewById(R.id.mailResetLayout).setVisibility(View.GONE);
//                    findViewById(R.id.phoneResetLayout).setVisibility(View.VISIBLE);
//                }
//            }
//        });
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
                case R.id.editTextPassword:
                    validaPass();
                    break;
                case R.id.editTextRePassword:
                    validateRepassword();
                    break;
                case R.id.editTextPhone:
                    validatePhone();
                    break;
                case R.id.editTextEmail:
                    validateEmail();
                    break;
            }
        }
    }



    public void senVerMail(View view){
        verMailBtn.startAnimation();
        if(submitFormMail()){
            mAuth.sendPasswordResetEmail(inputMail.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                verMailBtn.revertAnimation();
                                verMailBtn.setVisibility(View.GONE);
                                backToLogin.setVisibility(View.VISIBLE);
                                new SweetAlertDialog(ResetPasswordActivity.this)
                                        .setTitleText("חידוש סיסמה כבר נשלח לך באמצעות המייל.")
                                        .show();


                            } else {
                                new SweetAlertDialog(ResetPasswordActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Oops...")
                                        .setContentText("נפילה בשליחת אימות סיסמה למייל נסה מחדש!")
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
        }else
        {
            verMailBtn.revertAnimation();
        }
    }

    private boolean submitFormMail() {
        if (!validateEmail()) {
            return false;
        }
        return true;
    }
    private boolean validateEmail() {
        String email = inputMail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email) ) {
            inputLayoutMail.setError("מלא אימייל שלך");
            requestFocus(inputMail);
            return false;
        } else {
            inputLayoutMail.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
