package com.example.patrick.cmpsgraduation;

/**
 * Created by Patrick on 7/22/2016.
 */

class Model
{
    String courseID, department;
    String name; //course name
    String grade;
    String semesterTaken;
    String passed;
    String concentration;


    String value; /* 0 -&gt; checkbox disable, 1 -&gt; checkbox enable */


    Model( String department, String courseID, String name, String grade, String semesterTaken, String passed){
        this.department = department;
        this.courseID = courseID;
        this.name = name;
        //this.value = value;
        this.grade = grade;
        this.semesterTaken = semesterTaken;
        this.passed = passed;
    }

    Model( String department, String courseID, String name){
        this.department = department;
        this.courseID = courseID;
        this.name = name;
        //this.value = value;
        this.grade = grade;
        this.semesterTaken = semesterTaken;
        this.passed = passed;
    }

    public String getcrsID(){
        return this.courseID;
    }
    public String getName(){
        return this.name;
    }
    public String getValue(){
        return this.value;
    }
    public String getDepartment(){return this.department;}
    public String getGrade(){
        return this.grade;
    }
    public String getSemesterTaken(){
        return this.semesterTaken;
    }
    public String getPassed(){
        return this.passed;
    }
    public String getConcentration(){return this.concentration; }



}