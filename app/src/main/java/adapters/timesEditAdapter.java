package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.user.mainPages.R;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import databaseClasses.Constants;
import databaseClasses.TimeScheduling;
import teacherPackage.teacherScheduling;

public class timesEditAdapter extends RecyclerView.Adapter<timesEditAdapter.AppViewHolder> {
    private ArrayList<TimeScheduling> times;
    private Context context;

    public timesEditAdapter(Context context, ArrayList<TimeScheduling> times) {
        this.context = context;
        this.times = times;
    }

    public static class AppViewHolder extends RecyclerView.ViewHolder {
        TextView startTime;
        TextView endTime;
        TextView removeTime;
        CardView card;
        public AppViewHolder(View itemView) {
            super(itemView);
            startTime = (TextView) itemView.findViewById(R.id.startTime);
            endTime = (TextView) itemView.findViewById(R.id.endTime);
            removeTime = (TextView) itemView.findViewById(R.id.cancleLessonTime);
            card = (CardView) itemView.findViewById(R.id.row);

        }
    }

    @NonNull
    @Override
    public timesEditAdapter.AppViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.teacher_schedule_time_row_layout, viewGroup, false);
        // set the view's size, margins, paddings and layout parameters
        timesEditAdapter.AppViewHolder vh = new timesEditAdapter.AppViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final timesEditAdapter.AppViewHolder appViewHolder, final int position1) {
        final TimeScheduling noty=times.get(position1);
        Calendar startTime=Calendar.getInstance();
        startTime.setTime(times.get(position1).getStartTime());
        Calendar endTime=Calendar.getInstance();
        endTime.setTime(times.get(position1).getEndTime());

        appViewHolder.startTime.setText(String.format(Constants.timePrintingFormat,startTime.get(Calendar.HOUR_OF_DAY),endTime.get(Calendar.MINUTE)));
        appViewHolder.endTime.setText(String.format(Constants.timePrintingFormat,endTime.get(Calendar.HOUR_OF_DAY),endTime.get(Calendar.MINUTE)));

        appViewHolder.removeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(teacherScheduling.isSameHoursDays){
                    ArrayList<TimeScheduling> timesToRemove=new ArrayList<>();
                    for(Integer day : teacherScheduling.timesToDays.keySet()){

                        for(TimeScheduling time : teacherScheduling.timesToDays.get(day).values()){
                            if(time.getStartTime().equals(times.get(position1).getStartTime())){
                                timesToRemove.add(time);
                                appViewHolder.card.setVisibility(View.GONE);
                            }
                        }
                    }
                    for(Integer day : teacherScheduling.timesToDays.keySet()){

                        teacherScheduling.timesToDays.get(day).values().removeAll(timesToRemove);
                    }
                }else{
                    teacherScheduling.timesToDays.get(teacherScheduling.chosenDayFromLookUpDialog).values().remove(times.get(position1));
                    appViewHolder.card.setVisibility(View.GONE);
                }
            }
        });

    }
    @Override
    public int getItemCount() {
        if(times==null){
            return 0;
        }else
        {
            return times.size();
        }
    }

}
