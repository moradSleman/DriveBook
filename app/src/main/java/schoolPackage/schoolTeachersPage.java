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
import android.widget.Button;
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
import databaseClasses.Teacher;

import static databaseClasses.Constants.tagTeacherHome;

public class schoolTeachersPage extends Fragment {
    private View rootView;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private TextView teacherName,pupilsNum;
    private ImageView teacherImage;
    private ImageButton addTeacher;
    private String teacherPhone,teacherId;
    private Button delete;
    private SearchView searchTeacher;
    private ListView listTeachers;
    private ImageButton sms;
    private ImageButton whatup;
    private ImageButton call;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.school_teacher_page, container, false);

        db=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        teacherName=rootView.findViewById(R.id.teacherName);
        pupilsNum=rootView.findViewById(R.id.pupilsNum);
        delete=rootView.findViewById(R.id.deleteTeacher);
        teacherImage=rootView.findViewById(R.id.teacherImage);
        searchTeacher=rootView.findViewById(R.id.searchTeacher);
        listTeachers=rootView.findViewById(R.id.searchListTeachers);
        addTeacher=rootView.findViewById(R.id.addTeacher);
        sms=rootView.findViewById(R.id.sms);
        whatup=rootView.findViewById(R.id.whatUp);
        call=rootView.findViewById(R.id.call);

        setSearchListTeacher();
        onClickSms();
        setCallingListener();
        onClickWhatsApp();
        return rootView;
    }

    private void setSearchListTeacher() {
        db.collection(Constants.drivingSchool).document(schoolPagesNavigator.schoolUId).collection(Constants.teachers)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                final HashMap<String,String> teacherUIds=new HashMap<>();
                if(task.isSuccessful() && !task.getResult().isEmpty()){
                    final ArrayList<String> teachersList=new ArrayList<>();
                    for(QueryDocumentSnapshot doc:task.getResult()){
                        Teacher teacher=doc.toObject(Teacher.class);
                        teachersList.add(teacher.getTeacherName().toString()+"  "+(teacher.getTeacherPhone()==null? teacher.getTeacherMail().toString(): teacher.getTeacherPhone().toString()));
                        teacherUIds.put(teacher.getTeacherName().toString()+"  "+(teacher.getTeacherPhone()==null?teacher.getTeacherMail().toString(): teacher.getTeacherPhone().toString())
                                ,doc.getId());
                    }
                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, teachersList);
                    listTeachers.setAdapter(adapter);
                    listTeachers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String teacher=listTeachers.getItemAtPosition(position).toString();
                            searchTeacher.setQuery(teacher, true);
                        }
                    });
                    searchTeacher.setOnQueryTextListener(
                            new SearchView.OnQueryTextListener(){

                                @Override
                                public boolean onQueryTextSubmit(String query) {
                                    if (teachersList.contains(query)) {
                                        adapter.getFilter().filter(query);
                                        teacherId=teacherUIds.get(query.toString());
                                        setData();

                                    }
                                    else {
                                        Toast.makeText(getContext(), "Not found", Toast.LENGTH_LONG).show();
                                    }
                                    listTeachers.setVisibility(View.GONE);
                                    rootView.findViewById(R.id.cardTeacher).setVisibility(View.VISIBLE);

                                    return false;
                                }

                                @Override
                                public boolean onQueryTextChange(String newText) {
                                    if(searchTeacher.getQuery().toString().isEmpty()){
                                        rootView.findViewById(R.id.cardTeacher).setVisibility(View.VISIBLE);
                                        listTeachers.setVisibility(View.GONE);
                                    }else{
                                        rootView.findViewById(R.id.cardTeacher).setVisibility(View.GONE);
                                        listTeachers.setVisibility(View.VISIBLE);
                                    }
                                    adapter.getFilter().filter(newText);
                                    return false;
                                }
                            });
                }
            }
        });
    }

    private void setData() {
        db.collection(Constants.drivingSchool).document(schoolPagesNavigator.schoolUId).collection(Constants.teachers).document(teacherId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful() && task.getResult().exists()){
                            Teacher teacher=task.getResult().toObject(Teacher.class);
                            teacherName.setText(teacher.getTeacherName());
                            teacherPhone=teacher.getTeacherPhone()==null?"":teacher.getTeacherPhone();
                            task.getResult().getReference().collection(Constants.pupils).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    pupilsNum.setText(((Integer)task.getResult().size()).toString());
                                }
                            });
                        }
                    }
                });
        FirebaseStorage.getInstance().getReferenceFromUrl(Constants.teacherStoragePath + teacherId + "/"+Constants.faceImage+"/")
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                StorageReference mImageRef =
                        FirebaseStorage.getInstance().getReferenceFromUrl(Constants.teacherStoragePath + teacherId + "/"+Constants.faceImage+"/");
                final long ONE_MEGABYTE = 1024 * 1024;
                mImageRef.getBytes(ONE_MEGABYTE)
                        .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                teacherImage.setImageBitmap(bm);
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
    }

    private void onClickSms() {
        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"
                        + teacherPhone)));
            }
        });
    }

    private void setCallingListener() {
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(teacherPhone!="") {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+teacherPhone));
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

                    String toNumber = "972"+teacherPhone; // Replace with mobile phone number without +Sign or leading zeros, but with country code
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
