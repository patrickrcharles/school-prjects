/*
Authour - Patrick Charles
clid - prc9219
class - cmps 359 - Android programming summer 2016
assignment - final project

A program to keep track of a student's graduating progress utilizing databases and user input

 */


package com.example.patrick.cmpsgraduation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends Activity implements View.OnClickListener {

    //other variables for calculating gpa, current credits, remaining credits
    double gpa;
    int current_credits, remaining_credits, quality_points;

    //variables for database : table 'all_classes'
    String department, course_id, course_title, grade, concentration_all_classes, type, year_taken, semester_taken;
    double credits, _id;
    boolean passed;

    //variables for database : table 'student'
    String clid, first_name, last_name, concentration_student;

    //buttons for popup window
    Button new_user, login, close, submit_login, submit_new_user;

    //database helper object

    PopupWindow pw;
    DBHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Button new_user, login, close;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //openDatabase();
        Log.i("***SET CONTENT VIEW", "MainActivity Created");


        login = (Button) findViewById(R.id.login);
        new_user = (Button) findViewById(R.id.new_user);

        login.setOnClickListener(this);
        new_user.setOnClickListener(this);

    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_user:
                showPopupNewUser();
                break;
            case R.id.login:
                showPopupLogin();
                break;
            case R.id.new_user_submit:
                enterClasses();
                break;

            case R.id.login_submit:
                displayClasses();
                break;
        }
    }


    String[] new_user_concentrations = {"video game design",
            "scientific computing", "computer engineering", "information technology",
            "general computer science"};

    public void showPopupNewUser() {

        //openDatabase();

        try {
// We need to get the instance of the LayoutInflater
            LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.popup,
                    (ViewGroup) findViewById(R.id.popup_new_user));

            pw = new PopupWindow(layout, 300, 370, true);
            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);

            close = (Button) layout.findViewById(R.id.close_popup);
            submit_new_user = (Button) layout.findViewById(R.id.new_user_submit);

            Spinner concentration = (Spinner) layout.findViewById(R.id.spinner_new_user_concentration);
            //concentration.setAdapter();

            ArrayAdapter<String> adapter =
                    new ArrayAdapter<String>(MainActivity.this,
                            android.R.layout.simple_spinner_item, new_user_concentrations);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            concentration.setAdapter(adapter);

            close.setOnClickListener(cancel_button);
            submit_new_user.setOnClickListener(this);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void showPopupLogin() {

        //openDatabase();

        try {
// We need to get the instance of the LayoutInflater
            LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.login,
                    (ViewGroup) findViewById(R.id.popup_login));

            pw = new PopupWindow(layout, 300, 370, true);
            pw.showAtLocation(layout, Gravity.CENTER, 0, 0);

            close = (Button) layout.findViewById(R.id.close_popup);
            submit_login = (Button) layout.findViewById(R.id.login_submit);

            close.setOnClickListener(cancel_button);
            submit_login.setOnClickListener(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener cancel_button = new View.OnClickListener() {
        public void onClick(View v) {
            pw.dismiss();
        }
    };

    public void enterClasses() {

        //submit = (Button)findViewById(R.id.);

        submit_new_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, enterclasses.class));
            }
        });

    }

    public void displayClasses() {

        submit_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //EditText ed = (EditText) findViewById(R.id.login_clid);
                //EditText ed2 = (EditText) findViewById(R.id.login_password);

                startActivity(new Intent(MainActivity.this, displayClasses.class));

                /*
                String clid1 = ed.getText().toString();
                String password1 = ed2.getText().toString();
                String data1 = null,data2 = null; //store passwrd/clid from database

                String query = "select clid, password from student where clid = "+ clid1 +" password = "+ password1;
                //Model modelItems1[];
                Cursor cursor = db.myDataBase.rawQuery(query, null);
                int numRows = cursor.getCount();

                //modelItems1 = new Model[numRows];

                if (cursor.moveToFirst()) {
                    for (int i = 0; i < numRows; i++) {

                        data1 = cursor.getString(0);
                        data2 = cursor.getString(1);
                        cursor.moveToNext();
                    }
                    cursor.close();
                }

                if(clid1.equals(data1) && password1.equals(data2)){
                    startActivity(new Intent(MainActivity.this, displayClasses.class));}
                else {
                    showPopupLogin();
                }
                */
            }});}




    public void openDatabase(){
        // open database

        try {

            db.createDataBase();

        } catch (IOException e) {

            throw new RuntimeException(e);

        }

        try {

            db.openDataBase();

        } catch (SQLException sqle) {

            throw sqle;

        }
    }
}




            /*
    public void checkLogin(String clid, String password) {

        EditText ed = (EditText) findViewById(R.id.login_clid);
        EditText ed2 = (EditText) findViewById(R.id.login_password);

        String clid1 = ed.getText().toString();
        String password1 = ed2.getText().toString();

        String query = "select clid, password from student where clid = "+ clid +" password = "+ password;
        Model modelItems1[];

        Cursor cursor = db.myDataBase.rawQuery(query, null);
        int numRows = cursor.getCount();

        modelItems1 = new Model[numRows];

        if (cursor.moveToFirst()) {
            for (int i = 0; i < numRows; i++) {

                clid1 = cursor.getString(0);
                password1 = cursor.getString(1);
                cursor.moveToNext();
            }
            cursor.close();
        }

        if(clid1.equals(clid) && password1.equals(password)){
            enterClasses();}
        else{
                showPopupLogin();
            }
        }
        */


