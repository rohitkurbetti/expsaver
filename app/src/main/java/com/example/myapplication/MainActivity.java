package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    TextInputLayout etPerticularsLayout,textInputLayout2,textInputLayout3,textInputLayout4;
    TextInputLayout etPerticularsLayoutAlert,textInputLayout2Alert,textInputLayout3Alert,textInputLayout4Alert;
    TextInputEditText etPerticulars,etDateTime,etActSuffCost,etCostInc;
    TextView tvSavings,tvListAll;
    int hr,min;
    long longDate;
    long toPutMaxDate;
    Button btnSave;
    DbManager db;
    String pert;
    Integer costInc;
    Integer actSuffCost;
    Integer id=0;
    String addedDateTime;
    private CharSequence[] mItems;
    LinearLayout paintListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DbManager(this);
        etPerticulars = findViewById(R.id.etPerticulars);
        etCostInc = findViewById(R.id.etCostInc);
        etActSuffCost = findViewById(R.id.etActSuffCost);
        etDateTime = findViewById(R.id.etDateTime);
        etPerticularsLayout = findViewById(R.id.textInputLayout);
        textInputLayout2 = findViewById(R.id.textInputLayout2);
        textInputLayout3 = findViewById(R.id.textInputLayout3);
        textInputLayout4 = findViewById(R.id.textInputLayout4);
        tvListAll = findViewById(R.id.tvListAll);
        tvSavings = findViewById(R.id.tvSavings);
        paintListView = findViewById(R.id.paintListView);

        btnSave = findViewById(R.id.btnSave);

        getSavings();

        mItems = new CharSequence[]{
                "Movies",
                "Photos",
                "Music"
        };

        etPerticulars.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>0) {
                    String etperticulars = charSequence.toString();
                    if (etperticulars.length() > 40) {
                        etPerticularsLayout.setError("Input Exceeded");
                    } else {
                        etPerticularsLayout.setError("");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etDateTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>0) {
                    String etDateTime = charSequence.toString();
                    if (etDateTime.length() > 0) {
                        etPerticularsLayout.setError("");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnSave.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final View customView = getLayoutInflater().inflate(R.layout.edit_view, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                StringBuffer sb = new StringBuffer();
                builder.setTitle("Preferences");
                builder.setView(customView);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TextInputEditText etId = customView.findViewById(R.id.etId);
                        Integer id = Integer.parseInt(String.valueOf(etId.getText()).isEmpty()?"0":String.valueOf(etId.getText()));
                        if(id > 0) {
                            Cursor res = db.getOne(id);
                            if (res.getCount() > 0) {
                                populateEditScreen(res);
                            } else {
                                Snackbar.make(btnSave, "No rec found",Snackbar.LENGTH_SHORT).show();
                            }
                        }else{
                            Snackbar.make(btnSave, "Enter valid id",Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("List All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listAll();
//                        showSingleItemMaterialAlertDialog();
                    }
                });
                builder.setCancelable(false);
                builder.setMessage(sb);
                AlertDialog dialog = builder.create();
                dialog.show();
                return false;
            }

            private void populateEditScreen(Cursor res) {
                final View actMain = getLayoutInflater().inflate(R.layout.activity_main, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(actMain);
                TextInputEditText etPert = actMain.findViewById(R.id.etPerticulars);
                TextInputEditText etCostInc = actMain.findViewById(R.id.etCostInc);
                TextInputEditText etSuff =  actMain.findViewById(R.id.etActSuffCost);
                TextInputEditText etDate = actMain.findViewById(R.id.etDateTime);
                etPerticularsLayoutAlert = actMain.findViewById(R.id.textInputLayout);
                textInputLayout2Alert = actMain.findViewById(R.id.textInputLayout2);
                textInputLayout3Alert = actMain.findViewById(R.id.textInputLayout3);
                textInputLayout4Alert = actMain.findViewById(R.id.textInputLayout4);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(id != null && id !=0){
                            String str = db.deleteExpByBillNo(id);
                            Snackbar.make(btnSave, ""+str,Snackbar.LENGTH_SHORT).show();
                            getSavings();
                            listAll();
                        }else{
                            Snackbar.make(btnSave, "Enter valid id",Snackbar.LENGTH_SHORT).show();
                        }

                    }
                });
                etDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final Calendar c = Calendar.getInstance();
                        int mYear = c.get(Calendar.YEAR);
                        int mMonth = c.get(Calendar.MONTH);
                        int mDay = c.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                String day1,month1;
                                if(view.getDayOfMonth()<10){
                                    day1="0"+view.getDayOfMonth();
                                }else{
                                    day1=""+view.getDayOfMonth();
                                }
                                if((view.getMonth()+1)<10){
                                    month1="0"+(view.getMonth()+1);
                                }else{
                                    month1=""+(view.getMonth()+1);
                                }
                                etDate.setText(day1+"/"+month1+"/"+view.getYear());
                                timePicker(view.getYear()+"/"+month1+"/"+day1,etDate);
                            }

                            private void timePicker(String s, TextInputEditText etDateTime) {

                                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @RequiresApi(api = Build.VERSION_CODES.M)
                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                                  int minute) {
                                                hr = hourOfDay;
                                                min = minute;
                                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                                                etDate.setText(sdf.format(Date.parse(s+" "+view.getHour() + ":" + view.getMinute())));
                                            }
                                        }, hr, min, false);
                                timePickerDialog.show();
                            }
                        }, mYear, mMonth, mDay);
                        if(toPutMaxDate != 0){
                            datePickerDialog.getDatePicker().setMaxDate(toPutMaxDate);
                        }else{
                            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                        }
                        datePickerDialog.show();
                    }
                });

                Button btnEdit = actMain.findViewById(R.id.btnSave);

                while (res.moveToNext()) {
                    id = res.getInt(0);
                    pert = res.getString(1);
                    costInc = res.getInt(2);
                    actSuffCost = res.getInt(3);
                    addedDateTime = res.getString(5);
                    etPert.setText(String.valueOf(pert));
                    etCostInc.setText(String.valueOf(costInc));
                    etSuff.setText(String.valueOf(actSuffCost));
                    etDate.setText(String.valueOf(addedDateTime));
                }
                if(id!=null && id!=0){
                    btnEdit.setText("Update");
                    builder.setTitle("Edit Record "+id);
                }else{
                    btnEdit.setText("Save");
                }
                btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String date = null;
                        pert = String.valueOf(etPert.getText());
                        costInc = Integer.parseInt(String.valueOf(etCostInc.getText()).isEmpty()?"0": String.valueOf(etCostInc.getText()));
                        actSuffCost = Integer.parseInt(String.valueOf(etSuff.getText()).isEmpty()?"0": String.valueOf(etSuff.getText()));
                        addedDateTime = String.valueOf(etDate.getText());
                        try {
                            if(addedDateTime != null && addedDateTime.length()>0){
                                date = String.valueOf(formatStandardDate(addedDateTime));
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if(isValid(id, pert, costInc, actSuffCost, actSuffCost - costInc, addedDateTime, date)){
                            int res = db.updateRec(id, pert, costInc, actSuffCost, actSuffCost - costInc, addedDateTime, date);
                            if (res != 1) {
                                Snackbar.make(btnEdit, "Update Failed",Snackbar.LENGTH_SHORT).show();
                            }else{
                                Snackbar.make(btnEdit, "Record updated",Snackbar.LENGTH_SHORT).show();
                                getSavings();
                                listAll();
                            }
                        }else{
                            Snackbar.make(btnEdit, "Validation failed",Snackbar.LENGTH_SHORT).show();
                        }

                    }

                    private boolean isValid(Integer id, String pert, Integer costInc, Integer actSuffCost, int i, String addedDateTime, String date) {
                        boolean isValid = true;
                        String alphaNumericRegex = "^[a-zA-Z][a-zA-Z0-9]*$";
                        if(pert==null || pert=="" || pert.length()<=0){
                            isValid = false;
                            etPerticularsLayoutAlert.setError("Cannot be empty");
                        } else if(pert.length()>40) {
                            isValid = false;
                            etPerticularsLayoutAlert.setError("Input Exceeded");
                        } else if(!Pattern.compile(alphaNumericRegex).matcher(pert).matches()){
                            isValid = false;
                            etPerticularsLayoutAlert.setError("Enter valid input");
                        } else {
                            etPerticularsLayoutAlert.setError("");
                        }

                        if(id==null || id==0){
                            isValid = false;
                        }else if(id > 50000){
                            isValid = false;
                        }

                        if(costInc==null || costInc==0){
                            isValid = false;
                            textInputLayout2Alert.setError("Cannot be empty");
                        }else if(costInc > 50000){
                            isValid = false;
                            textInputLayout2Alert.setError("Input Exceeded");
                        } else{
                            textInputLayout2Alert.setError("");
                        }


                        if(actSuffCost==null || actSuffCost==0){
                            isValid = false;
                            textInputLayout3Alert.setError("Cannot be empty");
                        }else if(actSuffCost.intValue()>50000){
                            isValid = false;
                            textInputLayout3Alert.setError("Input Exceeded");
                        } else{
                            textInputLayout3Alert.setError("");
                        }


                        if(addedDateTime==null || addedDateTime=="" || addedDateTime.length()<=0){
                            isValid = false;
                            textInputLayout4Alert.setError("Cannot be empty");
                        }else{
                            textInputLayout4Alert.setError("");
                        }

                        if(date==null || date=="" || date.length()<=0){
                            isValid = false;

                        }
                        return isValid;


                    }

                    private String formatStandardDate(String addedDateTime) throws ParseException {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                        Date date = sdf.parse(addedDateTime);
                        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
                        String formattedDate = sdf1.format(date.getTime());
                        return formattedDate;
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pert = String.valueOf(etPerticulars.getText());
                Integer constInc = Integer.parseInt(String.valueOf(etCostInc.getText()).isEmpty()?"0": String.valueOf(etCostInc.getText()));
                Integer actSuffCost = Integer.parseInt(String.valueOf(etActSuffCost.getText()).isEmpty()?"0": String.valueOf(etActSuffCost.getText()));
                String dateTime = String.valueOf(etDateTime.getText());
                String date = null;
                Integer diff = 0;
                try {
                    if(dateTime != null && dateTime.length()>0){
                        date = String.valueOf(formatStandardDate(dateTime));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(isValid(pert, constInc, actSuffCost, dateTime, date)) {
                    try {
                        diff = calculateDiffernce(constInc,actSuffCost);
                        int res = db.saveOne(pert, constInc, actSuffCost, diff, dateTime, date);
                        if (res == 1) {
                            Snackbar.make(btnSave, "Insertion completed", Snackbar.LENGTH_SHORT).show();
                            resetLayoutFields();
                            getSavings();
                            listAll();
                        } else {
                            Snackbar.make(btnSave, "Insertion Failed", Snackbar.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Snackbar.make(btnSave, "Validation Failed", Snackbar.LENGTH_SHORT).show();
                }
            }

            private void resetLayoutFields() {
                etPerticulars.setText(null);
                etDateTime.setText(null);
                etActSuffCost.setText(null);
                etCostInc.setText(null);
                etPerticulars.requestFocus();
            }

            private Integer calculateDiffernce(Integer constInc, Integer actSuffCost) {
                return actSuffCost - constInc;
            }

            private boolean isValid(String pert, Integer constInc, Integer actSuffCost, String dateTime, String date) {
                boolean isValid = true;
                String alphaNumericRegex = "^[a-zA-Z][a-zA-Z0-9]*$";
                if(pert==null || pert=="" || pert.length()<=0){
                    isValid = false;
                    etPerticularsLayout.setError("Cannot be empty");
                } else if(pert.length()>40) {
                    isValid = false;
                    etPerticularsLayout.setError("Input Exceeded");
                } else if(!Pattern.compile(alphaNumericRegex).matcher(pert).matches()){
                    isValid = false;
                    etPerticularsLayout.setError("Enter valid input");
                } else {
                    etPerticularsLayout.setError("");
                }


                if(constInc==null || constInc==0){
                    isValid = false;
                    textInputLayout2.setError("Cannot be empty");
                }else if(constInc > 50000){
                    isValid = false;
                    textInputLayout2.setError("Input Exceeded");
                } else{
                    textInputLayout2.setError("");
                }


                if(actSuffCost==null || actSuffCost==0){
                    isValid = false;
                    textInputLayout3.setError("Cannot be empty");
                }else if(actSuffCost.intValue()>50000){
                    isValid = false;
                    textInputLayout3.setError("Input Exceeded");
                } else{
                    textInputLayout3.setError("");
                }


                if(dateTime==null || dateTime=="" || dateTime.length()<=0){
                    isValid = false;
                    textInputLayout4.setError("Cannot be empty");
                }else{
                    textInputLayout4.setError("");
                }

                if(date==null || date=="" || date.length()<=0){
                    isValid = false;

                }
                return isValid;
            }

            private String formatStandardDate(String dateTime) throws ParseException {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                Date date = sdf.parse(dateTime);
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
                String formattedDate = sdf1.format(date.getTime());
                return formattedDate;
            }
        });

        etDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        String day1,month1;
                        if(view.getDayOfMonth()<10){
                            day1="0"+view.getDayOfMonth();
                        }else{
                            day1=""+view.getDayOfMonth();
                        }
                        if((view.getMonth()+1)<10){
                            month1="0"+(view.getMonth()+1);
                        }else{
                            month1=""+(view.getMonth()+1);
                        }
                        etDateTime.setText(day1+"/"+month1+"/"+view.getYear());
                        timePicker(view.getYear()+"/"+month1+"/"+day1,etDateTime);
                    }

                    private void timePicker(String s, TextInputEditText etDateTime) {

                        TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.M)
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay,
                                                          int minute) {
                                        hr = hourOfDay;
                                        min = minute;
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                                        etDateTime.setText(sdf.format(Date.parse(s+" "+view.getHour() + ":" + view.getMinute())));
                                    }
                                }, hr, min, false);
                        timePickerDialog.show();
                    }
                }, mYear, mMonth, mDay);
                if(toPutMaxDate != 0){
                    datePickerDialog.getDatePicker().setMaxDate(toPutMaxDate);
                }else{
                    datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                }
                datePickerDialog.show();
            }
        });





    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showSingleItemMaterialAlertDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
        builder.setSingleChoiceItems(mItems, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Snackbar.make(btnSave, "You have selected : "+mItems[i],Snackbar.LENGTH_SHORT).show();
//                                dialogInterface.dismiss();
            }
        });
        builder.setBackground(getDrawable(R.drawable.alert_dialog_bg));
