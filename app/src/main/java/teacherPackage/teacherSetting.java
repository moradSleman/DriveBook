package teacherPackage;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.mainPages.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.meg7.widget.CircleImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import cn.pedant.SweetAlert.SweetAlertDialog;
import databaseClasses.Constants;
import databaseClasses.Teacher;
import databaseClasses.rotateImage;
import pupilPackage.pupilPagesNavigator;

import static android.app.Activity.RESULT_OK;

public class teacherSetting extends Fragment {
    private View rootView;
    private CardView privateSetting, privacySetting,notificationSetting;
    private TextView closePrivate,closePrivacy,closeNotification;

    //private Setting Fields
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReferenceFromUrl(Constants.teacherStoragePath);
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText inputEmail,inputName,inputPhone, inputDate,inputLocation;
    private TextInputLayout inputLayoutEmail,inputLayoutName,inputLayoutPhone
            ,inputLayoutDate,inputLAyoutLocation;
    private Button btnUpdate;
    private CircleImageView faceImage;
    private DatePickerDialog mDatePickerDialog, mDatePickerDialogTheory;
    private Date birthDate;
    private String schoolUId;
    private String teacherUId;
    private String teacherId;
    private String schoolId;
    private String teacherPhone;
    private SweetAlertDialog pDialog;

    //privace Data
    private EditText inputOldPass,inputNewPass,inputNewRePass;
    private TextInputLayout inputLayoutOldPass,inputLayoutNewPass,inputLayoutNewRePass;
    private Button savePass,cancelPrivacy;

