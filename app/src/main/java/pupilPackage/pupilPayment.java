package pupilPackage;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.mainPages.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import adapters.pupilPaymentAdapter;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import cn.pedant.SweetAlert.SweetAlertDialog;
import databaseClasses.Constants;
import databaseClasses.Pupil;
import databaseClasses.lessonPayment;
import databaseClasses.notification;
import databaseClasses.payment;
import databaseClasses.sqlLitDbHelper;
import teacherPackage.teacherHomePage;
import teacherPackage.teacherPagesNavigator;

public class pupilPayment {
    private String schoolId;
    private String teacherId;
    private String pupilId;
    private FirebaseFirestore db;
    private ImageButton addPaymant;
    private boolean isPupilPage;
    private Context context;
    private RecyclerView mRecyclerView;
    private ArrayList<payment> payments;
    private TextView totalPaysNeeded;
    private TextView totalPays;
    private TextView percentPay;
    private Double totalPayNeeded;
    private Double totalPay;
    private static pupilPaymentAdapter paymentAdapter;
    public void onCreate(Context context,boolean isPupilPage,String pupilId){
        this.context=context;
        totalPayNeeded=0.0;
        totalPay=0.0;
        this.isPupilPage=isPupilPage;
        this.pupilId=pupilId;
        payments=new ArrayList<>();
        paymentAdapter=new pupilPaymentAdapter(context,null,pupilPayment.this);
        db=FirebaseFirestore.getInstance();
        if(isPupilPage){
            this.schoolId=pupilPagesNavigator.schoolId;
            this.teacherId=pupilPagesNavigator.teacherId;
            this.pupilId=pupilPagesNavigator.pupilId;
            this.mRecyclerView=pupilHomePage.paymentRecycler;
            totalPaysNeeded=pupilHomePage.totalPayNeeded;
            totalPays=pupilHomePage.totalPays;
            percentPay=pupilHomePage.paymentPercent;
        }else{
            this.addPaymant=teacherHomePage.pupilAddPayment;
            this.schoolId= teacherPagesNavigator.schoolId;
            this.teacherId=teacherPagesNavigator.teacherId;
            this.pupilId=pupilId;
            this.mRecyclerView= teacherHomePage.paymentRecycler;
            totalPaysNeeded=teacherHomePage.totalPaysNeeded;
            totalPays=teacherHomePage.totalPays;
            percentPay=teacherHomePage.percentPayment;
            teacherHomePage.pupilPaymentPic.setVisibility(View.GONE);
            addPaymant.setVisibility(View.VISIBLE);
        }
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        setData();
        if(!isPupilPage && pupilId!=null)
        setAddPayment();
    }

