package adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.mainPages.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import cn.pedant.SweetAlert.SweetAlertDialog;
import databaseClasses.Constants;
import databaseClasses.notification;
import pupilPackage.pupilPagesNavigator;

public class pupilNotificationAdapter extends RecyclerView.Adapter<pupilNotificationAdapter.AppViewHolder> {
    private String notyTitle;
    private String notyContent;
    private String notyTime;
    private Context context;
    private ArrayList<notification> notys = new ArrayList<>();

    public pupilNotificationAdapter(Context context, ArrayList<notification> notyToRecycler) {
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
    public pupilNotificationAdapter.AppViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pupil_notification_adapter_row, viewGroup, false);
        // set the view's size, margins, paddings and layout parameters
        pupilNotificationAdapter.AppViewHolder vh = new pupilNotificationAdapter.AppViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final pupilNotificationAdapter.AppViewHolder appViewHolder, int position1) {
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
        if(noty.getTotalPaymentAdded()==null) {
            appViewHolder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog notyDialog=new Dialog(context);
                    notyDialog.setContentView(R.layout.pop_up_regular_noty);
                    TextView notytilte=(TextView) notyDialog.findViewById(R.id.title);
                    TextView notyContent=(TextView)notyDialog.findViewById(R.id.notyContent);
                    ImageButton exit=(ImageButton)notyDialog.findViewById(R.id.exit);
                    notytilte.setText(noty.getTitle());
                    notyContent.setText(noty.getBody());
                    exit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            notyDialog.dismiss();
                        }
                    });
                    notyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    notyDialog.show();
                    FirebaseFirestore.getInstance().collection(Constants.drivingSchool).document(pupilPagesNavigator.schoolId).collection(Constants.teachers)
                            .document(pupilPagesNavigator.teacherId).collection(Constants.pupils).document(pupilPagesNavigator.pupilId).collection(
                            Constants.notifications).whereEqualTo(Constants.timeNoty, noty.getTimeNoty()).get().addOnCompleteListener(
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
            });
        }else{
            appViewHolder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog mDialog=new Dialog(context);
                    mDialog.setContentView(R.layout.pop_up_noty_pay_pupil);
                    final CircularProgressButton save=mDialog.findViewById(R.id.save);
                    ImageButton exit=mDialog.findViewById(R.id.exit);
                    final TextView resultPayInsert = mDialog.findViewById(R.id.payInsertAnswer);
                    final EditText insertedPayValue=mDialog.findViewById(R.id.totalPay);

                    exit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                        }
                    });
                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            insertedPayValue.setText(insertedPayValue.getText().toString().replaceAll(" ",""));
                            save.startAnimation();
                           if(insertedPayValue.getText().toString().isEmpty()){
                               save.revertAnimation();
                               new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                       .setTitleText("תיבת מילוי סכום ששילמת ריקה!")
                                       .setConfirmText("בסדר")
                                       .show();
                               return;
                           }
                           if(Double.parseDouble(insertedPayValue.getText().toString())!=(Double.parseDouble(noty.getTotalPaymentAdded().toString()))){
                               save.revertAnimation();
                               resultPayInsert.setText("הסכום שהוכנס אינו תואם לסכום שהוקלד על ידי המורה נא לנסות שוב או לפנות למורה שלך לבירור");
                           }else {
                               FirebaseFirestore.getInstance().collection(Constants.drivingSchool).document(pupilPagesNavigator.schoolId).collection(
                                       Constants.teachers).document(pupilPagesNavigator.teacherId).collection(Constants.pupils).document(pupilPagesNavigator
                               .pupilId).collection(Constants.payments).document(noty.getTimeNoty().toString()).update(Constants.paymenAccepted,true);
                                save.revertAnimation();
                                mDialog.dismiss();
                               new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                                       .setTitleText("Good job!")
                                       .setContentText("תשלום הוסף בהצלחה")
                                       .show();
                           }
                        }
                    });
                    mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    mDialog.show();
                    FirebaseFirestore.getInstance().collection(Constants.drivingSchool).document(pupilPagesNavigator.schoolId).collection(Constants.teachers)
                            .document(pupilPagesNavigator.teacherId).collection(Constants.pupils).document(pupilPagesNavigator.pupilId).collection(
                            Constants.notifications).whereEqualTo(Constants.timeNoty, noty.getTimeNoty()).get().addOnCompleteListener(
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
