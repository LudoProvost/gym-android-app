package com.example.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class MemberSearchClassesActivity extends AppCompatActivity {

    Calendar myCalendar= new GregorianCalendar();
    DB_Management myDB;
    Button enrollButton;
    String username;
    String classId;
    Button backButton;
    Button searchButton;
    Spinner classSpinner;
    String[] classInfo;
    TextView classDescriptionText;
    EditText selectDateEdit;
    EditText selectClassName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_search_classes);

        myDB = new DB_Management(this);
        enrollButton = findViewById(R.id.enrollButton2);
        backButton = findViewById(R.id.backButton512);
        searchButton = findViewById(R.id.searchButton);
        username = LoginActivity.getUser();
        classSpinner = findViewById(R.id.searchClassSpinner);
        classDescriptionText = findViewById(R.id.classDescriptionText);
        selectDateEdit = findViewById(R.id.selectDateEdit3);
        selectClassName = findViewById(R.id.usernameLogin2);

        classInfo = new String[7];

        loadClassSpinnerData();
        classInfo = classSpinner.getSelectedItem().toString().split(" ");
        classId = getClassId();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectDateEdit.getText().toString().length() == 0) {
                    if (selectClassName.getText().toString().length() == 0) {
                        Toast.makeText(MemberSearchClassesActivity.this,"enter fields correctly.", Toast.LENGTH_SHORT).show();
                    } else {
                        loadClassSpinnerDataByClassName();
                        selectClassName.setText("");
                        selectDateEdit.setText("");
                    }
                } else {
                    loadClassSpinnerDataFromDate();
                    selectClassName.setText("");
                    selectDateEdit.setText("");
                }
            }
        });

        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                classInfo = classSpinner.getSelectedItem().toString().split(" ");
                classDescriptionText.setText(getClassDescription());
            }

            public void onNothingSelected(AdapterView<?> parentView) {
                // do nothing
            }
        });

        enrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                classId = getClassId();
                boolean isTimeConflicted = checkTimeConflict();
                boolean isFull = checkCapacity();
                if (!isFull && !isTimeConflicted) {
                    myDB.enrollInClass(username, classId);
                    Toast.makeText(MemberSearchClassesActivity.this,"Successfully enrolled in the class.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    if (isFull && isTimeConflicted) {
                        Toast.makeText(MemberSearchClassesActivity.this,"The class is full and the time conflicts with another class.", Toast.LENGTH_SHORT).show();
                    } else if (isFull) {
                        Toast.makeText(MemberSearchClassesActivity.this,"The class is full.", Toast.LENGTH_SHORT).show();
                    } else if (isTimeConflicted) {
                        Toast.makeText(MemberSearchClassesActivity.this,"The time conflicts with another class.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MemberSearchClassesActivity.this,"Unable to enroll.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                myCalendar.set(Calendar.YEAR,year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                String myFormat = "MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.CANADA);
                selectDateEdit.setText(sdf.format(myCalendar.getTime()));
            }
        };

        selectDateEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    new DatePickerDialog(MemberSearchClassesActivity.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });
    }

    /**
     * gets the id of the selected class
     * @return String of the class id selected
     */
    private String getClassId() {
        return classSpinner.getSelectedItem().toString().split(" ")[0];
    }

    private String getClassDescription() {
        String classType = classInfo[1];
        return myDB.getClassDescriptionByClassType(classType);
    }

    /**
     * checks if the capacity of a class has been reached
     * @return true if the class is full, else false
     */
    private boolean checkCapacity() {
        return myDB.getFreeSpotsOfClass(classId) <= 0;
    }

    /**
     * checks if there is a time conflict with another class that
     * the user is enrolled in
     * @return true if there is a time conflict with another class,
     * else false
     */
    private boolean checkTimeConflict() {
        Date[] selectedClassTimes = myDB.getClassTimeByClassId(classId);
        List<String> enrolledClasses = myDB.getAllClassesByEnrolment(username);
        List<Date[]> enrolledClassesTimes = new ArrayList<>();

        for (String enrolledClassId : enrolledClasses) {
            enrolledClassesTimes.add(myDB.getClassTimeByClassId(enrolledClassId));
        }

        Date selectedStartTime = selectedClassTimes[0];
        Date selectedEndTime = selectedClassTimes[1];

        for (Date[] enrolledTimes : enrolledClassesTimes) {
            Date enrolledStartTime = enrolledTimes[0];
            Date enrolledEndTime = enrolledTimes[1];

            boolean enStartBeforeSelEnd = false;
            boolean selStartBeforeEnEnd = false;

            // a.start <= b.end
            if (enrolledStartTime != null && selectedEndTime != null) {
                enStartBeforeSelEnd = enrolledStartTime.compareTo(selectedEndTime) <= 0;
            }

            // b.start <= a.end
            if (enrolledEndTime != null && selectedStartTime != null) {
                selStartBeforeEnEnd = selectedStartTime.compareTo(enrolledEndTime) <= 0;
            }

            if (enStartBeforeSelEnd && selStartBeforeEnEnd) {
                return true;
            }
        }

        return false;
    }

    /**
     * loads class spinner data from SQL database
     */
    private void loadClassSpinnerData() {

        List<String> scheduledClasses = myDB.getAllScheduledClassesWithID();
        List<String> enrolledClasses = myDB.getAllClassesByEnrolment(username);
        List<String> labels = new ArrayList<>();

        for (String classInfo : scheduledClasses) {

            // check if already enrolled in
            if (!enrolledClasses.contains(classInfo.split(" ")[0])) {
                labels.add(classInfo);
            }
        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, labels);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        classSpinner.setAdapter(dataAdapter);
    }

    /**
     * loads class spinner data from SQL database and from date chosen by user
     */
    private void loadClassSpinnerDataFromDate() {

        List<String> scheduledClasses = myDB.getAllScheduledClassesWithID();
        List<String> enrolledClasses = myDB.getAllClassesByEnrolment(username);
        List<String> labels = new ArrayList<>();
        String date = selectDateEdit.getText().toString();

        for (String classInfo : scheduledClasses) {

            // check date
            if (classInfo.split(" ")[3].equals(date)) {

                // check if already enrolled in
                if (!enrolledClasses.contains(classInfo.split(" ")[0])) {
                    labels.add(classInfo);
                }
            }
        }

        if (labels.isEmpty()) {
            Toast.makeText(MemberSearchClassesActivity.this,"No classes are scheduled this day.", Toast.LENGTH_SHORT).show();
            loadClassSpinnerData();
            selectClassName.setText("");
            selectDateEdit.setText("");
            return;
        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, labels);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        classSpinner.setAdapter(dataAdapter);
    }

    /**
     * loads class spinner data from SQL database
     */
    private void loadClassSpinnerDataByClassName() {

        String className = selectClassName.getText().toString();
        List<String> scheduledClasses = myDB.getAllScheduledClassesWithID();
        List<String> enrolledClasses = myDB.getAllClassesByEnrolment(username);
        List<String> labels = new ArrayList<>();

        for (String classInfo : scheduledClasses) {

            if (classInfo.split(" ")[1].equals(className)) {
                // check if already enrolled in
                if (!enrolledClasses.contains(classInfo.split(" ")[0])) {
                    labels.add(classInfo);
                }
            }
        }

        if (labels.isEmpty()) {
            Toast.makeText(MemberSearchClassesActivity.this,"No classes have this name.", Toast.LENGTH_SHORT).show();
            loadClassSpinnerData();
            selectClassName.setText("");
            selectDateEdit.setText("");
            return;
        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, labels);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to sinner
        classSpinner.setAdapter(dataAdapter);
    }

}