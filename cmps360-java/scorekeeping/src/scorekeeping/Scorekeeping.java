// Your Name(s) Patrick Charles, Kevin Pearson
// Your CLID(s) prc9219, kmp6372
// CMPS 360
// Programming Project : #2
// Due Date : 10/8
// Program Description: scorekeeping program for quiz bowl
// Certificate of Authenticity:   (Choose one of the two following forms:)
/*
I (we) certify that the code in the method functions including method function main of this 
project are entirely my own work. 

 */
package scorekeeping;

import java.util.ArrayList;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Patrick
 */
public class Scorekeeping extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("showUI.fxml"));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        launch(args);

    }

}
