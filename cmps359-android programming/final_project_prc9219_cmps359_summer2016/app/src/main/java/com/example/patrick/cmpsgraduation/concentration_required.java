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

public class concentration_required extends Activity implements View.OnClickListener {

    ListView lv2;
    Model[] modelItems2;//require
    Button concentration_required_submit_button;

    //DBHelper dbHelper;
    DBHelper db = new DBHelper(this);

    int numRows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concentration_required);

        concentration_required_submit_button = (Button) findViewById(R.id.concentration_required_next);
        concentration_required_submit_button.setOnClickListener(this);

        lv2 = (ListView) findViewById(R.id.listView2); //for concentration required

        openDatabase();
        getConcentrationRequiredClasses();

        CustomAdapterForCheckBoxList adapter2 = new CustomAdapterForCheckBoxList(this, modelItems2);
        lv2.setAdapter(adapter2);

        db.myDataBase.close();
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


    public void getConcentrationRequiredClasses() {

        String query2 = "select department, course_id, course_title, grade, semester_taken, passed from allclasses where " +
                "type='concentration required' order by department asc";
        Cursor cursor1 = db.myDataBase.rawQuery(query2, null);
        numRows = cursor1.getCount();
        modelItems2 = new Model[numRows];

        if (cursor1.moveToFirst()) {
            for (int i = 0; i < numRows; i++) {
                String data = cursor1.getString(0);
                String data1 = cursor1.getString(1);
                String data2 = cursor1.getString(2);
                String data3 = cursor1.getString(3);
                String data4 = cursor1.getString(4);
                String data5 = cursor1.getString(5);//checkbox value
                //String data6 = cursor1.getString(6);
                // do what ever you want here
                modelItems2[i] = new Model(data, data1, data2,data3, data4, data5);
                cursor1.moveToNext();

            }
            cursor1.close();

        }
        cursor1.close();
    }

    @Override
    public void onClick(View view) {
        concentration_required_submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(concentration_required.this, concentration_option.class));
            }
        });

    }
}

