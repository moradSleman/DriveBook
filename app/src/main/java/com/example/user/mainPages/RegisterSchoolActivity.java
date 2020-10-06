package com.example.user.mainPages;

import android.content.Intent;
import androidx.annotation.NonNull;

import com.chaos.view.PinView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import databaseClasses.Constants;
import databaseClasses.drivingSchool;
import databaseClasses.userToken;
import schoolPackage.schoolPagesNavigator;

public class RegisterSchoolActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String schoolName,schoolLocation,schoolMail,schoolPass,schoolPhone;
    private EditText inputSchoolName,inputSchoolLocation,inputPhone,inputEmail, inputPassword,inputRepassword;
    private TextInputLayout inputLayoutSchoolName,inputLayoutschoolLocation,inputLayoutPhone,inputLayoutEmail, inputLayoutPassword,inputLayoutRepassword;
    private Button continue1,continueMail,sendCodePhone,continuePhone, signInMail,haveAccountSign, cancelMailPhone,resendCode;
    private TextView reSendMail,mail;
    private PinView pin;
    private CheckBox privacyAccept;
    private SwitchMaterial switchMailPhone;
    SweetAlertDialog pDialog ;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private String verificationCode;
    private ImageView exit;
    private String phonePin;
    private Button signInPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.school_register);

        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.textInputMail);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);
        inputLayoutRepassword=(TextInputLayout)findViewById(R.id.TextInputLayoutRePassword);
        inputLayoutSchoolName=(TextInputLayout)findViewById(R.id.textInputNameSchool);
        inputLayoutschoolLocation=(TextInputLayout)findViewById(R.id.textInputLocation);
        inputLayoutPhone=(TextInputLayout)findViewById(R.id.textInputLayoutPhone);

        inputEmail = (EditText) findViewById(R.id.editTextMail);
        inputPassword = (EditText) findViewById(R.id.editTextPassword);
        inputRepassword=(EditText)findViewById(R.id.editTextRePassword);
        inputSchoolName=(EditText)findViewById(R.id.editTextNameSchool);
        inputSchoolLocation=(EditText)findViewById(R.id.editTextLocation);
        inputPhone=(EditText)findViewById(R.id.editTextPhone);


        continue1=(Button)findViewById(R.id.continue1);
        continueMail=(Button)findViewById(R.id.continueMail);
        sendCodePhone=(Button)findViewById(R.id.sendPhoneCode);
        continuePhone=(Button)findViewById(R.id.continuePhone);
        signInMail =(Button)findViewById(R.id.signIn);
        haveAccountSign=(Button)findViewById(R.id.haveAccountSign);
        cancelMailPhone =(Button)findViewById(R.id.cancelMailPhone);
        resendCode=(Button)findViewById(R.id.resendCode);
        signInPhone=(Button)findViewById(R.id.signInPhone);

        reSendMail=(TextView)findViewById(R.id.reSendMail);
        mail=(TextView)findViewById(R.id.email);
        pin=(PinView) findViewById(R.id.pinView);
        privacyAccept=(CheckBox) findViewById(R.id.privace);
        switchMailPhone=(SwitchMaterial)findViewById(R.id.isByMail);
        exit=(ImageView)findViewById(R.id.exit) ;

        inputEmail.addTextChangedListener(new RegisterSchoolActivity.MyTextWatcher(inputEmail));
        inputPassword.addTextChangedListener(new RegisterSchoolActivity.MyTextWatcher(inputPassword));
        inputRepassword.addTextChangedListener(new RegisterSchoolActivity.MyTextWatcher(inputRepassword));
        inputPhone.addTextChangedListener(new RegisterSchoolActivity.MyTextWatcher(inputPhone));
        inputSchoolLocation.addTextChangedListener(new RegisterSchoolActivity.MyTextWatcher(inputSchoolLocation));
        inputSchoolName.addTextChangedListener(new RegisterSchoolActivity.MyTextWatcher(inputSchoolName));

        pDialog= new SweetAlertDialog(RegisterSchoolActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#00a5ff"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);

        setCancelRegisterListener();
        setContinue1Listener();
        setSwitchListener();
        setContinueMailListener();
        setResenMailListener();
        setSendPhoneCodeListener();
        setSignInListener();
        setCancelRegister();
        setResndCondLisetner();
        setcontinuePhoneListener();
        setExitOnClickListener();
    }

    private void setExitOnClickListener() {
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.R3).setVisibility(View.GONE);
                findViewById(R.id.signIn).setVisibility(View.VISIBLE);
                findViewById(R.id.signInPhone).setVisibility(View.GONE);
                findViewById(R.id.R5).setVisibility(View.VISIBLE);
            }
        });
    }

    private void setResndCondLisetner() {
        resendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCodePhone.performClick();
            }
        });
    }

    private void setCancelRegister() {
        cancelMailPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterSchoolActivity.this,LoginActivity.class));
            }
        });
    }

    private void setSignInListener() {
            signInMail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pDialog.show();
                    mAuth.signInWithEmailAndPassword(schoolMail,schoolPass).addOnCompleteListener(
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        if (mAuth.getCurrentUser().isEmailVerified()) {
                                            FirebaseInstanceId.getInstance().getInstanceId()
                                                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                            if (task.isSuccessful()) {
                                                                String tokenId= task.getResult().getToken();
                                                                userToken myNewToken = new userToken();
                                                                myNewToken.setTokenId(tokenId);
                                                                db.collection(Constants.drivingSchool).document(mAuth.getCurrentUser().getUid().toString()).collection(Constants.myTokens).document(tokenId).
                                                                        set(myNewToken);
                                                                pDialog.dismiss();
                                                                schoolPagesNavigator.schoolUId=mAuth.getCurrentUser().getUid();
                                                                startActivity(new Intent(RegisterSchoolActivity.this, schoolPagesNavigator.class));
                                                            }else{
                                                                pDialog.dismiss();
                                                            }
                                                        }
                                                    });

                                        } else {
                                            pDialog.dismiss();
                                            new SweetAlertDialog(RegisterSchoolActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                    .setTitleText("אמת אימייל שלך כדי שתוכל לכנס!")
                                                    .show();
                                        }
                                    } else {
                                        pDialog.dismiss();
                                        new SweetAlertDialog(RegisterSchoolActivity.this, SweetAlertDialog.ERROR_TYPE)
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
                            }
                    );
                    }
            });
            signInPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseInstanceId.getInstance().getInstanceId()
                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                @Override
                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                    if (task.isSuccessful()) {
                                        String tokenId = task.getResult().getToken();
                                        userToken myNewToken = new userToken();
                                        myNewToken.setTokenId(tokenId);
                                        db.collection(Constants.drivingSchool).document(mAuth.getCurrentUser().getUid().toString()).collection(Constants.myTokens).document(tokenId).
                                                set(myNewToken);
                                        pDialog.dismiss();
                                        schoolPagesNavigator.schoolUId=mAuth.getCurrentUser().getUid();
                                        startActivity(new Intent(RegisterSchoolActivity.this, schoolPagesNavigator.class));
                                    }else{
                                        pDialog.dismiss();
                                    }
                                }
                            });
                }
            });

    }

    private void setSendPhoneCodeListener() {
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                String codeT = phoneAuthCredential.getSmsCode();

                //sometime the code is not detected automatically
                //in this case the code will be null
                //so user has to manually enter the code
                if (codeT != null && codeT!="") {
                    pDialog.dismiss();
                    findViewById(R.id.R2).setVisibility(View.GONE);
                    findViewById(R.id.R4).setVisibility(View.VISIBLE);
                    pin.setText(codeT);
                    //verifying the code
                    continuePhone.performClick();
                }else{
                    pDialog.dismiss();
                }
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                pDialog.dismiss();
                new SweetAlertDialog(RegisterSchoolActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("verification fialed")
                        .setContentText(e.toString())
                        .show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                pDialog.dismiss();
                verificationCode = s;
                findViewById(R.id.R2).setVisibility(View.GONE);
                findViewById(R.id.R4).setVisibility(View.VISIBLE);
            }
        };
        sendCodePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog.show();
                if(validatePhone()){
                            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                    "+972"+schoolPhone,                     // Phone number to verify
                                    60,                           // Timeout duration
                                    TimeUnit.SECONDS,                // Unit of timeout
                                    RegisterSchoolActivity.this,        // Activity (for callback binding)
                                    mCallback);                      // OnVerificationStateChangedCallbacks

                }
            }
        });
    }

    private void setcontinuePhoneListener() {
        continuePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validatePin()) {
                    pDialog.show();
                    if (verificationCode != null && !phonePin.isEmpty()) {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, phonePin);
                        mAuth.signInWithCredential(credential)
                                .addOnCompleteListener(RegisterSchoolActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            drivingSchool schoolToAdd=new drivingSchool();
                                            schoolToAdd.setSchoolLocation(schoolLocation);
                                            schoolToAdd.setSchoolName(schoolName);
                                            schoolToAdd.setSchoolPhone(schoolPhone);
                                            db.collection(Constants.drivingSchool).document(mAuth.getCurrentUser().getUid().toString()).
                                                    set(schoolToAdd).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Map<String, Object> user = new HashMap<>();
                                                        user.put(Constants.typeOfUser, Constants.schoolType);
                                                        db.collection(Constants.UIDS).document(mAuth.getCurrentUser().getUid().toString()).set(user).addOnCompleteListener(
                                                                new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Map<String, Object> phone = new HashMap<>();
                                                                            phone.put(Constants.registeredPhoneNumber, schoolPhone);
                                                                            db.collection(Constants.registeredPhones).document(schoolPhone.toString()).set(phone).addOnCompleteListener(
                                                                                    new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()) {
                                                                                                pDialog.dismiss();
                                                                                                findViewById(R.id.R4).setVisibility(View.GONE);
                                                                                                findViewById(R.id.signIn).setVisibility(View.GONE);
                                                                                                findViewById(R.id.signInPhone).setVisibility(View.VISIBLE);
                                                                                                findViewById(R.id.R5).setVisibility(View.VISIBLE);
                                                                                            }else{
                                                                                                pDialog.dismiss();
                                                                                            }
                                                                                        }
                                                                                    }
                                                                            );

                                                                        } else {
                                                                            pDialog.dismiss();
                                                                        }
                                                                    }
                                                                }
                                                        );


                                                    }
                                                    pDialog.dismiss();
                                                }
                                            });
                                        } else {
                                           pDialog.dismiss();
                                            //verification unsuccessful.. display an error message

                                            String message = "Somthing is wrong, we will fix it soon...";

                                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                                message = "Invalid code entered...";
                                            }
                                            new SweetAlertDialog(RegisterSchoolActivity.this, SweetAlertDialog.ERROR_TYPE)
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
                    }else{
                        pDialog.dismiss();
                    }
                }
            }
        });

    }

    private void setResenMailListener() {
        reSendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUserAndVerifyEmail(schoolMail,schoolPass);
            }
        });
    }

    private void setContinueMailListener() {
        continueMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(submitForm()){
                    pDialog.show();
                    createUserAndVerifyEmail(schoolMail,schoolPass);
                }
            }
        });
    }

    private void setSwitchListener() {
        switchMailPhone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    findViewById(R.id.mailViews).setVisibility(View.VISIBLE);
                    findViewById(R.id.continueMail).setVisibility(View.VISIBLE);
                    findViewById(R.id.textInputLayoutPhone).setVisibility(View.GONE);
                    findViewById(R.id.sendPhoneCode).setVisibility(View.GONE);
                }else{
                    findViewById(R.id.mailViews).setVisibility(View.GONE);
                    findViewById(R.id.continueMail).setVisibility(View.GONE);
                    findViewById(R.id.textInputLayoutPhone).setVisibility(View.VISIBLE);
                    findViewById(R.id.sendPhoneCode).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setContinue1Listener() {
        continue1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateSchooName() && validateLocation()){
                    schoolName=inputSchoolName.getText().toString();
                    schoolLocation=inputSchoolLocation.getText().toString();
                    findViewById(R.id.R1).setVisibility(View.GONE);
                    findViewById(R.id.R2).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setCancelRegisterListener() {
        haveAccountSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterSchoolActivity.this,LoginActivity.class));
            }
        });
    }

    public void createUserAndVerifyEmail(final String emailtxt, final String passwordTxt) {
        mAuth.createUserWithEmailAndPassword(emailtxt, passwordTxt).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        drivingSchool schoolToAdd=new drivingSchool();
                                        schoolToAdd.setSchoolEmail(schoolMail);
                                        schoolToAdd.setSchoolLocation(schoolLocation);
                                        schoolToAdd.setSchoolName(schoolName);
                                        db.collection(Constants.drivingSchool).document(mAuth.getCurrentUser().getUid()).
                                                set(schoolToAdd).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Map<String, Object> user = new HashMap<>();
                                                    user.put(Constants.typeOfUser, Constants.schoolType);
                                                    db.collection(Constants.UIDS).document(mAuth.getCurrentUser().getUid().toString()).set(user).addOnCompleteListener(
                                                            new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    pDialog.dismiss();
                                                                    findViewById(R.id.R2).setVisibility(View.GONE);
                                                                    mail.setText(schoolMail);
                                                                    findViewById(R.id.R3).setVisibility(View.VISIBLE);
                                                                }
                                                            }
                                                    );


                                                    }else
                                                pDialog.dismiss();
                                            }
                                        });


                                    } else
                                    {
                                        pDialog.dismiss();
                                        new SweetAlertDialog(RegisterSchoolActivity.this, SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Oops...")
                                                .setContentText(task.getException().getMessage().toString())
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
                        } else
                        {
                            pDialog.dismiss();
                            new SweetAlertDialog(RegisterSchoolActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText(task.getException().getMessage().toString())
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
    public boolean validatePin(){
        String pin1=pin.getText().toString();
        phonePin=pin1;
        if(phonePin.isEmpty() || phonePin.length()<6){
            return false;
        }
        return true;
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

        if(!validateRePAssword()){
            return false;
        }

       return true;
    }


    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();
        schoolMail=email;
        if (email.isEmpty()) {
            inputLayoutEmail.setError("מלא אימייל");
            requestFocus(inputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateRePAssword() {
        String password= inputRepassword.getText().toString().trim();
        schoolPass=password;
        if (password.isEmpty()) {
            inputLayoutRepassword.setError("מלא סיסמה מחדש");
            requestFocus(inputRepassword);
            return false;
        } else {
            if(!inputRepassword.getText().toString().trim().equals(inputPassword.getText().toString().trim())){
                inputLayoutRepassword.setError("סיסמה אינה תואמת");
                requestFocus(inputRepassword);
                return false;
            }else
            {
                inputLayoutRepassword.setErrorEnabled(false);
            }
        }

        return true;
    }

    private boolean validatePassword() {
        String passsword=inputPassword.getText().toString().trim();
        if (passsword.isEmpty()) {
            inputLayoutPassword.setError("מלא סיסמה");
            requestFocus(inputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateSchooName() {
        String name = inputSchoolName.getText().toString().trim();
        schoolName=name;
        if (name.isEmpty()) {
            inputLayoutSchoolName.setError("מלא שם בית ספר");
            requestFocus(inputSchoolName);
            return false;
        } else {
            inputLayoutSchoolName.setErrorEnabled(false);
        }

        return true;
    }
    private boolean validateLocation() {
        String location = inputSchoolLocation.getText().toString().trim();
        schoolLocation=location;
        if (location.isEmpty()) {
            inputLayoutschoolLocation.setError("מלא כתובת בית ספר");
            requestFocus(inputSchoolLocation);
            return false;
        } else {
            inputLayoutschoolLocation.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePhone() {
        String phone = inputPhone.getText().toString().trim();
        schoolPhone=phone;
        if (phone.isEmpty() || !isValidMobile(schoolPhone)) {
            inputLayoutPhone.setError("מלא מספר טלפון");
            requestFocus(inputPhone);
            return false;
        } else {
            inputLayoutPhone.setErrorEnabled(false);
        }

        return true;
    }

    private boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
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
                case R.id.editTextMail:
                    validateEmail();
                    break;
                case R.id.editTextPassword:
                    validatePassword();
                    break;
                case R.id.editTextRePassword:
                    validateRePAssword();
                    break;
            }
        }
    }
}
