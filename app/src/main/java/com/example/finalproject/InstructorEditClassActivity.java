package com.example.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class InstructorEditClassActivity extends AppCompatActivity {

    // the dates were kept as calendars.

    Calendar myCalendar = Calendar.getInstance();
    Calendar startCalendar = new GregorianCalendar();
    Calendar endCalendar = new GregorianCalendar();

    DB_Management myDB;
    Button backButton, saveChangesButton;
    Spinner classSpinner2, difficultySpinner;
    EditText selectDateEdit, selectStartTimeEdit, selectEndTimeEdit, maximumCapacityNumber;
    String username;
    int classID, capacity;
    String type, difficulty;
    long startTime, endTime;
    int startHour = 0, startMin = 0, endHour = 0, endMin = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_edit_class);

        myDB = new DB_Management(this);

        backButton = findViewById(R.id.backButton6);
        saveChangesButton = findViewById(R.id.saveChangesButton2);

        selectDateEdit = findViewById(R.id.selectDateEdit2);
        selectEndTimeEdit = findViewById(R.id.selectEndTimeEdit2);
        selectStartTimeEdit = findViewById(R.id.selectStartTimeEdit2);
        maximumCapacityNumber = findViewById(R.id.maximumCapacityNumber3);

        classSpinner2 = findViewById(R.id.classSpinner2);
        difficultySpinner = findViewById(R.id.difficultySpinner3);

        username = LoginActivity.getUser();

        loadSpinnerData();

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
                mTimePicker = new TimePickerDialog(InstructorEditClassActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        startCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        startCalendar.set(Calendar.MINUTE, selectedMinute);
                        String myFormat = "HH:mm";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.CANADA);
                        selectStartTimeEdit.setText(sdf.format(startCalendar.getTime()));
                    }
                }, startHour, startMin, true);//Yes 24 hour time
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
                mTimePicker = new TimePickerDialog(InstructorEditClassActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        endCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        endCalendar.set(Calendar.MINUTE, selectedMinute);
                        String myFormat = "HH:mm";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.CANADA);
                        selectEndTimeEdit.setText(sdf.format(endCalendar.getTime()));
                    }
                }, endHour, endMin, true);// Yes 24 hour time
                mTimePicker.setTitle("Select the end time");
                mTimePicker.show();
            }
        });

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                startCalendar.set(Calendar.YEAR, year);
                startCalendar.set(Calendar.MONTH, monthOfYear);
                startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                endCalendar.set(Calendar.YEAR, year);
                endCalendar.set(Calendar.MONTH, monthOfYear);
                endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd-MM-yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.CANADA);
                selectDateEdit.setText(sdf.format(myCalendar.getTime()));
            }
        };

        selectDateEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    new DatePickerDialog(InstructorEditClassActivity.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

        classSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                String[] selectedItem = new String[7];

                if (classSpinner2.getSelectedItem().toString().length() == 0) {
                    selectDateEdit.setText("");
                    selectEndTimeEdit.setText("");
                    selectStartTimeEdit.setText("");
                    maximumCapacityNumber.setText("");
                    return;
                }

                classID = Integer.parseInt(classSpinner2.getSelectedItem().toString().split(" ")[1]);
                List<String> listOfClasses = myDB.getAllClassesByInstructorName(username);

                for (String classOfInstructor : listOfClasses) {
                    if (classOfInstructor.split(" ")[0].equals(Integer.toString(classID))) {
                        selectedItem = classOfInstructor.split(" ");
                        break;
                    }
                }
                maximumCapacityNumber.setText(selectedItem[3].split("-")[0]);

                // update values
                type = selectedItem[1];
                difficulty = selectedItem[2];
                startHour = Integer.parseInt(selectedItem[4].split("-")[0].split(":")[0]);
                startMin = Integer.parseInt(selectedItem[4].split("-")[0].split(":")[1]);
                endHour = Integer.parseInt(selectedItem[4].split("-")[1].split(":")[0]);
                endMin = Integer.parseInt(selectedItem[4].split("-")[1].split(":")[1]);
                myCalendar.set(Calendar.MONTH, Integer.parseInt(selectedItem[3].split("-")[0])-1);          // -1 because $%#!& %!$@
                myCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(selectedItem[3].split("-")[1]));

                startCalendar.set(Calendar.YEAR, myCalendar.get(Calendar.YEAR));
                startCalendar.set(Calendar.MONTH, myCalendar.get(Calendar.MONTH));
                startCalendar.set(Calendar.DAY_OF_MONTH, myCalendar.get(Calendar.DAY_OF_MONTH));
                startCalendar.set(Calendar.HOUR_OF_DAY, startHour);
                startCalendar.set(Calendar.MINUTE, startMin);

                endCalendar.set(Calendar.YEAR, myCalendar.get(Calendar.YEAR));
                endCalendar.set(Calendar.MONTH, myCalendar.get(Calendar.MONTH));
                endCalendar.set(Calendar.DAY_OF_MONTH, myCalendar.get(Calendar.DAY_OF_MONTH));
                endCalendar.set(Calendar.HOUR_OF_DAY, endHour);
                endCalendar.set(Calendar.MINUTE, endMin);


                // temporary fix, spaces in class type. ex.: mortal combat
                try {
                    capacity = Integer.parseInt(selectedItem[5]);
                } catch (Exception ignored) {
                    capacity = Integer.parseInt(selectedItem[6]);
                }

                int difficultySpinnerIndex;
                switch (difficulty) {
                    case "Intermediate":
                        difficultySpinnerIndex = 1;
                        break;
                    case "Advanced":
                        difficultySpinnerIndex = 2;
                        break;
                    default:
                        difficultySpinnerIndex = 0;
                }

                // update textviews and difficulty spinner
                loadDifficultySpinnerData();
                difficultySpinner.setSelection(difficultySpinnerIndex);
                selectDateEdit.setText(selectedItem[3]);
                selectEndTimeEdit.setText(selectedItem[4].split("-")[1]);
                selectStartTimeEdit.setText(selectedItem[4].split("-")[0]);
