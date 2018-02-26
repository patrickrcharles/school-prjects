package com.example.patrick.cmpsgraduation;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.io.IOException;

public class math_prereq extends Activity implements View.OnClickListener {

    ListView lv4;
    Model[] modelItems4;//math prereqs
    Button math_prereps_submit_button;

    //DBHelper dbHelper;
    DBHelper db = new DBHelper(this);

    int numRows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_prereq);

        math_prereps_submit_button = (Button) findViewById(R.id.mth_prereqs_done);
        math_prereps_submit_button.setOnClickListener(this);

        lv4 = (ListView) findViewById(R.id.listView4); //for  required

        openDatabase();
        getMathPreReqs();

        CustomAdapterForCheckBoxList adapter = new CustomAdapterForCheckBoxList(this, modelItems4);
        lv4.setAdapter(adapter);

        db.myDataBase.close();
    }

    @Override
    public void onClick(View view) {
        math_prereps_submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(math_prereq.this, displayClasses.class));
            }
        });

    }


    public void openDatabase() {
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

    public void getMathPreReqs(){

        String query3 = "select department, course_id, course_title, grade, semester_taken, passed from allclasses where " +
                "type like 'required math%' order by department asc";

        Cursor cursor3 = db.myDataBase.rawQuery(query3, null);
        numRows = cursor3.getCount();
        modelItems4 = new Model[numRows];

        if (cursor3.moveToFirst()) {
            for (int i = 0; i < numRows; i++) {
                String data = cursor3.getString(0);
                String data1 = cursor3.getString(1);
                String data2 = cursor3.getString(2);
                String data3 = cursor3.getString(3); //checkbox value
                String data4 = cursor3.getString(4);
                String data5 = cursor3.getString(5);
                //String data6 = cursor3.getString(6);
                // do what ever you want here
                modelItems4[i] = new Model(data, data1, data2,data3, data4, data5);
                cursor3.moveToNext();

            }
            cursor3.close();

        }
        cursor3.close();

    }


}
