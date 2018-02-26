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

public class concentration_option extends Activity implements View.OnClickListener {
    ListView lv3;
    Model[] modelItems3;//require
    Button concentration_option_submit_button;

    //DBHelper dbHelper;
    DBHelper db = new DBHelper(this);

    int numRows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concentration_option);

        concentration_option_submit_button = (Button) findViewById(R.id.concentration_options_next);
        concentration_option_submit_button.setOnClickListener(this);

        lv3 = (ListView) findViewById(R.id.listView3); //for concentration required

        openDatabase();
        getConcentrationOptionClasses();

        CustomAdapterForCheckBoxList adapter2 = new CustomAdapterForCheckBoxList(this, modelItems3);
        lv3.setAdapter(adapter2);

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


    public void getConcentrationOptionClasses(){
        String query1 = "select department, course_id, course_title, grade, semester_taken, passed from allclasses where " +
                "type='concentration option' order by department asc";
        Cursor cursor2 = db.myDataBase.rawQuery(query1, null);
        numRows = cursor2.getCount();
        modelItems3 = new Model[numRows];

        if (cursor2.moveToFirst()) {
            for (int i = 0; i < numRows; i++) {
                String data = cursor2.getString(0);
                String data1 = cursor2.getString(1);
                String data2 = cursor2.getString(2);
                String data3 = cursor2.getString(3); //checkbox value
                String data4 = cursor2.getString(4);
                String data5 = cursor2.getString(5);
                //String data6 = cursor2.getString(6);
                // do what ever you want here
                modelItems3[i] = new Model(data, data1, data2,data3, data4, data5);
                cursor2.moveToNext();

            }
            cursor2.close();

        }
        cursor2.close();

    }

    @Override
    public void onClick(View view) {
        concentration_option_submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(concentration_option.this, free_electives.class));
            }
        });

    }
}
