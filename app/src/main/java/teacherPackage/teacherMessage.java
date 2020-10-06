package teacherPackage;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.user.mainPages.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import cn.pedant.SweetAlert.SweetAlertDialog;
import databaseClasses.Constants;
import databaseClasses.Pupil;

public class teacherMessage extends Fragment {
    private AppCompatCheckBox checkMsgToAll;
    private SearchView searchPupil;
    private EditText notyTitle,notycontent;
    private CircularProgressButton sendButton;
    private FirebaseFunctions mFunctions;
    private boolean isToall;
    private ImageButton backTohome;
    private ListView searchListView;
    private String pupilIdNoty;
    private final HashMap<String,String> pupilsUId=new HashMap<>();
    private AutoCompleteTextView editTextFilledExposedDropdown;
    private View rootView;
    private HashMap<String,String> schoolNameMapping;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.teacher_message, container, false);
        sendButton = rootView.findViewById(R.id.send);
        backTohome=rootView.findViewById(R.id.backTohome);
        checkMsgToAll=rootView.findViewById(R.id.checkMsgToAll);
        searchPupil=rootView.findViewById(R.id.searchPupil);
        notyTitle=rootView.findViewById(R.id.notyTitle);
        notycontent=rootView.findViewById(R.id.notyContent);
        searchListView=rootView.findViewById(R.id.searchList);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNot();
            }
        });
        mFunctions = FirebaseFunctions.getInstance();
        isToall=false;
        checkMsgToAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    isToall=true;
                    searchPupil.setVisibility(View.GONE);
                }else{
                    isToall=false;
                    searchPupil.setVisibility(View.VISIBLE);
                }
            }
        });
        backTohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new teacherHomePage(),Constants.tagTeacherHome).commit();
            }
        });
        setSearchViewList();
        setSchoolDropDown();
        return rootView;
    }
    private void setSchoolDropDown() {
        editTextFilledExposedDropdown =
                rootView.findViewById(R.id.filled_exposed_dropdown);
        editTextFilledExposedDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editTextFilledExposedDropdown.setText(editTextFilledExposedDropdown.getAdapter().getItem(position).toString());
                teacherPagesNavigator.schoolId = schoolNameMapping.get(editTextFilledExposedDropdown.getAdapter().getItem(position).toString());
                searchPupil.setQuery("",false);
                setSearchViewList();
                Fragment currentFragment = getFragmentManager().findFragmentByTag(Constants.tagTeacherMessage);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.detach(currentFragment);
                fragmentTransaction.attach(currentFragment);
                fragmentTransaction.commit();

            }
        });
        FirebaseFirestore.getInstance().collection(Constants.UIDS).document(teacherPagesNavigator.teacherId).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    private int counter;

                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult().exists()) {
                            final ArrayList<String> schoolNames=new ArrayList<>();
                            final ArrayList<String> schoolUIds=(ArrayList<String>) task.getResult().get(Constants.schoolsUIDs);
                            counter=schoolUIds.size();
                            schoolNameMapping=new HashMap<>();
                            for(final String schoolId:schoolUIds) {
                                FirebaseFirestore.getInstance().collection(Constants.drivingSchool).document(schoolId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful() && task.getResult().exists()){
                                            schoolNames.add(task.getResult().get(Constants.schoolName).toString());
                                            schoolNameMapping.put(task.getResult().get(Constants.schoolName).toString(),schoolId);
                                            counter--;
                                            if(counter==0){
                                                ArrayAdapter<String> adapter =
                                                        new ArrayAdapter<String>(
                                                                getContext(),
                                                                R.layout.drop_down_layout,
                                                                schoolNames);
                                                editTextFilledExposedDropdown.setAdapter(adapter);
                                            }
                                        }
                                    }
                                });
                            }

                        }
                    }

                });




    }


    public void sendNot(){
        String title= notyTitle.getText().toString();
        String content= notycontent.getText().toString();
        sendButton.startAnimation();
        if(title.isEmpty() || content.isEmpty()){
            sendButton.revertAnimation();
            new SweetAlertDialog(getContext())
                    .setContentText("מלא התוכן הריק")
                    .setConfirmText("OK")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                        }
                    })
                    .show();
        }else {
            addMessage(content,title,teacherPagesNavigator.teacherId,teacherPagesNavigator.schoolId)
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Exception e = task.getException();
                                if (e instanceof FirebaseFunctionsException) {
                                    FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                    FirebaseFunctionsException.Code code = ffe.getCode();
                                    Object details = ffe.getDetails();
                                    sendButton.revertAnimation();
                                    new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                            .setContentText(details.toString())
                                            .show();
                                }

                                // ...
                            }else
                            {
                                sendButton.revertAnimation();
                                new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Good job!")
                                        .setContentText("התראה נשלחת בהצלחה")
                                        .show();
                            }

                            // ...
                        }
                    });

        }
    }
    private void setSearchViewList() {
        FirebaseFirestore.getInstance().collection(Constants.drivingSchool).document(teacherPagesNavigator.schoolId).collection(Constants.teachers).document(teacherPagesNavigator.teacherId)
                .collection(Constants.pupils).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful() && !task.getResult().isEmpty()){
                    final ArrayList<String> pupilsList=new ArrayList<>();
                    for(QueryDocumentSnapshot doc:task.getResult()){
                        Pupil pupil=doc.toObject(Pupil.class);
                        pupilsList.add(pupil.getPupilName()+"  "+(pupil.getPupilPhone()==null?"":pupil.getPupilPhone()));
                        pupilsUId.put(pupil.getPupilName()+"  "+(pupil.getPupilPhone()==null?"":pupil.getPupilPhone()),doc.getId());
                    }
                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(teacherPagesNavigator.context, android.R.layout.simple_list_item_1, pupilsList);
                    searchListView.setAdapter(adapter);
                    searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String pupil=searchListView.getItemAtPosition(position).toString();
                            searchPupil.setQuery(pupil, true);
                        }
                    });
                    searchPupil.setOnQueryTextListener(
                            new SearchView.OnQueryTextListener(){

                                @Override
                                public boolean onQueryTextSubmit(String query) {
                                    if (pupilsList.contains(query)) {
                                        adapter.getFilter().filter(query);
                                        pupilIdNoty=pupilsUId.get(query);
                                        checkMsgToAll.setVisibility(View.VISIBLE);
                                        notyTitle.setVisibility(View.VISIBLE);
                                        notycontent.setVisibility(View.VISIBLE);
                                        searchListView.setVisibility(View.GONE);
                                    }
                                    else {
                                        Toast.makeText(getContext(), "Not found", Toast.LENGTH_LONG).show();
                                        checkMsgToAll.setVisibility(View.VISIBLE);
                                        notyTitle.setVisibility(View.VISIBLE);
                                        notycontent.setVisibility(View.VISIBLE);
                                        searchListView.setVisibility(View.GONE);
                                    }
                                    return false;
                                }

                                @Override
                                public boolean onQueryTextChange(String newText) {
                                    if(searchPupil.getQuery().toString().isEmpty()){
                                        checkMsgToAll.setVisibility(View.VISIBLE);
                                        notyTitle.setVisibility(View.VISIBLE);
                                        notycontent.setVisibility(View.VISIBLE);
                                        searchListView.setVisibility(View.GONE);
                                    }else{
                                        searchListView.setVisibility(View.VISIBLE);
                                        checkMsgToAll.setVisibility(View.GONE);
                                        notyTitle.setVisibility(View.GONE);
                                        notycontent.setVisibility(View.GONE);
                                    }
                                    adapter.getFilter().filter(newText);
                                    return false;
                                }
                            });
                }
            }
        });
    }
    private Task<String> addMessage(String content,String title,String teacherUId,String schoolId) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("content",content);
        data.put("teacherUId",teacherUId);
        data.put("schoolId",schoolId);
        if(!isToall)
        {
            data.put("pupilId",pupilsUId.get(searchPupil.getQuery().toString()));
        }else{
            data.put("pupilId","");
        }
        return mFunctions
                .getHttpsCallable("addMessageToMyPupils")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
    }

}