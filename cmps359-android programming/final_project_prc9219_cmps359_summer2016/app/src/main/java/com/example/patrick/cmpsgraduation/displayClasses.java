package com.example.patrick.cmpsgraduation;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.io.IOException;

public class displayClasses extends Activity implements View.OnClickListener {
    ListView lv5;
    Model[] modelItems5;//require
    Button home;

    //DBHelper dbHelper;
    DBHelper db = new DBHelper(this);

    int numRows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_classes);


        home = (Button) findViewById(R.id.display_classes_home);
        home.setOnClickListener(this);

        lv5 = (ListView) findViewById(R.id.listView5); //for concentration required

        openDatabase();
        getclassesNotPassed();

        CustomAdapterForCheckBoxList adapter = new CustomAdapterForCheckBoxList(this, modelItems5);
        lv5.setAdapter(adapter);

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


    public void getclassesNotPassed(){


        String query2 = "select department, course_id, course_title, grade, semester_taken, passed from allclasses where " +
                "passed ='false' order by department asc";

        Cursor cursor2 = db.myDataBase.rawQuery(query2, null);

        numRows = cursor2.getCount();
        modelItems5 = new Model[numRows];

        if (cursor2.moveToFirst()) {
            for (int i = 0; i < numRows; i++) {
                String data = cursor2.getString(0);
                String data1 = cursor2.getString(1);
                String data2 = cursor2.getString(2);
                String data3 = cursor2.getString(3);
                String data4 = cursor2.getString(4);
                String data5 = cursor2.getString(5);
                //String data6 = cursor2.getString(6);
                // do what ever you want here
                modelItems5[i] = new Model(data, data1, data2);
                cursor2.moveToNext();

            }
            cursor2.close();

        }
        cursor2.close();

    }

    @Override
    public void onClick(View view) {
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(displayClasses.this, MainActivity.class));
            }
        });

    }

}
