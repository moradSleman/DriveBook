package adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.mainPages.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import databaseClasses.Constants;
import databaseClasses.Test;
import databaseClasses.TimeScheduling;



public class teacherCalendarAdapter extends RecyclerView.Adapter<teacherCalendarAdapter.AppViewHolder>{
    List<TimeScheduling> timesListToRecycle;
    String schoolId;
    String teacherId;
    private FirebaseFirestore db;
    private Context context;
    public teacherCalendarAdapter(Context context, ArrayList<TimeScheduling> timesToRecycle, String schoolId, String teacherId) {
        this.timesListToRecycle=timesToRecycle;
        this.schoolId=schoolId;
        this.teacherId=teacherId;
        this.context=context;
        db=FirebaseFirestore.getInstance();
    }

    public static class AppViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        CardView mCardView;
        TextView startTime;
        TextView endTime;
        TextView pupilName;
        TextView testOrLesson;
        ImageView profileImage;
        ImageView removeSchedule;

        public AppViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.scheduleCard);
            startTime = (TextView) itemView.findViewById(R.id.startTime);
            endTime = (TextView) itemView.findViewById(R.id.endTime);
            pupilName = (TextView) itemView.findViewById(R.id.pupilName);
            testOrLesson = (TextView) itemView.findViewById(R.id.lessonOrTest);
            profileImage = (ImageView) itemView.findViewById(R.id.profileImage);
            removeSchedule = (ImageView) itemView.findViewById(R.id.removeSchedule);
        }
    }
    @NonNull
    @Override
    public teacherCalendarAdapter.AppViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.teacher_calendar_adapter_row, viewGroup, false);
        // set the view's size, margins, paddings and layout parameters
        AppViewHolder vh = new AppViewHolder(v);
        return vh;
    }
    @Override
    public void onBindViewHolder(@NonNull final teacherCalendarAdapter.AppViewHolder appViewHolder, int position1) {
        final int position=position1;
        Calendar startCal=Calendar.getInstance();
        startCal.setTime(timesListToRecycle.get(position).getStartTime());
        Integer startHour=startCal.get(Calendar.HOUR_OF_DAY);
        Integer startMin=startCal.get(Calendar.MINUTE);

        Calendar endCal=Calendar.getInstance();
        endCal.setTime(timesListToRecycle.get(position).getEndTime());

        appViewHolder.startTime.setText(String.format(Constants.timePrintingFormat,startHour,startMin));
        appViewHolder.endTime.setText(((Long)(timesListToRecycle.get(position).getEndTime().getTime()/60000-timesListToRecycle.get(position).getStartTime().getTime()/60000)).toString()+"דק");

        if(timesListToRecycle.get(position).isTest()){

                db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(teacherId).collection(Constants.pupils)
                        .document(timesListToRecycle.get(position).getPupilUId().toString()).collection(Constants.tests).get().addOnCompleteListener(
                        new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful() && !task.getResult().isEmpty()){
                                    int i=0;
                                    int notI=0;
                                    for(QueryDocumentSnapshot doc:task.getResult()){
                                        Test test=doc.toObject(Test.class);
                                        if(test.isInternalTest()){
                                            i++;
                                        }else{
                                            notI++;
                                        }
                                    }
                                    if(timesListToRecycle.get(position).isEnternalTest()) {
                                        appViewHolder.testOrLesson.setText("טסט פנימי מספר "+i);
                                    }else{
                                        appViewHolder.testOrLesson.setText("טסט חיצוני מספר "+notI);
                                    }
                                }
                            }
                        }
                );
        }
        appViewHolder.removeSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("אתה בטוח")
                        .setContentText("אי אפשר להחזיר את הזמן הזה רק במידה אם תשבץ זמנים חדשים")
                        .setConfirmText("כן תמחק")
                        .setCancelText("ביטול")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                                final SweetAlertDialog pDialog =new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
                                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                pDialog.setTitleText("Loading");
                                pDialog.setCancelable(false);
                                pDialog.show();
                                    db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(teacherId)
                                            .collection(Constants.schedules).document(timesListToRecycle.get(position).getDate().toString())
                                            .collection(Constants.oneDayeSchedules).document(timesListToRecycle.get(position).getStartTime().toString())
                                            .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(teacherId)
                                                    .collection(Constants.schedules).document(timesListToRecycle.get(position).getDate().toString())
                                                    .collection(Constants.oneDayeSchedules).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.isSuccessful() && !task.getResult().isEmpty()) {
                                                            int newPosition = appViewHolder.getAdapterPosition();
                                                            timesListToRecycle.remove(newPosition);
                                                            notifyItemRemoved(newPosition);
                                                            notifyItemRangeChanged(newPosition, timesListToRecycle.size());
                                                            pDialog.dismiss();
                                                            new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                                                                    .setTitleText("נמחק!")
                                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                        @Override
                                                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                            sweetAlertDialog.dismiss();
                                                                        }
                                                                    })
                                                                    .show();
                                                        }else{
                                                            db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(teacherId)
                                                                    .collection(Constants.schedules).document(timesListToRecycle.get(position).getDate().toString())
                                                                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    int newPosition = appViewHolder.getAdapterPosition();
                                                                    timesListToRecycle.remove(newPosition);
                                                                    notifyItemRemoved(newPosition);
                                                                    notifyItemRangeChanged(newPosition, timesListToRecycle.size());
                                                                    pDialog.dismiss();
                                                                    new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                                                                            .setTitleText("נמחק!")
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
                                            });
                                        }
                                    });
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        if(timesListToRecycle.get(position).getPupilFullName()!=null)
        {
            appViewHolder.pupilName.setText(timesListToRecycle.get(position).getPupilFullName());
        }
        else{
            appViewHolder.pupilName.setText("לא משובץ");
        }
        if(timesListToRecycle.get(position).getPupilUId()!=null)
        {
            FirebaseStorage.getInstance().getReferenceFromUrl(Constants.pupilsStoragePath + timesListToRecycle.get(position).getPupilUId() + "/"+Constants.faceImage+"/")
                    .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    StorageReference mImageRef =
                            FirebaseStorage.getInstance().getReferenceFromUrl(Constants.pupilsStoragePath + timesListToRecycle.get(position).getPupilUId() + "/"+Constants.faceImage+"/");
                    final long ONE_MEGABYTE = 1024 * 1024;
                    mImageRef.getBytes(ONE_MEGABYTE)
                            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    appViewHolder.profileImage.setImageBitmap(bm);
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
        else
        {

        }

        TimeScheduling currentTimeRow=timesListToRecycle.get(position);
        db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(teacherId)
                .collection(Constants.schedules).document(currentTimeRow.getDate().toString()).collection(Constants.oneDayeSchedules).
                document(currentTimeRow.getStartTime().toString()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                String source = documentSnapshot != null && documentSnapshot.getMetadata().hasPendingWrites()
                        ? "Local" : "Server";

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    final TimeScheduling timeUpdated=documentSnapshot.toObject(TimeScheduling.class);
                    if(timeUpdated.getPupilFullName()!=null && timeUpdated.getPupilUId()!=null) {
                        appViewHolder.pupilName.setText(timeUpdated.getPupilFullName().toString());
                        FirebaseStorage.getInstance().getReferenceFromUrl(Constants.pupilsStoragePath +
                                timeUpdated.getPupilUId() + "/" + Constants.faceImage + "/")
                                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                StorageReference mImageRef =
                                        FirebaseStorage.getInstance().getReferenceFromUrl(Constants.pupilsStoragePath +
                                                timeUpdated.getPupilUId() + "/" + Constants.faceImage + "/");
                                final long ONE_MEGABYTE = 1024 * 1024;
                                mImageRef.getBytes(ONE_MEGABYTE)
                                        .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                            @Override
                                            public void onSuccess(byte[] bytes) {
                                                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                appViewHolder.profileImage.setImageBitmap(bm);
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
                } else {
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if(timesListToRecycle==null){
            return 0;
        }else
        {
           return timesListToRecycle.size();
        }
    }
}
