package adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.mainPages.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import databaseClasses.Constants;
import databaseClasses.Teacher;
import databaseClasses.notification;
import teacherPackage.teacherPagesNavigator;

public class teacherNotificationAdapter extends RecyclerView.Adapter<teacherNotificationAdapter.AppViewHolder>{
    private String notyTitle;
    private String notyContent;
    private String notyTime;
    private Context context;
    private ArrayList<notification> notys = new ArrayList<>();

    public teacherNotificationAdapter(Context context, ArrayList<notification> notyToRecycler) {
        this.context = context;
        this.notys = notyToRecycler;
    }

    public static class AppViewHolder extends RecyclerView.ViewHolder {
        TextView notyTitle;
        TextView notyContent;
        TextView notyTime;
        TextView notyDate;
        ImageButton expandNoty;
        ImageView isRead;
        CardView card;
        public AppViewHolder(View itemView) {
            super(itemView);
            notyTitle = (TextView) itemView.findViewById(R.id.notyTitle);
            notyContent = (TextView) itemView.findViewById(R.id.notyContent);
            notyTime = (TextView) itemView.findViewById(R.id.notyTime);
            notyDate = (TextView) itemView.findViewById(R.id.notyDate);
            expandNoty = (ImageButton) itemView.findViewById(R.id.expandNoty);
            isRead = (ImageView) itemView.findViewById(R.id.notyIsRead);
            card=(CardView) itemView.findViewById(R.id.cardNoty);
        }
    }

