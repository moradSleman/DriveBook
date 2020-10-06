package adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.user.mainPages.FullscreenActivity;
import com.example.user.mainPages.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import cn.pedant.SweetAlert.SweetAlertDialog;
import databaseClasses.Constants;
import databaseClasses.Test;
import pupilPackage.pupilTest;
import teacherPackage.teacherPagesNavigator;

public class pupilTestAdapter  extends RecyclerView.Adapter<adapters.pupilTestAdapter.AppViewHolder>{
    List<Test> Tests;
    private Context context;
    private boolean ispupilPage;
    private Activity activity;
    public static ImageButton testResultImage;
    public pupilTestAdapter(Context context, ArrayList<Test> pupilTests,boolean isPupilPage) {
        this.Tests =pupilTests;
        this.context=context;
        this.ispupilPage=isPupilPage;
        activity=(Activity)context;
    }
    public static class AppViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            CardView mCardView;
            TextView testDate;
            TextView testTime;
            ImageView testPaperResult;
            public AppViewHolder(View itemView) {
                super(itemView);

                mCardView = (CardView) itemView.findViewById(R.id.row);
                testDate = (TextView) itemView.findViewById(R.id.testDate);
                testTime = (TextView) itemView.findViewById(R.id.testTime);
                testPaperResult = (ImageView) itemView.findViewById(R.id.testResultPaper);
            }
        }
            @NonNull
            @Override
            public adapters.pupilTestAdapter.AppViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pupil_test_adapter_row, viewGroup, false);
                // set the view's size, margins, paddings and layout parameters
                adapters.pupilTestAdapter.AppViewHolder vh = new adapters.pupilTestAdapter.AppViewHolder(v);
                return vh;
            }
            @Override
            public void onBindViewHolder(@NonNull final adapters.pupilTestAdapter.AppViewHolder appViewHolder, int position1) {
                final int position=position1;
                final Test currentTest= Tests.get(position);
                Calendar startCal=Calendar.getInstance();
                startCal.setTime(currentTest.getTestTimeStart());
                Integer startHour=startCal.get(Calendar.HOUR_OF_DAY);
                Integer startMin=startCal.get(Calendar.MINUTE);
                appViewHolder.testTime.setText(String.format(Constants.timePrintingFormat,startHour,startMin));
                SimpleDateFormat simple=new SimpleDateFormat("dd-MM-yyyy");
                appViewHolder.testDate.setText(simple.format(currentTest.getTestDate()));

                appViewHolder.testPaperResult.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        final Dialog myDialog;
                        boolean imagePick=false;
                        final LinearLayout passed;
                        final LinearLayout failed;
                        ImageButton imagePassed;
                        ImageButton imageFailed;
                        ImageButton exit;
                        final CircularProgressButton save;
                        TextView chooseResult;
                        TextView uploadImage;
                        TextView clickToExpandImage;

                        myDialog = new Dialog(context);
                        myDialog.setContentView(R.layout.pop_up_test_info);

                        passed=myDialog.findViewById(R.id.linearPassed);
                        failed=myDialog.findViewById(R.id.linearFailed);
                        imageFailed=myDialog.findViewById(R.id.failed);
                        imagePassed=myDialog.findViewById(R.id.passed);
                        testResultImage=myDialog.findViewById(R.id.testResulImage);
                        exit=myDialog.findViewById(R.id.exit);
                        save=myDialog.findViewById(R.id.save);
                        chooseResult=myDialog.findViewById(R.id.chooseResult);
                        uploadImage=myDialog.findViewById(R.id.uploadImage);

                        SpannableString content = new SpannableString("העלה קובץ טעויות");
                        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                        uploadImage.setText(content);

                        FirebaseStorage.getInstance().getReferenceFromUrl(Constants.pupilsStoragePath + pupilTest.pupilId + "/tests/"+currentTest.getTestTimeStart()
                        .toString()+"/resultDoc/")
                                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                StorageReference mImageRef =
                                        FirebaseStorage.getInstance().getReferenceFromUrl(Constants.pupilsStoragePath + pupilTest.pupilId + "/tests/"+currentTest.getTestTimeStart()
                                                .toString()+"/resultDoc/");
                                final long ONE_MEGABYTE = 1024 * 1024;
                                mImageRef.getBytes(ONE_MEGABYTE)
                                        .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                            @Override
                                            public void onSuccess(byte[] bytes) {
                                                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                testResultImage.setImageBitmap(bm);
                                                    testResultImage.setOnClickListener(new View.OnClickListener() {

                                                        public void onClick(View v) {
                                                            testResultImage.buildDrawingCache();
                                                            Bitmap bitmap = testResultImage.getDrawingCache();
                                                            Intent intent=new Intent(context, FullscreenActivity.class);
                                                            intent.putExtra("BitmapImage", bitmap);
                                                            activity.startActivity(intent);
                                                        }
                                                    });
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


                        if(currentTest.getResult().equals(Constants.testResultSucces)){
                            passed.setVisibility(View.VISIBLE);
                            failed.setVisibility(View.GONE);
                                chooseResult.setText("עבר");
                        }
                        else {
                            if (currentTest.getResult().equals(Constants.testResultFailed)) {
                                passed.setVisibility(View.GONE);
                                failed.setVisibility(View.VISIBLE);
                                    chooseResult.setText("נכשל");
                            } else {
                                if(!ispupilPage) {
                                    imageFailed.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            passed.setVisibility(View.GONE);
                                            failed.setVisibility(View.VISIBLE);
                                            currentTest.setResult(Constants.testResultFailed);
                                        }
                                    });

                                    imagePassed.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            passed.setVisibility(View.VISIBLE);
                                            failed.setVisibility(View.GONE);
                                            currentTest.setResult(Constants.testResultSucces);
                                        }
                                    });
                                }
                            }
                        }
                        if(ispupilPage){
                            if(imageFailed.getVisibility()==View.VISIBLE && imagePassed.getVisibility()==View.VISIBLE)
                            {
                                chooseResult.setText("עדיין אין תשובה");
                            }else{
                                chooseResult.setText("");
                            }
                            uploadImage.setVisibility(View.GONE);
                            save.setVisibility(View.GONE);

                        }
                        if(!ispupilPage) {
                            uploadImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                    photoPickerIntent.setType("image/*");
                                    activity.startActivityForResult(photoPickerIntent, 1);
                                }
                            });
                            save.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    save.startAnimation();
                                    FirebaseFirestore.getInstance().collection(Constants.drivingSchool).document(teacherPagesNavigator.schoolId)
                                            .collection(Constants.teachers).document(teacherPagesNavigator.teacherId).collection(Constants.pupils)
                                            .document(pupilTest.pupilId).collection(Constants.tests).document(currentTest.getTestTimeStart().toString())
                                            .set(currentTest);
                                    if (testResultImage.getDrawable() != null) {
                                        testResultImage.buildDrawingCache();
                                        Bitmap bitmap = testResultImage.getDrawingCache();
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                        byte[] facedata = baos.toByteArray();
                                        StorageReference storageRef1 = FirebaseStorage.getInstance().getReferenceFromUrl(Constants.pupilsStoragePath).
                                                child(pupilTest.pupilId + "/tests/" + currentTest.getTestTimeStart().toString() + "/resultDoc/");
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
                                                save.stopAnimation();
                                                new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                                                        .setTitleText("Good job!")
                                                        .setContentText("נתונים נשמרו בהצלחה")
                                                        .show();
                                            }
                                        });
                                    }else{
                                        save.stopAnimation();
                                        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                                                .setTitleText("Good job!")
                                                .setContentText("נתונים נשמרו בהצלחה")
                                                .show();
                                    }
                                }
                            });
                        }

                        exit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myDialog.dismiss();
                            }
                        });
                        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        myDialog.show();
                    }
                });
            }


    @Override
    public int getItemCount() {
                if(Tests ==null){
                    return 0;
                }else
                {
                    return Tests.size();
                }
            }
}
