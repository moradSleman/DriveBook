package schoolPackage;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.user.mainPages.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import cn.pedant.SweetAlert.SweetAlertDialog;
import databaseClasses.Constants;
import databaseClasses.drivingSchool;

public class schoolUpdateInfo extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText inputPhone,inputName,inputLocation;
    private TextInputLayout inputLayoutName,inputLayoutLocation,inputLayoutPhone;
    private CircularProgressButton btnUpdate;
    private TextView answerUpdate;
    private View rootView;
    private String schoolId;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.school_update_info,container,false);

        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

        inputLayoutName = (TextInputLayout) rootView.findViewById(R.id.textInputName);
        inputLayoutLocation = (TextInputLayout) rootView.findViewById(R.id.textInputLocation);
        inputLayoutPhone = (TextInputLayout) rootView.findViewById(R.id.textInputPhone);

        inputName = (EditText) rootView.findViewById(R.id.editTextName);
        inputLocation = (EditText) rootView.findViewById(R.id.editTextLocation);
        inputPhone = (EditText) rootView.findViewById(R.id.editTextPhone);

        Intent intent = getActivity().getIntent();
        schoolId = schoolPagesNavigator.schoolUId;
        String schoolAddress =schoolPagesNavigator.schoolAddress;
        inputLocation.setText(schoolAddress);
        String schoolName =schoolPagesNavigator.schoolName;
        inputName.setText(schoolName);
        String schoolPhone = schoolPagesNavigator.schoolPhone;
        inputPhone.setText(schoolPhone);

        btnUpdate = (CircularProgressButton) rootView.findViewById(R.id.signUpBtn);

        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputLocation.addTextChangedListener(new MyTextWatcher(inputLocation));
        inputPhone.addTextChangedListener(new MyTextWatcher(inputPhone));
        Calendar newCalendar = Calendar.getInstance();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answerUpdate.setText("");
                btnUpdate.startAnimation();
                if (submitForm()) {
                    resetOnClick();
                } else {
                    btnUpdate.revertAnimation();
                }
            }
        });


        return rootView;
    }


    public  void resetOnClick() {
        drivingSchool schoolToAdd=new drivingSchool();

        schoolToAdd.setSchoolLocation(inputLocation.getText().toString());
        schoolToAdd.setSchoolName(inputName.getText().toString());
        schoolToAdd.setSchoolPhone(inputPhone.getText().toString());

        db.collection(Constants.drivingSchool).document(schoolId).update(Constants.schoolLoaction,schoolToAdd.getSchoolLocation(),
                Constants.schoolName,schoolToAdd.getSchoolName(),Constants.schoolPhone,schoolToAdd.getSchoolPhone()).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        btnUpdate.revertAnimation();
                        new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Good job!")
                                .setContentText("נתונים שלך עודכנו במערכת")
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

    /**
     * Validating form
     */
    private boolean submitForm() {
        if (!validateName()) {
            btnUpdate.revertAnimation();
            return false;
        }

        if (!validateLocation()) {
            btnUpdate.revertAnimation();
            return false;
        }

        if(!validatePhone()){
            btnUpdate.revertAnimation();
            return false;
        }



        return true;
    }

    private boolean validatePhone(){
        if (inputPhone.getText().toString().trim().isEmpty()) {
            inputLayoutPhone.setError("מלא טלפון");
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
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





    private boolean validateLocation() {
        if (inputLocation.getText().toString().trim().isEmpty()) {
            inputLayoutLocation.setError("מלא כתובת");
            requestFocus(inputLocation);
            return false;
        } else {
            inputLayoutLocation.setErrorEnabled(false);
        }

        return true;
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
                case R.id.editTextLocation:
                    validateLocation();
                    break;
                case R.id.editTextPhone:
                    validatePhone();
                    break;
            }
        }
    }
}
