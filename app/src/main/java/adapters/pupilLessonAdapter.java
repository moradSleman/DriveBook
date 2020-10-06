package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.user.mainPages.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import databaseClasses.Constants;
import databaseClasses.lessonPayment;

public class pupilLessonAdapter extends RecyclerView.Adapter<adapters.pupilLessonAdapter.AppViewHolder>{
        List<lessonPayment> lessons;
        private Context context;
        public pupilLessonAdapter(Context context, ArrayList<lessonPayment> timesToRecycle) {
            this.lessons=timesToRecycle;
            this.context=context;
        }

        public static class AppViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            CardView mCardView;
            TextView lessonDate;
            TextView lessontime;
            TextView lessonNumber;
            TextView intervalLesson;
            public AppViewHolder(View itemView) {
                super(itemView);
                mCardView = (CardView) itemView.findViewById(R.id.row);
                lessonDate = (TextView) itemView.findViewById(R.id.lessonDate);
                lessontime = (TextView) itemView.findViewById(R.id.lessonTime);
                lessonNumber = (TextView) itemView.findViewById(R.id.lessonNumber);
                intervalLesson = (TextView) itemView.findViewById(R.id.lessonInterval);
            }
        }
        @NonNull
        @Override
        public adapters.pupilLessonAdapter.AppViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pupil_lesson_row_adapter, viewGroup, false);
            // set the view's size, margins, paddings and layout parameters
            adapters.pupilLessonAdapter.AppViewHolder vh = new adapters.pupilLessonAdapter.AppViewHolder(v);
            return vh;
        }
        @Override
        public void onBindViewHolder(@NonNull final adapters.pupilLessonAdapter.AppViewHolder appViewHolder, int position1) {
            final int position=position1;
            lessonPayment currentLesson=lessons.get(position);
            Calendar startCal=Calendar.getInstance();
            startCal.setTime(currentLesson.getLessonStartingDate());
            Integer startHour=startCal.get(Calendar.HOUR_OF_DAY);
            Integer startMin=startCal.get(Calendar.MINUTE);

            Long intervaltime=currentLesson.getLessonEndingDate().getTime()/60000-currentLesson.getLessonStartingDate().getTime()/60000;
            Integer intIntervalTime=Integer.parseInt(intervaltime.toString());
            appViewHolder.lessontime.setText(String.format(Constants.timePrintingFormat,startHour,startMin));
            appViewHolder.intervalLesson.setText(intIntervalTime.toString()+"דק'");
            SimpleDateFormat simple=new SimpleDateFormat("dd-MM-yyyy");
            appViewHolder.lessonDate.setText(simple.format(currentLesson.getLessonStartingDate()));
            appViewHolder.lessonNumber.setText(((Integer)(lessons.size()-(position))).toString());
        }

        @Override
        public int getItemCount() {
            if(lessons==null){
                return 0;
            }else
            {
                return lessons.size();
            }
        }
    }
