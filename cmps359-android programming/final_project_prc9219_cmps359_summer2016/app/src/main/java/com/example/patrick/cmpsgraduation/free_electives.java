package com.example.patrick.cmpsgraduation;

import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.IOException;

public class free_electives extends Activity implements View.OnClickListener {

    DBHelper db;
    Button free_electives_next;
    EditText crs1,credits1, grade1;
    EditText crs2,credits2, grade2;
    EditText crs3,credits3, grade3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_electives);

        free_electives_next = (Button) findViewById(R.id.free_electives_next);
        free_electives_next.setOnClickListener(this);

        //openDatabase();
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

    @Override
    public void onClick(View view) {
        free_electives_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(free_electives.this, math_prereq.class));
            }
        });

    }
}
