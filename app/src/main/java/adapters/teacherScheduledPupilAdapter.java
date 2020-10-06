package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.mainPages.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import databaseClasses.Constants;
import databaseClasses.TimeScheduling;
import teacherPackage.teacherScheduledPupil;

public class teacherScheduledPupilAdapter extends RecyclerView.Adapter<teacherScheduledPupilAdapter.AppViewHolder> {
    private FirebaseFirestore db;
    private String teacherId;
    private ArrayList<TimeScheduling> timesScheduledByPupil;
    Context context;
    public teacherScheduledPupilAdapter(Context context, ArrayList<TimeScheduling> timesScheduledByPupil, String teacherId) {
        this.context=context;
        this.teacherId=teacherId;
        this.timesScheduledByPupil=timesScheduledByPupil;
        db=FirebaseFirestore.getInstance();
    }

    public static class AppViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView date;
        ImageView profilePic;
        TextView startHour;
        TextView pupileName;
        TextView testOrLesson;
        TextView schoolName;
        CheckBox checkToCancle;
        public AppViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            startHour  = itemView.findViewById(R.id.startHour11);
            pupileName  = itemView.findViewById(R.id.pupilName);
            testOrLesson = itemView.findViewById(R.id.testOrLesson);
            checkToCancle = itemView.findViewById(R.id.checkToRemove);
            schoolName=itemView.findViewById(R.id.schoolName);
        }
    }
    @NonNull
    @Override
    public adapters.teacherScheduledPupilAdapter.AppViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.teacher_scheduled_pupil_adapter, viewGroup, false);
        // set the view's size, margins, paddings and layout parameters
        adapters.teacherScheduledPupilAdapter.AppViewHolder vh = new adapters.teacherScheduledPupilAdapter.AppViewHolder(v);
        return vh;
    }
    @Override
    public void onBindViewHolder(@NonNull final adapters.teacherScheduledPupilAdapter.AppViewHolder appViewHolder, int position1) {
        boolean isCheckAll=teacherScheduledPupil.isCheckAll;
        final TimeScheduling subjectData = timesScheduledByPupil.get(position1);
        db.collection(Constants.drivingSchool).document(subjectData.getSchoolUId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                appViewHolder.schoolName.setText(task.getResult().get(Constants.schoolName).toString());
            }
        });
            Calendar cal=Calendar.getInstance();
            cal.setTime(subjectData.getStartTime());
            Integer day=cal.get(Calendar.DAY_OF_MONTH);
            Integer month=cal.get(Calendar.MONTH);
            Integer year=cal.get(Calendar.YEAR);
            appViewHolder.date.setText(String.format(Constants.datePrintingFormat,day,month,year));
            appViewHolder.startHour.setText(String.format(Constants.timePrintingFormat,cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE)));
            appViewHolder.pupileName.setText(subjectData.getPupilFullName());
            if(subjectData.isTest()){
                if(subjectData.isEnternalTest())
                    appViewHolder.testOrLesson.setText("מבחן פנימי");
                else
                    appViewHolder.testOrLesson.setText("מבחן חיצוני");
            }else{
                appViewHolder.testOrLesson.setText("שיעור");
            }
            if(!appViewHolder.checkToCancle.isChecked()) {
                teacherScheduledPupil.timesToRemove.add(subjectData);
                teacherScheduledPupil.timesToStay.remove(subjectData);
            }
            appViewHolder.checkToCancle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        teacherScheduledPupil.timesToStay.add(subjectData);
                        teacherScheduledPupil.timesToRemove.remove(subjectData);
                    }else{
                        teacherScheduledPupil.timesToStay.remove(subjectData);
                        teacherScheduledPupil.timesToRemove.add(subjectData);
                    }
                }
            });
            if(isCheckAll){
                appViewHolder.checkToCancle.setChecked(true);
            }else{
                appViewHolder.checkToCancle.setChecked(false);
            }
    }

    @Override
    public int getItemCount() {
        if(timesScheduledByPupil==null){
            return 0;
        }else
        {
            return timesScheduledByPupil.size();
        }
    }
}
