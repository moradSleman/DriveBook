package teacherPackage;

import android.os.Bundle;
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

import adapters.teacherNotificationAdapter;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import databaseClasses.Constants;
import databaseClasses.notification;
import databaseClasses.sqlLitDbHelper;

public class teacherNotifications extends Fragment {
    private View rootView;
    private String schoolId;
    private String teacherId;
    private String pupilId;
    private RecyclerView newNotyRecycler;
    private RecyclerView oldNotyRecycler;
    private LinearLayoutManager mLayoutManager;
    private FirebaseFirestore db;
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
        mLayoutManager = new LinearLayoutManager(getContext());
        newNotyRecycler.setHasFixedSize(true);
        newNotyRecycler.setLayoutManager(mLayoutManager);
        mLayoutManager1 = new LinearLayoutManager(getContext());
        oldNotyRecycler.setHasFixedSize(true);
        oldNotyRecycler.setLayoutManager(mLayoutManager1);
        schoolId= teacherPagesNavigator.schoolId;
        teacherId=teacherPagesNavigator.teacherId;
        setnotyList();
        return rootView;
    }

    public void setnotyList() {
        if (sqlLitDbHelper.isConnected(getContext())) {
            db.collection(Constants.UIDS).document(teacherId).collection(Constants.notifications).orderBy(Constants.timeNoty, Query.Direction.DESCENDING).
                    get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            notification noty = doc.toObject(notification.class);
                            if (Calendar.getInstance().get(Calendar.DATE) - noty.getTimeNoty().getDate() > 1) {
                                oldNotifications.add(noty);
                            } else {
                                newNotifications.add(noty);
                            }
                        }
                        teacherNotificationAdapter newNotyAdapter = new teacherNotificationAdapter(getContext(), newNotifications);
                        newNotyRecycler.setAdapter(newNotyAdapter);

                        teacherNotificationAdapter oldNotyAdapter = new teacherNotificationAdapter(getContext(), oldNotifications);
                        oldNotyRecycler.setAdapter(oldNotyAdapter);
                    }
                }
            });
        }
    }

}
