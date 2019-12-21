package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class DecisionController {

    @FXML
    private Button approve_btn;

    @FXML
    private Button request_more_btn;

    @FXML
    private Button back_btn;

    @FXML
    private Button deny_btn;


    @FXML
    void back_click(ActionEvent event) {
    	ScreenController.getScreenController().activate(ScreenController.getScreenController().getLastScreen());
    }

    @FXML
    void deny_click(ActionEvent event) {

    }

    @FXML
    void approve_click(ActionEvent event) {

    }

    @FXML
    void request_more_click(ActionEvent event) {

    }

}
