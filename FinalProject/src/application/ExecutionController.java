package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ExecutionController {

    @FXML
    private Button back_btn;

    @FXML
    private TextField days_textbox;

    @FXML
    private Button execution_completed_btn;

    @FXML
    private Button submit_btn;

    @FXML
    void back_click(ActionEvent event) {
    	ScreenController.getScreenController().activate(ScreenController.getScreenController().getLastScreen());
    }

    @FXML
    void submit_click(ActionEvent event) {

    }

    @FXML
    void execution_completed_click(ActionEvent event) {

    }



}
