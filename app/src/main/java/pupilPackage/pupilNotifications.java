package pupilPackage;

import android.os.Bundle;

import adapters.pupilNotificationAdapter;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import databaseClasses.Constants;
import databaseClasses.notification;
import databaseClasses.sqlLitDbHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.mainPages.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;


public class pupilNotifications extends Fragment {
    private View rootView;
    private String schoolId;
    private String teacherId;
    private String pupilId;
    private RecyclerView newNotyRecycler;
    private RecyclerView oldNotyRecycler;
    private LinearLayoutManager mLayoutManager;
    private FirebaseFirestore db;
    private sqlLitDbHelper myLocalDb;
    private ArrayList<notification> sqlLiteNoties;
    private ArrayList<notification> newNotifications=new ArrayList<>();
    private ArrayList<notification> oldNotifications=new ArrayList<>();
    private LinearLayoutManager mLayoutManager1;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         rootView=inflater.inflate(R.layout.pupil_notifications, container, false);
        db=FirebaseFirestore.getInstance();
         newNotyRecycler =rootView.findViewById(R.id.newNotyRecycler);
        oldNotyRecycler =rootView.findViewById(R.id.oldNotyRecycler);
        sqlLiteNoties=new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(getContext());
        newNotyRecycler.setHasFixedSize(true);
        newNotyRecycler.setLayoutManager(mLayoutManager);
        mLayoutManager1 = new LinearLayoutManager(getContext());
        oldNotyRecycler.setHasFixedSize(true);
        oldNotyRecycler.setLayoutManager(mLayoutManager1);
        myLocalDb=new sqlLitDbHelper(getContext());
         schoolId=pupilPagesNavigator.schoolId;
         teacherId=pupilPagesNavigator.teacherId;
         pupilId = pupilPagesNavigator.pupilId;
         setnotyList();
         return rootView;
    }

    public void setnotyList(){
        if(sqlLitDbHelper.isConnected(getContext())) {
            db.collection(Constants.drivingSchool).document(schoolId).collection(Constants.teachers).document(teacherId).collection(Constants.pupils)
                    .document(pupilId).collection(Constants.notifications).orderBy(Constants.timeNoty, Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        sqlLiteNoties=myLocalDb.readNotyFromDb();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            notification noty = doc.toObject(notification.class);
                            if (Calendar.getInstance().get(Calendar.DATE) - noty.getTimeNoty().getDate() > 1) {
                                oldNotifications.add(noty);
                            } else {
                                newNotifications.add(noty);
                            }
                            if(!sqlLiteNoties.contains(noty))
                            {
                                myLocalDb.saveToNotyDb(noty);
                            }else{
                                if(!sqlLiteNoties.get(sqlLiteNoties.indexOf(noty)).isRead() && noty.isRead()){
                                    myLocalDb.updaateNoty(noty);
                                }
                            }
                        }
                        pupilNotificationAdapter newNotyAdapter = new pupilNotificationAdapter(getContext(), newNotifications);
                        newNotyRecycler.setAdapter(newNotyAdapter);

                        pupilNotificationAdapter oldNotyAdapter = new pupilNotificationAdapter(getContext(), oldNotifications);
                        oldNotyRecycler.setAdapter(oldNotyAdapter);
                    }
                }
            });
        }
        else
        {
            sqlLiteNoties=myLocalDb.readNotyFromDb();
            for(notification noty: sqlLiteNoties){
                if (Calendar.getInstance().get(Calendar.DATE) - noty.getTimeNoty().getDate() > 1) {
                    oldNotifications.add(noty);
                } else {
                    newNotifications.add(noty);
                }

            }
            pupilNotificationAdapter newNotyAdapter = new pupilNotificationAdapter(getContext(), newNotifications);
            newNotyRecycler.setAdapter(newNotyAdapter);

            pupilNotificationAdapter oldNotyAdapter = new pupilNotificationAdapter(getContext(), oldNotifications);
            oldNotyRecycler.setAdapter(oldNotyAdapter);

        }
    }

}
