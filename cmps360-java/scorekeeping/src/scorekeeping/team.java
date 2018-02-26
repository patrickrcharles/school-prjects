/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scorekeeping;

import java.util.ArrayList;

/**
 *
 * @author Patrick
 */
public class team {
    
    ArrayList<question> questions = new ArrayList<>();
    int teamId, totalQuestionsSolved;
    int rank;
    double totalTimeSolved;
    
    team(){
        
    }
    public void initTeams(int numQ){
        //array of questions to solve
        
         for(int j = 0; j < numQ ;j++){
                questions.add(new team.question());
                questions.get(j).correct = false;
                questions.get(j).questionID = j;
                questions.get(j).questionStart = 0;
                questions.get(j).questionStatus = " in progress";
                questions.get(j).isPaused = false;

                /*System.out.println("team" + this.teamId + " question " +  
                questions.get(j).questionID + " is : "+ 
                questions.get(j).correct + "\n");*/
            }
    }
    
    public Integer getTeamId(){
        return this.teamId;
    }
    
    public Integer getRank(){
        return this.totalQuestionsSolved;
    }
    
    public class question {
    
    int timeSolved; //in seconds
    int questionID;
    boolean correct;
    boolean timerOn;
    boolean isPaused;
    
    String questionStatus;
    int questionStart; //in seconds
    int questionEnd; //in seconds
    int questionElapsedTimeSoFar;
    

    question() {
        timeSolved = 0;
        //questionID = 0;
        correct = false;
        
    }
    
    public void calculateElapsedTime(){
        //math with start and end
    }
    
    public void setQuestionStatus(){
        if(correct == false)
        { 
             if ( isPaused==true)
              questionStatus     = "  submitted ";
              else if( isPaused==false)
                 questionStatus  =  " in progress";
        }
        else
            questionStatus  = "   solved   ";
    }
    
    public void questionMarkedSolved(){
        /*bool correct = true
        calculate time taken to finish, store in timeSolved
        setQuestionStatus();
        */
    }
    
    //get/set functions
    double getTimeSolved(){
        return timeSolved;
    }
    
    int getQuestionID()
    {
        return questionID;
    }
    
    boolean getCorrect(){
        return correct;
    }
    
    void setTimeSolved(int timeSolved){
        this.timeSolved = timeSolved;
    }
    
    void setQuestionID(int questionID)
    {
       this.questionID = questionID;
    }
    
    void setCorrect(boolean correct){
        this.correct = correct;
    }
    }
    
}