//                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//
//                            }
//                        });
//                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//
//                            }
//                        });
        builder.show();
    }

    private void listAll() {
        paintListView.removeAllViews();
        final View customView = getLayoutInflater().inflate(R.layout.custom_table, null);
        TableLayout tableLayout = customView.findViewById(R.id.table_layout);
        tableLayout.setWeightSum(1);
        Cursor res =  db.fetchAll();
//        if(res.getCount()>0){
//            tvListAll.setText(null);
//            while(res.moveToNext()){
//                sb.append(" "+res.getInt(0)+"  "+res.getString(1)+"  "+res.getInt(2)+"  "+res.getInt(3)+"  "+res.getInt(4)+"  "+res.getString(5)+"  "+res.getString(6)+"\n");
//            }
//            tvListAll.setText(sb.toString());
//        } else {
//            Snackbar.make(btnSave, "No records found",Snackbar.LENGTH_SHORT).show();
//        }
        if(res.getCount()>0){
            while(res.moveToNext()){
                TableRow tableRow = new TableRow(customView.getContext());
                tableRow.setPadding(3,3,3,3);

                TextView tvsrno = new TextView(customView.getContext());
                TextView tvpert = new TextView(customView.getContext());
                TextView tvCostInc = new TextView(customView.getContext());
                TextView tvactSuffCost = new TextView(customView.getContext());
                TextView tvdifference = new TextView(customView.getContext());
                TextView tvdateTime = new TextView(customView.getContext());

                tvsrno.setText(String.valueOf(res.getInt(0)));
                tvpert.setText(String.valueOf(res.getString(1)));
                tvCostInc.setText(String.valueOf(res.getInt(2)));
                tvactSuffCost.setText(String.valueOf(res.getInt(3)));
                tvdifference.setText(String.valueOf(res.getInt(4)));
                tvdateTime.setText(String.valueOf(res.getString(5)));

                tvsrno.setGravity(Gravity.CENTER_HORIZONTAL);
                tvpert.setGravity(Gravity.CENTER_HORIZONTAL);
                tvCostInc.setGravity(Gravity.CENTER_HORIZONTAL);
                tvactSuffCost.setGravity(Gravity.CENTER_HORIZONTAL);
                tvdifference.setGravity(Gravity.CENTER_HORIZONTAL);
                tvdateTime.setGravity(Gravity.CENTER_HORIZONTAL);

                tvsrno.setPadding(3,3,3,3);
                tvpert.setPadding(3,3,3,3);
                tvCostInc.setPadding(3,3,3,3);
                tvactSuffCost.setPadding(3,3,3,3);
                tvdifference.setPadding(3,3,3,3);
                tvdateTime.setPadding(3,3,3,3);

                tvsrno.setTextSize(10);
                tvpert.setTextSize(10);
                tvCostInc.setTextSize(10);
                tvactSuffCost.setTextSize(10);
                tvdifference.setTextSize(10);
                tvdateTime.setTextSize(10);

                tableRow.addView(tvsrno);
                tableRow.addView(tvpert);
                tableRow.addView(tvCostInc);
                tableRow.addView(tvactSuffCost);
                tableRow.addView(tvdifference);
                tableRow.addView(tvdateTime);

                tableLayout.addView(tableRow);
            }
            paintListView.addView(customView);
        }else{
            Snackbar.make(btnSave, "No records found",Snackbar.LENGTH_SHORT).show();
            paintListView.removeView(customView);
        }
    }

    private void getSavings() {
        int savings =0;
        savings = db.getSavings();
        tvSavings.setText("Total Savings: "+savings);
    }
}