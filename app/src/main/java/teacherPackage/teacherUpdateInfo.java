package teacherPackage;

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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
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
import java.util.Calendar;
import java.util.Date;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import cn.pedant.SweetAlert.SweetAlertDialog;
import databaseClasses.Constants;
import databaseClasses.Teacher;
import databaseClasses.rotateImage;

import static android.app.Activity.RESULT_OK;

public class teacherUpdateInfo extends Fragment {
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReferenceFromUrl(Constants.teacherStoragePath);
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText inputEmail,inputName,inputLocation, inputDate,inputLessonInterval,inputLessonCost;
    private TextInputLayout inputLayoutEmail,inputLayoutName,inputLayoutLocation
            ,inputLayoutDate,inputLayoutLessonInterval,inputLayoutLessonCost;
    private CircularProgressButton btnUpdate;
    private CircleImageView faceImage;
    private DatePickerDialog mDatePickerDialog;
    private TextView answerUpdate;
    private View rootView;
    private Date birthDate;
    private String schoolUId;
    private String teacherUId;
    private String teacherId;
    private String schoolId;
    private String teacherPhone;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.teacher_update_info,container,false);

            schoolUId = teacherPagesNavigator.schoolId;
            teacherUId= teacherPagesNavigator.teacherId;
        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

        inputLayoutName = (TextInputLayout) rootView.findViewById(R.id.textInputName);
        inputLayoutLocation = (TextInputLayout) rootView.findViewById(R.id.textInputLocation);
        inputLayoutEmail = (TextInputLayout) rootView.findViewById(R.id.textInputMail);
        inputLayoutDate = (TextInputLayout) rootView.findViewById(R.id.textInputbirthDate);
        inputLayoutLessonInterval = (TextInputLayout) rootView.findViewById(R.id.textInputLEssonInterval);
        inputLayoutLessonCost = (TextInputLayout) rootView.findViewById(R.id.textInputLessonCost);

        inputEmail = (EditText) rootView.findViewById(R.id.editTextMail);
        inputName = (EditText) rootView.findViewById(R.id.editTextName);
        inputLocation = (EditText) rootView.findViewById(R.id.editTextLocation);
        inputDate = (EditText) rootView.findViewById(R.id.editTextbirthDate);
        inputLessonInterval = (EditText) rootView.findViewById(R.id.editTextLessonInterval);
        inputLessonCost = (EditText)rootView.findViewById(R.id.editTextLessonCost);

        faceImage=(CircleImageView) rootView.findViewById(R.id.faceImage);

        Intent intent = getActivity().getIntent();
        teacherId = teacherPagesNavigator.teacherId;
        schoolId = teacherPagesNavigator.schoolId;
        teacherPhone = teacherPagesNavigator.Phone;
        String teacherAddress = teacherPagesNavigator.Address;
        inputLocation.setText(teacherAddress);
        String teacherName = teacherPagesNavigator.fullName;
        inputName.setText(teacherName);
        String teacherMail = teacherPagesNavigator.Email;
        inputEmail.setText(teacherMail);
        String teacherBirthDate = teacherPagesNavigator.birthDate;
        String[] datetEmp = teacherBirthDate.split(" ");
        inputDate.setText(datetEmp[2]+"-"+getDateNum(datetEmp[1])+"-"+datetEmp[5]);

        btnUpdate = (CircularProgressButton) rootView.findViewById(R.id.signUpBtn);

        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputLocation.addTextChangedListener(new MyTextWatcher(inputLocation));
        inputDate.addTextChangedListener(new MyTextWatcher(inputDate));
        inputLessonCost.addTextChangedListener(new MyTextWatcher(inputLessonCost));
        inputLessonInterval.addTextChangedListener(new MyTextWatcher(inputLayoutLessonInterval));

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
        FirebaseStorage.getInstance().getReferenceFromUrl(Constants.teacherStoragePath + teacherId + "/"+Constants.faceImage+"/")
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                StorageReference mImageRef =
                        FirebaseStorage.getInstance().getReferenceFromUrl(Constants.teacherStoragePath + teacherId + "/"+Constants.faceImage+"/");
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
        Teacher teacherToAdd=new Teacher();
        if(birthDate==null) {
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            Date date;
            try {
                date = df.parse(inputDate.getText().toString());
                teacherToAdd.setTeacherBirthdate(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else {
            teacherToAdd.setTeacherBirthdate(birthDate);
        }
        teacherToAdd.setTeacherMail(inputEmail.getText().toString());
        teacherToAdd.setTeacherLocation(inputLocation.getText().toString());
        teacherToAdd.setTeacherName(inputName.getText().toString());
        teacherToAdd.setTeacherPhone(teacherPhone);
        teacherToAdd.setLessonCost(Integer.parseInt(inputLessonCost.getText().toString()));
        teacherToAdd.setTeacherLessonIntervalTime(Integer.parseInt(inputLessonInterval.getText().toString()));
        db.collection(Constants.drivingSchool).document(schoolUId).collection(Constants.teachers).document(teacherUId).
               update(Constants.teacherBirthdate,teacherToAdd.getTeacherBirthdate(),
                Constants.teacherLocation,teacherToAdd.getTeacherLocation(),Constants.teacherLessonCost,teacherToAdd.getLessonCost(),
                       Constants.teacherMail,teacherToAdd.getTeacherMail(),Constants.teacherLessonInterval,teacherToAdd.getTeacherLessonIntervalTime()
                       ,Constants.teacherName,teacherToAdd.getTeacherName()).
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
            StorageReference storageRef1 = storageRef.child(teacherId + "/"+Constants.faceImage+"/");
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
        if (!validateLessonCost()) {
            btnUpdate.revertAnimation();
            return false;
        }
        if (!validateLessonInterval()) {
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
    private boolean validateLessonInterval(){
        String lessonInterval = inputLessonInterval.getText().toString().trim();

        if (lessonInterval.isEmpty()) {
            inputLayoutLessonInterval.setError("מלא אורך שיעור");
            requestFocus(inputLessonInterval);
            return false;
        } else {
            inputLayoutLessonInterval.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateLessonCost(){
        String lessonCost = inputLessonCost.getText().toString().trim();

        if (lessonCost.isEmpty()) {
            inputLayoutLessonCost.setError("מלא עלות שיעור");
            requestFocus(inputLessonCost);
            return false;
        } else {
            inputLayoutLessonCost.setErrorEnabled(false);
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
                case R.id.editTextbirthDate:
                    validateDate();
                    break;
                case R.id.editTextLessonCost:
                    validateLessonCost();
                    break;
                case R.id.editTextLessonInterval:
                    validateLessonInterval();
                    break;
            }
        }
    }
}
