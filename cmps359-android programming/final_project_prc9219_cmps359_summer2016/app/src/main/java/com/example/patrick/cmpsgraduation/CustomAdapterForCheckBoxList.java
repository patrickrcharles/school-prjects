package com.example.patrick.cmpsgraduation;

/**
 * Created by Patrick on 7/22/2016.
 */

 import android.app.Activity;
 import android.content.Context;
 import android.view.LayoutInflater;
 import android.view.View;
 import android.view.ViewGroup;
 import android.widget.AdapterView;
 import android.widget.ArrayAdapter;
 import android.widget.CheckBox;
 import android.widget.EditText;
 import android.widget.Spinner;
 import android.widget.TextView;

 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.List;

public class CustomAdapterForCheckBoxList extends ArrayAdapter<Model> implements View.OnClickListener {



    Model[] modelItems = null;
    Context context;
    dataStorage data;
    ArrayList<String> grades = new ArrayList<String>(Arrays.asList("A","B","C","D","F"));
    ArrayList<String> semesters = new ArrayList<String>(Arrays.asList(
            "fall 2016", "summer 2016","spring 2016",
                    "fall 2015", "summer 2015", "spring 2015",
                    "fall 2014", "summer 2014", "spring 2014",
                    "fall 2013", "summer 2013", "spring 2013",
                    "fall 2012", "summer 2012", "spring 2012",
                    "fall 2011", "summer 2011", "spring 2011",
                    "fall 2010", "summer 2010", "spring 2010",
                    "fall 2009", "summer 2009", "spring 2009",
                    "fall 2008", "summer 2008", "spring 2008",
                    "fall 2007", "summer 2007", "spring 2007",
                    "fall 2006", "summer 2006", "spring 2006",
                    "fall 2005", "summer 2005", "spring 2005",
                    "fall 2004", "summer 2004", "spring 2004",
                    "fall 2003", "summer 2003", "spring 2003",
                    "fall 2002", "summer 2002", "spring 2002",
                    "fall 2001", "summer 2001", "spring 2001",
                    "fall 2000", "summer 2000", "spring 2000"));

    public CustomAdapterForCheckBoxList(Context context, Model[] resource) {
        super(context, R.layout.row, resource);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.modelItems = resource;
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();

        convertView = inflater.inflate(R.layout.row, parent, false);

        TextView name = (TextView) convertView.findViewById(R.id.textView1);
        final CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkBox1);

        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean isChecked = cb.isChecked();
                // Do something here.
            }
        });


        final Spinner grade = (Spinner) convertView.findViewById(R.id.row_grade);
        final Spinner semester = (Spinner) convertView.findViewById(R.id.row_semester);


        //populate grade spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item, grades);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        grade.setAdapter(dataAdapter);

        //populate semester taken spinner
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item, semesters);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        semester.setAdapter(dataAdapter2);

        String crsID_course_Title = modelItems[position].getDepartment() + "  " +
                modelItems[position].getcrsID() + " : " +
                modelItems[position].getName();

        name.setText(crsID_course_Title);


        if (modelItems[position].getPassed() == "true")
            cb.setChecked(true);
        else
            cb.setChecked(false);
        return convertView;
    }



    @Override
    public void onClick(View view) {

    }
}
