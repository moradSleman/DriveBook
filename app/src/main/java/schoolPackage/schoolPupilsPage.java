package schoolPackage;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.mainPages.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import databaseClasses.Constants;
import databaseClasses.Pupil;
import databaseClasses.Teacher;
import databaseClasses.Test;

import static databaseClasses.Constants.tagTeacherHome;

public class schoolPupilsPage extends Fragment {
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private TextView pupilName,teacherName,lessonsNum,testsNum;
    private ImageView pupilImage;
    private String pupilId,teacherId,pupilPhone;
    private ListView pupilListView;
    private SearchView searchPupils;
    private View rootView;
    private ImageButton whatup,call,sms;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.school_pupil_page, container, false);

        db= FirebaseFirestore.getInstance();

        pupilName=rootView.findViewById(R.id.pupilName);
        teacherName=rootView.findViewById(R.id.teacherName);
        testsNum=rootView.findViewById(R.id.testsNum);
        lessonsNum=rootView.findViewById(R.id.lessonsNum);
        pupilImage=rootView.findViewById(R.id.pupilImage);
        whatup=rootView.findViewById(R.id.whatUp);
        call=rootView.findViewById(R.id.call);
        sms=rootView.findViewById(R.id.sms);

        pupilListView=rootView.findViewById(R.id.searchListPupils);
        searchPupils=rootView.findViewById(R.id.searchPupil);

        setSearchView();
        onClickSms();
        setCallingListener();
        onClickWhatsApp();
        return rootView;
    }

    private void setSearchView() {
        db.collection(Constants.drivingSchool).document(schoolPagesNavigator.schoolUId).collection(Constants.teachers)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                final HashMap<String,String> pupilUIDs=new HashMap<>();
                final HashMap<String,String> teacherIds=new HashMap<>();
                if(task.isSuccessful() && !task.getResult().isEmpty()){
                    final ArrayList<String> pupilsList=new ArrayList<>();
                    for(final QueryDocumentSnapshot doc:task.getResult()) {
                        doc.getReference().collection(Constants.pupils).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                    final Teacher teacher = doc.toObject(Teacher.class);
                                    for (QueryDocumentSnapshot doc1 : task.getResult()) {
                                        Pupil pupil = doc1.toObject(Pupil.class);
                                        pupilsList.add(pupil.getPupilName().toString() + "  " + (pupil.getPupilPhone() == null ? pupil.getPupilMail().toString() : pupil.getPupilPhone().toString()));
                                        pupilUIDs.put(pupil.getPupilName().toString() + "  " + (pupil.getPupilPhone() == null ? pupil.getPupilMail().toString() : pupil.getPupilPhone().toString())
                                                , doc1.getId());
                                        teacherIds.put(pupil.getPupilName().toString() + "  " + (pupil.getPupilPhone() == null ? pupil.getPupilMail().toString() : pupil.getPupilPhone().toString())
                                                , doc.getId());

                                    }
                                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, pupilsList);
                                    pupilListView.setAdapter(adapter);
                                    pupilListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @RequiresApi(api = Build.VERSION_CODES.O)
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            String pupil=pupilListView.getItemAtPosition(position).toString();
                                            searchPupils.setQuery(pupil, true);
                                        }
                                    });
                                    searchPupils.setOnQueryTextListener(
                                            new SearchView.OnQueryTextListener(){

                                                @Override
                                                public boolean onQueryTextSubmit(String query) {
                                                    if (pupilsList.contains(query)) {
                                                        adapter.getFilter().filter(query);
                                                        pupilId=pupilUIDs.get(query.toString());
                                                        teacherId=teacherIds.get(query.toString());
                                                        setData();

                                                    }
                                                    else {
                                                        Toast.makeText(getContext(), "Not found", Toast.LENGTH_LONG).show();
                                                    }
                                                    pupilListView.setVisibility(View.GONE);
                                                    rootView.findViewById(R.id.pupilCard).setVisibility(View.VISIBLE);

                                                    return false;
                                                }

                                                @Override
                                                public boolean onQueryTextChange(String newText) {
                                                    if(searchPupils.getQuery().toString().isEmpty()){
                                                        rootView.findViewById(R.id.pupilCard).setVisibility(View.VISIBLE);
                                                        pupilListView.setVisibility(View.GONE);
                                                    }else{
                                                        rootView.findViewById(R.id.pupilCard).setVisibility(View.GONE);
                                                        pupilListView.setVisibility(View.VISIBLE);
                                                    }
                                                    adapter.getFilter().filter(newText);
                                                    return false;
                                                }
                                            });

                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void setData() {
        FirebaseStorage.getInstance().getReferenceFromUrl(Constants.pupilsStoragePath + pupilId + "/"+Constants.faceImage+"/")
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                StorageReference mImageRef =
                        FirebaseStorage.getInstance().getReferenceFromUrl(Constants.teacherStoragePath + pupilId + "/"+Constants.faceImage+"/");
                final long ONE_MEGABYTE = 1024 * 1024;
                mImageRef.getBytes(ONE_MEGABYTE)
                        .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                pupilImage.setImageBitmap(bm);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(tagTeacherHome ,"onFailure: "+e);
            }
        });
        db.collection(Constants.drivingSchool).document(schoolPagesNavigator.schoolUId).collection(Constants.teachers).document(teacherId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful() && task.getResult().exists()){
                    teacherName.setText(task.getResult().get(Constants.teacherName).toString());
                }
            }
        });
        db.collection(Constants.drivingSchool).document(schoolPagesNavigator.schoolUId).collection(Constants.teachers).document(teacherId).collection(
                Constants.pupils).document(pupilId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful() && task.getResult().exists()){
                    pupilName.setText(task.getResult().get(Constants.pupilName).toString());
                    pupilPhone=task.getResult().get(Constants.pupilPhone)==null?"":task.getResult().get(Constants.pupilPhone).toString();
                }
                db.collection(Constants.drivingSchool).document(schoolPagesNavigator.schoolUId).collection(Constants.teachers).document(teacherId).collection(
                        Constants.pupils).document(pupilId).collection(Constants.lessons).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        lessonsNum.setText(((Integer)task.getResult().size()).toString());
                    }
                });
                db.collection(Constants.drivingSchool).document(schoolPagesNavigator.schoolUId).collection(Constants.teachers).document(teacherId).collection(
                        Constants.pupils).document(pupilId).collection(Constants.tests).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && !task.getResult().isEmpty()){
                            int countTestExternal=0;
                            for(QueryDocumentSnapshot doc: task.getResult()){
                                Test test=doc.toObject(Test.class);
                                if(!test.isInternalTest()){
                                    countTestExternal++;
                                }
                            }
                            testsNum.setText(((Integer)countTestExternal).toString());
                        }
                    }
                });
            }
        });
    }

    private void onClickSms() {
        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"
                        + pupilPhone)));
            }
        });
    }

    private void setCallingListener() {
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pupilPhone!="") {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+pupilPhone));
                    startActivity(intent);
                }
            }
        });
    }
    public void onClickWhatsApp() {
        whatup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm=getActivity().getPackageManager();
                try {
                    String text = "";// Replace with your message.

                    String toNumber = "972"+pupilPhone; // Replace with mobile phone number without +Sign or leading zeros, but with country code
                    //Suppose your country is India and your phone number is “xxxxxxxxxx”, then you need to send “91xxxxxxxxxx”.


                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+toNumber +"&text="+text));
                    startActivity(intent);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

}