    private void setAddPayment() {
        addPaymant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddPaymentDialog(null,null);
            }
        });
    }

    public void showAddPaymentDialog(final Calendar paymentDate, Double lastPayInsert){
        final Dialog myDialog;
        final boolean isUpdatePayment=paymentDate!=null?true:false;
        myDialog = new Dialog(context);
        myDialog.setContentView(R.layout.pop_up_add_payment);
        final ImageView facePhoto=myDialog.findViewById(R.id.facePhoto);
        final TextView pupilNameInsert=myDialog.findViewById(R.id.pupilName);
        ImageButton exit=myDialog.findViewById(R.id.exit);
        TextView todayDate=(TextView)myDialog.findViewById(R.id.todayDate);
        TextView todayTime=(TextView)myDialog.findViewById(R.id.todayTime);
        final EditText totalPayInsert=(EditText) myDialog.findViewById(R.id.totalPay);
        final CircularProgressButton saveButton=(CircularProgressButton)myDialog.findViewById(R.id.save);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        Date date1=Calendar.getInstance().getTime();
        if(!isUpdatePayment) {
            todayDate.setText(String.format(Constants.datePrintingFormat, Calendar.getInstance().get(Calendar.DATE), Calendar.getInstance().get(Calendar.MONTH) + 1
                    , Calendar.getInstance().get(Calendar.YEAR)));
            todayTime.setText(String.format(Constants.timePrintingFormat, date1.getHours(), date1.getMinutes()));
        }else{
            date1=paymentDate.getTime();
            totalPayInsert.setText(lastPayInsert.toString());
            todayDate.setText(String.format(Constants.datePrintingFormat, paymentDate.get(Calendar.DATE), paymentDate.get(Calendar.MONTH) + 1
                    , Calendar.getInstance().get(Calendar.YEAR)));
            todayTime.setText(String.format(Constants.timePrintingFormat, paymentDate.get(Calendar.HOUR), paymentDate.get(Calendar.MINUTE)));
        }
        final Date date=date1;
        db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(teacherId).collection(Constants.pupils)
                .document(pupilId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful() && task.getResult().exists()){
                    Pupil pupil=task.getResult().toObject(Pupil.class);
                    pupilNameInsert.setText(pupil.getPupilName());
                }
            }
        });
        FirebaseStorage.getInstance().getReferenceFromUrl(Constants.pupilsStoragePath+pupilId+"/"+Constants.faceImage+"/")
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
                                Bitmap circleBitmap = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);

                                BitmapShader shader = new BitmapShader(bm, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                                Paint paint = new Paint();
                                paint.setShader(shader);
                                paint.setAntiAlias(true);
                                Canvas c = new Canvas(circleBitmap);
                                c.drawCircle(bm.getWidth() / 2, bm.getHeight() / 2, bm.getWidth() / 2, paint);

                                facePhoto.setImageBitmap(circleBitmap);
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

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalPayInsert.setText(totalPayInsert.getText().toString().replaceAll(" ",""));
                saveButton.startAnimation();
                if(totalPayInsert.getText().toString().isEmpty()){
                    saveButton.revertAnimation();
                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("לא הוכנס סכום כסף")
                            .setConfirmText("בסדר")
                            .show();
                    return;
                }
                db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(teacherId).collection(Constants.pupils)
                        .document(pupilId).collection(Constants.lessons).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && !task.getResult().isEmpty()){
                            Double totalPay=0.0;
                            for(QueryDocumentSnapshot doc:task.getResult()){
                                lessonPayment lessonPayment=doc.toObject(lessonPayment.class);
                                totalPay+=lessonPayment.getLessonCost()==null?80:lessonPayment.getLessonCost();
                            }
                            final Double totalPayFinal=totalPay;
                            db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(teacherId).collection(Constants.pupils)
                                    .document(pupilId).collection(Constants.payments).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful() && !task.getResult().isEmpty()){
                                        Double totalPayed=0.0;
                                        for(QueryDocumentSnapshot doc:task.getResult()){
                                            payment payment=doc.toObject(databaseClasses.payment.class);
                                            if(payment.isAccepted())
                                            totalPayed+=payment.getTotalPay();
                                        }
                                        if(totalPayFinal-totalPayed>=Double.parseDouble(totalPayInsert.getText().toString())){

                                            final Dialog sendDialog=new Dialog(context);
                                            sendDialog.setContentView(R.layout.pop_up_send_pay_noty);
                                            final TextView totalPay=sendDialog.findViewById(R.id.totalPay);
                                            final TextView pupilName=sendDialog.findViewById(R.id.pupilName);
                                            ImageButton exit=sendDialog.findViewById(R.id.exit);
                                            final CircularProgressButton send=sendDialog.findViewById(R.id.save);

                                            exit.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    sendDialog.dismiss();
                                                }
                                            });
                                            totalPay.setText(totalPayInsert.getText().toString());
                                            pupilName.setText(pupilNameInsert.getText().toString());
                                            send.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    send.startAnimation();
                                                    final notification notyToAdd=new notification();
                                                    notyToAdd.setTimeNoty(date);
                                                    notyToAdd.setRead(false);
                                                    notyToAdd.setBody("ביצוע תשלום ");
                                                    notyToAdd.setTitle("אישור תשלום במזומן");
                                                    notyToAdd.setTotalPaymentAdded(Double.parseDouble(totalPayInsert.getText().toString()));
                                                    db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(teacherId)
                                                            .collection(Constants.pupils).document(pupilId).collection(Constants.notifications).document(date.toString())
                                                            .set(notyToAdd);
                                                    final payment payment=new payment();
                                                    payment.setTotalPay(Double.parseDouble(totalPayInsert.getText().toString()));
                                                    payment.setPayDate(date);
                                                    payment.setAccepted(false);
                                                    db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(teacherId)
                                                            .collection(Constants.pupils).document(pupilId).collection(Constants.payments).document(date.toString())
                                                            .set(payment).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            send.revertAnimation();
                                                            payments.add(0,payment);
                                                            pupilPayment.paymentAdapter.setPayments(payments);
                                                            pupilPayment.paymentAdapter.notifyDataSetChanged();
                                                            new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                                                                    .setTitleText("Good job!")
                                                                    .setContentText("נתוני תשלום נשלחו לתלמיד")
                                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                        @Override
                                                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                            sweetAlertDialog.dismiss();
                                                                            sendDialog.dismiss();
                                                                            myDialog.dismiss();
                                                                        }
                                                                    })
                                                                    .show();
                                                        }
                                                    });

                                                }
                                            });
                                            sendDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                            sendDialog.show();
                                            saveButton.revertAnimation();
                                        }
                                        else{
                                            saveButton.revertAnimation();
                                            new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                                    .setTitleText("סכום גבוהה מהחוב")
                                                    .setContentText("אי אפשר להכניס סכום כסף יותר ממה שהתלמיד חייב")
                                                    .setConfirmText("בסדר")
                                                    .show();
                                        }
                                    }else {
                                        if(totalPayFinal>=Double.parseDouble(totalPayInsert.getText().toString())){

                                            final Dialog sendDialog=new Dialog(context);
                                            sendDialog.setContentView(R.layout.pop_up_send_pay_noty);
                                            TextView totalPay=sendDialog.findViewById(R.id.totalPay);
                                            TextView pupilName=sendDialog.findViewById(R.id.pupilName);
                                            ImageButton exit=sendDialog.findViewById(R.id.exit);
                                            final CircularProgressButton send=sendDialog.findViewById(R.id.save);

                                            exit.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    sendDialog.dismiss();
                                                }
                                            });
                                            totalPay.setText(totalPayInsert.getText().toString());
                                            pupilName.setText(pupilNameInsert.getText().toString());
                                            send.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    send.startAnimation();
                                                    notification noty=new notification();
                                                    noty.setTimeNoty(date);
                                                    noty.setRead(false);
                                                    noty.setBody("בוצע תשלום");
                                                    noty.setTitle("אישור תשלום במזומן");
                                                    noty.setTotalPaymentAdded(Double.parseDouble(totalPayInsert.getText().toString()));
                                                    db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(teacherId)
                                                            .collection(Constants.pupils).document(pupilId).collection(Constants.notifications).document(date.toString())
                                                            .set(noty);
                                                    final payment payment=new payment();
                                                    payment.setTotalPay(Double.parseDouble(totalPayInsert.getText().toString()));
                                                    payment.setPayDate(date);
                                                    payment.setAccepted(false);
                                                    db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(teacherId)
                                                            .collection(Constants.pupils).document(pupilId).collection(Constants.payments).document(date.toString())
                                                            .set(payment).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            send.revertAnimation();
                                                            payments.add(0,payment);
                                                            pupilPayment.paymentAdapter.setPayments(payments);
                                                            pupilPayment.paymentAdapter.notifyDataSetChanged();
                                                            new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                                                                    .setTitleText("Good job!")
                                                                    .setContentText("נתוני תשלום נשלחו לתלמיד")
                                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                        @Override
                                                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                            sweetAlertDialog.dismiss();
                                                                            sendDialog.dismiss();
                                                                            myDialog.dismiss();
                                                                        }
                                                                    })
                                                                    .show();
                                                        }
                                                    });


                                                }
                                            });
                                            saveButton.revertAnimation();
                                            sendDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                            sendDialog.show();

                                        }
                                        else{
                                            saveButton.revertAnimation();
                                            new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                                    .setTitleText("סכום גבוהה מהחוב")
                                                    .setContentText("אי אפשר להכניס סכום כסף יותר ממה שהתלמיד חייב")
                                                    .setConfirmText("בסדר")
                                                    .show();
                                        }
                                    }
                                }
                            });
                        }else{
                            saveButton.revertAnimation();
                            new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("אין חוב")
                                    .setContentText("התלמיד עדיין לא לקח שיעורים לכן אין חוב עליו")
                                    .setConfirmText("בסדר")
                                    .show();
                        }
                    }
                });

            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }
    private void setData() {
        if(sqlLitDbHelper.isConnected(this.context)){
            if(pupilId!=null)
            db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(teacherId).collection(Constants.pupils)
                    .document(pupilId).collection(Constants.lessons).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful() && !task.getResult().isEmpty()){
                        for(QueryDocumentSnapshot doc:task.getResult()){
                            lessonPayment lesson=doc.toObject(lessonPayment.class);
                            totalPayNeeded+=lesson.getLessonCost()==null?80:lesson.getLessonCost();
                        }
                        totalPaysNeeded.setText(totalPayNeeded.toString());
                        db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(teacherId).collection(Constants.pupils)
                                .document(pupilId).collection(Constants.payments).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful() && !task.getResult().isEmpty()){
                                    for(QueryDocumentSnapshot doc:task.getResult()){
                                        payment payment=doc.toObject(payment.class);
                                        payments.add(payment);
                                        if(payment.isAccepted())
                                        totalPay+=payment.getTotalPay();
                                    }
                                    pupilPayment.paymentAdapter=new pupilPaymentAdapter(context,payments,pupilPayment.this);
                                    mRecyclerView.setAdapter(pupilPayment.paymentAdapter);
                                    totalPays.setText(totalPay.toString());
                                    Integer intValue=(int)((totalPay/totalPayNeeded)*100);
                                    percentPay.setText(intValue.toString()+"%");
                                }else{
                                    totalPays.setText("0");
                                    percentPay.setText("0%");
                                }
                            }
                        });
                    }
                }
            });
        }
    }
}
