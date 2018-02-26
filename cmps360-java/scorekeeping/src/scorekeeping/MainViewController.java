/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scorekeeping;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 */
public class MainViewController implements Initializable {

    //private final Stage stage = new Stage();
    static int numTeams, numQuestions, numDuration;
    boolean contestIsEnded;
    String reportFileName = "rankings.txt";

    @FXML
    Pane endContest;

    ArrayList<team> teams = new ArrayList<>();
    ObservableList<String> list = FXCollections.observableArrayList();
    ObservableList<Integer> qID = FXCollections.observableArrayList();
    ObservableList<Double> qTimeSolved = FXCollections.observableArrayList();
    ObservableList<Boolean> qCorrect = FXCollections.observableArrayList();

    ObservableList<String> statelist = FXCollections.observableArrayList();
    ObservableList<String> rankingsList = FXCollections.observableArrayList();
    ObservableList<Button> ButtonsList = FXCollections.observableArrayList();
    ObservableList<Button> timerButtonsList = FXCollections.observableArrayList();
    ObservableList<Label> timersList = FXCollections.observableArrayList();

    boolean timerOn = false;
    boolean timerPaused = true;
    //calculate minutes
    int timerHours = numDuration / 60;
    //calculate seconds. subtract 1 because 60 secs = 1 minute
    int timerMinutes = ((numDuration - 1) - (timerHours * 60));
    int timerSeconds = 60;

    boolean subTimerOn = false;
    boolean subTimerPaused = true;
    int subTimerHours = 0;
    int subTimerSeconds = 0;
    int subTimerMinutes = 0;

    javafx.animation.Timeline timer = null;
    javafx.animation.Timeline timer1 = null;
    static EventHandler<ActionEvent> eventHandler = null;

    @FXML
    private Button viewTeam;
    @FXML
    private Button startMainTimer;
    @FXML
    private Label timerLabel;
    @FXML
    private ComboBox<String> teamsDropdown;
    @FXML
    private Button viewRanking;
    @FXML
    private ListView<Integer> questionID;
    @FXML
    private ListView<Double> questionTimeSolved;
    @FXML
    private ListView<Boolean> questionCorrect;
    @FXML
    private Pane questionView;
    @FXML
    private ListView<String> questionState;
    @FXML
    private Pane questionStateView;
    @FXML
    private AnchorPane rankingsListView;
    @FXML
    private ListView<String> viewRankings;
    @FXML
    private Label rankingsHeaderLabel;
    @FXML
    private Pane rankingsPane;
    @FXML
    private ListView<Button> questionButtonsList;
    @FXML
    private Label questionStateLabel;
    @FXML
    private ListView<Button> questionTimerButtonsList;
    @FXML
    private Label subTimer;
    @FXML
    private ListView<Label> timerList;
    @FXML
    private Button endContestButton;
    @FXML
    private Pane mainTimer;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // disable view of question states
        questionStateView.setVisible(false);

        mainTimer.setVisible(true);
        endContest.setVisible(false);

        //questionStateView.setVisible(false);
        rankingsPane.setVisible(false);

        contestIsEnded = false;
        //intitialize array of teams and questions

        initTeams();
        //initialize String array for dropdownlist
        for (int i = 0; i < numTeams; i++) {
            list.add("Team" + i);

        }
        //populate dropdown list
        teamsDropdown.setItems(list);

