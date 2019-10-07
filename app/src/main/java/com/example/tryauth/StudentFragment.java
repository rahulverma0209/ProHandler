package com.example.tryauth;

//Some bug in loading new Fragment

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.*;
import android.widget.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.*;


/**
 * A simple {@link Fragment} subclass.
 */
public class StudentFragment extends Fragment {


    private FirebaseUser currentUser;
    private DatabaseReference firebaseDatabase,firebaseDatabase2;

    private Spinner teacherSpinner;
    private TextView studentFrgmentDiscription;
    private Button selectReviewerButton;
    private View mView;
    ArrayAdapter<String> dataAdapter;
    public static String uid;
    StudentChatFragment studentChatFragment;
    public String utid;

    public StudentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_student, container, false);


        teacherSpinner = mView.findViewById(R.id.teacherSpinner);
        teacherSpinner.setVisibility(View.INVISIBLE);
        studentFrgmentDiscription = mView.findViewById(R.id.studentFrgmentDiscription);
        selectReviewerButton = mView.findViewById(R.id.selectReviewerButton);
        selectReviewerButton.setVisibility(View.INVISIBLE);

        //Creating Adapter for spinner
        List<String> categories = new ArrayList<String>();
        categories.add("Select");
         dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teacherSpinner.setAdapter(dataAdapter);
        dataAdapter.notifyDataSetChanged();


        //Checking is user is having Reviewer or not
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();

        //user teacher id
        utid = "null";

        FirebaseDatabase.getInstance().getReference().child("group").child("student").child(uid)
                .addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{
                    String name = dataSnapshot.child("tid").getValue().toString();
                    Toast.makeText(getContext(),name,Toast.LENGTH_LONG).show();
                    studentChatFragment = new StudentChatFragment();
                    loadStudentChatFragment(studentChatFragment);
                }
                catch (NullPointerException exception){
                    loadListOfTeachers();
                    teacherSpinner.setVisibility(View.VISIBLE);
                    selectReviewerButton.setVisibility(View.VISIBLE);
                    studentFrgmentDiscription.setText("You have not selected your reviewer.\n\nSelect your reviewer which is assigned by college.");
                    Toast.makeText(getContext(),"reviewer not selected",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return mView;
    }

    void loadListOfTeachers(){

        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        List<String> tname = new ArrayList();
                        final List<String> tid = new ArrayList();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            UserData user = snapshot.getValue(UserData.class);
                            if(user.getUser_type().equals("teacher")){
                                tname.add(user.getName());
                                tid.add(snapshot.getKey());
                            }
                        }
                        teacherSpinner = mView.findViewById(R.id.teacherSpinner);
                        dataAdapter.addAll(tname);
                        dataAdapter.notifyDataSetChanged();

                        teacherSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                String item = parent.getItemAtPosition(position).toString();
                                if(position==0){
                                    utid = "null";
                                    Toast.makeText(parent.getContext(), "Select your reviewer " , Toast.LENGTH_LONG).show();
                                }
                                else
                                {
                                    utid = tid.get(position-1);
                                    Toast.makeText(parent.getContext(), "Selected: " + item + "\n"+ tid.get(position-1) , Toast.LENGTH_LONG).show();
                                }

                                selectReviewerButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        saveReviewerData();
                                        //Toast.makeText(getContext(), uid + "\n" + utid, Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });


                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    void saveReviewerData(){

        //Toast.makeText(getContext(), uid + "\n" + utid, Toast.LENGTH_LONG).show();

        firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("group").child("student").child(uid);
        HashMap<String,String> data = new HashMap<>();
        data.put("tid",utid);

        firebaseDatabase.setValue(data);


        Long temp = Long.parseLong("1570438972791");
        Long key = System.currentTimeMillis() - temp;
        firebaseDatabase2 = FirebaseDatabase.getInstance().getReference().child("group").child("teacher").child(utid).child(key.toString());
        HashMap<String,String> tdata = new HashMap<>();
        uid = currentUser.getUid();

        tdata.put("uid",uid);

        firebaseDatabase2.setValue(tdata).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //loadStudentChatFragment();
                Toast.makeText(getContext(), "Added", Toast.LENGTH_LONG).show();
            }
        });
    }

    void loadStudentChatFragment(Fragment studentChatFragment){
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_page_frame,studentChatFragment);
        fragmentTransaction.commit();
    }
}