    @NonNull
    @Override
    public teacherNotificationAdapter.AppViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pupil_notification_adapter_row, viewGroup, false);
        // set the view's size, margins, paddings and layout parameters
        teacherNotificationAdapter.AppViewHolder vh = new teacherNotificationAdapter.AppViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final teacherNotificationAdapter.AppViewHolder appViewHolder, int position1) {
        final notification noty=notys.get(position1);
        appViewHolder.notyTitle.setText(noty.getTitle());
        appViewHolder.notyContent.setText(noty.getBody());
        Calendar cal=Calendar.getInstance();
        cal.setTime(noty.getTimeNoty());
        String date="";
        boolean newNoty=false;
        if(cal.get(Calendar.DATE)==Calendar.getInstance().get(Calendar.DATE)&& cal.get(Calendar.MONTH)==Calendar.getInstance().get(Calendar.MONTH)
                && cal.get(Calendar.YEAR)==Calendar.getInstance().get(Calendar.YEAR))
        {
            newNoty=true;
            date="היום";
        }
        if(cal.get(Calendar.DATE)+1==Calendar.getInstance().get(Calendar.DATE) && cal.get(Calendar.MONTH)==Calendar.getInstance().get(Calendar.MONTH)
                && cal.get(Calendar.YEAR)==Calendar.getInstance().get(Calendar.YEAR))
        {
            newNoty=true;
            date="אתמול";
        }
        appViewHolder.notyDate.setText(newNoty?date:String.format(Constants.datePrintingFormat,cal.get(Calendar.DATE),cal.get(Calendar.MONTH)+1,
                cal.get(Calendar.YEAR)));
        appViewHolder.notyTime.setText(String.format(Constants.timePrintingFormat,cal.get(Calendar.HOUR),cal.get(Calendar.MINUTE)));
        appViewHolder.isRead.setVisibility(noty.isRead()?View.GONE:View.VISIBLE);
        appViewHolder.expandNoty.setVisibility(View.VISIBLE);
        if(noty.getSchoolIdToAdd()!=null){
            appViewHolder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseFirestore.getInstance().collection(Constants.drivingSchool).document(noty.getSchoolIdToAdd()).get().addOnCompleteListener(
                            new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful() && task.getResult().exists()){

                                        final Dialog dialog=new Dialog(context);
                                        dialog.setContentView(R.layout.teacher_add_school_dialog);
                                        ((TextView)dialog.findViewById(R.id.schoolName)).setText(task.getResult().get(Constants.schoolName).toString());
                                        final Button add=dialog.findViewById(R.id.yesAddSchool);
                                        final Button dont=dialog.findViewById(R.id.noDontAdd);
                                        ImageButton exit=dialog.findViewById(R.id.exit);
                                        exit.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog.dismiss();
                                            }
                                        });
                                        dont.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog.dismiss();
                                            }
                                        });
                                        FirebaseFirestore.getInstance().collection(Constants.UIDS).document(teacherPagesNavigator.teacherId).get().addOnCompleteListener(
                                                new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if(task.isSuccessful() && task.getResult().exists()){
                                                            if((((ArrayList<String>)task.getResult().get(Constants.schoolsUIDs)).contains(noty.getSchoolIdToAdd()))){
                                                            add.setVisibility(View.GONE);
                                                            dont.setVisibility(View.GONE);
                                                                ((TextView)dialog.findViewById(R.id.contentMsg)).setText("כבר הצטרפת אליו אחרי שקיבלנו אישורך");
                                                            }
                                                        }
                                                    }
                                                }
                                        );
                                        add.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                final SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
                                                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                                pDialog.setTitleText("Loading");
                                                pDialog.setCancelable(false);
                                                pDialog.show();
                                                FirebaseFirestore.getInstance().collection(Constants.UIDS).document(teacherPagesNavigator.teacherId).get().
                                                        addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if(task.isSuccessful() && task.getResult().exists()){
                                                                    ArrayList<String> schoolss=(ArrayList<String>) task.getResult().get(Constants.schoolsUIDs);
                                                                    schoolss.add(noty.getSchoolIdToAdd());
                                                                    FirebaseFirestore.getInstance().collection(Constants.UIDS).document(teacherPagesNavigator
                                                                    .teacherId).update(Constants.schoolsUIDs,schoolss);
                                                                }
                                                            }
                                                        });
                                                FirebaseFirestore.getInstance().collection(Constants.drivingSchool).document(teacherPagesNavigator.schoolId)
                                                        .collection(Constants.teachers).document(teacherPagesNavigator.teacherId).get().addOnCompleteListener(
                                                        new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful() && task.getResult().exists()){
                                                                    Teacher teacher=task.getResult().toObject(Teacher.class);
                                                                    FirebaseFirestore.getInstance().collection(Constants.drivingSchool).document(noty.
                                                                            getSchoolIdToAdd()).collection(Constants.teachers).document(teacherPagesNavigator.teacherId)
                                                                            .set(teacher).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isSuccessful()){
                                                                                FirebaseFirestore.getInstance().collection(Constants.UIDS).document(teacherPagesNavigator.teacherId)
                                                                                        .collection(Constants.notifications).whereEqualTo(Constants.timeNoty, noty.getTimeNoty()).get().addOnCompleteListener(
                                                                                        new OnCompleteListener<QuerySnapshot>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                                                                                    for (QueryDocumentSnapshot doc : task.getResult()) {
                                                                                                        doc.getReference().update(Constants.notyIsRead, true);
                                                                                                        pDialog.dismiss();
                                                                                                        dialog.dismiss();
                                                                                                        appViewHolder.isRead.setVisibility(View.GONE);
                                                                                                        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                                                                                                                .setTitleText("Good job!")
                                                                                                                .setContentText("הצטרפות לבית ספר חדש הצליחה")
                                                                                                                .show();
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                );

                                                                            }
                                                                        }
                                                                    });
                                                                }else{
                                                                    pDialog.dismiss();
                                                                }
                                                            }
                                                        }
                                                );
                                            }
                                        });
                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        dialog.show();
                                        FirebaseFirestore.getInstance().collection(Constants.UIDS).document(teacherPagesNavigator.teacherId).collection(Constants.notifications)
                                                .whereEqualTo(Constants.timeNoty, noty.getTimeNoty()).get().addOnCompleteListener(
                                                new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                                                doc.getReference().update(Constants.notyIsRead, true);
                                                            }
                                                        }
                                                    }
                                                }
                                        );
                                        appViewHolder.isRead.setVisibility(View.GONE);
                                    }
                                }
                            }
                    );

                }
            });
        }
    }
    @Override
    public int getItemCount() {
        if(notys==null){
            return 0;
        }else
        {
            return notys.size();
        }
    }

}