        eventHandler = e -> {
            //countdown timer
            if (timerHours == 0 && timerMinutes == 0 && timerSeconds == 0) {
                timer.pause();
                timerOn = false;
                contestIsEnded = true;
                mainTimer.setVisible(false);
                endContest.setVisible(true);
                subTimerPaused = true;

            } else if (timerHours > 0 && timerMinutes == 0 && timerSeconds == 0) {
                timerMinutes = 59;
                timerHours--;
                timerSeconds = 59;
            } else if (timerSeconds == 0) {
                timerMinutes--;
                timerSeconds = 59;
            } else {
                timerSeconds--;
            }

            timerLabel.setText(String.format("%02d:%02d:%02d", timerHours, timerMinutes, timerSeconds));

            //second timer
            if (subTimerSeconds == 60) {
                subTimerMinutes++;
                subTimerSeconds = 0;
            }
            subTimerSeconds++;

            subTimer.setText(String.format("%02d:%02d:%02d", subTimerHours, subTimerMinutes, subTimerSeconds));

            for (int j = 0; j < numQuestions; j++) {
                setTimerList(j, numQuestions);
            }
        };
        timer = new Timeline(
                new KeyFrame(Duration.millis(1000), eventHandler));
        timer.setCycleCount(180);
        timer.play();
        timerPaused = false;
        subTimerPaused = false;
    }

    @FXML
    private void handleEndContest(ActionEvent event) throws IOException {
       
        outputResultsToFile(true);
    }

    @FXML
    private void handleView(ActionEvent event) throws IOException {

        //if rankings visible set to false 
        rankingsPane.setVisible(false);

        statelist.clear();
        ButtonsList.clear();
        timerButtonsList.clear();
        //header
        questionStateLabel.setText("  question #  "
                + "    time solved "
                + "       status  \n");
        //put that huge for loop in a function
        initQuestionStates();
        //set visible
        questionStateView.setVisible(true);
        //3 listview populated
        questionState.setItems(statelist);
        questionButtonsList.setItems(ButtonsList);
        questionTimerButtonsList.setItems(timerButtonsList);
        timerList.setItems(timersList);

        //bind scrollbars together
        Node n1 = questionState.lookup(".scroll-bar");
        if (n1 instanceof ScrollBar) {

            final ScrollBar bar1 = (ScrollBar) n1;

            Node n2 = questionButtonsList.lookup(".scroll-bar");
            Node n3 = questionTimerButtonsList.lookup(".scroll-bar");
            Node n4 = timerList.lookup(".scroll-bar");
            if (n2 instanceof ScrollBar) {

                final ScrollBar bar2 = (ScrollBar) n2;
                bar1.valueProperty().bindBidirectional(bar2.valueProperty());

                final ScrollBar bar3 = (ScrollBar) n3;
                bar1.valueProperty().bindBidirectional(bar3.valueProperty());

                final ScrollBar bar4 = (ScrollBar) n4;
                bar1.valueProperty().bindBidirectional(bar4.valueProperty());
            }
        }
    }

    //calls all that bullshit 
    public void initQuestionStates() {
        int selectedIndex = teamsDropdown.getSelectionModel().getSelectedIndex();

        for (int i = 0; i < numQuestions; i++) {

            int tempInt = teams.get(selectedIndex).questions.get(i).questionElapsedTimeSoFar;
            String tempString = convertSecondsToTimeSTamp(tempInt);

            //correct/incorrect buttons init
            setButtonCorrectList(i, selectedIndex);
            //start/pause timer buttons init
            setButtonTimerList(i, selectedIndex);

            //setTimerList(i, selectedIndex);
            teams.get(selectedIndex).questions.get(i).setQuestionStatus();
            String questionState1;
            // if num questions <10
            if (teams.get(selectedIndex).questions.get(i).questionID < 10) {
                questionState1 = ("        "
                        + String.valueOf(teams.get(selectedIndex).questions.get(i).questionID)
                        + "                 "
                        + tempString
                        + "             "
                        + teams.get(selectedIndex).questions.get(i).questionStatus);
            } //num q's >= 10
            else {
                questionState1 = ("       "
                        + String.valueOf(teams.get(selectedIndex).questions.get(i).questionID)
                        + "                "
                        + tempString
                        + "             "
                        + teams.get(selectedIndex).questions.get(i).questionStatus);
            }
            //add to @fxml listview array
            statelist.add(questionState1);
        }
    }

    @FXML
    private void handleViewRank(ActionEvent event) {

        //disable rankings view pane if visible
        questionStateView.setVisible(false);

        //header fields label
        rankingsHeaderLabel.setText("  rank  " + "  team #  " + "  # correct  \n");
        //copy list of teams rankings, sort,
        //SORT LIST first, then copy
        //clear previous lists if any
        rankingsList.clear();
        //sort list by rank
        Collections.sort(teams, new Comparator<team>() {
            @Override
            public int compare(team o1, team o2) {
                return o2.getRank().compareTo(o1.getRank());
            }
        });
       

        for (int i = 0; i < numTeams; i++) {
            //list is already sorted on order of # questionsSolved.
            //just need to set rank to the index

            //NOTE: eventually add a check if questionSolved==questionsSolved
            // check totalTimeQuestionSolved
            teams.get(i).rank = i + 1;
            String rankings;
            //way complicated thing to make columns line up right
            if (teams.get(i).rank < 10) {
                rankings = ("    "
                        + String.valueOf(teams.get(i).rank)
                        + "          "
                        + String.valueOf(teams.get(i).teamId)
                        + "              "
                        + String.valueOf(teams.get(i).totalQuestionsSolved));
                if (teams.get(i).teamId < 10) {
                    rankings = ("    "
                            + String.valueOf(teams.get(i).rank)
                            + "            "
                            + String.valueOf(teams.get(i).teamId)
                            + "             "
                            + String.valueOf(teams.get(i).totalQuestionsSolved));
                } else if (teams.get(i).totalQuestionsSolved < 10) {
                    rankings = ("     "
                            + String.valueOf(teams.get(i).rank)
                            + "          "
                            + String.valueOf(teams.get(i).teamId)
                            + "               "
                            + String.valueOf(teams.get(i).totalQuestionsSolved));
                }
            } else {
                rankings = ("   "
                        + String.valueOf(teams.get(i).rank)
                        + "         "
                        + String.valueOf(teams.get(i).teamId)
                        + "            "
                        + String.valueOf(teams.get(i).totalQuestionsSolved));
            }
            //put string into array
            rankingsList.add(rankings);

        }//end of for loop

        //resort list by teamId to return list to original state
        Collections.sort(teams, new Comparator<team>() {
            @Override
            public int compare(team o1, team o2) {
                return o1.getTeamId().compareTo(o2.getTeamId());
            }
        });

        viewRankings.setItems(rankingsList);
        //make it visible
        rankingsPane.setVisible(true);

    }

    @FXML
    private void handleTimer() {

        if (!timerOn) {

            timerOn = true;
            subTimerOn = true;

            timer.play();
            timerPaused = false;
            subTimerPaused = false;

            startMainTimer.setText("Pause Timer!");

        } else if (timerPaused) {

            timer.play();
            timerPaused = false;
            subTimerPaused = false;

            startMainTimer.setText("Pause Timer!");

        } else if (!timerPaused) {

            timer.pause();
            timerPaused = true;
            subTimerPaused = true;
        }
    }

    public void initTeams() {

        for (int i = 0; i < numTeams; i++) {
            teams.add(new team());
            teams.get(i).teamId = i;
            teams.get(i).rank = 0; // because rankings would start at zero otheriwse
            teams.get(i).totalQuestionsSolved = 0;
            teams.get(i).totalTimeSolved = 0;
            //initialize question array for each team
            teams.get(i).initTeams(numQuestions);
        }

    }

    public int getCurrentTime() {

        int total;
        total = (subTimerHours * 3600) + (subTimerMinutes * 60) + (subTimerSeconds * 60);

        return total / 60;
    }

    public int calculateTimeElapsed(int teamIndex, int questionIndex) {

        int x = teams.get(teamIndex).questions.get(questionIndex).questionEnd;
        int y = teams.get(teamIndex).questions.get(questionIndex).questionStart;

        return x - y;
    }

    public void setButtonCorrectList(int i, int selectedIndex) {
 
        final String qC = "  mark correct  ";
        final String qI = " mark incorrect ";
        final String submit = "   submit problem  ";
        final String restart = "   restart problem ";

        //get index passed in, convert to string
        final String id = String.valueOf(i);
        //if question already correct, onclick change text to 'mark incorrect

        ButtonsList.add(new Button());
        ButtonsList.get(i).setId(id);

        if (teams.get(selectedIndex).questions.get(i).correct == true) {
            ButtonsList.get(i).setText(qI);
        } else {
            ButtonsList.get(i).setText(qC);
        }
        ButtonsList.get(i).setPadding(Insets.EMPTY);

        //set eventhandler for each buttion
        ButtonsList.get(i).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event1) {

                int currentButtonID = Integer.parseInt(ButtonsList.get(i).getId());
                boolean correct = teams.get(selectedIndex).questions.get(currentButtonID).correct;
                boolean paused = teams.get(selectedIndex).questions.get(currentButtonID).isPaused;

                //'mark incorrect' button pressed
                if (correct == true) {
                    // question was correct, marked incorrect. starts running again
                    teams.get(selectedIndex).questions.get(currentButtonID).correct = false;
                    teams.get(selectedIndex).questions.get(currentButtonID).isPaused = false;

                    teams.get(selectedIndex).questions.get(currentButtonID).setQuestionStatus();
                    teams.get(selectedIndex).totalQuestionsSolved--;

                    //start questionStart again
                    teams.get(selectedIndex).questions.get(currentButtonID).questionStart = getCurrentTime();
                   
                } else //question is not correct
                // question is not correct, but is paused
                {
                    if (correct == false && paused == true) {
                        teams.get(selectedIndex).questions.get(currentButtonID).isPaused = true;
                        teams.get(selectedIndex).questions.get(currentButtonID).correct = true;
                        teams.get(selectedIndex).questions.get(currentButtonID).isPaused = true;
                        teams.get(selectedIndex).questions.get(currentButtonID).correct = true;
                        teams.get(selectedIndex).questions.get(currentButtonID).setQuestionStatus();

                        teams.get(selectedIndex).totalQuestionsSolved++;
                        //add up current time elapsed

                        int temp = calculateTimeElapsed(selectedIndex, currentButtonID);
                        teams.get(selectedIndex).questions.get(currentButtonID).questionElapsedTimeSoFar += temp;

                    } else //question is not correct
                    {
                        teams.get(selectedIndex).questions.get(currentButtonID).isPaused = true;

                        teams.get(selectedIndex).questions.get(currentButtonID).correct = true;
                        teams.get(selectedIndex).questions.get(currentButtonID).setQuestionStatus();

                        teams.get(selectedIndex).totalQuestionsSolved++;
                        //set QuestionEnd to current to calculate total
                        teams.get(selectedIndex).questions.get(currentButtonID).questionEnd = getCurrentTime();
                        //add up current time elapsed

                        int temp = calculateTimeElapsed(selectedIndex, currentButtonID);
                        teams.get(selectedIndex).questions.get(currentButtonID).questionElapsedTimeSoFar += temp;
                    }
                }
                //clear arrays and reinitialize
                statelist.clear();
                ButtonsList.clear();
                timerButtonsList.clear();
                initQuestionStates();
            }
        });
    }

    public void setButtonTimerList(int i, int selectedIndex) {
        //set eventhandler for each timer button
        String submit = "   submit problem  ";
        String restart = "   restart problem ";
        final String qC = "  mark correct  ";
        final String qI = " mark incorrect ";
        final String id = String.valueOf(i);

        int currentButtonID = Integer.parseInt(ButtonsList.get(i).getId());
        boolean isPaused = teams.get(selectedIndex).questions.get(currentButtonID).isPaused;
        boolean correct = teams.get(selectedIndex).questions.get(currentButtonID).correct;

        timerButtonsList.add(new Button());
        //timerButtonsList.get(i).setText(submit);
        timerButtonsList.get(i).setPadding(Insets.EMPTY);
        timerButtonsList.get(i).setId(id);

        if (isPaused == true) {
            timerButtonsList.get(i).setText(restart);
        } else {
            timerButtonsList.get(i).setText(submit);
        }

        //EVENT HANDLER~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        timerButtonsList.get(i).setOnAction((ActionEvent event1) -> {

            //index for question to start stop timer
            int selectedTimer = questionTimerButtonsList.getSelectionModel().getSelectedIndex();
            //if paused, set paused to false, calculate time elapsed so far. set timestart to getTime()
            if (isPaused == true) {

                teams.get(selectedIndex).questions.get(currentButtonID).isPaused = false;
                teams.get(selectedIndex).questions.get(currentButtonID).correct = false;

                int temp = calculateTimeElapsed(selectedIndex, currentButtonID);
                //total elapsed time was paused, will start running again
                // update total? prob not since updated on pause
                //teams.get(selectedIndex).questions.get(currentButtonID).questionElapsedTimeSoFar +=temp;

                //restart counter. reset questionStart time to current
                teams.get(selectedIndex).questions.get(currentButtonID).questionStart = getCurrentTime();
            } else { //if isPaused = false 

                teams.get(selectedIndex).questions.get(currentButtonID).isPaused = true;
                teams.get(selectedIndex).questions.get(currentButtonID).correct = false;

                //question submitted but not correct yet. set questionEnd time.
                teams.get(selectedIndex).questions.get(currentButtonID).questionEnd = getCurrentTime();
                //calc total time so far
                int temp = calculateTimeElapsed(selectedIndex, currentButtonID);
                teams.get(selectedIndex).questions.get(currentButtonID).questionElapsedTimeSoFar += temp;

            }

            //clear lists 
            statelist.clear();
            ButtonsList.clear();
            timerButtonsList.clear();
            //re-initialize lists
            initQuestionStates();
        });
        //EVENT HANDLER~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    }

    public void setTimerList(int nTeams, int nQues) {

        String id = String.valueOf(nQues);
        timersList.add(new Label());
        timerButtonsList.get(nTeams).setId(id);
        /*
        if(teams.get(nTeams).questions.get(nQues).correct==true)
        {
            int tempInt = teams.get(nTeams).questions.get(nQues).questionElapsedTimeSoFar;
            String tempString = convertSecondsToTimeSTamp(tempInt);
            timersList.get(nTeams).setText(tempString);
        }
         */
        //else{
        timersList.get(nTeams).setText(String.format("%02d:%02d:%02d", subTimerHours, subTimerMinutes, subTimerSeconds));
    }

    public String convertSecondsToTimeSTamp(int seconds) {

        int h = 0, m = 0, s = 0;
        int total = seconds;
        String temp;
        if (total > 3600) {
            h++;
            total -= 3600;
        }
        if (total > 60) {
            m++;
            total -= 60;
        }
        s = total;
        temp = String.format("%02d:%02d:%02d", h, m, s);
        return temp;
    }

    public void outputResultsToFile(boolean endContest) throws FileNotFoundException {

        // if timer is 00:00:00
        if (endContest == true) {

            try (PrintWriter out = new PrintWriter(reportFileName)) {
                String rankings;
                out.println("  rank  " + "  team #  " + "  # correct  \n\n");

                for (int i = 0; i < numTeams; i++) {
                    rankings = ("    "
                            + String.valueOf(teams.get(i).rank)
                            + "          "
                            + String.valueOf(teams.get(i).teamId)
                            + "              "
                            + String.valueOf(teams.get(i).totalQuestionsSolved));
                    out.println(rankings + "\n");
                }
            }
            System.out.print("output file " + reportFileName + " created\n");
        }
    }
}