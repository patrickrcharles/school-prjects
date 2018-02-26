/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scorekeeping;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Patrick
 */
public class ShowUIController implements Initializable {

    EventHandler<ActionEvent> eventHandler = null;
    //userInputData Data = new userInputData();   
    int t, q, d;

    @FXML
    private Button startSubmit;
    @FXML
    private Button startClear;
    @FXML
    private TextField uiNumDuration;
    @FXML
    private TextField uiNumQuestions;
    @FXML
    private TextField uiNumTeams;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        System.out.println("initialized ############\n");
    }

    @FXML
    private void submitButtonAction(ActionEvent event) throws IOException {

        //get user input from fields, parse to int 
        t = stringToInt(uiNumTeams.getText());
        q = stringToInt(uiNumQuestions.getText());
        d = stringToInt(uiNumDuration.getText());

        MainViewController.numTeams = t;
        MainViewController.numQuestions = q;
        MainViewController.numDuration = d;

        Stage stage;
        Parent root;

        stage = (Stage) startSubmit.getScene().getWindow();
        //load up OTHER FXML document
        root = FXMLLoader.load(getClass().getResource("MainView.fxml"));
        //create a new scene with root and set the stage
        Scene scene = new Scene(root);

        //check max value
        if (t < 51 && q < 181 && d < 181) {
            stage.setScene(scene);
            stage.show();
        } else {
            //error strings       
            String errorT = " max # of teams is 50\n";
            String errorQ = " max # of questions is 180\n";
            String errorD = " max duration of contest is 180 minutes\n";
            String errorMessage = "";
            //error conditions
            if (t > 50) {
                errorMessage = errorT;
            }
            if (q > 180) {
                errorMessage += errorQ;
            }
            if (d > 180) {
                errorMessage += errorD;
            }
            //create popup dialog with 'ok' button
            Alert alert = new Alert(AlertType.ERROR, errorMessage, ButtonType.OK);
            alert.showAndWait();
        }
    }

    @FXML
    private void clearButtonAction(ActionEvent event) throws IOException {

        Stage stage;
        Parent root;
        stage = (Stage) startClear.getScene().getWindow();

        //load up OTHER FXML document
        root = FXMLLoader.load(getClass().getResource("showUI.fxml"));

        //create a new scene with root and set the stage
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    public static int stringToInt(String s) {
        try {
            return Integer.valueOf(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

}
