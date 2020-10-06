package teacherPackage;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chaos.view.PinView;
import com.example.user.mainPages.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import databaseClasses.Constants;
import databaseClasses.Pupil;
import schoolPackage.schoolPagesNavigator;

public class teacherRegisterPupil extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseFunctions mFunctions;
    private EditText inputEmail, inputPassword,inputRePassword,inputName,inputPhone;
    private TextInputLayout inputLayoutEmail, inputLayoutPassword,inputLayoutRePassword,inputLayoutName,inputLayoutPhone;

    private Button btnSignUpMail,btnsendCodePhone,btnCancel,btnSignUpPhone,btnResendCode,btnFinish;
    private TextView resendMail, pupilNameTxtView;
    private ImageButton back,exit;
    private SwitchMaterial switchMailPhone;
    private PinView pin;
    private String mVerificationId;

    private String schoolId;
    private String pupilName;
    private String pupilPhone;
    private String pupilMail;
    private String pupilPass;
    private String pinTxt;

    private String schoolUId;
    private String teacherUId;

    private View rootView;
    private SweetAlertDialog pDialog;
    private FirebaseAuth mAuth2;
    private Dialog createdMailDialog;
    private Dialog verifyPhoneCode;
    private Dialog finish;

    private ImageButton exitAddNoty;
    private Button sendNoty;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private String verificationCode;
    private ArrayList<String> schoolUIds;
    private TextView pupilNameTxtViewFinish;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.teacher_register_pupil,container,false);

        mFunctions = FirebaseFunctions.getInstance();
        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                .setDatabaseUrl("https://drivingbook-c7cc1.firebaseio.com")
                .setApiKey("AIzaSyCy27I0WjwRmrywbZOKVM9FcDQfPOGnTRA")
                .setApplicationId("drivingbook-c7cc1").build();

        try { FirebaseApp myApp = FirebaseApp.initializeApp(getContext(), firebaseOptions, "AnyAppName");
            mAuth2 = FirebaseAuth.getInstance(myApp);
        } catch (IllegalStateException e){
            mAuth2 = FirebaseAuth.getInstance(FirebaseApp.getInstance("AnyAppName"));
        }

        createdMailDialog=new Dialog(getContext());
        createdMailDialog.setContentView(R.layout.teacher_register_pupil_2);
        pupilNameTxtView =createdMailDialog.findViewById(R.id.teacherMail);
        exit = createdMailDialog.findViewById(R.id.exit);
        resendMail=createdMailDialog.findViewById(R.id.resendMail);

        verifyPhoneCode=new Dialog(getContext());
        verifyPhoneCode.setContentView(R.layout.teacher_register_pupil_3);
        pin=verifyPhoneCode.findViewById(R.id.pinView);
        btnSignUpPhone = verifyPhoneCode.findViewById(R.id.continuePhone);
        btnResendCode = verifyPhoneCode.findViewById(R.id.resendCode);

        finish=new Dialog(getContext());
        finish.setContentView(R.layout.teacher_register_pupil_4);
        pupilNameTxtViewFinish=finish.findViewById(R.id.teacherName);
        btnFinish=finish.findViewById(R.id.finish);


        pDialog= new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#00a5ff"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);

        schoolUId = schoolPagesNavigator.schoolUId;
        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

        inputLayoutName = (TextInputLayout) rootView.findViewById(R.id.textInputNameTeacher);
        inputLayoutPhone = (TextInputLayout) rootView.findViewById(R.id.textInputLayoutPhone);
        inputLayoutEmail = (TextInputLayout) rootView.findViewById(R.id.textInputMail);
        inputLayoutPassword = (TextInputLayout) rootView.findViewById(R.id.textInputLayoutPassword);
        inputLayoutRePassword = (TextInputLayout) rootView.findViewById(R.id.TextInputLayoutRePassword);

        inputEmail = (EditText) rootView.findViewById(R.id.editTextMail);
        inputPassword = (EditText) rootView.findViewById(R.id.editTextPassword);
        inputName = (EditText) rootView.findViewById(R.id.editTextNameTeacher);
        inputPhone = (EditText) rootView.findViewById(R.id.editTextPhone);
        inputRePassword = (EditText) rootView.findViewById(R.id.editTextRePassword);

        btnSignUpMail=(Button) rootView.findViewById(R.id.continueMail);
        btnsendCodePhone=(Button)rootView.findViewById(R.id.sendPhoneCode);
        btnCancel=(Button)rootView.findViewById(R.id.cancelMailPhone);

        back=(ImageButton)rootView.findViewById(R.id.leftArrow);
        switchMailPhone=(SwitchMaterial)rootView.findViewById(R.id.isByMail);

        inputEmail.addTextChangedListener(new teacherRegisterPupil.MyTextWatcher(inputEmail));
        inputPassword.addTextChangedListener(new teacherRegisterPupil.MyTextWatcher(inputPassword));
        inputName.addTextChangedListener(new teacherRegisterPupil.MyTextWatcher(inputName));
        inputPhone.addTextChangedListener(new teacherRegisterPupil.MyTextWatcher(inputPhone));
        inputRePassword.addTextChangedListener(new teacherRegisterPupil.MyTextWatcher(inputRePassword));

        setFinishListeners();
        setSchoolDropDown();
        setSwitchListener();
        setSignUpMailListener();
        setSendCodeLestiner();
        setCreatedMailDialogListeners();
        setVerifyPhoneCodeDialogListeners();
        return rootView;
    }

    private void setFinishListeners() {
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish.dismiss();
            }
        });
    }

    private void setSchoolDropDown() {
        db.collection(Constants.UIDS).document(teacherPagesNavigator.teacherId).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    private int counter;

                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult().exists()) {
                            final ArrayList<String> schoolNames=new ArrayList<>();
                            schoolUIds=(ArrayList<String>) task.getResult().get(Constants.schoolsUIDs);
                            counter=schoolUIds.size();
                            final HashMap<String ,String > schooools=new HashMap<>();
                            for(String schoolId1:schoolUIds) {
                                db.collection(Constants.drivingSchool).document(schoolId1).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful() && task.getResult().exists()){
                                            schoolNames.add(task.getResult().get(Constants.schoolName).toString());
                                            schooools.put(task.getResult().get(Constants.schoolName).toString(),schoolUIds.get(schoolUIds.size()-counter).toString());
                                            counter--;
                                            if(counter==0){
                                                ArrayAdapter<String> adapter =
                                                        new ArrayAdapter<String>(
                                                                teacherPagesNavigator.context,
                                                                R.layout.drop_down_layout,
                                                                schoolNames);
                                                final AutoCompleteTextView editTextFilledExposedDropdown =
                                                        rootView.findViewById(R.id.filled_exposed_dropdown);
                                                editTextFilledExposedDropdown.setAdapter(adapter);
                                                editTextFilledExposedDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                       schoolId = schooools.get(editTextFilledExposedDropdown.getAdapter().getItem(position).toString());
                                                    }
                                                });
                                            }
                                        }
                                    }
                                });
                            }

                        }
                    }

                });




    }

    private void setSendCodeLestiner() {
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                String codeT = phoneAuthCredential.getSmsCode();

                //sometime the code is not detected automatically
                //in this case the code will be null
                //so user has to manually enter the code
                if (codeT != null && codeT!="") {
                    pDialog.dismiss();
                    verifyPhoneCode.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    verifyPhoneCode.show();
                    pin.setText(codeT);
                    //verifying the code
                    btnSignUpPhone.performClick();
                }else{
                    pDialog.dismiss();
                }
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                pDialog.dismiss();
                new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("verification fialed")
                        .setContentText(e.toString())
                        .show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                pDialog.dismiss();
                verificationCode = s;
                verifyPhoneCode.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                verifyPhoneCode.show();
            }
        };
        btnsendCodePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validatePhone() && validateName()) {
                    pDialog.show();
                    if (validatePhone()) {
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                "+972" + pupilPhone,                     // Phone number to verify
                                60,                           // Timeout duration
                                TimeUnit.SECONDS,                // Unit of timeout
                                (Activity)getContext(),        // Activity (for callback binding)
                                mCallback);     // OnVerificationStateChangedCallbacks

                    }
                }
            }
        });
    }

    private void setVerifyPhoneCodeDialogListeners() {

        btnResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnsendCodePhone.performClick();
            }
        });
        btnSignUpPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validatePin()) {
                    pDialog.show();
                    if (verificationCode != null) {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, pinTxt);
                        mAuth2.signInWithCredential(credential)
                                .addOnCompleteListener((Activity) getContext(), new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Pupil pupil=new Pupil();
                                            pupil.setPupilName(pupilName);
                                            pupil.setPupilPhone(pupilPhone);
                                            pupil.setSchoolId(schoolId);
                                            db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(
                                                    teacherPagesNavigator.teacherId).collection(Constants.pupils).document(mAuth2.getCurrentUser().getUid().toString()).
                                                    set(pupil).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Map<String, Object> user = new HashMap<>();
                                                        user.put(Constants.typeOfUser, Constants.pupilType);
                                                        user.put(Constants.schoolId,schoolId);
                                                        user.put(Constants.teacherId,mAuth.getCurrentUser().getUid().toString());
                                                        db.collection(Constants.UIDS).document(mAuth2.getCurrentUser().getUid().toString()).set(user).addOnCompleteListener(
                                                                new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Map<String, Object> phone = new HashMap<>();
                                                                            phone.put(Constants.registeredPhoneNumber, pupilPhone);
                                                                            db.collection(Constants.registeredPhones).document(pupilPhone.toString()).set(phone).addOnCompleteListener(
                                                                                    new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()) {
                                                                                                mAuth2.signOut();
                                                                                                pDialog.dismiss();
                                                                                                verifyPhoneCode.dismiss();
                                                                                                pupilNameTxtViewFinish.setText(pupilName);
                                                                                                finish.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                                                                finish.show();
                                                                                            }else{
                                                                                                mAuth2.signOut();
                                                                                                pDialog.dismiss();
                                                                                            }
                                                                                        }
                                                                                    }
                                                                            );

                                                                        } else {
                                                                            mAuth2.signOut();
                                                                            pDialog.dismiss();
                                                                        }
                                                                    }
                                                                }
                                                        );


                                                    }else {
                                                        mAuth2.signOut();
                                                        pDialog.dismiss();
                                                    }
                                                }
                                            });
                                        } else {
                                            mAuth2.signOut();
                                            pDialog.dismiss();
                                            //verification unsuccessful.. display an error message

                                            String message = "Somthing is wrong, we will fix it soon...";

                                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                                message = "Invalid code entered...";
                                            }
                                            new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
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
                        mAuth2.signOut();
                        pDialog.dismiss();
                    }
                }
            }
        });
    }

    private void setCreatedMailDialogListeners() {
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createdMailDialog.dismiss();
            }
        });
        resendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSignUpMail.performClick();
            }
        });
    }

    private void setSignUpMailListener() {
        btnSignUpMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (submitMailForm()) {
                    pDialog.show();
                    mAuth.fetchSignInMethodsForEmail(pupilMail)
                            .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                                    boolean isNewUser = task.getResult().getSignInMethods().isEmpty();

                                    if (isNewUser) {
                                        createUserAndVerifyEmail();
                                    } else {
                                        pDialog.dismiss();
                                        new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                                                .setTitleText("מייל כבר נמצא במערכת")
                                                .setConfirmText("ok")
                                                .show();
                                    }
                                }
                            });
                }
            }
        });
    }

    public void createUserAndVerifyEmail() {
        mAuth2.createUserWithEmailAndPassword(pupilMail, pupilPass).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mAuth2.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Pupil pupil=new Pupil();
                                        pupil.setPupilMail(pupilMail);
                                        pupil.setPupilName(pupilName);
                                        pupil.setSchoolId(schoolId);
                                        db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers)
                                                .document(teacherPagesNavigator.teacherId).collection(Constants.pupils).document(mAuth2.getCurrentUser()
                                        .getUid().toString()).set(pupil).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Map<String, Object> user = new HashMap<>();
                                                    user.put(Constants.typeOfUser, Constants.pupilType);
                                                    user.put(Constants.schoolId,schoolId);
                                                    user.put(Constants.teacherId,mAuth.getCurrentUser().getUid().toString());
                                                    db.collection(Constants.UIDS).document(mAuth2.getCurrentUser().getUid().toString()).set(user).addOnCompleteListener(
                                                            new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    mAuth2.signOut();
                                                                    pDialog.dismiss();
                                                                    createdMailDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                                    createdMailDialog.show();
                                                                    pupilNameTxtView.setText(pupilMail);
                                                                }
                                                            }
                                                    );


                                                }else {
                                                    mAuth2.signOut();
                                                    pDialog.dismiss();
                                                }
                                            }
                                        });


                                    } else
                                    {
                                        mAuth2.signOut();
                                        pDialog.dismiss();
                                        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
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
                            new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
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
    private void setSwitchListener() {
        switchMailPhone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rootView.findViewById(R.id.mailViews).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.textInputLayoutPhone).setVisibility(View.GONE);
                    rootView.findViewById(R.id.continueMail).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.sendPhoneCode).setVisibility(View.GONE);
                }else{
                    rootView.findViewById(R.id.mailViews).setVisibility(View.GONE);
                    rootView.findViewById(R.id.continueMail).setVisibility(View.GONE);
                    rootView.findViewById(R.id.textInputLayoutPhone).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.sendPhoneCode).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public boolean validatePin(){
        pinTxt=pin.getText().toString();
        if(pinTxt.isEmpty() || pinTxt.length()<6){
            return false;
        }
        return true;
    }
    /**
     * Validating form
     */
    private boolean submitMailForm() {

        if (!validateEmail()) {
            return false;
        }

        if (!validatePassword()) {
            return false;
        }

        if(!validateRePassword()){
            return false;
        }

        return true;
    }

    private boolean validateEmail() {
        pupilMail = inputEmail.getText().toString().trim();

        if (pupilMail.isEmpty()) {
            inputLayoutEmail.setError("מלא אימייל");
            requestFocus(inputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateName() {
        pupilName =inputName.getText().toString().trim();
        if (pupilName.isEmpty()) {
            inputLayoutName.setError("מלא שם");
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateRePassword() {
        if (inputRePassword.getText().toString().trim().isEmpty()) {
            inputLayoutRePassword.setError("מלא סיסמה");
            requestFocus(inputRePassword);
            return false;
        } else {
            if (!inputRePassword.getText().toString().trim().equals(inputPassword.getText().toString().trim())) {
                inputLayoutRePassword.setError("סיסמה לא תואמת");
                requestFocus(inputRePassword);
                return false;
            }else{
                inputLayoutRePassword.setErrorEnabled(false);
            }
        }

        return true;
    }

    private boolean validatePassword() {
        pupilPass =inputPassword.getText().toString().trim();
        if (pupilPass.isEmpty()) {
            inputLayoutPassword.setError("מלא סיסמה");
            requestFocus(inputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePhone() {
        pupilPhone =inputPhone.getText().toString().trim();
        if (pupilPhone.isEmpty() && !isValidMobile(pupilPhone)) {
            inputLayoutPhone.setError("מלא טלפון");
            requestFocus(inputPhone);
            return false;
        } else {
            inputLayoutPhone.setErrorEnabled(false);
        }

        return true;
    }


    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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
                case R.id.editTextNameTeacher:
                    validateName();
                    break;
                case R.id.editTextRePassword:
                    validateRePassword();
                    break;
                case R.id.editTextPhone:
                    validatePhone();
                    break;
            }
        }
    }}
