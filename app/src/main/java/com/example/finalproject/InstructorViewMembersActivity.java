package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class InstructorViewMembersActivity extends AppCompatActivity {

    DB_Management myDB;
    Button backButton;
    Spinner classSpinner;
    String username;
    List<String> usersByClass;

    MyRecyclerViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_view_members);

        myDB = new DB_Management(this);

        backButton = findViewById(R.id.backButton100);
        classSpinner = findViewById(R.id.teachingClassSpinner);

        usersByClass = new ArrayList<>();
        usersByClass.add(" test ");

        username = LoginActivity.getUser();

        loadClassSpinnerData();

        // temp
//        boolean y;
//        y = myDB.enrollInClass("123", "3");
//        y = myDB.enrollInClass("my", "3");
//        y = myDB.enrollInClass("name", "3");
//        y=myDB.enrollInClass("is", "3");
//        y=myDB.enrollInClass("what", "3");
//
//        y = myDB.enrollInClass("boo", "7");
//        y = myDB.enrollInClass("ya", "7");
//        y = myDB.enrollInClass("bim", "7");
//        y=myDB.enrollInClass("bam", "7");
//        y=myDB.enrollInClass("pow", "7");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.classRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, usersByClass);
        recyclerView.setAdapter(adapter);

        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                Object item = parentView.getItemAtPosition(position);
                usersByClass = getUsers(classSpinner.getSelectedItem().toString().split(" ")[1]);
//              adapter.notifyDataSetChanged();
                RecyclerView recyclerView = findViewById(R.id.classRecycler);
                recyclerView.setLayoutManager(new LinearLayoutManager(InstructorViewMembersActivity.this));
                adapter = new MyRecyclerViewAdapter(InstructorViewMembersActivity.this, usersByClass);
                recyclerView.setAdapter(adapter);
                classSpinner.setSelected(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

    }

    /**
     * gets all users from a class id
     * @param selectedClassId ID of the class selected by the user
     * @return list of strings of the usernames of the users that
     * are enrolled in the class represented by the class ID
     * passed on.
     */
    private List<String> getUsers(String selectedClassId) {
//        String selectedClassId = classSpinner.getSelectedItem().toString().split(" ")[1];

        List<String> allUsersInClass = myDB.getAllUsersByClassId(selectedClassId);
        allUsersInClass.remove(username);
        return allUsersInClass;
    }

    /**
     * initializes the recyclerViewAdapter
     *
     */
    public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

        private List<String> mData;
        private LayoutInflater mInflater;

        // data is passed into the constructor
        MyRecyclerViewAdapter(Context context, List<String> data) {
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
        }

        // inflates the row layout from xml when needed
        @Override
        public MyRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.class_recycler_view_row, parent, false);
            return new MyRecyclerViewAdapter.ViewHolder(view);
        }

        // binds the data to the TextView in each row
        @Override
        public void onBindViewHolder(MyRecyclerViewAdapter.ViewHolder holder, int position) {
            String s = mData.get(position);
            holder.myTextView.setText(s);
        }

        // total number of rows
        @Override
        public int getItemCount() {
            return mData.size();
        }

        // stores and recycles views as they are scrolled off screen
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView myTextView;

            ViewHolder(View itemView) {
                super(itemView);
                myTextView = itemView.findViewById(R.id.classType);

            }
        }
    }

    /**
     * loads spinner data from the SQL database
     */
    private void loadClassSpinnerData() {
        myDB = new DB_Management(this );
        List<String> tempLabels = myDB.getAllClassesByInstructorName(username);
        List<String> labels = new ArrayList<>();

        for (String s : tempLabels) {
            String tempS;
            String[] t = s.split(" ");
            tempS = "id: "+t[0]+" , "+
                    t[1]+" "+
                    t[2]+" , cap. "+
                    t[5];
            labels.add(tempS);
        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, labels);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        classSpinner.setAdapter(dataAdapter);
    }
}