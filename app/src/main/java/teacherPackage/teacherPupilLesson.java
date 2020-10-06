package teacherPackage;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import pupilPackage.pupilLesson;

public class teacherPupilLesson extends AppCompatActivity {

    private Context context;
    public void onCreate(Context context,String pupilId){
        this.context=context;
        pupilLesson pupilLessonpage=new pupilLesson();
        pupilLessonpage.onCreate(this.context,teacherPagesNavigator.schoolId,teacherPagesNavigator.teacherId,pupilId);
    }
}
