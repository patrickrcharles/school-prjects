package com.example.patrick.cmpsgraduation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class enterclasses extends Activity implements View.OnClickListener {

    ListView lv, lv2, lv3, lv4;
    Model[] modelItems1;//require
    Model[] modelItems2;//concentration required
    Model[] modelItems3;//concentration option
    Model[] modelItems4;//math prereqs
    Button required_submit_button;

    //DBHelper dbHelper;
    DBHelper db = new DBHelper(this);

    int numRows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enterclasses);

        required_submit_button = (Button) findViewById(R.id.enterclasses_next);
        required_submit_button.setOnClickListener(this);

        lv = (ListView) findViewById(R.id.listView1); //for  required
        lv2 = (ListView) findViewById(R.id.listView2); //for concentration required
        lv3 = (ListView) findViewById(R.id.listView3); //for concentration options
        lv4 = (ListView) findViewById(R.id.listView4); //for concentration options


        openDatabase();
        getRequiredClasses();
        //getConcentrationRequiredClasses();
        //getConcentrationOptionClasses();
        // getMathPreReqs();

        CustomAdapterForCheckBoxList adapter = new CustomAdapterForCheckBoxList(this, modelItems1);
        lv.setAdapter(adapter);


        db.myDataBase.close();
    }

    @Override
    public void onClick(View view) {
        required_submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(enterclasses.this, concentration_required.class));
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_enter_classes, menu);
        return true;
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

    public void getRequiredClasses() {

        String query = "select department, course_id, course_title, grade, semester_taken, passed from allclasses where " +
                "type='required' order by department asc";

        Cursor cursor = db.myDataBase.rawQuery(query, null);
        numRows = cursor.getCount();
        modelItems1 = new Model[numRows];

        if (cursor.moveToFirst()) {
            for (int i = 0; i < numRows; i++) {
                String data = cursor.getString(0);
                String data1 = cursor.getString(1);
                String data2 = cursor.getString(2);
                String data3 = cursor.getString(3); //checkbox value
                String data4 = cursor.getString(4);
                String data5 = cursor.getString(5);
                //String data6 = cursor.getString(6);

                modelItems1[i] = new Model(data, data1, data2, data3, data4, data5);
                cursor.moveToNext();

            }
            cursor.close();

        }
    }


}





