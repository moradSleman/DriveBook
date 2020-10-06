package schoolPackage;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.user.mainPages.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import cn.pedant.SweetAlert.SweetAlertDialog;
import databaseClasses.Constants;
import databaseClasses.drivingSchool;

public class schoolSettings extends Fragment {
    private View rootView;
    private CardView privateSetting, privacySetting, notificationSetting;
    private TextView closePrivate, closePrivacy, closeNotification;

    //private data
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReferenceFromUrl(Constants.pupilsStoragePath);
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText inputEmail, inputName, inputPhone, inputLocation;
    private TextInputLayout inputLayoutEmail, inputLayoutName, inputLayoutPhone, inputLayoutLocation;
    private Button btnUpdate;
    private String schoolUId;
    private SweetAlertDialog pDialog;

    //privace Data
    private EditText inputOldPass,inputNewPass,inputNewRePass;
    private TextInputLayout inputLayoutOldPass,inputLayoutNewPass,inputLayoutNewRePass;
    private Button savePass,cancelPrivacy;

    //notifications
    private SwitchMaterial switchNotification;
    private Button saveNotySetting,cancelNoty;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.school_setting, container, false);
        pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        schoolUId=schoolPagesNavigator.schoolUId;
        //navigation
        privateSetting = rootView.findViewById(R.id.privateSetting);
        privacySetting = rootView.findViewById(R.id.privaceSetting);
        notificationSetting = rootView.findViewById(R.id.notificationSetting);
        closePrivate = rootView.findViewById(R.id.closePrivate);
        closePrivacy = rootView.findViewById(R.id.closePrivacy);
        closeNotification = rootView.findViewById(R.id.closeNotification);
        cancelNoty=rootView.findViewById(R.id.cancelPrivacy);
        cancelPrivacy=rootView.findViewById(R.id.cancelPrivacy);
        ////////
        setNavigations();


        //private Settings
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        inputLayoutPhone = (TextInputLayout) rootView.findViewById(R.id.textInputPhone);
        inputLayoutName = (TextInputLayout) rootView.findViewById(R.id.textInputName);
        inputLayoutEmail = (TextInputLayout) rootView.findViewById(R.id.textInputEmail);
        inputLayoutLocation = (TextInputLayout) rootView.findViewById(R.id.textInputLocation);

        inputPhone = (EditText) rootView.findViewById(R.id.editTextPhone);
        inputEmail = (EditText) rootView.findViewById(R.id.editTextEmail);
        inputName = (EditText) rootView.findViewById(R.id.editTextName);
        inputLocation = (EditText) rootView.findViewById(R.id.editTextLocation);
        btnUpdate = (Button) rootView.findViewById(R.id.update1);

        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputPhone.addTextChangedListener(new MyTextWatcher(inputPhone));
        inputLocation.addTextChangedListener(new MyTextWatcher(inputLocation));
        setData();

        //privacy Setting
        inputLayoutOldPass=rootView.findViewById(R.id.textInputOldPass);
        inputLayoutNewPass=rootView.findViewById(R.id.textInputNewPass);
        inputLayoutNewRePass=rootView.findViewById(R.id.textInputNewRePass);
        inputOldPass=rootView.findViewById(R.id.editTextOldPass);
        inputNewRePass=rootView.findViewById(R.id.editTextNewRePass);
        inputNewPass=rootView.findViewById(R.id.editTextNewPass);
        savePass=rootView.findViewById(R.id.savePass);
        cancelPrivacy=rootView.findViewById(R.id.cancelPrivacy);
        inputOldPass.addTextChangedListener(new MyTextPrivacyWatcher(inputOldPass));
        inputNewRePass.addTextChangedListener(new MyTextPrivacyWatcher(inputNewRePass));
        inputNewPass.addTextChangedListener(new MyTextPrivacyWatcher(inputNewPass));
        savePriavacyClicked();

        //notifications
        switchNotification=rootView.findViewById(R.id.notification);
        saveNotySetting=rootView.findViewById(R.id.saveNotySetting);
        setSaveListener();

        return rootView;
    }

    //notifications
    private void setSaveListener() {
        saveNotySetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog.show();
                db.collection(Constants.drivingSchool).document(schoolUId).update(Constants.schoolNotification
                ,switchNotification.isChecked()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pDialog.dismiss();
                        new SweetAlertDialog(getContext(),SweetAlertDialog.SUCCESS_TYPE)
                                .setContentText("נתונים התעדכנו בהצלחה")
                                .show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pDialog.dismiss();
                        new SweetAlertDialog(getContext(),SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("קרתה שגיאה")
                                .setContentText(e.getMessage().toString())
                                .show();
                    }
                });
            }
        });
    }

    //privacy setting
    public void savePriavacyClicked(){
        savePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(submitPrivacy()){
                    String oldPassWord=inputOldPass.getText().toString();
                    final String newPassword=inputNewPass.getText().toString();
                    pDialog.show();
                    final FirebaseAuth fAuth=FirebaseAuth.getInstance();
                    fAuth.getCurrentUser().reauthenticate(EmailAuthProvider.getCredential(fAuth.getCurrentUser().getEmail(),oldPassWord)).addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    fAuth.getCurrentUser().updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            pDialog.dismiss();
                                            new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                                                    .setTitleText("סיסמה התעדכנה בהצלחה")
                                                    .show();
                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    pDialog.dismiss();
                                                    new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                                                            .setTitleText(e.getMessage().toString())
                                                            .show();
                                                }
                                            });
                                }
                            }
                    ).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pDialog.dismiss();
                            new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("שגיאת משתמש")
                                    .setContentText(e.getMessage().toString())
                                    .setConfirmText("ok")
                                    .show();
                        }
                    });
                }
            }
        });
    }

    private class MyTextPrivacyWatcher implements TextWatcher {

        private View view;

        private MyTextPrivacyWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.editTextNewPass:
                    validateNewPass();
                    break;
                case R.id.editTextNewRePass:
                    validateNewRePass();
                    break;
                case R.id.editTextOldPass:
                    validateOldPass();
                    break;
            }
        }
    }

    private boolean validateOldPass() {
        if(inputOldPass.getText().toString().trim().isEmpty()) {
            inputLayoutOldPass.setError("הקלד סיסמה ישנה");
            requestFocus(inputLayoutOldPass);
            return false;
        }else
        {
            inputLayoutOldPass.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateNewRePass() {
        if(inputNewRePass.getText().toString().trim().isEmpty()) {
            inputLayoutNewRePass.setError("הקלד מחדש סיסמה חדשה");
            requestFocus(inputNewRePass);
            return false;
        }else
        if(!inputNewRePass.getText().toString().equals(inputNewPass.getText().toString())){
            inputNewRePass.setError("סיסמה אינה תואמת");
            requestFocus(inputLayoutNewRePass);
            return false;
        }else
        {
            inputLayoutNewRePass.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateNewPass() {
        if(inputNewPass.getText().toString().trim().isEmpty()) {
            inputLayoutNewPass.setError("הקלד סיסמה חדשה");
            requestFocus(inputNewPass);
            return false;
        }else
        {
            inputLayoutNewPass.setErrorEnabled(false);
        }

        return true;
    }
    private boolean submitPrivacy(){
        if (!validateOldPass()) {
            pDialog.dismiss();
            return false;
        }
        if (!validateNewPass()) {
            pDialog.dismiss();
            return false;
        }
        if (!validateNewRePass()) {
            pDialog.dismiss();
            return false;
        }
        return true;
    }
    //private Settings
    private void setData() {
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog.show();
                if (submitForm()) {
                    pDialog.show();
                    resetOnClick();
                } else {
                    pDialog.dismiss();
                }
            }
        });
        String schoolPhone = schoolPagesNavigator.schoolPhone == null ? "" : schoolPagesNavigator.schoolPhone;
        inputPhone.setText(schoolPhone);
        String schoolAddress = schoolPagesNavigator.schoolAddress == null ? "" : schoolPagesNavigator.schoolAddress;
        inputLocation.setText(schoolAddress);
        String schoolName = schoolPagesNavigator.schoolName;
        inputName.setText(schoolName);
        String schoolMail = schoolPagesNavigator.schoolMail == null ? "" : schoolPagesNavigator.schoolMail;
        inputEmail.setText(schoolMail);
    }

    private void resetOnClick() {
        final drivingSchool school = new drivingSchool();
        school.setSchoolEmail(inputEmail.getText().toString());
        school.setSchoolName(inputName.getText().toString());
        school.setSchoolPhone(inputPhone.getText().toString());
        school.setSchoolLocation(inputLocation.getText().toString());

        db.collection(Constants.drivingSchool).document(schoolUId).update(
                Constants.schoolPhone, school.getSchoolPhone(), Constants.schoolMail, school.getSchoolEmail()
                , Constants.schoolName, school.getSchoolName(), Constants.schoolLoaction, school.getSchoolLocation()).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pDialog.dismiss();
                       new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                       .setTitleText("Good Job!")
                       .setContentText("נתונים התעדכנו בהצלחה")
                       .show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("קרתה שגיאה")
                                .setContentText(e.getMessage().toString())
                                .show();
                    }
                });
    }

    private boolean submitForm() {
        if (!validateName()) {
            pDialog.dismiss();
            return false;
        }
        if (!validateEmail()) {
            pDialog.dismiss();
            return false;
        }
        if (!validatePhone()) {
            pDialog.dismiss();
            return false;
        }
        if(!validateLocation()){
            pDialog.dismiss();
            return false;
        }

        return true;
    }

    private boolean validateLocation() {
        String location = inputLocation.getText().toString().trim();

        if (location.isEmpty()) {
            inputLayoutLocation.setError("מלא מיקום");
            requestFocus(inputLocation);
            return false;
        } else {
            inputLayoutLocation.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty()) {
            inputLayoutEmail.setError("מלא אימייל");
            requestFocus(inputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePhone() {
        String phone = inputPhone.getText().toString().trim();

        if (phone.isEmpty()) {
            inputLayoutPhone.setError("מלא טלפון");
            requestFocus(inputPhone);
            return false;
        } else {
            inputLayoutPhone.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError("מלא שם");
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
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
                case R.id.editTextName:
                    validateName();
                    break;
                case R.id.editTextEmail:
                    validateEmail();
                    break;
                case R.id.editTextPhone:
                    validatePhone();
                    break;
                case R.id.editTextLocation:
                    validateLocation();
                    break;

            }
        }
    }

    //navigation
    private void setNavigations() {
        rootView.findViewById(R.id.backkk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new schoolHomePage(), Constants.tagSchoolHomePage).commit();
            }
        });
        privateSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.school_settings).setVisibility(View.GONE);
                rootView.findViewById(R.id.school_settings_1).setVisibility(View.VISIBLE);
            }
        });
        privacySetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.school_settings).setVisibility(View.GONE);
                rootView.findViewById(R.id.school_settings_2).setVisibility(View.VISIBLE);
            }
        });
        notificationSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.school_settings).setVisibility(View.GONE);
                rootView.findViewById(R.id.school_settings_3).setVisibility(View.VISIBLE);
            }
        });
        closePrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.school_settings).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.school_settings_1).setVisibility(View.GONE);
            }
        });
        closePrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.school_settings).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.school_settings_2).setVisibility(View.GONE);
            }
        });
        closeNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.school_settings).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.school_settings_3).setVisibility(View.GONE);
            }
        });
        cancelNoty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.school_settings).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.school_settings_3).setVisibility(View.GONE);
            }
        });
        cancelPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.school_settings).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.school_settings_2).setVisibility(View.GONE);
            }
        });
    }


}