    //notifications
    private SwitchMaterial switchNotification;
    private Button saveNotySetting;
    private Button canclNoty;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.teacher_settings, container, false);
        pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#00a5ff"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        //navigation
        privateSetting=rootView.findViewById(R.id.privateSetting);
        privacySetting =rootView.findViewById(R.id.privacySetting);
        notificationSetting=rootView.findViewById(R.id.notificationSetting);
        closePrivate=rootView.findViewById(R.id.closePrivate);
        closePrivacy=rootView.findViewById(R.id.closePrivacy);
        closeNotification=rootView.findViewById(R.id.closeNotification);
        canclNoty=rootView.findViewById(R.id.canclNoty);
        cancelPrivacy=rootView.findViewById(R.id.cancelPrivacy);
        ////////
        setNavigations();


        //private Settings
        schoolUId = teacherPagesNavigator.schoolId;
        teacherUId= teacherPagesNavigator.teacherId;
        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

        inputLayoutPhone = (TextInputLayout)rootView.findViewById(R.id.textInputPhone);
        inputLayoutName = (TextInputLayout) rootView.findViewById(R.id.textInputName);
        inputLayoutEmail = (TextInputLayout) rootView.findViewById(R.id.textInputEmail);
        inputLayoutDate = (TextInputLayout) rootView.findViewById(R.id.textInputbirthDate);
        inputLAyoutLocation = (TextInputLayout) rootView.findViewById(R.id.textInputLocation);

        inputPhone=(EditText)rootView.findViewById(R.id.editTextPhone);
        inputEmail = (EditText) rootView.findViewById(R.id.editTextEmail);
        inputName = (EditText) rootView.findViewById(R.id.editTextName);
        inputDate = (EditText) rootView.findViewById(R.id.editTextbirthDate);
        inputLocation = (EditText) rootView.findViewById(R.id.editTextLocation);

        btnUpdate=(Button)rootView.findViewById(R.id.update1);
        faceImage=(CircleImageView) rootView.findViewById(R.id.faceImage);

        teacherId = pupilPagesNavigator.teacherId;
        schoolId = pupilPagesNavigator.schoolId;

        inputEmail.addTextChangedListener(new teacherSetting.MyTextWatcher(inputEmail));
        inputName.addTextChangedListener(new teacherSetting.MyTextWatcher(inputName));
        inputDate.addTextChangedListener(new teacherSetting.MyTextWatcher(inputDate));
        inputPhone.addTextChangedListener(new teacherSetting.MyTextWatcher(inputPhone));
        inputLocation.addTextChangedListener(new teacherSetting.MyTextWatcher(inputLocation));
        setData();
        setFaceAndCertifacateImage();
        setDateListeners();

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
                db.collection(Constants.UIDS).document(teacherId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ArrayList<String> schoolsId=(ArrayList<String>) documentSnapshot.get(Constants.schoolsUIDs);
                        for(String schoolId:schoolsId){
                            db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(teacherUId)
                                    .update(Constants.teacherNotiication,switchNotification.isChecked()).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

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
        }
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

    private void setDateListeners(){
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
        final Teacher teacherToAdd=new Teacher();
        teacherToAdd.setTeacherBirthdate(birthDate);
        teacherToAdd.setTeacherMail(inputEmail.getText().toString());
        teacherToAdd.setTeacherName(inputName.getText().toString());
        teacherToAdd.setTeacherPhone(teacherPhone);
        teacherToAdd.setTeacherLocation(inputLocation.getText().toString());

        db.collection(Constants.drivingSchool).document(schoolUId).collection(Constants.teachers).document(teacherUId)
                .update(Constants.teacherBirthdate,teacherToAdd.getTeacherBirthdate(),
                Constants.teacherPhone,teacherToAdd.getTeacherPhone(),Constants.teacherMail,teacherToAdd.getTeacherMail()
                ,Constants.teacherName,teacherToAdd.getTeacherName(),Constants.teacherLocation,teacherToAdd.getTeacherLocation()).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(faceImage.getDrawable() == null){
                            teacherPagesNavigator.birthDate=teacherToAdd.getTeacherBirthdate().toString();
                            teacherPagesNavigator.birthDateDate=teacherToAdd.getTeacherBirthdate();
                            teacherPagesNavigator.Email=teacherToAdd.getTeacherMail();
                            teacherPagesNavigator.Phone=teacherToAdd.getTeacherPhone();
                            teacherPagesNavigator.fullName=teacherToAdd.getTeacherName();
                            pDialog.dismiss();
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
                    pDialog.dismiss();
                    new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("failed to upload photo")
                            .setContentText(exception.getMessage().toString())
                            .show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                    pDialog.dismiss();
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


    }
    private void setData(){
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog.show();
                if (submitForm()) {
                    resetOnClick();
                } else {
                    pDialog.dismiss();
                }
            }
        });
        faceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickimageFromGaller();
            }
        });

        teacherPhone = teacherPagesNavigator.Phone==null?"":teacherPagesNavigator.Phone;
        if(teacherPhone!="")
        inputPhone.setText(teacherPhone);
        String teacherAddress = teacherPagesNavigator.Address==null?"":teacherPagesNavigator.Address;
        if(teacherAddress!="")
        inputLocation.setText(teacherAddress);
        String teacherName = teacherPagesNavigator.fullName;
        inputName.setText(teacherName);
        String teacherEmail = teacherPagesNavigator.Email==null?"":teacherPagesNavigator.Email;
        if (teacherEmail!="")
        inputEmail.setText(teacherEmail);
        String dateBirth=teacherPagesNavigator.birthDate==null?"":teacherPagesNavigator.birthDate;
        if(dateBirth!="") {
            birthDate=teacherPagesNavigator.birthDateDate;
            String[] theorySplit = dateBirth.split(" ");
            inputDate.setText(theorySplit[2] + "-" + getDateNum(theorySplit[1]) + "-" + theorySplit[5]);
        }
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
    private void setNavigations() {
        rootView.findViewById(R.id.backkk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new teacherHomePage(),Constants.tagTeacherHome).commit();
            }
        });
        privateSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.teacher_settings).setVisibility(View.GONE);
                rootView.findViewById(R.id.teacher_settings_1).setVisibility(View.VISIBLE);
            }
        });
        privacySetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.teacher_settings).setVisibility(View.GONE);
                rootView.findViewById(R.id.teacher_settings_2).setVisibility(View.VISIBLE);
            }
        });
        notificationSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.teacher_settings).setVisibility(View.GONE);
                rootView.findViewById(R.id.teacher_settings_3).setVisibility(View.VISIBLE);
            }
        });
        closePrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.teacher_settings).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.teacher_settings_1).setVisibility(View.GONE);
            }
        });
        closePrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.teacher_settings).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.teacher_settings_2).setVisibility(View.GONE);
            }
        });
        closeNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.teacher_settings).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.teacher_settings_3).setVisibility(View.GONE);
            }
        });
        cancelPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.teacher_settings).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.teacher_settings_2).setVisibility(View.GONE);
            }
        });
        canclNoty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.teacher_settings).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.teacher_settings_3).setVisibility(View.GONE);
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
        if(!validateDate()){
            pDialog.dismiss();
            return false;
        }


        if (!validateLocation()) {
            pDialog.dismiss();
            return false;
        }

        return true;
    }

    private boolean validateDate(){
        if(inputDate.getText().toString().trim().isEmpty()) {
            inputLayoutDate.setError("בחר תאריך לידה");
            requestFocus(inputDate);
            return false;
        }else{
            inputLayoutDate.setErrorEnabled(false);
        }

        return true;
    }
    private boolean validateLocation(){
        if(inputLocation.getText().toString().trim().isEmpty()){
            inputLAyoutLocation.setError("מלא מיקום");
            requestFocus(inputLocation);
            return false;
        }else{
            inputLAyoutLocation.setErrorEnabled(false);
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
        teacherPhone = inputPhone.getText().toString().trim();
        if (teacherPhone.isEmpty()) {
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
                case R.id.editTextbirthDate:
                    validateDate();
                    break;
                case R.id.editTextLocation:
                    validateLocation();
                    break;

            }
        }
    }
}