//                maximumCapacityNumber.setText(Integer.toString(capacity));

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                fitnessType = fitnessTypeSpinner.getSelectedItem().toString();
//                difficulty = difficultySpinner.getSelectedItem().toString();

                boolean isFieldCorrect = true;
                // incorrect field entry cases
//                if (fitnessType.length() == 0) {
//                    fitnessTypeSpinner.setBackgroundColor(Color.RED);
//                    isFieldCorrect = false;
//                }
//                if (difficulty.length() == 0) {
//                    difficultySpinner.setBackgroundColor(Color.RED);
//                    isFieldCorrect = false;
//
//                }
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

                if (isFieldCorrect) {
                    capacity = Integer.parseInt(maximumCapacityNumber.getText().toString());
                    startTime = startCalendar.getTime().getTime();
                    endTime = endCalendar.getTime().getTime();
                    myDB.updateClass(classID, type, difficulty, startTime, endTime, capacity, username);
                    finish();
                } else {
                    Toast.makeText(InstructorEditClassActivity.this, "Make sure all of the fields are not empty.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    /**
     * loads spinner data
     */
    private void loadSpinnerData() {

        myDB = new DB_Management(this );
        List<String> temp = myDB.getAllClassesByInstructorName(username);
        List<String> labels = new ArrayList<>();

        if (temp.get(0) == null) {
            noClassAlert();
            return;
        }

        for (String classInfo : temp) {
            List<String> splitClass = Arrays.asList(classInfo.split(" "));
//            String shortenedLevel = (splitClass.get(2).substring(0,3)+".");
            labels.add("ID: "+splitClass.get(0)+" "+splitClass.get(1) +" "+splitClass.get(2)+" "+splitClass.get(3)+" at "+splitClass.get(4));
        }

        if (labels.isEmpty()) {
            noClassAlert();
            return;
        }

        labels.add(0,"");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, labels);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        classSpinner2.setAdapter(dataAdapter);

    }

    /**
     * loads spinner data from SQL database
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

    /**
     * makes an alert pop on the screen to prompt the user
     */
    private void noClassAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("")
                .setTitle("You currently have no classes.")
                .setPositiveButton("Go back", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("Add a class", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        Intent intent = new Intent(getApplicationContext(),ScheduleClassActivity.class);
                        startActivity(intent);
                    }
                });
        builder.create();
        builder.show();
    }
}