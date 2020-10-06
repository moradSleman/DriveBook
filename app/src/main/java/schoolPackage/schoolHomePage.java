package schoolPackage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import databaseClasses.Constants;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.user.mainPages.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class schoolHomePage extends Fragment {

    private TextView teachersNum,pupilNum,schoolName;
    private ImageButton pupils,teachers,statistics;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.school_home_page, container, false);
        teachersNum=rootView.findViewById(R.id.teacherNum);
        pupilNum=rootView.findViewById(R.id.pupilNum);
        schoolName=rootView.findViewById(R.id.schoolName);
        statistics=rootView.findViewById(R.id.statistics);
        teachers=rootView.findViewById(R.id.teachers);
        pupils=rootView.findViewById(R.id.pupils);

        db=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        setData();
        setImageListener();
        return rootView;
    }

    private void setImageListener() {
        pupils.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                schoolPupilsPage myFragment1 =new schoolPupilsPage();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        ,myFragment1,Constants.tagSchoolPupilsPage).commit();
            }
        });
        teachers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                schoolTeachersPage myFragment1 =new schoolTeachersPage();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        ,myFragment1,Constants.tagSchoolTeacherPage).commit();
            }
        });
        statistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                schoolStatisticsPage myFragment1 =new schoolStatisticsPage();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        ,myFragment1,Constants.tagSchoolStatisticsPage).commit();
            }
        });
    }

    private void setData() {
        schoolName.setText(schoolPagesNavigator.schoolName);
        db.collection(Constants.drivingSchool).document(auth.getCurrentUser().getUid().toString()).collection(Constants.teachers).get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    private int counter;
                    private int counterPupils;

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && !task.getResult().isEmpty()){
                            counterPupils=0;
                            counter=task.getResult().size();
                            teachersNum.setText(((Integer)task.getResult().size()).toString());
                            for(QueryDocumentSnapshot doc : task.getResult()){
                                doc.getReference().collection(Constants.pupils).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful() && !task.getResult().isEmpty()){
                                            counterPupils+=task.getResult().size();
                                            counter--;
                                            if(counter==0){
                                                pupilNum.setText(((Integer)counterPupils).toString());
                                            }
                                        }else{
                                            counter--;
                                            if(counter==0){
                                                pupilNum.setText(((Integer)counterPupils).toString());
                                            }
                                        }
                                    }
                                });
                            }
                        }

                    }
                }
        );

    }


}
