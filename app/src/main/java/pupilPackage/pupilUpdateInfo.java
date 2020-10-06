package pupilPackage;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.user.mainPages.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.meg7.widget.CircleImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import cn.pedant.SweetAlert.SweetAlertDialog;
import databaseClasses.Constants;
import databaseClasses.Pupil;
import databaseClasses.rotateImage;

import static android.app.Activity.RESULT_OK;

public class pupilUpdateInfo extends Fragment {
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReferenceFromUrl(Constants.pupilsStoragePath);
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText inputEmail,inputName,inputLocation, inputDate,inputTheory;
    private TextInputLayout inputLayoutEmail,inputLayoutName,inputLayoutLocation
            ,inputLayoutDate,inputLayoutTheory;
    private Spinner lessonAlertSpinner;
    private CircularProgressButton btnUpdate;
    private CircleImageView faceImage;
    private DatePickerDialog mDatePickerDialog, mDatePickerDialogTheory;
    private View rootView;
    private Date birthDate;
    private String schoolUId;
    private String teacherUId;
    private String pupilId;
    private Date theoryEndPick;
    private String teacherId;
    private String schoolId;
    private String pupilPhone;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.pupil_update_info,container,false);
        lessonAlertSpinner=rootView.findViewById(R.id.spinnerLessonAler);
        ArrayList<String> alertSpinnerItems=new ArrayList<>();
        alertSpinnerItems.add("חצי שעה");
        alertSpinnerItems.add("1 שעות");
        alertSpinnerItems.add("2 שעות");
        alertSpinnerItems.add("4 שעות");
        alertSpinnerItems.add("יום");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, alertSpinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lessonAlertSpinner.setAdapter(adapter);

        schoolUId = pupilPagesNavigator.schoolId;
        teacherUId= pupilPagesNavigator.teacherId;
        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

        inputLayoutName = (TextInputLayout) rootView.findViewById(R.id.textInputName);
        inputLayoutLocation = (TextInputLayout) rootView.findViewById(R.id.textInputLocation);
        inputLayoutEmail = (TextInputLayout) rootView.findViewById(R.id.textInputMail);
        inputLayoutDate = (TextInputLayout) rootView.findViewById(R.id.textInputbirthDate);
        inputLayoutTheory = (TextInputLayout) rootView.findViewById(R.id.textInputTheory);

        inputEmail = (EditText) rootView.findViewById(R.id.editTextMail);
        inputName = (EditText) rootView.findViewById(R.id.editTextName);
        inputLocation = (EditText) rootView.findViewById(R.id.editTextLocation);
        inputDate = (EditText) rootView.findViewById(R.id.editTextbirthDate);
        inputTheory = (EditText) rootView.findViewById(R.id.editTextTheory);

        lessonAlertSpinner=(Spinner) rootView.findViewById(R.id.spinnerLessonAler);
        faceImage=(CircleImageView) rootView.findViewById(R.id.faceImage);

        Intent intent = getActivity().getIntent();
        pupilId=pupilPagesNavigator.pupilId;
        teacherId = pupilPagesNavigator.teacherId;
        schoolId = pupilPagesNavigator.schoolId;
        pupilPhone = pupilPagesNavigator.pupilPhone;
        String pupilAddress = pupilPagesNavigator.Address;
        inputLocation.setText(pupilAddress);
        String pupilName = pupilPagesNavigator.fullName;
        inputName.setText(pupilName);
        String pupilEmail = pupilPagesNavigator.Email;
        inputEmail.setText(pupilEmail);
        String theoryDate=pupilPagesNavigator.theory;
        String[] birthSplit=theoryDate.split(" ");
        inputTheory.setText(birthSplit[2]+"-"+getDateNum(birthSplit[1])+"-"+birthSplit[5]);
        String dateBirth=pupilPagesNavigator.birthDate;
        String[] theorySplit=dateBirth.split(" ");
        inputDate.setText(theorySplit[2]+"-"+getDateNum(theorySplit[1])+"-"+theorySplit[5]);
        int alertLesson=pupilPagesNavigator.alertLesson;
        int position=0;
        switch (alertLesson){
            case (30) :
                position=0;
                break;
            case (60) :
                position=1;
                break;
            case (120) :
                position=2;
                break;
            case (180) :
                position=3;
                break;
            case (240) :
                position=4;
                break;
            case (1440) :
                position=5;
                break;
        }
        lessonAlertSpinner.setSelection(position);
        btnUpdate = (CircularProgressButton) rootView.findViewById(R.id.signUpBtn);

        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputLocation.addTextChangedListener(new MyTextWatcher(inputLocation));
        inputDate.addTextChangedListener(new MyTextWatcher(inputDate));
        inputTheory.addTextChangedListener(new MyTextWatcher(inputTheory));
        Calendar newCalendar = Calendar.getInstance();
        mDatePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy");
                birthDate = newDate.getTime();
                String fdate = sd.format(birthDate);

                inputDate.setText(fdate);

            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        mDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        inputDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mDatePickerDialog.show();
                return false;
            }
        });

        Calendar newCalendarTheory = Calendar.getInstance();
        mDatePickerDialogTheory = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy");
                theoryEndPick = newDate.getTime();
                String fdate = sd.format(theoryEndPick);

                inputTheory.setText(fdate);

            }
        }, newCalendarTheory.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        mDatePickerDialogTheory.getDatePicker().setMinDate(System.currentTimeMillis());
        inputTheory.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mDatePickerDialogTheory.show();
                return false;
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnUpdate.startAnimation();
                if (submitForm()) {
                    resetOnClick();
                } else {
                    btnUpdate.revertAnimation();
                }
            }
        });
        setFaceAndCertifacateImage();

        faceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickimageFromGaller();
            }
        });

        return rootView;
    }
    public int getDateNum(String month){
        switch(month){
            case "Jan":
                return 1;
            case "Feb":
                return 2;
            case "Mar":
                return 3;
            case "Apr":
                return 4;
            case "May":
                return 5;
            case "Jun":
                return 6;
            case "Jul":
                return 7;
            case "Aug":
                return 8;
            case "Sep":
                return 9;
            case "Oct":
                return 10;
            case "Nov":
                return 11;
            case "Dec":
                return 12;

        }
        return 0;
    }
    public void pickimageFromGaller(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (reqCode == 1 && resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                  final Bitmap orientedBitMap = rotateImage.scaleImage(this.getContext(), imageUri);
                faceImage.setImageBitmap(orientedBitMap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    public void setFaceAndCertifacateImage() {
        FirebaseStorage.getInstance().getReferenceFromUrl(Constants.pupilsStoragePath + pupilId + "/"+Constants.faceImage+"/")
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                StorageReference mImageRef =
                        FirebaseStorage.getInstance().getReferenceFromUrl(Constants.pupilsStoragePath + pupilId + "/"+Constants.faceImage+"/");
                final long ONE_MEGABYTE = 1024 * 1024;
                mImageRef.getBytes(ONE_MEGABYTE)
                        .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                faceImage.setImageBitmap(bm);
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


    public  void resetOnClick() {
        Pupil pupilToAdd=new Pupil();
        if(birthDate!=null) {
            pupilToAdd.setPupilBirthDate(birthDate);
        }
        if(!inputEmail.getText().toString().equals(""))
        {
            pupilToAdd.setPupilMail(inputEmail.getText().toString());
        }
        pupilToAdd.setPupilName(inputName.getText().toString());

        pupilToAdd.setPupilPhone(pupilPhone);

        if(theoryEndPick==null){
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            Date date;
            try {
                date = df.parse(inputTheory.getText().toString());
                pupilToAdd.setPupilTheoryEnd(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else {
            pupilToAdd.setPupilTheoryEnd(theoryEndPick);
        }
        int minutesAlert=30;
        switch (lessonAlertSpinner.getSelectedItem().toString()) {
            case ("חצי שעה") :
                minutesAlert=30;
                break;
            case ("1 שעות") :
                minutesAlert=60;
                break;
            case ("2 שעה") :
                minutesAlert=120;
                break;
            case ("3 שעות") :
                minutesAlert=180;
                break;
            case ("4 שעה") :
                minutesAlert=240;
                break;
            case ("יום") :
                minutesAlert=1440;
                break;
        }
        pupilToAdd.setTimeAlertBeforeLesson(minutesAlert);
        db.collection(Constants.drivingSchool).document(schoolUId).collection(Constants.teachers).document(teacherUId).
                collection(Constants.pupils).document(pupilId).update(Constants.pupilBirthdate,pupilToAdd.getPupilBirthDate(),
                Constants.pupilLocation,pupilToAdd.getPupilLocation(),Constants.pupilMail,pupilToAdd.getPupilMail()
        ,Constants.pupilName,pupilToAdd.getPupilName(),Constants.pupilTheoryEnd,pupilToAdd.getPupilTheoryEnd(),Constants.pupilAlertLesson,
                pupilToAdd.getTimeAlertBeforeLesson()).
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
        if (faceImage.getDrawable() != null) {
            faceImage.buildDrawingCache();
            Bitmap bitmap = faceImage.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] facedata = baos.toByteArray();
            StorageReference storageRef1 = storageRef.child(pupilId + "/"+Constants.faceImage+"/");
            UploadTask uploadTask = storageRef1.putBytes(facedata);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getUploadSessionUri();

                }
            });
        }


    }

    /**
     * Validating form
     */
    private boolean submitForm() {
        if (!validateName()) {
            btnUpdate.revertAnimation();
            return false;
        }
        if(!validateDate()){
            btnUpdate.revertAnimation();
            return false;
        }
        if (!validateLocation()) {
            btnUpdate.revertAnimation();
            return false;
        }

        if (!validateEmail()) {
            btnUpdate.revertAnimation();
            return false;
        }




        return true;
    }

    private boolean validateDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inputDate.getText().toString().trim());
            inputLayoutEmail.setErrorEnabled(false);
        } catch (ParseException pe) {
            inputLayoutDate.setError("מלא תאריך לפי הפורמאט 'dd-mm-yyyy'");
            requestFocus(inputDate);
            return false;
        }

        return true;
    }
    private boolean validateTheoryDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inputTheory.getText().toString().trim());
            inputLayoutTheory.setErrorEnabled(false);
        } catch (ParseException pe) {
            inputLayoutTheory.setError("מלא תאריך לפי הפורמאט 'dd-mm-yyyy'");
            requestFocus(inputTheory);
            return false;
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
                case R.id.editTextName:
                    validateName();
                    break;
                case R.id.editTextLocation:
                    validateLocation();
                    break;
                case R.id.editTextTheory:
                    validateTheoryDate();
                    break;
                case R.id.editTextbirthDate:
                    validateDate();
                    break;
            }
        }
    }
}
