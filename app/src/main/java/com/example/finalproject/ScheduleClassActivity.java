package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class ScheduleClassActivity extends AppCompatActivity {

    Calendar myCalendar = new GregorianCalendar();
    Calendar startCalendar = new GregorianCalendar();
    Calendar endCalendar = new GregorianCalendar();

    Button backButton, scheduleClass;
    Spinner fitnessTypeSpinner, difficultySpinner;
    EditText maximumCapacityNumber, selectStartTimeEdit, selectEndTimeEdit, selectDateEdit;
    DB_Management myDB;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_class);

        myDB = new DB_Management(this);
        backButton = findViewById(R.id.backButton5);
        scheduleClass = findViewById(R.id.scheduleClassButton);

        fitnessTypeSpinner = findViewById(R.id.fitnessTypeSpinner);
        difficultySpinner = findViewById(R.id.difficultySpinner);

        maximumCapacityNumber = findViewById(R.id.maximumCapacityNumber);
        selectStartTimeEdit = findViewById(R.id.selectStartTimeEdit);
        selectEndTimeEdit = findViewById(R.id.selectEndTimeEdit);
        selectDateEdit = findViewById(R.id.selectDateEdit);

        username = LoginActivity.getUser();

        loadFitnessSpinnerData();
        loadDifficultySpinnerData();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        selectStartTimeEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    return;
                }

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ScheduleClassActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        startCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        startCalendar.set(Calendar.MINUTE, selectedMinute);
                        String myFormat = "HH:mm";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.CANADA);
                        selectStartTimeEdit.setText(sdf.format(startCalendar.getTime()));
                    }
                }, 0, 0, true);//Yes 24 hour time
                mTimePicker.setTitle("Select the start time");
                mTimePicker.show();
            }
        });

        selectEndTimeEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    return;
                }

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ScheduleClassActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        endCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        endCalendar.set(Calendar.MINUTE, selectedMinute);
                        String myFormat = "HH:mm";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.CANADA);
                        selectEndTimeEdit.setText(sdf.format(endCalendar.getTime()));
                    }
                }, 0, 0, true);// Yes 24 hour time
                mTimePicker.setTitle("Select the end time");
                mTimePicker.show();
            }
        });

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                startCalendar.set(Calendar.YEAR, year);
                startCalendar.set(Calendar.MONTH, monthOfYear);
                startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                endCalendar.set(Calendar.YEAR, year);
                endCalendar.set(Calendar.MONTH, monthOfYear);
                endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

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
                    new DatePickerDialog(ScheduleClassActivity.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

        scheduleClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fitnessType, difficulty;
                int maximumCapacity;
                long startTime, endTime;

                fitnessType = fitnessTypeSpinner.getSelectedItem().toString();
                difficulty = difficultySpinner.getSelectedItem().toString();

                boolean isFieldCorrect = true;

                if (selectDateEdit.getText().length() == 0) {
                    selectDateEdit.setHintTextColor(Color.RED);
                    isFieldCorrect = false;

                }
                if (selectStartTimeEdit.getText().length() == 0) {
                    selectStartTimeEdit.setHintTextColor(Color.RED);
                    isFieldCorrect = false;

                }
                if (selectEndTimeEdit.getText().length() == 0) {
                    selectEndTimeEdit.setHintTextColor(Color.RED);
                    isFieldCorrect = false;

                }
                if (maximumCapacityNumber.getText().toString().length() == 0) {
                    maximumCapacityNumber.setHintTextColor(Color.RED);
                    isFieldCorrect = false;
                }
                if (startCalendar.getTime().getTime() >= endCalendar.getTime().getTime()) {
                    selectStartTimeEdit.setHintTextColor(Color.RED);
                    selectEndTimeEdit.setHintTextColor(Color.RED);
                    isFieldCorrect = false;

                }

                boolean isAlreadyTaught = false;
                List<String> classesByName = myDB.getAllClassesByClassName(fitnessType);
                for (String classes : classesByName) {
                    if (classes.split(" ")[2].split("-")[1].equals(selectDateEdit.getText().toString().split("-")[1])) {
                        isAlreadyTaught = true;
                        break;
                    }
                }


                if (isFieldCorrect && !isAlreadyTaught) {
                    maximumCapacity = Integer.parseInt(maximumCapacityNumber.getText().toString());
                    startTime = startCalendar.getTime().getTime();
                    endTime = endCalendar.getTime().getTime();
                    myDB.createClass(fitnessType,difficulty,startTime,endTime,maximumCapacity,username);
                    finish();
                } else {
                    if (!isFieldCorrect){
                        Toast.makeText(ScheduleClassActivity.this, "Make sure all of the fields are entered correctly.", Toast.LENGTH_SHORT).show();
                    }
                    if (isAlreadyTaught) {
                        Toast.makeText(ScheduleClassActivity.this, "Another instructor is already teaching this class. Change the day or the class.", Toast.LENGTH_SHORT).show();
                        selectDateEdit.setText("");
                        selectDateEdit.setHintTextColor(Color.RED);
                    }
                }
            }
        });
    }

    /**
     * loads spinner data
     */
    private void loadFitnessSpinnerData() {
        myDB = new DB_Management(this );
        List<String> labels = myDB.getAllClassTypes();
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, labels);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        fitnessTypeSpinner.setAdapter(dataAdapter);
    }

    /**
     * loads spinner data
     */
    private void loadDifficultySpinnerData() {
        List<String> labels = new ArrayList<>();
        labels.add("Beginner");
        labels.add("Intermediate");
        labels.add("Advanced");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, labels);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        difficultySpinner.setAdapter(dataAdapter);
    }
}